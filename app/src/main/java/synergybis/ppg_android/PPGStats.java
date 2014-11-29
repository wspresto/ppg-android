package synergybis.ppg_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.media.MediaPlayer;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;

import java.util.UUID;

public class PPGStats extends Activity {
    private static final String PPG_UUID = "0a3e5f23-59e7-46c8-9c46-66afa7347432";

    private static final int keyMessage = 1234567890;
    private static final int keyThrown = 987654321;

    private PebbleDataReceiver mDataReceiver = null;
    private final StringBuilder mDisplayText = new StringBuilder();


    private static MediaPlayer mediaPlayer;

    private static TextView armedDisplay;

    private int soundFileToBePlayed = -1; //this is the id of the file name for the currently selected sound clip

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

        soundFileToBePlayed = item.getItemId();
        //TODO: update layout text to show currently selected file profile
        mediaPlayer=MediaPlayer.create(PPGStats.this,R.raw.ready1);
        playSound();

        int id = soundFileToBePlayed;
        if (id == R.id.Deagle) {
            mediaPlayer=MediaPlayer.create(PPGStats.this,R.raw.deagle);
        } else if (id == R.id.PewPew) {
            mediaPlayer=MediaPlayer.create(PPGStats.this,R.raw.laser);
        } else if (id == R.id.Awp) {
            mediaPlayer=MediaPlayer.create(PPGStats.this,R.raw.awp);
        } else if (id == R.id.Mac10) {
            mediaPlayer=MediaPlayer.create(PPGStats.this,R.raw.mac10);
        } else if (id == R.id.FiveSeven) {
            mediaPlayer=MediaPlayer.create(PPGStats.this,R.raw.fiveseven);
        }

        playSound();
        updateUI("Ready to play!");
        return true;
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
                playSound();
            }
        };
        PebbleKit.registerReceivedDataHandler(this, mDataReceiver);
    }

    private void updateUI(String text) {
        armedDisplay.setText(text);
    }

    private void playSound() {
        mediaPlayer.start();
    }

}
