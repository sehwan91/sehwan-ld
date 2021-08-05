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
    
    // used to visualize the flow field
    public BufferedImage lic(double[][][] etf) {

        double[][] noise = new double[w][h];
        double[][] res = new double[w][h];

        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                noise[i][j] = Math.ceil(Math.random() * 255);
                res[i][j] = 0;
            }
        }

        int kernelLen = 40;

        double kernel[] = new double[kernelLen * 2 + 1];

        for (int i = 0; i < kernel.length; ++i) {
            kernel[i] = 1.0 / (kernelLen * 2 + 1); // box filter
        }  

        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {

                res[i][j] += noise[i][j] * kernel[kernelLen]; // center of kernel

                LDStreamTracer tracer = new LDStreamTracer(etf, i, j, false, false);

                for (int k = 1; k <= kernelLen; ++k) {
                    int[] next = tracer.getNextStreamPoint();

                    res[i][j] += noise[next[0]][next[1]] * kernel[kernelLen + k];
                }

                tracer = new LDStreamTracer(etf, i, j, true, false);

                for (int k = kernelLen - 1; k >= 0; --k) {
                    
                    int[] next = tracer.getNextStreamPoint();

                    res[i][j] += noise[next[0]][next[1]] * kernel[k];
                    
                }
            }
        }

        return conv2DArrToImage(res, false);
    }    
}
