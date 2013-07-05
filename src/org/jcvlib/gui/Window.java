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
package org.jcvlib.gui;

import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.image.TypeConvert;
import org.jcvlib.io.ImageRW;

/**
 * Base class for all GUI functions.
 *
 * <P>
 * Example of usage: <CODE><PRE>
 * // Image image = ...
 * Window window = new Window("My Image");
 * window.show(image);
 * ...
 * // window.close();   // Needed if you want close window from your program.
 * </PRE></CODE> or just: <CODE><PRE>
 * // Image image = ...
 * Window.openAndShow(image, "My Image");
 * </PRE></CODE>
 * </P>
 *
 * @version 1.013
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
/*
 * (non-Javadoc)
 * See:
 * * http://introcs.cs.princeton.edu/java/stdlib/Picture.java.html
 */
public final class Window {
    /**
     * Contain links to all windows.
     */
    private static ArrayList<Window> allWindows = new ArrayList<Window>();

    /**
     * Define char of last pressed keys in all windows.
     */
    private static int allLastPressedKeyChar;

    /**
     * Frame of window.
     */
    private JFrame frame;

    /**
     * Component with image.
     */
    private ImageComponent imageComponent;

    /**
     * Image to show.
     */
    private Image image;

    /**
     * Title of window.
     */
    private String title;

    /**
     * Define if current window is opened.
     */
    private boolean wasOpened;

    /**
     * Define char of last pressed keys in current window.
     */
    private int pressedKeyChar;

    /**
     * Mouse position.
     */
    private Point mousePos;

    /**
     * Simplest way show image. As name of image will be used hash code of given image.
     */
    public static void openAndShow(Image image) {
        Window.openAndShow(image, null);
    }

    /**
     * Simplest way show image.
     */
    public static void openAndShow(Image image, String title) {
        Window localWind = new Window(title);
        localWind.show(image);
    }

    /**
     * Create new window with defined size. As name of image will be used hash code of given image.
     */
    public Window() {
        this(null);
    }

    /**
     * Create new window with defined size and given name of this window.
     */
    @SuppressWarnings("static-access")
    public Window(String title) {
        this.wasOpened = false;
        this.title = title;

        // Add link to global list.
        synchronized (this.allWindows) {
            this.allWindows.add(this);
        }
    }

    /**
     * Set current window is visible.
     */
    private void open() {
        this.wasOpened = true;

        // Create the GUI for viewing the image if needed.
        this.frame = new JFrame();

        // Allow change size of window.
        this.frame.setResizable(true);

        // Add listener for correct close windows.
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close(); // Close current window.
            }
        });

        // Create listener for close all open windows on press <Esc> button.
        this.frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent event) {
                // Do nothing.
            }
            @Override
            public void keyReleased(KeyEvent event) {
                setKeyChar(event.getKeyChar());

                // Listener for close all windows by press 'Esc' button.
                if (getLastPressedKeyChar() == KeyEvent.VK_ESCAPE) {
                    closeAll();
                }
            }
            @Override
            public void keyTyped(KeyEvent event) {
                // Do nothing.
            }
        });

        // Set title to window.
        if (this.title == null) {
            this.title = Integer.toString(this.image.hashCode());
        }
        this.frame.setTitle(this.title);

        // Create panel with image.
        this.imageComponent = new ImageComponent(TypeConvert.toBufferedImage(this.image));
        JScrollPane panel = new JScrollPane(this.imageComponent);
        this.frame.add(panel);

        // Create menu bar.
        JMenuBar menuBar = new JMenuBar();
        this.frame.setJMenuBar(menuBar);

        this.createFileMenu(menuBar);
        this.createResizeMenu(menuBar);

        // Create status bar.
        this.createStatusBar();

        // Set objects onto canvas.
        this.frame.pack();
        this.frame.setVisible(true);
    }

    private void setKeyChar(int keyChar) {
        this.pressedKeyChar = keyChar;
        allLastPressedKeyChar = keyChar;
    }

    /**
     * Show or update image in current window.
     */
    public void show(Image image) {
        this.image = image;

        // Create window if needed.
        if (!this.wasOpened) {
            this.open();
        }

        this.imageComponent.setBufferedImage(TypeConvert.toBufferedImage(this.image));
        this.frame.repaint();
    }

    /**
     * Close current window if it was opened. Same as click on close window button.
     */
    public void close() {
        this.wasOpened = false;

        /*
         * (non-Javadoc)
         * See:
         * * http://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
         */
        this.frame.dispose();
    }

    /**
     * Close all opened windows. Same as press <STRONG>Esc</STRONG> key.
     */
    public static synchronized void closeAll() {
        if (allWindows != null) {
            for (Window window : allWindows) {
                window.close();
            }
        }
    }

    /**
     * Return last key char of pressed in current window.
     */
    public int getPressedKeyChar() {
        return this.pressedKeyChar;
    }

    /**
     * Return mouse point on image.
     */
    public Point getMousePoint() {
        return this.mousePos;
    }

    /**
     * Return last key char of last pressed key in some opened window in current application.
     *
     * <P>
     * If opened only one window result will be same as {@link #getPressedKeyChar()}.
     * </P>
     */
    public static int getLastPressedKeyChar() {
        return allLastPressedKeyChar;
    }

    private void createFileMenu(JMenuBar menuBar) {
        // Create menu 'File'.
        JMenu fileMenu = new JMenu(" File   ");
        menuBar.add(fileMenu);

        /*
         * Add menu 'Save...' menu item.
         */
        JMenuItem saveMenuItem = new JMenuItem(" Save...    ");
        fileMenu.add(saveMenuItem);

        saveMenuItem.addActionListener(new ActionListener() {
            /*
             * Opens a save dialog box when the user selects "Save As..." from the menu.
             */
            @Override
            public void actionPerformed(ActionEvent action) {
                FileDialog chooser = new FileDialog(frame, "Select path and name to save file", FileDialog.SAVE);

                chooser.setVisible(true);
                if (chooser.getFile() != null) {
                    try {
                        // Verify file name: name should include file name and extension.
                        String fileName;
                        String fileFormat;

                        if (chooser.getFile().lastIndexOf('.') >= 0) {
                            fileName =
                                chooser.getFile().substring(chooser.getFile().lastIndexOf(File.separatorChar) + 1,
                                    chooser.getFile().lastIndexOf('.'));
                            fileFormat = chooser.getFile().substring(chooser.getFile().lastIndexOf('.') + 1).toLowerCase();
                        } else {
                            fileName = chooser.getFile();
                            // File format is not specified -- PNG is default.
                            fileFormat = "png";
                            System.out.println("You not specified file format. By default, will be use PNG format.");
                        }

                        // Set new name title of window according to file name to saving image.
                        frame.setTitle(fileName);

                        // Write image to file.
                        String fullFilePath = chooser.getDirectory() + File.separator + fileName + "." + fileFormat;
                        ImageRW.write(image, fullFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Can not save image: " + e.getMessage());
                    }
                }
            }
        });
        // Add hot-key <Ctrl>+<S> to call 'File' -> 'Save...' menu item.
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        /*
         * Add menu 'Quit...' menu item.
         */
        JMenuItem quitMenuItem = new JMenuItem(" Quit       ");
        fileMenu.add(quitMenuItem);

        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                /*
                 * (non-Javadoc)
                 * See:
                 * * http://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
                 */
                frame.dispose();
            }
        });
        // Add hot-key <Ctrl>+<Q> to call 'File' -> 'Quite' menu item.
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    private void createResizeMenu(JMenuBar menuBar) {
        // Create menu 'Resize'.
        JMenu resizeMenu = new JMenu(" Resize ");
        menuBar.add(resizeMenu);

        /*
         * Add menu 'Zoom +' menu item.
         */
        JMenuItem zoomPlusMenuItem = new JMenuItem(" Zoom +     ");
        resizeMenu.add(zoomPlusMenuItem);

        zoomPlusMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                imageComponent.setScale(imageComponent.getScale() + 0.1f);
            }
        });
        // Add hot-key <Ctrl>+<+> to call 'Resize' -> 'Zoom +' menu item.
        zoomPlusMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        /*
         * Add menu 'Zoom --' menu item.
         */
        JMenuItem zoomMinusMenuItem = new JMenuItem(" Zoom --    ");
        resizeMenu.add(zoomMinusMenuItem);

        zoomMinusMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                imageComponent.setScale(imageComponent.getScale() - 0.1f);
            }
        });
        // Add hot-key <Ctrl>+<-> to call 'Resize' -> 'Zoom --' menu item.
        zoomMinusMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        /*
         * Add menu 'Zoom 0' menu item.
         */
        JMenuItem zoomDefaultMenuItem = new JMenuItem(" Zoom 0      ");
        resizeMenu.add(zoomDefaultMenuItem);

        zoomDefaultMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                imageComponent.setDefaultScale();
            }
        });
        // Add hot-key <Ctrl>+<0> to call 'Resize' -> 'Zoom 0' menu item.
        zoomDefaultMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    private void createStatusBar() {
        final StatusBar statusBar = new StatusBar();
        this.frame.add(statusBar, java.awt.BorderLayout.SOUTH);
        final String baseMsg =
            "Size: [" + Integer.toString(image.getWidth()) + "x" + Integer.toString(image.getHeight()) + "]    Ch: "
                + Integer.toString(image.getNumOfChannels());
        // Set status bar message.
        statusBar.setMessage(baseMsg);
        this.imageComponent.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent event) {
                // Do nothing.
                System.out.println(event.getX() + ", " + event.getY());
            }
            @Override
            public void mouseMoved(MouseEvent event) {
                // Get mouse position.
                int x = event.getX();
                int y = event.getY();

                // Create output info.
                if ((x < image.getWidth()) && (y < image.getHeight())) {
                    mousePos = new Point(x, y);
                    StringBuilder sb = new StringBuilder();

                    // Output position.
                    sb.append("    Point");
                    sb.append(mousePos.toString());

                    // Get color value.
                    sb.append(" = Color");
                    sb.append(image.get(mousePos).toString());

                    // Update status bar message.
                    statusBar.setMessage(baseMsg + sb.toString());
                }
            }
        });
    }
}
