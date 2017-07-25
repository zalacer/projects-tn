package graph;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import javax.imageio.ImageIO;

// https://rosettacode.org/wiki/Bitmap/Flood_fill#Java
 
public class FloodFill {
  public void floodFill(BufferedImage image, Point node, Color targetColor, Color replacementColor) {
    int width = image.getWidth();
    int height = image.getHeight();
    int target = targetColor.getRGB();
    int replacement = replacementColor.getRGB();
    if (target != replacement) {
      Deque<Point> queue = new LinkedList<Point>();
      do {
        int x = node.x;
        int y = node.y;
        while (x > 0 && image.getRGB(x - 1, y) == target) {
          x--;
        }
        boolean spanUp = false;
        boolean spanDown = false;
        while (x < width && image.getRGB(x, y) == target) {
          image.setRGB(x, y, replacement);
          if (!spanUp && y > 0 && image.getRGB(x, y - 1) == target) {
            queue.add(new Point(x, y - 1));
            spanUp = true;
          } else if (spanUp && y > 0 && image.getRGB(x, y - 1) != target) {
            spanUp = false;
          }
          if (!spanDown && y < height - 1 && image.getRGB(x, y + 1) == target) {
            queue.add(new Point(x, y + 1));
            spanDown = true;
          } else if (spanDown && y < height - 1 && image.getRGB(x, y + 1) != target) {
            spanDown = false;
          }
          x++;
        }
      } while ((node = queue.pollFirst()) != null);
    }
  }
  
  public static void main(String[] args) throws IOException {
    
    BufferedImage image = ImageIO.read(new File("Unfilledcirc.png"));
    new FloodFill().floodFill(image, new Point(50, 50), Color.WHITE, Color.RED);
    ImageIO.write(image, "png", new File("Redfilledcirc.png"));
    
  }

}
