package stevenseesall.com.camera;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Administrateur on 2015-02-12.
 */
public class ThreadSendTCPFeed {
    String mServerIP;
    byte[] mData;
    int mPort;

    public ThreadSendTCPFeed(byte[] data, String serverIP, int port){
        mServerIP = serverIP;
        mData = data;
        mPort = port;
    }

    public void send() throws IOException {
        Socket clientSocket = new Socket(mServerIP, mPort);
        DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
        Log.d("CameraTest", Integer.toString(mData.length));
        dOut.write(mData);
        dOut.close();
        clientSocket.close();
    }
}
