package com.example.ultranote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CreateNote extends AppCompatActivity {

    public static final int ADD_NOTE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnotepage);

        Button createTextNote = (Button) findViewById(R.id.createTextNoteBtn);
        createTextNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        ADD_NOTE_REQUEST_CODE
                );
            }
        });
    }
}
