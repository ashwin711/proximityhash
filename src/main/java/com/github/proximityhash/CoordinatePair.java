package com.github.proximityhash;

import lombok.Getter;
import lombok.Setter;

/**
 * A coordinate pair on a two dimensional plane.
 */
@Getter
@Setter
public class CoordinatePair {

    private double x;
    private double y;

    public CoordinatePair(double x, double y){
        this.x = x;
        this.y = y;
    }

}
