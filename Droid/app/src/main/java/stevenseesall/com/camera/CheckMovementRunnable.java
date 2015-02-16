package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.TextView;

public class CheckMovementRunnable implements Runnable {
    int[] mImage;
    int[] mImageAvant;
    int mSensibility;
    Activity mActivity;
    TextView mMouvementTextView;
    Boolean isAlarmOn;
    Boolean isAlarmRunning = false;
    Context mContext;

    public int[] getmImage(){
        return mImage;
    }

    public CheckMovementRunnable(int[] image, int frameWidth, int frameHeight, int sensibility, int[] imageAvant, Activity activity, TextView mouvementTextView, Context context, Boolean isalarmOn)
    {
        mImage = image;
        mSensibility = sensibility;
        mImageAvant = imageAvant;
        mActivity = activity;
        mMouvementTextView = mouvementTextView;
        mContext = context;
        isAlarmOn = isalarmOn;
    }

    public void run(){
        int sensibilityValue = 0;

        long diff = 0;
        if(mImageAvant != null) {
            for (int x = 0; x < mImage.length; x++) {
                int rActual = (mImage[x] & 0x00ff0000) >> 16;
                int gActual = (mImage[x] & 0x0000ff00) >> 8;
                int bActual = mImage[x] & 0x0000ff;

                int rOld = (mImageAvant[x] & 0x00ff0000) >> 16;
                int gOld = (mImageAvant[x] & 0x0000ff00) >> 8;
                int bOld = mImageAvant[x] & 0x0000ff;

                if(rActual <= rOld -15 || rActual >= rOld + 15)
                {
                    diff++;
                }
                else {
                    if (gActual <= gOld -15 || gActual >= gOld + 15) {
                        diff++;
                    }
                    else
                    {
                        if (bActual <= bOld -15 || bActual >= bOld + 15) {
                            diff++;
                        }
                    }
                }
            }
        }

        switch (mSensibility)
        {
            case 0:
                sensibilityValue = 5000;
                break;
            case 1:
                sensibilityValue = 10000;
                break;
            case 2:
                sensibilityValue = 20000;
                break;
            case 3:
                sensibilityValue = 30000;
                break;
            case 4:
                sensibilityValue = 40000;
                break;
            case 5:
                sensibilityValue = 50000;
                break;
            case 6:
                sensibilityValue = 75000;
                break;
        }

        if(diff > sensibilityValue){
                startAlarm();
        }
        else{
                onNoMovement();
        }
    }

    private void startAlarm(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMouvementTextView.setText("Mouvement!");
            }
        });

        if(!isAlarmRunning && isAlarmOn) {
            new Thread(new Runnable() {
                public void run() {
                    isAlarmRunning = true;
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if(notification != null){
                        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        if(notification != null){
                            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        }
                    }
                    Ringtone r = RingtoneManager.getRingtone(mContext, notification);
                    r.play();
                    while(r.isPlaying() && isAlarmOn){

                    }
                    r.stop();
                    isAlarmRunning = false;
                }
            }).start();
        }
    }

    private void onNoMovement(){
        //Évènement Arrêt
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMouvementTextView.setText("Arrêt!");
            }
        });
    }
}
