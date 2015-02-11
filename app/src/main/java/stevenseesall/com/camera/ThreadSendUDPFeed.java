package stevenseesall.com.camera;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Fred on 2/10/2015.
 */
public class ThreadSendUDPFeed implements Runnable {
    String mServerIP;
    byte[] mData;

    public ThreadSendUDPFeed(byte[] data, String serverIP){
        mServerIP = serverIP;
        mData = data;
    }

    public void run(){
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName(mServerIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] dataCouper = halveYUV420(mData, 1920, 1080, 12);
        DatagramPacket sendPacket = new DatagramPacket(dataCouper, dataCouper.length, IPAddress, 666);
        assert clientSocket != null;
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            Log.d("CameraTest", e.getMessage());
        }
        clientSocket.close();
    }

    public byte[] halveYUV420(byte[] data, int imageWidth, int imageHeight, int decrementor) {
        byte[] yuv = new byte[imageWidth/decrementor * imageHeight/decrementor * 3 / 2];
        // halve yuma
        int i = 0;
        for (int y = 0; y < imageHeight; y+=decrementor) {
            for (int x = 0; x < imageWidth; x+=decrementor) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // halve U and V color components
        for (int y = 0; y < imageHeight / 2; y+=decrementor) {
            for (int x = 0; x < imageWidth; x += (decrementor * 2)) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i++;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x + 1)];
                i++;
            }
        }
        return yuv;
    }

}
