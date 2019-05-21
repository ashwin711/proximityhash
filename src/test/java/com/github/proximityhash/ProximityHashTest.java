package com.github.proximityhash;

import org.apache.lucene.geo.Rectangle;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.GeoUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link com.github.proximityhash.ProximityHash}
 */
public class ProximityHashTest {

    @Test
    void findGeohashesWithinRadius_radiusDoesNotIntersectSidesOfAnyGeohash_shouldReturnOnlyHashOfInputPoint(){
        int precision = 12;
        String buckinghamFountainGeohash = "dp3wnpmb4ekt";
        GeoPoint centerPoint = getCenterOfGeohashRectangle(buckinghamFountainGeohash, precision);
        double radius = 0.000000000001;

        ProximityHashResult result = ProximityHash.findGeohashesWithinRadius(centerPoint, radius, precision);

        assertTrue(result.getHashesWithinRadius().isEmpty());
        assertEquals(1, result.getHashesPartiallyWithinRadius().size());
        assertTrue(result.getHashesPartiallyWithinRadius().contains(buckinghamFountainGeohash));
    }

    @Test
    void findGeoHashesWithinRadius_radiusIsSmallAndCenterPointLiesOnBoundaryOfTwoGeohashes_shouldReturnTwoBoundaryHashes(){
        int precision = 9;
        String bascomHillGeohash = "dp8mj9e71";
        Rectangle rect = GeoHashUtils.bbox(bascomHillGeohash);
        GeoPoint boundaryPoint = new GeoPoint(rect.maxLat - 0.00003, rect.minLon);
        double radius = 0.1;

        ProximityHashResult result = ProximityHash.findGeohashesWithinRadius(boundaryPoint, radius, precision);

        assertTrue(result.getHashesWithinRadius().isEmpty());
        assertEquals(2, result.getHashesPartiallyWithinRadius().size());
        assertTrue(result.getHashesPartiallyWithinRadius().contains(bascomHillGeohash));
        assertTrue(result.getHashesPartiallyWithinRadius().contains("dp8mj9e70")); // neighboring geohash
    }

    @Test
    void findGeoHashesWithinRadius_radiusIsSmallAndCenterPointLiesOnBoundaryOfFourGeohashes_shouldReturnFourBoundaryHashes(){
        int precision = 6;
        String naraParkGeohash = "xn0t74";
        Rectangle rect = GeoHashUtils.bbox(naraParkGeohash);
        GeoPoint boundaryPoint = new GeoPoint(rect.minLat, rect.minLon);
        double radius = 1;

        ProximityHashResult result = ProximityHash.findGeohashesWithinRadius(boundaryPoint, radius, precision);

        assertTrue(result.getHashesWithinRadius().isEmpty());
        String[] expectedHashes = {"xn0t71", "xn0t6c", "xn0t6f", naraParkGeohash};
        assertEquals(expectedHashes.length, result.getHashesPartiallyWithinRadius().size());
        assertSetContainsGeohashes(expectedHashes, result.getHashesPartiallyWithinRadius());
    }

    @Test
    void findGeoHashesWithinRadius_radiusDoesntFullyIncludeParentHashButTouchesAllNeighbors_centerAndNSEWneighborsArePartial(){
        int precision = 5;
        String sapporoGeohash = "xpssb";
        GeoPoint centerPoint = getCenterOfGeohashRectangle(sapporoGeohash, precision);
        double radius = (GeoUtils.geoHashCellWidth(precision) / 2) + 1;

        ProximityHashResult result = ProximityHash.findGeohashesWithinRadius(centerPoint, radius, precision);

        assertTrue(result.getHashesWithinRadius().isEmpty());
        String[] expectedHashes = {"xpst0", "xpssc", "xpss8", "xpskz", sapporoGeohash};
        assertEquals(expectedHashes.length, result.getHashesPartiallyWithinRadius().size());
        assertSetContainsGeohashes(expectedHashes, result.getHashesPartiallyWithinRadius());
    }

    @Test
    void findGeoHashesWithinRadius_radiusBarelyLargeEnoughToFullMatchCenterHash_centerFull8neighborsPartial(){
        int precision = 7;
        String shibuyaCrossingGeohash = "xn76fgr";
        GeoPoint centerPoint = getCenterOfGeohashRectangle(shibuyaCrossingGeohash, precision);
        double distToSide = (GeoUtils.geoHashCellWidth(precision) / 2);
        double radius = Math.sqrt(2 * Math.pow(distToSide, 2));

        ProximityHashResult result = ProximityHash.findGeohashesWithinRadius(centerPoint, radius, precision);

        assertEquals(1, result.getHashesWithinRadius().size());
        assertTrue(result.getHashesWithinRadius().contains(shibuyaCrossingGeohash));
        String[] expectedHashes = {"xn76g50", "xn76fgp", "xn76fgn", "xn76fgq", "xn76fgw", "xn76fgx", "xn76g58",
                "xn76g52"};
        assertEquals(expectedHashes.length, result.getHashesPartiallyWithinRadius().size());
        assertSetContainsGeohashes(expectedHashes, result.getHashesPartiallyWithinRadius());
    }

    @Test
    void findGeoHashesWithinRadius_radiusLargeEnoughToFullMatchFourHashes(){
        int precision = 3;
        String uluruGeohash = "qgm";
        Rectangle rect = GeoHashUtils.bbox(uluruGeohash);
        GeoPoint centerPoint = new GeoPoint(rect.maxLat, rect.minLon);
        double radius = GeoUtils.geoHashCellWidth(precision) * 1.5;

        ProximityHashResult result = ProximityHash.findGeohashesWithinRadius(centerPoint, radius, precision);

        String[] expctHashesFull = {uluruGeohash, "qgt", "qgs", "qgk"};
        String[] expctHashesPrt = {"qgw", "qg7", "qgg", "qgv", "qgy", "qgh", "qgj", "qgn", "qgq", "qge", "qgu", "qg5"};
        assertEquals(expctHashesFull.length, result.getHashesWithinRadius().size());
        assertSetContainsGeohashes(expctHashesFull, result.getHashesWithinRadius());
        assertEquals(expctHashesPrt.length, result.getHashesPartiallyWithinRadius().size());
        assertSetContainsGeohashes(expctHashesPrt, result.getHashesPartiallyWithinRadius());
    }

    @Test
    void findGeoHashesWithinRadius_radiusLargeEnoughToFullMatchManyHashes(){
        int precision = 2;
        String balkansItalyHash = "sr";
        Rectangle rect = GeoHashUtils.bbox(balkansItalyHash);
        GeoPoint centerPoint = new GeoPoint(rect.maxLat, rect.minLon);
        double radius = GeoUtils.geoHashCellWidth(precision) * 3;

        ProximityHashResult result = ProximityHash.findGeohashesWithinRadius(centerPoint, radius, precision);

        String[] expctHashesFull = {balkansItalyHash, "ub", "uc", "ud", "ue", "uf", "ug", "uh", "uk", "us", "uu", "s5",
                "s7", "g2", "g3", "er", "et", "g6", "eu", "g7", "g8", "ev", "g9", "ew", "ex", "ey", "ez", "sh", "sj",
                "sk", "sm", "u0", "gb", "sn", "u1", "gc", "u2", "sp", "gd", "u3", "sq", "ge", "u4", "gf", "u5", "gg",
                "ss", "u6", "st", "u7", "u8", "sv", "u9", "sw", "sx", "sy", "sz", "gs", "gu", "v0", "v1", "tp", "v4",
                "v5"};
        String[] expctHashesPrt = {"v6", "v7", "sd", "se", "sg", "uj", "um", "un", "uq", "s1", "ut", "ee", "s3", "gh",
                "s4", "ef", "uv", "su", "eg", "s6", "gk", "ek", "gm", "em", "g0", "en", "g1", "ep", "eq", "g4", "gt",
                "es", "g5", "gv", "vh", "th", "vj", "vk", "tj", "tn", "v2", "v3", "tq", "tr"};
        assertEquals(expctHashesFull.length, result.getHashesWithinRadius().size());
        assertSetContainsGeohashes(expctHashesFull, result.getHashesWithinRadius());
        assertEquals(expctHashesPrt.length, result.getHashesPartiallyWithinRadius().size());
        assertSetContainsGeohashes(expctHashesPrt, result.getHashesPartiallyWithinRadius());
    }

    /*
     * I am leaving this failing test to illustrate that this library doesn't work so well near the poles.
     */
    @Disabled
    @Test
    void findGeoHashesWithinRadius_radiusBarelyLargeEnoughToFullMatchCenterHash_antarctica(){
        int precision = 7;
        String antarcticaGeohash = "h915";
        GeoPoint centerPoint = getCenterOfGeohashRectangle(antarcticaGeohash, precision);
        double distToSide = (GeoUtils.geoHashCellWidth(precision) / 2);
        double radius = Math.sqrt(2 * Math.pow(distToSide, 2));

        ProximityHashResult result = ProximityHash.findGeohashesWithinRadius(centerPoint, radius, precision);

        assertEquals(1, result.getHashesWithinRadius().size());
        assertTrue(result.getHashesWithinRadius().contains(antarcticaGeohash));
        String[] expectedHashes = {"h90u", "h91h", "h91k", "h917", "h916", "h914", "h90f", "h90g"};
        assertEquals(expectedHashes.length, result.getHashesPartiallyWithinRadius().size());
        assertSetContainsGeohashes(expectedHashes, result.getHashesPartiallyWithinRadius());
    }

    private void assertSetContainsGeohashes(String[] expectedHashes, Set<String> actualHashes) {
        for (String hash : expectedHashes) {
            assertTrue(actualHashes.contains(hash));
        }
    }

    private GeoPoint getCenterOfGeohashRectangle(String geohash, int precision) {
        Rectangle rectangle = GeoHashUtils.bbox(geohash);

        double width = rectangle.maxLon - rectangle.minLon;
        double height = rectangle.maxLat - rectangle.minLat;

        return new GeoPoint(
                rectangle.minLat + (height / 2),
                rectangle.minLon + (width / 2)
        );
    }

}
