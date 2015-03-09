package stevenseesall.com.camera;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Administrateur on 2015-03-09.
 */
public class SeekBarQualityListener implements SeekBar.OnSeekBarChangeListener {

    TextView mQualityTextView;

    public SeekBarQualityListener(TextView textView){
        mQualityTextView = textView;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
        // TODO Auto-generated method stub
        mQualityTextView.setText(progress);
    }
}
