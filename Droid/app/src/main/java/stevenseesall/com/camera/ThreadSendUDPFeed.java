package stevenseesall.com.camera;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
        //Bitmap image = BitmapFactory.decodeByteArray(mData, 0, mData.length);
        //image.compress(Bitmap.CompressFormat.JPEG, 10);
        //OutputStream fOut = new FileOutputStream(externalStorageFile);

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
