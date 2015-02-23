/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;

/**
 *
 * @author Administrateur
 */
public class TCPThread implements Runnable{
    JLabel mJLabel;
    JLabel mJLabel2;
    JLabel mMainCam;
    JLabel mMainCamNumber;
    JCheckBox mJCheckBox;
    int mPort;
    int[] mImageAvant;
    int mHeight;
    int mWidth;
    int mNoCam;
    JCheckBox mSaveOnMov;
    JSpinner mJSpinner;

     final ExecutorService clientProcessingPool = Executors
                .newFixedThreadPool(10);
    
    public TCPThread(JLabel jLabel, JLabel jLabel2, JCheckBox jCheckBox, int port, JLabel mainCamera, JLabel mainCameraNumber, int noCam, JCheckBox saveOnMov, JSpinner jspinner){
        mJLabel = jLabel;
        mPort = port;
        mJLabel2 = jLabel2;
        mJCheckBox = jCheckBox;
        mMainCam = mainCamera;
        mMainCamNumber = mainCameraNumber;
        mNoCam = noCam;
        mSaveOnMov = saveOnMov;
        mJSpinner = jspinner;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(mPort);
            System.out.println("En attente de paquets TCP...");

            final Socket clientSocket = serverSocket.accept();
            try{
                readBytes(clientSocket);
                System.out.println("h = " + mWidth);
                System.out.println("w = " + mHeight);
                System.out.println("port = " + mPort);
                (new Thread(new UDPThread(mJLabel, mJLabel2, mJCheckBox, mPort, mHeight, mWidth, mMainCam, mMainCamNumber, mNoCam, mSaveOnMov, mJSpinner))).start();
                clientSocket.close();
            }
            catch(IOException e){
                System.out.println(e);
            }           
        } catch (IOException e) {
            System.err.println("Unable to process client request");
            e.printStackTrace();
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
    
    public void readBytes(Socket socket) throws IOException {
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            
            mHeight = dis.readInt();
            mWidth = dis.readInt();
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
                for (int x = 0; x < mImageActual.length; x+=2) {
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
                Image img = getImageFromArrayMEM(mImageActual,480,270);
                mJLabel.setIcon(new ImageIcon(img));
            }
        }
    }
}


