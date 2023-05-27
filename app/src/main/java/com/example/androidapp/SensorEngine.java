package com.example.androidapp;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 参考：https://github.com/LyricalMaestro/MPAndroidChartSample/blob/master/app/src/main/java/com/lyricaloriginal/mpandroidchartsample/DummySensorEngine.java

@RequiresApi(api = Build.VERSION_CODES.O)
class SensorEngine{
    public ArrayList<List<Double>> data = new ArrayList<>();
    public int data_size;
    CSVReader mCSVReader = new CSVReader();
    private final int mInterval;
    private final HandlerThread mThread;
    private final Handler mHandler;
    private final Handler mUiHandler;
    private boolean mRunning = false;
    private Listener mListener = null;
    public int progress_count = 0;
    private float rate;
    public float acc_rate = 0f;
    public int step = 0;
    private int prev_step = 0;
    public int main_goal;
    private List<Entry> entries = new ArrayList<>();
    float weight = 1f;
    public String selectedItem = "ウォーキング";

    SensorEngine(int interval, int mode, int goal, Context context) {
        if (interval < 10) {
            throw new IllegalArgumentException("intervalは10ms以上の整数を指定してください");
        }

        mInterval = interval;
        mUiHandler = new Handler();
        mThread = new HandlerThread("DummySensor");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        main_goal = goal;

        if (mode == 0) {
            data = mCSVReader.heart(context);
        } else if (mode == 1) {
            data = mCSVReader.keroro(context);
        } else if (mode == 2) {
            data = mCSVReader.miku(context);
        }

        data_size = data.size();
    }

    boolean isRunning() {
        return mRunning;
    }

    void setListener(Listener l) {
        if (!mRunning) {
            mListener = l;
        }
    }

    void start() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("このメソッドはUIスレッドから呼び出してください。");
        } else if (mRunning) {
            return;
        }
        mRunning = true;
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (!mRunning) {
                    return;
                }

                if(Objects.equals(selectedItem, "ジョギング")){
                    weight = 1.67f;
                } else if(Objects.equals(selectedItem, "ランニング")){
                    weight = 2.01f;
                } else if(Objects.equals(selectedItem, "ウォーキング")){
                    weight = 1.0f;
                }

                rate = ((float) step -  prev_step)/((float) main_goal)*weight;
                prev_step = step;
                acc_rate += rate;
                acc_rate = Math.min(acc_rate, 1f);
                progress_count = (int) (acc_rate*data_size);

                entries = new ArrayList<>();

                for(int i = 0;i < progress_count;i++){
                    final double x = data.get(i).get(0);
                    final double y = data.get(i).get(1);
                    entries.add(new Entry((float) x, (float) y));
                }

                mUiHandler.post(() -> {
                    if (mListener != null) {
                        mListener.onValueMonitored(entries);
                    }
                });

                mHandler.postDelayed(this, mInterval);
            }
        });
    }

    void stop() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("このメソッドはUIスレッドから呼び出してください。");
        }
        if (mRunning) {
            mRunning = false;
        }
    }

    void destroy() {
        if (mRunning) {
            return;
        }
        mThread.quit();
    }

    public interface Listener {
        void onValueMonitored(List<Entry> entries);
    }
}