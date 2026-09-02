package com.ideiasmidias.media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Configuration
public class MediaResourceConfig implements WebMvcConfigurer {

    @Value("${app.media.upload-dir:uploads/media}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String resourceLocation = uploadPath.toUri().toString();

        // Uploaded files are content-addressed by their generated name: a new
        // upload gets a new URL, an existing URL never changes. So they can be
        // cached hard, which keeps images and video off the origin entirely on
        // repeat views.
        registry.addResourceHandler("/uploads/media/**")
                .addResourceLocations(resourceLocation.endsWith("/") ? resourceLocation : resourceLocation + "/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());
    }
}