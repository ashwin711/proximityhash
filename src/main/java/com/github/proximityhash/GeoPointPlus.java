package com.github.proximityhash;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.geo.GeoPoint;

@Getter
public class GeoPointPlus {

    private GeoPoint geoPoint;
    private CoordinatePair coordinatePair;

    public GeoPointPlus(GeoPoint point, CoordinatePair pair) {
        this.coordinatePair = pair;
        this.geoPoint = point;
    }

}
