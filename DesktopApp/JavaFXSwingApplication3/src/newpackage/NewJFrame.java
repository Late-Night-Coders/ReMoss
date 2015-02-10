/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 *
 * @author Administrateur
 */
public class NewJFrame extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
        startServer();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
 public void startServer() {
        final ExecutorService clientProcessingPool = Executors
                .newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] receiveData = new byte[21600];
                    DatagramSocket serverSocket = new DatagramSocket(666);
                    System.out.println("Waiting for clients to connect...");
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        System.out.println("RECEIVED: " + receivePacket);
                        int rgb[] = new int[21600];
                        int[] image = decodeYUV420SP(rgb, receivePacket.getData(), 160, 90);
                        System.out.println("Decode Complet");
                        Image img = getImageFromArrayMEM(image,160,90);


                        System.out.println("getImageFromArray done");
                        NewJFrame.this.jLabel1.setIcon(new ImageIcon(img));
                        System.out.println("Image Créée");
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
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
    
    
    private class ClientTask implements Runnable {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Got a client !");
            try {
            /* Get Data From Client */
                byte[] byteArr = readBytes(clientSocket);
                System.out.println("Bytes reçus");
                int rgb[] = new int[259200];
                int[] image = decodeYUV420SP(rgb, byteArr, 480, 270);
                System.out.println("Decode Complet");
                Image img = getImageFromArrayMEM(image,480,540);
                
               
                System.out.println("getImageFromArray done");
                NewJFrame.this.jLabel1.setIcon(new ImageIcon(img));
                System.out.println("Image Créée");
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        
        public Image getImageFromArrayMEM(int[] pixels, int width, int height) {
            MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0, width);
            Toolkit tk = Toolkit.getDefaultToolkit();
            return tk.createImage(mis);
        }
        
        public byte[] readBytes(Socket socket) throws IOException {
            // Again, probably better to store these objects references in the support class
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            
            int len = dis.readInt();
            byte[] data = new byte[len];
            if (len > 0) {
                dis.readFully(data);
            }
            return data;
        }
        
        
    }

}
