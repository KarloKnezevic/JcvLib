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
package org.jcvlib.test.core;

import static org.junit.Assert.*;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Image;

import org.junit.Before;
import org.junit.Test;

/**
 * Test main image classes {@link Image}.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageInterpolationExtrapolation {

    private Image image8I;

    private Image image64F;

    private void setUp(final Image image) {
        /*
         * A    0  1  2  3  4   B
         *   +----------------+
         * 0 |  1  2  3  4  5 |
         * 1 |  6  7  8  9 10 |
         * 2 | 11 12 13 14 15 |
         *   +----------------+
         * D                    C
         */
        int color = 1;
        // Do not change the loop order!
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                image.set8I(x, y, 0, color);

                ++color;
            }
        }


    }

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() throws Exception {
        this.image8I  = new Image(5, 3, 1, Image.TYPE_8I);
        this.image64F = new Image(5, 3, 1, Image.TYPE_64F);

        this.setUp(this.image8I);
        this.setUp(this.image64F);
    }

    private void testExtrapolationZero(Image image) {
        // A
        assertEquals( 0.0, image.get(-1, -1,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        assertEquals( 0.0, image.get(-2, -2,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        // B
        assertEquals( 0.0, image.get( 5, -1,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        assertEquals( 0.0, image.get( 6, -2,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        // C
        assertEquals( 0.0, image.get( 5,  3,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        assertEquals( 0.0, image.get( 6,  4,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        // D
        assertEquals( 0.0, image.get(-1,  3,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        assertEquals( 0.0, image.get(-2,  4,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);

        // A-B
        assertEquals( 0.0, image.get( 2, -1,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        assertEquals( 0.0, image.get( 2, -2,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        // B-C
        assertEquals( 0.0, image.get( 5,  1,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        assertEquals( 0.0, image.get( 6,  1,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        // C-D
        assertEquals( 0.0, image.get( 2,  3,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        assertEquals( 0.0, image.get( 2,  4,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        // D-A
        assertEquals( 0.0, image.get(-1,  1,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
        assertEquals( 0.0, image.get(-2,  1,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);

        // Center
        assertEquals( 8.0, image.get( 2,  1,  0, Image.EXTRAPLOATION_ZERO), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Image#get(int, int, int, int)}.
     */
    @Test
    public void testExtrapolationZero() {
        this.testExtrapolationZero(this.image8I);
        this.testExtrapolationZero(this.image64F);
    }

    private void testExtrapolationReplicate(Image image) {
        // A
        assertEquals( 1.0, image.get(-1, -1,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        assertEquals( 1.0, image.get(-2, -2,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        // B
        assertEquals( 5.0, image.get( 5, -1,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        assertEquals( 5.0, image.get( 6, -2,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        // C
        assertEquals(15.0, image.get( 5,  3,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        assertEquals(15.0, image.get( 6,  4,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        // D
        assertEquals(11.0, image.get(-1,  3,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        assertEquals(11.0, image.get(-2,  4,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);

        // A-B
        assertEquals( 3.0, image.get( 2, -1,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        assertEquals( 3.0, image.get( 2, -2,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        // B-C
        assertEquals(10.0, image.get( 5,  1,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        assertEquals(10.0, image.get( 6,  1,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        // C-D
        assertEquals(13.0, image.get( 2,  3,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        assertEquals(13.0, image.get( 2,  4,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        // D-A
        assertEquals( 6.0, image.get(-1,  1,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);
        assertEquals( 6.0, image.get(-2,  1,  0, Image.EXTRAPLOATION_REPLICATE), JCV.PRECISION_MAX);

        // Center
        assertEquals( 8.0, image.get( 2,  1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Image#get(int, int, int, int)}.
     */
    @Test
    public void testExtrapolationReplicate() {
        this.testExtrapolationReplicate(this.image8I);
        this.testExtrapolationReplicate(this.image64F);
    }

    private void testExtrapolationReflect(Image image) {
        // A
        assertEquals( 1.0, image.get(-1, -1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        assertEquals( 7.0, image.get(-2, -2,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        // B
        assertEquals( 5.0, image.get( 5, -1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        assertEquals( 9.0, image.get( 6, -2,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        // C
        assertEquals(15.0, image.get( 5,  3,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        assertEquals( 9.0, image.get( 6,  4,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        // D
        assertEquals(11.0, image.get(-1,  3,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        assertEquals( 7.0, image.get(-2,  4,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);

        // A-B
        assertEquals( 3.0, image.get( 2, -1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        assertEquals( 8.0, image.get( 2, -2,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        // B-C
        assertEquals(10.0, image.get( 5,  1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        assertEquals( 9.0, image.get( 6,  1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        // C-D
        assertEquals(13.0, image.get( 2,  3,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        assertEquals( 8.0, image.get( 2,  4,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        // D-A
        assertEquals( 6.0, image.get(-1,  1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
        assertEquals( 7.0, image.get(-2,  1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);

        // Center
        assertEquals( 8.0, image.get( 2,  1,  0, Image.EXTRAPLOATION_REFLECT), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Image#get(int, int, int, int)}.
     */
    @Test
    public void testExtrapolationReflect() {
        this.testExtrapolationReflect(this.image8I);
        this.testExtrapolationReflect(this.image64F);
    }

    private void testExtrapolationWrap(Image image) {
        // A
        assertEquals(15.0, image.get(-1, -1,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        assertEquals( 9.0, image.get(-2, -2,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        // B
        assertEquals(11.0, image.get( 5, -1,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        assertEquals( 7.0, image.get( 6, -2,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        // C
        assertEquals( 1.0, image.get( 5,  3,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        assertEquals( 7.0, image.get( 6,  4,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        // D
        assertEquals( 5.0, image.get(-1,  3,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        assertEquals( 9.0, image.get(-2,  4,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);

        // A-B
        assertEquals(13.0, image.get( 2, -1,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        assertEquals( 8.0, image.get( 2, -2,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        // B-C
        assertEquals( 6.0, image.get( 5,  1,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        assertEquals( 7.0, image.get( 6,  1,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        // C-D
        assertEquals( 3.0, image.get( 2,  3,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        assertEquals( 8.0, image.get( 2,  4,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        // D-A
        assertEquals(10.0, image.get(-1,  1,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
        assertEquals( 9.0, image.get(-2,  1,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);

        // Center
        assertEquals( 8.0, image.get( 2,  1,  0, Image.EXTRAPLOATION_WRAP), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Image#get(int, int, int, int)}.
     */
    @Test
    public void testExtrapolationWrap() {
        this.testExtrapolationWrap(this.image8I);
        this.testExtrapolationWrap(this.image64F);
    }

    private void testExtrapolationException(Image image) {
        // Incorrect extrapolation type.
        try {
            image.get(-1, -1, 0, 5);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Go over 2 x Width.
        try {
            image.get(-1, -2 * image.getWidth() - 1, 0, Image.EXTRAPLOATION_REFLECT);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Go over 2 x Height.
        try {
            image.get(-2 * image.getHeight() - 1, -1, 0, Image.EXTRAPLOATION_REFLECT);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#get(int, int, int, int)}.
     */
    @Test
    public void testExtrapolationException() {
        this.testExtrapolationException(this.image8I);
        this.testExtrapolationException(this.image64F);
    }

    private void testInterpolationNearest(Image image) {
        assertEquals(7.0, image.get(1.1, 1.1, 0, Image.INTERPOLATION_NEAREST_NEIGHBOR), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Image#get(double, double, int, int)}.
     */
    @Test
    public void testInterpolationNearest() {
        this.testInterpolationNearest(this.image8I);
        this.testInterpolationNearest(this.image64F);
    }

    private void testInterpolationLinear(Image image) {
        assertEquals( 7.5, image.get(1.5, 1.0, 0, Image.INTERPOLATION_BILINEAR), JCV.PRECISION_MAX);
        assertEquals(10.0, image.get(1.5, 1.5, 0, Image.INTERPOLATION_BILINEAR), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Image#get(double, double, int, int)}.
     */
    @Test
    public void testInterpolationLinear() {
        this.testInterpolationLinear(this.image8I);
        this.testInterpolationLinear(this.image64F);
    }

    private void testInterpolationBicubic(Image image) {
        assertEquals( 7.5,    image.get(1.5, 1.0, 0, Image.INTERPOLATION_BICUBIC), JCV.PRECISION_MAX);
        assertEquals(10.3125, image.get(1.5, 1.5, 0, Image.INTERPOLATION_BICUBIC), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Image#get(double, double, int, int)}.
     */
    @Test
    public void testInterpolationBicubic() {
        this.testInterpolationBicubic(this.image8I);
        this.testInterpolationBicubic(this.image64F);
    }

    private void testInterpolationException(Image image) {
        // Incorrect interpolation type.
        try {
            image.get(1.1, 1.1, 0, 3);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#get(double, double, int, int)}.
     */
    @Test
    public void testInterpolationException() {
        this.testInterpolationException(this.image8I);
        this.testInterpolationException(this.image64F);
    }
}
