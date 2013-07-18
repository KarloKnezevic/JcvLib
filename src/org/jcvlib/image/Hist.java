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
package org.jcvlib.image;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

/**
 * This class contains methods for manipulate image histograms.
 *
 * <P>
 * <H6>Links:</H6>
 * <OL>
 * <LI><A href="http://en.wikipedia.org/wiki/Image_histogram">Image histogram -- Wikipedia</A>.</LI>
 * </OL>
 * </P>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Hist {
    /**
     * Correlation comparison.
     *
     * <P>
     * Return values in interval <CODE>[-1.0, 1.0]</CODE>
     * </P>
     *
     * <P>
     * Examples:
     * <UL>
     * <LI>Exact match -- <CODE>1.0</CODE></LI>
     * <LI>Half match -- <CODE>0.0</CODE></LI>
     * <LI>Total mismatch -- <CODE>-1.0</CODE></LI>
     * </UL>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Correlation_and_dependence">Correlation and dependence -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int HISTOGRAM_COMPARE_CORREL = 0;

    /**
     * Chi-Square comparison.
     *
     * <P>
     * Return values in interval <CODE>[0.0, 1.0]</CODE>
     * </P>
     *
     * <P>
     * Examples:
     * <UL>
     * <LI>Exact match -- <CODE>0.0</CODE></LI>
     * <LI>Half match -- <CODE>0.25</CODE></LI>
     * <LI>Total mismatch -- <CODE>1.0</CODE></LI>
     * </UL>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Pearson's_chi-squared_test">Pearson's chi-squared test -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int HISTOGRAM_COMPARE_CHISQR = 1;

    /**
     * Intersection comparison.
     *
     * <P>
     * Return values in interval <CODE>[0.0, 1.0]</CODE>
     * </P>
     *
     * <P>
     * Examples:
     * <UL>
     * <LI>Exact match -- <CODE>1.0</CODE></LI>
     * <LI>Half match -- <CODE>0.5</CODE></LI>
     * <LI>Total mismatch -- <CODE>0.0</CODE></LI>
     * </UL>
     * </P>
     *
     * <P>
     * <CODE><PRE>
     * d(H<SUB>1</SUB>, H<SUB>2</SUB>) = Sum<SUB>i</SUB>( Min(H<SUB>1</SUB>(i), H<SUB>2</SUB>(i)) )
     * </PRE></CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Intersection_(set_theory)">Intersection (set theory) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int HISTOGRAM_COMPARE_INTERSECT = 2;

    /**
     * Bhattacharyya comparison.
     *
     * <P>
     * Return values in interval <CODE>[0.0, 1.0]</CODE>
     * </P>
     *
     * <P>
     * Examples:
     * <UL>
     * <LI>Exact match -- <CODE>0.0</CODE></LI>
     * <LI>Half match -- <CODE>0.55</CODE></LI>
     * <LI>Total mismatch -- <CODE>1.0</CODE></LI>
     * </UL>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Bhattacharyya_distance">Bhattacharyya distance -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int HISTOGRAM_COMPARE_BHATTACHARYYA = 3;

    private final double[] histogram;

    private final int size;

    private final int channels;

    private final double blob;

    private int calcLength() {
        int res = 1;
        for (int i = 0; i < this.channels; ++i) {
            res *= this.size;
        }
        return res;
    }

    private int pos(final Image image, final int x, final int y) {
        double base = 1.0;
        int val = 0;
        for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
            val += JCV.roundDown(image.get(x, y, channel) * base / this.blob);
            base *= this.size;
        }
        return val;
    }

    /**
     * Create image histogram.
     *
     * @param image
     *            Source image.
     * @param size
     *            Size of histogram per channel.
     */
    public Hist(final Image image, final int size) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        if (size <= 0) {
            throw new IllegalArgumentException("Value of 'size' should be more than 0!");
        }

        /*
         * Perform operation.
         */
        this.size = size;
        this.channels = image.getNumOfChannels();
        this.blob = (1.0 + Color.COLOR_MAX_VALUE) / (double) this.size;

        // Initialize histogram.
        this.histogram = new double[this.calcLength()];
        for (int i = 0; i < this.histogram.length; ++i) {
            this.histogram[i] = 0.0;
        }

        // Calculate.
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                this.histogram[this.pos(image, x, y)] += 1.0;
            }
        }
        this.normalize();
    }

    /**
     * Create image histogram. Use <CODE>256</CODE> elements per channel by default.
     */
    public Hist(final Image image) {
        this(image, JCV.roundDown(Color.COLOR_MAX_VALUE) + 1);
    }

//    /**
//     * Create histogram from given source.
//     */
//    public Hist(final double[] histogram, int size, int channels) {
//        /*
//         * Verify parameters.
//         */
//        JCV.verifyIsNotNull(histogram, "histogram");
//
//        /*
//         * Perform operation.
//         */
//        this.histogram = histogram;
//        this.size = size;
//        this.channels = channels;
//        this.blob = this.calcBlob(this.size);
//
//        this.normalize();
//    }

    /**
     * @return
     *      Number of channels source image.
     */
    public int getChannels() {
        return this.channels;
    }

    /**
     * @return
     *      Length of current histogram.
     */
    public int getLength() {
        return this.histogram.length;
    }

    /**
     * Return value of selected histogram bin.
     */
    public double get(int bin) {
        return this.histogram[bin];
    }

    private void normalize() {
        double sum = 0.0;
        for (int i = 0; i < this.histogram.length; ++i) {
            sum += this.histogram[i];
        }

        for (int i = 0; i < this.histogram.length; ++i) {
            this.histogram[i] /= sum;
        }
    }

    /**
     * Return variance of current histogram.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Variance">Variance -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public double getVariance() {
        final double average = 1.0 / this.getLength();
        double sum = 0.0;
        for (int i = 0; i < this.getLength(); ++i) {
            sum += Math.pow(this.get(i) - average, 2);
        }
        return sum;
    }

    // TODO
    public Hist resize(final int newSize) {
        return null;
    }

    /**
     * Compare current histogram.
     *
     * <P>
     * <STRONG>Histograms must have the same size!</STRONG>
     * </P>
     *
     * @param hist
     *            Second histogram to compare.
     * @param compareType
     *            Type of compare histograms. Use <CODE>Hist.HISTOGRAM_COMPARE_*</CODE>.
     */
    public double compare(final Hist hist, final int compareType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(hist, "hist");
        if (this.getLength() != hist.getLength()) {
            throw new IllegalArgumentException("Length of current histogram (= " + Integer.toString(this.getLength())
                + ") and length of given histogram (= " + Integer.toString(hist.getLength()) + ") is not the same!");
        }

        /*
         * Perform operation.
         */
        final double average = 1.0 / this.getLength();
        double result = 0.0;
        double num = 0.0;
        double denSq = 0.0;
        switch (compareType) {
            case Hist.HISTOGRAM_COMPARE_CORREL:
                // Calculate numerator and denominator.
                for (int i = 0; i < this.getLength(); ++i) {
                    num += (this.get(i) - average) * (hist.get(i) - average);
                }

                denSq = Math.sqrt(this.getVariance() * hist.getVariance());
                if (!JCV.equalValues(denSq, 0.0, JCV.PRECISION_MAX) && !Double.isNaN(denSq)) {
                    result = num / denSq;
                }

                break;

            case Hist.HISTOGRAM_COMPARE_CHISQR:
                for (int i = 0; i < this.getLength(); ++i) {
                    if (this.get(i) > 0) {
                        double diff = this.get(i) - hist.get(i);
                        result += (diff * diff) / this.get(i);
                    }
                }

                break;

            case Hist.HISTOGRAM_COMPARE_INTERSECT:
                for (int i = 0; i < this.getLength(); ++i) {
                    result += Math.min(this.get(i), hist.get(i));
                }

                break;

            case Hist.HISTOGRAM_COMPARE_BHATTACHARYYA:
                denSq = average * this.getLength();

                for (int i = 0; i < this.getLength(); ++i) {
                    num += Math.sqrt(this.get(i) * hist.get(i));
                }

                result = Math.sqrt(1.0 - num / denSq);

                break;

            default:
                throw new IllegalArgumentException("Unknown compare type! Use 'JCV.HISTOGRAM_COMPARE_*' values!");
        }

        return result;
    }

    /**
     * Threshold to zero.
     *
     * <P>
     * <CODE><PRE>
     * if hist(i) <= threshold
     *      hist(i) := 0
     * else
     *      // Do nothing.
     * </PRE></CODE>
     * </P>
     */
    public void threshold(final double threshold) {
        /*
         * Verify parameters.
         */
        if (threshold < 0.0 || threshold > 1.0) {
            throw new IllegalArgumentException("Parameter 'threshold' (= " + Double.toString(threshold)
                + ") should be in interval [0.0, 1.0]!");
        }

        /*
         * Perform operation.
         */
        for (int i = 0; i < this.getLength(); ++i) {
            if (this.get(i) < threshold) {
                this.histogram[i] = 0.0;
            }
        }
        this.normalize();
    }

    public Image selectPixels(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");

        /*
         * Perform operation.
         */
        final Image result = new Image(image.getWidth(), image.getHeight(), 1, image.getType());
        Parallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                double val;
                if (histogram[pos(image, x, y)] > 0.0) {
                    val = Color.COLOR_MAX_VALUE;
                } else {
                    val = Color.COLOR_MIN_VALUE;
                }
                result.set(x, y, 0, val);
            }
        });
        return result;
    }
}
