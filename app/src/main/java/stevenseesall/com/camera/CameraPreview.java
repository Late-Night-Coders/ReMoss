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
    int SensibilityValue = 3000;
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
                Log.d("CameraTest", "progress = " + progress);
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

                public void onPreviewFrame(byte[] data, Camera camera) {
                    int frameHeight = camera.getParameters().getPreviewSize().height;
                    int frameWidth = camera.getParameters().getPreviewSize().width;
                    // number of pixels//transforms NV21 pixel data into RGB pixels
                    int rgb[] = new int[frameWidth * frameHeight];
                    // convertion
                    int[] myPixels = decodeYUV420SP(rgb, data, frameWidth, frameHeight);

                    //Log.d("CameraTest",myPixels.toString());
                    long diff = 0;
                    if(imageAvant != null) {
                        for (int x = 0; x < myPixels.length; x++) {
                            // Décalage de bits pour trouver les valeurs RGB actuelles
                            int rActual = (myPixels[x] & 0x00ff0000) >> 16;
                            int gActual = (myPixels[x] & 0x0000ff00) >> 8;
                            int bActual = myPixels[x] & 0x0000ff;

                            // Décalage de bits pour trouver les valeurs RGB de l'ancienne image
                            int rOld = (imageAvant[x] & 0x00ff0000) >> 16;
                            int gOld = (imageAvant[x] & 0x0000ff00) >> 8;
                            int bOld = imageAvant[x] & 0x0000ff;

                            if(rActual <= rOld -25 || rActual >= rOld + 25)
                            {
                                diff++;
                            }
                            else {
                                if (gActual <= gOld -25 || gActual >= gOld + 25) {
                                    diff++;
                                }
                                else
                                {
                                    if (bActual <= bOld -25 || bActual >= bOld + 25) {
                                        diff++;
                                    }
                                }
                            }
                        }
                        Log.d("CameraTest", "R:" + Integer.toString((myPixels[45] & 0x00ff0000) >> 16) + " G:" + Integer.toString((myPixels[45] & 0x0000ff00) >> 8) + " B:" + Integer.toString(myPixels[45] & 0x0000ff));
                    }
                    //Log.d("CameraTest", Integer.toString(Sensibility));
                    switch (Sensibility)
                    {
                        case 0:
                            SensibilityValue = 10000;
                            break;
                        case 1:
                            SensibilityValue = 20000;
                            break;
                        case 2:
                            SensibilityValue = 40000;
                            break;
                        case 3:
                            SensibilityValue = 60000;
                            break;
                        case 4:
                            SensibilityValue = 80000;
                            break;
                        case 5:
                            SensibilityValue = 100000;
                            break;
                        case 6:
                            SensibilityValue = 150000;
                            break;
                    }
                    Log.d("CameraTest", Integer.toString(SensibilityValue));
                    if(diff > SensibilityValue){
                        Log.d("CameraTest", "MOUVEMENT");
                        Log.d("CameraTest", Long.toString(diff));
                    }
                    else{
                        Log.d("CameraTest", "ARRET");
                        Log.d("CameraTest", Long.toString(diff));
                    }
                    imageAvant = myPixels;
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

    int[] decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++) {       int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) {
                    r = 0;
                }
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }
}
