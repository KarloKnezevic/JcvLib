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

import org.jcvlib.core.Color;
import org.jcvlib.core.JCV;
import org.jcvlib.core.Image;
import org.jcvlib.core.Size;
import org.junit.Test;

/**
 * Test class for points {@link JCV}.
 *
 * @version 1.008
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class JCVTest {
    /**
     * Test method for: {@link JCV#equalValues(double, double)}, {@link JCV#equalValues(double, double, double)}.
     */
    @Test
    public void testEqualValues() {
        assertTrue(JCV.equalValues(JCV.PRECISION_MAX, JCV.PRECISION_MAX));
        assertFalse(JCV.equalValues(JCV.PRECISION_MAX, 3 * JCV.PRECISION_MAX));
    }

    /**
     * Test method for: {@link JCV#round(double)}.
     */
    @Test
    public void testRound() {
        assertEquals(1, JCV.round(1.0));
        assertEquals(1, JCV.round(1.1));
        assertEquals(2, JCV.round(1.5));
        assertEquals(2, JCV.round(1.9));
        assertEquals(2, JCV.round(2.0));

        assertEquals(0, JCV.round(Color.COLOR_MIN_VALUE));
        assertEquals(255, JCV.round(Color.COLOR_MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, JCV.round(Double.NEGATIVE_INFINITY));
        assertEquals(0, JCV.round(Double.NaN));
        assertEquals(Integer.MAX_VALUE, JCV.round(Double.POSITIVE_INFINITY));
    }

    /**
     * Test method for: {@link JCV#roundUp(double)}.
     */
    @Test
    public void testRoundUp() {
        assertEquals(1, JCV.roundUp(1.0));
        assertEquals(2, JCV.roundUp(1.1));
        assertEquals(2, JCV.roundUp(1.5));
        assertEquals(2, JCV.roundUp(1.9));
        assertEquals(2, JCV.roundUp(2.0));

        assertEquals(0, JCV.roundUp(Color.COLOR_MIN_VALUE));
        assertEquals(255, JCV.roundUp(Color.COLOR_MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, JCV.roundUp(Double.NEGATIVE_INFINITY));
        assertEquals(0, JCV.roundUp(Double.NaN));
        assertEquals(Integer.MAX_VALUE, JCV.roundUp(Double.POSITIVE_INFINITY));
    }

    /**
     * Test method for: {@link JCV#roundDown(double)}.
     */
    @Test
    public void testRoundDown() {
        assertEquals(1, JCV.roundDown(1.0));
        assertEquals(1, JCV.roundDown(1.1));
        assertEquals(1, JCV.roundDown(1.5));
        assertEquals(1, JCV.roundDown(1.9));
        assertEquals(2, JCV.roundDown(2.0));

        assertEquals(0, JCV.roundDown(Color.COLOR_MIN_VALUE));
        assertEquals(255, JCV.roundDown(Color.COLOR_MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, JCV.roundDown(Double.NEGATIVE_INFINITY));
        assertEquals(0, JCV.roundDown(Double.NaN));
        assertEquals(Integer.MAX_VALUE, JCV.roundDown(Double.POSITIVE_INFINITY));
    }

    /**
     * Test method for: {@link JCV#verifyIsNotNull(Object, String)}.
     */
    @Test
    public void testVerifyIsNotNull() {
        JCV.verifyIsNotNull(1, "paramName");

        // Incorrect source object.
        try {
            JCV.verifyIsNotNull(null, "paramName");
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link JCV#verifyIsSameSize(Image, String, Image, String)}.
     */
    @Test
    public void testVerifySameSizeImage() {
        Image image1 = new Image(100, 100, 4, Image.TYPE_8I);
        Image image2 = new Image(100, 100, 6, Image.TYPE_8I);
        Image image3 = new Image(100, 101, 5, Image.TYPE_8I);
        Image image4 = new Image(101, 100, 6, Image.TYPE_8I);

        JCV.verifyIsSameSize(image1, "image1", image2, "image2");

        try {
            JCV.verifyIsSameSize(image1, "image1", image3, "image3");
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        try {
            JCV.verifyIsSameSize(image1, "image1", image4, "image4");
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link JCV#verifyOddSize(Size, String)}.
     */
    @Test
    public void testGetOddKernelCenter() {
        JCV.verifyOddSize(new Size(1, 1), "size");
        JCV.verifyOddSize(new Size(3, 3), "size");
        JCV.verifyOddSize(new Size(5, 5), "size");

        // Incorrect Width.
        try {
            JCV.verifyOddSize(new Size(2, 3), "size");
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect Height.
        try {
            JCV.verifyOddSize(new Size(3, 2), "size");
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
}
