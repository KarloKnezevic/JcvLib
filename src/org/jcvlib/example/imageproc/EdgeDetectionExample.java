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
import org.jcvlib.image.ColorConvert;
import org.jcvlib.image.Filters;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use <A href="http://en.wikipedia.org/wiki/Edge_detection">edge detection</A> operators.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class EdgeDetectionExample {
    public static void main(String[] args) throws IOException {
        // Read source image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Convert from RGB color image to Gray-scale color image.
        final Image gray = ColorConvert.fromRGBtoGray(image.getLayer(0, 3));

        // We just copy existing image to output results from other methods.
        final Image roberts = Filters.edgeDetection(gray, Filters.EDGE_DETECT_ROBERTS);
        final Image prewitt = Filters.edgeDetection(gray, Filters.EDGE_DETECT_PREWITT);
        final Image sobel = Filters.edgeDetection(gray, Filters.EDGE_DETECT_SOBEL);
        final Image scharr = Filters.edgeDetection(gray, Filters.EDGE_DETECT_SCHARR);

        // We can apply threshold to existing images with detected edges.
        final Image sobelT = Filters.threshold(sobel, 96.0, Filters.THRESHOLD_BINARY);

        // Output results.
        Window.openAndShow(gray, "Lenna Gary");
        Window.openAndShow(roberts, "Lenna Roberts");
        Window.openAndShow(prewitt, "Lenna Prewitt");
        Window.openAndShow(sobel, "Lenna Sobel");
        Window.openAndShow(scharr, "Lenna Scharr");
        Window.openAndShow(sobelT, "Lenna Sobel Threshold");
    }
}
