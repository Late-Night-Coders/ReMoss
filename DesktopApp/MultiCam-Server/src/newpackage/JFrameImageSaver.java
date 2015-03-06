/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Administrateur
 */
public class JFrameImageSaver {
    JLabel mMainCamera;
    BufferedImage mBI;
    JFrame mJFrame;
    
    public JFrameImageSaver(JFrame jFrame, JLabel mainCamera){
        mMainCamera = mainCamera;
        mJFrame = jFrame;
        mBI = toBufferedImage(((ImageIcon)(mMainCamera.getIcon())).getImage());
        saveImageToDisk(mJFrame, mBI);
    }
    
    public static BufferedImage toBufferedImage(Image img)
    {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
    
    private void saveImageToDisk(JFrame jframe, BufferedImage bi){
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(jframe) == JFileChooser.APPROVE_OPTION) {
            File outputfile = new File(fileChooser.getSelectedFile()+".jpg");
            outputfile.renameTo(outputfile);
            try {
                ImageIO.write(bi, "jpg", outputfile);
            } catch (IOException ex) {
                System.out.println("Erreur");
            }
        }
    }
}
