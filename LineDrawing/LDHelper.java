package LineDrawing;

import java.awt.image.*;

public class LDHelper {

    private int w, h;

    public LDHelper(int w, int h){
        this.w = w;
        this.h = h;
    }

    // convert 2d pixel data array to BufferedImage
    public BufferedImage conv2DArrToImage(double[][] arr, boolean normalized) {
        
        int[] data = new int[w * h];

        for (int j = 0; j < h; ++j) {
            for (int i = 0; i < w; ++i) 
            {
                int normalizer = 1;
                if (normalized) {
                    normalizer = 255;
                }
                data[j * w + i] = (int)(arr[i][j] * normalizer);                
            }
        }

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster r = bi.getRaster();
        r.setPixels(0, 0, w, h, data);
        bi.setData(r);

        return bi;
    }    
    // follow the streamline
    double[] getNextP(double vx, double vy, double x, double y, double fx, double fy) {
        double tx, ty;
        double newX = x, newY = y, newFx = fx, newFy = fy;

        if (vx >= 0) {
            tx = (1 - fx) / vx;
        } else {
            tx = -fx / vx;
        }

        if (vy >= 0) {
            ty = (1 - fy) / vy;
        } else {
            ty = -fy / vy;
        }

        if (tx < ty) {
            if (vx > 0) {
                newX++;
                newFx = 0;
            } else {
                newX--;
                newFx = 1;
            }

            newFy += tx * vy;
        } else {
            if (vy > 0) {
                newY++;
                newFy = 0;
            } else {
                newY--;
                newFy = 1;
            }

            newFx += ty * vx;
        }

    if (newX < 0 || newY < 0 || newX >= w || newY >= h) { return null; }

        double[] res = { newX, newY, newFx, newFy};
        return res;
    }

    public BufferedImage lic(double[][][] etf) {

        double[][] noise = new double[w][h];
        double[][] res = new double[w][h];

        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                noise[i][j] = Math.ceil(Math.random() * 255);
                res[i][j] = 0;
            }
        }

        double currPX, currPY, currFX, currFY, prevX, prevY;
        int kernelLen = 10;

        double kernel[] = new double[kernelLen * 2 + 1];

        // what is this kernel?????? some sort of hanning variant
        double kernelSum = 0;

        for (int i = 0; i < kernel.length; ++i) {
            kernel[i] = Math.sin(i * Math.PI / kernel.length);
            kernelSum += kernel[i];
        }

        for (int i = 0; i < kernel.length; ++i) {
            kernel[i] /= kernelSum;
        }

        double[] next;

        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                currPX = i;
                currPY = j;
                prevX = i;
                prevY = j;
                currFX = 0.5;
                currFY = 0.5;
                res[i][j] += noise[i][j] * kernel[kernelLen];

                for (int k = 1; k <= kernelLen; ++k) {
                    next = getNextP(etf[(int)currPX][(int)currPY][0], etf[(int)currPX][(int)currPY][1], currPX, currPY, currFX, currFY);

                    if (next == null || prevX == next[0] && prevY == next[1]) {
                        break;
                    }

                    prevX = currPX;
                    prevY = currPY;
                    currPX = next[0];
                    currPY = next[1];
                    currFX = next[2];
                    currFY = next[3];

                    res[i][j] += noise[(int)currPX][(int)currPY] * kernel[kernelLen + k];
                }

                currPX = i;
                currPY = j;
                prevX = i;
                prevY = j;
                currFX = 0.5;
                currFY = 0.5;

                for (int k = kernelLen - 1; k >= 0; --k) {
                    next = getNextP(-etf[(int)currPX][(int)currPY][0], -etf[(int)currPX][(int)currPY][1], currPX, currPY, currFX, currFY);

                    if (next == null || prevX == next[0] && prevY == next[1]) {
                        break;
                    }
                    prevX = currPX;
                    prevY = currPY;
                    currPX = next[0];
                    currPY = next[1];
                    currFX = next[2];
                    currFY = next[3];

                    res[i][j] += noise[(int)currPX][(int)currPY] * kernel[k];
                }
            }
        }

        return conv2DArrToImage(res, false);
    }    

}
