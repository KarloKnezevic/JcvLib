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
import junit.framework.TestSuite;

import org.jcvlib.core.JCV;
import org.jcvlib.core.ImageArray;
import org.jcvlib.core.ImageArray64F;
import org.jcvlib.core.ImageArray8I;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.core.Rectangle;
import org.jcvlib.core.Size;

import org.junit.Test;

/**
 * Test main image class {@link Image}, {@link ImageArray}, {@link ImageArray8I}, {@link ImageArray64F}.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageBase extends TestSuite {

    private void testSize(final Image image) {
        assertEquals(300, image.getWidth());
        assertEquals(200, image.getHeight());
        assertEquals(4, image.getNumOfChannels());

        assertEquals(300, image.getSize().getWidth());
        assertEquals(200, image.getSize().getHeight());

        assertEquals(300 * 200 * 4, image.getN());
    }
    /**
     * Test method for: {@link Image#getWidth()}, {@link Image#getHeight()}, {@link Image#getSize()}, {@link Image#getN()}.
     */
    @Test
    public void testSize() {
        this.testSize(new Image(300, 200, 4, Image.TYPE_64F));
        this.testSize(new Image(300, 200, 4, Image.TYPE_8I));
    }

    /**
     * Test method for: {@link Image#toString()}.
     */
    @Test
    public void testToString() {
        System.out.println("Printing example float-point image:");
        System.out.println(new Image(300, 200, 4, Image.TYPE_64F).toString());

        System.out.println("Printing example integer image:");
        System.out.println(new Image(300, 200, 4, Image.TYPE_8I).toString());
    }

    /**
     * Test method for: {@link Image#get(int, int, int)}, {@link Image#get8I(int, int, int)},
     * {@link Image#set(int, int, int, double)}, {@link Image#set8I(int, int, int, int)}.
     */
    @Test
    public void testPrecision() {
        double value = 254.123_456_789_123_4;

        System.out.println(value);

        final Image image1 = new Image(300, 200, 1, Image.TYPE_64F);
        final Image image2 = new Image(300, 200, 1, Image.TYPE_8I);

        image1.set(0, 0, 0, value);
        image2.set(0, 0, 0, value);

        assertEquals(value, image1.get(0, 0, 0), JCV.PRECISION_MAX);
        assertEquals(value, image2.get(0, 0, 0), 1.0);
    }

    /**
     * Test method for: {@link Image#get(int, int, int)}, {@link Image#set(int, int, int, double)}.
     */
    @Test
    public void testSetGet() {
        final Image image = new Image(1500, 1200, 4, Image.TYPE_64F);
        double value;
        final double inc = 0.000_1;

        // Test set method.
        value = Color.COLOR_MIN_VALUE;
        boolean visit = false;
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    image.set(x, y, channel, value);

                    // Next value.
                    value += inc;
                    if (value > Color.COLOR_MAX_VALUE) {
                        value = Color.COLOR_MIN_VALUE;
                        visit = true;
                    }
                }
            }
        }
        assertTrue(visit);

        // Test get method.
        value = Color.COLOR_MIN_VALUE;
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    assertEquals(value, image.get(x, y, channel), JCV.PRECISION_MAX);
                    assertEquals(JCV.round(value), image.get8I(x, y, channel));

                    // Next value.
                    value += inc;
                    if (value > Color.COLOR_MAX_VALUE) {
                        value = Color.COLOR_MIN_VALUE;
                    }
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#get8I(int, int, int)}, {@link Image#set8I(int, int, int, int)}.
     */
    @Test
    public void testSetGet8I() {
        final Image image = new Image(300, 200, 4, Image.TYPE_8I);
        int value;

        // Test set method.
        value = JCV.roundDown(Color.COLOR_MIN_VALUE);
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    image.set8I(x, y, channel, value);

                    // Next value.
                    ++value;
                    if (value > Color.COLOR_MAX_VALUE) {
                        value = JCV.roundUp(Color.COLOR_MIN_VALUE);
                    }
                }
            }
        }

        // Test get method.
        value = JCV.roundDown(Color.COLOR_MIN_VALUE);
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    assertEquals(value, image.get(x, y, channel), JCV.PRECISION_MAX);
                    assertEquals(value, image.get8I(x, y, channel));

                    // Next value.
                    ++value;
                    if (value > Color.COLOR_MAX_VALUE) {
                        value = JCV.roundUp(Color.COLOR_MIN_VALUE);
                    }
                }
            }
        }
    }

    private void testSetTruncate(final Image image) {
        image.set(0, 0, 0, Color.COLOR_MIN_VALUE - 0.1);
        assertEquals(Color.COLOR_MIN_VALUE, image.get(0, 0, 0), JCV.PRECISION_MAX);
        assertEquals(Color.COLOR_MIN_VALUE, image.get8I(0, 0, 0), JCV.PRECISION_MAX);

        image.set8I(1, 0, 0, JCV.roundDown(Color.COLOR_MIN_VALUE - 0.1));
        assertEquals(Color.COLOR_MIN_VALUE, image.get(1, 0, 0), JCV.PRECISION_MAX);
        assertEquals(Color.COLOR_MIN_VALUE, image.get8I(1, 0, 0), JCV.PRECISION_MAX);

        image.set(2, 0, 0, Color.COLOR_MAX_VALUE + 0.1);
        assertEquals(Color.COLOR_MAX_VALUE, image.get(2, 0, 0), JCV.PRECISION_MAX);
        assertEquals(Color.COLOR_MAX_VALUE, image.get8I(2, 0, 0), JCV.PRECISION_MAX);

        image.set8I(3, 0, 0, JCV.roundUp(Color.COLOR_MAX_VALUE + 0.1));
        assertEquals(Color.COLOR_MAX_VALUE, image.get(3, 0, 0), JCV.PRECISION_MAX);
        assertEquals(Color.COLOR_MAX_VALUE, image.get8I(3, 0, 0), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {Image#get(int, int, int)}, {@link Image#get8I(int, int, int)}, {@link Image#set(int, int, int, double)},
     * {@link Image#set8I(int, int, int, int)}.
     */
    @Test
    public void testSetTruncate() {
        this.testSetTruncate(new Image(10, 10, 1, Image.TYPE_8I));
        this.testSetTruncate(new Image(10, 10, 1, Image.TYPE_64F));
    }

    /**
     * Test method for: {@link Image#get(Point)}, {@link Image#set(Point, Color)}.
     */
    @Test
    public void testSetGetPointAndColor() {
        final double val1 = 32.1;
        final Image image = new Image(100, 100, 4, Image.TYPE_64F, new Color(4, val1));
        assertTrue(image.get(new Point(10, 10)).equals(new Color(new double[]{ val1, val1, val1, val1 })));

        final double val2 = 64.2;
        final Color c = new Color(new double[]{ val2, val2, val2, val2 });
        final Point p = new Point(10, 10);
        image.set(p, c);
        assertTrue(image.get(p).equals(c));
    }

    /**
     * Test method for: {@link Image#get(int, int, int)}, {@link Image#get8I(int, int, int)},
     * {@link Image#set(int, int, int, double)}, {@link Image#set8I(int, int, int, int)}.
     */
    @Test
    public void testSetGetException() {
        final Image image = new Image(300, 200, 4, Image.TYPE_64F);

        // Get on incorrect Height.
        try {
            image.get(300, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.get8I(300, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Get on incorrect Width.
        try {
            image.get(0, 200, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.get8I(0, 200, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set on incorrect Channel.
        try {
            image.get(0, 0, 4);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.get8I(0, 0, 4);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set on incorrect Height.
        try {
            image.set(300, 0, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.set8I(300, 0, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set on incorrect Width.
        try {
            image.set(0, 200, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.set8I(0, 200, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set on incorrect Channel.
        try {
            image.set(0, 0, 4, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.set8I(0, 0, 4, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set incorrect value.
        try {
            image.set(1, 1, 1, Double.NaN);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image}.
     */
    @Test
    public void testInitialColor() {
        final Image image = new Image(300, 200, 4, Image.TYPE_64F, new Color(new double[]{ 1.1, 2.2, 3.3, 4.4 }));
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    assertEquals(channel * 1.1 + 1.1, image.get(x, y, channel), JCV.PRECISION_MAX);
                }
            }
        }
    }

    /**
     * Test method for: {@link Image}.
     */
    @Test
    public void testCreateException() {
        // Incorrect Channel value.
        try {
            new Image(300, 200, 0, Image.TYPE_64F);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            new Image(300, 200, 0, Image.TYPE_8I);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect Channel value.
        try {
            new Image(300, 200, -1, Image.TYPE_64F);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            new Image(300, 200, -1, Image.TYPE_8I);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect initial color 1.
        try {
            new Image(300, 200, 1, Image.TYPE_64F, null);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            new Image(300, 200, 1, Image.TYPE_8I, null);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect initial color 2.
        try {
            new Image(300, 200, 3, Image.TYPE_64F, new Color(2, 1.0));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            new Image(300, 200, 3, Image.TYPE_8I, new Color(2, 1.0));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect type.
        try {
            new Image(300, 200, 1, 2);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#copy()}, {@link Image#copyTo(Image)}, {@link Image#equals(Object)}.
     */
    @Test
    public void testCopyCloneEquals() {
        final Image image = new Image(100, 100, 4, Image.TYPE_64F, new Color(new double[]{ 0.1, 0.2, 0.3, 0.4 }));
        assertTrue(image.equals(image));
        assertTrue(image.equals(image.copy()));
        assertFalse(image.equals(null));
        assertFalse(image.equals(image.getSubimage(10, 10, 10, 10)));
        assertFalse(image.equals(image.getLayer(1, 2)));
        assertFalse(image.equals(0));

        final Image copy = image.copy();
        copy.set(0, 0, 0, 0.5);
        assertFalse(image.equals(copy));

        final Image image1 = new Image(100, 100, 5, image.getType());
        assertFalse(image.equals(image1));
    }

    private void testCopyTo(final Image image) {
        final Image copy = image.copy();
        copy.set(10, 10, 2, 1.0);
        assertFalse(image.equals(copy));

        image.copyTo(copy);
        assertTrue(image.equals(copy));
    }

    /**
     * Test method for: {@link Image#copyTo(Image)}.
     */
    @Test
    public void testCopyTo() {
        this.testCopyTo(new Image(100, 100, 4, Image.TYPE_64F));
        this.testCopyTo(new Image(100, 100, 4, Image.TYPE_8I));
    }

    private void testCopyToException(final Image image) {
        final Image subImg = image.getSubimage(10, 10, 20, 20);
        try {
            image.copyTo(subImg);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        final Image layer = image.getLayer(0, 2);
        try {
            image.copyTo(layer);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#copyTo(Image)}.
     */
    @Test
    public void testCopyToException() {
        this.testCopyToException(new Image(100, 100, 3, Image.TYPE_8I));
        this.testCopyToException(new Image(100, 100, 3, Image.TYPE_64F));
    }

    private void testGetSubimage(final Image image, final double precision) {
        final Image subImage = image.getSubimage(new Rectangle(new Point(80, 80), new Size(20, 20)));
        final Image subSubImage = subImage.getSubimage(10, 10, 10, 10);

        assertEquals(subImage.getHeight(), 20);
        assertEquals(subImage.getWidth(), 20);

        assertEquals(subSubImage.getHeight(), 10);
        assertEquals(subSubImage.getWidth(), 10);

        /*
         * Verify access to pixels of sub-images.
         */
        final double value = 0.5;

        // Set pixels values.
        System.out.println(subSubImage);
        for (int y = 0; y < subSubImage.getHeight(); ++y) {
            for (int x = 0; x < subSubImage.getWidth(); ++x) {
                for (int channel = 0; channel < 4; ++channel) {
                    subSubImage.set(y, x, channel, value);
                }
            }
        }

        // Get and check pixels values.
        for (int y = 90; y < 100; ++y) {
            for (int x = 90; x < 100; ++x) {
                for (int channel = 0; channel < 4; ++channel) {
                    assertEquals(value, image.get(y, x, channel), precision);
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#getSubimage(Rectangle)}.
     */
    @Test
    public void testGetSubimage() {
        this.testGetSubimage(new Image(100, 100, 4, Image.TYPE_64F), JCV.PRECISION_MAX);
        this.testGetSubimage(new Image(100, 100, 4, Image.TYPE_8I), 1.0);
    }

    private void testGetSubimageException(final Image image) {
        image.getSubimage(new Rectangle(80, 80, 20, 20));

        // Incorrect Width.
        try {
            image.getSubimage(new Rectangle(80, 80, 20, 21));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect Height.
        try {
            image.getSubimage(new Rectangle(80, 80, 21, 20));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#getSubimage(Rectangle)}.
     */
    @Test
    public void testGetSubimageException() {
        this.testGetSubimageException(new Image(100, 100, 4, Image.TYPE_8I));
        this.testGetSubimageException(new Image(100, 100, 4, Image.TYPE_64F));
    }

    private void testGetLayerAndChannel(final Image image) {
        double value;
        final double inc = JCV.PRECISION_MAX * 1000;

        int startChannel = 1;
        int sizeLayer = 2;
        int numChannel = 3;

        final Image layer = image.getLayer(startChannel, sizeLayer);
        final Image channel = image.getChannel(numChannel);

        /*
         * Check size.
         */
        assertEquals(image.getHeight(), layer.getHeight());
        assertEquals(image.getWidth(), layer.getWidth());
        assertEquals(sizeLayer, layer.getNumOfChannels());

        assertEquals(image.getHeight(), channel.getHeight());
        assertEquals(image.getWidth(), channel.getWidth());
        assertEquals(1, channel.getNumOfChannels());

        /*
         * Check, that we have same source.
         */
        // Set values.
        value = 0.0;
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                for (int ch = 0; ch < image.getNumOfChannels(); ++ch) {
                    image.set(y, x, ch, value);

                    // Next value.
                    value += inc;
                    if (value > Color.COLOR_MAX_VALUE) {
                        value = 0.0;
                    }
                }
            }
        }
        // Check values.
        for (int y = 0; y < layer.getHeight(); ++y) {
            for (int x = 0; x < layer.getWidth(); ++x) {
                // Check single channel.
                assertEquals(image.get(y, x, numChannel), channel.get(y, x, 0), JCV.PRECISION_MAX);

                // Check layer.
                for (int ch = 0; ch < layer.getNumOfChannels(); ++ch) {
                    assertEquals(image.get(y, x, ch + startChannel), layer.get(y, x, ch), JCV.PRECISION_MAX);
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#getLayer(int, int)}, {@link Image#getChannel(int)}.
     */
    @Test
    public void testGetLayerAndChannel() {
        this.testGetLayerAndChannel(new Image(100, 100, 4, Image.TYPE_8I));
        this.testGetLayerAndChannel(new Image(100, 100, 4, Image.TYPE_64F));
    }

    private void testGetLayerAndChannelException(Image image) {
        // Incorrect Channel.
        try {
            image.getChannel(-1);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect Channel.
        try {
            image.getChannel(4);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect start position.
        try {
            image.getLayer(-1, 2);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect start length.
        try {
            image.getLayer(0, 5);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect start length.
        try {
            image.getLayer(2, 3);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect start length.
        try {
            image.getLayer(2, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#getLayer(int, int)}, {@link Image#getChannel(int)}.
     */
    @Test
    public void testGetLayerAndChannelException() {
        this.testGetLayerAndChannelException(new Image(100, 100, 4, Image.TYPE_8I));
        this.testGetLayerAndChannelException(new Image(100, 100, 4, Image.TYPE_64F));
    }
}
