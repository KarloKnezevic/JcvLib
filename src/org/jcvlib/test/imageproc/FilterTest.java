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
import org.jcvlib.core.Point;
import org.jcvlib.core.Size;

import org.jcvlib.imageproc.Filters;
import org.jcvlib.imageproc.Operator;

import org.junit.Test;

import Jama.Matrix;

/**
 * Test class for colors {@link Filters}.
 * 
 * @version 1.010
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class FilterTest {
    /**
     * Print first channel of given image with given comment.
     */
    private static void printImage(Image image, String comment) {
        System.out.println(comment);
        // Do not change the loop order!
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                System.out.print(image.get(x, y, 0) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * Test method for: {@link Filters#noneLinearFilter(Image, Image, Size, Point, int, int, Operator)}.
     */
    @Test
    public void testNonlinearFilter1() {
        Image image = new Image(5, 5, 1, Image.TYPE_64F, new Color(1, Color.COLOR_MIN_VALUE));
        int counter;
        
        // Initialize test image.
        counter = 1;
        // Do not change the loop order!
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                image.set8I(x, y, 0, counter);
                ++counter;
            }
        }
        
        printImage(image, "Image before any filter applying:");
        
        /*
         * 0 1 2 3 4
         * +-----------+
         * 0 | o o o o o |
         * 1 | o o o o o |
         * 2 | o o o x o |
         * 3 | o o o o o |
         * +-----------+
         */
        Size kernelSize = new Size(5, 4);
        final Point anchor = new Point(3, 2);
        
        Image result = new Image(5, 5, 1, Image.TYPE_64F);
        Filters.noneLinearFilter(image, result, kernelSize, anchor, 3, Image.EXTRAPLOATION_ZERO, new Operator() {
            @Override
            public Color execute(Image aperture) {
                // Nothing changing.
                return aperture.get(anchor);
            }
        });
        
        printImage(result, "Image after nonlinear filter applying:");
        
        // Check values.
        counter = 1;
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                assertEquals(counter, result.get8I(x, y, 0));
                ++counter;
            }
        }
    }
    
    /**
     * Test method for: {@link Filters#noneLinearFilter(Image, Image, Size, Point, int, int, Operator)}.
     */
    @Test
    public void testNonlinearFilter2() {
        Image image = new Image(5, 5, 1, Image.TYPE_64F, new Color(1, Color.COLOR_MIN_VALUE));
        /*
         * A 0 1 2 3 4 B
         * +----------------+
         * 0 | 0 1 0 0 0 |
         * 1 | 0 0 0 0 0 |
         * 2 | 0 0 0 0 0 |
         * 3 | 0 0 0 0 0 |
         * 4 | 0 0 0 0 0 |
         * +----------------+
         * D C
         */
        image.set(1, 0, 0, 1.0);
        printImage(image, "Matrix before any filter applying:");
        
        /*
         * 0 1 2 3 4
         * +-----------+
         * 0 | o o o o o |
         * 1 | o o o o o |
         * 2 | o o o x o |
         * 3 | o o o o o |
         * +-----------+
         */
        Size kernelSize = new Size(5, 4);
        final Point anchor = new Point(3, 2);
        
        Image result = new Image(5, 5, 1, Image.TYPE_64F);
        Filters.noneLinearFilter(image, result, kernelSize, anchor, 2, Image.EXTRAPLOATION_ZERO, new Operator() {
            @Override
            public Color execute(Image aperture) {
                /*
                 * 0 1 2 3 4
                 * +-----------+
                 * 0 | o o f o o |
                 * 1 | o o o o o |
                 * 2 | o o o t o |
                 * 3 | o o o o o |
                 * +-----------+
                 * f --> t
                 */
                return aperture.get(new Point(2, 0));
                // return aperture.get(anchor);
            }
        });
        
        // Check values.
        /*
         * A 0 1 2 3 4 B
         * +----------------+
         * 0 | 0 0 0 0 0 |
         * 1 | 0 0 0 0 0 |
         * 2 | 0 0 0 0 0 |
         * 3 | 0 0 0 0 0 |
         * 4 | 0 0 0 1 0 |
         * +----------------+
         * D C
         */
        printImage(result, "Matrix after nonlinear filter applying:");
        
        // Check values.
        for (int y = 0; y < result.getHeight(); ++y) {
            for (int x = 0; x < result.getWidth(); ++x) {
                if (x == 3 && y == 4) {
                    assertEquals(1.0, result.get(x, y, 0), JCV.PRECISION_MAX);
                } else {
                    assertEquals(0.0, result.get(x, y, 0), JCV.PRECISION_MAX);
                }
            }
        }
    }
    
    /**
     * Test method for: {@link Filters#noneLinearFilter(Image, Image, Size, Point, int, int, Operator)}.
     */
    @Test
    public void testNonlinearFilterException() {
        Operator op = new Operator() {
            @Override
            public Color execute(Image aperture) {
                return new Color(aperture.getNumOfChannels(), 0.0);
            }
        };
        
        // Incorrect anchor X position.
        try {
            Filters.noneLinearFilter(new Image(100, 100, 3, Image.TYPE_64F), new Image(100, 100, 3, Image.TYPE_64F), new Size(5, 5),
                new Point(5, 1), 1, Image.EXTRAPLOATION_ZERO, op);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        
        // Incorrect anchor Y position.
        try {
            Filters.noneLinearFilter(new Image(100, 100, 3, Image.TYPE_64F), new Image(100, 100, 3, Image.TYPE_64F), new Size(5, 5),
                new Point(1, 5), 1, Image.EXTRAPLOATION_ZERO, op);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        
        // Incorrect iterations.
        try {
            Filters.noneLinearFilter(new Image(100, 100, 3, Image.TYPE_64F), new Image(100, 100, 3, Image.TYPE_64F), new Size(5, 5),
                new Point(1, 1), -1, Image.EXTRAPLOATION_ZERO, op);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
    
    /**
     * Test method for: {@link Filters#linearFilter(Image, Matrix, double, double, int)}.
     */
    @Test
    public void testMatrixFilter() {
        Image image = new Image(5, 5, 1, Image.TYPE_64F, new Color(1, Color.COLOR_MIN_VALUE));
        /*
         * A 0 1 2 3 4 B
         * +--------------------------+
         * 0 | 0.0 0.0 0.0 0.0 0.0 |
         * 1 | 0.0 0.0 0.0 0.0 0.0 |
         * 2 | 0.0 0.0 0.1 0.0 0.0 |
         * 3 | 0.0 0.0 0.0 0.0 0.0 |
         * 4 | 0.0 0.0 0.0 0.0 0.0 |
         * +--------------------------+
         * D C
         */
        image.set(2, 2, 0, 0.1);
        
        printImage(image, "Matrix after linear filter applying:");
        
        /*
         * Second kernel.
         */
        Matrix kernel =
            new Matrix(new double[][]{ { 1.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0 } });
        
        Image result = Filters.linearFilter(image, kernel, 0.5, 0.3, Image.EXTRAPLOATION_ZERO);
        
        // Check values.
        /*
         * A 0 1 2 3 4 B
         * +--------------------------+
         * 0 | 0.3 0.3 0.3 0.3 0.3 |
         * 1 | 0.3 0.3 0.3 0.3 0.3 |
         * 2 | 0.3 0.3 0.3 0.3 0.3 |
         * 3 | 0.3 0.3 0.3 0.3 0.3 |
         * 4 | 0.3 0.3 0.3 0.5 0.3 |
         * +--------------------------+
         * D C
         */
        printImage(result, "Matrix after linear filter applying:");
        
        // Check values.
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                if (x == 3 && y == 4) {
                    assertEquals(0.5, result.get(x, y, 0), JCV.PRECISION_MAX);
                } else {
                    assertEquals(0.3, result.get(x, y, 0), JCV.PRECISION_MAX);
                }
            }
        }
    }
    
    /**
     * Test method for: {@link Filters#getGaussianKernel(int, double)}.
     */
    @Test
    public void testGaussKernel() {
        /*
         * Use values from: http://www.embege.com/gauss/
         */
        double[] kernelTest =
            new double[]{ 0.05448868454964433, 0.24420134200323346, 0.40261994689424435, 0.24420134200323346, 0.05448868454964433 };
        
        Matrix kernel = Filters.getGaussianKernel(5, 1.0);
        
        assertEquals(kernelTest.length, kernel.getRowDimension());
        assertEquals(1, kernel.getColumnDimension());
        
        double sumTest = 0.0;
        double sum = 0.0;
        for (int i = 0; i < kernelTest.length; ++i) {
            sumTest += kernelTest[i];
            sum += kernel.get(i, 0);
        }
        assertEquals(1.0, sumTest, 100 * JCV.PRECISION_MAX);
        assertEquals(1.0, sum, 100 * JCV.PRECISION_MAX);
        
        // Check values.
        for (int i = 0; i < kernelTest.length; ++i) {
            assertEquals(kernelTest[i], kernel.get(i, 0), 1000 * JCV.PRECISION_MAX);
        }
    }
    
    /**
     * Test method for: {@link Filters#getGaussianKernel(int, double)}.
     */
    @Test
    public void testGaussKernelException() {
        // Incorrect kernel size (more than 0).
        try {
            Filters.getGaussianKernel(0, 1.0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        
        // Incorrect kernel size (should be odd: 1, 3, 5, ...).
        try {
            Filters.getGaussianKernel(2, 1.0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
    
    /**
     * Test method for: {@link Filters#getSigma(int)}, {@link Filters#getKernelSize(double)}.
     */
    @Test
    public void testSigmaAndKernelSize() {
        double sigma1 = 1.5;
        int kernelSize1 = 9;
        
        assertEquals(kernelSize1, Filters.getKernelSize(sigma1), JCV.PRECISION_MAX);
        assertEquals(sigma1, Filters.getSigma(kernelSize1), JCV.PRECISION_MAX);
        
        double sigma2 = 1.0;
        int kernelSize2 = 6;
        
        assertEquals(kernelSize2 + 1, Filters.getKernelSize(sigma2), JCV.PRECISION_MAX);
        assertEquals(sigma2, Filters.getSigma(kernelSize2), JCV.PRECISION_MAX);
    }
}
