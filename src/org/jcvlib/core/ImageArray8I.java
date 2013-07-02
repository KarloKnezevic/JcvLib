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
package org.jcvlib.core;

/**
 * This type of arrays contains values into integer values and uses 8-bit for each value.
 *
 * @version 1.005
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageArray8I extends ImageArray {

    private final byte[] source;

    public ImageArray8I(int height, int width, int numOfChannels) {
        super(height, width, numOfChannels);

        this.source = new byte[this.getN()];
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#getUnsafe(int, int, int)
     */
    @Override
    public double getUnsafe(int x, int y, int channel) {
        return this.getUnsafe8I(x, y, channel);
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#getUnsafe8I(int, int, int)
     */
    @Override
    public int getUnsafe8I(int x, int y, int channel) {
        /*
         * (non-Javadoc)
         * See:
         * * http://stackoverflow.com/questions/4266756/can-we-make-unsigned-byte-in-java
         */
        return this.source[this.getArrayPosition(x, y, channel)] & 0xFF;
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#setUnsafe(int, int, int, double)
     */
    @Override
    public void setUnsafe(int x, int y, int channel, double value) {
        int v = JCV.round(value);
        if (v > Color.COLOR_MAX_VALUE) {
            v = JCV.roundDown(Color.COLOR_MAX_VALUE);
        }
        if (v < Color.COLOR_MIN_VALUE) {
            v = JCV.roundUp(Color.COLOR_MIN_VALUE);
        }
        this.setUnsafe8I(x, y, channel, v);
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#setUnsafe8I(int, int, int, int)
     */
    @Override
    public void setUnsafe8I(int x, int y, int channel, int value) {
        this.source[this.getArrayPosition(x, y, channel)] = (byte) value;
    }
}
