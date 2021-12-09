package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.glass.ui.GlassGestureDetector;

public class CommandActivity extends BaseActivity {

    private static final String TAG = CommandActivity.class.getSimpleName();
    private static final int FEATURE_VOICE_COMMANDS = 14;
    private static final int FEATURE_DEBUG_VOICE_COMMANDS = 15;
    private static final int REQUEST_PERMISSION_CODE = 200;
    private static final String[] PERMISSIONS = new String[]{"android.permission.RECORD_AUDIO"};

    private TextView textViewVoiceCommands, textViewAction;
    private boolean enableVoiceCommands = true;

    private final void setEnableVoiceCommands(boolean value) {
        this.enableVoiceCommands = value;
        this.invalidateOptionsMenu();
    }

    private class glassGestureDetector extends GlassGestureDetector{
        /**
         * {@link GlassGestureDetector} object is constructed by usage of this method.
         *
         * @param context           is a context of the application.
         * @param onGestureListener is a listener for the gestures.
         */
        public glassGestureDetector(Context context, OnGestureListener onGestureListener) {
            super(context, onGestureListener);


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(FEATURE_VOICE_COMMANDS);
        requestWindowFeature(FEATURE_DEBUG_VOICE_COMMANDS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        textViewAction = findViewById(R.id.action);
        textViewVoiceCommands = findViewById(R.id.voiceCommands);

        ActivityCompat.requestPermissions(
                CommandActivity.this,
                PERMISSIONS,
                REQUEST_PERMISSION_CODE
        );
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
     super.onCreatePanelMenu(featureId, menu);
        if (enableVoiceCommands) {
         //   menuInflater.inflate(R.menu.main_menu, menu);
            final MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.command_menu, menu);
            textViewVoiceCommands.setText(getString(R.string.main_voice_commands));
            textViewAction.setText(getString(R.string.swipe_forward_to_disable_voice_commands));
            return true;
        } else {
            textViewVoiceCommands.setText(getString(R.string.disabled));
            textViewAction.setText(getString(R.string.swipe_forward_to_enable_voice_commands));
            return false;
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                startActivity(new Intent(CommandActivity.this, AlternativeActivity.class));
                return true;

            case SWIPE_BACKWARD:
                toggleVoiceCommands();

                return true;

            case SWIPE_FORWARD:
                toggleVoiceCommands();

                return true;

            case SWIPE_DOWN:
                finish();
                return true;

            default:

                return super.onGesture(gesture);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission denied. Voice commands menu is disabled.");
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void toggleVoiceCommands() {
        enableVoiceCommands = !enableVoiceCommands;
        setEnableVoiceCommands(enableVoiceCommands);
        if (enableVoiceCommands){
            Log.e("Toggle", "true");
        }else{
            Log.e("Toggle", "false");
        }

    }
}