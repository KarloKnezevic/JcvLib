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
package org.jcvlib.test.core;

import static org.junit.Assert.*;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;

import org.junit.Test;

/**
 * Test class for colors {@link Color}.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ColorTest {
    /**
     * Test method for: {@link Color}.
     */
    @Test
    public void testCreate() {
        new Color(1);

        final double vlaue = 127.5;
        final Color color1 = new Color(3, vlaue);
        assertEquals(vlaue, color1.get(0), JCV.PRECISION_MAX);
        assertEquals(vlaue, color1.get(1), JCV.PRECISION_MAX);
        assertEquals(vlaue, color1.get(2), JCV.PRECISION_MAX);

        final double[] c = new double[]{ 32.1, 64.2, 128.3 };
        final Color color2 = new Color(c);
        assertEquals(c[0], color2.get(0), JCV.PRECISION_MAX);
        assertEquals(c[1], color2.get(1), JCV.PRECISION_MAX);
        assertEquals(c[2], color2.get(2), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Color}.
     */
    @Test
    public void testCreateException() {
        // Incorrect number of Channels.
        try {
            new Color(0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect number of Channels.
        try {
            new Color(-1);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect source value.
        try {
            new Color(new double[]{});
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Color#toString()}.
     */
    @Test
    public void testToString() {
        Color color = new Color(new double[]{ 10.0, 20.0, 30.0 });

        System.out.println("Printing example:");
        System.out.println(color.toString());
    }

    /**
     * Test method for: {@link Color#set(int, double)}, {@link Color#get(int)}.
     */
    @Test
    public void testSetGet() {
        final Color color = new Color(3);

        color.set(0, Color.COLOR_MIN_VALUE + 0.0);
        color.set(1, Color.COLOR_MIN_VALUE + 0.1);
        color.set(2, Color.COLOR_MIN_VALUE + 0.2);

        assertEquals(Color.COLOR_MIN_VALUE + 0.0, color.get(0), JCV.PRECISION_MAX);
        assertEquals(Color.COLOR_MIN_VALUE + 0.1, color.get(1), JCV.PRECISION_MAX);
        assertEquals(Color.COLOR_MIN_VALUE + 0.2, color.get(2), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Color#set(int, double)}, {@link Color#get(int)}.
     */
    @Test
    public void testGetSetException() {
        final Color color = new Color(5);

        // Set in incorrect position.
        try {
            color.set(5, Color.COLOR_MIN_VALUE);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set incorrect value.
        try {
            color.set(1, Double.NaN);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Color#set(int, double)}, {@link Color#get(int)}.
     */
    @Test
    public void testSetTruncate() {
        final Color color = new Color(4);

        // Test truncate to min value.
        color.set(0, Color.COLOR_MIN_VALUE - 1.0);
        color.set(1, Double.NEGATIVE_INFINITY);

        // Test truncate to max value.
        color.set(2, Color.COLOR_MAX_VALUE + 1.0);
        color.set(3, Double.POSITIVE_INFINITY);

        assertEquals(Color.COLOR_MIN_VALUE, color.get(0), JCV.PRECISION_MAX);
        assertEquals(Color.COLOR_MIN_VALUE, color.get(1), JCV.PRECISION_MAX);

        assertEquals(Color.COLOR_MAX_VALUE, color.get(2), JCV.PRECISION_MAX);
        assertEquals(Color.COLOR_MAX_VALUE, color.get(3), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Color#copy()}, {@link Color#equals(Object)}.
     */
    @Test
    public void testCopyEquals() {
        final Color color1 = new Color(2, 32.1);
        assertTrue(color1.equals(color1));
        assertFalse(color1.equals(null));

        final Color color2 = color1.copy();
        assertTrue(color1.equals(color2));

        color2.set(1, 64.2);
        assertFalse(color1.equals(color2));

        final Color color3 = new Color(3, 1);
        assertFalse(color1.equals(color3));
    }

    /**
     * Test method for: {@link Color#euclidDist(Color)}.
     */
    @Test
    public void testEuclidDist() {
        assertEquals(1.0, (new Color(new double[]{ 5.0, 5.0 })).euclidDist(new Color(new double[]{ 4.0, 6.0 })), JCV.PRECISION_MAX);

        final Color min = new Color(new double[]{ Color.COLOR_MIN_VALUE, Color.COLOR_MIN_VALUE });
        final Color max = new Color(new double[]{ Color.COLOR_MAX_VALUE, Color.COLOR_MAX_VALUE });
        assertEquals(Color.COLOR_MAX_VALUE, min.euclidDist(max), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Color#euclidDist(Color)}.
     */
    @Test
    public void testEuclidDistException() {
        try {
            (new Color(new double[]{ 1.0, 1.0 })).euclidDist(new Color(new double[]{ 4.0, 6.0, 8.0 }));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
}
