package com.github.proximityhash;

import lombok.Getter;

import java.util.Set;

/**
 * Results from {@link com.github.proximityhash.ProximityHash#findGeohashesWithinRadius(GeoPoint, double, int)}.
 */
@Getter
public class ProximityHashResult {

    /**
     * All geohashes of the given precision which lie entirely within the radius.
     */
    private Set<String> hashesWithinRadius;

    /**
     * All geohashes of the given precision which lie partially but not entirely within the radius.
     */
    private Set<String> hashesPartiallyWithinRadius;

    public ProximityHashResult(Set<String> hashesWithinRadius, Set<String> hashesPartiallyWithinRadius) {
        this.hashesWithinRadius = hashesWithinRadius;
        this.hashesPartiallyWithinRadius = hashesPartiallyWithinRadius;
    }

}
