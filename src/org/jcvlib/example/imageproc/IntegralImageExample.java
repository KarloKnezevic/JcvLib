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
import org.jcvlib.gui.Window;
import org.jcvlib.image.Misc;
import org.jcvlib.io.ImageRW;

/**
 * This is example show integral images for 2 different images.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class IntegralImageExample {
    public static void main(String[] args) throws IOException {
        // Read images.
        final Image image1 = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");
        final Image image2 = ImageRW.read("resources" + File.separatorChar + "GreenApple.jpg");

        // Calculate integral images.
        final Image integral1 = Misc.integralImage(image1);
        final Image integral2 = Misc.integralImage(image2);

        // Show window with images.
        Window.openAndShow(image1, "Lenna");
        Window.openAndShow(integral1, "Lenna Integral");
        Window.openAndShow(image2, "Green Apple");
        Window.openAndShow(integral2, "Green Apple Integral");
    }
}
