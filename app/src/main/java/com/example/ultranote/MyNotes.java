package com.example.ultranote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MyNotes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mynotes);

        ImageView createNote = (ImageView) findViewById(R.id.createNoteBtn);

        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateNote();
            }
        });

    }

    private void openCreateNote() {
        Intent intent = new Intent(this, CreateNote.class);
        startActivity(intent);
    }

}
