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
package org.jcvlib.example.imageproc;

import java.io.File;
import java.io.IOException;

import org.jcvlib.core.Image;
import org.jcvlib.core.Size;

import org.jcvlib.gui.Window;
import org.jcvlib.imageproc.Filters;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use <A href="http://en.wikipedia.org/wiki/Mathematical_morphology">morphology operations</A>.
 * 
 * @version 1.006
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ErodeDilateExample {
    public static void main(String[] args) throws IOException {
        // Read source image.
        Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");
        
        // Apply morphology operations.
        Image erode = Filters.morphology(image, new Size(3, 3), Filters.MORPHOLOGY_ERODE, 2);
        Image dilate = Filters.morphology(image, new Size(3, 3), Filters.MORPHOLOGY_DILATE, 2);
        Image open = Filters.morphology(image, new Size(3, 3), Filters.MORPHOLOGY_OPEN, 2);
        Image close = Filters.morphology(image, new Size(3, 3), Filters.MORPHOLOGY_CLOSE, 2);
        
        // Output results.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(erode, "Lenna Erode");
        Window.openAndShow(dilate, "Lenna Dilate");
        Window.openAndShow(open, "Lenna Open");
        Window.openAndShow(close, "Lenna Close");
    }
}
