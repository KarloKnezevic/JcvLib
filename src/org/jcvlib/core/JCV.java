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
package org.jcvlib.core;

import Jama.Matrix;

/**
 * Contains common useful constants and methods.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class JCV {

    public static final String LIB_NAME = "JcvLib";

    public static final String LIB_VER = "5.010";

    /**
     * Precision allowed by current realization for {@link Image#TYPE_64F} is <CODE>1.0</CODE>.
     */
    public static final double PRECISION_MIN = 1.0;

    /**
     * Precision allowed by current realization for {@link Image#TYPE_64F} is <CODE>10<SUP>-13</SUP></CODE>.
     */
    public static final double PRECISION_MAX = 0.000_000_000_000_1;

    /**
     * Constant to define OS Linux.
     */
    public static final String OS_LINUX = "Linux";

    /**
     * Constant to define OS Windows.
     */
    public static final String OS_WINDOWS = "Windows";

    /**
     * Constant to define unsupported OS.
     */
    public static final String OS_UNSUPPORTED = "UnsupportedOS";

    /**
     * Constant to define 32-bit architecture.
     */
    public static final String ARCH_32 = "32";

    /**
     * Constant to define 64-bit architecture.
     */
    public static final String ARCH_64 = "64";

    /**
     * Constant to define unsupported architecture.
     */
    public static final String ARCH_UNSUPPORTED = "UnsupportedArch";

    /**
     * Get constant that identify current operation system.
     *
     * @return
     *         Current operation system identifier. See <CODE>JCV.OS_*</CODE>.
     */
    public static String getOS() {
        /*
         * (non-Javadoc)
         * See:
         * * http://lopica.sourceforge.net/os.html
         */
        String os = System.getProperty("os.name");

        if (os.indexOf("Linux") >= 0) {
            return JCV.OS_LINUX;
        } else
            if (os.indexOf("Windows") >= 0) {
                return JCV.OS_WINDOWS;
            } else {
                return JCV.OS_UNSUPPORTED;
            }
    }

    /**
     * Get constant that identify current <STRONG>JVM</STRONG> architecture.
     *
     * <P>
     * It means, that on 64-bit architecture can be installed 32-bit JVM. In this case 32-bit constant will be returned.
     * It will be correct because we can run only 32-bit native code on 32-bit JCV.
     * </P>
     *
     * @return
     *         Current architecture identifier. See <CODE>JCV.ARCH_*</CODE>.
     */
    public static String getArch() {
        /*
         * (non-Javadoc)
         * See:
         * * http://lopica.sourceforge.net/os.html
         */
        String arch = System.getProperty("os.arch");

        if (arch.indexOf("64") >= 0) {
            return JCV.ARCH_64;
        } else
            if (arch.indexOf("x86") >= 0) {
                return JCV.ARCH_32;
            } else {
                return JCV.ARCH_UNSUPPORTED;
            }
    }

    /**
     * Check that current operation system is a Linux and running on supported architecture.
     *
     * @return
     *         If current operation system is <B>Linux</B> return <CODE>true</CODE> and <CODE>false</CODE> otherwise.
     */
    public static boolean isLinux() {
        return JCV.getOS().equals(JCV.OS_LINUX) && !JCV.getArch().equals(JCV.ARCH_UNSUPPORTED);
    }

    /**
     * Check that current operation system is a Windows and running on supported architecture.
     *
     * @return
     *         If current operation system is <B>Windows</B> return <CODE>true</CODE> and <CODE>false</CODE> otherwise.
     */
    public static boolean isWindows() {
        return JCV.getOS().equals(JCV.OS_WINDOWS) && !JCV.getArch().equals(JCV.ARCH_UNSUPPORTED);
    }

    /**
     * Correct compare 2 float-point numbers (with default {@link JCV#PRECISION_MAX} precision).
     *
     * <P>
     * <CODE><PRE>
     * if num1 == num2
     *      return true
     * otherwise
     *      return false
     * </PRE></CODE> Useful for compare values of 2 float-point colors.
     * </P>
     */
    public static boolean equalValues(final double num1, final double num2) {
        return JCV.equalValues(num1, num2, JCV.PRECISION_MAX);
    }

    /**
     * Correct compare 2 float-point numbers with defined precision.
     *
     * <P>
     * <CODE><PRE>
     * if num1 == num2
     *      return True
     * otherwise
     *      return False
     * </PRE></CODE> Useful for compare values of 2 float-point colors.
     * </P>
     */
    public static boolean equalValues(final double num1, final double num2, final double precision) {
        if (Math.abs(num1 - num2) <= precision) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Round value to nearest integer.
     *
     * <P>
     * For example:
     * <UL>
     * <LI><CODE>1.0 --> 1</CODE></LI>
     * <LI><CODE>1.1 --> 1</CODE></LI>
     * <LI><CODE>1.5 --> 2</CODE></LI>
     * <LI><CODE>1.9 --> 2</CODE></LI>
     * <LI><CODE>2.0 --> 2</CODE></LI>
     * </UL>
     * </P>
     */
    public static int round(final double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return Integer.MAX_VALUE;
        }
        if (value == Double.NEGATIVE_INFINITY) {
            return Integer.MIN_VALUE;
        }
        return (int) Math.round(value);
    }

    /**
     * Round value to greater integer.
     *
     * <P>
     * For example:
     * <UL>
     * <LI><CODE>1.0 --> 1</CODE></LI>
     * <LI><CODE>1.1 --> 2</CODE></LI>
     * <LI><CODE>1.5 --> 2</CODE></LI>
     * <LI><CODE>1.9 --> 2</CODE></LI>
     * <LI><CODE>2.0 --> 2</CODE></LI>
     * </UL>
     * </P>
     */
    public static int roundUp(final double value) {
        return (int) Math.ceil(value);
    }

    /**
     * Round value to less integer.
     *
     * <P>
     * For example:
     * <UL>
     * <LI><CODE>1.0 --> 1</CODE></LI>
     * <LI><CODE>1.1 --> 1</CODE></LI>
     * <LI><CODE>1.5 --> 1</CODE></LI>
     * <LI><CODE>1.9 --> 1</CODE></LI>
     * <LI><CODE>2.0 --> 2</CODE></LI>
     * </UL>
     * </P>
     */
    public static int roundDown(final double value) {
        return (int) Math.floor(value);
    }

    /**
     * Verify if given object is not <CODE>null</CODE> and generate {@link IllegalArgumentException} otherwise.
     *
     * @param obj
     *            Object to verification.
     * @param paramName
     *            Name of parameter that should be not null. Uses for generate more useful exception message.
     */
    public static void verifyIsNotNull(final Object obj, final String paramName) {
        /*
         * Verify parameters.
         */
        if (paramName == null) {
            throw new IllegalArgumentException("Parameter 'paramName' must be not null!");
        }

        /*
         * Apply checking.
         */
        if (obj == null) {
            throw new IllegalArgumentException("Parameter '" + paramName + "' must be not null!");
        }
    }

    /**
     * Verify if 2 images have same size and generate {@link IllegalArgumentException} otherwise.
     *
     * @param image1
     *            First image.
     * @param paramName1
     *            Name of first image parameter. Uses for generate more useful exception message.
     * @param image2
     *            Second image.
     * @param paramName2
     *            Name of second image parameter. Uses for generate more useful exception message.
     */
    public static void verifyIsSameSize(final Image image1, final String paramName1, final Image image2, final String paramName2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image1, "image1");
        JCV.verifyIsNotNull(paramName1, "paramName1");
        JCV.verifyIsNotNull(image2, "image2");
        JCV.verifyIsNotNull(paramName2, "paramName2");

        /*
         * Apply checking.
         */
        if (!image1.getSize().equals(image2.getSize())) {
            throw new IllegalArgumentException("Parameter '" + paramName1 + "' must have same size (= " + image1.getSize().toString()
                + ") as '" + paramName2 + "' size (= " + image2.getSize().toString() + ")!");
        }
    }

    /**
     * Verify if 2 images have same number of channels and generate {@link IllegalArgumentException} otherwise.
     *
     * @param image1
     *            First image.
     * @param paramName1
     *            Name of first image parameter. Uses for generate more useful exception message.
     * @param image2
     *            Second image.
     * @param paramName2
     *            Name of second image parameter. Uses for generate more useful exception message.
     */
    public static void verifyIsSameChannels(final Image image1, final String paramName1, final Image image2, final String paramName2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image1, "image1");
        JCV.verifyIsNotNull(paramName1, "paramName1");
        JCV.verifyIsNotNull(image2, "image2");
        JCV.verifyIsNotNull(paramName2, "paramName2");

        /*
         * Apply checking.
         */
        if (image1.getNumOfChannels() != image2.getNumOfChannels()) {
            throw new IllegalArgumentException("Parameter '" + paramName1 + "' must have same number of channels (= "
                + Integer.toString(image1.getNumOfChannels()) + ") as '" + paramName2 + "' number of channels (= "
                + Integer.toString(image2.getNumOfChannels()) + ")!");
        }
    }

    /**
     * Verify if image have defined number of channels and generate {@link IllegalArgumentException} otherwise.
     *
     * @param image
     *            Source image.
     * @param paramName
     *            Name of image parameter. Uses for generate more useful exception message.
     * @param num
     *            Required number of channels.
     */
    public static void verifyNumOfChannels(final Image image, final String paramName, final int num) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image, "image");
        JCV.verifyIsNotNull(paramName, "paramName");

        /*
         * Apply checking.
         */
        if (num <= 0) {
            throw new IllegalArgumentException("Parameter 'num' must be more than 0!");
        }
        if (image.getNumOfChannels() != num) {
            throw new IllegalArgumentException("Parameter '" + paramName + "' must have " + Integer.toString(num)
                + " channels, but it have " + Integer.toString(image.getNumOfChannels()) + "!");
        }
    }

    /**
     * Verify if 2 images have same type and generate {@link IllegalArgumentException} otherwise.
     *
     * @param image1
     *            First image.
     * @param paramName1
     *            Name of first image parameter. Uses for generate more useful exception message.
     * @param image2
     *            Second image.
     * @param paramName2
     *            Name of second image parameter. Uses for generate more useful exception message.
     */
    public static void verifyIsSameType(final Image image1, final String paramName1, final Image image2, final String paramName2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image1, "image1");
        JCV.verifyIsNotNull(paramName1, "paramName1");
        JCV.verifyIsNotNull(image2, "image2");
        JCV.verifyIsNotNull(paramName2, "paramName2");

        /*
         * Apply checking.
         */
        if (image1.getType() != image2.getType()) {
            throw new IllegalArgumentException("Parameter '" + paramName1 + "' must have same type (= "
                + Integer.toString(image1.getType()) + ") as '" + paramName2 + "' type (= " + Integer.toString(image2.getType()) + ")!");
        }
    }

    /**
     * Verify if 2 images have same size and generate {@link IllegalArgumentException} otherwise.
     *
     * @param mat1
     *            First matrix.
     * @param paramName1
     *            Name of first matrix parameter. Uses for generate more useful exception message.
     * @param mat2
     *            Second matrix.
     * @param paramName2
     *            Name of second matrix parameter. Uses for generate more useful exception message.
     */
    public static void verifyIsSameSize(final Matrix mat1, final String paramName1, final Matrix mat2, final String paramName2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(mat1, "mat1");
        JCV.verifyIsNotNull(paramName1, "paramName1");
        JCV.verifyIsNotNull(mat2, "mat2");
        JCV.verifyIsNotNull(paramName2, "paramName2");

        /*
         * Apply checking.
         */
        if (mat1.getColumnDimension() != mat2.getColumnDimension()) {
            throw new IllegalArgumentException("Parameter '" + paramName1 + "' must have same width (= "
                + Integer.toString(mat1.getColumnDimension()) + ") as '" + paramName2 + "' width (= "
                + Integer.toString(mat2.getColumnDimension()) + ")!");
        }
        if (mat1.getRowDimension() != mat2.getRowDimension()) {
            throw new IllegalArgumentException("Parameter '" + paramName1 + "' must have same height (= "
                + Integer.toString(mat1.getRowDimension()) + ") as '" + paramName2 + "' height (= "
                + Integer.toString(mat2.getRowDimension()) + ")!");
        }
    }

    /**
     * Verify that given size is odd.
     */
    public static void verifyOddSize(final Size kernelSize, final String paramName) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(kernelSize, paramName);

        /*
         * Apply checking.
         */
        if (kernelSize.getWidth() % 2 == 0) {
            throw new IllegalArgumentException("Parameter '" + paramName + ".getWidth()' must have width (= "
                + Integer.toString(kernelSize.getWidth()) + ") is odd value and be more than 0 (1, 3, 5, ...)!");
        }
        if (kernelSize.getHeight() % 2 == 0) {
            throw new IllegalArgumentException("Parameter '" + paramName + ".getHeight()' must have width (= "
                + Integer.toString(kernelSize.getHeight()) + ") is odd value and be more than 0 (1, 3, 5, ...)!");
        }
    }

    /**
     * Print matrix.
     *
     * @param M
     *            Source matrix.
     * @param mName
     *            Matrix name.
     */
    public static void printMatrix(final Matrix M, final String mName) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(M, "M");
        JCV.verifyIsNotNull(mName, "mName");

        /*
         * Perform operation.
         */
        // Form string.
        StringBuilder sb = new StringBuilder();
        sb.append(mName);
        sb.append(" = ");
        sb.append("\n");
        for (int row = 0; row < M.getRowDimension(); ++row) {
            sb.append("|");
            for (int col = 0; col < M.getColumnDimension(); ++col) {
                sb.append(M.get(row, col));
                sb.append(" ");
            }
            sb.append("|\n");
        }
        // Print result.
        System.out.println(sb.toString());
    }

    /**
     * Print matrix. Uses "M" as default matrix name.
     *
     * @param M
     *            Source matrix.
     */
    public static void printMatrix(final Matrix M) {
        JCV.printMatrix(M, "M");
    }
}
