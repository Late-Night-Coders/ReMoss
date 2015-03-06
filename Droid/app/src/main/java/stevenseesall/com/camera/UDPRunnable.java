package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

public class UDPRunnable extends Thread {
    Context mContext;
    Camera mCamera;
    Activity mActivity;
    FrameLayout mFrameLayout;
    String mServerIP;

    public UDPRunnable(final Context context, Activity activity, FrameLayout frameLayout, String serverIP){
        mServerIP = serverIP;
        mContext = context;
        mActivity = activity;
        mFrameLayout = frameLayout;
    }

    public void run() {
        mCamera = getCameraInstance();
        Looper.prepare();
        final CameraPreview preview = new CameraPreview(mContext, mCamera, mServerIP);
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
