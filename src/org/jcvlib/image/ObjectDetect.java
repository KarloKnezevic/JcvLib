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
import org.jcvlib.core.Point;

/**
 * Detect objects on given image.
 *
 * @version 1.010
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ObjectDetect {
    /**
     * Match template using <A href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean distance</A> between template and sub-image.
     *
     * <P>
     * <STRONG>Attention! Extremely slow method! Use small images (resize big images) or another methods!</STRONG>
     * </P>
     *
     * @param image
     *            Source image where we try to find template.
     * @param template
     *            Image with object that we want to find.
     * @return
     *         Result image where we will be save result. <STRONG>Should have same size and 1 channel!</STRONG> This image contains
     *         average Euclidean distance in normalize form (all values between {@link Color#COLOR_MIN_VALUE} and
     *         {@link Color#COLOR_MAX_VALUE}) between template and all sub-images.
     */
    /*
     * (non-Javadoc)
     * See:
     * * http://docs.opencv.org/modules/imgproc/doc/object_detection.html#matchtemplate
     * * http://docs.opencv.org/2.4.2/doc/tutorials/imgproc/histograms/template_matching/template_matching.html
     */
    public static Image matchTempleteEuclid(Image image, final Image template) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(template, "template");

        /*
         * Perform operation.
         */
        final Image result = new Image(image.getWidth(), image.getHeight(), 1, Image.TYPE_64F);

        Filters.noneLinearFilter(image, result, template.getSize(), new Point(0, 0), 1, Image.EXTRAPLOATION_ZERO, new Operator() {
            @Override
            public Color execute(Image aperture) {
                double result = 0.0;
                for (int x = 0; x < aperture.getWidth(); ++x) {
                    for (int y = 0; y < aperture.getHeight(); ++y) {
                        Point p = new Point(x, y);
                        result += aperture.get(p).euclidDist(template.get(p));
                    }
                }

                return new Color(new double[]{ Color.COLOR_MAX_VALUE - result / aperture.getSize().getN() });
            }
        });

        return result;
    }

    /**
     * Match template using <A href="http://en.wikipedia.org/wiki/Image_histogram">image histogram</A>
     * between template and sub-image.
     *
     * <P>
     * <STRONG>Attention! Extremely slow method! Use small images (resize big images) or another methods!</STRONG>
     * </P>
     *
     * @param image
     *            Source image where we try to find template.
     * @param template
     *            Image with object that we want to find.
     * @param compareType
     *            Type of compare histograms. Use <CODE>Hist.HISTOGRAM_COMPARE_*</CODE>.
     * @return
     *         Result image where we will be save result. <STRONG>Should have same size and 1 channel!</STRONG> This image contains
     *         distance in normalize form (all values between {@link Color#COLOR_MIN_VALUE} and {@link Color#COLOR_MAX_VALUE})
     *         between template and all sub-images.
     */
    /*
     * (non-Javadoc)
     * See:
     * * http://docs.opencv.org/modules/imgproc/doc/object_detection.html#matchtemplate
     */
    public static Image matchTempleteHist(Image image, final Image template, final int compareType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(template, "template");

        /*
         * Perform operation.
         */
        double scale = 1.0;
        double offset = 0.0;

        final Hist templateHist = new Hist(template);

        switch (compareType) {
            case Hist.HISTOGRAM_COMPARE_CORREL:
                scale = 0.5 * Color.COLOR_MAX_VALUE;
                offset = 0.5 * Color.COLOR_MAX_VALUE;
                break;

            case Hist.HISTOGRAM_COMPARE_CHISQR:
                scale = -1.0 * Color.COLOR_MAX_VALUE;
                offset = Color.COLOR_MAX_VALUE;
                break;

            case Hist.HISTOGRAM_COMPARE_INTERSECT:
                scale = Color.COLOR_MAX_VALUE;
                offset = 0.0;
                break;

            case Hist.HISTOGRAM_COMPARE_BHATTACHARYYA:
                scale = -1.0 * Color.COLOR_MAX_VALUE;
                offset = Color.COLOR_MAX_VALUE;
                break;

            default:
                throw new IllegalArgumentException("Unknown compare type! Use 'JCV.HISTOGRAM_COMPARE_*' values!");
        }

        final double proxyScale = scale;
        final double proxyOffset = offset;

        final Image result = new Image(image.getWidth(), image.getHeight(), 1, Image.TYPE_64F);

        Filters.noneLinearFilter(image, result, template.getSize(), new Point(0, 0), 1, Image.EXTRAPLOATION_ZERO, new Operator() {
            @Override
            public Color execute(Image aperture) {
                return new Color(new double[]{ proxyScale * templateHist.compare(new Hist(aperture), compareType)
                    + proxyOffset });
            }
        });

        return result;
    }
}
