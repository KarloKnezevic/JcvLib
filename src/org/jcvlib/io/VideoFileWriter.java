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
package org.jcvlib.io;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Image;
import org.jcvlib.core.Size;
import org.jcvlib.core.VideoWriter;
import org.jcvlib.image.TypeConvert;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * This class allow write video to disk.
 *
 * <P>
 * Video is a stream of images. Because of that we should have a object that will be save state to correct writing.
 * </P>
 *
 * <P>
 * Example of usage:<CODE><PRE>
 * ...
 * // String filePath = ...
 * // Size sizeOfVideo = ...
 * // int FPS = ...
 * VideoFileWriter video = new VideoFileWriter(filePath, sizeOfVideo, FPS);
 * video.open();
 * ...
 * // Image image = ...
 * ...
 * video.addImage(image);
 * ...
 * video.close();
 * ...
 * </PRE></CODE>
 * </P>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
/*
 * (non-Javadoc)
 * Based on examples from:
 * * https://github.com/xuggle/xuggle-xuggler/blob/master/src/com/xuggle/xuggler/demos/CaptureScreenToFile.java
 * * http://wiki.xuggle.com/Encoding_Video_from_a_sequence_of_Images
 * * http://stackoverflow.com/questions/9727590/what-codecs-does-xuggler-support
 */
public class VideoFileWriter implements VideoWriter {
    /**
     * Object for reading video from file.
     */
    private final IContainer outContainer;

    /**
     * Object for reading video from file.
     */
    private final IStreamCoder outStreamCoder;

    /**
     * Variable to show that device is opened.
     */
    private boolean isOpen;

    private long totalTimeStamp = 0;

    private long incrementTimeStamp;

    /**
     * Create new object for write video into the file.
     *
     * @param filePath
     *            Path to file for output.
     * @param sizeOfVideo
     *            Size of video for saving.
     * @param FPS
     *            <A href="http://en.wikipedia.org/wiki/Frame_rate">Frame rate per second</A> in Hz.
     * @param numPicturesInGroupOfPictures
     *            Determines the number of interframes between each keyframe.
     * @param bitRate
     *            Bit rate of video in Kbit per second (kbps). You can use 32 kbps, 64 kbps, 128 kbps, 256 kbps and so on.
     *            The relationship between the bit rate and the size of your video is
     *            also not quite linear. I've found that setting the bit rate too low actually increases
     *            the size of the video. Why this is I don't know, but just as a precaution you might want
     *            to play with the bit rate a little bit.
     */
    public VideoFileWriter(final String filePath, final Size sizeOfVideo, final int FPS, final int numPicturesInGroupOfPictures,
        final int bitRate) throws IOException {

        this.outContainer = IContainer.make();

        JCV.verifyIsNotNull(filePath, "filePath");
        int retval = this.outContainer.open(filePath, IContainer.Type.WRITE, null);
        if (retval < 0) {
            throw new IOException("Could not open output file!");
        }

        ICodec codec = ICodec.guessEncodingCodec(null, null, filePath, null, ICodec.Type.CODEC_TYPE_VIDEO);
        if (codec == null) {
            throw new IOException("Could not guess a codec!");
        }

        IStream outStream = this.outContainer.addNewStream(codec);
        this.outStreamCoder = outStream.getStreamCoder();

        this.outStreamCoder.setNumPicturesInGroupOfPictures(numPicturesInGroupOfPictures);

        this.outStreamCoder.setBitRate(1000 * bitRate);

        this.outStreamCoder.setPixelType(IPixelFormat.Type.YUV420P);

        JCV.verifyIsNotNull(sizeOfVideo, "sizeOfVideo");
        this.outStreamCoder.setWidth(sizeOfVideo.getWidth());
        this.outStreamCoder.setHeight(sizeOfVideo.getHeight());

        this.outStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
        this.outStreamCoder.setGlobalQuality(0); // Sets the video encoding to the highest quality.

        if (FPS < 0) {
            throw new IllegalArgumentException("Parameter 'FPS' must be more than 0!");
        }
        IRational frameRate = IRational.make(FPS, 1); // Number of images per second of your end result video.
        this.incrementTimeStamp = Math.round(1000000.0 / (long) FPS);
        this.outStreamCoder.setFrameRate(frameRate);
        this.outStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));

        retval = this.outStreamCoder.open(null, null);
        if (retval < 0) {
            throw new IOException("Could not open input decoder!");
        }

        retval = this.outContainer.writeHeader();
        if (retval < 0) {
            throw new IOException("Could not write file header!");
        }

        this.isOpen = false;
    }

    /**
     * Create new object for write video into the file.
     * Using 30 <A href="http://en.wikipedia.org/wiki/Frame_rate">FPS</A>, 10 pictures in group of pictures and 128 kbps
     * as default value.
     *
     * @param filePath
     *            Path to file for output.
     * @param sizeOfImageIntoVideo
     *            Size of image (width and height) into video.
     */
    public VideoFileWriter(final String filePath, final Size sizeOfImageIntoVideo) throws IOException {
        this(filePath, sizeOfImageIntoVideo, 30, 10, 128);
    }

    /**
     * Open file for writing.
     */
    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.VideoWriter#open()
     */
    @Override
    public void open() {
        this.isOpen = true;
    }

    /**
     * Check if current device is opened.
     */
    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.VideoWriter#isOpen()
     */
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * Add new image into video stream.
     */
    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.VideoWriter#addImage(org.jcvlib.core.Image)
     */
    @Override
    public void addImage(final Image image) throws IOException {
        if (!this.isOpen()) {
            throw new IOException("Current object does not opened yet! Please call #open() method!");
        }

        BufferedImage worksWithXugglerBufferedImage = convertToType(TypeConvert.toBufferedImage(image), BufferedImage.TYPE_3BYTE_BGR);
        IPacket packet = IPacket.make();

        IConverter converter = null;
        try {
            converter = ConverterFactory.createConverter(worksWithXugglerBufferedImage, IPixelFormat.Type.YUV420P);
        } catch (UnsupportedOperationException e) {
            throw new IOException(e.getMessage());
        }

        this.totalTimeStamp += this.incrementTimeStamp;
        IVideoPicture outFrame = converter.toPicture(worksWithXugglerBufferedImage, this.totalTimeStamp);
        outFrame.setQuality(0);
        int retval = this.outStreamCoder.encodeVideo(packet, outFrame, 0);
        if (retval < 0) {
            throw new IOException("Could not encode video!");
        }

        if (packet.isComplete()) {
            retval = this.outContainer.writePacket(packet);
            if (retval < 0) {
                throw new IOException("Could not save packet to container!");
            }
        }
    }

    /**
     * Convert a {@link BufferedImage} of any type, to {@link BufferedImage} of a specified type.
     * If the source image is the same type as the target type, then original image is returned,
     * otherwise new image of the correct type is created and the content of the source image is
     * copied into the new image.
     *
     * @param sourceImage
     *            Image to be converted.
     * @param targetType
     *            BufferedImage type.
     *
     * @return
     *         BufferedImage of the specified target type.
     */
    private BufferedImage convertToType(final BufferedImage sourceImage, final int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            // If the source image is already the target type, return the source image.
            image = sourceImage;
        } else {
            // Otherwise create a new image of the target type and draw the new image.
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

    /**
     * Close out the file we're currently working on.
     */
    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.VideoWriter#close()
     */
    @Override
    public void close() {
        int retval = this.outContainer.writeTrailer();

        if (retval < 0) {
            throw new RuntimeException("Could not write trailer to output file!");
        }

        this.isOpen = false;
    }
}
