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
import java.util.LinkedList;
import java.util.List;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.core.Rectangle;
import org.jcvlib.core.Size;

import org.jcvlib.parallel.ChannelsLoop;
import org.jcvlib.parallel.Parallel;

/**
 * Contains miscellaneous image processing methods.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Misc {
    /**
     * Define 4 directions type (for more details see <A href="http://en.wikipedia.org/wiki/Flood_fill">Flood fill
     * algorithm description on Wikipedia</A>).
     */
    public static final int DIRECTIONS_TYPE_4 = 4;

    /**
     * Define 8 directions type (for more details see <A href="http://en.wikipedia.org/wiki/Flood_fill">Flood fill
     * algorithm description on Wikipedia</A>).
     */
    public static final int DIRECTIONS_TYPE_8 = 8;

    /**
     * Define fixed difference between pixels in {@link Misc#floodFill(Image, Point, double, Color, int, int)}.
     */
    public static final int FLOOD_FILL_RANGE_FIXED = 0;

    /**
     * Define float and depending from neighbor difference between pixels in {@link Misc#floodFill(Image, Point, double, Color, int, int)}.
     */
    public static final int FLOOD_FILL_RANGE_NEIGHBOR = 1;

    /**
     * <A href="http://en.wikipedia.org/wiki/Flood_fill">Flood fill algorithm</A> for fill some region.
     *
     * <P>
     * This region is detected by start position and distance between color of start point and neighbors of this point.
     * </P>
     *
     * @param image
     *            Source image.
     * @param seed
     *            Start point.
     * @param distance
     *            Distance between start color and color all others neighbor points. Uses for compare
     *            <A href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean distance</A>
     *            between two pixels. Maximum value for image with 3 channels is <CODE>442</CODE>.
     * @param fillColor
     *            Fill color value.
     * @param directionType
     *            Type of direction: 4 or 8 (for more details see <A href="http://en.wikipedia.org/wiki/Flood_fill">Flood fill
     *            algorithm description on Wikipedia</A>). See <CODE>Misc.DIRECTIONS_TYPE_*</CODE>.
     * @param rangeType
     *            Define how to calculate difference: calculate difference between the current pixel and seed pixel or difference
     *            between neighbor pixels (i.e. the range is floating).
     * @return
     *         Number of filled pixels.
     */
    public static FloodFillStruct floodFill(final Image image, final Point seed, final double distance, final Color fillColor,
        final int directionType, final int rangeType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(seed, "seed");
        JCV.verifyIsNotNull(fillColor, "fillColor");

        /*
         * Perform operation.
         */
        ArrayList<Point> pointList = new ArrayList<Point>();
        pointList.add(seed);

        Color seedColor = image.get(seed);

        ArrayList<Color> colorList = new ArrayList<Color>();
        colorList.add(image.get(seed));

        // Statistic values.
        int totalFillPixels = 0;

        int leftSide = seed.getX();
        int rigthSide = seed.getX();

        int topSide = seed.getY();
        int bottomSide = seed.getY();

        int centerOfGravityX = seed.getX();
        int centerOfGravityY = seed.getY();

        // Main loop.
        while (!pointList.isEmpty() && !colorList.isEmpty()) {
            Point point = pointList.get(0);
            pointList.remove(0);
            Color color = colorList.get(0);
            colorList.remove(0);

            // Add neighbors.
            ArrayList<Point> neighbors = new ArrayList<Point>();
            switch (directionType) {
                case Misc.DIRECTIONS_TYPE_8:
                    if (point.getX() > 0 && point.getY() > 0) {
                        // (x - 1, y - 1)
                        neighbors.add(new Point(point.getX() - 1, point.getY() - 1));
                    }
                    if (point.getX() > 0 && point.getY() < image.getHeight() - 1) {
                        // (x - 1, y + 1)
                        neighbors.add(new Point(point.getX() - 1, point.getY() + 1));
                    }
                    if (point.getX() < image.getWidth() - 1 && point.getY() > 0) {
                        // (x + 1, y - 1)
                        neighbors.add(new Point(point.getX() + 1, point.getY() - 1));
                    }
                    if (point.getX() < image.getWidth() - 1 && point.getY() < image.getHeight() - 1) {
                        // (x + 1, y + 1)
                        neighbors.add(new Point(point.getX() + 1, point.getY() + 1));
                    }

                    // And add all neighbors from type 4.
                case Misc.DIRECTIONS_TYPE_4:
                    if (point.getX() > 0) {
                        // (x - 1, y)
                        neighbors.add(new Point(point.getX() - 1, point.getY()));
                    }
                    if (point.getX() < image.getWidth() - 1) {
                        // (x + 1, y)
                        neighbors.add(new Point(point.getX() + 1, point.getY()));
                    }
                    if (point.getY() > 0) {
                        // (x, y - 1)
                        neighbors.add(new Point(point.getX(), point.getY() - 1));
                    }
                    if (point.getY() < image.getHeight() - 1) {
                        // (x, y + 1)
                        neighbors.add(new Point(point.getX(), point.getY() + 1));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown direction type " + Integer.toString(directionType)
                        + "! Use 'Misc.DIRECTIONS_TYPE_*' values!");
            }

            // Verify neighbors.
            Color sourceColor;
            switch (rangeType) {
                case Misc.FLOOD_FILL_RANGE_FIXED:
                    sourceColor = seedColor;
                    break;

                case Misc.FLOOD_FILL_RANGE_NEIGHBOR:
                    sourceColor = color;
                    break;

                default:
                    throw new IllegalArgumentException("Unknown range type " + Integer.toString(rangeType)
                        + "! Use 'Misc.FLOOD_FILL_RANGE_*' values!");
            }

            // Add or not add found pixel into filled pixels and calculate statistics.
            for (Point p : neighbors) {
                if (!image.get(p).equals(fillColor) && sourceColor.euclidDist(image.get(p)) <= distance) {
                    pointList.add(p);
                    colorList.add(image.get(p));

                    image.set(p, fillColor);

                    // Calculate statistics.
                    ++totalFillPixels;

                    if (p.getX() < leftSide) {
                        leftSide = p.getX();
                    }
                    if (p.getX() > rigthSide) {
                        rigthSide = p.getX();
                    }
                    if (p.getY() < topSide) {
                        topSide = p.getY();
                    }
                    if (p.getY() > bottomSide) {
                        bottomSide = p.getY();
                    }

                    centerOfGravityX += p.getX();
                    centerOfGravityY += p.getY();
                }
            }
        }

        return new FloodFillStruct(totalFillPixels, new Rectangle(leftSide, topSide, rigthSide - leftSide + 1, bottomSide - topSide + 1),
            new Point(JCV.round((double) centerOfGravityX / (double) totalFillPixels), JCV.round((double) centerOfGravityY
                / (double) totalFillPixels)));
    }

    /**
     * <A href="http://en.wikipedia.org/wiki/Flood_fill">Flood fill algorithm</A> for fill some region.
     *
     * <P>
     * This region is detected by start position and distance between color of start point and neighbors of this point. As default direction
     * type uses {@link Misc#DIRECTIONS_TYPE_8}, as range type uses {@link Misc#FLOOD_FILL_RANGE_FIXED}.
     * </P>
     *
     * @param image
     *            Source image.
     * @param seed
     *            Start point.
     * @param distance
     *            Distance between start color and color all others neighbor points. Uses for compare
     *            <A href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean distance</A> between
     *            two pixels.
     * @param fillColor
     *            Fill color value.
     * @return
     *         Number of filled pixels.
     */
    public static FloodFillStruct floodFill(final Image image, final Point seed, final double distance, final Color fillColor) {
        return Misc.floodFill(image, seed, distance, fillColor, Misc.DIRECTIONS_TYPE_8, Misc.FLOOD_FILL_RANGE_FIXED);
    }

    /**
     * Calculate <A href="http://en.wikipedia.org/wiki/Summed_area_table">Integral image (summed area table)</A>.
     *
     * <P>
     * To get real sum of values you need multiply getting value to <STRONG>number of pixels into image!</STRONG> For example:
     *
     * <PRE>
     * <CODE>
     * // Image image = ...
     * Image intImg = Filters.integralImage(image);
     * ...
     * // int x = ...
     * // int y = ...
     * // int channel = ...
     * double sumVal = intImg.get(x, y, channel) * intImg.getSize().getN();
     * </CODE>
     * </PRE>
     *
     * </P>
     */
    public static Image integralImage(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");

        /*
         * Perform operation.
         */
        final Image result = new Image(image.getWidth(), image.getHeight(), image.getNumOfChannels(), Image.TYPE_64F);

        Parallel.channels(image, new ChannelsLoop() {
            @Override
            public void execute(final int channel) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    for (int y = 0; y < image.getHeight(); ++y) {
                        double left = Color.COLOR_MIN_VALUE;
                        if (x > 0) {
                            left = result.get(x - 1, y, channel);
                        }

                        double top = Color.COLOR_MIN_VALUE;
                        if (y > 0) {
                            top = result.get(x, y - 1, channel);
                        }

                        double topLeft = Color.COLOR_MIN_VALUE;
                        if (x > 0 && y > 0) {
                            topLeft = result.get(x - 1, y - 1, channel);
                        }

                        result.set(x, y, channel, left + top - topLeft + image.get(x, y, channel) / (double) image.getSize().getN());
                    }
                }
            }
        });

        return result;
    }

    /**
     * Constructs the <A href="http://en.wikipedia.org/wiki/Gaussian_pyramid">Gaussian pyramid</A> for an image.
     *
     * @param image
     *            Source image.
     * @param minSize
     *            Minimum image size into created pyramid.
     * @return
     *         Pyramid of images.
     */
    public static List<Image> buildPyramid(final Image image, final Size minSize) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(minSize, "minSize");

        /*
         * Perform operation.
         */
        final List<Image> result = new LinkedList<Image>();

        Image current = image;
        while ((current.getWidth() > minSize.getWidth()) || (current.getHeight() > minSize.getHeight())) {
            current = Geom.scale(current, 0.5);
            result.add(current);
        }

        return result;
    }

    /**
     * Constructs the <A href="http://en.wikipedia.org/wiki/Gaussian_pyramid">Gaussian pyramid</A> for an image.
     *
     * <P>
     * Use size <CODE>[8x8]</CODE> as default minimal image size value.
     * </P>
     *
     * @param image
     *            Source image.
     * @return
     *         Pyramid of images.
     */
    public static List<Image> buildPyramid(final Image image) {
        return Misc.buildPyramid(image, new Size(8, 8));
    }
}
