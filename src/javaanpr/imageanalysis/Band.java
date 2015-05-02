/*
------------------------------------------------------------------------
JavaANPR - Automatic Number Plate Recognition System for Java
------------------------------------------------------------------------

This file is a part of the JavaANPR, licensed under the terms of the
Educational Community License

Copyright (c) 2006-2007 Ondrej Martinsky. All rights reserved

This Original Work, including software, source code, documents, or
other related items, is being provided by the copyright holder(s)
subject to the terms of the Educational Community License. By
obtaining, using and/or copying this Original Work, you agree that you
have read, understand, and will comply with the following terms and
conditions of the Educational Community License:

Permission to use, copy, modify, merge, publish, distribute, and
sublicense this Original Work and its documentation, with or without
modification, for any purpose, and without fee or royalty to the
copyright holder(s) is hereby granted, provided that you include the
following on ALL copies of the Original Work or portions thereof,
including modifications or derivatives, that you make:

# The full text of the Educational Community License in a location
viewable to users of the redistributed or derivative work.

# Any pre-existing intellectual property disclaimers, notices, or terms
and conditions.

# Notice of any changes or modifications to the Original Work,
including the date the changes were made.

# Any modifications of the Original Work must be distributed in such a
manner as to avoid any confusion with the Original Work of the
copyright holders.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

The name and trademarks of copyright holder(s) may NOT be used in
advertising or publicity pertaining to the Original or Derivative Works
without specific, written prior permission. Title to copyright in the
Original Work and any associated documentation will at all times remain
with the copyright holders. 

If you want to alter upon this work, you MUST attribute it in 
a) all source files
b) on every place, where is the copyright of derivated work
exactly by the following label :

---- label begin ----
This work is a derivate of the JavaANPR. JavaANPR is a intellectual 
property of Ondrej Martinsky. Please visit http://javaanpr.sourceforge.net 
for more info about JavaANPR. 
----  label end  ----

------------------------------------------------------------------------
                                         http://javaanpr.sourceforge.net
------------------------------------------------------------------------
*/


package javaanpr.imageanalysis;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javaanpr.intelligence.Intelligence;
import javax.imageio.ImageIO;
//import javaanpr.configurator.Configurator;

public class Band extends Photo {
    static public Graph.ProbabilityDistributor distributor = new Graph.ProbabilityDistributor(0,0,25,25);
    static private int numberOfCandidates = Intelligence.configurator.getIntProperty("intelligence_numberOfPlates");
    private int x,y;
    private BandGraph graphHandle = null;
    
    /** Creates a new instance of Band */
    public Band() {
        image = null;
    }
    
    public Band(BufferedImage bi) {
        super(bi);
    }
    public Band(BufferedImage bi,int xin, int yin){
         super(bi);
         this.x = xin;
         this.y = yin;
     }
    
    public int gety(){
        return y;
    }
    
    public BufferedImage renderGraph() {
        this.computeGraph();
        return graphHandle.renderHorizontally(this.getWidth(), 100);
    }
    
    private Vector<Graph.Peak> computeGraph() {
        if (graphHandle != null) return graphHandle.peaks; // chart has already been calculated
        BufferedImage imageCopy = duplicateBufferedImage(this.image);
        fullEdgeDetector(imageCopy);
        graphHandle = histogram(imageCopy);
        graphHandle.rankFilter(image.getHeight());
        graphHandle.applyProbabilityDistributor(distributor);
        graphHandle.findPeaks(numberOfCandidates);
        return graphHandle.peaks;
    }
    
    public Vector<Plate> getPlates() {
        Vector<Plate> out = new Vector<Plate>();
        
        Vector<Graph.Peak> peaks = computeGraph();
        for (int i=0; i<peaks.size(); i++) {
            // cuts from original ! image sign , and store in a vector . ATTENTION !!!!!! Die cut from the original , so
            // The coordinates of the calculated imagecopy we must apply inverse transformation
            Graph.Peak p = peaks.elementAt(i);
            out.add(new Plate(
                    image.getSubimage(  p.getLeft()  , 0  , p.getDiff()  , image.getHeight()  ),  p.getLeft()  , 0 ,
                     p.getDiff()  , image.getHeight())

                     );
            /*if(i==0)
            {
                System.out.println("p.getLeftplate(0) = "+p.getLeft());
                System.out.println(" p.getDiffplate(0)= "+ p.getDiff());
                System.out.println("image.getHeightplate(0)= "+image.getHeight());
           try { 
    // retrieve image
    BufferedImage bi =image.getSubimage(  p.getLeft()  , 0  , p.getDiff()  , image.getHeight()  );
    File outputfile = new File("C:\\Users\\Godfather\\Desktop\\ANPRimages\\getPlates\\saved25.jpg");
    ImageIO.write(bi, "jpg", outputfile);
         }
 catch (IOException e) {
        }
            }
            if(i==1)
            {
                System.out.println("p.getLeftplate(1) = "+p.getLeft());
                System.out.println(" p.getDiff(plate1)= "+ p.getDiff());
                System.out.println("image.getHeightplate(1)= "+image.getHeight());
           try { 
    // retrieve image
   BufferedImage bi =image.getSubimage(  p.getLeft()  , 0  , p.getDiff()  , image.getHeight()  );
    File outputfile = new File("C:\\Users\\Godfather\\Desktop\\ANPRimages\\getPlates\\saved26.jpg");
    ImageIO.write(bi, "jpg", outputfile);
         }
 catch (IOException e) {
        }
        
}
            if(i==2)
            {
                System.out.println("p.getLeftplate(2) = "+p.getLeft());
                System.out.println(" p.getDiffplate(2)= "+ p.getDiff());
                 System.out.println("image.getHeightplate(2)= "+image.getHeight());
           try { 
    // retrieve image
    BufferedImage bi =image.getSubimage(  p.getLeft()  , 0  , p.getDiff()  , image.getHeight()  );
    File outputfile = new File("C:\\Users\\Godfather\\Desktop\\ANPRimages\\getPlates\\saved27.jpg");
    ImageIO.write(bi, "jpg", outputfile);
         }
 catch (IOException e) {
        }
        }
        
       try {
    // retrieve image
   BufferedImage bi =image;
    File outputfile = new File("C:\\Users\\Godfather\\Desktop\\saved16.jpg");
    ImageIO.write(bi, "jpg", outputfile);
         }
 catch (IOException e) {
}*/
        }
        return out;
    }
    
//    public void horizontalRankBi(BufferedImage image) {
//        BufferedImage imageCopy = duplicateBi(image);
//        
//        float data[] = new float[image.getHeight()];
//        for (int i=0; i<data.length; i++) data[i] = 1.0f/data.length;
//        
//        new ConvolveOp(new Kernel(data.length,1, data), ConvolveOp.EDGE_NO_OP, null).filter(imageCopy, image);
//    }
    
    public BandGraph histogram(BufferedImage bi) {
        BandGraph graph = new BandGraph(this);
        for (int x=0; x<bi.getWidth(); x++) {
            float counter = 0;
            for (int y=0; y<bi.getHeight();y++)
                counter += getBrightness(bi,x,y);
            graph.addPeak(counter);
   
        }
        return graph;
    }
    
   public void fullEdgeDetector(BufferedImage source) {
        float verticalMatrix[] = {
            -1,0,1,
            -2,0,2,
            -1,0,1,
        };
        float horizontalMatrix[] = {
            -1,-2,-1,
            0, 0, 0,
            1, 2, 1
        };
       /* try {
    // retrieve image
   BufferedImage bi =source;
    File outputfile = new File("C:\\Users\\Godfather\\Desktop\\saved0.jpg");
    ImageIO.write(bi, "jpg", outputfile);
         }
    catch (IOException e) {
}*/
        BufferedImage i1 = createBlankBi(source);
        BufferedImage i2 = createBlankBi(source);
        
        new ConvolveOp(new Kernel(3, 3, verticalMatrix), ConvolveOp.EDGE_NO_OP, null).filter(source, i1);
        new ConvolveOp(new Kernel(3, 3, horizontalMatrix), ConvolveOp.EDGE_NO_OP, null).filter(source, i2);
         
        /*try {
    // retrieve image
   BufferedImage bi =source;
    File outputfile = new File("C:\\Users\\Godfather\\Desktop\\saved6.jpg");
    ImageIO.write(bi, "jpg", outputfile);
         }
    catch (IOException e) {
}
   /* 
         try {
    // retrieve image
   BufferedImage bi =i1;
    File outputfile = new File("C:\\Users\\Godfather\\Desktop\\saved.jpg");
    ImageIO.write(bi, "jpg", outputfile);
         }
         BufferedImage bi2 = i2;
    File outputfile2 = new File("C:\\Users\\Godfather\\Desktop\\saved2.jpg");
    ImageIO.write(bi2, "jpg", outputfile2);
} catch (IOException e) {
}*/
        int w = source.getWidth();
        int h = source.getHeight();
        //System.out.println("w="+w );
        //System.out.println("h="+h );
        for (int x=0; x < w; x++)
            for (int y=0; y < h; y++) {
            float sum = 0.0f;
            sum += this.getBrightness(i1,x,y);
            sum += this.getBrightness(i2,x,y);
            this.setBrightness(source, x, y, Math.min(1.0f, sum));
            }
        
    }    
    
}
