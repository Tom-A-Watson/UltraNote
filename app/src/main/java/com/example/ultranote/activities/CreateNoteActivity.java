package com.example.ultranote.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ultranote.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import database.NotesDatabase;
import entities.Note;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText noteTitleInput, noteSubtitleInput, noteInput;
    private TextView textDateTime;
    private View noteColourIndicator;
    private ImageView noteImage;
    final Note note = new Note();

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

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
        noteImage = findViewById(R.id.noteImage);

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

        ImageView addImageBtn = findViewById(R.id.addImageButton);
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if  (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateNoteActivity.this,
                            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }

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
                Intent homePage = new Intent(CreateNoteActivity.this, Home.class);

                setResult(RESULT_OK, intent);
                homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(homePage);
            }
        }

        new SaveNoteTask().execute();
    }

    private void initColourPicker() {
        final LinearLayout colourPickerLayout = findViewById(R.id.colourPickerLayout);
        final BottomSheetBehavior<LinearLayout> bsb = BottomSheetBehavior.from(colourPickerLayout);

        colourPickerLayout.findViewById(R.id.colourPickerText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bsb.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView grey = colourPickerLayout.findViewById(R.id.imageColour1);
        final ImageView red = colourPickerLayout.findViewById(R.id.imageColour2);
        final ImageView orange = colourPickerLayout.findViewById(R.id.imageColour3);
        final ImageView lightOrange = colourPickerLayout.findViewById(R.id.imageColour4);
        final ImageView yellow = colourPickerLayout.findViewById(R.id.imageColour5);
        final ImageView lightGreen = colourPickerLayout.findViewById(R.id.imageColour6);
        final ImageView green = colourPickerLayout.findViewById(R.id.imageColour7);
        final ImageView lightBlue = colourPickerLayout.findViewById(R.id.imageColour8);
        final ImageView blue = colourPickerLayout.findViewById(R.id.imageColour9);
        final ImageView indigo = colourPickerLayout.findViewById(R.id.imageColour10);
        final ImageView purple = colourPickerLayout.findViewById(R.id.imageColour11);
        final ImageView violet = colourPickerLayout.findViewById(R.id.imageColour12);
        final ImageView lightMaroon = colourPickerLayout.findViewById(R.id.imageColour13);
        final ImageView[] colours = new ImageView[] { grey, red, orange, lightOrange, yellow, lightGreen,
                green, lightBlue, blue, indigo, purple, violet, lightMaroon };

        colourPickerLayout.findViewById(R.id.viewColour1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 1);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#333333"));
                note.setColour("#333333");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 2);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF2929"));
                note.setColour("#FF2929");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 3);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF5722"));
                note.setColour("#FF5722");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 4);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF9800"));
                note.setColour("#FF9800");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 5);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FFE719"));
                note.setColour("#FFE719");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 6);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#8BC34A"));
                note.setColour("#8BC34A");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 7);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
                note.setColour("#4CAF50");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 8);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#00BCD4"));
                note.setColour("#00BCD4");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 9);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#2196F3"));
                note.setColour("#2196F3");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 10);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#3F51B5"));
                note.setColour("#3F51B5");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 11);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#673AB7"));
                note.setColour("#673AB7");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 12);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#9C27B0"));
                note.setColour("#9C27B0");

            }
        });
        colourPickerLayout.findViewById(R.id.viewColour13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColour(colours, 13);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#E91E63"));
                note.setColour("#E91E63");
            }
        });
    }

    private void chooseColour(ImageView[] colours, int colourNumber) {
        for (int i = 0; i < colours.length; i++) {
            if (i == colourNumber - 1) {
                colours[i].setImageResource(R.drawable.ic_done);
                continue;
            }

            colours[i].setImageResource(0);
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {

                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        noteImage.setImageBitmap(bitmap);
                        noteImage.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


}