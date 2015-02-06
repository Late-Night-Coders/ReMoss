package stevenseesall.com.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by renaud on 15-02-02.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    int Sensibility = 0;
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(final Context context, Camera camera, SeekBar s) {
        super(context);
        mCamera = camera;

        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                Sensibility = progress;
                Toast.makeText(context, "Sensibilité: " + progress, Toast.LENGTH_SHORT).show();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size cameraSize = sizes.get(0);
        parameters.setPreviewSize(cameraSize.width, cameraSize.height);
        parameters.setRotation(90);
        parameters.setPreviewFrameRate(8);

        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                int[] imageAvant;

                public void onPreviewFrame(final byte[] data, Camera camera) {
                    final int frameHeight = camera.getParameters().getPreviewSize().height;
                    final int frameWidth = camera.getParameters().getPreviewSize().width;
                    // number of pixels//transforms NV21 pixel data into RGB pixels

                    camera.addCallbackBuffer(data);
                    return;
                }
            });
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d("Error" ,"Error starting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
