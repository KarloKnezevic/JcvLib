/*
 * Copyright 2012-2013 JcvLib Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
package org.jcvlib.image;

import org.jcvlib.core.Point;
import org.jcvlib.core.Rectangle;

/**
 * This structure contains some useful statistics about filled field. This is immutable object -- you can not change values of it after
 * creation.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class FloodFillStruct {

    private final int totalFillPixels;

    private final Rectangle fillRect;

    private final Point centerMass;

    public FloodFillStruct(final int totalFillPixels, final Rectangle fillRect, final Point centerOfGravity) {
        this.totalFillPixels = totalFillPixels;
        this.fillRect = fillRect;
        this.centerMass = centerOfGravity;
    }

    /**
     * Return number of pixels that was filled.
     */
    public int getTotalFillPixels() {
        return this.totalFillPixels;
    }

    /**
     * Return rectangle with filled field.
     */
    public Rectangle getFillRect() {
        return this.fillRect;
    }

    /**
     * Return center of mass for filled field.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Center_of_mass">Center of mass -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public Point getCenterMass() {
        return this.centerMass;
    }
}
