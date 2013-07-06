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
 * Base class for all image arrays. Contains values in 3-dimensional space.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public abstract class ImageArray {
    /**
     * Save original size of array.
     */
    protected final Size size;

    /**
     * Save number of channels.
     */
    protected final int numOfChannels;

    /**
     * Create new empty array.
     */
    protected ImageArray(final int width, final int height, final int numOfChannels) {
        this.size = new Size(width, height);

        if (numOfChannels <= 0) {
            throw new IllegalArgumentException("Value of 'numOfChannels' (= " + Integer.toString(numOfChannels)
                + ") must be more than 0!");
        }
        this.numOfChannels = numOfChannels;
    }

    /**
     * Return width of array.
     */
    public int getWidth() {
        return this.size.getWidth();
    }

    /**
     * Return height of array.
     */
    public int getHeight() {
        return this.size.getHeight();
    }

    /**
     * Return size of array.
     */
    public Size getSize() {
        return this.size;
    }

    /**
     * Return number of channels in current array.
     */
    public int getNumOfChannels() {
        return this.numOfChannels;
    }

    /**
     * Return number of values into current array (same as <CODE>getHeight() * getWidth() * getNumOfChannels</CODE>).
     */
    public int getN() {
        return this.getSize().getN() * this.getNumOfChannels();
    }

    /**
     * Return position in source array for given point and channel.
     */
    protected int getArrayPosition(final int x, final int y, final int channel) {
        return this.getNumOfChannels() * this.getHeight() * x + this.getNumOfChannels() * y + channel;
    }

    /**
     * Return float-point value from interval <CODE>[0.0, 255.0]</CODE> of selected channel from selected pixel
     * <STRONG>without position checking</STRONG>.
     */
    public abstract double getUnsafe(final int x, final int y, final int channel);

    /**
     * Return integer value from interval <CODE>0..255</CODE> of selected channel from selected pixel
     * <STRING>rounded to integer values</STRONG>.
     */
    public abstract int getUnsafe8I(final int x, final int y, final int channel);

    /**
     * Set float-point value from interval <CODE>[0.0, 255.0]</CODE> to selected channel from selected pixel
     * <STRONG>without position checking</STRONG>.
     */
    public abstract void setUnsafe(final int x, final int y, final int channel, final double value);

    /**
     * Set integer value from interval <CODE>0..255</CODE> to selected channel from selected pixel
     * <STRONG>without position checking</STRONG>.
     */
    public abstract void setUnsafe8I(final int x, final int y, final int channel, final int value);
}
