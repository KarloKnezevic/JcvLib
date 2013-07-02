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
package org.jcvlib.imageproc;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.core.Rectangle;

import org.jcvlib.parallel.JcvParallel;
import org.jcvlib.parallel.PixelsLoop;

import Jama.Matrix;

/**
 * Contain methods to perform arithmetic operation with images, like <I>sum</I>, <I>subtraction</I> and <I>multiplication</I> and etc.
 * 
 * @version 1.012
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageMath {
    /**
     * This method <STRONG>sum</STRONG> 2 given images.
     * 
     * <P>
     * If value of color more than <CODE>{@link Color#COLOR_MAX_VALUE}</CODE> this color value set
     * <CODE>{@link Color#COLOR_MAX_VALUE}</CODE>.
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
        
        JcvParallel.pixels(image1, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                for (int channel = 0; channel < image1.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, image1.get(x, y, channel) + image2.get(x, y, channel));
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
        
        JcvParallel.pixels(image1, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
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
     * If value of color more than <CODE>255.0</CODE> this color value set <CODE>255.0</CODE>.
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
        
        JcvParallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, image.get(x, y, channel) * c);
                }
            }
        });
        
        return result;
    }
    
    /**
     * Return <A href="http://en.wikipedia.org/wiki/Arithmetic_mean">arithmetic mean</A> of colors in current image.
     */
    public static Color getMean(Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        
        /*
         * Perform operation.
         */
        // Initialize.
        double[] sum = new double[image.getNumOfChannels()];
        for (int channel = 0; channel < sum.length; ++channel) {
            sum[channel] = 0.0;
        }
        
        // Sum all values.
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    sum[channel] += image.get(x, y, channel);
                }
            }
        }
        
        // Calculate average.
        Color mean = new Color(image.getNumOfChannels());
        for (int channel = 0; channel < sum.length; ++channel) {
            mean.set(channel, sum[channel] / image.getSize().getN());
        }
        
        return mean;
    }
    
    /**
     * Calculate <A href="http://docs.gimp.org/en/plug-in-convmatrix.html">matrix convolution</A>.
     * 
     * @param image
     *            Source image.
     * @param kernel
     *            Matrix for convolution.
     * @return
     *         Array with result of convolution matrix to all channels of image.
     */
    public static double[] convolve(Image image, Matrix kernel) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(kernel, "kernel");
        
        /*
         * Perform operation.
         */
        double[] result = new double[image.getNumOfChannels()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = 0.0;
        }
        
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    result[channel] += image.get(x, y, channel) * kernel.get(y, x);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Put one image to another (using <A href="http://en.wikipedia.org/wiki/Alpha_compositing">Alpha
     * channel</A>).
     * 
     * <P>
     * The images must have the same channels size. The last channel uses as Alpha channel.
     * </P>
     * 
     * @param baseImage
     *            This image will be changed. <STRONG>This image should have 3 or 4 channels!</STRONG>
     * @param injectPosition
     *            Position where will be injected <CODE>injectImage</CODE> on <CODE>baseImage</CODE>.
     * @param injectImage
     *            This image injected to <CODE>baseImage</CODE> image. <STRONG>This image should have 3 or 4 channels!</STRONG>
     * @return
     *         Image with combination of baseImage and injectImage. That image have same size and type as baseImage and have 3 channels.
     */
    public static Image injectImage(Image baseImage, Point injectPosition, Image injectImage) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(baseImage, "baseImage");
        JCV.verifyIsNotNull(injectPosition, "injectPosition");
        JCV.verifyIsNotNull(injectImage, "injectedImage");
        
        // Verify images.
        if (baseImage.getNumOfChannels() < 3 || baseImage.getNumOfChannels() > 4) {
            throw new IllegalArgumentException("Channel number of 'baseImage' (= " + Integer.toString(baseImage.getNumOfChannels())
                + ") must be 3 or 4!");
        }
        if (injectImage.getNumOfChannels() < 3 || injectImage.getNumOfChannels() > 4) {
            throw new IllegalArgumentException("Channel number of 'injectImage' (= " + Integer.toString(injectImage.getNumOfChannels())
                + ") must be 3 or 4!");
        }
        
        /*
         * Perform operation.
         */
        final Image result = baseImage.copy();
        
        final Image baseImageSub =
            result.getSubimage(new Rectangle(injectPosition.getX(), injectPosition.getY(), Math.min(injectImage.getWidth(),
                baseImage.getWidth() - injectPosition.getX()), Math.min(injectImage.getHeight(),
                baseImage.getHeight() - injectPosition.getY())));
        final Image injectImageSub = injectImage.getSubimage(new Rectangle(0, 0, baseImageSub.getWidth(), baseImageSub.getHeight()));
        
        // Inject images.
        JcvParallel.pixels(baseImageSub, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                for (int channel = 0; channel < 3; ++channel) {
                    double alpha1;
                    if (injectImageSub.getNumOfChannels() == 3) {
                        alpha1 = 1.0;
                    } else {
                        alpha1 = injectImageSub.get(x, y, 3) / Color.COLOR_MAX_VALUE;
                    }
                    
                    double alpha2;
                    if (baseImageSub.getNumOfChannels() == 3) {
                        alpha2 = 1.0;
                    } else {
                        alpha2 = baseImageSub.get(x, y, 3) / Color.COLOR_MAX_VALUE;
                    }
                    
                    double value = alpha1 * injectImageSub.get(x, y, channel) + alpha2 * baseImageSub.get(x, y, channel) * (1.0 - alpha1);
                    if (value > Color.COLOR_MAX_VALUE) {
                        value = Color.COLOR_MAX_VALUE;
                    }
                    
                    baseImageSub.set(x, y, channel, value);
                }
            }
        });
        
        return result;
    }
}
