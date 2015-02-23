package stevenseesall.com.camera;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.IOException;

public class MainActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findViewById(R.id.btn_Connect).setOnClickListener(new Connection_OnClickListener(this));
    }

    private class Connection_OnClickListener implements View.OnClickListener {

        Activity mActivity;
        EditText mServerIPField = (EditText)findViewById(R.id.txtIp);
        FrameLayout mCameraPreview;

        public Connection_OnClickListener(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(View v) {
            setContentView(R.layout.remote_mode);
            mCameraPreview = (FrameLayout)findViewById(R.id.camera_preview);

            String serverIp = mServerIPField.getText().toString();
            serverIp = IPAddressCipher.decryptIPAddress(serverIp);
            UDPRunnable udpRunnable = new UDPRunnable(
                    getApplicationContext(),
                    mActivity,
                    mCameraPreview,
                    serverIp
            );
            udpRunnable.start();
        }
    }
}


