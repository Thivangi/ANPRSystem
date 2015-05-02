/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaanpr.Imageproperties;

/**
 *
 * @author Godfather
 */
 import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * @version $Id: ImageUtils.java 587751 2007-10-24 02:41:36Z vgritsenko $
 */
final public class ImageUtils {



    final public static ImageProperties getJpegProperties(File file) throws FileNotFoundException, IOException{
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));

            // check for "magic" header
            byte[] buf = new byte[2];
            int count = in.read(buf, 0, 2);
            if (count < 2) {
                throw new RuntimeException("Not a valid Jpeg file!");
            }
            if ((buf[0]) != (byte) 0xFF || (buf[1]) != (byte) 0xD8) {
                throw new RuntimeException("Not a valid Jpeg file!");
            }

            int width = 0;
            int height = 0;
            char[] comment = null;

            boolean hasDims = false;
            boolean hasComment = false;
            int ch = 0;

            while (ch != 0xDA && !(hasDims && hasComment)) {
                /* Find next marker (JPEG markers begin with 0xFF) */
                while (ch != 0xFF) {
                    ch = in.read();
                }
                /* JPEG markers can be padded with unlimited 0xFF's */
                while (ch == 0xFF) {
                    ch = in.read();
                }
                /* Now, ch contains the value of the marker. */

                int length = 256 * in.read();
                length += in.read();
                if (length < 2) {
                    throw new RuntimeException("Not a valid Jpeg file!");
                }
                /* Now, length contains the length of the marker. */

                if (ch >= 0xC0 && ch <= 0xC3) {
                    in.read();
                    height = 256 * in.read();
                    height += in.read();
                    width = 256 * in.read();
                    width += in.read();
                    for (int foo = 0; foo < length - 2 - 5; foo++) {
                        in.read();
                    }
                    hasDims = true;
                }
                else if (ch == 0xFE) {
                    // that's the comment marker
                    comment = new char[length-2];
                    for (int foo = 0; foo < length - 2; foo++)
                        comment[foo] = (char) in.read();
                    hasComment = true;
                }
                else {
                    // just skip marker
                    for (int foo = 0; foo < length - 2; foo++) {
                        in.read();
                    }
                }
            }
            return (new ImageProperties(width, height, comment, "jpeg"));

        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                }
            }
        }
    }

    final public static ImageProperties getGifProperties(File file) throws FileNotFoundException, IOException{
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            byte[] buf = new byte[10];
            int count = in.read(buf, 0, 10);
            if (count < 10) {
                throw new RuntimeException("Not a valid Gif file!");
            }
            if ((buf[0]) != (byte) 'G' || (buf[1]) != (byte) 'I' || (buf[2]) != (byte) 'F') {
                throw new RuntimeException("Not a valid Gif file!");
            }

            int w1 = (buf[6] & 0xff) | (buf[6] & 0x80);
            int w2 = (buf[7] & 0xff) | (buf[7] & 0x80);
            int h1 = (buf[8] & 0xff) | (buf[8] & 0x80);
            int h2 = (buf[9] & 0xff) | (buf[9] & 0x80);

            int width = w1 + (w2 << 8);
            int height = h1 + (h2 << 8);

            return (new ImageProperties(width, height, null,"gif"));

        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                }
            }
        }
    }
}
    

