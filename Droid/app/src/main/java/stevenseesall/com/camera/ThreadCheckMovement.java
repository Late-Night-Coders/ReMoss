package stevenseesall.com.camera;

import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Fred on 2/10/2015.
 */
public class ThreadCheckMovement implements Runnable {
    int[] mImage;
    int[] mImageAvant;
    int mFrameWidth;
    int mFrameHeight;
    int mSkippedFrameHorizontal;
    int mSkippedFrameVertical;
    int mSensibility;
    Activity mActivity;
    TextView mMouvementTextView;
    Boolean isAlarmOn;
    Boolean isAlarmRunning = false;

    ToggleButton mToggleButton;
    Context mContext;

    public ThreadCheckMovement(int[] image, int frameWidth, int frameHeight, int skippedFrameHorizontal, int skippedFrameVertical,
                               int sensibility, int[] imageAvant, Activity activity, TextView mouvementTextView, ToggleButton toggleButton,
                               Context context, Boolean isalarmOn)
    {
        mImage = image;
        mFrameWidth = frameWidth;
        mFrameHeight = frameHeight;
        mSkippedFrameHorizontal = skippedFrameHorizontal;
        mSkippedFrameVertical = skippedFrameVertical;
        mSensibility = sensibility;
        mImageAvant = imageAvant;
        mActivity = activity;
        mMouvementTextView = mouvementTextView;
        mToggleButton = toggleButton;
        mContext = context;
        isAlarmOn = isalarmOn;
    }

    public int[] getmImage(){
        return mImage;
    }

    public void run(){
        int sensibilityValue = 0;

        long diff = 0;
        if(mImageAvant != null) {
            for (int x = 0; x < mImage.length; x++) {
                // Décalage de bits pour trouver les valeurs RGB actuelles
                int rActual = (mImage[x] & 0x00ff0000) >> 16;
                int gActual = (mImage[x] & 0x0000ff00) >> 8;
                int bActual = mImage[x] & 0x0000ff;

                // Décalage de bits pour trouver les valeurs RGB de l'ancienne image
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
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    if(notification != null){
                        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        if(notification != null){
                            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
