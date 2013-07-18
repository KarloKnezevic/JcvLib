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
package org.jcvlib.core;

/**
 * This type of arrays contains values into integer values and uses 8-bit for each value.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageArray8I extends ImageArray {

    private final byte[] source;

    public ImageArray8I(final int height, final int width, final int numOfChannels) {
        super(height, width, numOfChannels);

        this.source = new byte[this.getN()];
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#getUnsafe(int, int, int)
     */
    @Override
    public double getUnsafe(final int x, final int y, final int channel) {
        return this.getUnsafe8I(x, y, channel);
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#getUnsafe8I(int, int, int)
     */
    @Override
    public int getUnsafe8I(final int x, final int y, final int channel) {
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
    public void setUnsafe(final int x, final int y, final int channel, final double value) {
        this.setUnsafe8I(x, y, channel, JCV.round(value));
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#setUnsafe8I(int, int, int, int)
     */
    @Override
    public void setUnsafe8I(final int x, final int y, final int channel, final int value) {
        this.source[this.getArrayPosition(x, y, channel)] = (byte) value;
    }
}
