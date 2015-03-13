package AndroidCameraServerPackage;

public class CheckMovement{
     int[] mImageAvant;
     public int[] mImageActual;
     int mDiff = 0;
     boolean mShowMov;
     
     public CheckMovement(int[] imageAvant, int[] imageActual, boolean showMov){
         mImageAvant = imageAvant;
         mImageActual = imageActual;
         mShowMov = showMov;
     }
     
     public int CheckDiff(){
        if(mImageAvant != null) {
            for (int x = 0; x < mImageActual.length; x++) {
                // Décalage de bits pour trouver les valeurs RGB actuelles
                int rActual = (mImageActual[x] & 0x00ff0000) >> 16;
                int gActual = (mImageActual[x] & 0x0000ff00) >> 8;
                int bActual = mImageActual[x] & 0x0000ff;

                // Décalage de bits pour trouver les valeurs RGB de l'ancienne image
                int rOld = (mImageAvant[x] & 0x00ff0000) >> 16;
                int gOld = (mImageAvant[x] & 0x0000ff00) >> 8;
                int bOld = mImageAvant[x] & 0x0000ff;

                if(rActual <= rOld -15 || rActual >= rOld + 15)
                {
                    mDiff++;
                    if(mShowMov){
                        mImageActual[x] = 0xffff0000;
                    }

                }
                else {
                    if (gActual <= gOld -15 || gActual >= gOld + 15) {
                        mDiff++;
                        if(mShowMov){
                        mImageActual[x] = 0xffff0000;
                        }
                    }
                    else
                    {
                        if (bActual <= bOld -15 || bActual >= bOld + 15) {
                            mDiff++;
                            if(mShowMov){
                                mImageActual[x] = 0xffff0000;
                            }
                        }
                    }
                }
            }
            return mDiff;
        }
        else{
            return 0;
        }
    }
}
