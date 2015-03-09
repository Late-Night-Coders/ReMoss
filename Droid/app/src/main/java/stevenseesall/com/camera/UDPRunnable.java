package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Looper;
import android.widget.FrameLayout;
import android.widget.SeekBar;

public class UDPRunnable extends Thread {
    Context mContext;
    Camera mCamera;
    Activity mActivity;
    FrameLayout mFrameLayout;
    String mServerIP;
    SeekBar mSkb;

    public UDPRunnable(final Context context, Activity activity, FrameLayout frameLayout, String serverIP, SeekBar skb){
        mServerIP = serverIP;
        mContext = context;
        mActivity = activity;
        mFrameLayout = frameLayout;
        mSkb = skb;
    }

    public void run() {
        mCamera = getCameraInstance();
        Looper.prepare();
        final CameraPreview preview = new CameraPreview(mContext, mCamera, mServerIP, mSkb);
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
