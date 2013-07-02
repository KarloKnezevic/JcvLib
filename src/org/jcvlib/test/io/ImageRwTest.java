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
package org.jcvlib.test.io;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.imageproc.TypeConvert;
import org.jcvlib.io.ImageRW;

import org.junit.Test;

/**
 * Test {@link ImageRW}.
 * 
 * @version 1.011
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageRwTest {
    /**
     * Test method for: {@link ImageRW#write(Image, String)}.
     */
    @Test
    public void testBMP() {
        writeAndReadTest(init(1500, 1200, 3), "BMP");
    }
    
    /**
     * Test method for: {@link ImageRW#write(Image, String)}.
     */
    @Test
    public void testJPG() {
        try {
            String imagePath = "Test.jpg";
            Image image = init(1500, 1200, 4);
            
            ImageRW.write(image, imagePath);
            BufferedImage bufImg = TypeConvert.toBufferedImage(image);
            ImageIO.write(bufImg, "jpg", new File(imagePath));
            
            Image newImage = ImageRW.read(imagePath);
            BufferedImage bufImgNew = ImageIO.read(new File(imagePath));
            
            assertTrue(newImage.equals(TypeConvert.fromBufferedImage(bufImgNew)));
            
            // Remove temp file.
            (new File(imagePath)).delete();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    /**
     * Test method for: {@link ImageRW#write(Image, String)}.
     */
    @Test
    public void testPNG() {
        writeAndReadTest(init(1500, 1200, 4), "PNG");
    }
    
    /**
     * Create and initialize new {@link JcvImage64F}.
     */
    private static Image init(int width, int height, int numOfChannels) {
        Image image = new Image(width, height, numOfChannels, Image.TYPE_8I);
        
        boolean wasMax = false;
        
        double value = Color.COLOR_MIN_VALUE;
        for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    image.set(x, y, channel, value);
                    
                    value += 1.0; // We can write only integer values!
                    if (value > Color.COLOR_MAX_VALUE) {
                        value = Color.COLOR_MIN_VALUE;
                        wasMax = true;
                    }
                }
            }
        }
        
        assertTrue(wasMax);
        
        return image;
    }
    
    /**
     * Write, read and compare images.
     * 
     * @param fileExtension
     *            File extension for temporary saving.
     */
    private static void writeAndReadTest(Image image, String fileExtension) {
        try {
            String imagePath = "Test." + fileExtension;
            ImageRW.write(image, imagePath);
            Image newImage = ImageRW.read(imagePath);
            
            assertTrue(image.equals(newImage));
            
            // Remove temp file.
            (new File(imagePath)).delete();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
