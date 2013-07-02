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

import org.jcvlib.core.Point;
import org.jcvlib.core.Rectangle;

import org.junit.Test;

/**
 * Test class for rectangle {@link Rectangle}.
 *
 * @version 1.010
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class RectangleTest {
    /**
     * Test method for: {@link Rectangle#toString()}.
     */
    @Test
    public void testToString() {
        Rectangle rect = new Rectangle(2, 1, 20, 10);

        System.out.println("Printing example:");
        System.out.println(rect.toString());
    }

    /**
     * Test method for: {@link Rectangle#equals(Object)}.
     */
    @Test
    public void testEquals() {
        Rectangle rect1 = new Rectangle(2, 1, 20, 10);
        assertTrue(rect1.equals(rect1));
        assertFalse(rect1.equals(null));
        assertFalse(rect1.equals(0));

        assertFalse(rect1.equals(new Rectangle(1, 1, 20, 10)));
        assertFalse(rect1.equals(new Rectangle(2, 2, 20, 10)));
        assertFalse(rect1.equals(new Rectangle(2, 1, 20, 11)));
        assertFalse(rect1.equals(new Rectangle(2, 1, 21, 10)));
    }

    /**
     * Test method for: {@link Rectangle#contains(Rectangle)}.
     */
    @Test
    public void testInsideIn() {
        Rectangle rect = new Rectangle(10, 20, 10, 20);

        assertTrue(rect.contains(new Rectangle(10, 20, 10, 20)));

        // Incorrect Y position.
        assertFalse(rect.contains(new Rectangle(11, 20, 10, 20)));
        // Incorrect X position.
        assertFalse(rect.contains(new Rectangle(10, 21, 10, 20)));
        // Incorrect Height.
        assertFalse(rect.contains(new Rectangle(10, 20, 11, 20)));
        // Incorrect Width.
        assertFalse(rect.contains(new Rectangle(10, 20, 10, 21)));
    }

    /**
     * Test method for: {@link Rectangle#contains(Point)}.
     */
    @Test
    public void testContains() {
        Rectangle rect = new Rectangle(10, 20, 10, 20);

        assertTrue(rect.contains(new Point(10, 20)));
        assertTrue(rect.contains(new Point(19, 39)));

        // Incorrect X position.
        assertFalse(rect.contains(new Point(9, 20)));
        // Incorrect Y position.
        assertFalse(rect.contains(new Point(10, 19)));
        // Incorrect Width.
        assertFalse(rect.contains(new Point(20, 39)));
        // Incorrect Height.
        assertFalse(rect.contains(new Point(19, 40)));
    }
}
