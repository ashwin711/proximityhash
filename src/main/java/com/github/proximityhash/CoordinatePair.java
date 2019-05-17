package com.github.proximityhash;

import lombok.Data;

/**
 * A coordinate pair on a two dimensional plane. Integer coordinates only.
 */
@Data
public class CoordinatePair {

    private long x;
    private long y;

    public CoordinatePair(long x, long y){
        this.x = x;
        this.y = y;
    }

    public CoordinatePair add(long x, long y){
        return new CoordinatePair(this.x + x, this.y + y);
    }

}
