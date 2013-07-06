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
package org.jcvlib.core;

import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

/**
 * Main class for all images that used into the JcvLib.
 *
 * <P>
 * This image can have 2 types of content:
 * <UL>
 * <LI>{@link Image#TYPE_8I} -- Integer values, that uses 8-bit for save values from interval <CODE>0..255</CODE>.</LI>
 * <LI>{@link Image#TYPE_64F} -- Float-point values, that uses 64-bit for save values from interval <CODE>[0.0, 255.0]</CODE>.</LI>
 * </UL>
 * </P>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Image {
    /**
     * This image type uses integer values from interval <CODE>0..255</CODE> for saving image pixels and uses 8-bit for each value.
     */
    public static final int TYPE_8I = 0;

    /**
     * This image type uses float-point values from interval <CODE>[0.0, 255.0]</CODE> for saving image pixels and uses 64-bit for each
     * value.
     */
    public static final int TYPE_64F = 1;

    /**
     * Extrapolation type (image boundaries are denoted with '<CODE>|</CODE>'): <CODE>00000|abcdefgh|00000</CODE>.
     */
    public static final int EXTRAPLOATION_ZERO = 0;

    /**
     * Extrapolation type (image boundaries are denoted with '<CODE>|</CODE>'): <CODE>aaaaaa|abcdefgh|hhhhhhh</CODE>.
     */
    public static final int EXTRAPLOATION_REPLICATE = 1;

    /**
     * Extrapolation type (image boundaries are denoted with '<CODE>|</CODE>'): <CODE>fedcba|abcdefgh|hgfedcb</CODE>.
     */
    public static final int EXTRAPLOATION_REFLECT = 2;

    /**
     * Extrapolation type (image boundaries are denoted with '<CODE>|</CODE>'): <CODE>cdefgh|abcdefgh|abcdefg</CODE>.
     */
    public static final int EXTRAPLOATION_WRAP = 4;

    /**
     * Nearest neighbor interpolation.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Nearest-neighbor_interpolation">Nearest neighbor interpolation -- Wikipedia</A>.</LI>
     * <LI><A href="http://www.compuphase.com/graphic/scale.htm">Quick image scaling algorithms</A>.</LI>
     * <LI><A href="http://www.cambridgeincolour.com/tutorials/image-interpolation.htm">Understanding Digital Image Interpolation</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int INTERPOLATION_NEAREST_NEIGHBOR = 0;

    /**
     * Bilinear interpolation.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Bilinear_interpolation">Bilinear interpolation -- Wikipedia</A>.</LI>
     * <LI><A href="http://www.compuphase.com/graphic/scale.htm">Quick image scaling algorithms</A>.</LI>
     * <LI><A href="http://www.cambridgeincolour.com/tutorials/image-interpolation.htm">Understanding Digital Image Interpolation</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int INTERPOLATION_BILINEAR = 1;

    /**
     * Bicubic interpolation.
     *
     * <P>
     * <H6>Links:</H6>
     * <OL>
     * <LI><A href="http://en.wikipedia.org/wiki/Bicubic_interpolation">Bicubic interpolation -- Wikipedia</A>.</LI>
     * <LI><A href="http://www.compuphase.com/graphic/scale.htm">Quick image scaling algorithms</A>.</LI>
     * <LI><A href="http://www.cambridgeincolour.com/tutorials/image-interpolation.htm">Understanding Digital Image Interpolation</A>.</LI>
     * </OL>
     * </P>
     */
    public static final int INTERPOLATION_BICUBIC = 2;

    /**
     * Contains type of internal source.
     */
    protected final int sourceType;

    /**
     * Contains pixel values.
     */
    protected final ImageArray source;

    /**
     * Define sub-image.
     */
    protected final Rectangle currentImage;

    /**
     * Define start position layer from current source.
     */
    protected final int startChannel;

    /**
     * Define size of layer from current source.
     */
    protected final int sizeLayer;

    /**
     * Create new image base on given source.
     */
    private Image(final int type, final ImageArray source, final Rectangle currentImage, final int startChannel, final int sizeLayer) {
        this.sourceType = type;
        this.source = source;
        this.currentImage = currentImage;
        this.startChannel = startChannel;
        this.sizeLayer = sizeLayer;
    }

    /**
     * Create new empty image.
     *
     * @param width
     *            Width of image (in pixels).
     * @param height
     *            Height of image (in pixels).
     * @param numOfChannels
     *            Number of channels in current image.
     * @param type
     *            Type of image. You should use 'Image.TYPE_*' as a parameter!
     */
    public Image(final int width, final int height, final int numOfChannels, final int type) {
        switch (type) {
            case Image.TYPE_8I:
                this.source = new ImageArray8I(width, height, numOfChannels);
                break;

            case Image.TYPE_64F:
                this.source = new ImageArray64F(width, height, numOfChannels);
                break;

            default:
                throw new IllegalArgumentException("Value of 'type' is unknown! Use 'Image.TYPE_*' as a parameter!");
        }
        this.sourceType = type;

        this.currentImage = new Rectangle(new Point(0, 0), this.source.getSize());

        this.startChannel = 0;
        this.sizeLayer = this.source.getNumOfChannels();
    }

    /**
     * Create new image and initialize it by given color.
     *
     * @param width
     *            Width of image (in pixels).
     * @param height
     *            Height of image (in pixels).
     * @param numOfChannels
     *            Number of channels in current image.
     * @param type
     *            Type of image source. You should use <CODE>Image.TYPE_*</CODE> as a parameter!
     * @param initColor
     *            Color to initialize image. Should have same number of channels as current image.
     */
    public Image(final int width, final int height, final int numOfChannels, final int type, final Color initColor) {
        this(width, height, numOfChannels, type);

        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(initColor, "initColor");
        if (initColor.getNumOfChannels() != this.getNumOfChannels()) {
            throw new IllegalArgumentException("Parameter 'initColor' should have same number of channels (= "
                + Integer.toString(this.getNumOfChannels()) + ") as image (= " + Integer.toString(initColor.getNumOfChannels()) + ")!");
        }

        /*
         * Set values.
         */
        Parallel.pixels(this, new PixelsLoop() {
            @Override
            public void execute(int x, int y) {
                for (int channel = 0; channel < getNumOfChannels(); ++channel) {
                    set(x, y, channel, initColor.get(channel));
                }
            }
        });
    }

    /**
     * Create <STRONG>empty</STRONG> image with size, number of channels and source type as in given image.
     *
     * <P>
     * <STRONG>Values from given image will be not copied!</STRONG>
     * </P>
     */
    public Image(final Image image) {
        this(image.getWidth(), image.getHeight(), image.getNumOfChannels(), image.getType());
    }

    /**
     * Return width of image (in pixels).
     */
    public int getWidth() {
        return this.currentImage.getWidth();
    }

    /**
     * Return height of image (in pixels).
     */
    public int getHeight() {
        return this.currentImage.getHeight();
    }

    /**
     * Return size of image (in pixels).
     */
    public Size getSize() {
        return this.currentImage.getSize();
    }

    /**
     * Return number of channels in current image.
     */
    public int getNumOfChannels() {
        return this.sizeLayer;
    }

    /**
     * Return number of values into current image (same as <CODE>getWidth() * getHeight() * getNumOfChannels</CODE>).
     */
    public int getN() {
        return this.getSize().getN() * this.getNumOfChannels();
    }

    /**
     * Return source type of current image.
     */
    public int getType() {
        return this.sourceType;
    }

    /**
     * Verify given point.
     */
    private void verifyPoint(final int x, final int y, final int channel) {
        if (x < 0 || x >= this.getWidth()) {
            throw new IllegalArgumentException("Value of 'x' (= " + Integer.toString(x) + ") " + "must in interval 0.."
                + Integer.toString(this.getWidth() - 1) + "!");
        }

        if (y < 0 || y >= this.getHeight()) {
            throw new IllegalArgumentException("Value of 'y' (= " + Integer.toString(y) + ") " + "must in interval 0.."
                + Integer.toString(this.getHeight() - 1) + "!");
        }

        if (channel < 0 || channel >= this.getNumOfChannels()) {
            throw new IllegalArgumentException("Value of 'channel' (= " + Integer.toString(channel) + ") " + "must in interval 0.."
                + Integer.toString(this.getNumOfChannels() - 1) + "!");
        }
    }

    /**
     * Return float-point value of selected channel from selected pixel.
     */
    public double get(final int x, final int y, final int channel) {
        /*
         * Verify parameters.
         */
        this.verifyPoint(x, y, channel);

        /*
         * Return value.
         */
        return this.source.getUnsafe(this.currentImage.getX() + x, this.currentImage.getY() + y, this.startChannel + channel);
    }

    /**
     * Return integer value in interval <CODE>[0, 255]</CODE> of selected channel from selected pixel.
     */
    public int get8I(final int x, final int y, final int channel) {
        /*
         * Verify parameters.
         */
        this.verifyPoint(x, y, channel);

        /*
         * Return value.
         */
        return this.source.getUnsafe8I(this.currentImage.getX() + x, this.currentImage.getY() + y, this.startChannel + channel);
    }

    /**
     * Set float-point value to selected channel from selected pixel.
     */
    public void set(final int x, final int y, final int channel, double value) {
        /*
         * Verify parameters.
         */
        this.verifyPoint(x, y, channel);
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("Value of color should not be a NaN!");
        }

        /*
         * Set value.
         */
        if (value < Color.COLOR_MIN_VALUE) {
            value = Color.COLOR_MIN_VALUE;
        }
        if (value > Color.COLOR_MAX_VALUE) {
            value = Color.COLOR_MAX_VALUE;
        }
        this.source.setUnsafe(this.currentImage.getX() + x, this.currentImage.getY() + y, this.startChannel + channel, value);
    }

    /**
     * Set integer value in interval <CODE>[0, 255]</CODE> to selected channel from selected pixel.
     */
    public void set8I(final int x, final int y, final int channel, int value) {
        /*
         * Verify parameters.
         */
        this.verifyPoint(x, y, channel);

        /*
         * Set value.
         */
        if (value < Color.COLOR_MIN_VALUE) {
            value = JCV.roundUp(Color.COLOR_MIN_VALUE);
        }
        if (value > Color.COLOR_MAX_VALUE) {
            value = JCV.roundDown(Color.COLOR_MAX_VALUE);
        }
        this.source.setUnsafe8I(this.currentImage.getX() + x, this.currentImage.getY() + y, this.startChannel + channel, value);
    }

    /**
     * Return color from selected pixel.
     */
    public Color get(final Point point) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(point, "point");

        /*
         * Return value.
         */
        Color result = new Color(this.getNumOfChannels());

        for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
            result.set(channel, this.get(point.getX(), point.getY(), channel));
        }

        return result;
    }

    /**
     * Set color to selected pixel.
     */
    public void set(final Point point, final Color color) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(point, "point");
        JCV.verifyIsNotNull(color, "color");

        /*
         * Set value.
         */
        for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
            this.set(point.getX(), point.getY(), channel, color.get(channel));
        }
    }

    /**
     * Translate coordinates to release extrapolation on image borders.
     *
     * @param xy
     *            X or Y values.
     * @param wh
     *            Width or Height of image.
     * @param extrapolationType
     *            Type of extrapolation. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Coordinates that are should be used for extrapolation.
     */
    private int translateCoordinateUnsafe(final int xy, final int wh, final int extrapolationType) {
        int res;
        switch (extrapolationType) {
            case EXTRAPLOATION_REPLICATE:
                if (xy < 0) {
                    res = 0;
                } else
                    if (xy >= wh) {
                        res = wh - 1;
                    } else {
                        res = xy;
                    }
                break;

            case EXTRAPLOATION_REFLECT:
                if (xy < 0) {
                    res = -xy - 1;
                } else
                    if (xy >= wh) {
                        res = wh - (xy - wh) - 1;
                    } else {
                        res = xy;
                    }
                break;

            case EXTRAPLOATION_WRAP:
                if (xy < 0) {
                    res = wh + xy;
                } else
                    if (xy >= wh) {
                        res = xy - wh;
                    } else {
                        res = xy;
                    }
                break;

            default:
                throw new IllegalArgumentException("Unknown 'extrapolationType'!");
        }
        return res;
    }

    /**
     * Extrapolate image uses selected extrapolation type.
     *
     * Uses into filters.
     *
     * @param x
     *            X-position.
     * @param y
     *            Y-position.
     * @param channel
     *            Number of channel.
     * @param extrapolationType
     *            Type of extrapolation. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Values of color from current image using extrapolation. It means, that you can using negative values of <CODE>x</CODE> and
     *         <CODE>y</CODE> or values more than <CODE>width</CODE> and <CODE>height</CODE>.
     */
    public double get(final int x, final int y, final int channel, final int extrapolationType) {
        /*
         * Check bounds.
         */
        if (x < -2 * this.getWidth() || x > 2 * this.getWidth()) {
            throw new IllegalArgumentException("Incorrect value of 'x' (= " + Integer.toString(x) + ") must in interval "
                + Integer.toString(-2 * this.getWidth()) + ".." + Integer.toString(2 * this.getWidth() - 1) + "!");
        }

        if (y < -2 * this.getHeight() || y > 2 * this.getHeight()) {
            throw new IllegalArgumentException("Incorrect value of 'y' (= " + Integer.toString(y) + ") must in interval "
                + Integer.toString(-2 * this.getHeight()) + ".." + Integer.toString(2 * this.getHeight() - 1) + "!");
        }

        /*
         * Generate value.
         */
        if (extrapolationType == EXTRAPLOATION_ZERO) {
            if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) {
                return Color.COLOR_MIN_VALUE;
            } else {
                return this.get(x, y, channel);
            }
        } else {
            return this.get(
                    this.translateCoordinateUnsafe(x, this.getWidth(),  extrapolationType),
                    this.translateCoordinateUnsafe(y, this.getHeight(), extrapolationType),
                    channel
                );
        }
    }

    /**
     * Calculate cubic interpolation.
     *
     * @param p
     *            Number of values. Base on this values we will be calculate interpolation.
     * @param x
     *            Position that we want to interpolate.
     */
    private double cubicInterpolation(final double[] p, final double x) {
        /*
         * (non-Javadoc)
         * See:
         * * http://www.paulinternet.nl/?page=bicubic
         */
        return p[1] + 0.5 * x * (p[2] - p[0] + x * (2.0 * p[0] - 5.0 * p[1] + 4.0 * p[2] - p[3] + x * (3.0 * (p[1] - p[2]) + p[3] - p[0])));
    }

    /**
     * Return nearest values to given position.
     */
    private double[] getNearPos(final int x, final int y, final int channel) {
        return new double[]{
            this.get(x - 1, y, channel, Image.EXTRAPLOATION_REFLECT),
            this.get(x    , y, channel, Image.EXTRAPLOATION_REFLECT),
            this.get(x + 1, y, channel, Image.EXTRAPLOATION_REFLECT),
            this.get(x + 2, y, channel, Image.EXTRAPLOATION_REFLECT)
        };
    }

    /**
     * Interpolate image uses selected interpolation type. Uses into geometry transformations.
     *
     * @param x
     *            X-position.
     * @param y
     *            Y-position.
     * @param channel
     *            Number of channel.
     * @param interpolationType
     *            Type of interpolation. Use <CODE>Geom.INTERPOLATION_*</CODE> parameters.
     * @return
     *         Values of color from current image using interpolation. It means, that you can using fractional
     *         pixel position values of <CODE>x</CODE> and <CODE>y</CODE> result will be interpolate.
     */
    public double get(final double x, final double y, final int channel, final int interpolationType) {
        switch (interpolationType) {
            case Image.INTERPOLATION_NEAREST_NEIGHBOR:
                return this.get(JCV.round(x), JCV.round(y), channel);

            case Image.INTERPOLATION_BILINEAR:
                int minX = JCV.roundDown(x);
                int maxX = JCV.roundUp(x);
                int minY = JCV.roundDown(y);
                int maxY = JCV.roundUp(y);

                double p1 = maxX - x;
                double p2 = maxY - y;
                double p3 = x - minX;
                double p4 = y - minY;

                // Special case on borders of interpolation net.
                if (JCV.equalValues(p2, p4)) {
                    p2 = 0.5;
                    p4 = 0.5;
                }
                if (JCV.equalValues(p1, p3)) {
                    p1 = 0.5;
                    p3 = 0.5;
                }

                double sum = 0.0;
                sum += p2 * p1 * this.get(minX, minY, channel);
                sum += p1 * p4 * this.get(maxX, minY, channel);
                sum += p2 * p3 * this.get(minX, maxY, channel);
                sum += p4 * p3 * this.get(maxX, maxY, channel);

                return sum;

            case Image.INTERPOLATION_BICUBIC:
                int baseX = JCV.roundDown(x);
                int baseY = JCV.roundDown(y);

                double offsetX = x - baseX;
                double offsetY = y - baseY;

                double[] arr = new double[4];
                arr[0] = this.cubicInterpolation(this.getNearPos(baseX, baseY - 1, channel), offsetX);
                arr[1] = this.cubicInterpolation(this.getNearPos(baseX, baseY + 0, channel), offsetX);
                arr[2] = this.cubicInterpolation(this.getNearPos(baseX, baseY + 1, channel), offsetX);
                arr[3] = this.cubicInterpolation(this.getNearPos(baseX, baseY + 2, channel), offsetX);

                return this.cubicInterpolation(arr, offsetY);

            default:
                throw new IllegalArgumentException(
                    "Parameter 'interpolationType' have unknown value! Use 'Geom.INTERPOLATION_*' as a parameters!");
        }
    }

    /**
     * Copy values from current to given image. <STRONG>Given image should have SAME size as current image.</STRONG>
     */
    public void copyTo(final Image target) {
        /*
         * Verify parameters.
         */
        // Check size.
        JCV.verifyIsSameSize(this, "current", target, "target");
        // Check channels.
        if (target.getNumOfChannels() != this.getNumOfChannels()) {
            throw new IllegalArgumentException("Given image should have a same number of cannels (= "
                + Integer.toString(target.getNumOfChannels()) + ") as current image (= " + Integer.toString(this.getNumOfChannels()) + ")!");
        }

        /*
         * Copy values.
         */
        Parallel.pixels(this, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                for (int channel = 0; channel < getNumOfChannels(); ++channel) {
                    target.set(x, y, channel, get(x, y, channel));
                }
            }
        });
    }

    /**
     * Return copy of current image. It will be a <STRONG>REAL COPY</STRONG> of current image or sub-image!
     */
    public Image copy() {
        Image copy = new Image(this);
        this.copyTo(copy);

        return copy;
    }

    /**
     * Return sub-image based on current source. It is <STRONG>NOT COPY</STRONG> current image.
     */
    public Image getSubimage(final int x, final int y, final int width, final int height) {
        return this.getSubimage(new Rectangle(x, y, width, height));
    }

    /**
     * Return sub-image based on current source. It is <STRONG>NOT COPY</STRONG> current image.
     */
    public Image getSubimage(final Rectangle subImageRect) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(subImageRect, "subImageRect");
        if (this.currentImage.getWidth() < subImageRect.getX() + subImageRect.getWidth()
            || this.currentImage.getHeight() < subImageRect.getY() + subImageRect.getHeight()) {
            throw new IllegalArgumentException("Can not create sub-image with defined size!");
        }

        /*
         * Create new object.
         */
        return new Image(this.sourceType, this.source,
            new Rectangle(subImageRect.getX() + this.currentImage.getX(), subImageRect.getY() + this.currentImage.getY(),
                subImageRect.getWidth(), subImageRect.getHeight()), this.startChannel, this.sizeLayer);
    }

    /**
     * Select from multichannel image from current image.
     *
     * <P>
     * Returned image will be have <STRONG>same size</STRONG> but this new image contains only <CODE>sizeLayer</CODE> channels that got from
     * <CODE>startChannel</CODE> to <CODE>startChannel + sizeLayer</CODE>. It is <STRONG>NOT COPY</STRONG> of current image -- this is
     * <STRONG>SAME</STRONG> channels from current image!
     * </P>
     */
    public Image getLayer(final int startChannel, final int sizeLayer) {
        /*
         * Verify parameters.
         */
        if (startChannel < 0) {
            throw new IllegalArgumentException("Value of 'channel' (= " + Integer.toString(startChannel) + ") must in interval 0.."
                + Integer.toString(this.getNumOfChannels() - 1) + "!");
        }
        if (sizeLayer < 1) {
            throw new IllegalArgumentException("Value of 'sizeChannels' (= " + Integer.toString(sizeLayer)
                + ") must more or equals than 1!");
        }
        if (startChannel + sizeLayer > this.getNumOfChannels()) {
            throw new IllegalArgumentException("Value of 'startChannel + sizeChannels' (= " + Integer.toString(startChannel + sizeLayer)
                + ") " + "must be less or equals than " + Integer.toString(this.getNumOfChannels()) + "!");
        }

        /*
         * Create new object.
         */
        return new Image(this.sourceType, this.source, this.currentImage, startChannel, sizeLayer);
    }

    /**
     * Select single channel image based on channel with number <CODE>channelNumber</CODE> from current multichannel image.
     *
     * <P>
     * This is analog of <CODE>split</CODE> function from other computer vision libraries. You can get access to channels as an elements of
     * a list. It is <STRONG>NOT COPY</STRONG> current image -- this is <STRONG>SAME</STRONG> channel from image!
     * </P>
     */
    public Image getChannel(final int numChannel) {
        return this.getLayer(numChannel, 1);
    }

    /**
     * Return <CODE>true</CODE> if current image equivalent to object from parameter and <CODE>false</CODE> otherwise.
     *
     * <P>
     * Uses {@link JCV#PRECISION_MAX} by default.
     * </P>
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        return this.equals(object, JCV.PRECISION_MAX);
    }

    /**
     * Return <CODE>true</CODE> if current image equivalent to object from parameter and <CODE>false</CODE> otherwise.
     *
     * @param object
     *            Object for compare with current image.
     * @param precision
     *            Precision for compare values of pixels.
     */
    public boolean equals(final Object object, final double precision) {
        if (object == null) {
            return false;
        }

        Image image = null;

        if (object instanceof Image) {
            image = (Image) object;
        } else {
            return false;
        }

        if (image == this) {
            return true;
        }

        // Compare size.
        if (!this.getSize().equals(image.getSize())) {
            return false;
        }
        if (this.getNumOfChannels() != image.getNumOfChannels()) {
            return false;
        }

        // Compare values.
        for (int x = 0; x < this.getWidth(); ++x) {
            for (int y = 0; y < this.getHeight(); ++y) {
                for (int channel = 0; channel < this.getNumOfChannels(); ++channel) {
                    if (!JCV.equalValues(this.get(x, y, channel), image.get(x, y, channel), precision)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Return string with some internal information about this channel.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Image:");
        sb.append("\n");

        sb.append("    Hash:            ");
        sb.append(this.hashCode());
        sb.append("\n");

        sb.append("    Source type:     ");
        sb.append(this.sourceType == Image.TYPE_8I ? "8-bit integer" : "64-bit float-point");
        sb.append("\n");

        sb.append("    Source size:     ");
        sb.append(this.source.getSize().toString());
        sb.append("\n");

        sb.append("    Current image:   ");
        sb.append(this.currentImage.toString());
        sb.append("\n");

        sb.append("    Start channel:   ");
        sb.append(this.startChannel);
        sb.append("\n");

        sb.append("    Size layer:      ");
        sb.append(this.sizeLayer);
        sb.append("\n");

        return sb.toString();
    }
}
