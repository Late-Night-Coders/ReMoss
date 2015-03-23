package remoss.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class UDPThread implements Runnable{
    int mPort;
    int mNoCam;
    AndroidCameraServer mNewJFrame;
    byte[] mImageAvant;
    boolean isSavingFile = false;
    public boolean TimedOut = false;
    AssignCamera mCams;
    DatagramSocket serverSocket;

    final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
    
    public UDPThread(AndroidCameraServer newJFrame, int port, int noCam, AssignCamera cams){
        mPort = port;
        mNoCam = noCam;
        mNewJFrame = newJFrame;
        mCams = cams;
    }
    
    @Override
    public void run() {
        try {
            byte[] receiveData = new byte[100000];
            serverSocket = new DatagramSocket(mPort);
            serverSocket.setSoTimeout(10000);
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
        } catch(SocketTimeoutException STE){
            System.out.println("Socket timeout");
            serverSocket.close();
            if(mNoCam == 1){
                mCams.isCam1Used = false;
            }
            else
                if(mNoCam == 2){
                    mCams.isCam2Used = false;
                }
                else
                    if(mNoCam == 3){
                        mCams.isCam3Used = false;
                    }
                    else
                        if(mNoCam == 4){
                            mCams.isCam4Used = false;
                        }
                        else
                            if(mNoCam == 5){
                                mCams.isCam5Used = false;
                            }
                            else
                                if(mNoCam == 6){
                                    mCams.isCam6Used = false;
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
    
    public int CamIdled(){
        return mNoCam;
    }
}
