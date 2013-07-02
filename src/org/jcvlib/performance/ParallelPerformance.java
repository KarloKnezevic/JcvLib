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
package org.jcvlib.performance;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.parallel.JcvParallel;
import org.jcvlib.parallel.PixelsLoop;

/**
 * Compare single-thread and multithread solution.
 * 
 * @version 1.006
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ParallelPerformance {
    private static final int channels = 4;
    
    private static double testSingleThreaded(int width, int height, int numOfIterations) {
        // Initialize.
        Image image = new Image(width, height, channels, Image.TYPE_64F, new Color(channels, Color.COLOR_MIN_VALUE));
        
        // Time catch.
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numOfIterations; ++i) {
            for (int x = 0; x < image.getWidth(); ++x) {
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                        image.set(x, y, channel, 2 * image.get(x, y, channel));
                    }
                }
            }
        }
        
        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }
    
    private static double testMultiThreaded(int width, int height, int numOfIterations) {
        // Initialize.
        final Image image = new Image(width, height, channels, Image.TYPE_64F, new Color(channels, Color.COLOR_MIN_VALUE));
        
        // Time catch.
        long startTime = System.currentTimeMillis();
        
        // Start testing.
        for (int i = 0; i < numOfIterations; ++i) {
            JcvParallel.pixels(image, new PixelsLoop() {
                @Override
                public void execute(int x, int y) {
                    for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                        image.set(x, y, channel, 2 * image.get(x, y, channel));
                    }
                }
            });
        }
        
        return (double) (System.currentTimeMillis() - startTime) / (double) numOfIterations;
    }
    
    /**
     * Run this test.
     */
    public static void main(String[] args) {
        int numOfIterations = 10;
        int[] sizes = new int[]{ 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1200, 1500, 2000, 2500, 3000, 3500, 4000, 4500 };
        
        for (int mSize : sizes) {
            System.out.println("Size " + mSize + "x" + mSize + " by " + numOfIterations + " iterations: ");
            System.out.println("    Single-thread:   " + testSingleThreaded(mSize, mSize, numOfIterations));
            System.out.println("    Multi-thread:    " + testMultiThreaded(mSize, mSize, numOfIterations));
        }
    }
}
