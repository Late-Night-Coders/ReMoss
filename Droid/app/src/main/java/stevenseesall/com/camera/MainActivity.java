package stevenseesall.com.camera;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.IOException;

public class MainActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_PC).setOnClickListener(new PC_OnClickListener(this));
        findViewById(R.id.btn_StandAlone).setOnClickListener(new StandAlone_OnClickListener(this));
    }

    private class PC_OnClickListener implements View.OnClickListener {

        Activity mActivity;

        public PC_OnClickListener(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(View v) {
            setContentView(R.layout.login);
        }
    }

    private class StandAlone_OnClickListener implements View.OnClickListener {

        Activity mActivity;

        public StandAlone_OnClickListener(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(View v) {
            setContentView(R.layout.stand_alone);
            StandAloneRunnable TC = null;
            try {
                TC = new StandAloneRunnable(getApplicationContext(), mActivity, (SeekBar) findViewById(R.id.seekBar),
                        (FrameLayout) findViewById(R.id.camera_preview), (TextView) findViewById(R.id.textView), (TextView) findViewById(R.id.textView3), (ToggleButton) findViewById(R.id.toggleButton));
            } catch (IOException e) {
                e.printStackTrace();
            }
            TC.start();
        }
    }

}


