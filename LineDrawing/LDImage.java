package LineDrawing;

import java.awt.image.*;
import java.awt.color.*;
import java.io.*;
import javax.imageio.*;

// Class for doing simple get pixel values on BufferedImage
public class LDImage {

    private BufferedImage img;
    private BufferedImage colorImg;
    private int[] imgData;
    private int w, h;

    public LDImage(String filename) {
        try {
            colorImg = ImageIO.read(new File(filename));
            w = colorImg.getWidth();
            h = colorImg.getHeight();

            // Convert to grayscale
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);  
            ColorConvertOp op = new ColorConvertOp(cs, null);  
            img = op.filter(colorImg, null);

            imgData = img.getData().getPixels(0, 0, w, h, (int[])null);
            
        } catch (IOException e) {
            System.out.println("Something went wrong with image reading");
        }
    }

    // returns the pixel value at x, y
    // simplify convolution on out of bounds pixels by returning 0
    public int get(int x, int y) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            return 0;
        }
        return imgData[(w * y) + x];
    }

    public int getW() {
        return this.w;
    }

    public int getH() {
        return this.h;
    }

    public BufferedImage getImage() {
        return img;
    }     

    public BufferedImage getColorImage() {
        return colorImg;
    }

    public double[][] getImageArray() {

        double[][] res = new double[w][h];

        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                res[i][j] = get(i, j);
            }
        }

        return res;
    }

}