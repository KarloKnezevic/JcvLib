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
import org.jcvlib.core.Size;
import org.jcvlib.gui.Window;
import org.jcvlib.image.Geom;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use geometry transformations.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class GeometryExample {
    public static void main(String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Resize image.
        final Image reflectD = Geom.reflect(image, Geom.REFLECT_DIAGONAL);
        final Image resize = Geom.resize(image, new Size(800, 800));
        final Image rotate45 = Geom.rotate(image, 45.0);

        // Show windows with images.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(reflectD, "Lenna diagonal");
        Window.openAndShow(resize, "Lenna resized");
        Window.openAndShow(rotate45, "Lenna rotate 45");

    }
}
