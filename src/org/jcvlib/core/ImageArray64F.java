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
 * This type of image arrays contains values into float-point values and uses 64-bit for each value.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageArray64F extends ImageArray {

    private final double[] source;

    public ImageArray64F(final int width, final int height, final int numOfChannels) {
        super(width, height, numOfChannels);

        this.source = new double[this.getN()];
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#getUnsafe(int, int, int)
     */
    @Override
    public double getUnsafe(final int x, final int y, final int channel) {
        return this.source[this.getArrayPosition(x, y, channel)];
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#getUnsafe8I(int, int, int)
     */
    @Override
    public int getUnsafe8I(final int x, final int y, final int channel) {
        return JCV.round(this.getUnsafe(x, y, channel));
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#setUnsafe(int, int, int, double)
     */
    @Override
    public void setUnsafe(final int x, final int y, final int channel, final double value) {
        this.source[this.getArrayPosition(x, y, channel)] = value;
    }

    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.ImageArray#setUnsafe8I(int, int, int, int)
     */
    @Override
    public void setUnsafe8I(final int x, final int y, final int channel, final int value) {
        this.setUnsafe(x, y, channel, value);
    }
}
