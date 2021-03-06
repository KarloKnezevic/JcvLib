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

import java.io.IOException;

/**
 * Interface for writing images to video stream of different sources (video file, network and etc.).
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public interface VideoWriter {
    /**
     * Open stream for reading video.
     */
    public void open() throws IOException;

    /**
     * Check if current stream is opened.
     */
    public boolean isOpen();

    /**
     * Add new image into video stream.
     */
    public void addImage(final Image image) throws IOException;

    /**
     * Close stream and stop reading data from it.
     */
    public void close();
}
