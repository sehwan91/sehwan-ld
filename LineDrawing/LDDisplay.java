package LineDrawing;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

// class to visualize the results / the inbetween steps
public class LDDisplay extends Component
{
    private BufferedImage bi;
 
    private static String testFile = "test6.jpg";
    
    public LDDisplay(BufferedImage img) {
        bi = img;
    } 
 
    public Dimension getPreferredSize() {
        return new Dimension(bi.getWidth(), bi.getHeight());
    }
 
    public void paint(Graphics g) { 
        g.drawImage(bi, 0, 0, null);
    }   

    public static void main(String[] args) {

        long startTime = System.nanoTime();

        // load image from file and convert to grayscale
        LDImage ldimg = new LDImage(testFile);

        // compute the etf from given grayscale image
        LDETF ldetf = new LDETF(ldimg);
        ldetf.computeETF(3, 3, 1);

        LDFDoG ldFDoG = new LDFDoG(1.0, 3.0, 0.99, 0.5);
        double[][] imgArray = ldFDoG.filterImage(ldetf.getETF(), ldimg.getImageArray());
        imgArray = ldFDoG.filterImage(ldetf.getETF(), imgArray);
        imgArray = ldFDoG.filterImage(ldetf.getETF(), imgArray);

        long stopTime = System.nanoTime();
        System.out.println((stopTime - startTime) / 1000000 + " ms");


        /////
        ///// Visualization code
        /////

        LDHelper helper = new LDHelper(ldimg.getW(), ldimg.getH());

        // show the normal image
        JFrame normalImage = new JFrame("Normal image");
        normalImage.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        normalImage.add(new LDDisplay(ldimg.getColorImage()));
        normalImage.pack();
        normalImage.setVisible(true);

        // show the image after sobel filtering
        JFrame sobelImage = new JFrame("Sobel operator");
        sobelImage.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        sobelImage.add(new LDDisplay(helper.conv2DArrToImage(ldetf.getMagnitudeMap(), true)));
        sobelImage.pack();
        //sobelImage.setVisible(true);
         
        // show visualization of the etf
        JFrame etfImage = new JFrame("ETF visualization");
        etfImage.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        // use helper functions to draw the etf
        // using line integral convolution                
        etfImage.add(new LDDisplay(helper.lic(ldetf.getETF())));
        etfImage.pack();
        //etfImage.setVisible(true);

       // show the final image after FDoG
       JFrame finalImage = new JFrame("Final Image");
       finalImage.addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent e) {System.exit(0);}
       });
       finalImage.add(new LDDisplay( helper.conv2DArrToImage(imgArray, false) ));
       finalImage.pack();
       finalImage.setVisible(true);
    }
}