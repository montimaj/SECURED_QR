package edu.sxccal.utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/*
 * Converts a colored image to black and white
*/

public class ImgtoBlackandWhite
{   
  
	/**
	 * Creates black and white image
	 * @param f Input image file
	 * @param dest Output directory to store BW.png
	 * @return path to BW.png
	 * @throws Exception
	 */
	public static String toBW(String f,String dest) throws Exception
	{    
	    File file = new File(f);    
	    BufferedImage img = ImageIO.read(file);    
	    BufferedImage bwimg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_BYTE_BINARY);    
	    Graphics2D graphics = bwimg.createGraphics();
	    graphics.drawImage(img, 0, 0, null);
	    f=dest+"/BW.png";
	    ImageIO.write(bwimg, "png", new File(f));      
	    return f; 
	} 
}