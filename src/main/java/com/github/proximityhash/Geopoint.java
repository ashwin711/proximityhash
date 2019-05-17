package com.github.proximityhash;

import lombok.Getter;
import lombok.Setter;

/**
 * A point on the earth, described as latitude and longitude coordinates.
 */
@Getter
@Setter
public class Geopoint {

    private double latitude;
    private double longitude;

    public Geopoint(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
