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

import org.jcvlib.parallel.JcvParallel;
import org.jcvlib.parallel.PixelsLoop;

/**
 * Contains methods to convert images from one color scheme to another.
 *
 * <P>
 * Supported following color schemas:
 * <UL>
 * <LI>
 * <A href="http://en.wikipedia.org/wiki/Grayscale">Grayscale</A> <CODE><-></CODE> <A href="http://en.wikipedia.org/wiki/RGB">RGB</A>.</LI>
 * <LI>
 * <A href="http://en.wikipedia.org/wiki/HSL_and_HSV">HSL/HLS/HSI</A> <CODE><-></CODE> <A href="http://en.wikipedia.org/wiki/RGB">RGB</A>.</LI>
 * <LI>
 * <A href="http://en.wikipedia.org/wiki/HSL_and_HSV">HSV/HSB</A> <CODE><-></CODE> <A href="http://en.wikipedia.org/wiki/RGB">RGB</A>.</LI>
 * </UL>
 * </P>
 *
 * @version 1.030
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ColorConvert {
    /**
     * Convert image from <A href="http://en.wikipedia.org/wiki/RGB">RGB</A> color scheme
     * (this color scheme include Red, Green and Blue channels) to
     * <A href="http://en.wikipedia.org/wiki/Grayscale">Grayscale</A> color scheme
     * (this color scheme include only Gray color channel).
     *
     * @param image
     *            Image with RGB color scheme. This image should have 3 channels.
     * @return
     *         Image with Grayscale color scheme. This image have 1 channel.
     */
    public static Image fromRGBtoGray(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyNumOfChannels(image, "image", 3);

        /*
         * Perform transformation.
         */
        final Image result = new Image(image.getWidth(), image.getHeight(), 1, image.getType());

        JcvParallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                double gray =
                    // Red
                    0.299 * image.get(x, y, 0) +
                    // Green
                    0.587 * image.get(x, y, 1) +
                    // Blue
                    0.114 * image.get(x, y, 2);

                result.set(x, y, 0, gray);
            }
        });

        return result;
    }

    /**
     * Convert image from <A href="http://en.wikipedia.org/wiki/Grayscale">Grayscale</A> color scheme
     * (this color scheme include only Gray color channel) to
     * <A href="http://en.wikipedia.org/wiki/RGB">RGB</A> color scheme
     * (this color scheme include Red, Green and Blue channels).
     *
     * @param image
     *            Image with Grayscale color scheme. This image should have 1 channel.
     * @return
     *         Image with RGB color scheme. This image have 3 channels.
     */
    public static Image fromGrayToRGB(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyNumOfChannels(image, "image", 1);

        /*
         * Perform transformation.
         */
        final Image result = new Image(image);

        for (int channel = 0; channel < result.getNumOfChannels(); ++channel) {
            final int proxyChannel = channel;
            JcvParallel.pixels(image, new PixelsLoop() {
                @Override
                public void execute(int x, int y) {
                    result.set(x, y, proxyChannel, image.get(x, y, 0));
                }
            });
        }

        return result;
    }

    /**
     * Convert image from <A href="http://en.wikipedia.org/wiki/RGB">RGB</A> color scheme
     * (this color scheme include Red, Green and Blue channels) to
     * <A href="http://en.wikipedia.org/wiki/HSL_and_HSV">HSL</A> (same as HLS/HSI) color scheme
     * (this color scheme include Hue, Saturation and Lightness/Intensity channels).
     *
     * <P>
     * Hue change values from interval [0.0, 360.0] of degrees, but translated to interval [0.0, 255.0]. Saturation and Lightness change
     * values from interval [0.0, 1.0], but translated to interval [0.0, 255.0] too.
     * </P>
     *
     * @param image
     *            Image with RGB color scheme. This image should have 3 channels.
     * @return
     *         Image with HSL color scheme. This image have 3 channels.
     */
    public static Image fromRGBtoHSL(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyNumOfChannels(image, "image", 3);

        /*
         * Perform transformation.
         */
        final Image result = new Image(image);

        JcvParallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                // Initialization.
                double R = image.get(x, y, 0);
                double G = image.get(x, y, 1);
                double B = image.get(x, y, 2);

                // Convert.
                double M = Math.max(R, Math.max(G, B));
                double m = Math.min(R, Math.min(G, B));
                double d = M - m;

                // Compute value Lightness.
                double L = (M + m) / 2.0;

                // Compute value Saturation.
                double S;
                if (d == 0) {
                    S = 0.0;
                } else {
                    double t;
                    if (L <= Color.COLOR_MAX_VALUE / 2.0) {
                        t = L;
                    } else {
                        t = Color.COLOR_MAX_VALUE - L;
                    }

                    S = (d * Color.COLOR_MAX_VALUE) / (2.0 * t);
                }

                // Compute value Hue.
                double H;
                if (M > 0.0 && d > 0.0) {
                    double r = (M - R) / d;
                    double g = (M - G) / d;
                    double b = (M - B) / d;

                    double h;
                    if (R == M) {
                        h = b - g;
                    } else
                        if (G == M) {
                            h = r - b + 2.0;
                        } else {
                            h = g - r + 4.0;
                        }
                    if (h < 0.0) {
                        h += 6.0;
                    }
                    H = h / 6.0;
                } else {
                    H = 0.0;
                }
                H *= Color.COLOR_MAX_VALUE;

                // Save result.
                result.set(x, y, 0, H);
                result.set(x, y, 1, S);
                result.set(x, y, 2, L);
            }
        });

        return result;
    }

    /**
     * Convert image from <A href="http://en.wikipedia.org/wiki/HSL_and_HSV">HSL</A> (same as HLS/HSI) color scheme
     * (this color scheme include Hue, Saturation and Lightness/Intensity channels) to
     * <A href="http://en.wikipedia.org/wiki/RGB">RGB</A> color scheme
     * (this color scheme include Red, Green and Blue channels).
     *
     * <P>
     * Hue change values from interval [0.0, 360.0] of degrees, but translated to interval [0.0, 255.0]. Saturation and Lightness change
     * values from interval [0.0, 1.0], but translated to interval [0.0, 255.0] too.
     * </P>
     *
     * @param image
     *            Image with HSL color scheme. This image should have 3 channels.
     * @return
     *         Image with RGB color scheme. This image have 3 channels.
     */
    public static Image fromHSLtoRGB(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyNumOfChannels(image, "image", 3);

        /*
         * Perform transformation.
         */
        final Image result = new Image(image);

        JcvParallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                // Initialization.
                double H = image.get(x, y, 0);
                double S = image.get(x, y, 1);
                double L = image.get(x, y, 2);

                // Convert.
                double h = (6.0 * (H / Color.COLOR_MAX_VALUE)) % 6.0;
                int c1 = (int) h;
                double c2 = h - c1;

                double d;
                S /= Color.COLOR_MAX_VALUE;
                if (L <= Color.COLOR_MAX_VALUE / 2.0) {
                    d = S * L;
                } else {
                    d = S * (Color.COLOR_MAX_VALUE - L);
                }

                double W = L + d;
                double X = L - d;
                double Y = W - (W - X) * c2;
                double Z = X + (W - X) * c2;

                double R;
                double G;
                double B;
                switch (c1) {
                    case 0:
                        R = W; G = Z; B = X; break;
                    case 1:
                        R = Y; G = W; B = X; break;
                    case 2:
                        R = X; G = W; B = Z; break;
                    case 3:
                        R = X; G = Y; B = W; break;
                    case 4:
                        R = Z; G = X; B = W; break;
                    case 5:
                        R = W; G = X; B = Y; break;
                    default:
                        R = 0.0; G = 0.0; B = 0.0;
                }

                // Save result.
                result.set(x, y, 0, R);
                result.set(x, y, 1, G);
                result.set(x, y, 2, B);
            }
        });

        return result;
    }

    /**
     * Convert image from <A href="http://en.wikipedia.org/wiki/RGB">RGB</A> color scheme
     * (this color scheme include Red, Green and Blue channels) to
     * <A href="http://en.wikipedia.org/wiki/HSL_and_HSV">HSV</A> (same as HSB) color scheme
     * (this color scheme include Hue, Saturation, Value (Brightness) or Intensity channel).
     *
     * <P>
     * Hue change values from interval [0.0, 360.0] of degrees, but translated to interval [0.0, 255.0]. Saturation and Value change values
     * from interval [0.0, 1.0], but translated to interval [0.0, 255.0] too.
     * </P>
     *
     * @param image
     *            Image with RGB color scheme. This image should have 3 channels.
     * @return
     *         Image with HSV color scheme. This image have 3 channels.
     */
    public static Image fromRGBtoHSV(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyNumOfChannels(image, "image", 3);

        /*
         * Perform transformation.
         */
        final Image result = new Image(image);

        JcvParallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                // Initialization.
                double R = image.get(x, y, 0);
                double G = image.get(x, y, 1);
                double B = image.get(x, y, 2);

                // Convert.
                double M = Math.max(R, Math.max(G, B));
                double m = Math.min(R, Math.min(G, B));
                double d = M - m;

                // Compute value Hue.
                double H;
                if (d > 0.0) {
                    if (R == M) {
                        H = (G - B) / d;
                    } else
                        if (G == M) {
                            H = 2.0 + (B - R) / d;
                        } else {
                            H = 4.0 + (R - G) / d;
                        }

                    if (H < 0) {
                        H = H + 6.0;
                    }
                    H = H / 6.0;
                } else {
                    // H = Double.NaN;
                    H = 0.0;
                }
                H *= Color.COLOR_MAX_VALUE;

                // Compute value Saturation.
                double S;
                if (M > 0.0) {
                    S = d / M;
                } else {
                    S = 0.0;
                }
                S *= Color.COLOR_MAX_VALUE;

                // Compute value Value (Brightness).
                double V = M;

                // Save result.
                result.set(x, y, 0, H);
                result.set(x, y, 1, S);
                result.set(x, y, 2, V);
            }
        });

        return result;
    }

    /**
     * Convert image from <A href="http://en.wikipedia.org/wiki/HSL_and_HSV">HSV</A> (same as HSB) color scheme
     * (this color scheme include Hue, Saturation, Value (Brightness) channel) to
     * <A href="http://en.wikipedia.org/wiki/RGB">RGB</A> color scheme
     * (this color scheme include Red, Green and Blue channels).
     *
     * <P>
     * Hue change values from interval [0.0, 360.0] of degrees, but translated to interval [0.0, 255.0]. Saturation and Value change values
     * from interval [0.0, 1.0], but translated to interval [0.0, 255.0] too.
     * </P>
     *
     * @param image
     *            Image with HSV color scheme. This image should have 3 channels.
     * @return
     *         Image with RGB color scheme. This image have 3 channels.
     */
    public static Image fromHSVtoRGB(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyNumOfChannels(image, "image", 3);

        /*
         * Perform transformation.
         */
        final Image result = new Image(image);

        JcvParallel.pixels(image, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                // Initialization.
                double H = image.get(x, y, 0);
                double S = image.get(x, y, 1);
                double V = image.get(x, y, 2);

                // Convert.
                double h = ((6.0 * H) / Color.COLOR_MAX_VALUE) % 6.0;
                int c1 = (int) h;
                double c2 = h - c1;

                double X = ((Color.COLOR_MAX_VALUE - S) * V) / Color.COLOR_MAX_VALUE;
                double Y = ((Color.COLOR_MAX_VALUE - (S * c2)) * V) / Color.COLOR_MAX_VALUE;
                double Z = ((Color.COLOR_MAX_VALUE - (S * (1.0 - c2))) * V) / Color.COLOR_MAX_VALUE;

                double R;
                double G;
                double B;
                switch (c1) {
                    case 0:
                        R = V; G = Z; B = X; break;
                    case 1:
                        R = Y; G = V; B = X; break;
                    case 2:
                        R = X; G = V; B = Z; break;
                    case 3:
                        R = X; G = Y; B = V; break;
                    case 4:
                        R = Z; G = X; B = V; break;
                    case 5:
                        R = V; G = X; B = Y; break;
                    default:
                        R = 0.0; G = 0.0; B = 0.0;
                }

                // Save result.
                result.set(x, y, 0, R);
                result.set(x, y, 1, G);
                result.set(x, y, 2, B);
            }
        });

        return result;
    }
}
