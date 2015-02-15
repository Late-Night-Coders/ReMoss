package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
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
    String ServerIP = "192.168.1.100";
    Boolean mSendingData = false;
    Boolean ScreenSizeSent = false;

    public CameraPreview(final Context context, Camera camera) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = camera;
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
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                public void onPreviewFrame(final byte[] data, final Camera camera) {
                    final int frameHeight = camera.getParameters().getPreviewSize().height;
                    final int frameWidth = camera.getParameters().getPreviewSize().width;


                    if(IsStandAlone) {
                        if (Libre) {
                            (new Thread() {
                                public void run() {
                                    Libre = false;
                                    int rgb[] = new int[(frameWidth * frameHeight) / mRationCheckedPixelHor / mRationCheckedPixelVer];
                                    int[] image = decodeYUV420SP(rgb, data, frameWidth, frameHeight, mRationCheckedPixelHor, mRationCheckedPixelVer);
                                    if (mImageAvant == null) {
                                        mImageAvant = image;
                                    }
                                    ThreadCheckMovement checkMovement = new ThreadCheckMovement(image, frameWidth, frameHeight,
                                            mSensibility, mImageAvant, mActivity, mMouvementTextView, mContext, isAlarmOn);
                                    (new Thread(checkMovement)).start();
                                    mImageAvant = checkMovement.getmImage();
                                    Libre = true;
                                }
                            }).start();
                        }
                    }
                    else{
                        if(!ScreenSizeSent){
                            new Thread(new SendScreenSizeTCP(frameHeight, frameWidth, ServerIP, 666)).start();
                            ScreenSizeSent = true;
                        }

                        if(!mSendingData && ScreenSizeSent){
                            mSendingData = true;
                            (new Thread() {
                                public void run() {
                                    final byte[] dataCouper = halveYUV420(data, frameWidth, frameHeight, 6);
                                    try {
                                        byte[] compressedData = compress(dataCouper);
                                        ThreadSendUDPFeed UDP = new ThreadSendUDPFeed(compressedData, ServerIP, 666);
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

                }
            });
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d("Error", "Error starting camera preview: " + e.getMessage());
        }
    }

    int[] decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height, int skippedFrameHorizontal, int skippedFrameVertical) {
        final int frameSize = (width * height / skippedFrameHorizontal) / skippedFrameVertical;
        for (int j = 0, yp = 0; j < height; j += skippedFrameHorizontal) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i += skippedFrameVertical, yp++) {
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
                } else if (r > 262143)
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

    public byte[] halveYUV420(byte[] data, int imageWidth, int imageHeight, int decrementor) {
        byte[] yuv = new byte[imageWidth/decrementor * imageHeight/decrementor * 3 / 2];
        // halve yuma
        int i = 0;
        for (int y = 0; y < imageHeight; y+=decrementor) {
            for (int x = 0; x < imageWidth; x+=decrementor) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // halve U and V color components
        for (int y = 0; y < imageHeight / 2; y+=decrementor) {
            for (int x = 0; x < imageWidth; x += (decrementor * 2)) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i++;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x + 1)];
                i++;
            }
        }
        return yuv;
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
