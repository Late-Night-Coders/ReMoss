package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Fred on 2/7/2015.
 */
public class ThreadCamera extends Thread {
    Context mContext;
    Camera mCamera;
    Activity mActivity;
    SeekBar mSeekBar;
    FrameLayout mFrameLayout;
    int mSensibility = 0;
    private SurfaceHolder mHolder;
    boolean Libre = true;
    int[] mImageAvant;
    int mSkippedFrameHorizontal = 2;
    int mSkippedFrameVertical = 2;
    TextView mTextView;
    TextView mMouvementTextView;
    Boolean mMouvement = false;
    Boolean isAlarmRunning = false;
    ToggleButton mToggleButton;
    Boolean isAlarmOn = false;
    Boolean sendingData = false;

    String ServerIP = "10.1.250.100";
    public static final int Port = 666;


    public ThreadCamera(final Context context, Activity activity, SeekBar seekBar, FrameLayout frameLayout, TextView textView,
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
                }
                else {
                    isAlarmOn = false;
                }
            }
        });
    }




    public void run() {
        boolean hasCamera = checkCameraHardware(mContext);

        if (!hasCamera) {
            throw new RuntimeException("No camera found on device");
        }
        mCamera = getCameraInstance();
        Looper.prepare();
        final CameraPreview preview = new CameraPreview(mContext);
        final FrameLayout previewFrame = mFrameLayout;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewFrame.addView(preview);
            }
        });
        Looper.loop();

    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera camera;
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            throw new RuntimeException("No camera found on device");
        }

        return camera; // returns null if camera is unavailable

    }

    private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        public CameraPreview(final Context context) {
            super(context);

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mSensibility = progress;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText(Integer.toString(mSensibility));
                        }
                    });

                    //Toast.makeText(context, "Sensibilité: " + progress, Toast.LENGTH_SHORT).show();
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

            try {
                mCamera.stopPreview();
            } catch (Exception e) {

            }

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
                    public void onPreviewFrame(final byte[] data, final Camera camera) {
                        final int frameHeight = camera.getParameters().getPreviewSize().height;
                        final int frameWidth = camera.getParameters().getPreviewSize().width;

                        if(Libre) {
                            new Thread(new Runnable() {
                                public void run() {
                                    Libre = false;
                                    CheckMovement(data, frameWidth, frameHeight,mSkippedFrameHorizontal,
                                            mSkippedFrameVertical, mSensibility);

                                    if(!sendingData) {
                                        new Thread(new Runnable() {
                                            public void run() {
                                                sendingData = true;
                                                try {
                                                    Socket s = new Socket(ServerIP, Port);
                                                    OutputStream out = s.getOutputStream();
                                                    DataOutputStream dos = new DataOutputStream(out);

                                                    byte[] dataCouper = halveYUV420(data, 1920, 1080);
                                                    dos.writeInt(dataCouper.length);
                                                    if (dataCouper.length > 0) {
                                                        dos.write(dataCouper, 0, dataCouper.length);
                                                    }

                                                    out.close();
                                                    s.close();
                                                } catch (IOException e) {
                                                    Log.d("CameraTest", e.getMessage());
                                                }
                                                sendingData = false;
                                            }
                                        }).start();
                                    }

                                    Libre = true;
                                }
                            }).start();
                        }
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

        public void CheckMovement(byte[] data, int frameWidth, int frameHeight,
                                  int skippedFrameHorizontal, int skippedFrameVertical, int sensibility){

            int sensibilityValue = 0;
            int rgb[] = new int[(frameWidth * frameHeight) / skippedFrameHorizontal / skippedFrameVertical];

            final int[] myPixels = decodeYUV420SP(rgb, data, frameWidth,
                    frameHeight, skippedFrameHorizontal, skippedFrameHorizontal);

            long diff = 0;
            if(mImageAvant != null) {
                for (int x = 0; x < myPixels.length; x++) {
                    // Décalage de bits pour trouver les valeurs RGB actuelles
                    int rActual = (myPixels[x] & 0x00ff0000) >> 16;
                    int gActual = (myPixels[x] & 0x0000ff00) >> 8;
                    int bActual = myPixels[x] & 0x0000ff;

                    // Décalage de bits pour trouver les valeurs RGB de l'ancienne image
                    int rOld = (mImageAvant[x] & 0x00ff0000) >> 16;
                    int gOld = (mImageAvant[x] & 0x0000ff00) >> 8;
                    int bOld = mImageAvant[x] & 0x0000ff;

                    if(rActual <= rOld -15 || rActual >= rOld + 15)
                    {
                        diff++;
                    }
                    else {
                        if (gActual <= gOld -15 || gActual >= gOld + 15) {
                            diff++;
                        }
                        else
                        {
                            if (bActual <= bOld -15 || bActual >= bOld + 15) {
                                diff++;
                            }
                        }
                    }
                }
            }
            switch (sensibility)
            {
                case 0:
                    sensibilityValue = 5000;
                    break;
                case 1:
                    sensibilityValue = 10000;
                    break;
                case 2:
                    sensibilityValue = 20000;
                    break;
                case 3:
                    sensibilityValue = 30000;
                    break;
                case 4:
                    sensibilityValue = 40000;
                    break;
                case 5:
                    sensibilityValue = 50000;
                    break;
                case 6:
                    sensibilityValue = 75000;
                    break;
            }

            if(diff > sensibilityValue){
                //Log.d("CameraTest", "MOUVEMENT");
                //Log.d("CameraTest", Long.toString(diff));
                if(!mMouvement)
                {
                    //Évènement mouvement
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMouvementTextView.setText("Mouvement!");
                        }
                    });

                    if(!isAlarmRunning && isAlarmOn) {
                        new Thread(new Runnable() {
                            public void run() {
                                isAlarmRunning = true;
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                if(notification != null){
                                    notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                                    if(notification != null){
                                        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    }
                                }
                                Ringtone r = RingtoneManager.getRingtone(mContext, notification);
                                r.play();
                                while(r.isPlaying() && isAlarmOn){

                                }
                                r.stop();
                                isAlarmRunning = false;
                            }
                        }).start();
                    }


                }
                mMouvement = true;
            }
            else{
                //Log.d("CameraTest", "ARRET");
                //Log.d("CameraTest", Long.toString(diff));
                if(mMouvement)
                {
                    //Évènement Arrêt
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMouvementTextView.setText("Arrêt!");
                        }
                    });
                }
                mMouvement = false;
            }
            mImageAvant = myPixels;
        }

        int[] decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height, int skippedFrameHorizontal, int skippedFrameVertical) {
            final int frameSize = (width * height / skippedFrameHorizontal) / skippedFrameVertical;
            for (int j = 0, yp = 0; j < height; j+=skippedFrameHorizontal) {
                int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
                for (int i = 0; i < width; i+=skippedFrameVertical, yp++) {
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

        public byte[] halveYUV420(byte[] data, int imageWidth, int imageHeight) {
            byte[] yuv = new byte[imageWidth/4 * imageHeight/4 * 3 / 2];
            // halve yuma
            int i = 0;
            for (int y = 0; y < imageHeight; y+=4) {
                for (int x = 0; x < imageWidth; x+=4) {
                    yuv[i] = data[y * imageWidth + x];
                    i++;
                }
            }
            // halve U and V color components
            for (int y = 0; y < imageHeight / 2; y+=4) {
                for (int x = 0; x < imageWidth; x += 8) {
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                    i++;
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x + 1)];
                    i++;
                }
            }
            return yuv;
        }
    }
}


/* Code validation alarme

Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

if(alert == null){
    // alert is null, using backup
    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    // I can't see this ever being null (as always have a default notification)
    // but just incase
    if(alert == null) {
        // alert backup is null, using 2nd backup
        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    }
}
 */

/*Camera.Parameters parameters = mCamera.getParameters();
                                mCamera.stopPreview();
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                                mCamera.setParameters(parameters);
                                mCamera.startPreview();*/