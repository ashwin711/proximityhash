package com.github.proximityhash;

import org.apache.lucene.util.SloppyMath;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.GeoUtils;

/**
 * Math utility functions for ProximityHash.
 */
class MathUtils {

    /**
     * Returns the distance between two GeoPoints, in meters.
     */
    private static double computeDistanceBetween(GeoPoint from, GeoPoint to) {
        return SloppyMath.haversinMeters(from.getLat(), from.lon(), to.getLat(), to.getLon());
    }

    /**
     * Computes the distance on the sphere between the point p and the line segment start to end.
     *
     * Adapted from https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/PolyUtil.java#L460
     *
     * @param p the point to be measured
     * @param start the beginning of the line segment
     * @param end the end of the line segment
     * @return the distance in meters (assuming spherical earth)
     */
    static double distanceToLine(final GeoPoint p, final GeoPoint start, final GeoPoint end) {
        if (start.equals(end)) {
            return computeDistanceBetween(end, p);
        }

        final double s0lat = SloppyMath.toRadians(p.getLat());
        final double s0lng = SloppyMath.toRadians(p.getLon());
        final double s1lat = SloppyMath.toRadians(start.getLat());
        final double s1lng = SloppyMath.toRadians(start.getLon());
        final double s2lat = SloppyMath.toRadians(end.getLat());
        final double s2lng = SloppyMath.toRadians(end.getLon());

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return computeDistanceBetween(p, start);
        }
        if (u >= 1) {
            return computeDistanceBetween(p, end);
        }
        GeoPoint sa = new GeoPoint(p.getLat() - start.getLat(), p.getLon() - start.getLon());
        GeoPoint sb = new GeoPoint(u * (end.getLat() - start.getLat()), u * (end.getLon() - start.getLon()));
        return computeDistanceBetween(sa, sb);
    }

    /**
     * Note that there is no function to handle crossing the poles because this library doesn't work near them anyways.
     *
     * This doesn't work if the meridian is crossed by more than 360 degrees, but that will never happen in ProximityHash.
     */
    static double handleCrossingPrimeMeridian(double longitude){
        if (longitude > 180.0 || longitude < -180.0) {
            int sign = Integer.signum((int) longitude);

            return ((longitude * sign) - 360.0) * sign;
        }

        return longitude;
    }

    /**
     * Adds a number of meters to the latitude and the longitude of a Geopoint.
     * Addition is done on a sphere the size of Earth.
     *
     * @param point The lat, long pair to which the numbers will be added.
     * @param deltaY The distance (in meters) to add to the latitude.
     * @param deltaX The distance (in meters) to add to the longitude.
     * @return A Geopoint.
     */
    static GeoPoint addToGeopoint(GeoPoint point, double deltaY, double deltaX) {
        double latDiff = (deltaY / GeoUtils.EARTH_MEAN_RADIUS) * (180 / Math.PI);
        double lngDiff = ((deltaX / GeoUtils.EARTH_MEAN_RADIUS) * (180 / Math.PI)) /
                Math.cos(point.getLat() * (Math.PI / 180));

        return new GeoPoint(
                point.getLat() + latDiff,
                handleCrossingPrimeMeridian(point.getLon() + lngDiff)
        );
    }

}
