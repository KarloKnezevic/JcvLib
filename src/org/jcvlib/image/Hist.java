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

import java.util.ArrayList;
import java.util.List;

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
     * <LI><A href="http://en.wikipedia.org/wiki/Correlation_and_dependenc">Correlation and dependenc -- Wikipedia</A>.</LI>
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

    private final List<Double[]> histogram;

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
        // Initialize histogram.
        this.histogram = new ArrayList<Double[]>(sizes.length);
        for (int i = 0; i < this.histogram.size(); ++i) {
            Double[] histChannel = new Double[sizes[i]];
            for (int j = 0; j < histChannel.length; ++j) {
                histChannel[j] = 0.0;
            }
            this.histogram.set(i, histChannel);
        }

        // Calculate.
        double[] blob = new double[this.histogram.size()];
        for (int i = 0; i < blob.length; ++i) {
            blob[i] = (Color.COLOR_MAX_VALUE + 1.0) / (double) sizes[i];
        }
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    int pos = JCV.roundDown(image.get(x, y, channel) / blob[channel]) + channel * sizes[channel];
                    this.histogram.get(channel)[pos] += 1.0;
                }
            }
        }

        // Normalize.
        for (int i = 0; i < this.histogram.size(); ++i) {
            Double[] histChannel = this.histogram.get(i);
            for (int j = 0; j < histChannel.length; ++j) {
                histChannel[j] /= (double) image.getN() / Color.COLOR_MAX_VALUE;
            }
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

    public int getNumOfChannels() {
        return this.histogram.size();
    }

    public Double[] getChannel(int num) {
        return this.histogram.get(num);
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
    public static double compareHist(double[] hist1, double[] hist2, int compareType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(hist1, "hist1");
        JCV.verifyIsNotNull(hist2, "hist2");
        if (hist1.length == 0) {
            throw new IllegalArgumentException("Size of 'hist1' should be not 0!");
        }
        if (hist2.length == 0) {
            throw new IllegalArgumentException("Size of 'hist2' should be not 0!");
        }
        if (hist1.length != hist2.length) {
            throw new IllegalArgumentException("Size of 'hist1' (= " + Integer.toString(hist1.length) + ") and 'hist2' (= "
                + Integer.toString(hist2.length) + ") should be the same!");
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
                for (int i = 0; i < hist1.length; ++i) {
                    average1 += hist1[i];
                }
                average1 /= hist1.length;

                // Calculate average of second histogram.
                for (int i = 0; i < hist2.length; ++i) {
                    average2 += hist2[i];
                }
                average2 /= hist2.length;

                // Calculate numerator and denominator.
                double numerator = 0.0;
                double denominator1 = 0.0;
                double denominator2 = 0.0;
                double cov1;
                double cov2;
                for (int i = 0; i < hist1.length; ++i) {
                    cov1 = hist1[i] - average1;
                    cov2 = hist2[i] - average2;

                    numerator += cov1 * cov2;
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
                for (int i = 0; i < hist1.length; ++i) {
                    if (hist1[i] > 0) {
                        double diff = hist1[i] - hist2[i];
                        result += (diff * diff) / hist1[i];
                    }
                }
                result /= Color.COLOR_MAX_VALUE;

                break;

            case Hist.HISTOGRAM_COMPARE_INTERSECT:
                for (int i = 0; i < hist1.length; ++i) {
                    result += Math.min(hist1[i], hist2[i]);
                }
                result /= Color.COLOR_MAX_VALUE;

                break;

            case Hist.HISTOGRAM_COMPARE_BHATTACHARYYA:
                double sum1 = 0.0;
                double sum2 = 0.0;

                // Calculate sum of first histogram.
                for (int i = 0; i < hist1.length; ++i) {
                    sum1 += hist1[i];
                }

                // Calculate sum of second histogram.
                for (int i = 0; i < hist2.length; ++i) {
                    sum2 += hist2[i];
                }
                double denSqSum = Math.sqrt(sum1 * sum2);

                double multSum = 0.0;
                for (int i = 0; i < hist1.length; ++i) {
                    multSum += Math.sqrt(hist1[i] * hist2[i]);
                }

                result = Math.sqrt(1.0 - multSum / denSqSum);

                break;

            default:
                throw new IllegalArgumentException("Unknown compare type! Use 'JCV.HISTOGRAM_COMPARE_*' values!");
        }

        return result;
    }
}
