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
package org.jcvlib.image;

import org.jcvlib.core.Point;
import org.jcvlib.core.Rectangle;

/**
 * This structure contains some useful statistics about filled field. This is immutable object -- you can not change
 * values of it after creation.
 * 
 * @version 1.007
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class FloodFillStruct {
    
    private int totalFillPixels;
    
    private Rectangle fillRect;
    
    private Point centerMass;
    
    public FloodFillStruct(int totalFillPixels, Rectangle fillRect, Point centerOfGravity) {
        this.totalFillPixels = totalFillPixels;
        this.fillRect = fillRect;
        this.centerMass = centerOfGravity;
    }
    
    /**
     * Return number of pixels that was filled.
     */
    public int getTotalFillPixels() {
        return this.totalFillPixels;
    }
    
    /**
     * Return rectangle with filled field.
     */
    public Rectangle getFillRect() {
        return this.fillRect;
    }
    
    /**
     * Return <A href="http://en.wikipedia.org/wiki/Center_of_mass">Center of mass</A> for filled field.
     */
    public Point getCenterMass() {
        return this.centerMass;
    }
}
