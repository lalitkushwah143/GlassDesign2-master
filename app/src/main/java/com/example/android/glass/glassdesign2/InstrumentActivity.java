package com.example.android.glass.glassdesign2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataAnno;
import com.example.android.glass.glassdesign2.data.DataAnno1;
import com.example.android.glass.glassdesign2.data.DataAnno2;
import com.example.glass.ui.GlassGestureDetector;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class InstrumentActivity extends BaseActivity {

    private ImageView imageView;
    private ConstraintLayout constraintLayout;
    private FirebaseFirestore firestore;
    private DataAnno dataAnno;
    private Boolean aBoolean = false;
    private int index = -1;
    private TextView textView_help;

    private FloatingActionButton fab;

    private LineData data = new LineData();
    private ArrayList<Entry> entries = new ArrayList<>();
    private LineDataSet dataSet;
    private ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private TextView tvTitle, tvContent;
    private LineChart chart;
    private ConstraintLayout layout1, layout2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);

        //getSupportActionBar().hide();


        imageView = findViewById(R.id.activity_instrument_imageview);
        constraintLayout = findViewById(R.id.activity_instrument_clayout);
        fab = findViewById(R.id.activity_instrument_fab);
        textView_help = findViewById(R.id.activity_instrument_textview);
        textView_help.setText("TAP to show annotation");
        tvTitle = findViewById(R.id.activity_instrument_tvTitle);
        tvContent = findViewById(R.id.activity_instrument_tv1);
        chart = findViewById(R.id.activity_instrument_chart);
        layout1 = findViewById(R.id.activity_instrument_layout1);
        layout2 = findViewById(R.id.activity_instrument_layout2);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("annotation").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                for (QueryDocumentSnapshot snapshot : value) {
                    if (snapshot.getData().get("annotationData") != null && snapshot.getData().get("imgUrl") != null
                            && snapshot.getData().get("name") != null) {
                        String name = snapshot.getData().get("name").toString();
                        String img = snapshot.getData().get("imgUrl").toString();
                        ArrayList<DataAnno1> dataAnno1 = new ArrayList<>();
                        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) snapshot.get("annotationData");
                        for (Map<String, Object> map : list){
                            Map<String, Object> map1 = (Map<String, Object>) map.get("mark");
                            String comment = map.get("comment").toString();
                            String mqttId;
                            if (map.containsKey("mqttId")){
                                mqttId = map.get("mqttId").toString();
                            }else {
                                mqttId = "";
                            }
                            String id = map.get("id").toString();
                            String type, height, width, x, y;
                            DataAnno2 dataAnno2;
                            if (!map1.isEmpty()){
                                type = map1.get("type").toString();
                                height = map1.get("height").toString();
                                width = map1.get("width").toString();
                                x = map1.get("x").toString();
                                y = map1.get("y").toString();
                                dataAnno2 = new DataAnno2(type, Float.parseFloat(height), Float.parseFloat(width), Float.parseFloat(x), Float.parseFloat(y));
                            }else {
                                dataAnno2 = null;
                            }
                            dataAnno1.add(new DataAnno1(id, comment, mqttId,   dataAnno2));
                            dataAnno = new DataAnno(name, img, dataAnno1);
                            Picasso.get().load(dataAnno.getImgUrl())
                                    .into(imageView);
                        }

                    }else {
                        Log.e("Jobs" , "Missing Parameters");
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e("name", ":" + dataAnno.getName());
                //Log.e("im " , dataAnno.getImgUrl());
                if (imageView.getDrawable() == null){
                    Toast.makeText(InstrumentActivity.this, "Please wait while Image is loading", Toast.LENGTH_SHORT).show();
                }else {
                    for ( int i =0 ; i<dataAnno.getAnnotationData().size(); i++){
                        DataAnno1 dataAnno1 = dataAnno.getAnnotationData().get(i);
                        //Log.e("comment", dataAnno1.getComment());
                        //Log.e("id", dataAnno1.getId());
                        //Log.e("mqtt", dataAnno1.getMqttId());
                        DataAnno2 dataAnno2 = dataAnno1.getMark();
                        //Log.e("type", dataAnno2.getType());
                        //Log.e("height", ":" + dataAnno2.getHeight());
                        //Log.e("width", ":" + dataAnno2.getWidth());
                        //Log.e("x", ":" + dataAnno2.getX());
                        //Log.e("y", ":" + dataAnno2.getY());

                        int[] cords = getBitmapPositionInsideImageView(imageView);
                        int actualWidth = imageView.getDrawable().getIntrinsicWidth();
                        int actualHeight = imageView.getDrawable().getIntrinsicHeight();
                        float width_pro = cords[2]/(float)actualWidth;
                        float height_pro = cords[3]/(float)actualHeight;
                        float new_x = dataAnno2.getX()*width_pro;
                        float new_y = dataAnno2.getY()*height_pro;


                        if (i<5){
                            int tvId = getResIdForSubscriberIndex(i);
                            TextView textView2 = new TextView(InstrumentActivity.this);
                            textView2.setId(tvId);
                            textView2.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            textView2.setLayoutParams(layoutParams2);
                            textView2.setTextColor(getColor(R.color.black));
                            if (dataAnno1.getMqttId() != null && dataAnno1.getMqttId().toString().length()>0){
                                textView2.setText(dataAnno1.getComment().toString());
                            }else {
                                textView2.setText("X");
                            }
                            //textView2.setBackgroundColor(getColor(R.color.color_accept));
                            textView2.setBackground(getDrawable(R.drawable.indicator_selected_dot));
                            //textView2.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.indicator_selected_dot), null, null, null);
                            textView2.setX(new_x + cords[0]);
                            textView2.setY(new_y + cords[1]);
                            layout1.addView(textView2);

                            textView2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int typed_id = view.getId();
                                    TypedArray typedArray = getResources().obtainTypedArray(R.array.instrument_tv_ids);
                                    for (int i=0; i<typedArray.length(); i++){
                                        if (typed_id == typedArray.getResourceId(i, 5)){
                                            OpenDialog(i);
                                        }
                                    }
                                }
                            });
                        }
                    }

                }

            }
        });
/*
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int[] corddss = getBitmapPositionInsideImageView(imageView);
                Log.e("corddd", ": " + Arrays.toString(corddss));
                int[] viewCoords = new int[2];
                imageView.getLocationOnScreen(viewCoords);
                //Log.e("cords: ",  viewCoords.toString());
                int left = imageView.getLeft();
                int right = imageView.getRight();
                //Log.e("fd", ": "+left + ": " + right);

                float[] imageMatrix = new float[9];
                imageView.getImageMatrix().getValues(imageMatrix);
                float scale = imageMatrix[Matrix.MSCALE_X];
                float transX = imageMatrix[Matrix.MSCALE_Y];
                imageView.getWidth();
                //Log.e("matrix", transX +": "+ scale);
                Log.e("x, y" , ": "+ viewCoords+": " + viewCoords[0] + " , " + viewCoords[1]);
                Log.e("size", imageView.getDrawable().getIntrinsicWidth() +" : "+imageView.getDrawable().getIntrinsicHeight());

                TextView textView = new TextView(InstrumentActivity.this);
                textView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);
                textView.setText("gdkasjfka");
                textView.setX(viewCoords[0]);
                textView.setY(viewCoords[1] -200);
                constraintLayout.addView(textView);

                TextView textView2 = new TextView(InstrumentActivity.this);
                textView2.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView2.setLayoutParams(layoutParams2);
                textView2.setTextColor(getColor(R.color.black));
                textView2.setText("new With structure");
                //textView2.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                textView2.setX(corddss[0]);
                textView2.setY(corddss[1]);
                constraintLayout.addView(textView2);
            }
        });

 */
    }

    private void OpenDialog(int index){
        final String[] current_value = {""};
        final float[] floats;

        DataAnno1 anno1 = dataAnno.getAnnotationData().get(index);

        if (anno1.getMqttId() == null){
            Toast.makeText(InstrumentActivity.this, "No MQTT process Value Assigned", Toast.LENGTH_SHORT).show();

        }else {
            String mqttId = anno1.getMqttId();

            if (mqttId.equals("")){
                Toast.makeText(InstrumentActivity.this, "No MQTT process Value Assigned", Toast.LENGTH_SHORT).show();
            }else {
              //  alertDialogBuilder = new AlertDialog.Builder(InstrumentActivity.this);
               // LayoutInflater inflater=InstrumentActivity.this.getLayoutInflater();
              //  final View view=inflater.inflate(R.layout.dialog_instrument, null);
              //  alertDialogBuilder.setView(view);

              //  TextView textView = view.findViewById(R.id.dialog_instrument_tv1);
                //LineChart chart = view.findViewById(R.id.dialog_instrument_chart);

                //textView.setText("Current Value for "+ mqttId +" is : "+ current_value[0]);
                layout2.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.GONE);
                String title = dataAnno.getAnnotationData().get(index).getComment();
               // alertDialogBuilder.setTitle(title);
               // alertDialog = alertDialogBuilder.create();
               // alertDialog.show();

                tvTitle.setText(title);

                chart.getLegend().setTextColor(getResources().getColor(R.color.white));
                chart.getDescription().setTextColor(getResources().getColor(R.color.white));
                XAxis xAxis = chart.getXAxis();
                xAxis.setTextColor(getResources().getColor(R.color.white));

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setTextColor(getResources().getColor(R.color.design_orange));
                leftAxis.setDrawGridLines(true);
                leftAxis.setGranularityEnabled(true);

                entries = new ArrayList<>();
                dataSet = new LineDataSet(entries, "points");
                dataSet.setColor(getResources().getColor(R.color.design_orange));
                dataSet.setCircleColor(getResources().getColor(R.color.design_orange));
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSets.clear();
                dataSets.add(dataSet);

                data =new LineData(dataSets);
                chart.setData(data);
                chart.invalidate();

                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                chart.setPinchZoom(true);
                chart.setHighlightPerDragEnabled(true);
                chart.setContentDescription("MQTT");

                DocumentReference documentReference =  firestore.collection("mqtt").document(mqttId);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("Instument", "Listen failed.", error);
                            return;
                        }
                        if (value != null && value.exists()) {
                            tvContent.setText("Current Value for " + mqttId + " is : \n" + value.getData().get("value").toString());

                            ArrayList<Map<String, Object>> list = new ArrayList<>();
                            entries.clear();
                            dataSet.notifyDataSetChanged();
                            data.notifyDataChanged();
                            chart.notifyDataSetChanged();
                            list = (ArrayList<Map<String, Object>>) value.get("previous");

                            assert list != null;
                            if (list.isEmpty()){
                                //Log.e("previewu", "if empty");

                                Toast.makeText(InstrumentActivity.this, "No history found", Toast.LENGTH_SHORT).show();
                            }else {
                                //Log.e("previewu", "else size" + list.size());
                                for (int i=0; i<list.size(); i++){
                                    String s = list.get(i).get("value").toString();
                                    entries.add(new Entry(i, Float.parseFloat(s)));
                                    //Log.e("entri size", ": " + entries.size());
                                    dataSet.notifyDataSetChanged();
                                    data.notifyDataChanged();
                                    chart.notifyDataSetChanged();
                                    chart.moveViewToX(entries.size());
                                }

                            }

                        } else {
                            Log.d("Instrument", "Current data: null");
                            Toast.makeText(InstrumentActivity.this, "No mqtt data available", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        }


    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (imageView.getDrawable() == null){
                    aBoolean = false;
                    Toast.makeText(InstrumentActivity.this, "Please wait while Image is loading", Toast.LENGTH_SHORT).show();
                }else {
                    if (aBoolean){
                        Toast.makeText(InstrumentActivity.this, "Already Shown", Toast.LENGTH_SHORT).show();
                    }else {
                        aBoolean = true;
                        textView_help.setText("SWIPE FW/BW : to switch annotations \n SWIPE UP : View Annotation");
                    for ( int i =0 ; i<dataAnno.getAnnotationData().size(); i++){
                        DataAnno1 dataAnno1 = dataAnno.getAnnotationData().get(i);

                        DataAnno2 dataAnno2 = dataAnno1.getMark();

                        int[] cords = getBitmapPositionInsideImageView(imageView);
                        int actualWidth = imageView.getDrawable().getIntrinsicWidth();
                        int actualHeight = imageView.getDrawable().getIntrinsicHeight();
                        float width_pro = cords[2]/(float)actualWidth;
                        float height_pro = cords[3]/(float)actualHeight;
                        float new_x = dataAnno2.getX()*width_pro;
                        float new_y = dataAnno2.getY()*height_pro;


                        if (i<5){
                            int tvId = getResIdForSubscriberIndex(i);
                            TextView textView2 = new TextView(InstrumentActivity.this);
                            textView2.setId(tvId);
                            textView2.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            textView2.setLayoutParams(layoutParams2);
                            textView2.setTextColor(getColor(R.color.black));
                            if (dataAnno1.getMqttId() != null && dataAnno1.getMqttId().toString().length()>0){
                                textView2.setText(dataAnno1.getComment().toString());
                            }else {
                                textView2.setText("X");
                            }
                            //textView2.setBackgroundColor(getColor(R.color.color_accept));
                            textView2.setBackground(getDrawable(R.drawable.indicator_selected_dot));
                            textView2.setTypeface(textView2.getTypeface(), Typeface.BOLD);
                            //textView2.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.indicator_selected_dot), null, null, null);
                            textView2.setX(new_x + cords[0]);
                            textView2.setY(new_y + cords[1]);
                            layout1.addView(textView2);

                            textView2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int typed_id = view.getId();
                                    TypedArray typedArray = getResources().obtainTypedArray(R.array.instrument_tv_ids);
                                    for (int i=0; i<typedArray.length(); i++){
                                        if (typed_id == typedArray.getResourceId(i, 5)){
                                            OpenDialog(i);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    }
                }

                return true;

            case SWIPE_FORWARD:
                if (aBoolean){
                    if (layout2.getVisibility() == View.VISIBLE){
                        Toast.makeText(InstrumentActivity.this, "Close the Chart first", Toast.LENGTH_SHORT).show();
                    }else {
                        int size = dataAnno.getAnnotationData().size();
                        if (size>0){
                            if (index <size-1){
                                index = index + 1;
                                Toast.makeText(InstrumentActivity.this, "Next", Toast.LENGTH_SHORT).show();
                                for (int i=0;  i < dataAnno.getAnnotationData().size(); i++){
                                    TextView tvx = findViewById(getResIdForSubscriberIndex(i));
                                    if (tvx != null){
                                        if (index == i){
                                            //tvx.setTextColor(getColor(R.color.design_orange));
                                            tvx.setTextSize(20);
                                        }else {
                                            // tvx.setTextColor(getColor(R.color.black));
                                            tvx.setTextSize(14);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;

            case SWIPE_BACKWARD:
                if (aBoolean){
                    if (layout2.getVisibility() == View.VISIBLE){
                        Toast.makeText(InstrumentActivity.this, "Close the Chart First", Toast.LENGTH_SHORT).show();
                    }else {
                        int size = dataAnno.getAnnotationData().size();
                        if (size>0){
                            if (index >0){
                                index = index - 1;
                                Toast.makeText(InstrumentActivity.this, "Previous", Toast.LENGTH_SHORT).show();
                                for (int i=0;  i < dataAnno.getAnnotationData().size(); i++){
                                    TextView tvx = findViewById(getResIdForSubscriberIndex(i));
                                    if (tvx != null){
                                        if (index == i){
                                            // tvx.setTextColor(getColor(R.color.design_orange));
                                            tvx.setTextSize(20);
                                        }else {
                                            //tvx.setTextColor(getColor(R.color.black));
                                            tvx.setTextSize(14);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;

            case SWIPE_UP:
                if (index < 0){
                    Toast.makeText(InstrumentActivity.this, "Please SWIPE FW to select annotation", Toast.LENGTH_SHORT).show();
                }else if (layout2.getVisibility() == View.VISIBLE){
                    Toast.makeText(InstrumentActivity.this, "Already Opened the Chart", Toast.LENGTH_SHORT).show();
                } else {
                    OpenDialog(index);
                }
                return true;

            case SWIPE_DOWN:
                    if (layout2.getVisibility() == View.VISIBLE){
                            Log.e("sam", "here is showing");
                            layout1.setVisibility(View.VISIBLE);
                            layout2.setVisibility(View.GONE);
                            return true;
                    }else {
                        Log.e("dsa", "is null");
                        return super.onGesture(gesture);
                    }

            default:
                return super.onGesture(gesture);
        }
    }

    private int getResIdForSubscriberIndex(int index) {
        TypedArray arr = getResources().obtainTypedArray(R.array.instrument_tv_ids);
        int subId = arr.getResourceId(index, 0);
        arr.recycle();
        return subId;
    }


    /**
     * Returns the bitmap position inside an imageView.
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    public static int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }

}