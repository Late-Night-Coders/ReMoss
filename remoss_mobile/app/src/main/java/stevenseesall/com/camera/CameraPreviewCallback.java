package stevenseesall.com.camera;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static stevenseesall.com.camera.Utils.*;
import static stevenseesall.com.camera.Utils.compress;

public class CameraPreviewCallback implements Camera.PreviewCallback {

    private boolean mScreenSizeSent;
    private boolean mGettingPort;
    private boolean mSendingData;
    private String mServerIP;
    private int mPort;
    private int mQuality = 1;
    SeekBar mSKB;

    public CameraPreviewCallback(Camera camera, String serverIP, SeekBar skb) {
        mServerIP = serverIP;
        mGettingPort = false;
        mSendingData = false;
        mScreenSizeSent = false;
        mSKB = skb;
    }

    private void SendSize(final int height,final int width){
        (new Thread() {
            public void run() {
                mGettingPort = true;
                SendScreenSizeTCP TCP = new SendScreenSizeTCP(height, width, mServerIP);
                TCP.GetCam();
                mPort = TCP.mPort;
                mScreenSizeSent = true;
            }
        }).start();
    }

    private void SendData(final byte[] data, final int width, final int height){
        (new Thread() {
            public void run() {
                YuvImage yuv_image = new YuvImage(data, ImageFormat.NV21, width, height, null);
                ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
                int qual = mSKB.getProgress();
                yuv_image.compressToJpeg(new Rect(0, 0, width, height), 10 * qual, output_stream);
                byte[] byt=output_stream.toByteArray();
                try {
                    byte[] compressedData = compress(byt);
                    ThreadSendUDPFeed UDP = new ThreadSendUDPFeed(compressedData, mServerIP, mPort);
                    UDP.send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally{
                    mSendingData = false;
                }
            }
        }).start();
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {

        final int frameHeight = camera.getParameters().getPreviewSize().height;
        final int frameWidth = camera.getParameters().getPreviewSize().width;

        if(!mScreenSizeSent && !mGettingPort){
            SendSize(frameHeight, frameWidth);
        }

        if(!mSendingData && mScreenSizeSent){
            mSendingData = true;
            byte[] dataCouper = halveYUV420(halveYUV420(data,frameWidth, frameHeight, 2),frameWidth/2, frameHeight/2, 2);
            SendData(dataCouper, frameWidth/4, frameHeight/4);
        }
    }
}
