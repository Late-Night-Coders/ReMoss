package AndroidCameraServerPackage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public final class ImageProcessing {
    int mNoCam;
    JLabel mJLabelCamera;
    boolean isSavingFile = false;
    AndroidCameraServer mNewJFrame;
    
    public ImageProcessing(byte[] data, byte[] mImageAvant, AndroidCameraServer newJFrame, int noCam) throws IOException{
        mNoCam = noCam;
        mNewJFrame = newJFrame;
        
        switch(noCam){
            case 1:
                mJLabelCamera = mNewJFrame.Camera1;
                break;
            case 2:
                mJLabelCamera = mNewJFrame.Camera2;
                break;
            case 3:
                mJLabelCamera = mNewJFrame.Camera3;
                break;
            case 4:
                mJLabelCamera = mNewJFrame.Camera4;
                break;
            case 5:
                mJLabelCamera = mNewJFrame.Camera5;
                break;
            default:
                mJLabelCamera = mNewJFrame.Camera6;
                break;
        }
        
        BufferedImage img = ToBufferedImage(data);
        
        if(mNewJFrame.chk_SaveMovement.isSelected() || mNewJFrame.chk_diff.isSelected()){
            BufferedImage oldImg = ToBufferedImage(mImageAvant);
            CheckMovement chkMov = new CheckMovement(BufferedImageToInt(oldImg), BufferedImageToInt(img), mNewJFrame.chk_diff.isSelected());
            int pourcentDiff = (int)  Math.round(chkMov.CheckDiff() / ((double)img.getWidth() * (double)img.getHeight()) * 100); 
            BufferedImage imagePrim = GetHighQualityBufferedImage(chkMov, img.getWidth(), img.getHeight());
              
            if(pourcentDiff > (int)mNewJFrame.spn_trigger.getValue() && mNewJFrame.chk_SaveMovement.isSelected()){
                saveImageToDisk(imagePrim, "Camera " + Integer.toString(noCam));
            }
            
            if(mNewJFrame.MainCameraNumber.getText().equals(Integer.toString(mNoCam))){
                mNewJFrame.MainCamera.setIcon(new ImageIcon(imagePrim));
            }
            displayImage(img);
        }
        else{
            if(mNewJFrame.MainCameraNumber.getText().equals(Integer.toString(mNoCam))){
                displayImageMain(mNewJFrame.MainCamera, img);
            }
            displayImage(img);
        }
    }
    
    private void saveImageToDisk(final BufferedImage bi, final String CameraMaison){
        (new Thread() {
            @Override
            public void run() {
                isSavingFile = true;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
                Date date = new Date();
                System.out.println(mNewJFrame.txt_Path.getText());
                File outputfile = new File(mNewJFrame.txt_Path.getText() + "\\" + dateFormat.format(date)+"-Camera "+ CameraMaison + ".jpg");
                outputfile.renameTo(outputfile);
                try {
                    ImageIO.write(bi, "jpg", outputfile);
                } catch (IOException ex) {
                    System.out.println("Erreur");
                }
                finally{
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(UDPThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    isSavingFile = false;
                }
            }
        }).start();
    }    
    

    public BufferedImage ToBufferedImage(byte[] img) throws IOException {
        ByteArrayInputStream baos=new ByteArrayInputStream(img);
        return ImageIO.read(baos);
    }
    
    public Image getImageFromArrayMEM(int[] pixels, int width, int height) {
        MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0, width);
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.createImage(mis);
    }
    
    public void PrintWatermark(BufferedImage imagePrim) {
        Graphics2D g2d = (Graphics2D) imagePrim.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2d.setComposite(alphaChannel); 
        g2d.setColor(Color.RED); 
        g2d.setFont(new Font("Arial", Font.BOLD, 24)); 
        FontMetrics fontMetrics = g2d.getFontMetrics(); 
        Rectangle2D rect = fontMetrics.getStringBounds("gdwa", g2d); 
        int centerX = (imagePrim.getWidth() - (int) rect.getWidth()) - 200; 
        int centerY = imagePrim.getHeight() - 30; 
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        g2d.drawString(dateFormat.format(date), centerX, centerY);
        g2d.dispose();
    }   
    
    public void displayImage(BufferedImage img){
        Dimension d = mJLabelCamera.getSize();
        Image thumbnailimg = img.getScaledInstance(d.width, d.height,  java.awt.Image.SCALE_SMOOTH);
        mJLabelCamera.setIcon(new ImageIcon(thumbnailimg));
    }
    
    public void displayImageMain(JLabel mainCamera, BufferedImage img){
        Dimension dPrim = mainCamera.getSize();
        Image primImage = img.getScaledInstance(dPrim.width, dPrim.height,  java.awt.Image.SCALE_SMOOTH);
        BufferedImage imagePrim = toBufferedImage(primImage); 
        PrintWatermark(imagePrim);
        mainCamera.setIcon(new ImageIcon(imagePrim));
    }
    
    public static BufferedImage toBufferedImage(Image img)
    {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
    
    int[] BufferedImageToInt(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        int rgb[] = new int[width*height];
        img.getRGB(0,0,width, height,rgb,0,width);
        return rgb;
    }
    
    public BufferedImage GetHighQualityBufferedImage(CheckMovement chkMov, int width, int height){
        Dimension dPrim = mNewJFrame.MainCamera.getSize();
        Image image = getImageFromArrayMEM(chkMov.mImageActual,width, height);
        Image primImage = image.getScaledInstance(dPrim.width, dPrim.height,  java.awt.Image.SCALE_SMOOTH);
        BufferedImage imagePrim = toBufferedImage(primImage); 
        PrintWatermark(imagePrim);
        return imagePrim;
    }
}
