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
package org.jcvlib.test.image;

import static org.junit.Assert.*;

import org.jcvlib.core.Color;
import org.jcvlib.core.FloodFillStruct;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.image.Misc;
import org.junit.Test;

/**
 * Test {@link Misc}.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class MiscTest {
    /**
     * Test method for: {@link Misc#floodFill(Image, Point, double, Color, int, int)}.
     */
    @Test
    public void testFloodFill() {
        /*
         * Ininitialize the image.
         */
        /*
         * +----------------+
         * |  0  1  2  3  4 |
         * |  5  6  7  8  3 |
         * | 10 11 12 13 14 |
         * +----------------+
         */
        Image image = new Image(5, 3, 1, Image.TYPE_8I);
        int color = 0;
        // Do not change the loop order!
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                image.set(x, y, 0, color);
                ++color;
            }
        }
        image.set(4, 1, 0, 3.0);

        /*
         * Test 1.
         */
        // FloodFill this image.
        FloodFillStruct result1 =
            Misc.floodFill(image.copy(), new Point(0, 0), 3.0, new Color(1, Color.COLOR_MAX_VALUE), Misc.DIRECTIONS_TYPE_4,
                Misc.FLOOD_FILL_RANGE_FIXED);
        // Check.
        assertEquals(4, result1.getTotalFillPixels());

        /*
         * Test 2.
         */
        // FloodFill this image.
        FloodFillStruct result2 =
            Misc.floodFill(image.copy(), new Point(0, 0), 4.0, new Color(1, Color.COLOR_MAX_VALUE), Misc.DIRECTIONS_TYPE_4,
                Misc.FLOOD_FILL_RANGE_NEIGHBOR);
        // Check.
        assertEquals(6, result2.getTotalFillPixels());

        /*
         * Test 3.
         */
        // FloodFill this image.
        FloodFillStruct result3 =
            Misc.floodFill(image.copy(), new Point(0, 0), 3.0, new Color(1, Color.COLOR_MAX_VALUE), Misc.DIRECTIONS_TYPE_8,
                Misc.FLOOD_FILL_RANGE_FIXED);
        // Check.
        assertEquals(5, result3.getTotalFillPixels());
    }

    /**
     * Test method for: {@link Misc#floodFill(Image, Point, double, Color, int, int)}.
     */
    @Test
    public void testFloodFillException() {
        Image image = new Image(100, 100, 5, Image.TYPE_8I);
        Point point = new Point(0, 0);
        Color color = new Color(1, Color.COLOR_MAX_VALUE);

        // Incorrect direction type.
        try {
            Misc.floodFill(image, point, 3.0, color, 9, Misc.FLOOD_FILL_RANGE_FIXED);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect range type.
        try {
            Misc.floodFill(image, point, 3.0, color, Misc.DIRECTIONS_TYPE_8, 2);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Misc#integralImage(Image)}.
     */
    @Test
    public void testIntergralImage() {
        Image image = new Image(1500, 1200, 3, Image.TYPE_64F, new Color(3, Color.COLOR_MAX_VALUE));
        Image intImage = Misc.integralImage(image);

        // Check values.
        // Do not change the loop order!
        for (int x = 0; x < intImage.getWidth(); ++x) {
            for (int y = 0; y < intImage.getHeight(); ++y) {
                for (int channel = 0; channel < intImage.getNumOfChannels(); ++channel) {
                    assertEquals((x + 1) * (y + 1) * Color.COLOR_MAX_VALUE, intImage.get(x, y, channel) * intImage.getSize().getN(), 1.0);
                }
            }
        }
    }
}
