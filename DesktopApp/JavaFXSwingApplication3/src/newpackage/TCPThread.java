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
import javax.swing.JLabel;

/**
 *
 * @author Administrateur
 */
public class TCPThread implements Runnable{
    JLabel mJLabel;
    int mPort;
     final ExecutorService clientProcessingPool = Executors
                .newFixedThreadPool(10);
    
    public TCPThread(JLabel jLabel, int port){
        mJLabel = jLabel;
        mPort = port;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(mPort);
            System.out.println("En attente de paquets TCP...");
            
            while (true) {
                        final Socket clientSocket = serverSocket.accept();
                        System.out.println("accepté");
                        try{
                            byte[] byteArr = readBytes(clientSocket);
                            int rgb[] = new int[57600];
                            int[] image = decodeYUV420SP(rgb, byteArr, 320, 180);
                            Image img = getImageFromArrayMEM(image,320,180);
                            mJLabel.setIcon(new ImageIcon(img));
                            clientSocket.close();
                        }
                        catch(IOException e){
                            System.out.println(e);
                        }
                        
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
    
    public byte[] readBytes(Socket socket) throws IOException {
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            
            byte[] data = new byte[57600];
            dis.readFully(data);
            return data;
        }
}
