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
package org.jcvlib.example.core;

import org.jcvlib.core.Image;
import org.jcvlib.gui.Window;

/**
 * This is example show how to get and set pixels values on images.
 *
 * <P>
 * There are 2 types of images:
 * <OL>
 * <LI>{@link Image#TYPE_8I} that uses 8-bit integer values for each channel in interval <CODE>0..255</CODE></LI>
 * <LI>{@link Image#TYPE_64F} that uses 64-bit float-point values for each channel in interval <CODE>[0.0, 0.255]</CODE></LI>
 * </OL>
 * Both this classes implements interface {@link Image}.
 * </P>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class AccessToPixelExample {
    public static void main(String[] args) {
        // Create empty image with one channel.
        final Image image = new Image(400, 400, 1, Image.TYPE_8I);

        // Set values into image.
        for (int x = 50; x < 350; ++x) {
            for (int y = 50; y < 350; ++y) {
                image.set8I(x, y, 0, 63);
            }
        }
        for (int x = 100; x < 300; ++x) {
            for (int y = 100; y < 300; ++y) {
                image.set8I(x, y, 0, 127);
            }
        }
        for (int x = 150; x < 250; ++x) {
            for (int y = 150; y < 250; ++y) {
                image.set8I(x, y, 0, 255);
            }
        }

        // Get values into image.
        System.out.println("Brightness of first  sqaure: " + image.get(10, 10, 0));
        System.out.println("Brightness of second sqaure: " + image.get(110, 110, 0));
        System.out.println("Brightness of third  sqaure: " + image.get(210, 210, 0));

        // Show windows with images.
        Window.openAndShow(image, "White squares");
    }
}
