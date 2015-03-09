package stevenseesall.com.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    Camera mCamera;
    private SurfaceHolder mHolder;
    String mServerIP;
    SeekBar mQualitySeekBar;

    public CameraPreview(final Context context, Camera camera, String serverIP) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = camera;
        mServerIP = serverIP;
        mQualitySeekBar = (SeekBar) findViewById(R.id.skb_Quality);
        mQualitySeekBar.setProgress(5);
        mQualitySeekBar.setOnSeekBarChangeListener(new SeekBarQualityListener((TextView)findViewById(R.id.txt_SetQuality)));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("Error", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size cameraSize = sizes.get(0);
        parameters.setPreviewSize(cameraSize.width, cameraSize.height);
        parameters.setRotation(90);
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);

        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(new CameraPreviewCallback(mCamera, mServerIP));
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d("Error", "Error starting camera preview: " + e.getMessage());
        }
    }
}
