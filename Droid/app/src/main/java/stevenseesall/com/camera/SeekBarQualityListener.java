package stevenseesall.com.camera;

import android.util.Log;
import android.widget.SeekBar;

/**
 * Created by Administrateur on 2015-03-09.
 */
public class SeekBarQualityListener implements SeekBar.OnSeekBarChangeListener {
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        Log.d("CameraTest", Integer.toString(progress));
    }
}
