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
package org.jcvlib.test.imageproc;

import static org.junit.Assert.*;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.imageproc.ColorConvert;

import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link ColorConvert} to convert color schemas.
 *
 * @version 1.026
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ColorConvertTest {

    private int width;

    private int height;

    private int numOfChannels;

    private Image imageRGB;

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() throws Exception {
        this.width = 5;
        this.height = 4;

        // Returned image should be contain only 3 channels.
        this.numOfChannels = 3;

        // Create image.
        this.imageRGB = new Image(this.width, this.height, this.numOfChannels, Image.TYPE_8I);

        // Initialize image.
        int value = JCV.round(Color.COLOR_MIN_VALUE);
        for (int x = 0; x < imageRGB.getWidth(); ++x) {
            for (int y = 0; y < imageRGB.getHeight(); ++y) {
                for (int channel = 0; channel < imageRGB.getNumOfChannels(); ++channel) {
                    imageRGB.set8I(x, y, channel, value);

                    ++value;
                    if (value > Color.COLOR_MAX_VALUE) {
                        value = JCV.round(Color.COLOR_MIN_VALUE);
                    }
                }
            }
        }
    }

    /**
     * Test method for: {@link ColorConvert#fromRGBtoGray(Image)}, {@link ColorConvert#fromGrayToRGB(Image)}.
     */
    @Test
    public void testRGBtoGrayToRGB() {
        Image imgGray = ColorConvert.fromRGBtoGray(this.imageRGB);
        assertFalse(imgGray.equals(this.imageRGB, 0.5));

        Image imgRGB = ColorConvert.fromGrayToRGB(imgGray);
        for (int channel = 0; channel < imgRGB.getNumOfChannels(); ++channel) {
            assertTrue(imgRGB.getChannel(channel).equals(imgGray));
        }
    }

    /**
     * Test method for: {@link ColorConvert#fromRGBtoHSL(Image)}, {@link ColorConvert#fromHSLtoRGB(Image)}.
     */
    @Test
    public void testRGBtoHSLtoRGB() {
        Image imgHLS = ColorConvert.fromRGBtoHSL(this.imageRGB);
        assertFalse(imgHLS.equals(this.imageRGB, 0.5));

        Image imgRGB = ColorConvert.fromHSLtoRGB(imgHLS);
        assertTrue(imgRGB.equals(this.imageRGB, 0.01));
    }

    /**
     * Test method for: {@link ColorConvert#fromRGBtoHSV(Image)}, {@link ColorConvert#fromHSVtoRGB(Image)}.
     */
    @Test
    public void testRGBtoHSVtoRGB() {
        Image imgHSV = ColorConvert.fromRGBtoHSV(this.imageRGB);
        assertFalse(imgHSV.equals(this.imageRGB, 0.5));

        Image imgRGB = ColorConvert.fromHSVtoRGB(imgHSV);
        assertTrue(imgRGB.equals(this.imageRGB));
    }
}
