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

import org.jcvlib.core.ImageArray;
import org.jcvlib.core.ImageArray64F;
import org.jcvlib.core.Image;
import org.jcvlib.core.ImageArray8I;
import org.jcvlib.parallel.Parallel;

/**
 * Compare time access to vales in {@link Image} and in standard array.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ArrayPerformance {

    private static final int channels = 4;

    private static double testImage64F(final int width, final int height, final int numOfIterations) {
        // Initialize.
        final Image image = new Image(width, height, channels, Image.TYPE_64F);

        // Time catch.
        final long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            for (int x = 0; x < image.getWidth(); ++x) {
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                        image.set(x, y, channel, x * y * channel * image.get(x, y, channel));
                    }
                }
            }
        }

        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }

    private static double testImage8I(final int width, final int height, final int numOfIterations) {
        // Initialize.
        final Image image = new Image(width, height, channels, Image.TYPE_8I);

        // Time catch.
        final long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            for (int x = 0; x < image.getWidth(); ++x) {
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                        image.set(x, y, channel, x * y * channel * image.get(x, y, channel));
                    }
                }
            }
        }

        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }

    private static double testImageArray64F(final int width, final int height, final int numOfIterations) {
        // Initialize.
        final ImageArray array = new ImageArray64F(width, height, channels);

        // Time catch.
        final long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            for (int x = 0; x < array.getWidth(); ++x) {
                for (int y = 0; y < array.getHeight(); ++y) {
                    for (int channel = 0; channel < array.getNumOfChannels(); ++channel) {
                        array.setUnsafe(x, y, channel, x * y * channel * array.getUnsafe(x, y, channel));
                    }
                }
            }
        }

        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }

    private static double testImageArray8I(final int width, final int height, final int numOfIterations) {
        // Initialize.
        final ImageArray array = new ImageArray8I(width, height, channels);

        // Time catch.
        final long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            for (int x = 0; x < array.getWidth(); ++x) {
                for (int y = 0; y < array.getHeight(); ++y) {
                    for (int channel = 0; channel < array.getNumOfChannels(); ++channel) {
                        array.setUnsafe(x, y, channel, x * y * channel * array.getUnsafe(x, y, channel));
                    }
                }
            }
        }

        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }

    private static double testDoubleArray(final int width, final int height, final int numOfIterations) {
        // Initialize.
        final double[] array = new double[width * height * channels];

        // Time catch.
        final long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = i * array[i];
            }
        }

        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }

    private static double testByteArray(final int width, final int height, final int numOfIterations) {
        // Initialize.
        final byte[] array = new byte[width * height * channels];

        // Time catch.
        final long startTime = System.currentTimeMillis();

        // Start testing.
        for (int num = 0; num < numOfIterations; ++num) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = (byte) (i * array[i]);
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

        Parallel.setNumOfWorkers(1);
        for (int mSize : sizes) {
            System.out.println("Size " + mSize + "x" + mSize + " by " + numOfIterations + " iterations: ");
            System.out.println("    Image64F:      " + testImage64F(mSize, mSize, numOfIterations));
            System.out.println("    Image8I:       " + testImage8I(mSize, mSize, numOfIterations));
            System.out.println("    ImageArray64F: " + testImageArray64F(mSize, mSize, numOfIterations));
            System.out.println("    ImageArray8I:  " + testImageArray8I(mSize, mSize, numOfIterations));
            System.out.println("    double[]:      " + testDoubleArray(mSize, mSize, numOfIterations));
            System.out.println("    byte[]:        " + testByteArray(mSize, mSize, numOfIterations));
        }
    }
}
