package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class SelectMenuActivity extends AppCompatActivity {

    public int mode = -1;
    public int goal = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_menu);

        CheckBox checkbox = findViewById(R.id.demo_checkbox);

        Button menu_button_01 = findViewById(R.id.menu_button_01);
        menu_button_01.setOnClickListener(arg0 -> {
            this.mode = 0;
            this.goal = 100;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("MODE", this.mode);
            intent.putExtra("GOAL", this.goal);
            startActivity(intent);
        });

        Button menu_button_02 = findViewById(R.id.menu_button_02);
        menu_button_02.setOnClickListener(arg0 -> {
            this.mode = 1;
            if (!checkbox.isChecked()) {
                this.goal = 1000;
            } else {
                this.goal = 100;
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("MODE", this.mode);
            intent.putExtra("GOAL", this.goal);
            startActivity(intent);
        });

        Button menu_button_03 = findViewById(R.id.menu_button_03);
        menu_button_03.setOnClickListener(arg0 -> {
            this.mode = 2;
            if (!checkbox.isChecked()) {
                this.goal = 3939;
            } else {
                this.goal = 100;
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("MODE", this.mode);
            intent.putExtra("GOAL", this.goal);
            startActivity(intent);
        });
    }
}