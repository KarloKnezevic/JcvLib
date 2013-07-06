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
 * @version 1.022
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

    /**
     * Create image histogram.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Image_histogram">Image histogram -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param sizes
     *            Size of histogram that will be created.
     */
    public Hist(final Image image, int[] sizes) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(sizes, "sizes");
        if (image.getNumOfChannels() != sizes.length) {
            throw new IllegalArgumentException("Number of values in 'sizes' array should be the same as channels in image!");
        }

        /*
         * Perform operation.
         */
        // Calculate length.
        int len = 1;
        for (int i = 0; i < sizes.length; ++i) {
            len *= sizes[i];
        }

        // Initialize histogram.
        this.histogram = new double[len];
        for (int i = 0; i < this.histogram.length; ++i) {
            this.histogram[i] = 0.0;
        }

        // Calculate.
        double[] blob = new double[sizes.length];
        for (int i = 0; i < blob.length; ++i) {
            blob[i] = (Color.COLOR_MAX_VALUE + 1.0) / (double) sizes[i];
        }
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                double val = 1.0;
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    val *= 1.0 + image.get(x, y, channel) / blob[channel];
                }
                int pos = JCV.roundDown(val) - 1;
                this.histogram[pos] += 1.0;
            }
        }

        // Normalize.
        for (int i = 0; i < this.histogram.length; ++i) {
            this.histogram[i] /= (double) image.getSize().getN();
        }
    }

    private static int[] genArr(final int len, final int val) {
        int[] result = new int[len];
        for (int i = 0; i < result.length; ++i) {
            result[i] = val;
        }
        return result;
    }

    /**
     * Create image histogram.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Image_histogram">Image histogram -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param sizes
     *            Size of histogram that will be created.
     */
    public Hist(final Image image, final int sizes) {
        this(image, genArr(image.getNumOfChannels(), sizes));
    }

    /**
     * Create image histogram.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Image_histogram">Image histogram -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param sizes
     *            Size of histogram that will be created.
     */
    public Hist(final Image image) {
        this(image, genArr(image.getNumOfChannels(), 256));
    }

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
    public double compare(Hist hist, int compareType) {
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
        double result = 0.0;

        switch (compareType) {
            case Hist.HISTOGRAM_COMPARE_CORREL:
                double average1 = 0.0;
                double average2 = 0.0;

                // Calculate average of first histogram.
                for (int i = 0; i < this.getLength(); ++i) {
                    average1 += this.get(i);
                }
                average1 /= this.getLength();

                // Calculate average of second histogram.
                for (int i = 0; i < hist.getLength(); ++i) {
                    average2 += hist.get(i);
                }
                average2 /= hist.getLength();

                // Calculate numerator and denominator.
                double numerator = 0.0;
                double denominator1 = 0.0;
                double denominator2 = 0.0;
                double cov1;
                double cov2;
                for (int i = 0; i < this.getLength(); ++i) {
                    cov1 = this.get(i) - average1;
                    cov2 = hist.get(i) - average2;

                    numerator    += cov1 * cov2;
                    denominator1 += cov1 * cov1;
                    denominator2 += cov2 * cov2;
                }

                double denominatorSq = Math.sqrt(denominator1 * denominator2);
                if (!JCV.equalValues(denominatorSq, 0.0, JCV.PRECISION_MAX) && !Double.isNaN(denominatorSq)) {
                    result = numerator / denominatorSq;
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
                double sum1 = 0.0;
                double sum2 = 0.0;

                // Calculate sum of first histogram.
                for (int i = 0; i < this.getLength(); ++i) {
                    sum1 += this.get(i);
                }

                // Calculate sum of second histogram.
                for (int i = 0; i < hist.getLength(); ++i) {
                    sum2 += hist.get(i);
                }
                double denSqSum = Math.sqrt(sum1 * sum2);

                double multSum = 0.0;
                for (int i = 0; i < this.getLength(); ++i) {
                    multSum += Math.sqrt(this.get(i) * hist.get(i));
                }

                result = Math.sqrt(denSqSum - multSum / denSqSum);

                break;

            default:
                throw new IllegalArgumentException("Unknown compare type! Use 'JCV.HISTOGRAM_COMPARE_*' values!");
        }

        return result;
    }
}
