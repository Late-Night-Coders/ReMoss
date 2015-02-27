package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;

/**
 * Created by Fred on 2/15/2015.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    Context mContext;
    Camera mCamera;
    Activity mActivity;
    SeekBar mSeekBar;
    int mSensibility = 0;
    private SurfaceHolder mHolder;
    boolean Libre = true;
    int[] mImageAvant;
    TextView mTextView;
    TextView mMouvementTextView;
    ToggleButton mToggleButton;
    Boolean isAlarmOn = false;
    int mRationCheckedPixelHor = 3;
    int mRationCheckedPixelVer = 3;
    Boolean IsStandAlone = false;
    String mServerIP;
    String mNoCam;
    int mNoPort;
    boolean mGettingPort = false;

    Boolean mSendingData = false;
    Boolean ScreenSizeSent = false;

    public CameraPreview(final Context context, Camera camera, String mServerIP) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = camera;
        this.mServerIP = mServerIP;
    }

    public CameraPreview(final Context context, Camera camera, Activity activity, SeekBar seekBar,
                         int[] imageAvant, TextView textView, TextView mouvementTextView,
                         ToggleButton toggleButton, Boolean alarmOn, Boolean isStandAlone) {
        super(context);
        mContext = context;
        mCamera = camera;
        mActivity = activity;
        mSeekBar = seekBar;
        mImageAvant = imageAvant;
        mTextView = textView;
        mMouvementTextView = mouvementTextView;
        mToggleButton = toggleButton;
        isAlarmOn = alarmOn;
        IsStandAlone = isStandAlone;


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSensibility = progress;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(Integer.toString(mSensibility));
                    }
                });
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mHolder = getHolder();
        mHolder.addCallback(this);
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
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mHolder.getSurface() == null) {
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
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                public void onPreviewFrame(final byte[] data, final Camera camera) {
                    final int frameHeight = camera.getParameters().getPreviewSize().height;
                    final int frameWidth = camera.getParameters().getPreviewSize().width;

                    if(!ScreenSizeSent && !mGettingPort){
                        (new Thread() {
                            public void run() {
                                mGettingPort = true;
                                SendScreenSizeTCP TCP = new SendScreenSizeTCP(frameHeight, frameWidth, mServerIP);
                                TCP.GetCam();
                                mNoPort = TCP.mPort;
                                ScreenSizeSent = true;
                            }
                        }).start();

                    }

                    if(!mSendingData && ScreenSizeSent){
                        mSendingData = true;
                        (new Thread() {
                            public void run() {
                                int format =  ImageFormat.NV21;
                                YuvImage yuv_image = new YuvImage(data, format, frameWidth, frameHeight, null);
                                Rect rect = new Rect(0, 0, frameWidth, frameHeight);
                                ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
                                yuv_image.compressToJpeg(rect, 50, output_stream);
                                byte[] byt=output_stream.toByteArray();
                                try {
                                    byte[] compressedData = compress(byt);
                                    Log.d("CameraTest", Integer.toString(compressedData.length));
                                    ThreadSendUDPFeed UDP = new ThreadSendUDPFeed(compressedData, mServerIP, mNoPort);
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
                }
            });
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d("Error", "Error starting camera preview: " + e.getMessage());
        }
    }

    public byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        deflater.finish();
        byte[] buffer = new byte[25000];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();

        deflater.end();

        return output;
    }
}
