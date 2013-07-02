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
import org.jcvlib.gui.Window;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to work with image channels.
 * 
 * <P>
 * Images represents as multilayer arrays. Standard image is 3 (<A href="http://en.wikipedia.org/wiki/RGB">RGB</A> -- Read, Green and Blue)
 * or 4 (<A href="http://en.wikipedia.org/wiki/RGB">RGB</A> + <A href="http://en.wikipedia.org/wiki/Alpha_compositing">Alpha</A>) channels.
 * </P>
 * 
 * @version 1.006
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ChannelsExample {
    public static void main(String[] args) throws IOException {
        // Read image.
        Image image = ImageRW.read("resources" + File.separatorChar + "RGB.png");
        Window.openAndShow(image, "Original RGB Image");
        
        // Get layer -- new image.
        Image imageRedChannel = image.getChannel(0);
        Window.openAndShow(imageRedChannel, "Only Red Channel");
    }
}
