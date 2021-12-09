package com.example.android.glass.glassdesign2;

import androidx.annotation.Nullable;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.android.glass.glassdesign2.data.DataCall;
import com.example.android.glass.glassdesign2.data.DataUploads;
import com.example.glass.ui.GlassGestureDetector;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import android.opengl.GLSurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class VideoCallActivity extends BaseActivity implements  EasyPermissions.PermissionCallbacks, Session.SessionListener,
        PublisherKit.PublisherListener{

    private static String API_KEY = "47217094";
    public static final String SESSION_ID = "1_MX40NzIxNzA5NH5-MTYxOTg1NDAzMjYxMn5ZU21TVzNmVlhpYjc1QmJVdjBOMlZtekt-UH4";
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NzIxNzA5NCZzaWc9NzUxYWVkM2YwZDBhM2JhOWU5MjdjZTJkOTRhMmU4Nzg0OGNjZGM3NzpzZXNzaW9uX2lkPTFfTVg0ME56SXhOekE1Tkg1LU1UWXhPVGcxTkRBek1qWXhNbjVaVTIxVFZ6Tm1WbGhwWWpjMVFtSlZkakJPTWxadGVrdC1VSDQmY3JlYXRlX3RpbWU9MTYxOTg1NDA0OCZub25jZT0wLjgzNjU4ODU2MzIwMzMxOTImcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTYyMjQ0NjA1NyZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";
    static class SubscriberContainer {
        public RelativeLayout container;
        public ToggleButton toggleAudio;
        public Subscriber subscriber;

        public SubscriberContainer(RelativeLayout container,
                                   ToggleButton toggleAudio,
                                   Subscriber subscriber) {
            this.container = container;
            this.toggleAudio = toggleAudio;
            this.subscriber = subscriber;
        }
    }
    private static final String TAG = "Video_call " + VideoCallActivity.class.getSimpleName();

    private final int MAX_NUM_SUBSCRIBERS = 2;

    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Session mSession;
    private Publisher mPublisher;

    private List<SubscriberContainer> mSubscribers;

    private RelativeLayout mPublisherViewContainer;

    private boolean sessionConnected = false;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference, ref1, ref_session, ref2;
    private StorageReference storageReference, storageRef1;
    private RecyclerView rcView;
    private ArrayList<DataUploads> arrayList =  new ArrayList<>();
    private ImageAdapter adapter;
    private String step_key, manual_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherview);

        rcView = findViewById(R.id.activity_call_rcView);

        step_key = getIntent().getStringExtra("step");
        manual_key = getIntent().getStringExtra("manual");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rcView.setLayoutManager(layoutManager);
        rcView.setHasFixedSize(true);
        adapter = new ImageAdapter(this, arrayList);
        rcView.setAdapter(adapter);

        final Button swapCamera = (Button) findViewById(R.id.swapCamera);
        swapCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mPublisher == null) {
                    return;
                }
                mPublisher.cycleCamera();
            }
        });

        final ToggleButton toggleAudio = (ToggleButton) findViewById(R.id.toggleAudio);
        toggleAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPublisher == null) {
                    return;
                }
                if (isChecked) {
                    mPublisher.setPublishAudio(true);
                } else {
                    mPublisher.setPublishAudio(false);
                }
            }
        });

        final ToggleButton toggleVideo = (ToggleButton) findViewById(R.id.toggleVideo);
        toggleVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPublisher == null) {
                    return;
                }
                if (isChecked) {
                    mPublisher.setPublishVideo(true);
                } else {
                    mPublisher.setPublishVideo(false);
                }
            }
        });

        mSubscribers = new ArrayList<>();
        for (int i = 0; i < MAX_NUM_SUBSCRIBERS; i++) {
            int containerId = getResources().getIdentifier("subscriberview" + (new Integer(i)).toString(),
                    "id", VideoCallActivity.this.getPackageName());
            int toggleAudioId = getResources().getIdentifier("toggleAudioSubscriber" + (new Integer(i)).toString(),
                    "id", VideoCallActivity.this.getPackageName());
            mSubscribers.add(new SubscriberContainer(
                    findViewById(containerId),
                    findViewById(toggleAudioId),
                    null
            ));
        }

        requestPermissions();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference= firebaseDatabase.getReference();
        ref1= reference.child("calls");
        ref_session = reference.child("session");

        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String key=ref1.push().getKey();
        //  ref1.child(key).child("time").setValue(now.toString());

        assert key != null;
        ref1.child(key).setValue(new DataCall(key,manual_key, step_key, Timestamp.now()));

        ref_session.setValue(key);
        ref2 = reference.child("uploads").child(key);
        storageReference = FirebaseStorage.getInstance().getReference().child("images").child(key);

        ref2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayList.add(new DataUploads(snapshot.getKey().toString(), snapshot.getValue().toString()));
                adapter.notifyDataSetChanged();
                Toast.makeText(VideoCallActivity.this, "Image Received, TAP to check", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayList.add(new DataUploads(snapshot.getKey().toString(), snapshot.getValue().toString()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                arrayList.remove(new DataUploads(snapshot.getKey().toString(), snapshot.getValue().toString()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataUploads> list=new ArrayList<>();

        public ImageAdapter(Context context, ArrayList<DataUploads> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_call, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, final int position) {
            Picasso.get().load(list.get(position).getUrl()).into(holder.imageView);
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDesc;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.list_call_imageview);
            }
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");

        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");

        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();

        if (mSession == null) {
            return;
        }
        mSession.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();

        if (mSession == null) {
            return;
        }
        mSession.onPause();

        if (isFinishing()) {
            disconnectSession();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onPause");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

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
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            mSession = new Session.Builder(VideoCallActivity.this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }
    }
    @Override
    public void onConnected(Session session) {
        Log.d(TAG, "onConnected: Connected to session " + session.getSessionId());
        sessionConnected = true;

        mPublisher = new Publisher.Builder(VideoCallActivity.this).name("publisher").build();

        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        mPublisherViewContainer.addView(mPublisher.getView());

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.d(TAG, "onDisconnected: disconnected from session " + session.getSessionId());
        sessionConnected = false;
        mSession = null;
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.d(TAG, "onError: Error (" + opentokError.getMessage() + ") in session " + session.getSessionId());

        Toast.makeText(this, "Session error. See the logcat please.", Toast.LENGTH_LONG).show();
        finish();
    }

    private SubscriberContainer findFirstEmptyContainer(Subscriber subscriber) {
        for (SubscriberContainer c : mSubscribers) {
            if (c.subscriber == null) {

                return c;
            }
        }
        return null;
    }

    private SubscriberContainer findContainerForStream(Stream stream) {
        for (SubscriberContainer c : mSubscribers) {
            if (c.subscriber.getStream().getStreamId().equals(stream.getStreamId())) {
                return c;
            }
        }
        return null;
    }

    private void addSubscriber(Subscriber subscriber) {
        SubscriberContainer container = findFirstEmptyContainer(subscriber);
        if (container == null) {
            Toast.makeText(this, "New subscriber ignored. MAX_NUM_SUBSCRIBERS limit reached.", Toast.LENGTH_LONG).show();
            return;
        }

        container.subscriber = subscriber;
        container.container.addView(subscriber.getView());
        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        container.toggleAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                subscriber.setSubscribeToAudio(true);
            } else {
                subscriber.setSubscribeToAudio(false);
            }
        });
        container.container.setVisibility(View.VISIBLE);
        container.toggleAudio.setVisibility(View.VISIBLE);
    }

    private void removeSubscriberWithStream(Stream stream) {
        SubscriberContainer container = findContainerForStream(stream);
        if (container == null) {
            return;
        }

        container.container.removeView(container.subscriber.getView());
        container.toggleAudio.setOnCheckedChangeListener(null);
        container.toggleAudio.setVisibility(View.INVISIBLE);
        container.subscriber = null;
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.d(TAG, "onStreamReceived: New stream " + stream.getStreamId() + " in session " + session.getSessionId());

        final Subscriber subscriber = new Subscriber.Builder(VideoCallActivity.this, stream).build();
        mSession.subscribe(subscriber);
        addSubscriber(subscriber);
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.d(TAG, "onStreamDropped: Stream " + stream.getStreamId() + " dropped from session " + session.getSessionId());

        removeSubscriberWithStream(stream);
    }

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
        Log.d(TAG, "onError: Error (" + opentokError.getMessage() + ") in publisher");

        Toast.makeText(this, "Session error. See the logcat please.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void disconnectSession() {
        if (mSession == null || !sessionConnected) {
            return;
        }
        sessionConnected = false;

        if (mSubscribers.size() > 0) {
            for (SubscriberContainer c : mSubscribers) {
                if (c.subscriber != null) {
                    mSession.unsubscribe(c.subscriber);
                    c.subscriber.destroy();
                }
            }
        }

        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
            mSession.unpublish(mPublisher);
            mPublisher.destroy();
            mPublisher = null;
        }
        mSession.disconnect();
    }

    private void ReArrangeUI() {
        if (mSubscribers.size() > 0) {
            for (SubscriberContainer c : mSubscribers) {
                if (c.subscriber == null) {
                    mSession.unsubscribe(c.subscriber);
                    c.subscriber.destroy();
                }
            }
        }
    }


    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case SWIPE_DOWN:
                if (rcView.getVisibility() == View.VISIBLE){
                    rcView.setVisibility(View.GONE);
                }else {
                  //  mSession.disconnect();
                    ref_session.setValue("");
                    finish();
                }
                break;

            case TAP:
                if (rcView.getVisibility() == View.VISIBLE){
                    rcView.setVisibility(View.GONE);
                }else {
                    rcView.setVisibility(View.VISIBLE);
                }
                break;
        }
        return true;
    }


}