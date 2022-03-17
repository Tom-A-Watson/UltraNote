package com.example.ultranote;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MyNotes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mynotes);
        this.setTitle("My Notes");

    }
}
