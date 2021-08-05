package LineDrawing;

public class LDFDoG {

    private double sigC = 1.0;
    private double sigM = 3.0;
    private double rho = 0.99;
    private double tau = 0.5;

    public LDFDoG() {
    }

    public LDFDoG(double sigC, double sigM, double rho, double tau) {
        this.sigC = sigC;
        this.sigM = sigM;
        this.rho = rho;
        this.tau = tau;
    }

    // perform FDoG filtering according to given parameters
    // on given image using given ETF
    public double[][] filterImage(double[][][] etf, double[][] img) {

        int w  = img.length, h = img[0].length;

        double[][] res = new double[w][h];

        // kernel for (6)
        // -T to T
        int lenDoG = 11;
        double[] kernelDoG = new double[1 + (lenDoG * 2)];
        
        for (int i = -lenDoG; i <= lenDoG; ++i) {
            kernelDoG[i+lenDoG] = diffOfGaussians(i);
        }
        
        // kernel for (9)
        // -S to S
        int flowLen = 11;
        double[] kernelFlow = new double[1 + (flowLen * 2)];

        for (int i = -flowLen; i <= flowLen; ++i) {
            kernelFlow[i+flowLen] = gaussian1D(sigM, i);
        }

        double sum; // H(x)

        // equation (9)
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {

                sum = filter1D(kernelDoG, lenDoG, i, j, etf, img);

                LDStreamTracer tracer = new LDStreamTracer(etf, i, j, false, false);

                for (int k = flowLen + 1; k < kernelFlow.length; ++k) {
                    int[] next = tracer.getNextStreamPoint();
                    
                    sum += filter1D(kernelDoG, lenDoG, next[0], next[1], etf, img) * kernelFlow[k];
                }

                tracer = new LDStreamTracer(etf, i, j, true, false);

                for (int k = flowLen - 1; k >= 0; --k) {
                    int[] next = tracer.getNextStreamPoint();

                    sum += filter1D(kernelDoG, lenDoG, next[0], next[1], etf, img) * kernelFlow[k];
                }

                // black and white thresholding
                // equation (10)

                res[i][j] = 255;
                if (sum < 0 && (1 + Math.tanh(sum)) < tau) {
                    res[i][j] = 0;                    
                }
            }
        }

        return res;
    }

    // equation (6)
    private double filter1D(double[] kernel, int T, int x, int y, double[][][] etf, double[][] img) {

        double sum = img[x][y] * kernel[T];

        LDStreamTracer tracer = new LDStreamTracer(etf, x, y, false, true);
        
        for (int k = T + 1; k < kernel.length; ++k) {
            int[] next = tracer.getNextStreamPoint();

            if (next == null) {
                break;
            }

            sum += img[next[0]][next[1]] * kernel[k];
        }

        tracer = new LDStreamTracer(etf, x, y, true, true);
        
        for (int k = T - 1; k >= 0; --k) {
            int[] next = tracer.getNextStreamPoint();

            if (next == null) {
                break;
            }

            sum += img[next[0]][next[1]] * kernel[k];
        }

        return sum;
    }

    // equation (7)
    private double diffOfGaussians(double t) {
        double sigS = 1.6 * sigC;

        return gaussian1D(sigC, t) - (rho * gaussian1D(sigS, t));
    }
    
    // equation (8)
    private double gaussian1D(double sig, double t) {
        return ((1 / (Math.sqrt(2 * Math.PI) * sig)) * Math.exp(-(t * t) / (2 * sig * sig) ));
    }

}
