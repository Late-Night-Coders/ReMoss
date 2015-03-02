package stevenseesall.com.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Fred on 2/10/2015.
 */
public class ThreadSendUDPFeed {
    String mServerIP;
    byte[] mData;
    int mPort;

    public ThreadSendUDPFeed(byte[] data, String serverIP, int port){
        mServerIP = serverIP;
        mData = data;
        mPort = port;
    }

    public void send(){
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

        DatagramPacket sendPacket = new DatagramPacket(mData, mData.length, IPAddress, mPort);
        assert clientSocket != null;
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            Log.d("CameraTest", e.getMessage());
        }
        clientSocket.close();
    }
}
