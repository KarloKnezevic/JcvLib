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

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.core.Size;
import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

/**
 * This class contains algorithms for <A href="http://en.wikipedia.org/wiki/Computer_stereo_vision">stereo vision</A>.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Stereo {
    /**
     * Create map of distance base on 2 images.
     *
     * @param left
     *            Left image.
     * @param right
     *            Right image.
     * @param windowSize
     *            Size of image for detect same pixels.
     * @return
     *         Map of distance.
     */
    public static Image getMap(final Image left, final Image right, final Size windowSize) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(left, "left", right, "right");
        JCV.verifyIsSameChannels(left, "left", right, "right");
        JCV.verifyIsSameType(left, "left", right, "right");

        JCV.verifyIsNotNull(windowSize, "window");
        if (windowSize.getWidth() % 2 != 1 || windowSize.getHeight() % 2 != 1) {
            throw new IllegalArgumentException("Parameter 'window' should have odd size!");
        }

        /*
         * Perform transformation.
         */
        //final int widthShift = JCV.round((windowSize.getWidth() - 1) / 2);
        final int widthShift = windowSize.getWidth() - 1;
        //final int widthShift = JCV.round((windowSize.getWidth() - 1) / 4);
        final int heightShift = JCV.round((windowSize.getHeight() - 1) / 2);
        final double maxDist = Math.sqrt(widthShift * widthShift + heightShift * heightShift);
        final Image result =
            new Image(left.getWidth() - windowSize.getWidth() + 1, left.getHeight() - windowSize.getHeight() + 1, 1, left.getType());

        Parallel.pixels(result, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                double minColor = Double.MAX_VALUE;
                double minDist = Double.MAX_VALUE;

                final Color leftColor = left.get(new Point(x + widthShift, y + heightShift));
                for (int cx = 0; cx < windowSize.getWidth(); ++cx) {
                    for (int cy = 0; cy < windowSize.getHeight(); ++cy) {
                        final int rx = x + cx;
                        final int ry = y + cy;

                        final Color rightColor = right.get(new Point(rx, ry));

                        double distColor = leftColor.euclidDist(rightColor);
                        if (distColor < minColor) {
                            minColor = distColor;
                            minDist = Math.sqrt((rx - x) * (rx - x) + (ry - y) * (ry - y));
                        }
                    }
                }

                result.set(x, y, 0, (1.0 - minDist / maxDist) * Color.COLOR_MAX_VALUE);
            }
        });

        return result;
    }

    /**
     * Create map of distance base on 2 images.
     *
     * <P>
     * Uses 10 % (percent) of image width by default window size.
     * </P>
     *
     * @param left
     *            Left image.
     * @param right
     *            Right image.
     * @return
     *         Map of distance.
     */
    public static Image getMap(final Image left, final Image right) {
        final double windowCoeff = 0.10; // 10 %
        return Stereo.getMap(left, right, new Size(2 * JCV.round(left.getWidth() * windowCoeff / 2.0) + 1, 1));
    }
}
