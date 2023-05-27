package com.example.androidapp;

import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.Collections;
import java.util.List;

// 参考：https://akira-watson.com/android/spinner.html

public class MainActivity extends AppCompatActivity implements SensorEngine.Listener, SensorEventListener{

    private ScatterChart mChart;
    private SensorEngine mSensorEngine;
    private SensorManager sensorManager;
    private ProgressBar progressBar;
    private Sensor sensor;
    private StepCount stepCount;
    private final String[] spinnerItems = {"ウォーキング", "ジョギング", "ランニング"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCount = new StepCount();

        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                mSensorEngine.selectedItem = (String) spinner.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(TYPE_LINEAR_ACCELERATION);

        Intent intent = getIntent();
        int mode = intent.getIntExtra("MODE", 0);
        int goal = intent.getIntExtra("GOAL", 0);

        mSensorEngine = new SensorEngine(1000, mode, goal, getApplicationContext());
        mSensorEngine.setListener(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        mChart = findViewById(R.id.scatter_chart);

        if(mode == -1 || goal == -1) {
            Intent intent_error = new Intent(getApplicationContext(), SelectMenuActivity.class);
            startActivity(intent_error);
        }

        if(mode == 0){
            initChart(1f, -1f, 1.5f, -1f);
        } else if(mode == 1){
            initChart(5f, -6f, 6f, -10f);
        } else if(mode == 2){
            initChart(9f, -7.5f, 7f, -20f);
        }

        findViewById(R.id.start_button).setOnClickListener(v -> {
            if (mSensorEngine.isRunning()) {
                stopMonitoring();
            } else {
                startMonitoring();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();
        if (mSensorEngine.isRunning()) {
            stopMonitoring();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorEngine.destroy();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onValueMonitored(List<Entry> entries) {
        ScatterData data = mChart.getData();
        if (data == null) {
            return;
        }

        ScatterDataSet set = (ScatterDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            set = new ScatterDataSet(null, "");
            set.setColor(Color.BLACK);
            set.setScatterShapeHoleColor(Color.BLACK);
            set.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            set.setDrawValues(false);
            data.addDataSet(set);
        }

        Collections.sort(entries, new EntryXComparator());
        set.setValues(entries);

        mChart.getData().notifyDataChanged();
        mChart.notifyDataSetChanged();

        mChart.setVisibleXRangeMaximum(60);

        mChart.moveViewToX(data.getDataSetCount() - 61);

        progressBar.setProgress((int) (mSensorEngine.acc_rate*100f));

        if (mSensorEngine.acc_rate == 1) {
            this.finishMonitoring();
        }
    }

    private void initChart(float xmax, float xmin, float ymax, float ymin) {
        mChart.setBackgroundColor(Color.WHITE);

        ScatterData data = new ScatterData();
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setAxisMaximum(xmax);
        xl.setAxisMinimum(xmin);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMaximum(ymax);
        leftAxis.setAxisMinimum(ymin);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMonitoring() {
        mSensorEngine.start();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        TextView textView = findViewById(R.id.start_button);
        textView.setText(R.string.stop_button);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopMonitoring() {
        mSensorEngine.stop();
        sensorManager.unregisterListener(this);
        TextView textView = findViewById(R.id.start_button);
        textView.setText(R.string.start_button);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void finishMonitoring() {
        mSensorEngine.stop();
        sensorManager.unregisterListener(this);
        Button start_button = findViewById(R.id.start_button);
        start_button.setText(R.string.finish_button);

        start_button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SelectMenuActivity.class);
            startActivity(intent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] ac = event.values;

        if(stepCount.check_step(ac)){
            mSensorEngine.step++;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}