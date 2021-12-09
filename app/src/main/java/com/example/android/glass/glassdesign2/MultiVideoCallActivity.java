package com.example.android.glass.glassdesign2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.android.glass.glassdesign2.data.DataCallLog;
import com.example.android.glass.glassdesign2.data.DataCallMedia;
import com.example.android.glass.glassdesign2.data.DataNoti;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MultiVideoCallActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{

    private static final String TAG = MultiVideoCallActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_CODE = 124;
    private static final int REQUEST_CODE = 129;
    private static final int REQUEST_CODE_EXIT = 130;
    private String MENU_KEY="menu_key";

    private Session session;
    private Publisher publisher;

    private ArrayList<Subscriber> subscribers = new ArrayList<>();
    private HashMap<Stream, Subscriber> subscriberStreams = new HashMap<>();

    private ConstraintLayout container;
    private RecyclerView recyclerView, rcViewOptions;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private String step, manual_key, manual_name, user;

    private String call_id = "";
    private ArrayList<DataCallMedia> arrayList = new ArrayList<>();
    private ArrayList<String> optionsArraylist = new ArrayList<>();
    private MediaAdapter adapter;
    private OptionsAdapter optionsAdapter;
    private DocumentReference documentReference, notiReference;
    private int count = 0;
    private Boolean call_flag = false;
    private String pref_id, resolution;
    private int options_index;
    private int hidden_count;
    private CardView cardView;
    private TextView tvDisabled;
    private boolean flag_single = false;
    private int index_single = -1;
    private ArrayList<Integer> videoList = new ArrayList<>();

    private SeekBar seekbar;

    private AudioManager audioManager;

    private PublisherKit.PublisherListener publisherListener = new PublisherKit.PublisherListener() {
        @Override
        public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
            Log.d(TAG, "onStreamCreated: Own stream " + stream.getStreamId() + " created");
        }

        @Override
        public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
            Log.d(TAG, "onStreamDestroyed: Own stream " + stream.getStreamId() + " destroyed");
        }

        @Override
        public void onError(PublisherKit publisherKit, OpentokError opentokError) {
            finishWithMessage("PublisherKit error: " + opentokError.getMessage());
        }
    };

    private Session.SessionListener sessionListener = new Session.SessionListener() {
        @Override
        public void onConnected(Session session) {
            Log.d(TAG, "onConnected: Connected to session " + session.getSessionId());

            session.publish(publisher);
        }

        @Override
        public void onDisconnected(Session session) {
            Log.d(TAG, "onDisconnected: disconnected from session " + session.getSessionId());

            MultiVideoCallActivity.this.session = null;
        }

        @Override
        public void onError(Session session, OpentokError opentokError) {
            finishWithMessage("Session error: " + opentokError.getMessage());
        }

        @Override
        public void onStreamReceived(Session session, Stream stream) {
            Log.d(TAG, "onStreamReceived: New stream " + stream.getStreamId() + " in session " + session.getSessionId());

            final Subscriber subscriber = new Subscriber.Builder(MultiVideoCallActivity.this, stream).build();
            session.subscribe(subscriber);
            subscribers.add(subscriber);
            subscriberStreams.put(stream, subscriber);

            int subId = getResIdForSubscriberIndex(subscribers.size() - 1);
            int videoId = getResIdForVideoIndex(subscribers.size() -1);
            int audioId = getResIdForAudioIndex(subscribers.size() -1);

            subscriber.getView().setId(subId);


            subscriber.setVideoListener(new SubscriberKit.VideoListener() {
                @Override
                public void onVideoDataReceived(SubscriberKit subscriberKit) {

                }

                @Override
                public void onVideoDisabled(SubscriberKit subscriberKit, String s) {
                 //   subscriber.getView().setVisibility(View.GONE);
                 //   CheckDisabled();
                    calculateLayout();
                }

                @Override
                public void onVideoEnabled(SubscriberKit subscriberKit, String s) {
                 //   subscriber.getView().setVisibility(View.VISIBLE);
                  //  CheckDisabled();
                    calculateLayout();
                }

                @Override
                public void onVideoDisableWarning(SubscriberKit subscriberKit) {

                }

                @Override
                public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {

                }
            });

            container.addView(subscriber.getView());

            calculateLayout();
          //  CheckDisabled();
        }

        @Override
        public void onStreamDropped(Session session, Stream stream) {
            Log.d(TAG, "onStreamDropped: Stream " + stream.getStreamId() + " dropped from session " + session.getSessionId());

            Subscriber subscriber = subscriberStreams.get(stream);

            if (subscriber == null) {
                return;
            }

            subscribers.remove(subscriber);
            subscriberStreams.remove(stream);

            container.removeView(subscriber.getView());

            // Recalculate view Ids
            for (int i = 0; i < subscribers.size(); i++) {
                subscribers.get(i).getView().setId(getResIdForSubscriberIndex(i));
            }

            index_single = 0;
            calculateLayout();
          //  CheckDisabled();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_video_call);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        seekbar = findViewById(R.id.activity_multi_seekbar);
        seekbar.setProgress(100);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        showHide();

        pref_id = getIntent().getStringExtra("pref_id");
        resolution = getIntent().getStringExtra("resolution");


        tvDisabled = findViewById(R.id.activity_multi_tvCountDisabled);
        cardView = findViewById(R.id.activity_multi_cardview);

        container = findViewById(R.id.activity_multi_container);
        recyclerView = findViewById(R.id.activity_multi_rcView);
        rcViewOptions = findViewById(R.id.activity_multi_rcViewOptions);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        step = getIntent().getStringExtra("step");
        manual_key = getIntent().getStringExtra("manual");
        manual_name = getIntent().getStringExtra("manual_name");
        user = getIntent().getStringExtra("user");
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        adapter = new MediaAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rcViewOptions.setLayoutManager(layoutManager);
        rcViewOptions.setHasFixedSize(true);
        optionsAdapter = new OptionsAdapter(this, optionsArraylist);
        rcViewOptions.setAdapter(optionsAdapter);

        final SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rcViewOptions);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                final View foundView = snapHelper.findSnapView(layoutManager);
                if (foundView == null) {
                    return;
                }
                options_index = layoutManager.getPosition(foundView);
            }
        });

        prepareOptions();



        Log.e("Step", step + "ds");
        Log.e("Manual: ", manual_key);
        Log.e("manula name: ", manual_name);

        DocumentReference documentReference = firestore.collection("OpenTokConfig").document("BkEGCdgSefXrFkEmzcCG");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()){
                        if (document.getData().get("api_key") != null && document.getData().get("token") != null && document.getData().get("session_id") != null) {
                            OpenTokConfig.API_KEY = document.getData().get("api_key").toString();
                            OpenTokConfig.TOKEN = document.getData().get("token").toString();
                            OpenTokConfig.SESSION_ID = document.getData().get("session_id").toString();
                            if(!OpenTokConfig.isValid()) {
                                finishWithMessage("Invalid OpenTokConfig. " + OpenTokConfig.getDescription());
                            }else {
                                Log.e("OpenTokConfig: ", "API: " + OpenTokConfig.API_KEY + "\n SEssion: " + OpenTokConfig.SESSION_ID + "\n Token: " + OpenTokConfig.TOKEN);
                                requestPermissions();
                            }
                        }else {
                            finishWithMessage("Initiation Call: Missing Parameters");
                        }
                    }else {
                        finishWithMessage("Intiation Call: No document found");
                    }
                }else {
                    finishWithMessage("Initiation Call : Failed to get Credentials");
                }
            }
        });

/*
        firestore.collection("OpenTokConfig")
                .addSnapshotListener( MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        OpenTokConfig.API_KEY = "";
                        OpenTokConfig.TOKEN = "";
                        OpenTokConfig.SESSION_ID = "";

                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("api_key") != null && snapshot.getData().get("token") != null && snapshot.getData().get("session_id") != null) {
                                OpenTokConfig.API_KEY = snapshot.getData().get("api_key").toString();
                                OpenTokConfig.TOKEN = snapshot.getData().get("token").toString();
                                OpenTokConfig.SESSION_ID = snapshot.getData().get("session_id").toString();
                            }else {
                                Log.e("VideoCall", "Missing Parameters");
                            }
                        }
                        if(!OpenTokConfig.isValid()) {
                            finishWithMessage("Invalid OpenTokConfig. " + OpenTokConfig.getDescription());
                            return;
                        }

                        Log.e("OpenTokConfig: ", "API: " + OpenTokConfig.API_KEY + "\n SEssion: " + OpenTokConfig.SESSION_ID + "\n Token: " + OpenTokConfig.TOKEN);
                        requestPermissions();


                    }
                });


 */

    }

    private void prepareOptions(){
        optionsArraylist.clear();
        optionsArraylist.add(0, "Mute Device Audio");
        optionsArraylist.add(1, "Mute All");
        optionsArraylist.add(2, "Disable Camera");
        optionsArraylist.add(3, "Disable Streams");

        optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case SWIPE_UP:

                if (recyclerView.getVisibility() == View.VISIBLE){
                    recyclerView.setVisibility(View.GONE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                }

                return true;

            case SWIPE_DOWN:
                if (recyclerView.getVisibility() == View.VISIBLE){
                    recyclerView.setVisibility(View.GONE);
                }else {
                    Intent intent = new Intent(MultiVideoCallActivity.this, MenuActivity.class);
                    intent.putExtra(MENU_KEY, R.menu.menu_videocall_exit);
                    startActivityForResult(intent, REQUEST_CODE_EXIT );
                }


                return true;

            case SWIPE_FORWARD:
                if (flag_single){
                    if (videoList != null) {
                        if (index_single >= videoList.size() -1) {
                            Log.e("Video Call", "Last User");
                            Toast.makeText(this, "Last User", Toast.LENGTH_SHORT).show();
                        } else{
                            //Show Next VISIBLE
                            index_single = index_single + 1;
                            calculateLayout();
                            Log.e("Video Call", "index" + index_single);
                        }
                    }
                }else {
                    if (recyclerView.getVisibility() != View.VISIBLE) {
                        // Increase Volume
                        //  int increase = 1;
                        if (audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) > audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)) {
                            int increase = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) + 1;
                            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, increase, 0);
                            int percentage = (audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) * 100) / audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                            seekbar.setProgress(percentage);
                            Log.e("Volume: ", percentage + "%");
                            //    Toast.makeText(this, percentage + "%", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(this, "Max Volume", Toast.LENGTH_SHORT).show();
                        }

                        showHide();
                    }
                }

              //  audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.MODE_IN_CALL);
              //  audioManager.adjustStreamVolume(AudioManager.MODE_IN_CALL, 100, 0);
               // Log.e("Volume1: ", audioManager.getStreamVolume(AudioManager.MODE_IN_CALL) + "%");
/*
                Log.e("Volume3: ", audioManager.getStreamVolume(AudioManager.MODE_CALL_SCREENING) + "%");

                Log.e("Volume4: ", audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "%");
                Log.e("Volume5: ", audioManager.getStreamVolume(AudioManager.FLAG_PLAY_SOUND) + "%");
                Log.e("Volume6: ", audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "%");
                Log.e("Volume7: ", audioManager.getStreamVolume(AudioManager.STREAM_ACCESSIBILITY) + "%");

                Log.e("Volume8: ", audioManager.getStreamVolume(AudioManager.STREAM_DTMF) + "%");

                Log.e("Volume9: ", audioManager.getStreamVolume(AudioManager.STREAM_RING) + "%");
                Log.e("Volume6: ", audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) + "%");


 */


                return true;

            case SWIPE_BACKWARD:
                if (flag_single){
                    if (videoList != null) {
                        if (index_single == 0) {
                            Log .e("Video Call", "First User");
                            Toast.makeText(this, "First User", Toast.LENGTH_SHORT).show();
                        } else {
                            //Show Previous VISIBLE
                         //   ConstraintSetHelper set = new ConstraintSetHelper(R.id.activity_multi_container);
                          //  set.layoutViewFullScreen(videoList.get(index_single - 1));
                          //  set.layoutViewWidthPercent(videoList.get(index_single - 1), 1f);
                          //  set.layoutViewHeightPercent(videoList.get(index_single - 1), 1f);
                          //  set.applyToLayout(container, true);
                            index_single = index_single - 1;
                            calculateLayout();
                            Log.e("Video Call", "index" + index_single);
                        }
                    }
                }else {

                    if (recyclerView.getVisibility() != View.VISIBLE) {

                        //Decrease volume
                        if (audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) > 1) {
                            int decrease = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) - 1;
                            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, decrease, 0);
                            int percentage1 = (audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) * 100) / audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                            seekbar.setProgress(percentage1);
                            Log.e("Volume: ", percentage1 + " %");
                            //   Toast.makeText(this, percentage1 + " %", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Min Volume", Toast.LENGTH_SHORT).show();
                        }
                        showHide();
                    }
                }

                return true;

            case TAP:

                Intent intent = new Intent(MultiVideoCallActivity.this, MenuActivity.class);
                intent.putExtra(MENU_KEY, R.menu.menu_video_call);
                startActivityForResult(intent, REQUEST_CODE );

                /*
                if (rcViewOptions.getVisibility() == View.VISIBLE){

                    switch (options_index){
                        case 0:

                            if (publisher !=  null){
                                if (publisher.getPublishAudio()){
                                    publisher.setPublishAudio(false);
                                    optionsArraylist.set(0, "UnMute Device Audio");
                                    optionsAdapter.notifyDataSetChanged();
                                    //  rcViewOptions.setVisibility(View.GONE);
                                    Toast.makeText(this, "Device Audio Muted", Toast.LENGTH_SHORT).show();
                                }else {
                                    publisher.setPublishAudio(true);
                                    optionsArraylist.set(0, "Mute Device Audio");
                                    optionsAdapter.notifyDataSetChanged();
                                    //  rcViewOptions.setVisibility(View.GONE);
                                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(this, "Device Audio Enabled", Toast.LENGTH_SHORT).show();
                                }
                            }

                            break;

                        case 1:
                            
                            Boolean flag_audio = true;
                            
                            for (int i =0; i< subscribers.size() ; i++){
                                if (subscribers.get(i) != null){
                                    if (subscribers.get(i).getSubscribeToAudio()){
                                        subscribers.get(i).setSubscribeToAudio(false);
                                        optionsArraylist.set(1, "Unmute All");
                                        optionsAdapter.notifyDataSetChanged();
                                        flag_audio = false;
                                    }else {
                                        subscribers.get(i).setSubscribeToAudio(true);
                                        optionsArraylist.set(1, "Mute All");
                                        optionsAdapter.notifyDataSetChanged();
                                        flag_audio = true;
                                    }
                                }
                            }
                            
                            if (!flag_audio){
                                Toast.makeText(this, "All Muted", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(this, "All Unmuted", Toast.LENGTH_SHORT).show();
                            }
                            
                            break;

                        case 2:
                            if (publisher !=  null){
                                if (publisher.getPublishVideo()){
                                    publisher.setPublishVideo(false);
                                    optionsArraylist.set(2, "Enable Camera");
                                    optionsAdapter.notifyDataSetChanged();
                                    Toast.makeText(this, "Device Camera Disabled", Toast.LENGTH_SHORT).show();
                                }else {
                                    publisher.setPublishVideo(true);
                                    optionsArraylist.set(2, "Disable Camera");
                                    optionsAdapter.notifyDataSetChanged();
                                    Toast.makeText(this, "Device Camera Enabled", Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;

                        case 3:
                            Boolean flag_video = true;
                            for (int i =0; i< subscribers.size() ; i++){
                                if (subscribers.get(i) != null){
                                    if (subscribers.get(i).getSubscribeToVideo()){
                                        subscribers.get(i).setSubscribeToVideo(false);
                                        optionsArraylist.set(3, "Enable Streams");
                                        optionsAdapter.notifyDataSetChanged();
                                        flag_video = false;
                                    }else {
                                        subscribers.get(i).setSubscribeToVideo(true);
                                        optionsArraylist.set(3, "Disable Streams");
                                        optionsAdapter.notifyDataSetChanged();
                                        flag_video = true;
                                    }
                                }
                            }

                            if (!flag_video){
                                Toast.makeText(this, "Streams Disabled", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(this, "Streams Enabled", Toast.LENGTH_SHORT).show();
                            }
                            break;

                        default:

                            // do nothing
                            break;
                    }
                    rcViewOptions.setVisibility(View.GONE);
                }else {
                    rcViewOptions.setVisibility(View.VISIBLE);
                }

                 */

                return true;

            default:

                return super.onGesture(gesture);
        }

    }

    private void showHide(){
        seekbar.setVisibility(View.VISIBLE);
         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                seekbar.setVisibility(View.GONE);

            }
        }, 3000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            String selectedOption = "";
            switch (id) {

                case R.id.bMuteAudio:
                    if (publisher != null){
                        publisher.setPublishAudio(!publisher.getPublishAudio());

                    }
                    break;

                case R.id.bMuteVideo:
                    if (publisher != null){
                        //  publisher.getView().setVisibility(View.GONE);
                        //    publisher.getView().setVisibility(View.VISIBLE);
                        publisher.setPublishVideo(!publisher.getPublishVideo());
                    }
                    break;
                case R.id.bMuteSubAutio:
                    for (int i =0; i< subscribers.size() ; i++){
                        if (subscribers.get(i) != null){
                            subscribers.get(i).setSubscribeToAudio(!subscribers.get(i).getSubscribeToAudio());

                        }
                    }
                    break;
                case R.id.bMuteSubVideo:
                    for (int i =0; i< subscribers.size() ; i++){
                        if (subscribers.get(i) != null){
                            subscribers.get(i).setSubscribeToVideo(!subscribers.get(i).getSubscribeToVideo());
                        }
                    }
                    
                    break;
                    
                case R.id.bSendImage:

                    if (flag_single){
                        flag_single = false;
                        calculateLayout();
                    }else {
                        if (videoList != null) {
                            if (videoList.size() > 0) {
                                //Enable Single Mode
                                flag_single = true;
                                calculateLayout();
                            } else {
                                //No Required Users
                                flag_single = false;
                                calculateLayout();
                                Toast.makeText(this, "No Users", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            flag_single = false;
                            calculateLayout();
                        }
                    }
                    break;
                 /*   for (int i =0; i< subscriberStreams.size() ; i++){
                        if (subscriberStreams.get(i) != null){
                               subscriberStreams.get(i).setSubscribeToVideo(!subscriberStreams.get(i).getSubscribeToVideo());
                        }
                    }

                  */

            }
            Toast.makeText(this.getApplicationContext(),  " "+selectedOption, Toast.LENGTH_SHORT)
                    .show();
        }else if (requestCode == REQUEST_CODE_EXIT && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            String selectedOption = "";
            switch (id) {

                case R.id.bResumeCall:
                    // Do nothing and resume call
                    break;

                case R.id.bExitCall:
                    // Exit call
                    if (notiReference != null) {
                        notiReference.update("status", "ended");
                        notiReference.update("message", "call has ended");
                    }

                    finishWithMessage("Call Ended");
                    break;
            }
            Toast.makeText(this.getApplicationContext(),  " "+selectedOption, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataCallMedia> list=new ArrayList<>();

        public MediaAdapter(Context context, ArrayList<DataCallMedia> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public MediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_call, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull MediaAdapter.ViewHolder holder, final int position) {
            Picasso.get().load(list.get(position).getUrl()).into(holder.imageView);
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.list_call_imageview);
            }
        }
    }

    public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{

        private Context context;
        private ArrayList<String> list=new ArrayList<>();

        public OptionsAdapter(Context context, ArrayList<String> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_call, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, final int position) {
            

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.list_call_imageview);
            }
        }
    }


    private void CheckDisabled(){
        for (int i =0; i<subscribers.size() ; i++){
            if ( subscribers.get(i) != null && subscribers.get(i).getView().getVisibility() == View.GONE){
                hidden_count = hidden_count + 1;
            }
        }
        tvDisabled.setText(hidden_count + "");


    }


    @Override
    protected void onResume() {
        super.onResume();
        hidden_count =0;

       // CheckDisabled();
        calculateLayout();

        if (session == null) {
            return;
        }

        session.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (session == null) {
            return;
        }

        session.onPause();

        if (isFinishing()) {
            session.onPause();
            disconnectSession();
        }
    }

    @Override
    protected void onDestroy() {
        disconnectSession();

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        finishWithMessage("onPermissionsDenied: " + requestCode + ":" + perms.size());
    }

    private int getResIdForSubscriberIndex(int index) {
        TypedArray arr = getResources().obtainTypedArray(R.array.subscriber_view_ids);
        int subId = arr.getResourceId(index, 0);
        arr.recycle();
        return subId;
    }

    private int getResIdForVideoIndex(int index) {
        TypedArray arr = getResources().obtainTypedArray(R.array.bvideo_view_ids);
        int subId = arr.getResourceId(index, 0);
        arr.recycle();
        return subId;
    }

    private int getResIdForAudioIndex(int index) {
        TypedArray arr = getResources().obtainTypedArray(R.array.baudio_view_ids);
        int subId = arr.getResourceId(index, 0);
        arr.recycle();
        return subId;
    }

    private void startPublisherPreview() {
        switch (resolution){

            case "AUTO":
                publisher = new Publisher.Builder(this)
                        .build();
                publisher.setCameraId(0);

                publisher.setPublisherListener(publisherListener);
                publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                publisher.startPreview();

                break;

            case "LOW":
                publisher = new Publisher.Builder(this)
                        .resolution(Publisher.CameraCaptureResolution.LOW)
                        .frameRate(Publisher.CameraCaptureFrameRate.FPS_30)
                        .build();
                publisher.setCameraId(0);

                publisher.setPublisherListener(publisherListener);
                publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                publisher.startPreview();
                break;

            case "MEDIUM":
                publisher = new Publisher.Builder(this)
                        .resolution(Publisher.CameraCaptureResolution.MEDIUM)
                        .frameRate(Publisher.CameraCaptureFrameRate.FPS_30)
                        .build();
                publisher.setPublisherListener(publisherListener);
                publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                publisher.startPreview();
                break;

            case "HIGH":
                publisher = new Publisher.Builder(this)
                        .resolution(Publisher.CameraCaptureResolution.HIGH)
                        .frameRate(Publisher.CameraCaptureFrameRate.FPS_30)
                        .build();
                publisher.setPublisherListener(publisherListener);
                publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                publisher.startPreview();
                break;

            default:
                publisher = new Publisher.Builder(this)
                        .frameRate(Publisher.CameraCaptureFrameRate.FPS_30)
                        .build();
                publisher.setPublisherListener(publisherListener);
                publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                publisher.startPreview();
                break;
        }

    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_CODE)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };

        if (EasyPermissions.hasPermissions(this, perms)) {
            session = new Session.Builder(this, OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID).sessionOptions(new Session.SessionOptions() {
                @Override
                public boolean useTextureViews() {
                    return true;
                }
            }).build();
            session.setSessionListener(sessionListener);
            session.connect(OpenTokConfig.TOKEN);

            String email = firebaseAuth.getCurrentUser().getEmail().toString();

            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
            Log.e("Time: ", now.toString());
            Log.e("Email: ", email);

            if (email!=null && SplashActivity.machine_id!=null && manual_key != null  && now != null && user != null) {



                firestore.collection("CallLogData")
                        .add(new DataCallLog(email, SplashActivity.machine_id, manual_key, step, Timestamp.now(), manual_name))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                call_id = documentReference.getId().toString();
                                Log.e("Create Call log", "Success");
                                Log.e("Current key", call_id + " v");

                                documentReference = firestore.collection("CallCurrent").document("x9Rmo3HXJaLjeLmOF0i2");

                                documentReference.update("call_id", call_id);

                                firestore.collection("videoCallData")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    count = task.getResult().size();
                                                 //   String receicer = "admin3@gmail.com";

                                                    firestore.collection("videoCallData")
                                                            .add(new DataNoti(call_id, firebaseAuth.getCurrentUser().getEmail().toString(), user, "is Calling", "waiting", count))
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Log.e("Video Call", "Sent notification");
                                                                //    Log.e("Data", Objects.requireNonNull(documentReference.get().getResult()).toString());
                                                                    notiReference = documentReference;
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(MultiVideoCallActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                    Log.e("Video Call: ", e.toString());
                                                                }
                                                            });

                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

/*
                                firestore.collection("videoCallData").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        assert value != null;
                                        if (!call_flag) {
                                            call_flag = true;
                                            count = value.size();


                                        }
                                    }
                                });


 */

                                firestore.collection("CallMedia")
                                        .whereEqualTo("call_id", call_id)
                                        .addSnapshotListener(MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                assert value != null;
                                                arrayList.clear();
                                                for (QueryDocumentSnapshot snapshot : value) {
                                                    if (snapshot.getData().get("call_id") != null && snapshot.getData().get("url") != null) {
                                                        arrayList.add(new DataCallMedia(snapshot.getData().get("call_id").toString(),
                                                                snapshot.getData().get("url").toString()));
                                                        adapter.notifyDataSetChanged();
                                                        Toast.makeText(MultiVideoCallActivity.this, "Image received, SWIPE UP to check", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Log.e("VideoCall", "Missing Parameters");
                                                    }
                                                }
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Create Call log", "Failure" + e.toString());

                            }
                        });
            }

            startPublisherPreview();
            publisher.getView().setId(R.id.publisher_view_id);
            container.addView(publisher.getView());
            calculateLayout();
         //   CheckDisabled();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), PERMISSIONS_REQUEST_CODE, perms);
        }
    }

    private void calculateLayout() {
        Log.e("In cal", "index" + index_single);
        ConstraintSetHelper set = new ConstraintSetHelper(R.id.activity_multi_container);

        int size = subscribers.size();
        videoList =  new ArrayList<Integer>();

        for (int i =0; i<subscribers.size(); i++){
            if (subscribers.get(i).getStream().hasVideo()){
                videoList.add(getResIdForSubscriberIndex(i));
                subscribers.get(i).getView().setVisibility(View.VISIBLE);
            }else {
                subscribers.get(i).getView().setVisibility(View.GONE);
            }
        }
        tvDisabled.setText(videoList.size() + " : " + size);
        if (flag_single){

            if (videoList != null && videoList.size() >0) {
                if (index_single == -1){
                    index_single = 0;
                 set.layoutViewFullScreen(videoList.get(0));
                 set.layoutViewHeightPercent(videoList.get(0), 1f);
                 set.layoutViewWidthPercent(videoList.get(0), 1f);
                 for (int i=0; i< subscribers.size() ; i++){
                     if (getResIdForAudioIndex(i) == videoList.get(0)){
                         subscribers.get(i).getView().setVisibility(View.VISIBLE);
                     }else {
                         subscribers.get(i).getView().setVisibility(View.GONE);
                     }
                 }
                 flag_single = true;
                }else if (index_single < videoList.size()){
                    set.layoutViewFullScreen(videoList.get(index_single));
                    set.layoutViewHeightPercent(videoList.get(index_single), 1f);
                    set.layoutViewWidthPercent(videoList.get(index_single), 1f);
                    for (int i=0; i< subscribers.size() ; i++){
                        if (getResIdForAudioIndex(i) == videoList.get(index_single)){
                            subscribers.get(i).getView().setVisibility(View.VISIBLE);
                        }else {
                            subscribers.get(i).getView().setVisibility(View.GONE);
                        }
                    }
                    flag_single = true;
                }else {
                    Log.e("Video Call", "Index out");
                }
            }else {
                flag_single = false;
                Toast.makeText(this, "No Users for single View", Toast.LENGTH_SHORT).show();
                Log.e("Video CAll", "No Users for single View");
            }
        }else {
            switch (videoList.size()) {
                case 0:
                    if (publisher != null) {
                        if (publisher.getView() != null) {
                            publisher.getView().setVisibility(View.VISIBLE);
                        }
                    }
                    set.layoutViewFullScreen(R.id.publisher_view_id);
                    break;

                case 1:
                    if (publisher != null) {
                        if (publisher.getView() != null) {
                            publisher.getView().setVisibility(View.VISIBLE);
                        }
                    }
                    set.layoutViewWithTopBound(R.id.publisher_view_id, R.id.activity_multi_container);
                    set.layoutViewWithTopBound(videoList.get(0), R.id.activity_multi_container);
                    set.layoutViewWithBottomBound(R.id.publisher_view_id, R.id.activity_multi_container);
                    set.layoutViewWithBottomBound(videoList.get(0), R.id.activity_multi_container);

                    set.layoutTwoViewsOccupyingAllRow(videoList.get(0), R.id.publisher_view_id);

                    set.layoutViewWidthPercent(R.id.publisher_view_id, .5f);
                    set.layoutViewWidthPercent(videoList.get(0), .5f);
                    break;

                case 2:
                    if (publisher != null) {
                        if (publisher.getView() != null) {
                            publisher.getView().setVisibility(View.GONE);
                        }
                    }
                    set.layoutViewWithTopBound(videoList.get(0), R.id.activity_multi_container);
                    set.layoutViewWithTopBound(videoList.get(1), R.id.activity_multi_container);
                    set.layoutViewWithBottomBound(videoList.get(0), R.id.activity_multi_container);
                    set.layoutViewWithBottomBound(videoList.get(1), R.id.activity_multi_container);

                    set.layoutTwoViewsOccupyingAllRow(videoList.get(0), videoList.get(1));

                    set.layoutViewWidthPercent(videoList.get(0), .5f);
                    set.layoutViewWidthPercent(videoList.get(1), .5f);
                    break;

                case 3:
                    if (publisher != null) {
                        if (publisher.getView() != null) {
                            publisher.getView().setVisibility(View.GONE);
                        }
                    }
                    set.layoutViewWithBottomBound(videoList.get(0), R.id.activity_multi_container);
                    set.layoutViewWithTopBound(videoList.get(0), R.id.activity_multi_container);

                    set.layoutTwoViewsOccupyingAllRow(videoList.get(0), videoList.get(1));
                    set.layoutTwoViewsOccupyingAllRow(videoList.get(0), videoList.get(2));
                    set.layoutViewWidthPercent(videoList.get(0), .5f);
                    set.layoutViewWidthPercent(videoList.get(1), .5f);
                    set.layoutViewWidthPercent(videoList.get(2), .5f);

                    set.layoutViewWithTopBound(videoList.get(1), R.id.activity_multi_container);
                    set.layoutViewWithBottomBound(videoList.get(2), R.id.activity_multi_container);
                    set.layoutViewHeightPercent(videoList.get(1), .5f);
                    set.layoutViewHeightPercent(videoList.get(2), .5f);

                    set.layoutViewAboveView(videoList.get(1), videoList.get(2));
                    break;

                case 4:
                    if (publisher != null) {
                        if (publisher.getView() != null) {
                            publisher.getView().setVisibility(View.GONE);
                        }
                    }
                    set.layoutViewWithTopBound(videoList.get(0), R.id.activity_multi_container);
                    set.layoutViewHeightPercent(videoList.get(0), .5f);
                    set.layoutViewWithTopBound(videoList.get(1), R.id.activity_multi_container);
                    set.layoutViewHeightPercent(videoList.get(1), .5f);
                    set.layoutTwoViewsOccupyingAllRow(videoList.get(0), videoList.get(1));
                    set.layoutViewWidthPercent(videoList.get(0), .5f);
                    set.layoutViewWidthPercent(videoList.get(1), .5f);

                    set.layoutViewAboveView(videoList.get(0), videoList.get(2));
                    set.layoutViewHeightPercent(videoList.get(2), .5f);
                    set.layoutViewAboveView(videoList.get(1), videoList.get(3));
                    set.layoutViewHeightPercent(videoList.get(3), .5f);
                    set.layoutViewWithBottomBound(videoList.get(2), R.id.activity_multi_container);
                    set.layoutViewWithBottomBound(videoList.get(3), R.id.activity_multi_container);
                    set.layoutTwoViewsOccupyingAllRow(videoList.get(2), videoList.get(3));
                    set.layoutViewWidthPercent(videoList.get(2), .5f);
                    set.layoutViewWidthPercent(videoList.get(3), .5f);
                    break;

                case 5:
                    if (publisher != null) {
                        if (publisher.getView() != null) {
                            publisher.getView().setVisibility(View.GONE);
                        }
                    }
                    float pers = 1f / 3;
                    set.layoutViewWithTopBound(videoList.get(0), R.id.activity_multi_container);
                    set.layoutViewHeightPercent(videoList.get(0), .5f);
                    set.layoutViewWithTopBound(videoList.get(2), R.id.activity_multi_container);
                    set.layoutViewHeightPercent(videoList.get(2), pers);
                    set.layoutViewAboveView(videoList.get(0), videoList.get(1));
                    set.layoutViewHeightPercent(videoList.get(1), .5f);
                    set.layoutViewWithBottomBound(videoList.get(1), R.id.activity_multi_container);
                    set.layoutViewAboveView(videoList.get(2), videoList.get(3));
                    set.layoutViewHeightPercent(videoList.get(3), pers);
                    set.layoutViewAboveView(videoList.get(3), videoList.get(4));
                    set.layoutViewHeightPercent(videoList.get(4), pers);
                    set.layoutViewWithBottomBound(videoList.get(4), R.id.activity_multi_container);
                    set.layoutTwoViewsOccupyingAllRow(videoList.get(0), videoList.get(2));
                    set.layoutTwoViewsOccupyingAllRow(videoList.get(0), videoList.get(3));
                    set.layoutTwoViewsOccupyingAllRow(videoList.get(1), videoList.get(4));
                    set.layoutViewWidthPercent(videoList.get(0), .5f);
                    set.layoutViewWidthPercent(videoList.get(1), .5f);
                    set.layoutViewWidthPercent(videoList.get(2), .5f);
                    set.layoutViewWidthPercent(videoList.get(3), .5f);
                    set.layoutViewWidthPercent(videoList.get(4), .5f);


                    break;

                default:
                    // Do nothing
                    break;
            }

        /*
        if (size == 0) {
            // Publisher full screen
            set.layoutViewFullScreen(R.id.publisher_view_id);
        } else if (size == 1) {
            // Publisher
            // Subscriber


            set.layoutViewWithTopBound(R.id.publisher_view_id, R.id.activity_multi_container);
            set.layoutViewWithTopBound(getResIdForSubscriberIndex(0), R.id.activity_multi_container);
            set.layoutViewWithBottomBound(R.id.publisher_view_id, R.id.activity_multi_container);
            set.layoutViewWithBottomBound(getResIdForSubscriberIndex(0), R.id.activity_multi_container);

            set.layoutTwoViewsOccupyingAllRow(getResIdForSubscriberIndex(0), R.id.publisher_view_id);

            set.layoutViewWidthPercent(R.id.publisher_view_id, .5f);
            set.layoutViewWidthPercent(getResIdForSubscriberIndex(0), .5f);

        } else if (size % 2 == 0){

            if (size ==2 ){
                set.layoutViewWithBottomBound(R.id.publisher_view_id, R.id.activity_multi_container);
                set.layoutViewWithTopBound(R.id.publisher_view_id, R.id.activity_multi_container);

                set.layoutTwoViewsOccupyingAllRow(R.id.publisher_view_id, getResIdForSubscriberIndex(0));
                set.layoutTwoViewsOccupyingAllRow(R.id.publisher_view_id, getResIdForSubscriberIndex(1));

                set.layoutViewWithTopBound(getResIdForSubscriberIndex(0), R.id.activity_multi_container);
                set.layoutViewWithBottomBound(getResIdForSubscriberIndex(1), R.id.activity_multi_container);
                set.layoutViewHeightPercent(getResIdForSubscriberIndex(0), .5f);
                set.layoutViewHeightPercent(getResIdForSubscriberIndex(1), .5f);

                set.layoutViewAboveView(getResIdForSubscriberIndex(0), getResIdForSubscriberIndex(1));

            }else{
                //  Publisher
                // Sub1 | Sub2
                // Sub3 | Sub4
                //    .....
                int rows = (size / 2) + 1;
                float heightPercent = 1f / rows;

                set.layoutViewWithTopBound(R.id.publisher_view_id, R.id.activity_multi_container);
                set.layoutViewAllContainerWide(R.id.publisher_view_id, R.id.activity_multi_container);
                set.layoutViewHeightPercent(R.id.publisher_view_id, heightPercent);

                for (int i = 0; i < size; i += 2) {
                    if (i == 0) {
                        set.layoutViewAboveView(R.id.publisher_view_id, getResIdForSubscriberIndex(i));
                        set.layoutViewAboveView(R.id.publisher_view_id, getResIdForSubscriberIndex(i + 1));
                    } else {
                        set.layoutViewAboveView(getResIdForSubscriberIndex(i - 2), getResIdForSubscriberIndex(i));
                        set.layoutViewAboveView(getResIdForSubscriberIndex(i - 1), getResIdForSubscriberIndex(i + 1));
                    }

                    set.layoutTwoViewsOccupyingAllRow(getResIdForSubscriberIndex(i), getResIdForSubscriberIndex(i + 1));
                    set.layoutViewHeightPercent(getResIdForSubscriberIndex(i), heightPercent);
                    set.layoutViewHeightPercent(getResIdForSubscriberIndex(i + 1), heightPercent);
                }

                set.layoutViewWithBottomBound(getResIdForSubscriberIndex(size - 2), R.id.activity_multi_container);
                set.layoutViewWithBottomBound(getResIdForSubscriberIndex(size - 1), R.id.activity_multi_container);
            }

        } else if (size > 1) {
            // Pub  | Sub1
            // Sub2 | Sub3
            // Sub3 | Sub4
            //    .....
            int rows = ((size + 1) / 2);
            float heightPercent = 1f / rows;

            set.layoutViewWithTopBound(R.id.publisher_view_id, R.id.activity_multi_container);
            set.layoutViewHeightPercent(R.id.publisher_view_id, heightPercent);
            set.layoutViewWithTopBound(getResIdForSubscriberIndex(0), R.id.activity_multi_container);
            set.layoutViewHeightPercent(getResIdForSubscriberIndex(0), heightPercent);
            set.layoutTwoViewsOccupyingAllRow(R.id.publisher_view_id, getResIdForSubscriberIndex(0));

            for (int i = 1; i < size; i += 2) {
                if (i == 1) {
                    set.layoutViewAboveView(R.id.publisher_view_id, getResIdForSubscriberIndex(i));
                    set.layoutViewHeightPercent(R.id.publisher_view_id, heightPercent);
                    set.layoutViewAboveView(getResIdForSubscriberIndex(0), getResIdForSubscriberIndex(i + 1));
                    set.layoutViewHeightPercent(getResIdForSubscriberIndex(0), heightPercent);
                } else {
                    set.layoutViewAboveView(getResIdForSubscriberIndex(i - 2), getResIdForSubscriberIndex(i));
                    set.layoutViewHeightPercent(getResIdForSubscriberIndex(i - 2), heightPercent);
                    set.layoutViewAboveView(getResIdForSubscriberIndex(i - 1), getResIdForSubscriberIndex(i + 1));
                    set.layoutViewHeightPercent(getResIdForSubscriberIndex(i - 1), heightPercent);
                }
                set.layoutTwoViewsOccupyingAllRow(getResIdForSubscriberIndex(i), getResIdForSubscriberIndex(i + 1));
            }

            set.layoutViewWithBottomBound(getResIdForSubscriberIndex(size - 2), R.id.activity_multi_container);
            set.layoutViewWithBottomBound(getResIdForSubscriberIndex(size - 1), R.id.activity_multi_container);
        }


         */
        }
        set.applyToLayout(container, true);
    }

    private void disconnectSession() {
        if (session == null) {
            return;
        }

        if (subscribers.size() > 0) {
            for (Subscriber subscriber : subscribers) {
                if (subscriber != null) {
                    session.unsubscribe(subscriber);
                }
            }
        }

        if (publisher != null) {
            publisher.getCapturer().stopCapture();
            session.unpublish(publisher);
            container.removeView(publisher.getView());
            publisher = null;
        }
        session.disconnect();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void finishWithMessage(String message) {

        if (message.equals("Session error: Cannot publish: the client is not connected to the OpenTok session.")){
            disconnectSession();
        }else {
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            disconnectSession();
        }

        this.finish();
    }
}
