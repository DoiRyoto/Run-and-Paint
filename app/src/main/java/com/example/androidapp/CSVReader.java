package com.example.androidapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<List<Double>> keroro(Context context) {
        AssetManager assetManager = context.getResources().getAssets();
        ArrayList<List<Double>> result = new ArrayList<>();
        try {
            // CSVファイルの読み込み
            InputStream inputStream = assetManager.open("keroro.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                String[] data = line.split(",");
                List<Double> xy = new ArrayList<>();
                xy.add(Double.parseDouble(data[1]));
                xy.add(Double.parseDouble(data[2]));
                result.add(xy);
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<List<Double>> heart(Context context) {
        AssetManager assetManager = context.getResources().getAssets();
        ArrayList<List<Double>> result = new ArrayList<>();
        try {
            // CSVファイルの読み込み
            InputStream inputStream = assetManager.open("heart.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                String[] data = line.split(",");
                List<Double> xy = new ArrayList<>();
                xy.add(Double.parseDouble(data[1]));
                xy.add(Double.parseDouble(data[2]));
                result.add(xy);
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<List<Double>> miku(Context context) {
        AssetManager assetManager = context.getResources().getAssets();
        ArrayList<List<Double>> result = new ArrayList<>();
        try {
            // CSVファイルの読み込み
            InputStream inputStream = assetManager.open("miku.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                String[] data = line.split(",");
                List<Double> xy = new ArrayList<>();
                xy.add(Double.parseDouble(data[1]));
                xy.add(Double.parseDouble(data[2]));
                result.add(xy);
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}