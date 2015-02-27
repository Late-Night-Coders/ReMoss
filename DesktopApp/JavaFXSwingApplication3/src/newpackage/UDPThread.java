/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;

/**
 *
 * @author Fred
 */
public class UDPThread implements Runnable{
    JLabel mJLabelMainCam;
    JLabel mJLabelDiff;
    JCheckBox mJCheckBox;
    int mPort;
    int[] mImageAvant;
    int mHeight;
    int mWidth;
    int decrementor = 6;
    int mNoCam;
    JLabel mMainCam;
    JLabel mMainCamNumber;
    boolean isSavingFile = false;
    JCheckBox mSaveOnMov;
    JSpinner mJSpinner;

    final ExecutorService clientProcessingPool = Executors
                .newFixedThreadPool(10);
    
    public UDPThread(JLabel mainCam, JLabel diff, JCheckBox chk_diff, int port, int height, int width, JLabel mainCamera, 
            JLabel mainCameraNumber, int noCam, JCheckBox saveOnMov, JSpinner spn_trigger){
        mJLabelMainCam = mainCam;
        mPort = port;
        mJLabelDiff = diff;
        mJCheckBox = chk_diff;
        mHeight = height;
        mWidth = width;
        mMainCam = mainCamera;
        mMainCamNumber = mainCameraNumber;
        mNoCam = noCam;
        mSaveOnMov = saveOnMov;
        mJSpinner = spn_trigger;
    }
    
    @Override
    public void run() {
        try {
            byte[] receiveData = new byte[100000];
            DatagramSocket serverSocket = new DatagramSocket(mPort);
            System.out.println("En attente de paquets UDP...");
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                byte[] data = decompress(trim(receivePacket.getData()));
                System.out.println(data.length);
                ByteArrayInputStream baos=new ByteArrayInputStream(data);
                BufferedImage bImageFromConvert = ImageIO.read(baos);
               
                
                
                if(mSaveOnMov.isSelected() || mJCheckBox.isSelected()){
                    boolean showMov = false;
                    boolean saveOnMov = false;
                    if(mJCheckBox.isSelected()){
                        showMov = true;
                    }
                    if(mSaveOnMov.isSelected()){
                        saveOnMov = true;
                    }
                    //(new Thread(new UDPThread.CheckMovement(image, mImageAvant, showMov, saveOnMov))).start();
                    //mImageAvant = image;
                }
                else{
                    Dimension d = mJLabelMainCam.getSize();
                    Image newimg = bImageFromConvert.getScaledInstance(d.width, d.height,  java.awt.Image.SCALE_SMOOTH);                   
                    mJLabelMainCam.setIcon(new ImageIcon(newimg));
                    if(mMainCamNumber.getText().equals(Integer.toString(mNoCam))){
                        Dimension dPrim = mMainCam.getSize();
                        Image primImage = bImageFromConvert.getScaledInstance(dPrim.width, dPrim.height,  java.awt.Image.SCALE_SMOOTH);
                        BufferedImage imagePrim = toBufferedImage(primImage); 
                        PrintWatermark(imagePrim);
                        mMainCam.setIcon(new ImageIcon(imagePrim));
                    }
                }
            }
        } catch (IOException e) {

        } catch (DataFormatException ex) {
            Logger.getLogger(UDPThread.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    static byte[] trim(byte[] bytes)
    {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0)
        {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
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
    
    public static BufferedImage toBufferedImage(Image img)
    {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
    
    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {  
        Inflater inflater = new Inflater();   
        inflater.setInput(data);  

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
        byte[] buffer = new byte[21600];  
        while (!inflater.finished()) {  
         int count = inflater.inflate(buffer);  
         outputStream.write(buffer, 0, count);  
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();  

        inflater.end();
        return output;  
       }  
    
    
    
    private class CheckMovement implements Runnable{
     int[] mImageAvant;
     int[] mImageActual;
     int mDiff = 0;
     boolean mShowMov = false;
     boolean mSaveOnMov = false;
     
     
     public CheckMovement(int[] imageAvant, int[] imageActual, boolean showMov, boolean saveOnMov){
         mImageAvant = imageAvant;
         mImageActual = imageActual;
         mShowMov = showMov;
         mSaveOnMov = saveOnMov;
     }
     
     public void run(){
         
            if(mImageAvant != null) {
                for (int x = 0; x < mImageActual.length; x++) {
                    // Décalage de bits pour trouver les valeurs RGB actuelles
                    int rActual = (mImageActual[x] & 0x00ff0000) >> 16;
                    int gActual = (mImageActual[x] & 0x0000ff00) >> 8;
                    int bActual = mImageActual[x] & 0x0000ff;

                    // Décalage de bits pour trouver les valeurs RGB de l'ancienne image
                    int rOld = (mImageAvant[x] & 0x00ff0000) >> 16;
                    int gOld = (mImageAvant[x] & 0x0000ff00) >> 8;
                    int bOld = mImageAvant[x] & 0x0000ff;

                    if(rActual <= rOld -15 || rActual >= rOld + 15)
                    {
                        mDiff++;
                        if(mShowMov){
                            mImageActual[x] = 0xffff0000;
                        }
                        
                    }
                    else {
                        if (gActual <= gOld -15 || gActual >= gOld + 15) {
                            mDiff++;
                            if(mShowMov){
                            mImageActual[x] = 0xffff0000;
                            }
                        }
                        else
                        {
                            if (bActual <= bOld -15 || bActual >= bOld + 15) {
                                mDiff++;
                                if(mShowMov){
                                    mImageActual[x] = 0xffff0000;
                                }
                            }
                        }
                    }
                }
                Image img = getImageFromArrayMEM(mImageActual,mWidth / decrementor, mHeight / decrementor);
                BufferedImage image2 = toBufferedImage(img); // transform it 
                Dimension d = mJLabelMainCam.getSize();
                Image newimg = image2.getScaledInstance(d.width, d.height,  java.awt.Image.SCALE_SMOOTH);
                mJLabelMainCam.setIcon(new ImageIcon(newimg));
                if(mMainCamNumber.getText().equals(Integer.toString(mNoCam))){
                    int pourcentDiff = (int)  Math.round((mDiff / ((double)mWidth  / (double)decrementor * (double)mHeight / (double)decrementor)) * 100);               
                    mJLabelDiff.setText("Différence: " + pourcentDiff);
                    Dimension dPrim = mMainCam.getSize();
                    Image primImage = image2.getScaledInstance(dPrim.width, dPrim.height,  java.awt.Image.SCALE_SMOOTH);
                    BufferedImage imagePrim = toBufferedImage(primImage); 
                    PrintWatermark(imagePrim);
                    mMainCam.setIcon(new ImageIcon(imagePrim));
                    if(pourcentDiff > (int)mJSpinner.getValue() && mSaveOnMov){
                        BufferedImage bi = toBufferedImage(((ImageIcon)(mMainCam.getIcon())).getImage());
                        saveImageToDisk(bi, mMainCamNumber.getText());
                    }
                }
            }
        }
     
        private void saveImageToDisk(final BufferedImage bi, final String CameraMaison){
            (new Thread() {
                public void run() {
                    isSavingFile = true;
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
                    Date date = new Date();
                    String sysdrive = System.getenv("SystemDrive");
                    File outputfile = new File("C://" + dateFormat.format(date)+"-Camera "+ CameraMaison + ".jpg");
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
    }
}
