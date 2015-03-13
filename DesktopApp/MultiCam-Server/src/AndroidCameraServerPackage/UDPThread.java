/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;

/**
 *
 * @author Fred
 */
public class UDPThread implements Runnable{
    int mPort;
    int mNoCam;
    AndroidCameraServer mNewJFrame;
    byte[] mImageAvant;
    boolean isSavingFile = false;

    final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
    
    public UDPThread(AndroidCameraServer newJFrame, int port, int noCam){
        mPort = port;
        mNoCam = noCam;
        mNewJFrame = newJFrame;
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
                if(mImageAvant != null){
                    ImageProcessing ImageProc = new ImageProcessing(data,mImageAvant, mNewJFrame, mNoCam);
                }
                mImageAvant = data;
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
}
