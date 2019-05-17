package com.github.proximityhash;

import org.elasticsearch.common.geo.GeoHashUtils;
import org.elasticsearch.common.geo.GeoUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A class with methods to calculate all Geohashes within a given radius from a given location (specified as a latitude,
 * longitude pair).
 *
 * @author phonaputer
 *
 * TODO: update everything to calculate distances on a sphere instead of pretending the earth is flat. (haversine)
 */
public class ProximityHash {

    /**
     * Checks to see if a point is within a circle.
     *
     * @param lng Longitude (or X coordinate) of the point.
     * @param lat Latitude (or Y coordinate) of the point.
     * @param centerLng Longitude (or X coordinate) of the circle's center.
     * @param centerLat Latitude (or Y coordinate) of the circle's center.
     * @param radius Radius of the circle (in meters).
     * @return True if the point is within the circle. False otherwise.
     */
    private static boolean inCircleCheck(double lng, double lat, double centerLng, double centerLat, double radius) {
        double xDiff = lng - centerLng;
        double yDiff = lat - centerLat;

        return (Math.pow(xDiff, 2) + Math.pow(yDiff, 2)) <= Math.pow(radius, 2);
    }

    /**
     * Find the centroid of a rectangle from its width, height, and position of its lower left corner.
     *
     * @param x X coordinate of the rectangle's lower left corner.
     * @param y Y coordinate of the rectangle's lower left corner.
     * @param height Height of the rectangle.
     * @param width Width of the rectangle.
     * @return Coordinates of the rectangle's centroid.
     */
    private static CoordinatePair getCentroid(double x, double y, double width, double height) {
        double xCen = x + (width / 2);
        double yCen = y + (height / 2);

        return new CoordinatePair(xCen, yCen);
    }

    /**
     * Add a pair of numbers to a latitude, longitude pair (after converting them to delta coordinates).
     *
     * @param point The lat, long pair to which the numbers will be added.
     * @param deltaY The distance (in meters) to add to the latitude.
     * @param deltaX The distance (in meters) to add to the longitude.
     * @return A Geopoint.
     */
    private static Geopoint addToGeopoint(Geopoint point, double deltaY, double deltaX) {
        double latDiff = (deltaY / GeoUtils.EARTH_MEAN_RADIUS) * (180 / Math.PI);
        double lngDiff = ((deltaX / GeoUtils.EARTH_MEAN_RADIUS) * (180 / Math.PI)) /
                Math.cos(point.getLatitude() * (Math.PI / 180));

        return new Geopoint(point.getLatitude() + latDiff, point.getLongitude() + lngDiff);
    }

    /**
     * Find points North, South, East, and West of the given point (by the given magnitudes).
     *
     * Finds 4 points: (lat - X, long - Y), (lat + X, long + Y), (lat + X, long - Y), (lat - X, long + Y)
     *
     * @param point Starting point for finding adjacent points.
     * @param deltaMagnitude X and Y delta values to add/subtract to the longitude and latitude of the given point.
     * @return A list of four Geopoints.
     */
    private static List<Geopoint> getPointsForFindingAdjacentHashes(Geopoint point, CoordinatePair deltaMagnitude){
        List<Geopoint> points = new LinkedList<>();

        points.add(addToGeopoint(point, deltaMagnitude.getY(), deltaMagnitude.getX()));
        points.add(addToGeopoint(point, -deltaMagnitude.getY(), deltaMagnitude.getX()));
        points.add(addToGeopoint(point, deltaMagnitude.getY(), -deltaMagnitude.getX()));
        points.add(addToGeopoint(point, -deltaMagnitude.getY(), -deltaMagnitude.getX()));

        return points;
    }

    /**
     * Generates all geohashes within the given radius of the given point (including the one the point is located in).
     *
     * @param startingPoint The center point of the circle within which geohashes will be returned.
     *                      A latitude, longitude pair.
     * @param radius The radius (in meters) within which to return geohashes.
     * @param precision The precision of the geohashes to be returned (see geohash specification for details).
     * @return A list of all geohash strings.
     */
    public static List<String> generateGeohashes(Geopoint startingPoint, double radius, int precision) {
        double x = 0.0;
        double y = 0.0;

        List<Geopoint> points = new ArrayList<>();
        List<String> geohashes = new LinkedList<>();

        double latStepSize = GeoUtils.geoHashCellHeight(precision);
        double lngStepSize = GeoUtils.geoHashCellWidth(precision);

        int numLatSteps = Double.valueOf(Math.ceil(radius / latStepSize)).intValue();
        int numLngSteps = Double.valueOf(Math.ceil(radius / lngStepSize)).intValue();

        for (int latItr = 0; latItr < numLatSteps; latItr++) {
            double tempLat = y + latStepSize * latItr;

            for (int lngItr = 0; lngItr < numLngSteps; lngItr++) {
                double tempLng = x + lngStepSize * lngItr;

                if (inCircleCheck(tempLng, tempLat, x, y, radius)){
                    CoordinatePair centroid = getCentroid(tempLng, tempLat, lngStepSize, latStepSize);

                    points.addAll(getPointsForFindingAdjacentHashes(startingPoint, centroid));
                }
            }
        }

        geohashes.add(GeoHashUtils.stringEncode(startingPoint.getLongitude(), startingPoint.getLatitude(), precision));
        for (Geopoint point : points) {
            geohashes.add(GeoHashUtils.stringEncode(point.getLongitude(), point.getLatitude(), precision));
        }

        return geohashes;
    }

}