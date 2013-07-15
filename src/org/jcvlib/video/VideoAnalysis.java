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
package org.jcvlib.video;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.image.Misc;
import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

/**
 * This class contains methods for <A href="http://en.wikipedia.org/wiki/Motion_analysis">Motion analysis</A> and
 * <A href="http://en.wikipedia.org/wiki/Video_tracking">Video tracking</A>.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class VideoAnalysis {
    /**
     * Update <A href="http://www.cse.ohio-state.edu/~jwdavis/CVL/Research/MHI/mhi.html">motion history image</A> by silhouette or object
     * mask.
     *
     * @param history
     *            Motion history image.
     * @param mask
     *            Silhouette or object mask.
     * @param outOfDate
     *            Out of date coefficients on motion history image. Should be in diapasone <CODE>[0.0, 255.0]</CODE>.
     */
    public static void updateHistoryImage(final Image history, final Image mask, final double outOfDate) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(history, "history", mask, "mask");

        if (outOfDate < Color.COLOR_MIN_VALUE || outOfDate > Color.COLOR_MAX_VALUE) {
            throw new IllegalArgumentException("Parameter 'outOfDate' have incorrect value! Use values from diapasone [0.0, 255.0]!");
        }

        /*
         * Perform operation.
         */
        // Out of date current values.
        final Image proxyHistory = history;
        Parallel.pixels(proxyHistory, new PixelsLoop() {
            @Override
            public void execute(final int x, final int y) {
                for (int channel = 0; channel < proxyHistory.getNumOfChannels(); ++channel) {
                    proxyHistory.set(x, y, channel, proxyHistory.get(x, y, channel) - outOfDate);
                }
            }
        });

        // Add new image to history.
        Misc.sum(history, mask).copyTo(history);
    }
}
