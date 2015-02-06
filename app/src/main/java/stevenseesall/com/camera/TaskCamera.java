package stevenseesall.com.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.SeekBar;

/**
 * Created by Administrateur on 2015-02-06.
 */
public class TaskCamera extends AsyncTask<Void, Void, Void> {
    SeekBar seekBar;
    FrameLayout previewFrame;
    Context context;

    public TaskCamera(SeekBar skBar, FrameLayout frmLayout, Context ctx){
        seekBar = skBar;
        previewFrame = frmLayout;
        context = ctx;
    }

    @Override
    protected void onPreExecute()
    {
        boolean hasCamera = checkCameraHardware(context);
        if (hasCamera == false) {
            throw new RuntimeException("No camera found on device");
        }

        final Camera camera = getCameraInstance();
        final CameraPreview preview = new CameraPreview(context, camera, seekBar);
        previewFrame.addView(preview);
    }

    @Override
    protected Void doInBackground(Void... contexts) {
        return null;
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera camera = null;

        try {
            camera = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            throw new RuntimeException("No camera found on device");
        }

        return camera; // returns null if camera is unavailable
    }

    private int[] DetectMovement(int frameWidth, int frameHeight, byte[] data, int[] imageAvant, int Sensibility)
    {
        int SensibilityValue = 0;
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
            // Log.d("CameraTest", "R:" + Integer.toString((myPixels[45] & 0x00ff0000) >> 16) + " G:" + Integer.toString((myPixels[45] & 0x0000ff00) >> 8) + " B:" + Integer.toString(myPixels[45] & 0x0000ff));
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
                SensibilityValue = 120000;
                break;
        }
        //Log.d("CameraTest", Integer.toString(SensibilityValue));
        if(diff > SensibilityValue){
            Log.d("CameraTest", "MOUVEMENT");
            Log.d("CameraTest", Long.toString(diff));
        }
        else{
            Log.d("CameraTest", "ARRET");
            Log.d("CameraTest", Long.toString(diff));
        }
        return myPixels;
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
