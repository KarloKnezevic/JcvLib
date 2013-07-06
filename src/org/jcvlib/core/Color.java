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
 * This class contains all color values of some pixel.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Color {
    /**
     * Bounds of color values -- minimum value.
     */
    public static final double COLOR_MIN_VALUE = 0.0;

    /**
     * Bounds of color value -- maximum value.
     */
    public static final double COLOR_MAX_VALUE = 255.0;

    /**
     * Contains all channels of some pixel.
     */
    private final double[] color;

    /**
     * Create empty color.
     */
    public Color(final int numOfChannels) {
        /*
         * Verify parameters.
         */
        if (numOfChannels <= 0) {
            throw new IllegalArgumentException("Value of 'numOfChannels' (= " + Integer.toString(numOfChannels) + ") must be more than 0!");
        }

        /*
         * Create internal structure.
         */
        this.color = new double[numOfChannels];
    }

    /**
     * Create empty color and initialize all channels by given value (from interval <CODE>[0.0, 255.0]</CODE>).
     */
    public Color(final int numOfChannels, final double value) {
        this(numOfChannels);

        for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
            this.set(channel, value);
        }
    }

    /**
     * Create empty color and initialize it by given float-point color values (from interval <CODE>[0.0, 1.0]</CODE>).
     */
    public Color(final double[] colorValues) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(colorValues, "colorValues");
        if (colorValues.length == 0) {
            throw new IllegalArgumentException("Object 'colorValues' have no elements!");
        }

        /*
         * Create new object.
         */
        this.color = new double[colorValues.length];
        for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
            this.set(channel, colorValues[channel]);
        }
    }

    /**
     * Return number channels of current color.
     */
    public int getNumOfChannels() {
        return this.color.length;
    }

    /**
     * Verify given number of channel.
     */
    private void verifyChannel(final int channel) {
        if (channel < 0 || channel >= this.getNumOfChannels()) {
            throw new IllegalArgumentException("Value of 'channel' (= " + Integer.toString(channel) + ") must in interval 0.."
                + Integer.toString(this.getNumOfChannels() - 1) + "!");
        }
    }

    /**
     * Return value (from interval <CODE>[0.0, 255.0]</CODE>) of color from given channel number.
     */
    public double get(final int channel) {
        /*
         * Verify parameters.
         */
        this.verifyChannel(channel);

        /*
         * Return value.
         */
        return this.color[channel];
    }

    /**
     * Set value (from interval <CODE>[0.0, 255.0]</CODE>) of color to given channel number.
     */
    public void set(final int channel, double value) {
        /*
         * Verify parameters.
         */
        this.verifyChannel(channel);
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("Value of color should be not a NaN!");
        }

        /*
         * Perform operation.
         */
        if (value < Color.COLOR_MIN_VALUE) {
            value = Color.COLOR_MIN_VALUE;
        }
        if (value > Color.COLOR_MAX_VALUE) {
            value = Color.COLOR_MAX_VALUE;
        }
        this.color[channel] = value;
    }

    /**
     * Calculate normalize (all values between {@link Color#COLOR_MIN_VALUE} and {@link Color#COLOR_MAX_VALUE}) Euclidean distance between 2
     * colors.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean distance -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public double euclidDist(final Color c) {
        /*
         * Verify parameters.
         */
        if (this.getNumOfChannels() != c.getNumOfChannels()) {
            throw new IllegalArgumentException("Given color must have same number of channels (= " + Integer.toString(c.getNumOfChannels())
                + ") " + "as currect color (= " + Integer.toString(this.getNumOfChannels()) + ")!");
        }

        /*
         * Perform operation.
         */
        // Calculate sum of squares.
        double result = 0.0;
        for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
            double dist = this.get(channel) - c.get(channel);
            result += dist * dist;
        }

        return Math.sqrt(result) / Math.sqrt(this.getNumOfChannels());
    }

    /**
     * Return <CODE>true</CODE> if current color equivalent to object from parameter and <CODE>false</CODE> otherwise.
     *
     * <P>
     * Uses {@link JCV#PRECISION_MAX} by default.
     * </P>
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        return this.equals(object, JCV.PRECISION_MAX);
    }

    /**
     * Return <CODE>true</CODE> if current color equivalent to object from parameter and <CODE>false</CODE> otherwise.
     *
     * @param object
     *            Object for compare with current color.
     * @param precision
     *            Precision for compare values of pixels.
     */
    public boolean equals(final Object object, final double precision) {
        if (object == null) {
            return false;
        }

        Color color = null;

        if (object instanceof Color) {
            color = (Color) object;
        } else {
            return false;
        }

        if (color == this) {
            return true;
        }

        if (color.getNumOfChannels() != this.getNumOfChannels()) {
            return false;
        }

        for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
            if (!JCV.equalValues(color.get(channel), this.get(channel), precision)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return copy of current color.
     *
     * <P>
     * It will be a <STRONG>REAL COPY</STRONG> of current color!
     * </P>
     */
    public Color copy() {
        Color result = new Color(this.getNumOfChannels());

        for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
            result.set(channel, this.get(channel));
        }

        return result;
    }

    /**
     * Return string with values of this channel.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
            sb.append(this.get(channel));

            if (channel < this.getNumOfChannels() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");

        return sb.toString();
    }
}
