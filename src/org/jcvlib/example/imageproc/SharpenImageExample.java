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
package org.jcvlib.example.imageproc;

import java.io.File;
import java.io.IOException;

import org.jcvlib.core.Image;
import org.jcvlib.gui.Window;
import org.jcvlib.image.Filters;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use sharpen filters.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class SharpenImageExample {
    public static void main(String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Sharpen using Laplacian method.
        final Image laplacianSharpen = Filters.sharpen(image, Filters.SHARPEN_LAPLACIAN);

        // Modern sharpen method using special matrix.
        final Image modernSharpen = Filters.sharpen(image, Filters.SHARPEN_MODERN);

        // Show window with images.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(laplacianSharpen, "Lenna Laplacian Sharpen");
        Window.openAndShow(modernSharpen, "Lenna Modern Sharpen");
    }
}
