package LineDrawing;

// class to follow streamline in flowfields 
// eg. Edge Tangent Flow
public class LDStreamTracer {
    
    private double[][][] flowField;
    private int w, h;

    private int currX, currY;
    private double vecX, vecY;
    private double currSx, currSy;
    
    private boolean backwards;
    private boolean orthogonal;

    public LDStreamTracer(double[][][] ff, int startX, int startY, boolean isBackwards, boolean isOrthogonal) {

        flowField = ff;
        w = ff.length;
        h = ff[0].length;

        currX = startX;
        currY = startY;

        currSx = 0.5;
        currSy = 0.5;

        backwards = isBackwards;
        orthogonal = isOrthogonal;
    }

    // follow the streamline and return the next pixel xy
    //  Sx, Sy refer to the "coordinates" of the streamline 
    //  intersecting with the pixel boundary
    public int[] getNextStreamPoint() {

        // normalized distance of triangle sides used to find x or y of intersection
        double nx, ny; 

        vecX = flowField[currX][currY][0];
        vecY = flowField[currX][currY][1];

        if (orthogonal) {
            double tmp = vecX;
            vecX = -vecY;
            vecY = tmp;
        }

        if (backwards) {
            vecX = -vecX;
            vecY = -vecY;
        }

        if (vecX >= 0) {
            nx = (1 - currSx) / vecX;
        } else {
            nx = -currSx / vecX;
        }

        if (vecY >= 0) {
            ny = (1 - currSy) / vecY;
        } else {
            ny = -currSy / vecY;
        }

        if (nx < ny) {
            if (vecX > 0) {
                currX++;
                currSx = 0;
            } else {
                currX--;
                currSx = 1;
            }
            currSy += nx * vecY;

        } else {
            if (vecY > 0) {
                currY++;
                currSy = 0;
            } else {
                currY--;
                currSy = 1;
            }
            currSx += ny * vecX;
        }

        if (currX < 0) { currX = 0; }
        if (currY < 0) { currY = 0; }
        if (currX >= w) { currX = w - 1; }
        if (currY >= h) { currY = h - 1; }

        int[] res = { currX, currY };
        return res;
    }
}
