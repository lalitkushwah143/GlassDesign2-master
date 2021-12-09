package com.example.android.glass.glassdesign2;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.android.glass.glassdesign2.data.DataRecipe;
import com.example.glass.ui.GlassGestureDetector;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Random;

public class ChartActivity extends BaseActivity {

    private LineChart chart;
    private LineData data = new LineData();
    private ArrayList<DataRecipe> arrayList= MonitorActivity.recipeArrayList;
    private float current_time=0, current_temp=0, current_pressure= 800;
    private float x, y;
    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Entry> entries1 = new ArrayList<>();
    private ArrayList<Entry> entries3 = new ArrayList<>();
    private ArrayList<Entry> entries4 = new ArrayList<>();
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    private LineDataSet dataSet, dataSet1, realSet, pressureSet;
    private Thread thread;
    private float real_time=0, real_temp=0, fix_temp = 0, real_pressure =0;
    private int total_time = 1675;
    private Boolean FLAG = false;
    private Boolean FLAG2 = false;
    private TextView tvPressure, tvTemp;

    private BarChart barChart;
    private ArrayList<BarEntry> barEntries = new ArrayList<>();
    private BarDataSet barDataSet;
    private BarData barData;
    private int barProgress =0;

    private ProgressBar barPositive, barNegative, barPositive2, barNegative2;
    private TextView tvZero, tvReal, tvAlert, tvTimeReal;
    private ConstraintLayout constraintLayout;
    private ProgressBar barPositivePre, barPositive2Pre;
    private TextView tvZeroPre, tvRealPre, tvAlertPre, tvTimeRealPre;
    private ConstraintLayout constraintLayoutPre;
    private TextView tvTitle, tvTitle2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chart = findViewById(R.id.activity_chart_chart);
        tvPressure = findViewById(R.id.activity_chart_tvPressure);
        tvTemp = findViewById(R.id.activity_chart_tvTemp);
        barChart = findViewById(R.id.activity_chart_barchart);
        barPositive = findViewById(R.id.activity_chart_barPositive);
        barNegative = findViewById(R.id.activity_chart_barNegative);
        barNegative2 = findViewById(R.id.activity_chart_barNegativeReal);
        barPositive2 = findViewById(R.id.activity_chart_barPositiveReal);
        tvZero = findViewById(R.id.activity_chart_tvZero);
        tvReal = findViewById(R.id.activity_chart_tvReal);
        constraintLayout = findViewById(R.id.activity_chart_layout);
        tvAlert = findViewById(R.id.activity_chart_tvAlert);
        tvTimeReal = findViewById(R.id.activity_chart_tvTimeReal);
        barPositivePre = findViewById(R.id.activity_chart_barPositive2);
        barPositive2Pre = findViewById(R.id.activity_chart_barPositiveReal2);
        tvZeroPre = findViewById(R.id.activity_chart_tvZero2);
        tvRealPre = findViewById(R.id.activity_chart_tvReal2);
        constraintLayoutPre = findViewById(R.id.activity_chart_layout2);
        tvAlertPre = findViewById(R.id.activity_chart_tvAlert2);
        tvTimeRealPre = findViewById(R.id.activity_chart_tvTimeReal2);
        tvTitle = findViewById(R.id.activity_chart_tvTitle1);
        tvTitle2 = findViewById(R.id.activity_chart_tvTitle2);

        barChart.setBackgroundColor(Color.WHITE);
        barChart.setExtraTopOffset(-30f);
        barChart.setExtraBottomOffset(10f);
        barChart.setExtraLeftOffset(70f);
        barChart.setExtraRightOffset(70f);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
     //   xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(5);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);

        YAxis left = barChart.getAxisLeft();
        left.setDrawLabels(false);
        left.setSpaceTop(25f);
        left.setSpaceBottom(25f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);


        barEntries.add(0, new BarEntry(0, new float[]{350, 1700}));

        barDataSet = new BarDataSet(barEntries, "Temp");
        barDataSet.setColor(getResources().getColor(R.color.color_green_light));

        barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        //barData.setDrawValues(false);
        barChart.setData(barData);

        loadData();
        dataSet = new LineDataSet(entries, "Temperature");
        dataSet1 = new LineDataSet(entries1, "Pressure");
        dataSet1.setColor(getResources().getColor(R.color.dark_pink));
        dataSet1.setCircleColor(getResources().getColor(R.color.dark_pink));

        realSet = new LineDataSet(entries3, "RealTime Data");
        realSet.setColor(getResources().getColor(R.color.chart_real));
        realSet.setCircleColor(getResources().getColor(R.color.chart_real));

        pressureSet = new LineDataSet(entries4, "RealTime Data");
        pressureSet.setColor(getResources().getColor(R.color.chart_real2));
        pressureSet.setCircleColor(getResources().getColor(R.color.chart_real2));

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        realSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        pressureSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(getResources().getColor(R.color.dark_pink));
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);

        dataSets.add(dataSet);
        dataSets.add(dataSet1);
        dataSets.add(realSet);
        dataSets.add(pressureSet);

        data =new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(getResources().getColor(R.color.color_back));
        chart.setHighlightPerDragEnabled(true);

        feedRealtime();
    }

    private void loadData() {

        for (int i=0; i< arrayList.size(); i++){
            DataRecipe lyo = arrayList.get(i);
            x = (float) (lyo.getTemp1() - current_temp)/lyo.getTime1();
            y = (float) (lyo.getPressure() - current_pressure)/lyo.getTime1();
            Log.e("This:  ", x+" "+current_time+" "+ current_temp+" ");

            for (int j=0; j<lyo.getTime1(); j++){
                current_time++;
                current_temp = current_temp + x;
                current_pressure = current_pressure + y;
                entries.add(new Entry(current_time, current_temp));
                entries1.add(new Entry(current_time, current_pressure));
            }
            Log.e("This:  ", current_time+" "+ current_temp+" ");

            for (int k=0; k<lyo.getTime2(); k++){
                current_time++;
                entries.add(new Entry(current_time, current_temp));
                entries1.add(new Entry(current_time, current_pressure));
            }
            Log.e("This:  ", current_time+" "+ current_temp+" ");

        }
    }

    private void feedRealtime() {

        entries3.add(new Entry(0, 0));
        entries4.add(new Entry(0, 800));

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry();
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i< arrayList.size(); i++) {
                    DataRecipe lyo = arrayList.get(i);
                    float temp_pre = 0;
                    float pressure_pre =0;
                    if (i==0){
                        temp_pre =0f;
                        pressure_pre = 0f;

                    }else {
                        DataRecipe lyo1 = arrayList.get(i-1);
                        temp_pre = (float) lyo1.getTemp1();
                        pressure_pre = (float) lyo1.getPressure();
                    }
                    x = (float) (lyo.getTemp1() - temp_pre) / lyo.getTime1();
                    y = (float) (-1)*(lyo.getPressure() - pressure_pre) / lyo.getTime1();
                    FLAG2 = real_temp < lyo.getTemp1();
                    if (FLAG2){
                        Log.e("Randomx", "Section 1");
                        for (int j = 0; j < (lyo.getTime2()+ lyo.getTime1()); j++) {
                            real_time++;
                            if (real_temp < lyo.getTemp1()){
                                Random random = new Random();
                                if (x==0){
                                    Log.e("Chart", "Nothing");
                                }else if (x < 0) {
                                    Log.e("Random ", "Yes");
                                    float z= -x;
                                    int bound = (int) (z*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc +" "+real_temp );
                                    real_temp = real_temp - abc;
                                } else {
                                    int bound = (int) (x*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc +" "+real_temp );
                                    real_temp = real_temp + abc;
                                }
                            }else {
                                real_temp = lyo.getTemp1();
                            }

                            /////////
                            if (real_pressure > lyo.getPressure()){
                                Random random = new Random();
                                if (y==0){
                                    Log.e("Chart", "Nothing");
                                }else if (y < 0) {
                                    Log.e("Random ", "Yes");
                                    float z= -y;
                                    int bound = (int) (z*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc2 = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc2 +" "+real_pressure );
                                    real_pressure = real_pressure - abc2;
                                } else {
                                    int bound = (int) (y*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc2 = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc2 +" "+real_pressure );
                                    real_pressure = real_pressure - abc2;
                                }
                            }else {
                                real_pressure = lyo.getPressure();
                            }
                            runOnUiThread(runnable);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //addEntry();
                            // entries3.add(new Entry(real_time, real_temp));

                        }
                    }else {
                        Log.e("Randomx", "Section 2");
                        for (int j = 0; j < (lyo.getTime2()+ lyo.getTime1()); j++) {
                            real_time++;
                            if (real_temp > lyo.getTemp1()){
                                Random random = new Random();
                                if (x==0){
                                    Log.e("Chart", "Nothing");
                                }else if (x < 0) {
                                    Log.e("Random ", "Yes");
                                    float z= -x;
                                    int bound = (int) (z*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc +" "+real_temp );
                                    real_temp = real_temp - abc;
                                } else {
                                    int bound = (int) (x*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc +" "+real_temp );
                                    real_temp = real_temp + abc;
                                }
                            }else {
                                real_temp = lyo.getTemp1();
                            }

                            /////////
                            if (real_pressure > lyo.getPressure()){
                                Random random = new Random();
                                if (y==0){
                                    Log.e("Chart", "Nothing");
                                }else if (y < 0) {
                                    Log.e("Random ", "Yes");
                                    float z= -y;
                                    int bound = (int) (z*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc2 = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc2 +" "+real_pressure );
                                    real_pressure = real_pressure - abc2;
                                } else {
                                    int bound = (int) (y*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc2 = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc2 +" "+real_pressure );
                                    real_pressure = real_pressure - abc2;
                                }
                            }else {
                                real_pressure = lyo.getPressure();
                            }


                            runOnUiThread(runnable);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // addEntry();
                            //  entries3.add(new Entry(real_time, real_temp));
                        }
                    }

                }
            }
        });
        thread.start();

    }

    private void addEntry() {
        entries3.add(new Entry(real_time, real_temp));
        realSet.notifyDataSetChanged();
        entries4.add(new Entry(real_time, real_pressure));
        pressureSet.notifyDataSetChanged();
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.moveViewToX(entries3.size());
        chart.moveViewToX(entries4.size());
        tvPressure.setText("Time: " + (int)real_time);
        tvTemp.setText("Temp: " +real_temp);
        tvTimeReal.setText("Time: " + (int)real_time);
        tvTimeRealPre.setText("Time: " + (int)real_time);

        float s= entries.get((int)real_time -1).getY();
        tvZero.setText("Expected Values: " + Math.round(s) + "°C");
        tvReal.setText("Realtime Values: " + (int)real_temp + "°C");

        float s2= entries1.get((int)real_time -1).getY();
        tvZeroPre.setText("Expected Values: " + Math.round(s2));
        tvRealPre.setText("Realtime Values: " + (int)real_pressure);



        if (real_temp < 0 ){
            barPositive.setProgress(0);
            barNegative.setProgress((int)(Math.round(s)* (-1)));
            barPositive2.setProgress(0);
            barNegative2.setProgress((int)(real_temp* (-1)));

        }else if (real_temp > 0 )
        {
            barPositive.setProgress((int)s);
            barNegative.setProgress(0);
            barPositive2.setProgress((int)real_temp);
            barNegative2.setProgress(0);
        }else {
            barPositive.setProgress(0);
            barNegative.setProgress(0);

            barPositive2.setProgress(0);
            barNegative2.setProgress(0);
        }

        barPositivePre.setProgress(((int) s2) /10);
        barPositive2Pre.setProgress(((int)real_pressure) /10);

        if (Float.compare(s, real_temp) > 0){
            tvAlert.setText("Difference: " + ((int)Math.round(s) - (int)real_temp)  + "°C");
            if (((int)Math.round(s) - (int)real_temp) > 5){
                tvAlert.setTextColor(getResources().getColor(R.color.color_critical));
            }else {
                tvAlert.setTextColor(getResources().getColor(R.color.color_green_light));
            }
        }else if (((int)real_temp)  -(int)Math.round(s) > 0){
            tvAlert.setText("Difference: " + (((int)real_temp)  -(int)Math.round(s))  + "°C");
            if (((int)real_temp)  -(int)Math.round(s) > 5){
                tvAlert.setTextColor(getResources().getColor(R.color.color_critical));
            }else {
                tvAlert.setTextColor(getResources().getColor(R.color.color_green_light));
            }
        }else {
            tvAlert.setTextColor(getResources().getColor(R.color.color_green_light));
        }
        if (Float.compare(s2/10, real_pressure/10) > 0){
            tvAlertPre.setText("Difference: " + ((int)Math.round(s2) - (int)real_pressure));
            if (((int)Math.round(s2) - (int)real_pressure) > 100){
                tvAlertPre.setTextColor(getResources().getColor(R.color.color_critical));
            }else {
                tvAlertPre.setTextColor(getResources().getColor(R.color.color_green_light));
            }
        }else if (((int)real_pressure)/10  -(int)Math.round(s2)/10 > 0){
            tvAlertPre.setText("Difference: " + (((int)real_pressure)  -(int)Math.round(s2)));
            if (((int)real_pressure)  -(int)Math.round(s2) > 100){
                tvAlertPre.setTextColor(getResources().getColor(R.color.color_critical));
            }else {
                tvAlertPre.setTextColor(getResources().getColor(R.color.color_green_light));
            }
        }else {
            tvAlertPre.setText("Difference: " + (((int)real_pressure)/10  -(int)Math.round(s2)/10));
            tvAlertPre.setTextColor(getResources().getColor(R.color.color_green_light));
        }
        barEntries.set(0, new BarEntry(0, new float[]{real_time, 1700}));
        barData.notifyDataChanged();
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        //barChart.moveViewToX(barEntries.size());
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case SWIPE_FORWARD:
                if (chart.getVisibility() == View.VISIBLE){
                    constraintLayout.setVisibility(View.VISIBLE);
                    chart.setVisibility(View.GONE);
                    tvTemp.setVisibility(View.GONE);
                    tvPressure.setVisibility(View.GONE);
                    constraintLayoutPre.setVisibility(View.GONE);
                    tvTitle.setVisibility(View.VISIBLE);
                    tvTitle2.setVisibility(View.GONE);
                }else if (constraintLayout.getVisibility() == View.VISIBLE){
                    constraintLayout.setVisibility(View.GONE);
                    chart.setVisibility(View.GONE);
                    tvTemp.setVisibility(View.GONE);
                    tvPressure.setVisibility(View.GONE);
                    constraintLayoutPre.setVisibility(View.VISIBLE);
                    tvTitle.setVisibility(View.GONE);
                    tvTitle2.setVisibility(View.VISIBLE);
                }
                break;

            case SWIPE_BACKWARD:
                if (constraintLayoutPre.getVisibility() == View.VISIBLE){
                    chart.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tvPressure.setVisibility(View.GONE);
                    tvTemp.setVisibility(View.GONE);
                    constraintLayoutPre.setVisibility(View.GONE);
                    tvTitle2.setVisibility(View.GONE);
                    tvTitle.setVisibility(View.VISIBLE);
                }else if(constraintLayout.getVisibility() == View.VISIBLE){
                    chart.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.GONE);
                    tvPressure.setVisibility(View.VISIBLE);
                    tvTemp.setVisibility(View.VISIBLE);
                    constraintLayoutPre.setVisibility(View.GONE);
                    tvTitle.setVisibility(View.GONE);
                    tvTitle2.setVisibility(View.GONE);
                }
                break;

        }
        return super.onGesture(gesture);
    }
}