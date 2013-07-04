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
package org.jcvlib.example.core;

import java.io.File;
import java.io.IOException;

import org.jcvlib.core.Image;
import org.jcvlib.core.Rectangle;
import org.jcvlib.gui.Window;
import org.jcvlib.image.ImageMath;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to define sub-image.
 * 
 * <P>
 * When you create sub-image you not copy data from source image to sub-image, you just create object with <STRONG>SAME SOURCES</STRONG> and
 * all changes on sub-image will be performed on source image too.
 * </P>
 * 
 * <P>
 * If you want create a new sub-image (with independent source) use {@link Image#copy()} method to copy source from defined sub-image.
 * </P>
 * 
 * @version 1.007
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class SubimageMultExample {
    public static void main(String[] args) throws IOException {
        // Read source image.
        Image sourceImage = ImageRW.read("resources" + File.separatorChar + "GreenApple.jpg");
        
        // Define sub-images.
        Image subImage1 = sourceImage.getSubimage(new Rectangle(250, 100, 100, 100));
        Image subImage2 = sourceImage.getSubimage(new Rectangle(350, 100, 100, 100)).copy();
        
        // Set some changes into sub-images.
        ImageMath.mult(subImage1, 2.0).copyTo(subImage1); // This changes will be applied onto source image.
        ImageMath.mult(subImage2, 0.5).copyTo(subImage2); // This image have different source and changes on it not applied onto source image.
        
        // Show window with images.
        Window.openAndShow(sourceImage, "Green Apple");
        Window.openAndShow(subImage1, "Sub-image 1");
        Window.openAndShow(subImage2, "Sub-image 2");
    }
}
