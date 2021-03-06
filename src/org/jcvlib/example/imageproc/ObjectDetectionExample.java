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
import org.jcvlib.image.Hist;
import org.jcvlib.image.ObjectDetect;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how used object detection operators.
 *
 * <P>
 * <STRONG>Attention! Extremely slow method! Use small images (resize big images) or another methods!</STRONG>
 * </P>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ObjectDetectionExample {
    public static void main(String[] args) throws IOException {
        // Read image.
        final Image source = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Create template for search.
        final Image template = source.getSubimage(320, 260, 35, 25);

        // Euclid compare.
        final Image mapEuclid = ObjectDetect.matchTempleteEuclid(source, template);

        // Histogram compare.
        final Image mapHist = ObjectDetect.matchTempleteHist(source, template, Hist.HISTOGRAM_COMPARE_CORREL);

        // Show window with images.
        Window.openAndShow(source, "Lenna");
        Window.openAndShow(template, "Template");
        Window.openAndShow(mapEuclid, "Map Euclid");
        Window.openAndShow(mapHist, "Map Hist");
    }
}
