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
import org.jcvlib.image.Hist;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link Hist} .
 *
 * @version 1.020
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class HistTest {

    private double[] model;     // Model.

    private double[] hMatch;    // Half-match.

    private double[] mMatch;    // Mismatch

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() throws Exception {
        this.model = new double[]{ Color.COLOR_MIN_VALUE, Color.COLOR_MAX_VALUE }; // Model.
        this.hMatch = new double[]{ 127.5, 127.5 }; // Half-match.
        this.mMatch = new double[]{ Color.COLOR_MAX_VALUE, Color.COLOR_MIN_VALUE }; // Mismatch.
    }

    /**
     * Test method for: {@link Hist#calculateHistogram(Image, int)}.
     */
    @Test
    public void testCalculateHistogram() {
        Image imageCh1 = new Image(100, 100, 1, Image.TYPE_64F, new Color(1, Color.COLOR_MAX_VALUE));
        Image imageCh4 = new Image(10, 10, 4, Image.TYPE_64F, new Color(4, Color.COLOR_MAX_VALUE));

        /*
         * First histogram.
         */
        Hist histCh1 = new Hist(imageCh1);

        // Check size.
        assertEquals(imageCh1.getNumOfChannels(), histCh1.getNumOfChannels());

        // Check value.
        assertEquals(Color.COLOR_MAX_VALUE, histCh1.getChannel(0)[0], 0.0001);

        // Check if normalized.
//        double sum1 = 0.0;
//        for (int i = 0; i < histCh1.length; ++i) {
//            sum1 += histCh1[i];
//        }
//        assertEquals(Color.COLOR_MAX_VALUE, sum1, 0.0001);
//
//        /*
//         * Second histogram.
//         */
//        int size2 = 256;
//        double[] histCh4 = Hist.calculateHistogram(imageCh4, size2);
//
//        // Check size.
//        assertEquals(4 * size2, histCh4.length);
//
//        // Check if normalized.
//        double sum2 = 0.0;
//        for (int i = 0; i < histCh4.length; ++i) {
//            sum2 += histCh4[i];
//        }
//        assertEquals(Color.COLOR_MAX_VALUE, sum2, JCV.PRECISION_MAX);
//
//        // Check value.
//        assertEquals(Color.COLOR_MAX_VALUE * 0.25, histCh4[1 * size2 - 1], JCV.PRECISION_MAX);
//        assertEquals(Color.COLOR_MAX_VALUE * 0.25, histCh4[2 * size2 - 1], JCV.PRECISION_MAX);
//        assertEquals(Color.COLOR_MAX_VALUE * 0.25, histCh4[3 * size2 - 1], JCV.PRECISION_MAX);
//        assertEquals(Color.COLOR_MAX_VALUE * 0.25, histCh4[4 * size2 - 1], JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist#compareHist(double[], double[], int)}.
     */
    @Test
    public void testCorrel() {
        /*
         * See: http://easycalculation.com/statistics/learn-correlation.php
         */
        double[] H1 = new double[]{ 60.0, 61.0, 62.0, 63.0, 65.0 };
        double[] H2 = new double[]{ 3.1, 3.6, 3.8, 4.0, 4.1 };
        assertEquals(0.9119, Hist.compareHist(H1, H2, Hist.HISTOGRAM_COMPARE_CORREL), 0.0001);

        assertEquals(1.0, Hist.compareHist(this.model, this.model, Hist.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION_MAX);
        assertEquals(0.0, Hist.compareHist(this.model, this.hMatch, Hist.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION_MAX);
        assertEquals(-1.0, Hist.compareHist(this.model, this.mMatch, Hist.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist#compareHist(double[], double[], int)}.
     */
    @Test
    public void testChiSqr() {
        /*
         * See: http://www.slideshare.net/mhsgeography/chi-square-worked-example
         */
        double[] H1 = new double[]{ 10.0, 10.0, 10.0, 10.0, 10.0 };
        double[] H2 = new double[]{  4.0,  6.0, 14.0, 10.0, 16.0 };
        assertEquals(10.4 / Color.COLOR_MAX_VALUE, Hist.compareHist(H1, H2, Hist.HISTOGRAM_COMPARE_CHISQR), 0.0001);

        assertEquals(0.0,  Hist.compareHist(this.model, this.model,  Hist.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION_MAX);
        assertEquals(0.25, Hist.compareHist(this.model, this.hMatch, Hist.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION_MAX);
        assertEquals(1.0,  Hist.compareHist(this.model, this.mMatch, Hist.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist#compareHist(double[], double[], int)}.
     */
    @Test
    public void testIntersect() {
        assertEquals(1.0, Hist.compareHist(this.model, this.model,  Hist.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION_MAX);
        assertEquals(0.5, Hist.compareHist(this.model, this.hMatch, Hist.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION_MAX);
        assertEquals(0.0, Hist.compareHist(this.model, this.mMatch, Hist.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist#compareHist(double[], double[], int)}.
     */
    @Test
    public void testBhattacharyya() {
        assertEquals(0.0,  Hist.compareHist(this.model, this.model,  Hist.HISTOGRAM_COMPARE_BHATTACHARYYA), 0.01);
        assertEquals(0.55, Hist.compareHist(this.model, this.hMatch, Hist.HISTOGRAM_COMPARE_BHATTACHARYYA), 0.01);
        assertEquals(1.0,  Hist.compareHist(this.model, this.mMatch, Hist.HISTOGRAM_COMPARE_BHATTACHARYYA), 0.01);
    }
}
