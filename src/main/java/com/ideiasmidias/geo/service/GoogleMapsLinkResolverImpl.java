package com.ideiasmidias.geo.service;

import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.geo.dto.ResolveMapsLinkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A pasted Google Maps link is the easiest way for a non-technical owner to
 * hand over their business location: sharing a pin from the Maps app is
 * something people already know how to do, unlike clicking a precise spot
 * on an unfamiliar map widget.
 *
 * <p>The host allowlist below is a deliberate SSRF guard: this is the only
 * place in the backend that fetches a URL supplied by an admin, so the
 * request is only ever allowed to reach Google's own domains, never an
 * arbitrary or internal address.
 */
@Slf4j
@Service
public class GoogleMapsLinkResolverImpl implements GoogleMapsLinkResolver {

    private static final Set<String> ALLOWED_HOSTS = Set.of(
            "maps.app.goo.gl", "goo.gl", "g.co",
            "maps.google.com", "www.google.com", "google.com"
    );

    private static final List<Pattern> COORDINATE_PATTERNS = List.of(
            Pattern.compile("!3d(-?\\d+\\.\\d+)!4d(-?\\d+\\.\\d+)"),
            Pattern.compile("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)"),
            Pattern.compile("[?&]q=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)"),
            Pattern.compile("[?&]ll=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    );

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public ResolveMapsLinkResponse resolve(String rawUrl) {
        URI uri = parseAndValidate(rawUrl);

        Optional<double[]> direct = extractCoordinates(rawUrl);
        if (direct.isPresent()) {
            return toResponse(direct.get());
        }

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("User-Agent", "Mozilla/5.0 (compatible; IdeiasMidiasBot/1.0)")
                .timeout(Duration.ofSeconds(8))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.warn("Failed to resolve Google Maps link. url={}, error={}", rawUrl, e.getMessage());
            throw new BadRequestException("Could not open that link. Please try again or use the search box instead.");
        }

        Optional<double[]> fromFinalUrl = extractCoordinates(response.uri().toString());
        if (fromFinalUrl.isPresent()) {
            return toResponse(fromFinalUrl.get());
        }

        Optional<double[]> fromBody = extractCoordinates(response.body());
        if (fromBody.isPresent()) {
            return toResponse(fromBody.get());
        }

        throw new BadRequestException(
                "Could not find a location in that link. Make sure it's a Google Maps link, or use the search box instead."
        );
    }

    private URI parseAndValidate(String rawUrl) {
        URI uri;
        try {
            uri = new URI(rawUrl.trim());
        } catch (URISyntaxException e) {
            throw new BadRequestException("That doesn't look like a valid link");
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new BadRequestException("The link must start with https://");
        }

        String host = uri.getHost() != null ? uri.getHost().toLowerCase() : "";
        if (!ALLOWED_HOSTS.contains(host)) {
            throw new BadRequestException("Only Google Maps links are supported");
        }

        return uri;
    }

    private Optional<double[]> extractCoordinates(String text) {
        if (text == null) {
            return Optional.empty();
        }
        for (Pattern pattern : COORDINATE_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                double lat = Double.parseDouble(matcher.group(1));
                double lng = Double.parseDouble(matcher.group(2));
                if (isValidLatLng(lat, lng)) {
                    return Optional.of(new double[]{lat, lng});
                }
            }
        }
        return Optional.empty();
    }

    private boolean isValidLatLng(double lat, double lng) {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }

    private ResolveMapsLinkResponse toResponse(double[] latLng) {
        return new ResolveMapsLinkResponse(latLng[0], latLng[1]);
    }
}
