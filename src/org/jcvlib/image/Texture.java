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

/**
 * Contains methods to <A href="http://en.wikipedia.org/wiki/Image_texture">texture</A> analysis.
 * 
 * @version 1.007
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Texture {
    /**
     * Implements <A href="http://en.wikipedia.org/wiki/Local_binary_patterns">LBP (Local Binary Patterns)</A>. But with different
     * values processing order and another type of result compare coding.
     * 
     * <P>
     * For example, for size <CODE>[5x3]</CODE> (both dimensions should be odd) <STRONG>order of processing</STRONG> will be following:
     * <CODE><PRE>
     * |  1  2  3  4  5 |
     * |  6  7 (x) 8  9 |
     * | 10 11 12 13 14 |
     * </PRE></CODE> Each value compare with center (marked as <CODE>(x)</CODE>) and if value more or equals than center selected
     * <CODE>1</CODE> and <CODE>0</CODE> otherwise. Then this values are coded using <A
     * heref="http://en.wikipedia.org/wiki/Arithmetic_coding"> arithmetic coding</A>.
     * </P>
     * 
     * @param image
     *            Source image for perform analysis.
     * @param patternSize
     *            Size of applying pattern. <STRONG>Should have odd size for both dimensions (1, 3, 5, ...)!</STRONG>
     * @param extrapolationType
     *            Type of extrapolation on image border. Use <CODE>Filters.EXTRAPLOATION_*</CODE> parameters.
     * @return
     *         Image with texture unique float-point value. Have same size and type as a source image, but have 1 channel.
     */
    public static Image doubleLBP(Image image, final Size patternSize, int extrapolationType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyOddSize(patternSize, "patternSize");
        
        /*
         * Perform operation.
         */
        Image result = new Image(image);
        
        Filters.noneLinearFilter(image, result, patternSize, patternSize.getCenter(), 1, extrapolationType, new Operator() {
            @Override
            public Color execute(Image aperture) {
                Color res = new Color(aperture.getNumOfChannels());
                
                for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                    int xCenter = aperture.getSize().getCenter().getX();
                    int yCenter = aperture.getSize().getCenter().getY();
                    
                    double center = aperture.get(xCenter, yCenter, channel);
                    
                    double leftSide = Color.COLOR_MIN_VALUE;
                    double rightSide = Color.COLOR_MAX_VALUE;
                    
                    // For all values from current aperture.
                    for (int x = 0; x < aperture.getWidth(); ++x) {
                        for (int y = 0; y < aperture.getHeight(); ++y) {
                            if (x != xCenter && y != yCenter) {
                                double middle = 0.5 * (rightSide + leftSide);
                                
                                // Threshold.
                                if (aperture.get(x, y, channel) >= center) {
                                    /*
                                     * Current:
                                     * ~~~~~~~~
                                     * middle
                                     * leftSize |--------|--------| rightSide
                                     * 
                                     * New:
                                     * ~~~~
                                     * leftSize
                                     * |--------|--------| rightSide
                                     */
                                    leftSide = middle;
                                } else {
                                    /*
                                     * Current:
                                     * ~~~~~~~~
                                     * middle
                                     * leftSize |--------|--------| rightSide
                                     * 
                                     * New:
                                     * ~~~~
                                     * rightSide
                                     * leftSize |--------|--------|
                                     */
                                    rightSide = middle;
                                }
                            }
                        }
                    }
                    
                    res.set(channel, 0.5 * (rightSide + leftSide));
                }
                
                return res;
            }
        });
        
        return result;
    }
    
    /**
     * Find <A href="http://en.wikipedia.org/wiki/Hamming_distance">Hamming distance</A> between 2 LBP (Local Binary Patterns)
     * represented as float-point values.
     * 
     * <P>
     * This algorithm restore
     * </P>
     * 
     * @param lbp1
     *            First LBP float-point value.
     * @param lbp2
     *            First LBP float-point value.
     * @return
     *         Value from interval [0.0, 1.0].
     */
    public static double hemmingDist(double lbp1, double lbp2, final Size patternSize) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(patternSize, "patternSize");
        
        /*
         * Perform operation.
         */
        int count = 0;
        
        double leftSide1 = Color.COLOR_MIN_VALUE;
        double rightSide1 = Color.COLOR_MAX_VALUE;
        
        double leftSide2 = Color.COLOR_MIN_VALUE;
        double rightSide2 = Color.COLOR_MAX_VALUE;
        
        for (int i = 0; i < patternSize.getN() - 1; ++i) {
            double middle1 = 0.5 * (rightSide1 + leftSide1);
            double middle2 = 0.5 * (rightSide2 + leftSide2);
            
            boolean val1;
            if (lbp1 >= middle1) {
                leftSide1 = middle1;
                val1 = true;
            } else {
                rightSide1 = middle1;
                val1 = false;
            }
            
            boolean val2;
            if (lbp2 >= middle2) {
                leftSide2 = middle2;
                val2 = true;
            } else {
                rightSide2 = middle2;
                val2 = false;
            }
            
            if (val1 != val2) {
                ++count;
            }
        }
        
        return (double) count / patternSize.getN();
    }
}
