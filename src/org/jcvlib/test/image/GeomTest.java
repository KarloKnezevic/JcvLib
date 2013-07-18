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
package org.jcvlib.test.image;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.image.Geom;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link Geom}.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class GeomTest {
    private Image image;

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() throws Exception {
        this.image = new Image(150, 120, 3, Image.TYPE_64F);

        // Do not change the loop order!
        for (int channel = 0; channel < this.image.getNumOfChannels(); ++channel) {
            double val = Color.COLOR_MIN_VALUE;
            for (int y = 0; y < this.image.getHeight(); ++y) {
                for (int x = 0; x < this.image.getWidth(); ++x) {
                    this.image.set(x, y, channel, val);

                    val += 1.0;
                    if (val > Color.COLOR_MAX_VALUE) {
                        val = Color.COLOR_MIN_VALUE;
                    }
                }
            }
        }
    }

    /**
     * Test method for: {@link Geom#reflect(Image, int)}.
     */
    @Test
    public void testMirroringHorizontal() {
        Image mirror = Geom.reflect(this.image, Geom.REFLECT_HORIZONTAL);

        assertTrue(this.image.getSize().equals(mirror.getSize()));

        // Check values.
        for (int x = 0; x < mirror.getWidth(); ++x) {
            for (int y = 0; y < mirror.getHeight(); ++y) {
                for (int channel = 0; channel < this.image.getNumOfChannels(); ++channel) {
                    double answer = this.image.get(mirror.getWidth() - x - 1, y, channel);
                    double result = mirror.get(x, y, channel);

                    assertEquals(answer, result, JCV.PRECISION_MAX);
                }
            }
        }
    }

    /**
     * Test method for: {@link Geom#reflect(Image, int)}.
     */
    @Test
    public void testMirroringVertical() {
        Image mirror = Geom.reflect(this.image, Geom.REFLECT_VERTICAL);

        assertTrue(this.image.getSize().equals(mirror.getSize()));

        // Check values.
        for (int x = 0; x < mirror.getWidth(); ++x) {
            for (int y = 0; y < mirror.getHeight(); ++y) {
                for (int channel = 0; channel < this.image.getNumOfChannels(); ++channel) {
                    double answer = this.image.get(x, mirror.getHeight() - y - 1, channel);
                    double result = mirror.get(x, y, channel);

                    assertEquals(answer, result, JCV.PRECISION_MAX);
                }
            }
        }
    }

    /**
     * Test method for: {@link Geom#reflect(Image, int)}.
     */
    @Test
    public void testMirroringDiagonal() {
        Image mirror = Geom.reflect(this.image, Geom.REFLECT_DIAGONAL);

        assertTrue(this.image.getSize().equals(mirror.getSize()));

        // Check values.
        for (int x = 0; x < mirror.getWidth(); ++x) {
            for (int y = 0; y < mirror.getHeight(); ++y) {
                for (int channel = 0; channel < this.image.getNumOfChannels(); ++channel) {
                    double answer = this.image.get(mirror.getWidth() - x - 1, mirror.getHeight() - y - 1, channel);
                    double result = mirror.get(x, y, channel);

                    assertEquals(answer, result, JCV.PRECISION_MAX);
                }
            }
        }
    }

    /**
     * Test method for: {@link Geom#getPerspectiveTransfrom(java.util.List, java.util.List)}.
     */
    @Test
    public void testGetPerspectiveTransformException() {
        List<Point> pointsIncorrect = new LinkedList<Point>();
        pointsIncorrect.add(new Point(0, 0));
        pointsIncorrect.add(new Point(10, 10));
        pointsIncorrect.add(new Point(20, 20));
        pointsIncorrect.add(new Point(67, 207));

        List<Point> pointsCorrect = new LinkedList<Point>();
        pointsCorrect.add(new Point(49, 83));
        pointsCorrect.add(new Point(210, 66));
        pointsCorrect.add(new Point(238, 174));
        pointsCorrect.add(new Point(67, 207));

        try {
            Geom.getPerspectiveTransfrom(pointsIncorrect, pointsCorrect);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
}
