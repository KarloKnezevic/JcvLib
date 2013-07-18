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
package org.jcvlib.parallel;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Image;

import pro.zavodnikov.jparfor.core.JLoopI;
import pro.zavodnikov.jparfor.core.JParfor;

/**
 * Class for parallelization image processing algorithms.
 *
 * <P>
 * Example: <CODE><PRE>
 * // Image image = ...
 * JcvParallel.channels(image, new ChannelsLoopI() {
 *          {@literal @}Override
 *          public void execute(int channel) {
 *              // Do something.
 *          }
 *      });
 * </PRE></CODE> or <CODE><PRE>
 * // Image image = ...
 * JcvParallel.pixels(image, new JcvPixelsLoopI() {
 *          {@literal @}Override
 *          public void execute(int x, int y) {
 *              // Do something.
 *          }
 *      });
 * </PRE></CODE>
 * </P>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Parallel {
    /**
     * Minimal image size to parallelization by default: 100 x 100 = 10 000 elements).
     */
    public static final int MIN_SIZE_DEFAULT = 100 * 100;

    private static int currentMinSize = MIN_SIZE_DEFAULT;

    /**
     * Return minimal size for parallelization.
     */
    public static int getMinSize() {
        return currentMinSize;
    }

    /**
     * Set min size for parallelization.
     */
    public static void setMinSize(final int minSize) {
        currentMinSize = minSize;
    }

    /**
     * Return number of worker that will be used.
     */
    public static int getNumOfWorkers() {
        return JParfor.getMaxWorkers();
    }

    /**
     * Set number of workers that will be used.
     */
    public static void setNumOfWorkers(final int maxWork) {
        JParfor.setMaxWorkers(maxWork);
    }

    /**
     * Parallel processing channels of image.
     */
    public static void channels(final Image image, final ChannelsLoop runner) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(runner, "runner");

        /*
         * Perform operation.
         */
        JParfor.setMinIterations(JCV.roundUp((double) Parallel.getMinSize() / (double) image.getSize().getN()));
        JParfor.exec(image.getNumOfChannels(), new JLoopI() {
            @Override
            public void execute(final int channel, final int nThread) {
                runner.execute(channel);
            }
        });
    }

    /**
     * Parallel processing pixels of image.
     *
     * @param image
     *            Source image.
     * @param runner
     *            Object to process image on each loop step.
     */
    public static void pixels(final Image image, final PixelsLoop runner) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(runner, "runner");

        /*
         * Perform operation.
         */
        JParfor.setMinIterations(JCV.roundUp(Parallel.getMinSize() / image.getWidth()));
        JParfor.exec(image.getHeight(), new JLoopI() {
            @Override
            public void execute(final int y, final int nThread) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    runner.execute(x, y);
                }
            }
        });
    }
}
