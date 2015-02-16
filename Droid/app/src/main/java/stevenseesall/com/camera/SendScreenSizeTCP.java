package stevenseesall.com.camera;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Fred on 2/14/2015.
 */
public class SendScreenSizeTCP implements Runnable {
    String mServerIP;
    int mHeight = 0;
    int mWidth = 0;
    int mPort;

    public SendScreenSizeTCP(int height, int width, String serverIP, int port){
        mServerIP = serverIP;
        mHeight = height;
        mWidth = width;
        mPort = port;
    }

    public void run() {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(mServerIP, mPort);
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
            dOut.writeInt(mHeight);
            dOut.writeInt(mWidth);
            dOut.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
