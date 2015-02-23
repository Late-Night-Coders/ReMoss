/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author Fred
 */
public class UDPThread implements Runnable{
    JLabel mJLabel;
    JLabel mJLabel2;
    JCheckBox mJCheckBox;
    int mPort;
    int[] mImageAvant;
    int mHeight;
    int mWidth;
    int decrementor = 6;
    int mNoCam;
    JLabel mMainCam;
    JLabel mMainCamNumber;

    final ExecutorService clientProcessingPool = Executors
                .newFixedThreadPool(10);
    
    public UDPThread(JLabel jLabel, JLabel jLabel2, JCheckBox jCheckBox, int port, int height, int width, JLabel mainCamera, JLabel mainCameraNumber, int noCam){
        mJLabel = jLabel;
        mPort = port;
        mJLabel2 = jLabel2;
        mJCheckBox = jCheckBox;
        mHeight = height;
        mWidth = width;
        mMainCam = mainCamera;
        mMainCamNumber = mainCameraNumber;
        mNoCam = noCam;
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
                int rgb[] = new int[receiveData.length];
                int[] image = decodeYUV420SP(rgb, decompress(receivePacket.getData()), mWidth / decrementor, mHeight / decrementor);
                
                if(mJCheckBox.isSelected()){
                    (new Thread(new UDPThread.CheckMovement(image, mImageAvant))).start();
                    mImageAvant = image;
                }
                else{
                    Image img = getImageFromArrayMEM(image,mWidth / decrementor, mHeight / decrementor);
                    BufferedImage image2 = toBufferedImage(img); // transform it 
                    Dimension d = mJLabel.getSize();
                    Image newimg = image2.getScaledInstance(d.width, d.height,  java.awt.Image.SCALE_SMOOTH);
                    mJLabel.setIcon(new ImageIcon(newimg));
                    if(mMainCamNumber.getText().equals(Integer.toString(mNoCam))){
                        Dimension dPrim = mMainCam.getSize();
                        Image primImage = image2.getScaledInstance(dPrim.width, dPrim.height,  java.awt.Image.SCALE_SMOOTH);
                        mMainCam.setIcon(new ImageIcon(primImage));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to process client request");
            e.printStackTrace();
        } catch (DataFormatException ex) {
            Logger.getLogger(UDPThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public int[] decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
                int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
                for (int i = 0; i < width; i++, yp++) {
                        int y = (0xff & ((int) yuv420sp[yp])) - 16;
                        if (y < 0) y = 0;
                        if ((i & 1) == 0) {
                                v = (0xff & yuv420sp[uvp++]) - 128;
                                u = (0xff & yuv420sp[uvp++]) - 128;
                        }

                        int y1192 = 1192 * y;
                        int r = (y1192 + 1634 * v);
                        int g = (y1192 - 833 * v - 400 * u);
                        int b = (y1192 + 2066 * u);

                        if (r < 0) r = 0; else if (r > 262143) r = 262143;
                        if (g < 0) g = 0; else if (g > 262143) g = 262143;
                        if (b < 0) b = 0; else if (b > 262143) b = 262143;

                        rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                }
        }
        return rgb;
    }
    
    public Image getImageFromArrayMEM(int[] pixels, int width, int height) {
        MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0, width);
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.createImage(mis);
    }
    
    public byte[] readBytes(Socket socket) throws IOException {
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            
            byte[] data = new byte[21600];
            dis.readFully(data);
            return data;
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
     
     public CheckMovement(int[] imageAvant, int[] imageActual){
         mImageAvant = imageAvant;
         mImageActual = imageActual;
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
                        mImageActual[x] = 0xffff0000;
                    }
                    else {
                        if (gActual <= gOld -15 || gActual >= gOld + 15) {
                            mDiff++;
                            mImageActual[x] = 0xffff0000;
                        }
                        else
                        {
                            if (bActual <= bOld -15 || bActual >= bOld + 15) {
                                mDiff++;
                                mImageActual[x] = 0xffff0000;
                            }
                        }
                    }
                }
                mJLabel2.setText("Différence: " + mDiff);
                Image img = getImageFromArrayMEM(mImageActual,mWidth / decrementor, mHeight / decrementor);
                BufferedImage image2 = toBufferedImage(img); // transform it 
                Dimension d = mJLabel.getSize();
                Image newimg = image2.getScaledInstance(d.width, d.height,  java.awt.Image.SCALE_SMOOTH);
                mJLabel.setIcon(new ImageIcon(newimg));
                if(mMainCamNumber.getText().equals(Integer.toString(mNoCam))){
                    Dimension dPrim = mMainCam.getSize();
                    Image primImage = image2.getScaledInstance(dPrim.width, dPrim.height,  java.awt.Image.SCALE_SMOOTH);
                    mMainCam.setIcon(new ImageIcon(primImage));
                }
            }
        }
     
     
    }
}
