package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.glass.ui.GlassGestureDetector;

import java.util.List;

public class AlternativeActivity extends BaseActivity {

    private boolean useMainVoiceCommands = true;
    private final int REQUEST_CODE = 999;
    private TextView textViewVoiceCommands;
    private static final int FEATURE_VOICE_COMMANDS = 14;


    private void SetuseMainVoiceCommands(boolean value) {
        this.useMainVoiceCommands = value;
        this.sendBroadcast(new Intent("reload-voice-commands"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(FEATURE_VOICE_COMMANDS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative);

        textViewVoiceCommands = findViewById(R.id.voiceCommands);

    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
      //  return super.onCreatePanelMenu(featureId, menu);

        if (useMainVoiceCommands) {
            final MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.command_menu, menu);
        //    menuInflater.inflate(R.menu.main_menu, menu)
            textViewVoiceCommands.setText(getString(R.string.main_voice_commands));
            return true;
        } else {
            final MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alternative_menu, menu);
          //  menuInflater.inflate(R.menu.alternative_menu, menu)
            textViewVoiceCommands.setText(getString(R.string.alternative_voice_commands));
            return true;
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
       // return super.onGesture(gesture);

        switch (gesture){

            case TAP:
                requestVoiceRecognition();

                return true;

            case SWIPE_DOWN:
                finish();
                return true;
            case SWIPE_FORWARD:
                useMainVoiceCommands = !useMainVoiceCommands;
                SetuseMainVoiceCommands(useMainVoiceCommands);

                return true;

            case SWIPE_BACKWARD:
                useMainVoiceCommands = !useMainVoiceCommands;
                SetuseMainVoiceCommands(useMainVoiceCommands);
                return true;

            default:
                return super.onGesture(gesture);
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            // Handle selected menu item
            case R.id.manual:
                // Handle edit action
                Log.e("Command", "Manual");
                return true;

            case R.id.job:
                // Handle edit action
                Log.e("Command", "JOb");

                return true;
            case R.id.monitor:
                // Handle edit action
                Log.e("Command", "Monitor");

                return true;
            case R.id.bCommand4:
                // Handle edit action
                Log.e("Command", "Design");

                return true;
            case R.id.bCommand5:
                // Handle edit action
                Log.e("Command", "Machine");

                return true;

            default:
                return super.onContextItemSelected(item);
        }


    }

    private void requestVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        this.startActivityForResult(intent, this.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null){
            List results = (List)data.getStringArrayListExtra("android.speech.extra.RESULTS");
            if (results != null && results.size() > 0) {
                for (int i=0; i<results.size() ; i++){
                    Log.d("VoiceActivity", "result: " + String.valueOf(results.get(i).toString()));
                    CharSequence var5 = (CharSequence)results.get(i).toString();
                    if (var5.length() != 0) {
                        Log.e("Result", ((String)results.get(i)));

                        switch (results.get(i).toString().toLowerCase()){
                            case "monitor":
                                startActivity(new Intent(AlternativeActivity.this, MonitorActivity.class));
                                break;

                            case "manual":
                                startActivity(new Intent(AlternativeActivity.this, LyoManualActivity.class));
                                break;

                            case "quality":
                                startActivity(new Intent(AlternativeActivity.this, DQActivity.class));
                                break;

                            case "machine":
                                startActivity(new Intent(AlternativeActivity.this, MachinesActivity.class));
                                break;

                            case "job":
                                startActivity(new Intent(AlternativeActivity.this, JobActivity.class));
                                break;

                            default:
                                // Do nothing
                                break;

                        }
                    }
                }
            }else {
                Log.e("Result", "Size 0");
            }

        } else {
            Log.d("VoiceActivity", "Result not OK");
        }
    }
}