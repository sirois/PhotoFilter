import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

public class ImageFilters {

    public static int getRed(int rgb) { return (rgb >> 16) & 0xff; }
    public static int getGreen(int rgb) { return (rgb >> 8) & 0xff; }
    public static int getBlue(int rgb) { return rgb & 0xff; }
    public static int rgbColour(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }
    public static double brightness(int rgb) {
        int r = getRed(rgb);
        int g = getGreen(rgb);
        int b = getBlue(rgb);
        return 0.21*r + 0.72*g + 0.07*b;
    }

    public static BufferedImage convertToGrayscale(BufferedImage img) {
        BufferedImage result = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB
            );
        for(int x = 0; x < img.getWidth(); x++) {
            for(int y = 0; y < img.getHeight(); y++) {
                int col = img.getRGB(x, y);
                
                int gr = (int)brightness(col);
                result.setRGB(x, y, rgbColour(gr, gr, gr));
            }
        }
        return result;
    }



    public static BufferedImage thresholdImage(BufferedImage img, int threshold) {

        BufferedImage result = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB
            );
        for(int x = 0; x < img.getWidth(); x++) {
            for(int y = 0; y < img.getHeight(); y++) {
                int col = img.getRGB(x, y);
                int gr = (int)brightness(col);
                
                if(gr > threshold){ 
                    result.setRGB(x, y, rgbColour(255, 255, 255));
                }else{
                    result.setRGB(x, y, rgbColour(0, 0, 0));
                }
            }
        }
        return result;
        
    }
    
    public static BufferedImage horizontalMirror(BufferedImage img) {
         BufferedImage result = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB
            );

            int w = img.getWidth()-1;
        for(int x = 0; x < img.getWidth(); x++) {
            for(int y = 0; y < img.getHeight(); y++) {
                  int col = img.getRGB(x,y);
                  result.setRGB(w-x, y, col);
                }
            }
        
        return result;
    }

    public static BufferedImage splitToFour(BufferedImage img) {
         BufferedImage result = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB
            );
    
        for(int x = 0; x < 2*img.getWidth(); x+=2) {
            for(int y = 0; y < 2*img.getHeight(); y+=2) {
                  int w = img.getWidth();
                  int h = img.getHeight();
                  int col = img.getRGB(x%w,y%h);
                  result.setRGB(x/2, y/2, col);
                }
            }
           
        
        return result;
    }

    public static BufferedImage imageCorrelation(BufferedImage img, double[][] mask) {
        BufferedImage result = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB
            );
        
            int w = img.getWidth()-1;  
            int h = img.getHeight()-1;
        for(int x = 0; x < img.getWidth(); x++) {
            for(int y = 0; y < img.getHeight(); y++) {
                if(x==0 || y==0 || x==w || y==h){
                 result.setRGB(x, y, rgbColour(0, 0, 0));
                }else{
                    double r =0;
                    double g =0;
                    double b=0;
                    for(int i=0;i<3;i++){
                            for(int j=0;j<3;j++){
                                r +=(double)(getRed(img.getRGB(x-1+j,y-1+i)))*mask[i][j];
                                g +=(double)(getGreen(img.getRGB(x-1+j,y-1+i)))*mask[i][j];
                                 b +=(double)(getBlue(img.getRGB(x-1+j,y-1+i)))*mask[i][j];
                            }
                        }
                         if(r>255)
                            r=255;
                         if(r<0)
                            r = 0;
                            if(g>255)
                            g=255;
                         if(g<0)
                            g = 0;
                   
                          if(b>255)
                            b=255;
                         if(b<0)
                            b = 0;
                result.setRGB(x,y,rgbColour((int)r,(int)g,(int)b));
            }//else
            }
        }
        return result;
    }
    
    public static BufferedImage rowPixelSort(BufferedImage img, int n) {

        for (int m=0;m<n;m++){
            for(int y = 0; y < img.getHeight(); y++) {
                for(int x = 1; x < img.getWidth(); x++) {
                  if(brightness(img.getRGB(x,y))<brightness(img.getRGB(x-1,y))){
                      int temp = img.getRGB(x,y);
                      img.setRGB(x,y,img.getRGB(x-1,y) );
                      img.setRGB(x-1,y,temp);
                    }
                  
                }
            }
        }  
        
        return img;
    }


    /* 
     * http://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) { return (BufferedImage) img;}
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB
            );
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }

   
    public static JPanel createPanel(Image img) {
       
        class ImagePanel extends JPanel {
            private Image img;
            public ImagePanel(Image img) {
                this.img = img;
                this.setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null)));
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, this);
            }
        }
        return new ImagePanel(img);
    }

    public static void main(String[] args) {
        Image img = Toolkit.getDefaultToolkit().getImage("img1.jpg");
        MediaTracker m = new MediaTracker(new JPanel());
        m.addImage(img, 0);
        try { m.waitForAll(); } catch(InterruptedException e) { }
        BufferedImage bimg = toBufferedImage(img); 
        JFrame f = new JFrame("CCPS 109 Lab 7");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(2, 3));
        f.add(createPanel(thresholdImage(bimg, 150)));
        f.add(createPanel(horizontalMirror(bimg)));
        f.add(createPanel(splitToFour(bimg)));
        double wt = 1.0/9;
        double[][] blur = {{wt,wt,wt},{wt,wt,wt},{wt,wt,wt}};
        f.add(createPanel(imageCorrelation(bimg, blur)));
        double[][] edged ={{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
        f.add(createPanel(imageCorrelation(convertToGrayscale(bimg), edged)));
        //double [][] sharpen = {{0,-1,0},{-1,5,-1},{0,-1,0}};
        //f.add(createPanel(imageCorrelation(bimg, sharpen)));
        f.add(createPanel(rowPixelSort(bimg, bimg.getWidth())));
        f.pack();
        f.setVisible(true); 
    }
}

