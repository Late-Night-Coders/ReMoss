package stevenseesall.com.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of Camera
        final Camera camera = getCameraInstance();
        checkCameraHardware(getApplicationContext());

        // Create our Preview view and set it as the content of our activity.
        final CameraPreview preview = new CameraPreview(this, camera);
        final FrameLayout previewFrame = (FrameLayout) findViewById(R.id.camera_preview);
        previewFrame.addView(preview);

        final Button captureButton = (Button) findViewById(R.id.button_capture);
        final PictureCallback pictureCallback = new PictureCallback();

        captureButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}


