package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

/**
 * Created by Fred on 2/7/2015.
 */
public class StandAloneRunnable extends Thread {
    Context mContext;
    Camera mCamera;
    Activity mActivity;
    SeekBar mSeekBar;
    FrameLayout mFrameLayout;
    int[] mImageAvant;
    TextView mTextView;
    TextView mMouvementTextView;
    ToggleButton mToggleButton;
    Boolean isAlarmOn = false;
    
    public StandAloneRunnable(final Context context, Activity activity, SeekBar seekBar, FrameLayout frameLayout, TextView textView,
                              TextView textViewMouvement, ToggleButton toggleButton) throws IOException {
        mContext = context;
        mActivity = activity;
        mSeekBar = seekBar;
        mFrameLayout = frameLayout;
        mTextView = textView;
        mMouvementTextView = textViewMouvement;
        mToggleButton = toggleButton;

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    isAlarmOn = true;
                } else {
                    isAlarmOn = false;
                }
            }
        });
    }

    public void run() {
        mCamera = getCameraInstance();
        Looper.prepare();
        final CameraPreview preview = new CameraPreview(mContext, mCamera,
                mActivity, mSeekBar, mImageAvant, mTextView, mMouvementTextView, mToggleButton, isAlarmOn, true);
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