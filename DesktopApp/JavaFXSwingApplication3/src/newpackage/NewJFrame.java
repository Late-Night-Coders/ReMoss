/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Administrateur
 */
public class NewJFrame extends javax.swing.JFrame {
    boolean isCam1Used = false; 
    boolean isCam2Used = false;
    boolean isCam3Used = false;
    boolean isCam4Used = false;
    boolean isCam5Used = false;
    boolean isCam6Used = false;
    boolean isCam1Enlarged = false;
    
    TCPThread Cam1;
    TCPThread Cam2;
    TCPThread Cam3;
    TCPThread Cam4;
    TCPThread Cam5;
    TCPThread Cam6;
    
    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() throws IOException {
        Image i = ImageIO.read(getClass().getResource("/newpackage/Camera.png"));
        setIconImage(i);
        initComponents();
        IpAddressCipher ipCipher = new IpAddressCipher();
        String encryptedIPAddress = ipCipher.encryptIPAddress();
        this.lblAddress.setText(encryptedIPAddress);
        System.out.println("Entrez " + encryptedIPAddress + " sur vos appareils mobile.");
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

        Camera1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        Camera2 = new javax.swing.JLabel();
        Camera4 = new javax.swing.JLabel();
        Camera5 = new javax.swing.JLabel();
        Camera3 = new javax.swing.JLabel();
        Camera6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblAddress = new javax.swing.JLabel();
        MainCamera = new javax.swing.JLabel();
        MainCameraNumber = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Skaïpe");

        Camera1.setBackground(new java.awt.Color(0, 0, 0));
        Camera1.setOpaque(true);
        Camera1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Camera1MouseClicked(evt);
            }
        });

        jLabel2.setText("Différence: 0");

        jCheckBox1.setText("Afficher les différences");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jButton1.setLabel("Prendre une photo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Camera2.setBackground(new java.awt.Color(0, 0, 0));
        Camera2.setOpaque(true);
        Camera2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Camera2MouseClicked(evt);
            }
        });

        Camera4.setBackground(new java.awt.Color(0, 0, 0));
        Camera4.setOpaque(true);
        Camera4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Camera4MouseClicked(evt);
            }
        });

        Camera5.setBackground(new java.awt.Color(0, 0, 0));
        Camera5.setOpaque(true);
        Camera5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Camera5MouseClicked(evt);
            }
        });

        Camera3.setBackground(new java.awt.Color(0, 0, 0));
        Camera3.setOpaque(true);
        Camera3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Camera3MouseClicked(evt);
            }
        });

        Camera6.setBackground(new java.awt.Color(0, 0, 0));
        Camera6.setOpaque(true);
        Camera6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Camera6MouseClicked(evt);
            }
        });

        jLabel8.setText("Caméra #1");

        jLabel9.setText("Caméra #2");

        jLabel10.setText("Caméra #3");

        jLabel11.setText("Caméra #4");

        jLabel12.setText("Caméra #5");

        jLabel13.setText("Caméra #6");

        jLabel14.setText("Chaîne de connexion");

        lblAddress.setText("j");

        MainCamera.setBackground(new java.awt.Color(0, 0, 0));
        MainCamera.setOpaque(true);

        MainCameraNumber.setText("1");

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(MainCamera, javax.swing.GroupLayout.PREFERRED_SIZE, 999, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(MainCameraNumber)
                                .addGap(90, 90, 90))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jCheckBox1)
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(Camera1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Camera2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10)
                            .addComponent(Camera3, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(Camera4, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(Camera5, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Camera6, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Camera3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Camera2, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                            .addComponent(Camera1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Camera4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Camera5, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Camera6, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(MainCamera, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(MainCameraNumber)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox1)
                        .addGap(56, 56, 56)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAddress)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        BufferedImage bi = toBufferedImage(((ImageIcon)(NewJFrame.this.Camera1.getIcon())).getImage());
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(NewJFrame.this) == JFileChooser.APPROVE_OPTION) {
            File outputfile = new File(fileChooser.getSelectedFile()+".jpg");
            outputfile.renameTo(outputfile);
            try {
                ImageIO.write(bi, "jpg", outputfile);
            } catch (IOException ex) {
                System.out.println("Erreur");
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void Camera1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Camera1MouseClicked
        NewJFrame.this.MainCameraNumber.setText("1");
    }//GEN-LAST:event_Camera1MouseClicked

    private void Camera2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Camera2MouseClicked
        NewJFrame.this.MainCameraNumber.setText("2");
    }//GEN-LAST:event_Camera2MouseClicked

    private void Camera3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Camera3MouseClicked
        NewJFrame.this.MainCameraNumber.setText("3");
    }//GEN-LAST:event_Camera3MouseClicked

    private void Camera4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Camera4MouseClicked
        NewJFrame.this.MainCameraNumber.setText("4");
    }//GEN-LAST:event_Camera4MouseClicked

    private void Camera5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Camera5MouseClicked
        NewJFrame.this.MainCameraNumber.setText("5");
    }//GEN-LAST:event_Camera5MouseClicked

    private void Camera6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Camera6MouseClicked
        NewJFrame.this.MainCameraNumber.setText("6");
    }//GEN-LAST:event_Camera6MouseClicked

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
                try {
                    new NewJFrame().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Camera1;
    private javax.swing.JLabel Camera2;
    private javax.swing.JLabel Camera3;
    private javax.swing.JLabel Camera4;
    private javax.swing.JLabel Camera5;
    private javax.swing.JLabel Camera6;
    private javax.swing.JLabel MainCamera;
    private javax.swing.JLabel MainCameraNumber;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblAddress;
    // End of variables declaration//GEN-END:variables
 public void startServer() {
        final ExecutorService clientProcessingPool = Executors
                .newFixedThreadPool(10);
        (new Thread(new AssignCamera())).start();
        //MainCamera
        Cam1 = new TCPThread(NewJFrame.this.Camera1, NewJFrame.this.jLabel2, NewJFrame.this.jCheckBox1, 40000, NewJFrame.this.MainCamera, NewJFrame.this.MainCameraNumber, 1);
        Cam2 = new TCPThread(NewJFrame.this.Camera2, NewJFrame.this.jLabel2, NewJFrame.this.jCheckBox1, 40001, NewJFrame.this.MainCamera, NewJFrame.this.MainCameraNumber, 2);
        Cam3 = new TCPThread(NewJFrame.this.Camera3, NewJFrame.this.jLabel2, NewJFrame.this.jCheckBox1, 40002, NewJFrame.this.MainCamera, NewJFrame.this.MainCameraNumber, 3);
        Cam4 = new TCPThread(NewJFrame.this.Camera4, NewJFrame.this.jLabel2, NewJFrame.this.jCheckBox1, 40003, NewJFrame.this.MainCamera, NewJFrame.this.MainCameraNumber, 4);
        Cam5 = new TCPThread(NewJFrame.this.Camera5, NewJFrame.this.jLabel2, NewJFrame.this.jCheckBox1, 40004, NewJFrame.this.MainCamera, NewJFrame.this.MainCameraNumber, 5);
        Cam6 = new TCPThread(NewJFrame.this.Camera6, NewJFrame.this.jLabel2, NewJFrame.this.jCheckBox1, 40005, NewJFrame.this.MainCamera, NewJFrame.this.MainCameraNumber, 6);
        (new Thread(Cam1)).start();
        (new Thread(Cam2)).start();
        (new Thread(Cam3)).start();
        (new Thread(Cam4)).start();
        (new Thread(Cam5)).start();
        (new Thread(Cam6)).start();
    }
 
    public static BufferedImage toBufferedImage(Image img)
    {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
    
    private class AssignCamera implements Runnable {
        @Override 
        public void run(){
            ServerSocket serverSocket;
            try {
                while(true){
                    serverSocket = new ServerSocket(44444);
                    System.out.println("En attente de paquets pour assignation Caméra...");
                    final Socket clientSocket = serverSocket.accept();
                    System.out.println("Pacquet accepté");
                    try{
                        readStr(clientSocket);
                        clientSocket.close();
                        serverSocket.close();
                    }
                    catch(IOException e){
                        System.out.println(e);          
                    }
                }
            }
            catch(Exception e){
                
            }
        }
        
        public void readStr(Socket socket) throws IOException {
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            
            String check = dis.readLine();
            System.out.println("check: " + check);
            if(check != null){
                Socket AskSocket = new Socket(check, 44444);
                DataOutputStream AskSocketout = new DataOutputStream(AskSocket.getOutputStream());
                
                if(!isCam1Used){
                    isCam1Used = true;
                    AskSocketout.writeInt(40000);
                }
                else
                    if(!isCam2Used){
                        isCam2Used = true;
                        AskSocketout.writeInt(40001);
                    }
                    else
                        if(!isCam3Used){
                            isCam3Used = true;
                            AskSocketout.writeInt(40002);
                        }
                        else
                            if(!isCam4Used){
                                isCam4Used = true;
                                AskSocketout.writeInt(40003);
                            }
                            else
                                if(!isCam5Used){
                                    isCam5Used = true;
                                    AskSocketout.writeInt(40004);
                                }
                                else
                                    if(!isCam6Used){
                                        isCam6Used = true;
                                        AskSocketout.writeInt(40005);
                                    }
                
                AskSocketout.close();
                AskSocket.close();
            }
        }
    }
    
    private void EnlargeCamera(JLabel jlabel){
        
    }
    
    private void ShrinkCamera(JLabel jlabel){
        String cam = jlabel.toString();
        javax.swing.JOptionPane.showMessageDialog(null,cam);
    }
 
}


