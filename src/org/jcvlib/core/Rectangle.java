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
 * Contain rectangle on image. This is <STRONG>immutable object</STRONG> -- you can not change values of it after creation.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Rectangle {

    private final Point startPoint;

    private final Size rectSize;

    /**
     * Create new rectangle.
     */
    public Rectangle(Point startPoint, Size rectSize) {
        this.startPoint = startPoint;
        this.rectSize = rectSize;
    }

    /**
     * Create new rectangle.
     */
    public Rectangle(final int x, final int y, final int width, final int height) {
        this(new Point(x, y), new Size(width, height));
    }

    /**
     * Return height-left point of this rectangle.
     */
    public Point getHeightLeftPoint() {
        return this.startPoint;
    }

    /**
     * Return X-value of height-left point of this rectangle.
     */
    public int getX() {
        return this.startPoint.getX();
    }

    /**
     * Return Y-value of height-left point of this rectangle.
     */
    public int getY() {
        return this.startPoint.getY();
    }

    /**
     * Return size of rectangle (in pixels).
     */
    public Size getSize() {
        return this.rectSize;
    }

    /**
     * Return width of rectangle.
     */
    public int getWidth() {
        return this.rectSize.getWidth();
    }

    /**
     * Return height of rectangle.
     */
    public int getHeight() {
        return this.rectSize.getHeight();
    }

    /**
     * Return number of pixels into current rectangle (same as <CODE>getWidth() * getHeight()</CODE>).
     */
    public int getN() {
        return this.getSize().getN();
    }

    /**
     * Check if given rectangle can be placed into current rectangle.
     */
    public boolean contains(final Rectangle insideRectangle) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(insideRectangle, "insideRectangle");

        /*
         * Check.
         */
        if (insideRectangle.getX() < this.getX() || insideRectangle.getY() < this.getY()
            || insideRectangle.getX() + insideRectangle.getWidth() > this.getX() + this.getWidth()
            || insideRectangle.getY() + insideRectangle.getHeight() > this.getY() + this.getHeight()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if current rectangle contains given point.
     */
    public boolean contains(final Point insidePoint) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(insidePoint, "insidePoint");

        /*
         * Check.
         */
        if (insidePoint.getX() < this.getX() || insidePoint.getX() >= this.getX() + this.getWidth() || insidePoint.getY() < this.getY()
            || insidePoint.getY() >= this.getY() + this.getHeight()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Return <CODE>true</CODE> if current rectangle equivalent to object from parameter and <CODE>false</CODE> otherwise.
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

        if (object == this) {
            return true;
        }

        if (!(object instanceof Rectangle)) {
            return false;
        }

        Rectangle rect = (Rectangle) object;
        if (this.startPoint.equals(rect.getHeightLeftPoint()) && this.rectSize.equals(rect.getSize())) {
            return true;
        }

        return false;
    }

    /**
     * Return string with values of this rectangle.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{" + this.startPoint.toString() + " x " + this.rectSize.toString() + "}";
    }
}
