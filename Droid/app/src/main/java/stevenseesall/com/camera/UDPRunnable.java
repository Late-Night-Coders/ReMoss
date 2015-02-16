package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;

/**
 * Created by Fred on 2/14/2015.
 */
public class UDPRunnable extends Thread {
    Context mContext;
    Camera mCamera;
    Activity mActivity;
    FrameLayout mFrameLayout;
    private SurfaceHolder mHolder;
    String ServerIP = "";
    Boolean mSendingData = false;
    Boolean ScreenSizeSent = false;

    public UDPRunnable(final Context context, Activity activity, FrameLayout frameLayout, String ip){
        ServerIP = ip;
        mContext = context;
        mActivity = activity;
        mFrameLayout = frameLayout;
    }


    public void run() {
        mCamera = getCameraInstance();
        Looper.prepare();
        final CameraPreview preview = new CameraPreview(mContext, mCamera);
        final FrameLayout previewFrame = mFrameLayout;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewFrame.addView(preview);
            }
        });
        Looper.loop();

    }

    public static Camera getCameraInstance() {
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            throw new RuntimeException("No camera found on device");
        }

        return camera;
    }
}