package com.github.proximityhash;

import org.apache.lucene.geo.Rectangle;
import org.apache.lucene.util.SloppyMath;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.GeoUtils;

import java.util.*;

/**
 * A class with methods to calculate all Geohashes within a given radius from a given geolocation.
 *
 * @author phonaputer
 */
public class ProximityHash {

    /*
     * Adding meters to lat-long isn't 100% accurate (and it gets worse closer to the poles).
     * Thus our step size needs to be smaller than the length/height of geohashes in order to hit all of them.
     * This number can be tuned depending on where on earth you are hashing and the precision of the hashes.
     */
    private static final double MAGIC_NUMBER = 0.7;

    /**
     * Checks if the closest point on any side of the georectangle is within the radius.
     *
     * This is for the case where the rectangle touches the radius, but no corner of the rectangle is within the radius.
     */
    private static boolean checkCircleIntersectsRectangleGeometrically(double radius, GeoPoint center,
                                                                       Rectangle geoRect, String geohash,
                                                                       Set<String> partiallyWithinRadius) {
        GeoPoint nw = new GeoPoint(geoRect.maxLat, geoRect.minLon);
        GeoPoint ne = new GeoPoint(geoRect.maxLat, geoRect.maxLon);
        GeoPoint se = new GeoPoint(geoRect.minLat, geoRect.maxLon);
        GeoPoint sw = new GeoPoint(geoRect.minLat, geoRect.minLon);

        if (radius >= MathUtils.distanceToLine(center, nw, ne) ||
                radius >= MathUtils.distanceToLine(center, sw, se) ||
                radius >= MathUtils.distanceToLine(center, sw, nw) ||
                radius >= MathUtils.distanceToLine(center, se, ne)) {

            partiallyWithinRadius.add(geohash);

            return true;
        }

        return false;
    }

    /**
     * Covers the cases where at least one corner of an intersected geo hash rectangle is within the circle.
     *
     * For partial matching, it is useful to consider this as a separate case because the math is much simpler.
     */
    private static boolean checkInsideRadiusUsingSimpleMath(double radius, GeoPoint startingPoint,
                                                            Rectangle geohashRectangle, String geohash,
                                                            Set<String> fullyWithinRadius,
                                                            Set<String> partiallyWithinRadius) {

        boolean isNwCornerInside = radius >= SloppyMath.haversinMeters(
                startingPoint.getLat(), startingPoint.lon(),
                geohashRectangle.maxLat, geohashRectangle.minLon);

        boolean isNeCornerInside = radius >= SloppyMath.haversinMeters(
                startingPoint.getLat(), startingPoint.lon(),
                geohashRectangle.maxLat, geohashRectangle.maxLon);

        boolean isSeCornerInside = radius >= SloppyMath.haversinMeters(
                startingPoint.getLat(), startingPoint.lon(),
                geohashRectangle.minLat, geohashRectangle.maxLon);

        boolean isSwCornerInside = radius >= SloppyMath.haversinMeters(
                startingPoint.getLat(), startingPoint.lon(),
                geohashRectangle.minLat, geohashRectangle.minLon);

        if (isNwCornerInside && isNeCornerInside && isSeCornerInside && isSwCornerInside) {
            fullyWithinRadius.add(geohash);

            return true;
        }

        if (isNwCornerInside || isNeCornerInside || isSeCornerInside || isSwCornerInside) {
            partiallyWithinRadius.add(geohash);

            return true;
        }

        return false;
    }

    private static boolean isGeohashInsideRadius(double radius, GeoPoint startingPoint, GeoPoint point,
                                                 Set<String> fullyWithinRadius, Set<String> partiallyWithinRadius,
                                                 int precision) {
        String geohash = GeoHashUtils.stringEncode(point.lon(), point.getLat(), precision);

        if (fullyWithinRadius.contains(geohash) || partiallyWithinRadius.contains(geohash)){
            return true;
        }

        Rectangle geohashRectangle = GeoHashUtils.bbox(geohash);

        if(checkInsideRadiusUsingSimpleMath(radius, startingPoint, geohashRectangle, geohash, fullyWithinRadius,
                partiallyWithinRadius)) {
            return true;
        }

        return checkCircleIntersectsRectangleGeometrically(radius, startingPoint, geohashRectangle, geohash,
                partiallyWithinRadius);
    }

    /**
     * The radius can be so small that is doesn't even intersect a side of the parent geohash rectangle.
     * However, we still want to return that rectangle as a partial match.
     */
    private static void makeSureStartingPointHashIsInTheResult(Set<String> fullyWithinRadius, Set<String> partiallyWithinRadius,
                                                    GeoPoint startingPoint, int precision) {
        if (fullyWithinRadius.size() < 1 && partiallyWithinRadius.size() < 1) {
            partiallyWithinRadius.add(
                    GeoHashUtils.stringEncode(startingPoint.getLon(), startingPoint.getLat(), precision)
            );
        }
    }

    /**
     * Finds all geohashes within the given radius of the given point.
     *
     * @param startingPoint The center point of the circle within which geohashes will be returned.
     *                      A latitude, longitude pair.
     * @param radius The radius (in meters) within which to return geohashes.
     * @param precision The precision of the geohashes to be returned (see geohash specification for details).
     * @return One set of all geohashes which lie fully within the radius and a second set of those which lie partially
     *         within it.
     */
    public static ProximityHashResult findGeohashesWithinRadius(GeoPoint startingPoint, double radius, int precision) {
        Set<String> fullMatchHashes = new HashSet<>();
        Set<String> partialMatchHashes = new HashSet<>();

        long latStepSize = (long) (Math.floor(GeoUtils.geoHashCellHeight(precision)) * MAGIC_NUMBER);
        long lngStepSize = (long) (Math.floor(GeoUtils.geoHashCellWidth(precision)) * MAGIC_NUMBER);

        long latSteps = (long) Math.ceil(radius / latStepSize) + 1;
        long lngSteps = (long) Math.ceil(radius / lngStepSize) + 1;

        for (int latI = 0; latI < latSteps; latI++){
            long latStep = latStepSize * latI;

            for (int lngI = 0; lngI < lngSteps; lngI++) {
                long lngStep = lngStepSize * lngI;

                GeoPoint plusPlus = MathUtils.addToGeopoint(startingPoint, latStep, lngStep);
                GeoPoint plusMinus = MathUtils.addToGeopoint(startingPoint, latStep, -lngStep);
                GeoPoint minusPlus = MathUtils.addToGeopoint(startingPoint, -latStep, lngStep);
                GeoPoint minusMinus = MathUtils.addToGeopoint(startingPoint, -latStep, -lngStep);

                isGeohashInsideRadius(radius, startingPoint, plusPlus, fullMatchHashes, partialMatchHashes, precision);
                isGeohashInsideRadius(radius, startingPoint, plusMinus, fullMatchHashes, partialMatchHashes, precision);
                isGeohashInsideRadius(radius, startingPoint, minusPlus, fullMatchHashes, partialMatchHashes, precision);
                isGeohashInsideRadius(radius, startingPoint, minusMinus, fullMatchHashes, partialMatchHashes, precision);
            }
        }

        makeSureStartingPointHashIsInTheResult(fullMatchHashes, partialMatchHashes, startingPoint, precision);

        return new ProximityHashResult(fullMatchHashes, partialMatchHashes);
    }

}