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
package org.jcvlib.performance;

/**
 * Check how many digits can be saved into float and double.
 * 
 * @version 1.001
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Signs {
    
    /**
     * Run this test.
     */
    public static void main(String[] args) {
        float f = 0.12345678901234567890f;
        double d = 0.12345678901234567890;
        double m = 1.0 / 256.0;
        
        System.out.println("Float:  " + f); //  7 digits.
        System.out.println("Double: " + d); // 16 digits.
        System.out.println("Min:    " + m); //  3 digits.
    }
}
