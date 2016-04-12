package ch09.io;

// 6. The BMP file format for uncompressed image files is well documented and simple.
// Using random access, write a program that reflects each row of pixels in place,
// without writing a new file.

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Ch0906BMP {

  public static void flipHorizontally(BufferedImage img) {
    int w = img.getWidth();
    int h = img.getHeight();
    for (int i = 0; i < w/2; i++)
      for (int j = 0; j < h; j++) {
        // for a given j swap i with w - i - 1 if i < w/2
        int tmp = img.getRGB(i, j);
        img.setRGB(i, j, img.getRGB(w - i - 1, j));
        img.setRGB(w - i - 1, j, tmp);
      }

    // uncomment to write flipped image
    //    try {
    //      ImageIO.write(img, "bmp", new File("bmp24bit-fliph.bmp"));
    //    } catch (IOException e) {
    //      e.printStackTrace();
    //    }
  }

  public static void main(String[] args) {

    BufferedImage img = null;
    try {
      img = ImageIO.read(new File("bmp24bit.bmp"));
    } catch (IOException e) {}

    flipHorizontally(img);

  }

}
