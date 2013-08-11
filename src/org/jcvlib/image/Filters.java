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

import java.util.Arrays;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.core.Rectangle;
import org.jcvlib.core.Size;
import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

import Jama.Matrix;

/**
 * Contains base filters.
 *
 * <P>
 * <H6>Links:</H6>
 * <OL>
 * <LI><A href="http://lodev.org/cgtutor/filtering.html">Image Filtering</A>.</LI>
 * <LI>Shapiro L., Stockman G. -- Computer Vision. 2000.</LI>
 * <LI>Gonzalez R. C., Woods R. E. -- Digital Image Processing. 2nd ed. 2002.<LI>
 * <LI>Szeliski R. -- Computer Vision: Algorithms and Applications. 2010.<LI>
 * </OL>
 * </P>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Filters {
    /**
     * Binary threshold.
     *
     * <P>
     * <CODE><PRE>
     * if src(x, y) <= threshold
     *      src(x, y) := 0
     * else
     *      src(x, y) := maxVal
     * </PRE></CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int THRESHOLD_BINARY = 0;

    /**
     * Binary inverted threshold.
     *
     * <P>
     * <CODE><PRE>
     * if src(x, y) <= threshold
     *      src(x, y) := maxVal
     * else
     *      src(x, y) := 0
     * </PRE></CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int THRESHOLD_BINARY_INV = 1;

    /**
     * Truncated threshold.
     *
     * <P>
     * <CODE><PRE>
     * if src(x, y) <= threshold
     *      // Do nothing.
     * else
     *      src(x, y) := threshold
     * </PRE></CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int THRESHOLD_TRUNC = 2;

    /**
     * Threshold to zero.
     *
     * <P>
     * <CODE><PRE>
     * if src(x, y) <= threshold
     *      src(x, y) := 0
     * else
     *      // Do nothing.
     * </PRE></CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int THRESHOLD_TO_ZERO = 3;

    /**
     * Threshold to zero inverted.
     *
     * <P>
     * <CODE><PRE>
     * if src(x, y) <= threshold
     *      // Do nothing.
     * else
     *      src(x, y) := 0
     * </PRE></CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int THRESHOLD_TO_ZERO_INV = 4;

    /**
     * Calculate mean value for all pixels from aperture of current pixel. All values have same weight.
     */
    public static final int ADAPTIVE_MEAN = 0;

    /**
     * Calculate mean value for all pixels from aperture of current pixel. All values have same weight. Invert result.
     */
    public static final int ADAPTIVE_MEAN_INV = 1;

    /**
     * Calculate mean value for all pixels from aperture of current pixel. All values have weight from Gaussian matrix.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int ADAPTIVE_GAUSSIAN = 2;

    /**
     * Calculate mean value for all pixels from aperture of current pixel. All values have weight from Gaussian matrix. Invert result.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int ADAPTIVE_GAUSSIAN_INV = 3;

    /**
     * Roberts cross.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Roberts_Cross">Roberts Cross -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int EDGE_DETECT_ROBERTS = 0;
    /**
     * Prewitt operator.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Prewitt_operator">Prewitt operator -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int EDGE_DETECT_PREWITT = 1;

    /**
     * Sobel operator.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Sobel_operator">Sobel operator -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int EDGE_DETECT_SOBEL = 2;

    /**
     * Scharr operator.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Sobel_operator#Alternative_operators">Sobel operator.
     * Alternative_operators -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int EDGE_DETECT_SCHARR = 3;

    /**
     * Coefficient that uses for select kernel size from sigma and back (= 6.0).
     */
    private static final double sigmaSizeCoeff = 6.0;

    /**
     * Box blur.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Box_blur">Box blur -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int BLUR_BOX = 0;

    /**
     * Gaussian blur.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int BLUR_GAUSSIAN = 1;

    /**
     * Median filter.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Median_filter">Median filter -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int BLUR_MEDIAN = 2;

    /**
     * Kuwahara blur.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://rsbweb.nih.gov/ij/plugins/kuwahara.html">Kuwahara Filter</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int BLUR_KUWAHARA = 3;

    /**
     * Sharpen image using Discrete Laplace operator.
     *
     * <P>
     * Algorithm:
     * <OL>
     * <LI>Apply discrete Laplace operator to source image.</LI>
     * <LI>Summarize source image with image from step 1.</LI>
     * </OL>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Discrete_Laplace_operator">Discrete Laplace operator -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int SHARPEN_LAPLACIAN = 0;

    /**
     * Sharpen image using common-used sharpen matrix.
     */
    public static final int SHARPEN_MODERN = 1;

    /**
     * Dilation.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Dilation_(morphology)">Dilation (morphology) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int MORPHOLOGY_DILATE = 0;

    /**
     * Erosion.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Erosion_(morphology)">Erosion (morphology) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int MORPHOLOGY_ERODE = 1;

    /**
     * Opening.
     *
     * <P>
     * <CODE>open(image, kernel) = dilate(erode(image, kernel), kernel)</CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Opening_(morphology)">Opening (morphology) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int MORPHOLOGY_OPEN = 2;

    /**
     * Closing.
     *
     * <P>
     * <CODE>close(image, kernel) = erode(dilate(image, kernel), kernel)</CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Closing_(morphology)">Closing (morphology) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int MORPHOLOGY_CLOSE = 3;

    /**
     * Morphological gradient.
     *
     * <P>
     * <CODE>morphologyGradient(image, kernel) = dilate(image, kernel) - erode(image, kernel)</CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Morphological_Gradient">Morphological Gradient -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int MORPHOLOGY_GRADIENT = 4;

    /**
     * White top-hat.
     *
     * <P>
     * <CODE>white_top_hat(image, kernel) = image - open(image, kernel)</CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Top-hat_transform">Top-hat transform -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int MORPHOLOGY_WHITE_TOP_HAT = 5;

    /**
     * Black top-hat.
     *
     * <P>
     * <CODE>black_top_hat(image, kernel) = close(image, kernel) - image</CODE>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="https://en.wikipedia.org/wiki/Top-hat_transform">Top-hat transform -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int MORPHOLOGY_BLACK_TOP_HAT = 6;

    /**
     * Nonlinear filter.
     *
     * <P>
     * For each pixel on this image apply given operator. This operator get kernel values and put result into current pixel.
     * For example, we have kernel size <CODE>[4 x 4]</CODE> and anchor <CODE>(1, 1)</CODE> for each pixel on image we select a field:
     *
     * <PRE>
     * <CODE>
     *     0 1 2 3
     *   +---------+
     * 0 | o o o o |
     * 1 | o x o o |
     * 2 | o o o o |
     * 3 | o o o o |
     *   +---------+
     * </CODE>
     * </PRE>
     *
     * And put this values into operator. Result of this operator set to current pixel.
     * </P>
     *
     * <P>
     * Some filters need kernel with only odd sizes and anchor only in the center (for example, Gaussian blur).
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Nonlinear_filter">Nonlinear filter -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param source
     *            Source image.
     * @param result
     *            Result image.
     * @param kernelSize
     *            Size of kernel that will be used for image operation.
     * @param anchor
     *            Anchor of the kernel that contain the relative position of a filtered point within the kernel.
     * @param iterations
     *            Number of iteration this filter to source image.
     * @param extrapolationType
     *            Type of extrapolation on image border. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @param operator
     *            Operation that should get kernel on each step.
     */
    public static void noneLinearFilter(final Image source, Image result, final Size kernelSize, final Point anchor,
        final int iterations, final int extrapolationType, final Operator operator) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(source, "source", result, "result");

        JCV.verifyIsNotNull(kernelSize, "kernelSize");
        if (kernelSize.getWidth() > source.getWidth()) {
            throw new IllegalArgumentException("Parameter 'kernelSize.getWidth()' should be in interval 0.."
                + Integer.toString(source.getWidth()) + "!");
        }
        if (kernelSize.getHeight() > source.getHeight()) {
            throw new IllegalArgumentException("Parameter 'kernelSize.getHeight()' should be in interval 0.."
                + Integer.toString(source.getHeight()) + "!");
        }

        JCV.verifyIsNotNull(anchor, "anchor");
        if (anchor.getX() >= kernelSize.getWidth()) {
            throw new IllegalArgumentException("Parameter 'anchor.getX()' should be in interval 0.."
                + Integer.toString(kernelSize.getWidth() - 1) + "!");
        }
        if (anchor.getY() >= kernelSize.getHeight()) {
            throw new IllegalArgumentException("Parameter 'anchor.getY()' should be in interval 0.."
                + Integer.toString(kernelSize.getHeight() - 1) + "!");
        }

        if (iterations < 0) {
            throw new IllegalArgumentException("Parameter 'iterations' should be more or equals than 0!");
        }

        JCV.verifyIsNotNull(operator, "run");

        /*
         * Perform transformation.
         */
        Image temp = source;
        for (int i = 0; i < iterations; ++i) {
            final Image currentSource = temp;
            final Image currentResult = result;

            // Create extend image.
            final Image sourceExtend = new Image(source.getWidth() + kernelSize.getWidth() - 1,
                source.getHeight() + kernelSize.getHeight() - 1, source.getNumOfChannels(), source.getType());

            // Fill extend image.
            Parallel.pixels(sourceExtend, new PixelsLoop() {
                @Override
                public void execute(final int x, final int y) {
                    for (int channel = 0; channel < sourceExtend.getNumOfChannels(); ++channel) {
                        sourceExtend.set(x, y, channel,
                            currentSource.get(x - anchor.getX(), y - anchor.getY(), channel,
                                extrapolationType));
                    }
                }
            });

            // Run operator for each pixel from extended image.
            Parallel.pixels(
                sourceExtend.getSubimage(0, 0, sourceExtend.getWidth() - kernelSize.getWidth() + 1,
                    sourceExtend.getHeight() - kernelSize.getHeight() + 1), new PixelsLoop() {
                    @Override
                    public void execute(final int x, final int y) {
                        Image aperture = sourceExtend.getSubimage(new Rectangle(new Point(x, y), kernelSize));
                        currentResult.set(new Point(x, y), operator.execute(aperture));
                    }
                });

            temp = currentResult.copy();
        }

        result = temp;
    }

    /**
     * Convolves an image with the kernel. Common-used method for apply linear matrix filter.
     *
     * <P>
     * For example, we have matrix kernel:
     *
     * <PRE>
     * <CODE>
     *     0 1 2
     *   +-------+
     * 0 | 1 1 1 |
     * 1 | 1 1 1 |
     * 2 | 1 1 1 |
     *   +-------+
     * </CODE>
     * </PRE>
     *
     * with <CODE>div = 9</CODE> and <CODE>offset = 0</CODE>.
     * </P>
     *
     * <P>
     * For each pixels we select aperture (using extrapolation on the image borders):
     *
     * <PRE>
     * <CODE>
     *     0 1 2
     *   +-------+
     * 0 | o o o |
     * 1 | o x o |
     * 2 | o o o |
     *   +-------+
     * </CODE>
     * </PRE>
     *
     * and multiply each value from aperture to corresponding value from kernel. For example value in position <CODE>(0, 1)</CODE> from
     * aperture will be multiply to value <CODE>(0, 1)</CODE> from kernel.
     * </P>
     *
     * <P>
     * Then all multiplied values are summarized, divided to <CODE>div</CODE> value and to the result added <CODE>offset</CODE> value.
     * </P>
     *
     * <P>
     * Result of this operations saved into output image into corresponding position.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Linear_filter">Linear filter -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param kernel
     *            Kernel to perform convolution. <STRONG>Should have odd size both all dimensions (1, 3, 5, ...)!</STRONG>
     * @param div
     *            Coefficient to division.
     * @param offset
     *            Value to offset the result.
     * @param extrapolationType
     *            Type of extrapolation on image border. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying linear filter. Have same size, number of channels and type as a source image.
     */
    public static Image linearFilter(final Image image, final Matrix kernel, final double div, final double offset,
        final int extrapolationType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(kernel, "kernel");

        /*
         * Perform transformation.
         */
        final Size kernelSize = new Size(kernel.getColumnDimension(), kernel.getRowDimension());
        JCV.verifyOddSize(kernelSize, "kernel.getSize()");

        final Image result = image.getSame();

        Filters.noneLinearFilter(image, result, kernelSize, kernelSize.getCenter(), 1, extrapolationType, new Operator() {
            @Override
            public Color execute(final Image aperture) {
                final Color res = new Color(aperture.getNumOfChannels());

                double[] conv = aperture.convolve(kernel);
                for (int i = 0; i < res.getNumOfChannels(); ++i) {
                    res.set(i, conv[i] / div + offset);
                }

                return res;
            }
        });

        return result;
    }

    /**
     * Implement separable filter (2 consistent linear transformations with first and second filter).
     *
     * @param image
     *            Source image.
     * @param kernelFirst
     *            Kernel to perform convolution at <STRONG>first step</STRONG>.
     *            <STRONG>Should have odd size for all dimensions (1, 3, 5, ...)!</STRONG>
     * @param kernelSecond
     *            Kernel to perform convolution at <STRONG>second step</STRONG>.
     *            <STRONG>Should have odd size for all dimensions (1, 3, 5, ...)!</STRONG>
     * @param div
     *            Coefficient to division.
     * @param offset
     *            Value to offset the result.
     * @param extrapolationType
     *            Type of extrapolation on image border. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying separable filter. Have same size, number of channels and type as a source image.
     */
    public static Image separableFilter(final Image image, final Matrix kernelFirst, final Matrix kernelSecond, final double div,
        final double offset, final int extrapolationType) {
        // First iteration.
        final Image result = Filters.linearFilter(image, kernelFirst, div, offset, extrapolationType);

        // Second iteration.
        return Filters.linearFilter(result, kernelSecond, div, offset, extrapolationType);
    }

    /**
     * Apply threshold to given <STRONG>scalar</STRONG> value.
     *
     * @param val
     *            Current value.
     * @param threshold
     *            Threshold value.
     * @param maxVal
     *            Maximal value.
     * @param thresholdType
     *            Type of applying threshold.
     * @return
     *         Value after applying threshold.
     */
    private static double applyThreshold(final double val, final double threshold, final double maxVal, final int thresholdType) {
        switch (thresholdType) {
            case THRESHOLD_BINARY:
                if (val <= threshold) {
                    return Color.COLOR_MIN_VALUE;
                } else {
                    return maxVal;
                }

            case THRESHOLD_BINARY_INV:
                if (val <= threshold) {
                    return maxVal;
                } else {
                    return Color.COLOR_MIN_VALUE;
                }

            case THRESHOLD_TRUNC:
                if (val <= threshold) {
                    return val;
                } else {
                    return threshold;
                }

            case THRESHOLD_TO_ZERO:
                if (val <= threshold) {
                    return Color.COLOR_MIN_VALUE;
                } else {
                    return val;
                }

            case THRESHOLD_TO_ZERO_INV:
                if (val <= threshold) {
                    return val;
                } else {
                    return Color.COLOR_MIN_VALUE;
                }

            default:
                throw new IllegalArgumentException(
                    "Parameter 'thresholdType' have unknown value! Use 'Filters.THRESHOLD_*' as a parameters!");
        }
    }

    /**
     * Threshold filter.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param threshold
     *            Threshold value.
     * @param thresholdType
     *            Threshold type. Use <CODE>Filters.THRESHOLD_*</CODE> parameters.
     * @param maxVal
     *            If current color value more than threshold, set this value.
     * @return
     *         Image with result of applying threshold filter. Have same size, number of channels and type as a source image.
     */
    public static Image threshold(final Image image, final double threshold, final int thresholdType, final double maxVal) {
        /*
         * Verify parameters.
         */
        if (threshold < Color.COLOR_MIN_VALUE || threshold > Color.COLOR_MAX_VALUE) {
            throw new IllegalArgumentException("Parameter 'threshold' (=" + Double.toString(threshold) + ") must be in interval ["
                + Double.toString(Color.COLOR_MIN_VALUE) + ", " + Double.toString(Color.COLOR_MAX_VALUE) + "]!");
        }

        if (maxVal < Color.COLOR_MIN_VALUE || maxVal > Color.COLOR_MAX_VALUE) {
            throw new IllegalArgumentException("Parameter 'max' (=" + Double.toString(maxVal) + ") must be in interval ["
                + Double.toString(Color.COLOR_MIN_VALUE) + ", " + Double.toString(Color.COLOR_MAX_VALUE) + "]!");
        }

        /*
         * Perform transformation.
         */
        final Image result = image.getSame();

        Parallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, applyThreshold(image.get(x, y, channel), threshold, maxVal, thresholdType));
                }
            }
        });

        return result;
    }
    /**
     * Threshold filter.
     *
     * <P>
     * Uses {@link Color#COLOR_MAX_VALUE} by default maximal value.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param threshold
     *            Threshold value.
     * @param thresholdType
     *            Threshold type. Use <CODE>Filters.THRESHOLD_*</CODE> parameters.
     * @return
     *         Image with result of applying threshold filter. Have same size, number of channels and type as a source image.
     */
    public static Image threshold(final Image image, final double threshold, final int thresholdType) {
        return threshold(image, threshold, thresholdType, Color.COLOR_MAX_VALUE);
    }
    /**
     * Adaptive threshold.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://homepages.inf.ed.ac.uk/rbf/HIPR2/adpthrsh.htm">Adaptive Thresholding -- HIPR2</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param blockSize
     *            Size of block for detection threshold value for current pixel.
     * @param type
     *            Type of calculating threshold value. Use <CODE>Filters.ADAPTIVE_*</CODE> parameters.
     * @param maxVal
     *            If current color value more than threshold, set this value.
     * @param C
     *            Constant subtracted from the mean or weighted mean. Should be in interval <CODE>[0.0, 255.0]</CODE>.
     * @return
     *         Image with result of applying adaptive threshold filter. Have same size, number of channels and
     *         type as a source image.
     */
    public static Image adapriveThreshold(final Image image, final int blockSize, final int type, final double C, final double maxVal) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");

        final Matrix coeff = new Matrix(blockSize, blockSize);
        switch (type) {
            case ADAPTIVE_MEAN:
            case ADAPTIVE_MEAN_INV:
                coeff.setMatrix(0, blockSize - 1, 0, blockSize - 1, new Matrix(blockSize, blockSize, 1.0 / (blockSize * blockSize - 1)));
                break;
            case ADAPTIVE_GAUSSIAN:
            case ADAPTIVE_GAUSSIAN_INV:
                Matrix gaussianKernel = Filters.getGaussianKernel(blockSize);
                coeff.setMatrix(0, blockSize - 1, 0, blockSize - 1, gaussianKernel.times(gaussianKernel.transpose()));
                break;
            default:
                throw
                new IllegalArgumentException("Parameter 'adaptiveType' have unknown value! Use 'Filters.ADAPTIVE_*' as a parameters!");
        }

        final int thresholdType;
        switch (type) {
            case ADAPTIVE_MEAN:
            case ADAPTIVE_GAUSSIAN:
                thresholdType = THRESHOLD_BINARY;
                break;
            case ADAPTIVE_MEAN_INV:
            case ADAPTIVE_GAUSSIAN_INV:
                thresholdType = THRESHOLD_BINARY_INV;
                break;
            default:
                throw
                new IllegalArgumentException("Parameter 'adaptiveType' have unknown value! Use 'Filters.ADAPTIVE_*' as a parameters!");
        }

        if (C < Color.COLOR_MIN_VALUE || C > Color.COLOR_MAX_VALUE) {
            throw new IllegalArgumentException("Parameter 'C' should be in interval [0.0, 255.0]!");
        }

        /*
         * Perform transformation.
         */
        final Image result = image.getSame();

        final Size apertureSize = new Size(blockSize, blockSize);
        Filters.noneLinearFilter(image, result, apertureSize, apertureSize.getCenter(), 1, Image.EXTRAPLOATION_REPLICATE, new Operator() {
            @Override
            public Color execute(final Image aperture) {
                final Color res = new Color(aperture.getNumOfChannels());

                for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                    /*
                     * Find threshold value.
                     */
                    Point center = aperture.getSize().getCenter();
                    double sum = 0.0;
                    for (int x = 0; x < aperture.getWidth(); ++x) {
                        for (int y = 0; y < aperture.getHeight(); ++y) {
                            sum += aperture.get(x, y, channel) * coeff.get(y, x);
                        }
                    }

                    /*
                     * Apply threshold.
                     */
                    double threshold = sum - C;
                    if (threshold < Color.COLOR_MIN_VALUE) {
                        threshold = Color.COLOR_MIN_VALUE;
                    }
                    if (threshold > Color.COLOR_MAX_VALUE) {
                        threshold = Color.COLOR_MAX_VALUE;
                    }

                    double val = aperture.get(center.getX(), center.getY(), channel);

                    res.set(channel, applyThreshold(val, threshold, maxVal, thresholdType));
                }

                return res;
            }
        });

        return result;
    }
    /**
     * Adaptive threshold.
     *
     * <P>
     * Uses {@link Color#COLOR_MAX_VALUE} by default maximal value.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://homepages.inf.ed.ac.uk/rbf/HIPR2/adpthrsh.htm">Adaptive Thresholding -- HIPR2</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param blockSize
     *            Size of block for detection threshold value for current pixel.
     * @param type
     *            Type of calculating threshold value. Use <CODE>Filters.ADAPTIVE_*</CODE> parameters.
     * @param C
     *            Constant subtracted from the mean or weighted mean. Should be in interval <CODE>[0.0, 255.0]</CODE>.
     * @return
     *         Image with result of applying adaptive threshold filter. Have same size, number of channels and
     *         type as a source image.
     */
    public static Image adapriveThreshold(final Image image, final int blockSize, final int type, final double C) {
        return adapriveThreshold(image, blockSize, type, C, Color.COLOR_MAX_VALUE);
    }
    /**
     * Theshold by Otsu method.
     */
    public static int calcOtsuThreshold(final Image image) {
        final Hist hist = new Hist(image);

        // Initialize q1(t).
        final double[] q1 = new double[256];
        q1[0] = hist.get(0);
        for (int i = 1; i < q1.length; ++i) {
            q1[i] = q1[i - 1] + hist.get(i);
        }

        // Initialize mu1(t).
        final double[] mu1 = new double[256];
        mu1[0] = 0;
        for (int i = 1; i < mu1.length; ++i) {
            mu1[i] = (q1[i - 1] * mu1[i - 1] + i * hist.get(i)) / q1[i];
        }

        // Initialize mu.
        double mu = 0.0;
        for (int i = 0; i < hist.getLength(); ++i) {
            mu += i * hist.get(i);
        }

        // Initialize mu2(t).
        final double[] mu2 = new double[256];
        for (int i = 0; i < mu2.length; ++i) {
            mu2[i] = (mu - q1[i] * mu1[i]) / (1.0 - q1[i]);
        }


        double[] sb = new double[256];
        for (int i = 0; i < sb.length; ++i) {
            sb[i] = q1[i] * (1.0 - q1[i]) * Math.pow(mu1[i] - mu2[i], 2);
        }

        double max = sb[0];
        int t = 0;
        for (int i = 1; i < sb.length; ++i) {
            if (max < sb[i]) {
                max = sb[i];
                t = i;
            }
        }

        return t;
    }

    /**
     * Edge detection algorithms.
     *
     * <P>
     * Based on {@link Filters#gradientFilter(Image, Matrix, Matrix, double, int)}.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Edge_detection">Edge detection -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param edgeDetectiontype
     *            Edge detection type. Use <CODE>Filters.EDGE_DETECT_*</CODE>.
     * @param scale
     *            Scale parameter for values in result image.
     * @param extrapolationType
     *            Type extrapolation on image border. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying detecting edges filter. Have same size, number of channels and type as a source image.
     */
    public static Image edgeDetection(final Image image, final int edgeDetectiontype, final double scale, final int extrapolationType) {
        Matrix matrixKernelX = null;
        Matrix matrixKernelY = null;

        switch (edgeDetectiontype) {
            case EDGE_DETECT_ROBERTS:
                matrixKernelX = new Matrix(new double[][]{
                    { 1.0, 0.0, 0.0 },
                    { 0.0,-1.0, 0.0 },
                    { 0.0, 0.0, 0.0 }
                });
                matrixKernelY = new Matrix(new double[][]{
                    { 0.0, 0.0, 0.0 },
                    { 0.0, 0.0,-1.0 },
                    { 0.0, 1.0, 0.0 }
                });
                break;

            case EDGE_DETECT_PREWITT:
                matrixKernelX = new Matrix(new double[][]{
                    {-1.0, 0.0, 1.0 },
                    {-1.0, 0.0, 1.0 },
                    {-1.0, 0.0, 1.0 }
                });
                matrixKernelY = new Matrix(new double[][]{
                    { 1.0, 1.0, 1.0 },
                    { 0.0, 0.0, 0.0 },
                    {-1.0,-1.0,-1.0 }
                });
                break;

            case EDGE_DETECT_SOBEL:
                matrixKernelX = new Matrix(new double[][]{
                    {-1.0, 0.0, 1.0 },
                    {-2.0, 0.0, 2.0 },
                    {-1.0, 0.0, 1.0 }
                });
                matrixKernelY = new Matrix(new double[][]{
                    {-1.0,-2.0,-1.0 },
                    { 0.0, 0.0, 0.0 },
                    { 1.0, 2.0, 1.0 }
                });
                break;

            case EDGE_DETECT_SCHARR:
                matrixKernelX = new Matrix(new double[][]{
                    { 3.0, 0.0, -3.0 },
                    {10.0, 0.0,-10.0 },
                    { 3.0, 0.0, -3.0 }
                });
                matrixKernelY = new Matrix(new double[][]{
                    { 3.0, 10.0, 3.0 },
                    { 0.0,  0.0, 0.0 },
                    {-3.0,-10.0,-3.0 }
                });
                break;
        }

        return gradientFilter(image, matrixKernelX, matrixKernelY, scale, extrapolationType);
    }
    /**
     * Edge detection algorithms.
     *
     * <P>
     * Based on {@link Filters#gradientFilter(Image, Matrix, Matrix, double, int)}.
     * </P>
     *
     * <P>
     * Use <CODE>1.0</CODE> as default scale and {@link Image#EXTRAPLOATION_REFLECT} as default extrapolation type.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Edge_detection">Edge detection -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param edgeDetectiontype
     *            Edge detection type. Use <CODE>Filters.EDGE_DETECT_*</CODE>.
     * @return
     *         Image with result of applying detecting edges filter. Have same size, number of channels and type as a source image.
     */
    public static Image edgeDetection(final Image image, final int edgeDetectiontype) {
        return edgeDetection(image, edgeDetectiontype, 1.0, Image.EXTRAPLOATION_REFLECT);
    }
    /**
     * Edge detection algorithms.
     *
     * <P>
     * Based on {@link Filters#gradientFilter(Image, Matrix, Matrix, double, int)}.
     * </P>
     *
     * <P>
     * Use {@link Filters#EDGE_DETECT_SOBEL} as default edge detection method, <CODE>1.0</CODE> as default scale and
     * {@link Image#EXTRAPLOATION_REFLECT} as default extrapolation type.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Edge_detection">Edge detection -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @return
     *         Image with result of applying detecting edges filter. Have same size, number of channels and type as a source image.
     */
    public static Image edgeDetection(final Image image) {
        return edgeDetection(image, EDGE_DETECT_SOBEL, 1.0, Image.EXTRAPLOATION_REFLECT);
    }

    /**
     * Gradient filter by X and Y dimensions.
     *
     * <P>
     * This method is useful for creation own gradient methods.
     * </P>
     *
     * <P>
     * Algorithm:
     * <OL>
     * <LI>Convolve aperture with kernel <CODE>derivativeX</CODE> and save result as <CODE>Gx</CODE>.</LI>
     * <LI>Convolve aperture with kernel <CODE>derivativeY</CODE> and save result as <CODE>Gy</CODE>.</LI>
     * <LI>Calculate gradient by formula <CODE>G = SQRT(Gx<SUP>2</SUP> + Gy<SUP>2</SUP>)</CODE>.</LI>
     * </OL>
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Image_gradient">Image gradient -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param derivativeX
     *            Derivative values for X-dimension.
     *            <STRONG>Should have same size as <CODE>derivativeY</CODE></STRONG>.
     * @param derivativeY
     *            Derivative values for Y-dimension.
     *            <STRONG>Should have same size as <CODE>derivativeX</CODE></STRONG>.
     *            Needed to normalize values of different derivatives.
     * @param scale
     *            Scale parameter.
     * @param extrapolationType
     *            Operation that implements to given kernel. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying gradient filter. Have same size, number of channels and type as a source image.
     */
    public static Image gradientFilter(final Image image, final Matrix derivativeX, final Matrix derivativeY, final double scale,
        final int extrapolationType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsSameSize(derivativeX, "derivativeX", derivativeY, "derivativeY");

        /*
         * Perform transformation.
         */
        final Size dervSize = new Size(derivativeX.getColumnDimension(), derivativeX.getRowDimension());
        JCV.verifyOddSize(dervSize, "derivativeX");

        final Image result = image.getSame();

        Filters.noneLinearFilter(image, result, dervSize, dervSize.getCenter(), 1, extrapolationType, new Operator() {
            @Override
            public Color execute(final Image aperture) {
                final double[] Gx = aperture.convolve(derivativeX);
                final double[] Gy = aperture.convolve(derivativeY);

                Color res = new Color(aperture.getNumOfChannels());
                for (int channel = 0; channel < res.getNumOfChannels(); ++channel) {
                    // Calculate 'G' and multiply to scale parameter.
                    res.set(channel, scale * Math.sqrt(Gx[channel] * Gx[channel] + Gy[channel] * Gy[channel]));
                }

                return res;
            }
        });

        return result;
    }



    /**
     * Discrete Laplace operator.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Discrete_Laplace_operator">Discrete Laplace operator -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param extrapolationType
     *            Type of extrapolation. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying Laplace operator. Have same size, number of channels and type as a source image.
     */
    public static Image laplacian(final Image image, final int extrapolationType) {
        final Matrix laplaceKernel = new Matrix(new double[][]{
            { 0.0, 1.0, 0.0 },
            { 1.0,-4.0, 1.0 },
            { 0.0, 1.0, 0.0 }
        });
        final double div = -1.0;
        final double offset = Color.COLOR_MIN_VALUE;

        return Filters.linearFilter(image, laplaceKernel, div, offset, extrapolationType);
    }

    /**
     * Discrete Laplace operator.
     *
     * <P>
     * Use {@link Image#EXTRAPLOATION_REPLICATE} by default.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Discrete_Laplace_operator">Discrete Laplace operator -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static Image laplacian(final Image image) {
        return Filters.laplacian(image, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * Invert values into image: each value V invert to <CODE>({@link Color#COLOR_MAX_VALUE} - V)</CODE>.
     */
    public static Image invert(final Image image) {
        final Matrix invertKernel = new Matrix(new double[][]{
            {-1.0 }
        });
        final double div = 1.0;
        final double offset = Color.COLOR_MAX_VALUE;

        return Filters.linearFilter(image, invertKernel, div, offset, Image.EXTRAPLOATION_REFLECT);
    }

    /**
     * Release Gaussian kernel (see Gaussian function) for Gaussian filter as a <STRONG>vector with size <CODE>(width, 1)</CODE></STRONG>.
     *
     * <P>
     * Used formula: <CODE><PRE>
     * G(x) = alpha * exp{-(x<SUP>2</SUP>) / (2 * sigma<SUP>2</SUP>)}
     * </PRE></CODE> where <CODE>alpha</CODE> is a scalable parameter to <CODE>sum(G(x, y)) == 1.0</CODE>.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Gaussian_filter">Gaussian filter -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static Matrix getGaussianKernel(final int kernelSize, final double sigma2) {
        /*
         * Verify parameters.
         */
        if (kernelSize == 0 || kernelSize % 2 == 0) {
            throw new IllegalArgumentException("Parameter 'kernelSize' must have width (= " + Integer.toString(kernelSize)
                + ") is odd value and be more than 0 (1, 3, 5, ...)!");
        }

        /*
         * Create matrix.
         */
        Matrix kernel = new Matrix(kernelSize, 1);

        // Calculate values.
        final int kernelHalfSize = (int) ((kernelSize - 1) / 2);
        for (int i = -kernelHalfSize; i <= kernelHalfSize; ++i) {
            kernel.set(i + kernelHalfSize, 0, Math.exp(-(i * i) / (2.0 * sigma2)));
        }

        // Scale.
        double sum = 0.0;
        for (int i = 0; i < kernel.getRowDimension(); ++i) {
            sum += kernel.get(i, 0);
        }
        kernel = kernel.times(1.0 / sum);

        return kernel;
    }

    /**
     * Release Gaussian kernel (see Gaussian function) for Gaussian filter as a <STRONG>vector with size (width, 1)</STRONG>.
     *
     * <P>
     * <CODE>sigma</CODE> calculated automatically.
     * </P>
     *
     * <P>
     * Used formula: <CODE><PRE>
     * G(x) = alpha * exp{-(x<SUP>2</SUP>) / (2 * sigma<SUP>2</SUP>)}
     * </PRE></CODE> where <CODE>alpha</CODE> is a scalable parameter to <CODE>sum(G(x, y)) == 1.0</CODE>.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Gaussian_filter">Gaussian filter -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static Matrix getGaussianKernel(final int kernelSize) {
        return Filters.getGaussianKernel(kernelSize, Filters.getSigma(kernelSize));
    }

    /**
     * Return size of one dimension base on <CODE>sigma</CODE> value (see Gaussian blur).
     */
    public static int getKernelSize(final double sigma) {
        int size = JCV.roundDown(sigmaSizeCoeff * sigma);
        if (size % 2 == 0) {
            ++size;
        }

        return size;
    }

    /**
     * Return <CODE>sigma</CODE> base on size value of one dimension for Gaussian blur.
     */
    public static double getSigma(final int size) {
        return (double) size / sigmaSizeCoeff;
    }

    /**
     * Gaussian blur.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @param sigmaX
     *            Sigma value by X dimension (see formula).
     * @param sigmaY
     *            Sigma value by Y dimension (see formula).
     * @param extrapolationType
     *            Type of extrapolation. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying Gaussian blur filter. Have same size, number of channels and type as a source image.
     */
    public static Image gaussianBlur(final Image image, final Size kernelSize, final double sigmaX, final double sigmaY,
        final int extrapolationType) {
        final Matrix gaussianKernelX = Filters.getGaussianKernel(kernelSize.getWidth(), sigmaX);
        final Matrix gaussianKernelY = Filters.getGaussianKernel(kernelSize.getHeight(), sigmaY).transpose();
        final double div = 1.0;
        final double offset = Color.COLOR_MIN_VALUE;

        return Filters.separableFilter(image, gaussianKernelX, gaussianKernelY, div, offset, extrapolationType);
    }

    /**
     * Calculate variance of sub-rectangle of kernel. Used into Kuwahara blur.
     */
    private static double[] variance(final Image aperture, final Color average) {
        final double[] result = new double[aperture.getNumOfChannels()];
        for (int i = 0; i < aperture.getNumOfChannels(); ++i) {
            result[i] = 0.0;
        }

        for (int x = 0; x < aperture.getWidth(); ++x) {
            for (int y = 0; y < aperture.getHeight(); ++y) {
                for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                    double value = average.get(channel) - aperture.get(x, y, channel);
                    result[channel] += value * value;
                }
            }
        }

        return result;
    }

    /**
     * Kuwahara blur.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://rsbweb.nih.gov/ij/plugins/kuwahara.html">Kuwahara Filter</A>.</LI>
     * </OL>
     * </P>
     */
    private static Image kuwaharaBlur(final Image image, final Size kernelSize, final int extrapolationType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyOddSize(kernelSize, "kernelSize");
        if (kernelSize.getWidth() != kernelSize.getHeight()) {
            throw new IllegalArgumentException("Kernel should be a squre!");
        }

        /*
         * Perform transformation.
         */
        if (kernelSize.getN() > 9) {
            final Point kernelCenter = kernelSize.getCenter();
            Image result = image.getSame();

            Filters.noneLinearFilter(image, result, kernelSize, kernelCenter, 1, extrapolationType, new Operator() {
                @Override
                public Color execute(final Image aperture) {
                    final Image[] windows = new Image[4];

                    final Color[] mean = new Color[windows.length];
                    final double[][] variance = new double[windows.length][aperture.getNumOfChannels()];

                    final Size windowSize = new Size(kernelCenter.getX(), kernelCenter.getY());

                    // Create sub-images.
                    windows[0] = aperture.getSubimage(new Rectangle(0, 0,
                        windowSize.getWidth(), windowSize.getHeight()));
                    windows[1] = aperture.getSubimage(new Rectangle(kernelCenter.getX() + 1, 0,
                        windowSize.getWidth(), windowSize.getHeight()));
                    windows[2] = aperture.getSubimage(new Rectangle(0, kernelCenter.getY() + 1,
                        windowSize.getWidth(),  windowSize.getHeight()));
                    windows[3] = aperture.getSubimage(new Rectangle(kernelCenter.getX() + 1, kernelCenter.getY() + 1,
                        windowSize.getWidth(), windowSize.getHeight()));

                    // Calculate average and variance.
                    for (int i = 0; i < windows.length; ++i) {
                        mean[i] = Misc.calculateMean(windows[i]);
                        variance[i] = variance(windows[i], mean[i]);
                    }

                    // Found min of variance.
                    final Color res = new Color(aperture.getNumOfChannels());
                    for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                        int minPos = 0;
                        double minVal = variance[minPos][channel];
                        for (int i = 1; i < 4; ++i) {
                            if (variance[i][channel] < minVal) {
                                minVal = variance[i][channel];
                                minPos = i;
                            }
                        }

                        // Different values for different channels.
                        res.set(channel, mean[minPos].get(channel));
                    }

                    return res;
                }
            });

            return result;
        } else {
            return image;
        }
    }

    /**
     * Blur image.
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @param blurType
     *            Type of blur. Use <CODE>Filters.BLUR_*</CODE> parameters.
     * @param extrapolationType
     *            Type of extrapolation. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying blur filter. Have same size, number of channels and type as a source image.
     */
    public static Image blur(final Image image, final Size kernelSize, final int blurType, final int extrapolationType) {
        /*
         * Verify parameters.
         */
        JCV.verifyOddSize(kernelSize, "kernelSize");

        /*
         * Perform transformation.
         */
        switch (blurType) {
            case Filters.BLUR_BOX:
                // Create a kernel.
                final Matrix box = new Matrix(kernelSize.getHeight(), kernelSize.getWidth());
                for (int x = 0; x < box.getColumnDimension(); ++x) {
                    for (int y = 0; y < box.getRowDimension(); ++y) {
                        box.set(y, x, 1.0);
                    }
                }
                final double div = kernelSize.getN();
                final double offset = Color.COLOR_MIN_VALUE;

                // Apply filter.
                return Filters.linearFilter(image, box, div, offset, extrapolationType);

            case Filters.BLUR_GAUSSIAN:
                return Filters.gaussianBlur(image, kernelSize, Filters.getSigma(kernelSize.getWidth()),
                    Filters.getSigma(kernelSize.getHeight()), extrapolationType);

            case Filters.BLUR_MEDIAN:
                final Point kernelCenter = kernelSize.getCenter();
                final Image result = image.getSame();

                Filters.noneLinearFilter(image, result, kernelSize, kernelCenter, 1, extrapolationType, new Operator() {
                    @Override
                    public Color execute(final Image aperture) {
                        final Color res = new Color(aperture.getNumOfChannels());
                        for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                            // Copy content into temporary array.
                            final double[] tempArr = new double[aperture.getSize().getN()];

                            for (int x = 0; x < aperture.getWidth(); ++x) {
                                for (int y = 0; y < aperture.getHeight(); ++y) {
                                    tempArr[x * aperture.getHeight() + y] = aperture.get(x, y, channel);
                                }
                            }

                            // Sort temporary array.
                            Arrays.sort(tempArr);

                            // Return middle element.
                            res.set(channel, tempArr[(tempArr.length - 1) / 2]);
                        }

                        return res;
                    }
                });

                return result;

            case Filters.BLUR_KUWAHARA:
                return kuwaharaBlur(image, kernelSize, extrapolationType);

            default:
                throw new IllegalArgumentException("Parameter 'blurType' have unknown value! Use 'Filters.BLUR_*' as a parameters!");
        }
    }

    /**
     * Blur image.
     *
     * <P>
     * Use {@link Image#EXTRAPLOATION_REPLICATE} as default extrapolation type.
     * </P>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @param blurType
     *            Type of blur. Use <CODE>Filters.BLUR_*</CODE> parameters.
     * @return
     *         Image with result of applying blur filter. Have same size, number of channels and type as a source image.
     */
    public static Image blur(final Image image, final Size kernelSize, final int blurType) {
        return Filters.blur(image, kernelSize, blurType, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * Blur image.
     *
     * <P>
     * Use {@link Filters#BLUR_GAUSSIAN} as default blur type and {@link Image#EXTRAPLOATION_REPLICATE} as default extrapolation type.
     * </P>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @return
     *         Image with result of applying blur filter. Have same size, number of channels and type as a source image.
     */
    public static Image blur(final Image image, final Size kernelSize) {
        return Filters.blur(image, kernelSize, Filters.BLUR_GAUSSIAN, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * Sharpen image.
     *
     * @param image
     *            Source image.
     * @param extrapolationType
     *            Type of extrapolation. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying sharpen filter. Have same size, number of channels and type as a source image.
     */
    public static Image sharpen(final Image image, final int sharpenType, final int extrapolationType) {
        switch (sharpenType) {
            case Filters.SHARPEN_LAPLACIAN:
                return Misc.sum(Filters.laplacian(image), image);

            case Filters.SHARPEN_MODERN:
                Matrix modernSharpen = new Matrix(new double[][]{
                    { 0.0,-1.0, 0.0 },
                    {-1.0, 5.0,-1.0 },
                    { 0.0,-1.0, 0.0 }
                 });
                double div = 1.0;
                double offset = Color.COLOR_MIN_VALUE;

                return Filters.linearFilter(image, modernSharpen, div, offset, Image.EXTRAPLOATION_REFLECT);

            default:
                throw new IllegalArgumentException("Parameter 'sharpenType' have unknown value! Use 'Filters.SHARPEN_*' as a parameters!");
        }
    }

    /**
     * Sharpen image.
     *
     * <P>
     * Use {@link Image#EXTRAPLOATION_REPLICATE} as default extrapolation type.
     * </P>
     *
     * @param image
     *            Source image.
     * @param sharpenType
     *            Type of sharpen method. Use <CODE>Filters.SHARPEN_*</CODE> parameters.
     * @return
     *         Image with result of applying sharpen filter. Have same size, number of channels and type as a source image.
     */
    public static Image sharpen(final Image image, final int sharpenType) {
        return Filters.sharpen(image, sharpenType, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * Sharpen image.
     *
     * <P>
     * Use {@link Filters#SHARPEN_MODERN} as default sharpen type and {@link Image#EXTRAPLOATION_REPLICATE} as default extrapolation type.
     * </P>
     *
     * @param source
     *            Source image.
     * @return
     *         Image with result of applying sharpen filter. Have same size, number of channels and type as a source image.
     */
    public static Image sharpen(final Image source) {
        return Filters.sharpen(source, Filters.SHARPEN_MODERN, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * Morphology transformation.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Mathematical_morphology">Mathematical morphology -- Wikipedia</A>.</LI>
     * <LI><A href="http://haralick.org/journals/04767941.pdf">Haralick R. M., Sternberg S., Zhuang X. -- Image Analysis
     * Using Mathematical Morphology. 1987</A.></LI>
     * <LI>Shapiro L., Stockman G. -- Computer Vision. 2000.</LI>
     * <LI>Gonzalez R. C., Woods R. E. -- Digital Image Processing. 2nd ed. 2002.<LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @param morphologyType
     *            Type of morphology filter. Use <CODE>Morphology.*</CODE> parameters.
     * @param iterations
     *            Number of applying this filter to source image.
     * @param extrapolationType
     *            Type of extrapolation. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with result of applying morphology filter. Have same size, number of channels and type as a source image.
     */
    public static Image morphology(final Image image, final Size kernelSize, final int morphologyType, final int iterations,
        final int extrapolationType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyOddSize(kernelSize, "kernelSize");

        /*
         * Perform transformation.
         */
        Image result = image.getSame();

        switch (morphologyType) {
            case MORPHOLOGY_DILATE:
                noneLinearFilter(image, result, kernelSize, kernelSize.getCenter(), iterations, extrapolationType, new Operator() {
                    @Override
                    public Color execute(final Image aperture) {
                        final Color max = new Color(aperture.getNumOfChannels(), Color.COLOR_MIN_VALUE);

                        // Find maximum.
                        for (int x = 0; x < aperture.getWidth(); ++x) {
                            for (int y = 0; y < aperture.getHeight(); ++y) {
                                for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                                    if (max.get(channel) < aperture.get(x, y, channel)) {
                                        max.set(channel, aperture.get(x, y, channel));
                                    }
                                }
                            }
                        }

                        return max;
                    }
                });

                break;

            case MORPHOLOGY_ERODE:
                noneLinearFilter(image, result, kernelSize, kernelSize.getCenter(), iterations, extrapolationType, new Operator() {
                    @Override
                    public Color execute(final Image aperture) {
                        final Color min = new Color(aperture.getNumOfChannels(), Color.COLOR_MAX_VALUE);

                        // Find minimum.
                        for (int x = 0; x < aperture.getWidth(); ++x) {
                            for (int y = 0; y < aperture.getHeight(); ++y) {
                                for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                                    if (min.get(channel) > aperture.get(x, y, channel)) {
                                        min.set(channel, aperture.get(x, y, channel));
                                    }
                                }
                            }
                        }

                        return min;
                    }
                });

                break;

            case MORPHOLOGY_OPEN:
                result = image;
                for (int i = 0; i < iterations; ++i) {
                    result = Filters.morphology(result, kernelSize, MORPHOLOGY_DILATE, 1, extrapolationType);
                    result = Filters.morphology(result, kernelSize, MORPHOLOGY_ERODE, 1, extrapolationType);
                }

                break;

            case MORPHOLOGY_CLOSE:
                result = image;
                for (int i = 0; i < iterations; ++i) {
                    result = Filters.morphology(result, kernelSize, MORPHOLOGY_ERODE, 1, extrapolationType);
                    result = Filters.morphology(result, kernelSize, MORPHOLOGY_DILATE, 1, extrapolationType);
                }

                break;

            case MORPHOLOGY_GRADIENT:
                result = image;
                for (int i = 0; i < iterations; ++i) {
                    final Image dilate = Filters.morphology(result, kernelSize, MORPHOLOGY_DILATE, 1, extrapolationType);
                    final Image erode = Filters.morphology(result, kernelSize, MORPHOLOGY_ERODE, 1, extrapolationType);

                    result = Misc.absDiff(dilate, erode);
                }

                break;

            case MORPHOLOGY_WHITE_TOP_HAT:
                result = image;
                for (int i = 0; i < iterations; ++i) {
                    result = Misc.minus(result, Filters.morphology(result, kernelSize, MORPHOLOGY_OPEN, 1, extrapolationType));
                }
                break;

            case MORPHOLOGY_BLACK_TOP_HAT:
                result = image;
                for (int i = 0; i < iterations; ++i) {
                    result = Misc.minus(Filters.morphology(result, kernelSize, MORPHOLOGY_CLOSE, 1, extrapolationType), result);
                }
                break;

            default:
                throw new IllegalArgumentException(
                    "Parameter 'morphologyType' have unknown value! Use 'Morphology.*' as a parameters!");
        }

        return result;
    }

    /**
     * Same as {@link Filters#morphology(Image, Size, int, int, int)}, but use {@link Image#EXTRAPLOATION_REPLICATE}
     * as default extrapolation type.
     */
    public static Image morphology(final Image image, final Size kernelSize, final int morphologyType, final int iterations) {
        return morphology(image, kernelSize, morphologyType, iterations, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * Same as {@link Filters#morphology(Image, Size, int, int)}, but use <CODE>1</CODE> as default iteration and
     * {@link Image#EXTRAPLOATION_REPLICATE} as default extrapolation type.
     */
    public static Image morphology(final Image image, final Size kernelSize, final int morphologyType) {
        return morphology(image, kernelSize, morphologyType, 1, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * Interface for implement linear and nonlinear operations.
     *
     * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
     */
    public interface Operator {
        /**
         * Perform some operations and return a result.
         */
        public Color execute(final Image aperture);
    }
}
