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

/**
 * Contain <CODE>(x, y)</CODE> position on image. This is <STRONG>immutable object</STRONG> -- you can not change values of it after
 * creation.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Point {

    private final int x;

    private final int y;

    /**
     * Create new point.
     */
    public Point(final int x, final int y) {
        /*
         * Verify parameters.
         */
        if (x < 0) {
            throw new IllegalArgumentException("Value of 'x' (= " + Integer.toString(x) + ") must be more or equals than 0!");
        }
        if (y < 0) {
            throw new IllegalArgumentException("Value of 'y' (= " + Integer.toString(y) + ") must be more or equals than 0!");
        }

        /*
         * Initialize internal variables.
         */
        this.x = x;
        this.y = y;
    }

    /**
     * Return X-value of pixel.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Return Y-value of pixel.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Return <CODE>true</CODE> if current point equivalent to object from parameter and <CODE>false</CODE> otherwise.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        Point point = null;

        if (object instanceof Point) {
            point = (Point) object;
        } else {
            return false;
        }

        if (point == this) {
            return true;
        }

        if (this.getX() == point.getX() && this.getY() == point.getY()) {
            return true;
        }

        return false;
    }

    /**
     * Return string with values of this point.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(" + Integer.toString(this.getX()) + ", " + Integer.toString(this.getY()) + ")";
    }
}
