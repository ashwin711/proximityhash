package com.github.proximityhash;

import org.apache.lucene.geo.Rectangle;
import org.apache.lucene.util.SloppyMath;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.GeoUtils;

import java.util.*;

/**
 * A class with methods to calculate all Geohashes within a given radius from a given location (specified as a latitude,
 * longitude pair).
 *
 * @author phonaputer
 */
public class ProximityHash {

//    /**
//     * Check to see if a point is within a circle whose center is at (0,0).
//     *
//     * @param ptX X coordinate (or longitude) of the point.
//     * @param ptY Y coordinate (or latitude) of the point.
//     * @param radius Radius of the circle (in meters).
//     * @return True if the point is within the circle. False otherwise.
//     */
//    private static boolean isWithinRadius(double ptX, double ptY, double radius) {
//        return (Math.pow(ptX, 2) + Math.pow(ptY, 2)) <= Math.pow(radius, 2);
//    }
//
//    /**
//     * Find the centroid of a rectangle from its width, height, and position of its lower left corner.
//     *
//     * @param x X coordinate of the rectangle's lower left corner.
//     * @param y Y coordinate of the rectangle's lower left corner.
//     * @param height Height of the rectangle.
//     * @param width Width of the rectangle.
//     * @return Coordinates of the rectangle's centroid.
//     */
//    private static CoordinatePair getCentroid(double x, double y, double width, double height) {
//        double xCen = x + (width / 2);
//        double yCen = y + (height / 2);
//
//        return new CoordinatePair((long)xCen, (long)yCen);//todo remove this cast
//    }
//
//    /**
//     * Find points North, South, East, and West of the given point (by the given magnitudes).
//     *
//     * Finds 4 points: (lat - X, long - Y), (lat + X, long + Y), (lat + X, long - Y), (lat - X, long + Y)
//     *
//     * @param point Starting point for finding adjacent points.
//     * @param deltaMagnitude X and Y delta values to add/subtract to the longitude and latitude of the given point.
//     * @return A list of four Geopoints.
//     */
//    private static List<GeoPoint> getPointsForFindingAdjacentHashes(GeoPoint point, CoordinatePair deltaMagnitude){
//        List<GeoPoint> points = new LinkedList<>();
//
//        points.add(addToGeopoint(point, deltaMagnitude.getY(), deltaMagnitude.getX()));
//        points.add(addToGeopoint(point, -deltaMagnitude.getY(), deltaMagnitude.getX()));
//        points.add(addToGeopoint(point, deltaMagnitude.getY(), -deltaMagnitude.getX()));
//        points.add(addToGeopoint(point, -deltaMagnitude.getY(), -deltaMagnitude.getX()));
//
//        return points;
//    }
//
//    /**
//     * Generates all geohashes within the given radius of the given point (including the one the point is located in).
//     *
//     * @param startingPoint The center point of the circle within which geohashes will be returned.
//     *                      A latitude, longitude pair.
//     * @param radius The radius (in meters) within which to return geohashes.
//     * @param precision The precision of the geohashes to be returned (see geohash specification for details).
//     * @return A list of all geohash strings.
//     */
//    public static List<String> generateGeohashes(GeoPoint startingPoint, double radius, int precision) {
//        List<GeoPoint> points = new ArrayList<>();
//        List<String> geohashes = new LinkedList<>();
//
//        double latStepSize = GeoUtils.geoHashCellHeight(precision);
//        double lngStepSize = GeoUtils.geoHashCellWidth(precision);
//
//        int numLatSteps = Double.valueOf(Math.ceil(radius / latStepSize)).intValue();
//        int numLngSteps = Double.valueOf(Math.ceil(radius / lngStepSize)).intValue();
//
//        for (int latItr = 0; latItr < numLatSteps; latItr++) {
//            double curLat = latStepSize * latItr;
//
//            for (int lngItr = 0; lngItr < numLngSteps; lngItr++) {
//                double curLng = lngStepSize * lngItr;
//
//                if (isWithinRadius(curLng, curLat, radius)){
//                    CoordinatePair centroid = getCentroid(curLng, curLat, lngStepSize, latStepSize);
//
//                    points.addAll(getPointsForFindingAdjacentHashes(startingPoint, centroid));
//                }
//            }
//        }
//
//        geohashes.add(GeoHashUtils.stringEncode(startingPoint.getLon(), startingPoint.getLat(), precision));
//        for (GeoPoint point : points) {
//            geohashes.add(GeoHashUtils.stringEncode(point.getLon(), point.getLat(), precision));
//        }
//
//        return geohashes;
//    }





    // ################# NEW CODE BELOW THIS LINE #################

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

    private static boolean checkCircleIntersectsRectangleGeometry(double radius, GeoPoint center,
                                                                  Rectangle geohashRectangle, String geohash,
                                                                  Set<String> fullyWithinRadius,
                                                                  Set<String> partiallyWithinRadius) {
        return false; //TODO implement this.
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

        return false;
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
    private static GeoPoint addToGeopoint(GeoPoint point, double deltaY, double deltaX) {
        double latDiff = (deltaY / GeoUtils.EARTH_MEAN_RADIUS) * (180 / Math.PI);
        double lngDiff = ((deltaX / GeoUtils.EARTH_MEAN_RADIUS) * (180 / Math.PI)) /
                Math.cos(point.getLat() * (Math.PI / 180));

        return new GeoPoint(point.getLat() + latDiff, point.getLon() + lngDiff);
    }

    //TODO: consider max lat and lng values in case addition should wrap around
    private static List<GeoPointPlus> getSiblings(GeoPointPlus pointPlus, long deltaX, long deltaY,
                                                  Set<Integer> alreadyTouched) {
        List<GeoPointPlus> result = new LinkedList<>();

        GeoPoint point = pointPlus.getGeoPoint();
        CoordinatePair pair = pointPlus.getCoordinatePair();

        CoordinatePair pairNorth = pair.add(0, deltaY);
        CoordinatePair pairSouth = pair.add(0, -deltaY);
        CoordinatePair pairEast = pair.add(deltaX, 0);
        CoordinatePair pairWest = pair.add(-deltaX, 0);

        if (alreadyTouched.add(pairNorth.hashCode())){
            result.add(new GeoPointPlus(addToGeopoint(point, deltaY, 0), pairNorth));
        }
        if (alreadyTouched.add(pairSouth.hashCode())){
            result.add(new GeoPointPlus(addToGeopoint(point, -deltaY, 0), pairSouth));
        }
        if (alreadyTouched.add(pairEast.hashCode())){
            result.add(new GeoPointPlus(addToGeopoint(point, 0, deltaX), pairEast));
        }
        if (alreadyTouched.add(pairWest.hashCode())){
            result.add(new GeoPointPlus(addToGeopoint(point, 0, -deltaX), pairWest));
        }

        return result;
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
    public static ProximityHashResult generateGeohashes2(GeoPoint startingPoint, double radius, int precision) {
        Set<String> fullMatchHashes = new HashSet<>();
        Set<String> partialMatchHashes = new HashSet<>();

        long latStepSize = (long) Math.floor(GeoUtils.geoHashCellHeight(precision));
        long lngStepSize = (long) Math.floor(GeoUtils.geoHashCellWidth(precision));

        Queue<GeoPointPlus> pointQueue = new LinkedList<>();
        Set<Integer> touchedCoord = new HashSet<>();

        CoordinatePair startingPair = new CoordinatePair(0, 0);
        pointQueue.add(new GeoPointPlus(startingPoint, startingPair));
        touchedCoord.add(startingPair.hashCode());

        while (!pointQueue.isEmpty()){
            GeoPointPlus curPoint = pointQueue.remove();

            System.out.println(
                    "ptQ len: " + pointQueue.size() +
                    ", x:" + curPoint.getCoordinatePair().getX() +
                    ", y: " + curPoint.getCoordinatePair().getY() +
                    ", rad: " + radius);


            if (isGeohashInsideRadius(radius, startingPoint, curPoint.getGeoPoint(), fullMatchHashes,
                    partialMatchHashes, precision)) {
                pointQueue.addAll(getSiblings(curPoint, lngStepSize, latStepSize, touchedCoord));
            }
        }

        return new ProximityHashResult(fullMatchHashes, partialMatchHashes);
    }

}