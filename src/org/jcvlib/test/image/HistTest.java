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

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.image.Hist;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link Hist} .
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class HistTest {

    private Hist model;     // Model.

    private Hist hMatch;    // Half-match.

    private Hist mMatch;    // Mismatch.

    private double BHATTACHARYYA_PRECISION = 0.01;

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() throws Exception {
        final Image image = new Image(2, 1, 1, Image.TYPE_8I);

        // Model.
        image.set(0, 0, 0, 255.0);
        image.set(1, 0, 0, 255.0);
        this.model = new Hist(image, 2);

        // Half-match.
        image.set(0, 0, 0, 0.0);
        image.set(1, 0, 0, 255.0);
        this.hMatch = new Hist(image, 2);

        // Mismatch.
        image.set(0, 0, 0, 0.0);
        image.set(1, 0, 0, 0.0);
        this.mMatch = new Hist(image, 2);
    }

    private void checkNorm(Hist hist) {
        double sum = 0.0;
        for (int i = 0; i < hist.getLength(); ++i) {
            sum += hist.get(i);
        }
        assertEquals(1.0, sum, JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist}.
     */
    @Test
    public void testCalculateHistogram() {
        final Image imageCh1 = new Image(100, 100, 1, Image.TYPE_64F, new Color(1, Color.COLOR_MAX_VALUE));
        final Image imageCh3 = new Image( 10,  10, 3, Image.TYPE_64F, new Color(3, Color.COLOR_MAX_VALUE));

        /*
         * First histogram.
         */
        final Hist histCh1 = new Hist(imageCh1);
        final Hist histCh3 = new Hist(imageCh3);

        // Check size.
        assertEquals(256, histCh1.getLength());
        assertEquals(256 * 256 * 256, histCh3.getLength());

        // Check normalize.
        this.checkNorm(histCh1);
        this.checkNorm(histCh3);

        // Check values.
        // Single-channel.
        for (int i = 0; i < histCh1.getLength() - 1; ++i) {
            assertEquals(0.0, histCh1.get(i), JCV.PRECISION_MAX);
        }
        assertEquals(1.0, histCh1.get(histCh1.getLength() - 1), JCV.PRECISION_MAX);
        // Multichannel.
        for (int i = 0; i < histCh3.getLength() - 1; ++i) {
            assertEquals(0.0, histCh3.get(i), JCV.PRECISION_MAX);
        }
        assertEquals(1.0, histCh3.get(histCh3.getLength() - 1), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist#compare(Hist, int)}.
     */
    @Test
    public void testCorrel() {
//        /*
//         * See: http://easycalculation.com/statistics/learn-correlation.php
//         */
//        final Hist H1 = new Hist(new double[]{ 60.0, 61.0, 62.0, 63.0, 65.0 }, 1, 1);
//        final Hist H2 = new Hist(new double[]{ 3.1, 3.6, 3.8, 4.0, 4.1 }, 1, 1);
//        assertEquals(13.9 / 15.24336, H1.compare(H2, Hist.HISTOGRAM_COMPARE_CORREL), 0.0001);

        assertEquals( 1.0, this.model.compare(this.model,  Hist.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION_MAX);
        assertEquals( 0.0, this.model.compare(this.hMatch, Hist.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION_MAX);
        assertEquals(-1.0, this.model.compare(this.mMatch, Hist.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist#compare(Hist, int)}.
     */
    @Test
    public void testChiSqr() {
//        /*
//         * See: http://www.slideshare.net/mhsgeography/chi-square-worked-example
//         */
//        final Hist H1 = new Hist(new double[]{ 10.0, 10.0, 10.0, 10.0, 10.0 }, 1, 1);
//        final Hist H2 = new Hist(new double[]{  4.0,  6.0, 14.0, 10.0, 16.0 }, 1, 1);
//        assertEquals(10.4, H1.compare(H2, Hist.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION_MAX);

        assertEquals(0.0,  this.model.compare(this.model,  Hist.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION_MAX);
        assertEquals(0.25, this.model.compare(this.hMatch, Hist.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION_MAX);
        assertEquals(1.0,  this.model.compare(this.mMatch, Hist.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist#compare(Hist, int)}.
     */
    @Test
    public void testIntersect() {
        assertEquals(1.0, this.model.compare(this.model,  Hist.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION_MAX);
        assertEquals(0.5, this.model.compare(this.hMatch, Hist.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION_MAX);
        assertEquals(0.0, this.model.compare(this.mMatch, Hist.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION_MAX);
    }

    /**
     * Test method for: {@link Hist#compare(Hist, int)}.
     */
    @Test
    public void testBhattacharyya() {
        assertEquals(0.0,  this.model.compare(this.model,  Hist.HISTOGRAM_COMPARE_BHATTACHARYYA), JCV.PRECISION_MAX);
        assertEquals(0.55, this.model.compare(this.hMatch, Hist.HISTOGRAM_COMPARE_BHATTACHARYYA), BHATTACHARYYA_PRECISION);
        assertEquals(1.0,  this.model.compare(this.mMatch, Hist.HISTOGRAM_COMPARE_BHATTACHARYYA), JCV.PRECISION_MAX);
    }
}
