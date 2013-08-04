/*
 * Copyright 2012-2013 JcvLib Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * This class is part of Java Computer Vision Library (JcvLib).
 */
package org.jcvlib.core;

import org.jcvlib.image.Misc;

/**
 * Region is a structure that defined by filling some area into image. To get this object call
 * {@link Misc#floodFill(Image, Point, double, Color, int, int)} method.
 *
 *
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Region {

    private final Point internalPoint;

    private final int areaSize;

    private final Rectangle boundingRect;

    private final Point centroid;

    public Region(final Point internalPoint, final int areaSize, final Rectangle fillRect, final Point centroid) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(internalPoint, "internalPoint");
        if (areaSize < 0) {
            throw new IllegalArgumentException("Parameter 'areaSize' must be more oe equals than 0!");
        }
        JCV.verifyIsNotNull(fillRect, "fillRect");
        JCV.verifyIsNotNull(centroid, "centroid");

        /*
         * Save values.
         */
        this.internalPoint = internalPoint;
        this.areaSize = areaSize;
        this.boundingRect = fillRect;
        this.centroid = centroid;
    }

    /**
     * Return some point that placed into current region. Usually it is a seed point of
     * {@link Misc#floodFill(Image, Point, double, Color, int, int)}.
     */
    public Point getInternalPoint() {
        return this.internalPoint;
    }

    /**
     * Return number of pixels into current region.
     */
    public int getAreaSize() {
        return this.areaSize;
    }

    /**
     * Return minimal rectangle that contains current region.
     */
    public Rectangle getBoundingRect() {
        return this.boundingRect;
    }

    /**
     * Return centroid of current region.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Centroid">Centroid -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public Point getCentroid() {
        return this.centroid;
    }
}
