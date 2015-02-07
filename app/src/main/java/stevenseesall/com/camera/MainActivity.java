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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ThreadCamera TC = new ThreadCamera(getApplicationContext(), this,(SeekBar) findViewById(R.id.seekBar),(FrameLayout) findViewById(R.id.camera_preview), (EditText) findViewById(R.id.editText));
        TC.start();

        // Create an instance of Camera
        /*final Camera camera = getCameraInstance();
        boolean hasCamera = checkCameraHardware(getApplicationContext());

        if (hasCamera == false) {
            throw new RuntimeException("No camera found on device");
        }

        // Create our Preview view and set it as the content of our activity.
        final CameraPreview preview = new CameraPreview(this, camera, (SeekBar) findViewById(R.id.seekBar));
        final FrameLayout previewFrame = (FrameLayout) findViewById(R.id.camera_preview);
        previewFrame.addView(preview);*/
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
}


