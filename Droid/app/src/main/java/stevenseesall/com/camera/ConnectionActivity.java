package stevenseesall.com.camera;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import stevenseesall.com.camera.R;

public class ConnectionActivity extends ActionBarActivity {

    TextView txtIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        findViewById(R.id.btn_Connect).setOnClickListener(new Connection_OnClickListener(this));
        txtIp = (TextView)findViewById(R.id.txtIp);
    }

    private class Connection_OnClickListener implements View.OnClickListener {

        Activity mActivity;

        public Connection_OnClickListener(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(View v) {
            setContentView(R.layout.remote_mode);
            UDPRunnable UDPRunnable = new UDPRunnable(getApplicationContext(), mActivity, (FrameLayout) findViewById(R.id.camera_preview), (String)txtIp.getText());
            UDPRunnable.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection, menu);
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
