package stevenseesall.com.camera;

import android.hardware.Camera;

import java.io.IOException;

import static stevenseesall.com.camera.Utils.*;
import static stevenseesall.com.camera.Utils.compress;

public class CameraPreviewCallback implements Camera.PreviewCallback {

    private boolean mScreenSizeSent;
    private boolean mGettingPort;
    private boolean mSendingData;
    private String mServerIP;
    private int mPort;
    private int mFrameHeight;
    private int mFrameWidth;

    public CameraPreviewCallback(Camera camera, String serverIP) {
        mServerIP = serverIP;
        mFrameHeight = camera.getParameters().getPreviewSize().height;
        mFrameWidth = camera.getParameters().getPreviewSize().width;

        mGettingPort = false;
        mSendingData = false;
        mScreenSizeSent = false;
    }

    private void sendSize() {
        Thread thread = new Thread() {
            public void run() {
                mGettingPort = true;
                SendScreenSizeTCP TCP = new SendScreenSizeTCP(mFrameHeight, mFrameWidth, mServerIP);
                TCP.GetCam();
                mPort = TCP.mPort;
                mScreenSizeSent = true;
            }
        };

        thread.start();
    }

    private void sendFeed(final byte[] data) {
        Thread thread = new Thread() {
            public void run() {
                final byte[] dataCouper = halveYUV420(data, mFrameWidth, mFrameHeight, 6);
                try {
                    byte[] compressedData = compress(dataCouper);
                    ThreadSendUDPFeed UDP = new ThreadSendUDPFeed(compressedData, mServerIP, mPort);
                    UDP.send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally{
                    mSendingData = false;
                }
            }
        };

        thread.start();
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {

        if(!mScreenSizeSent && !mGettingPort){
            sendSize();
        }

        if(!mSendingData && mScreenSizeSent) {
            mSendingData = true;
            sendFeed(data);
        }
    }
}
