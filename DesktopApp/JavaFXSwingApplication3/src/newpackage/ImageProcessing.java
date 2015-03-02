/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Administrateur
 */
public class ImageProcessing {
    byte[] mData;
    
    public ImageProcessing(byte[] data){
        mData = data;
    }

    public BufferedImage ToBufferedImage() throws IOException {
        ByteArrayInputStream baos=new ByteArrayInputStream(mData);
        return ImageIO.read(baos);
    }
}
