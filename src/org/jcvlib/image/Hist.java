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

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;

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

    private int pow(final int a, final int b) {
        int res = 1;
        for (int i = 0; i < b; ++i) {
            res *= a;
        }
        return res;
    }

    /**
     * Create image histogram.
     *
     * @param image
     *            Source image.
     * @param sizes
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
        // Initialize histogram.
        this.histogram = new double[this.pow(size, image.getNumOfChannels())];
        for (int i = 0; i < this.histogram.length; ++i) {
            this.histogram[i] = 0.0;
        }

        // Calculate.
        final double blob = (1.0 + Color.COLOR_MAX_VALUE) / (double) size;
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                double base = 1.0;
                int val = 0;
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    val += JCV.round(image.get(x, y, channel) * base / blob);
                    base *= size;
                }
                this.histogram[val] += 1.0;
            }
        }

        // Normalize.
        for (int i = 0; i < this.histogram.length; ++i) {
            this.histogram[i] /= (double) image.getSize().getN();
        }
    }

    /**
     * Create image histogram. Use <CODE>256</CODE> elements per channel by default.
     *
     * @param image
     *            Source image.
     * @param sizes
     *            Size of histogram that will be created.
     */
    public Hist(final Image image) {
        this(image, JCV.roundDown(Color.COLOR_MAX_VALUE) + 1);
    }

    /**
     * Create histogram from given source.
     */
    public Hist(final double[] histogram) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(histogram, "histogram");

        /*
         * Perform operation.
         */
        this.histogram = histogram;
    }

    public int getLength() {
        return this.histogram.length;
    }

    public double get(int bin) {
        return this.histogram[bin];
    }

    /**
     * Return average of current histogram.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Average">Average -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public double getAverage() {
        double sum = 0.0;
        for (int i = 0; i < this.getLength(); ++i) {
            sum += this.get(i);
        }
        return sum / this.getLength();
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
        final double average = this.getAverage();
        double sum = 0.0;
        for (int i = 0; i < this.getLength(); ++i) {
            sum += Math.pow(this.get(i) - average, 2);
        }
        return sum;
    }

    /**
     * Compare 2 histograms.
     *
     * <P>
     * <STRONG>Histograms must have the same size!</STRONG>
     * </P>
     *
     * @param hist1
     *            First histogram.
     * @param hist2
     *            Second histogram.
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
        double average1 = this.getAverage();
        double average2 = hist.getAverage();
        double result = 0.0;
        double num;
        double denSq;
        switch (compareType) {
            case Hist.HISTOGRAM_COMPARE_CORREL:
                // Calculate numerator and denominator.
                num = 0.0;
                for (int i = 0; i < this.getLength(); ++i) {
                    num += (this.get(i) - average1) * (hist.get(i) - average2);
                }

                denSq = Math.sqrt(this.getVariance() * hist.getVariance());
                if (!JCV.equalValues(denSq, 0.0, JCV.PRECISION_MAX) && !Double.isNaN(denSq)) {
                    result = num / denSq;
                } else {
                    result = 0.0;
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
                denSq = Math.sqrt(average1 * average2 * this.getLength() * hist.getLength());

                num = 0.0;
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
}
