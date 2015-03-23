package stevenseesall.com.camera;

import android.widget.EditText;
import android.widget.SeekBar;

public class SeekBarQualityListener implements SeekBar.OnSeekBarChangeListener {

    EditText mQualityTextView;

    public SeekBarQualityListener(EditText textView){
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
        mQualityTextView.setText(Integer.toString(progress));
    }
}
