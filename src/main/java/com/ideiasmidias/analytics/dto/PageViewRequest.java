package com.ideiasmidias.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A visit reported by the browser itself.
 *
 * <p>{@code visitorId} is a random identifier the browser generates once and
 * keeps in local storage. It carries no personal data — it exists only so the
 * same browser is recognised as the same visitor instead of being counted
 * again on every page load.
 */
@Getter
@Setter
@NoArgsConstructor
public class PageViewRequest {

    @NotBlank
    @Size(max = 255)
    private String path;

    @Size(max = 180)
    private String sectionSlug;

    @Size(max = 64)
    private String visitorId;
}
