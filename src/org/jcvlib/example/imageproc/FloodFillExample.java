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

import org.jcvlib.core.Color;
import org.jcvlib.core.Region;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.gui.Window;
import org.jcvlib.image.Misc;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to using function to <A href="http://en.wikipedia.org/wiki/Flood_fill">fill</A> some region.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class FloodFillExample {
    public static void main(String[] args) throws IOException {
        // Read images.
        final Image imageMan = ImageRW.read("resources" + File.separatorChar + "Man.bmp");
        final Image imageLenna = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Fill region by green color.
        final Color color = new Color(new double[]{ 0, 255, 0 });
        // Seed
        final Point seed = new Point(0, 0);
        // Distance between colors.
        final double dist = 6.0;

        // Apply flood fill algorithm.
        final Region statMan =
                        Misc.floodFill(imageMan, seed, dist, color, Misc.DIRECTIONS_TYPE_8, Misc.FLOOD_FILL_RANGE_NEIGHBOR);
        final Region statLenna =
                        Misc.floodFill(imageLenna, seed, dist, color, Misc.DIRECTIONS_TYPE_8, Misc.FLOOD_FILL_RANGE_NEIGHBOR);

        // Output statistics.
        System.out.println("Man:");
        System.out.println("    fill pixels: " + statMan.getAreaSize());
        System.out.println("    fill rect:   " + statMan.getBoundingRect().toString());
        System.out.println("    center mass: " + statMan.getCentroid().toString());

        System.out.println("Lenna:");
        System.out.println("    fill pixels: " + statLenna.getAreaSize());
        System.out.println("    fill rect:   " + statLenna.getBoundingRect().toString());
        System.out.println("    center mass: " + statLenna.getCentroid().toString());

        // Show window with image after fill region.
        Window.openAndShow(imageMan, "Man with fill region");
        Window.openAndShow(imageLenna, "Lenna with fill region");
    }
}
