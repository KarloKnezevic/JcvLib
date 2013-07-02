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
package org.jcvlib.io;

import java.io.IOException;

import org.jcvlib.core.JCV;
import org.jcvlib.core.Image;
import org.jcvlib.core.Size;
import org.jcvlib.core.VideoReader;

import org.jcvlib.imageproc.TypeConvert;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IMetaData;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

/**
 * This class allow get images from web-cameras.
 *
 * <P>
 * Example of usage:<CODE><PRE>
 * ...
 * int numberOfWebCam = 0; // First web-camera.
 * int FPS = 30;
 * Size sizeOfGettingImage = new Size(320, 240);
 * // Initialize web-camera.
 * WebCamReader webCam = new WebCamReader(numberOfWebCam, FPS, sizeOfGettingImage);
 * ...
 * Image image = null;
 * webCam.open();
 * if (webCam.isOpen()) {
 *      image = webCam.getImage();
 *      ...
 * }
 * ...
 * webCam.close();
 * ...
 * </PRE></CODE>
 * </P>
 *
 * @version 1.017
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
/*
 * Based on examples from:
 * * https://github.com/xuggle/xuggle-xuggler/blob/master/src/com/xuggle/xuggler/demos/DisplayWebcamVideo.java
 */
public class WebCamReader implements VideoReader, Runnable {
    /**
     * Tread for reading images from web-camera.
     */
    private Thread t;

    /**
     * Object for reading video from web-cameras.
     */
    private IContainer container;

    /**
     * Objects for reading video from web-cameras.
     */
    private IStreamCoder videoCoder;

    /**
     * Name of OS-specific driver to read data from web-camera.
     */
    private String driverName;

    /**
     * Define path to web-camera.
     */
    private String deviceName;

    /**
     * Define number of device.
     */
    private int numWebCam;

    /**
     * Variable to show that device is opened.
     */
    private boolean isOpen;

    /**
     * Error message.
     */
    private String errorMessage;

    /**
     * FPS (Frame Per Second).
     */
    private int FPS;

    /**
     * Size of getting frame from web-camera.
     */
    private Size size;

    /**
     * Common image buffer.
     */
    private IVideoPicture bufImg;

    /**
     * Open first web-camera (number 0) with 30 FPS and 320x240 picture size.
     */
    public WebCamReader() {
        this(0);
    }

    /**
     * Open web-camera with <CODE>30</CODE> FPS and <CODE>320x240</CODE> picture size.
     *
     * @param numWebCam
     *            Number of web-camera to open.
     */
    public WebCamReader(int numWebCam) {
        this(numWebCam, 30, new Size(320, 240));
    }

    /**
     * Open web-camera with defined web-camera number, <A href="http://en.wikipedia.org/wiki/Frame_rate">FPS</A> and
     * size of getting image.
     *
     * @param numWebCam
     *            Number of web-camera to open.
     * @param FPS
     *            <A href="http://en.wikipedia.org/wiki/Frame_rate">FPS (Frames Per Second)</A>.
     * @param sizeOfImage
     *            Size of getting image
     */
    public WebCamReader(int numWebCam, int FPS, Size sizeOfImage) {
        if (numWebCam < 0) {
            throw new IllegalArgumentException("Parameter 'numWebCam' (=" + Integer.toString(numWebCam) + ") must be more or equals 0!");
        }
        this.numWebCam = numWebCam;

        if (FPS < 0) {
            throw new IllegalArgumentException("Parameter 'FPS' (=" + Integer.toString(FPS) + ") must be more or equals 0!");
        }
        this.FPS = FPS;

        JCV.verifyIsNotNull(sizeOfImage, "size");
        this.size = sizeOfImage;

        if (JCV.isLinux()) {
            this.driverName = "video4linux2";
            this.deviceName = "/dev/video" + Integer.toString(this.numWebCam);
        } else
            if (JCV.isWindows()) {
                this.driverName = "vfwcap";
                this.deviceName = Integer.toString(numWebCam);
            } else {
                throw new RuntimeException("Current operation system or architecture is not supported!");
            }

        this.bufImg = null;
    }

    private void generateError(String errorMessage) throws IOException {
        this.errorMessage = errorMessage;
        this.checkErrors();
    }

    private void checkErrors() throws IOException {
        if (this.errorMessage != null) {
            this.close();
            throw new IOException(this.errorMessage);
        }
    }

    /**
     * Open current device.
     */
    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.VideoReader#open()
     */
    @Override
    public void open() throws IOException {
        int maxWaitTime = 10000; // In milliseconds (10^{-3} seconds).
        int waitStep = 10; // In milliseconds (10^{-3} seconds).
        int timeCounter;

        // Reopened if needed.
        if (this.isOpen()) {
            this.close();

            // Wait while video thread will be closed.
            timeCounter = 0;
            while (this.bufImg == null && timeCounter < maxWaitTime) {
                // Wait closed camera.
                try {
                    Thread.sleep(waitStep);
                } catch (InterruptedException e) {
                    this.generateError(e.getMessage());
                }

                // Calculate waiting time.
                timeCounter += waitStep;
            }

            // Generate an error!
            if (this.bufImg == null) {
                this.generateError("Can not closed camera that was opened!");
            }
        }

        try {
            this.t = new Thread(this);
            this.t.start();

            // Wait while web-camera device is opened.
            this.isOpen = true;
            timeCounter = 0;
            while (this.bufImg == null && this.isOpen() && timeCounter < maxWaitTime) {
                // Wait other thread.
                try {
                    Thread.sleep(waitStep);
                } catch (InterruptedException e) {
                    this.generateError(e.getMessage());
                }

                // Calculate waiting time.
                timeCounter += waitStep;
            }
        } catch (RuntimeException e) {
            this.generateError(e.getMessage());
        }

        // Generate an error!
        if (this.bufImg == null && this.isOpen()) {
            this.generateError("Can not open camera!");
        }
    }

    /**
     * Check if current device is opened.
     */
    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.VideoReader#isOpen()
     */
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * Run thread to read data from web-camera.
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        try {
            /*
             * 1. Let's make sure that we can actually convert video pixel formats.
             */
            if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
                throw new RuntimeException("You must install the GPL version of Xuggler (with 'IVideoResampler' support)!");
            }

            /*
             * 2. Create a Xuggler container object.
             */
            this.container = IContainer.make();

            /*
             * 3. Tell Xuggler about the device format.
             */
            IContainerFormat format = IContainerFormat.make();
            if (format.setInputFormat(this.driverName) < 0) {
                throw new RuntimeException("Couldn't open web-camera device: " + this.driverName);
            }

            /*
             * 4. Configure device.
             *
             * Devices, unlike most files, need to have parameters set in order for Xuggler
             * to know how to configure them, for a webcam, these parameters make sense.
             */
            IMetaData params = IMetaData.make();
            params.setValue("framerate", Integer.toString(this.FPS) + "/1");
            params.setValue("video_size", Integer.toString(this.size.getWidth()) + "x" + Integer.toString(this.size.getHeight()));

            /*
             * 5. Open up the container.
             */
            int retval = this.container.open(this.deviceName, IContainer.Type.READ, format, false, true, params, null);
            if (retval < 0) {
                /*
                 * This little trick converts the non friendly integer return value into a
                 * slightly more friendly object to get a human-readable error name.
                 */
                this.close();

                IError error = IError.make(retval);
                throw new RuntimeException("Could not open file: " + this.deviceName + "; Error: " + error.getDescription());
            }

            /*
             * 6. Find first video stream.
             */
            // Query how many streams the call to open found.
            int numStreams = this.container.getNumStreams();
            // Iterate through the streams to find the first video stream.
            int videoStreamId = -1;
            this.videoCoder = null;
            for (int i = 0; i < numStreams; i++) {
                // Find the stream object.
                IStream stream = this.container.getStream(i);

                // Get the pre-configured decoder that can decode this stream.
                IStreamCoder coder = stream.getStreamCoder();
                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                    videoStreamId = i;
                    this.videoCoder = coder;
                    break;
                }
            }

            /*
             * 7. Verify video stream.
             */
            // Check if we found video stream.
            if (videoStreamId == -1) {
                throw new RuntimeException("Could not find video stream in container: " + deviceName);
            }
            // Try to open up our decoder so it can do work.
            if (this.videoCoder.open() < 0) {
                throw new RuntimeException("Could not open video decoder for container: " + deviceName);
            }

            /*
             * 8. Check color scheme. If needed create color scheme convertor.
             */
            IVideoResampler resampler = null;
            if (this.videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                // If this stream is not in BGR24, we're going to need to convert it.
                resampler =
                    IVideoResampler.make(this.videoCoder.getWidth(), this.videoCoder.getHeight(), IPixelFormat.Type.BGR24,
                        this.videoCoder.getWidth(), this.videoCoder.getHeight(), this.videoCoder.getPixelType());
                if (resampler == null) {
                    throw new RuntimeException("Could not create color space resampler for: " + deviceName);
                }
            }

            /*
             * 9. Now, we start walking through the container looking at each packet.
             */
            IPacket packet = IPacket.make();
            while (this.container.readNextPacket(packet) >= 0 && this.isOpen) {
                /*
                 * 10. Check the packet: if it belongs to our video stream.
                 */
                if (packet.getStreamIndex() == videoStreamId) {
                    /*
                     * 11. We allocate a new picture to get the data out of Xuggler.
                     */
                    IVideoPicture picture =
                        IVideoPicture.make(this.videoCoder.getPixelType(), this.videoCoder.getWidth(), this.videoCoder.getHeight());
                    int offset = 0;
                    while (offset < packet.getSize()) {
                        /*
                         * 12. Decode the video, checking for any errors.
                         */
                        int bytesDecoded = this.videoCoder.decodeVideo(picture, packet, offset);
                        if (bytesDecoded < 0) {
                            throw new RuntimeException("Got error decoding video in: " + deviceName);
                        }

                        offset += bytesDecoded;
                    }

                    /*
                     * 13. Check the current picture.
                     *
                     * Some decoders will consume data in a packet, but will not be able to
                     * construct a full video picture yet. Therefore you should always check
                     * if you got a complete picture from the decoder.
                     */
                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        /*
                         * 14. Convert color packet into BGR24 color scheme if needed.
                         *
                         * If the resampler is not null, that means we didn't get the video
                         * in BGR24 format and need to convert it into BGR24 format.
                         */
                        if (resampler != null) {
                            newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
                            if (resampler.resample(newPic, picture) < 0) {
                                throw new RuntimeException("Could not resample video from: " + deviceName);
                            }
                        }

                        /*
                         * 15. Check color scheme.
                         */
                        if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                            throw new RuntimeException("Could not decode video as BGR 24 bit data in: " + deviceName);
                        }

                        /*
                         * Copy link to created object.
                         */
                        this.bufImg = newPic;
                    }
                }
            }
        } catch (RuntimeException e) {
            this.errorMessage = e.getMessage();
        }

        // Current stream is closed.
        this.isOpen = false;

        /*
         * 17. Close.
         *
         * Technically since we're exiting anyway, these will be cleaned up by the garbage collector...
         * But because we're nice people and want to be invited places for Christmas, we're going to
         * show how to clean up.
         */
        if (this.container != null) {
            this.container.close();
            this.container = null;
        }

        if (this.videoCoder != null) {
            this.videoCoder.close();
            this.videoCoder = null;
        }
    }

    /**
     * Get current image from web-camera.
     */
    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.VideoReader#getImage()
     */
    @SuppressWarnings("deprecation")
    @Override
    public Image getImage() throws IOException {
        this.checkErrors();

        /*
         * 16. Convert the BGR24 to BufferedImage and then to Image.
         */
        return TypeConvert.fromBufferedImage(Utils.videoPictureToImage(this.bufImg));
    }

    /**
     * Return size of image that get from web-camera.
     */
    public Size getSize() {
        return this.size;
    }

    /**
     * Close current device and stop reading data from it.
     */
    /*
     * (non-Javadoc)
     * @see org.jcvlib.core.VideoReader#close()
     */
    @Override
    public void close() {
        this.isOpen = false;
    }
}
