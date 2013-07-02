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
package org.jcvlib.performance;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Size;

import org.jcvlib.imageproc.Filters;
import org.jcvlib.imageproc.Operator;

import org.jcvlib.parallel.JcvParallel;

/**
 * Compare time access to vales in {@link Image} and in standard array on real task -- threshold filter.
 *
 * <P>
 * Uses 3 cases:
 * <OL>
 * <LI>Standard threshold implementation.</LI>
 * <LI>Implementation throw nonlinear filter.</LI>
 * <LI>Threshold on <CODE>double[]</CODE> array.</LI>
 * </OL>
 * </P>
 *
 * @version 1.004
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ThresholdPerformance {

    private static final int channels = 4;

    private static final double threshold = 25.0;

    private static final int thresholdType = Filters.THRESHOLD_BINARY;

    private static final double maxVal = Color.COLOR_MAX_VALUE;

    private static double testImageNonlinear(int width, int height, int numOfIterations) {
        // Initialize.
        Image image = new Image(width, height, channels, Image.TYPE_64F, new Color(channels, 10.0));

        // Time catch.
        long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            Filters.noneLinearFilter(image, image, new Size(1, 1), (new Size(1, 1)).getCenter(), 1, Image.EXTRAPLOATION_REFLECT,
                new Operator() {
                    @Override
                    public Color execute(Image aperture) {
                        Color res = new Color(aperture.getNumOfChannels());

                        for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                            double color = 0.0;

                            switch (thresholdType) {
                                case Filters.THRESHOLD_BINARY:
                                    if (aperture.get(0, 0, channel) <= threshold) {
                                        color = Color.COLOR_MIN_VALUE;
                                    } else {
                                        color = maxVal;
                                    }
                                    break;

                                case Filters.THRESHOLD_BINARY_INV:
                                    if (aperture.get(0, 0, channel) <= threshold) {
                                        color = maxVal;
                                    } else {
                                        color = Color.COLOR_MIN_VALUE;
                                    }
                                    break;

                                case Filters.THRESHOLD_TRUNC:
                                    if (aperture.get(0, 0, channel) <= threshold) {
                                        color = aperture.get(0, 0, channel);
                                    } else {
                                        color = threshold;
                                    }
                                    break;

                                case Filters.THRESHOLD_TO_ZERO:
                                    if (aperture.get(0, 0, channel) <= threshold) {
                                        color = Color.COLOR_MIN_VALUE;
                                    } else {
                                        color = aperture.get(0, 0, channel);
                                    }
                                    break;

                                case Filters.THRESHOLD_TO_ZERO_INV:
                                    if (aperture.get(0, 0, channel) <= threshold) {
                                        color = aperture.get(0, 0, channel);
                                    } else {
                                        color = Color.COLOR_MIN_VALUE;
                                    }
                                    break;

                                default:
                                    throw new IllegalArgumentException(
                                        "Parameter 'thresholdType' have unknown value! Use 'Filters.THRESHOLD_*' as a parameters!");
                            }

                            res.set(channel, color);
                        }

                        return res;
                    }
                });
        }

        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }

    private static double testImageThreshold(int width, int height, int numOfIterations) {
        // Initialize.
        Image image = new Image(width, height, channels, Image.TYPE_64F, new Color(channels, 10.0));

        // Time catch.
        long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            Filters.threshold(image, threshold, thresholdType, maxVal);
        }

        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }

    private static double testArray(int width, int height, int numOfIterations) {
        // Initialize.
        double[] array = new double[width * height * channels];
        double value = 0.0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int channel = 0; channel < channels; ++channel) {
                    array[x * height * channel + y * channel + channel] = value;
                    value += 1.0;
                }
            }
        }

        // Time catch.
        long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            for (int channel = 0; channel < channels; ++channel) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        double val = array[y * width * channel + x * channel + channel];
                        double res;

                        switch (thresholdType) {
                            case Filters.THRESHOLD_BINARY:
                                if (val <= threshold) {
                                    res = Color.COLOR_MIN_VALUE;
                                } else {
                                    res = maxVal;
                                }

                                break;

                            case Filters.THRESHOLD_BINARY_INV:
                                if (val <= threshold) {
                                    res = maxVal;
                                } else {
                                    res = Color.COLOR_MIN_VALUE;
                                }

                                break;

                            case Filters.THRESHOLD_TRUNC:
                                if (val <= threshold) {
                                    res = val;
                                } else {
                                    res = threshold;
                                }

                                break;

                            case Filters.THRESHOLD_TO_ZERO:
                                if (val <= threshold) {
                                    res = Color.COLOR_MIN_VALUE;
                                } else {
                                    res = val;
                                }

                                break;

                            case Filters.THRESHOLD_TO_ZERO_INV:
                                if (val <= threshold) {
                                    res = val;
                                } else {
                                    res = Color.COLOR_MIN_VALUE;
                                }

                                break;

                            default:
                                throw new IllegalArgumentException(
                                    "Parameter 'thresholdType' have unknown value! Use 'Filters.THRESHOLD_*' as a parameters!");
                        }

                        array[y * width * channel + x * channel + channel] = res;
                    }
                }
            }
        }

        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }

    /**
     * Run this test.
     */
    public static void main(String[] args) {
        int numOfIterations = 10;
        int[] sizes = new int[]{ 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500 };

        JcvParallel.setNumOfWorkers(1);
        for (int mSize : sizes) {
            System.out.println("Size " + mSize + "x" + mSize + " by " + numOfIterations + " iterations: ");
            System.out.println("    Nonlinear: " + testImageNonlinear(mSize, mSize, numOfIterations));
            System.out.println("    Threshold: " + testImageThreshold(mSize, mSize, numOfIterations));
            System.out.println("    double[]:  " + testArray(mSize, mSize, numOfIterations));
        }
    }
}
