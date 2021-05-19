package LineDrawing;

public class LDETF {
    public double[][][] etf;   // current etf
    public double[][] gmag;    // gradient magnitude map

    // adjustable parameters
    private int ksize = 3;
    private double eta = 1;

    private LDImage img;

    public LDETF(LDImage ldimg) {
        img = ldimg;

        etf = new double[img.getW()][img.getH()][2];
        gmag = new double[img.getW()][img.getH()];
    }

    // normalize vector components such that vector magnitude = 1
    private void normalizeETF() {
        double maxMag = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < img.getW(); ++i) 
        {
            for (int j = 0; j < img.getH(); ++j)
            {
                double gradX = etf[i][j][0];
                double gradY = etf[i][j][1];
                double gradMag = Math.sqrt((gradX * gradX) + (gradY * gradY));
                if (gradMag > maxMag) {
                    maxMag = gradMag;
                }
            }
        }

        for (int i = 0; i < img.getW(); ++i) 
        {
            for (int j = 0; j < img.getH(); ++j)
            {
                etf[i][j][0] /= maxMag;
                etf[i][j][1] /= maxMag;
            }
        }
    }

    // initialize etf by applying sobel operator to get initial gradient vectors
    // populates the gradient vector map and the gradient magnitude map,
    // normalized to max (0 - 1)
    private void initGradientAndETF() {

        double maxMag = 0;
        // first pass: compute gradient vector map and gradient magnitude map
        // also compute initial etf
        for (int i = 0; i < img.getW(); ++i) 
        {
            for (int j = 0; j < img.getH(); ++j)
            {
                // sobel
                double gradX = img.get(i - 1, j - 1) + 2 * img.get(i - 1, j) + img.get(i - 1, j + 1) 
                               - img.get(i + 1, j - 1) - 2 * img.get(i + 1, j) - img.get(i + 1, j + 1);                

                double gradY = img.get(i - 1, j - 1) + 2 * img.get(i, j - 1) + img.get(i + 1, j - 1) 
                               - img.get(i - 1, j + 1) - 2 * img.get(i, j + 1) - img.get(i + 1, j + 1);

                // init etf by rotating gradient vector 90 degrees ccw
                etf[i][j][0] = -gradY;
                etf[i][j][1] = gradX;                
                
                double gradMag = Math.sqrt((gradX * gradX) + (gradY * gradY));

                // pre-compute max gradient magnitude
                if (gradMag > maxMag) {
                    maxMag = gradMag;
                }

                gmag[i][j] = gradMag;               
            }
        }

        // second pass: create initial etf and normalize gradient magnitude map
        for (int i = 0; i < img.getW(); ++i) 
        {
            for (int j = 0; j < img.getH(); ++j)
            {
                // normalize gradient magnitude map
                gmag[i][j] /= maxMag;
            }
        }

        // normalize the etf before use!
        normalizeETF();
    }

    // cx cy : center pixel xy
    // rx ry : neighbor pixel xy
    private int ws(int cx, int cy, int rx, int ry) {
        int dx = cx - rx;
        int dy = cy - ry;

        if (Math.sqrt((dx * dx) + (dy * dy)) >= ksize) {
            return 0;
        }

        return 1;
    }

    //// Equation 1 component equations

    private double wm(int cx, int cy, int rx, int ry) {
        return 0.5 * (1 +  Math.tanh(eta * (gmag[rx][ry] - gmag[cx][cy])));
    }

    private double dot(int cx, int cy, int rx, int ry) {
        return (etf[cx][cy][0] * etf[rx][ry][0]) + (etf[cx][cy][1] * etf[rx][ry][1]);
    }

    private double wd(int cx, int cy, int rx, int ry) {
        return Math.abs(dot(cx, cy, rx, ry));
    }

    private int phi(int cx, int cy, int rx, int ry) {
        if (dot(cx, cy, rx, ry) > 0) {
            return 1;
        }
        return -1;
    }

    // iterate on existing etf
    // t0x -> t1x, etc
    private void iterateETF() {
        double[][][] newETF = new double[img.getW()][img.getH()][2];

        for (int i = 0; i < img.getW(); ++i) 
            for (int j = 0; j < img.getH(); ++j) 
            {
                double tNewX = 0;
                double tNewY = 0;

                // ETF construction filter... painful runtime
                for (int x = i - ksize; x < i + ksize + 1; ++x) {
                    for (int y = j - ksize; y < j + ksize + 1; ++y) {
                    
                        if (x < 0 || x >= img.getW() || y < 0 || y >= img.getH()) {
                            continue;
                        }

                        int ws = ws(i, j, x, y);
                        double wm = wm(i, j, x, y);
                        double wd = wd(i, j, x, y);
                        int phi = phi(i, j, x, y);

                        tNewX +=  phi * etf[x][y][0] * ws * wm * wd;
                        tNewY +=  phi * etf[x][y][1] * ws * wm * wd;
                    }
                }

                newETF[i][j][0] = tNewX;
                newETF[i][j][1] = tNewY;
            }        

        etf = newETF;
        normalizeETF();
    }

    // call this function to compute the etf
    // specify iterations, filter kernel size, eta
    public void computeETF(int iterations, int ksize, double eta) {

        this.ksize = ksize;
        this.eta = eta;

        initGradientAndETF();

        for (int i = 0; i < iterations; ++i)
        {
            iterateETF();
        }
    }

    // write return functions for the etf
}
