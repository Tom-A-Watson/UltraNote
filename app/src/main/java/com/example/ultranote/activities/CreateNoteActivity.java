package com.example.ultranote.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ultranote.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import database.NotesDatabase;
import entities.Note;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText noteTitleInput, noteSubtitleInput, noteInput;
    private TextView textDateTime;
    private View noteColourIndicator;
    final Note note = new Note();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        noteTitleInput = findViewById(R.id.noteTitleInput);
        noteSubtitleInput = findViewById(R.id.noteSubtitleInput);
        noteInput = findViewById(R.id.noteInput);
        textDateTime = findViewById(R.id.textDateTime);
        noteColourIndicator = findViewById(R.id.noteColourIndicator);

        textDateTime.setText(
                new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm a",
                        Locale.getDefault()).format((new Date()))
        );

        ImageView saveBtn = findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        initColourPicker();
    }

    private void saveNote() {
        if (noteTitleInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (noteInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "The note requires some content!", Toast.LENGTH_SHORT).show();
            return;
        }

        note.setTitle(noteTitleInput.getText().toString());
        note.setSubtitle(noteSubtitleInput.getText().toString());
        note.setNoteText(noteInput.getText().toString());
        note.setDateTime(textDateTime.getText().toString());

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new SaveNoteTask().execute();
    }

    private void initColourPicker() {
        final LinearLayout colourPickerLayout = findViewById(R.id.colourPickerLayout);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(colourPickerLayout);

        colourPickerLayout.findViewById(R.id.colourPickerText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColour1 = colourPickerLayout.findViewById(R.id.imageColour1);
        final ImageView imageColour2 = colourPickerLayout.findViewById(R.id.imageColour2);
        final ImageView imageColour3 = colourPickerLayout.findViewById(R.id.imageColour3);
        final ImageView imageColour4 = colourPickerLayout.findViewById(R.id.imageColour4);
        final ImageView imageColour5 = colourPickerLayout.findViewById(R.id.imageColour5);
        final ImageView imageColour6 = colourPickerLayout.findViewById(R.id.imageColour6);
        final ImageView imageColour7 = colourPickerLayout.findViewById(R.id.imageColour7);
        final ImageView imageColour8 = colourPickerLayout.findViewById(R.id.imageColour8);
        final ImageView imageColour9 = colourPickerLayout.findViewById(R.id.imageColour9);
        final ImageView imageColour10 = colourPickerLayout.findViewById(R.id.imageColour10);
        final ImageView imageColour11 = colourPickerLayout.findViewById(R.id.imageColour11);
        final ImageView imageColour12 = colourPickerLayout.findViewById(R.id.imageColour12);
        final ImageView imageColour13 = colourPickerLayout.findViewById(R.id.imageColour13);
        final ImageView[] imageColours = new ImageView[]{imageColour1, imageColour2, imageColour3,
                imageColour4, imageColour5, imageColour6, imageColour7, imageColour8, imageColour9,
                imageColour10, imageColour11, imageColour12, imageColour13};

        colourPickerLayout.findViewById(R.id.viewColour1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 1);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#333333"));
                note.setColour("#333333");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 2);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF2929"));
                note.setColour("#FF2929");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 3);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF5722"));
                note.setColour("#FF5722");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 4);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF9800"));
                note.setColour("#FF9800");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 5);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FFE719"));
                note.setColour("#FFE719");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 6);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#8BC34A"));
                note.setColour("#8BC34A");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 7);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
                note.setColour("#4CAF50");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 8);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#00BCD4"));
                note.setColour("#00BCD4");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 9);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#2196F3"));
                note.setColour("#2196F3");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 10);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#3F51B5"));
                note.setColour("#3F51B5");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 11);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#673AB7"));
                note.setColour("#673AB7");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 12);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#9C27B0"));
                note.setColour("#9C27B0");

            }
        });
        colourPickerLayout.findViewById(R.id.viewColour13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickSelected(imageColours, 13);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#E91E63"));
                note.setColour("#E91E63");
            }
        });
    }

    private void tickSelected(ImageView[] imageColours, int colourNumber) {

        for (int i = 0; i < imageColours.length; i++) {
            if (i == colourNumber - 1) {
                imageColours[i].setImageResource(R.drawable.ic_done);
                continue;
            }

            imageColours[i].setImageResource(0);
        }
    }

}