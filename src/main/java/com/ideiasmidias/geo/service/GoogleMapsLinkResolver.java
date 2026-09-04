package com.ideiasmidias.geo.service;

import com.ideiasmidias.geo.dto.ResolveMapsLinkResponse;

public interface GoogleMapsLinkResolver {

    /**
     * Turns a Google Maps link (full or shortened) into coordinates. A long
     * link's coordinates are read straight out of the URL; a short link
     * (maps.app.goo.gl, goo.gl, g.co) is followed to wherever it redirects
     * and read from there instead.
     */
    ResolveMapsLinkResponse resolve(String rawUrl);
}
