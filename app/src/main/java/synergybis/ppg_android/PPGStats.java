package synergybis.ppg_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.media.MediaPlayer;
import android.widget.Button;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;

import java.util.UUID;

public class PPGStats extends Activity {
    private static final String PPG_UUID = "0a3e5f23-59e7-46c8-9c46-66afa7347432";

    private static final int keyMessage = 1234567890;
    private static boolean pinFlag = false;   //When this is true, wait for the KABOOM. Ha.

    private PebbleDataReceiver mDataReceiver = null;
    private final StringBuilder mDisplayText = new StringBuilder();

//    SharedPreferences records =

    private static MediaPlayer mediaPlayer;

    private static String audioFilePath;
    private static TextView armedDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppgstats);
        armedDisplay = (TextView)findViewById(R.id.armedDisplay);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ppgstats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDataReceiver != null) {
            unregisterReceiver(mDataReceiver);
            mDataReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDataReceiver = new PebbleDataReceiver(UUID.fromString(PPG_UUID)) {
            @Override
            public void receiveData(final Context context, int transactionId, PebbleDictionary data) {
                PebbleKit.sendAckToPebble(context, transactionId);
                mDisplayText.setLength(0);
                mDisplayText.append(data.getString(keyMessage));
                playSound();
                pinFlag = !pinFlag;
                updateUI();
            }
        };

        PebbleKit.registerReceivedDataHandler(this, mDataReceiver);
    }

    private void updateUI() {
        armedDisplay.setText(mDisplayText.toString());
    }

    private void playSound() {
        if (pinFlag) {
            mediaPlayer=MediaPlayer.create(PPGStats.this,R.raw.explosion);
        } else {
            mediaPlayer=MediaPlayer.create(PPGStats.this,R.raw.pullpin);
        }
        mediaPlayer.start();
    }

}
