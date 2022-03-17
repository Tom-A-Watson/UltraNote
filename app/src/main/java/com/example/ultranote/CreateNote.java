package com.example.ultranote;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CreateNote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnote);
        this.setTitle("Create a Note");

    }
}
