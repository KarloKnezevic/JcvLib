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
import org.jcvlib.core.Size;
import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

/**
 * Contains mathematical morphology operations.
 *
 * <P>
 * <H6>Links:</H6>
 * <OL>
 * <LI><A href="http://en.wikipedia.org/wiki/Mathematical_morphology">Mathematical morphology -- Wikipedia</A>.</LI>
 * </OL>
 * </P>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Morphology {
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
    public static final int DILATE = 0;
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
    public static final int ERODE = 1;
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
    public static final int OPEN = 2;
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
    public static final int CLOSE = 3;
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
     * <LI><A href="http://en.wikipedia.org/wiki/Morphological_gradient">Morphological gradient -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int GRADIENT = 4;
    /**
     * Morphology transformation.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Mathematical_morphology">Mathematical morphology -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @param morphologyType
     *            Type of morphology filter. Use <CODE>Filters.MORPHOLOGY_*</CODE> parameters.
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
        Image result = new Image(image);

        Image temp;
        switch (morphologyType) {
            case DILATE:
                Filters.noneLinearFilter(image, result, kernelSize, kernelSize.getCenter(), iterations, extrapolationType, new Operator() {
                    @Override
                    public Color execute(final Image aperture) {
                        Color max = new Color(aperture.getNumOfChannels(), Color.COLOR_MIN_VALUE);

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

                return result;

            case ERODE:
                Filters.noneLinearFilter(image, result, kernelSize, kernelSize.getCenter(), iterations, extrapolationType, new Operator() {
                    @Override
                    public Color execute(final Image aperture) {
                        Color min = new Color(aperture.getNumOfChannels(), Color.COLOR_MAX_VALUE);

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

                return result;

            case OPEN:
                result = null;

                temp = image;
                for (int i = 0; i < iterations; ++i) {
                    temp = Morphology.morphology(temp, kernelSize, DILATE, 1, extrapolationType);
                    temp = Morphology.morphology(temp, kernelSize, ERODE, 1, extrapolationType);
                }

                return temp;

            case CLOSE:
                result = null;

                temp = image;
                for (int i = 0; i < iterations; ++i) {
                    temp = Morphology.morphology(temp, kernelSize, ERODE, 1, extrapolationType);
                    temp = Morphology.morphology(temp, kernelSize, DILATE, 1, extrapolationType);
                }

                return temp;

            case GRADIENT:
                result = null;

                temp = image;
                for (int i = 0; i < iterations; ++i) {
                    Image dilate = Morphology.morphology(temp, kernelSize, DILATE, 1, extrapolationType);
                    Image erode = Morphology.morphology(temp, kernelSize, ERODE, 1, extrapolationType);

                    temp = absDiff(dilate, erode);
                }

                return temp;

            default:
                throw new IllegalArgumentException(
                    "Parameter 'morphologyType' have unknown value! Use 'Filters.MORPHOLOGY_*' as a parameters!");
        }
    }

    /**
     * Morphology transformation.
     *
     * <P>
     * Use {@link Image#EXTRAPLOATION_REPLICATE} as default extrapolation type.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Mathematical_morphology">Mathematical morphology -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @param morphologyType
     *            Type of morphology filter. Use <CODE>Filters.MORPHOLOGY_*</CODE> parameters.
     * @param iterations
     *            Number of applying this filter to source image.
     * @return
     *         Image with result of applying morphology filter. Have same size, number of channels and type as a source image.
     */
    public static Image morphology(final Image image, final Size kernelSize, final int morphologyType, final int iterations) {
        return morphology(image, kernelSize, morphologyType, iterations, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * Morphology transformation.
     *
     * <P>
     * Use <CODE>1</CODE> as default iteration and {@link Image#EXTRAPLOATION_REPLICATE} as default extrapolation type.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Mathematical_morphology">Mathematical morphology -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @param morphologyType
     *            Type of morphology filter. Use <CODE>Filters.MORPHOLOGY_*</CODE> parameters.
     * @return
     *         Image with result of applying morphology filter. Have same size, number of channels and type as a source image.
     */
    public static Image morphology(final Image image, final Size kernelSize, final int morphologyType) {
        return morphology(image, kernelSize, morphologyType, 1, Image.EXTRAPLOATION_REPLICATE);
    }

    /**
     * This method <STRONG>sum</STRONG> 2 given images. Analog <STRONG>union</STRONG> for binary images and sets.
     *
     * <P>
     * If value of color more than <CODE>{@link Color#COLOR_MAX_VALUE}</CODE> this color value set
     * <CODE>{@link Color#COLOR_MAX_VALUE}</CODE>.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Union_(set_theory)">Union (set theory) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static Image sum(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(image1, "image1", image2, "image2");
        JCV.verifyIsSameChannels(image1, "image1", image2, "image2");

        /*
         * Perform operation.
         */
        final Image result = new Image(image1);

        Parallel.pixels(image1, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                for (int channel = 0; channel < image1.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, image1.get(x, y, channel) + image2.get(x, y, channel));
                }
            }
        });

        return result;
    }

    /**
     * First image <STRONG>minus</STRONG> second image. Analog <STRONG>complement</STRONG> for sets.
     *
     * <P>
     * If value of color less than <CODE>{@link Color#COLOR_MIN_VALUE}</CODE> this color value set
     * <CODE>{@link Color#COLOR_MIN_VALUE}</CODE>.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Complement_(set_theory)">Complement (set theory) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static Image minus(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(image1, "image1", image2, "image2");
        JCV.verifyIsSameChannels(image1, "image1", image2, "image2");

        /*
         * Perform operation.
         */
        final Image result = new Image(image1);

        Parallel.pixels(image1, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                for (int channel = 0; channel < image1.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, image1.get(x, y, channel) - image2.get(x, y, channel));
                }
            }
        });

        return result;
    }

    /**
     * Absolute value of difference between 2 images.
     */
    public static Image absDiff(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(image1, "image1", image2, "image2");
        JCV.verifyIsSameChannels(image1, "image1", image2, "image2");

        /*
         * Perform operation.
         */
        final Image result = new Image(image1);

        Parallel.pixels(image1, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                for (int channel = 0; channel < image1.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, Math.abs(image1.get(x, y, channel) - image2.get(x, y, channel)));
                }
            }
        });

        return result;
    }

    /**
     * This method <STRONG>multiply</STRONG> 2 images element by element. Analog <STRONG>intersection</STRONG> for sets.
     *
     * <P>
     * If value of color more than <CODE>{@link Color#COLOR_MAX_VALUE}</CODE> this color value set
     * <CODE>{@link Color#COLOR_MAX_VALUE}</CODE>.
     * </P>
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Intersection_(set_theory)">Intersection (set theory) -- Wikipedia</A>.</LI>
     * </OL>
     * </P>
     */
    public static Image mult(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(image1, "image1", image2, "image2");
        JCV.verifyIsSameChannels(image1, "image1", image2, "image2");

        /*
         * Perform operation.
         */
        final Image result = new Image(image1);

        Parallel.pixels(image1, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                for (int channel = 0; channel < image1.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, Math.abs(image1.get(x, y, channel) - image2.get(x, y, channel)));
                }
            }
        });

        return result;
    }

    /**
     * This method <STRONG>multiply</STRONG> image on number.
     *
     * <P>
     * If value of color more than <CODE>{@link Color#COLOR_MAX_VALUE}</CODE> this color value set
     * <CODE>{@link Color#COLOR_MAX_VALUE}</CODE>.
     * </P>
     */
    public static Image mult(final Image image, final double c) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");

        if (c < 0.0) {
            throw new IllegalArgumentException("Parameter 'c' (=" + Double.toString(c) + ") must be more than 0.0!");
        }

        /*
         * Perform operation.
         */
        final Image result = new Image(image);

        Parallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, image.get(x, y, channel) * c);
                }
            }
        });

        return result;
    }
}
