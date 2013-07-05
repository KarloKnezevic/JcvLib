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
package org.jcvlib.core;

/**
 * Contain <CODE>[height, width]</CODE> size of image, rectangle or something else. This is <STRONG>immutable object</STRONG> --
 * you can not change values of it after creation.
 *
 * @version 1.014
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Size {

    private final int width;

    private final int height;

    /**
     * Create new size.
     */
    public Size(int width, int height) {
        /*
         * Verify parameters.
         */
        if (width <= 0) {
            throw new IllegalArgumentException("Value of 'width' (= " + Integer.toString(width) + ") must be more than 0!");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Value of 'height' (= " + Integer.toString(height) + ") must be more than 0!");
        }

        /*
         * Initialize internal variables.
         */
        this.width = width;
        this.height = height;
    }

    /**
     * Return width of saved size.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Return height of saved size.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Return number of elements (pixels, values and etc.) into current size (same as <CODE>getWidth() * getHeight()</CODE>).
     */
    public int getN() {
        return this.getWidth() * this.getHeight();
    }

    /**
     * Return <CODE>true</CODE> if current size equivalent to object from parameter and <CODE>false</CODE> otherwise.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        Size size = null;

        if (object instanceof Size) {
            size = (Size) object;
        } else {
            return false;
        }

        if (size == this) {
            return true;
        }

        if (this.width == size.getWidth() && this.height == size.getHeight()) {
            return true;
        }

        return false;
    }

    /**
     * Return <CODE>true</CODE> if current size less or equals on width <STRONG>and</STRONG> height than given.
     */
    public boolean lessOrEqualsThan(Size size) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(size, "size");

        /*
         * Check.
         */
        return (this.getWidth() <= size.getWidth()) && (this.getHeight() <= size.getHeight());
    }

    /**
     * Return center of element with current size.
     *
     * <P>
     * For example if we have size <CODE>[3 x 2]</CODE> this method return point <CODE>(1, 0)</CODE>
     * (<STRONG>not <CODE>(1, 1)</CODE>!</STRONG>). This method round down to odd size and then found a center!
     * </P>
     */
    public Point getCenter() {
        int x = this.getWidth() / 2;
        if (this.getWidth() % 2 == 0) {
            --x;
        }

        int y = this.getHeight() / 2;
        if (this.getHeight() % 2 == 0) {
            --y;
        }

        return new Point(x, y);
    }

    /**
     * Return string with values of this size.
     */
    @Override
    public String toString() {
        return "[" + Integer.toString(this.getWidth()) + " x " + Integer.toString(this.getHeight()) + "]";
    }
}
