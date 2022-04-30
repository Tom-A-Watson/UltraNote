package com.example.ultranote.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private TextView textDateTime, webURL;
    private View noteColourIndicator;
    private ImageView noteImage;
    private String selectedImagePath;
    private LinearLayout webURLLayout;
    private AlertDialog addURLDialog;
    private Note existingNote;
    final Note note = new Note();

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        noteTitleInput = findViewById(R.id.noteTitleInput);
        noteSubtitleInput = findViewById(R.id.noteSubtitleInput);
        noteInput = findViewById(R.id.noteInput);
        textDateTime = findViewById(R.id.textDateTime);
        noteColourIndicator = findViewById(R.id.noteColourIndicator);
        noteImage = findViewById(R.id.noteImage);
        selectedImagePath = "";
        webURL = findViewById(R.id.webUrl);
        webURLLayout = findViewById(R.id.webUrlLayout);

        textDateTime.setText(
                new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm a",
                        Locale.getDefault()).format((new Date()))
        );

        ImageView backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ImageView saveBtn = findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        ImageView addURLBtn = findViewById(R.id.addURLButton);
        addURLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddURLDialog();
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

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            existingNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        initColourPicker();
    }

    private void setViewOrUpdateNote() {
        noteTitleInput.setText(existingNote.getTitle());
        noteSubtitleInput.setText(existingNote.getSubtitle());
        noteInput.setText(existingNote.getNoteText());
        textDateTime.setText(existingNote.getDateTime());

        if (existingNote.getImagePath() != null && !existingNote.getImagePath().trim().isEmpty()) {
            noteImage.setImageBitmap(BitmapFactory.decodeFile(existingNote.getImagePath()));
            noteImage.setVisibility(View.VISIBLE);
            selectedImagePath = existingNote.getImagePath();
        }

        if (existingNote.getWebLink() != null && !existingNote.getWebLink().trim().isEmpty()) {
            webURL.setText(existingNote.getWebLink());
            webURLLayout.setVisibility(View.VISIBLE);
        }
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
        note.setImagePath(selectedImagePath);

        if (webURLLayout.getVisibility() == View.VISIBLE) {
            note.setWebLink(webURL.getText().toString());
        }

        if (existingNote != null) {
            note.setId(existingNote.getId());
        }

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

        if (existingNote != null && existingNote.getColour() != null
                && !existingNote.getColour().trim().isEmpty()) {
            switch (existingNote.getColour()) {
                case "#FF2929":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#FF2929"));
                    note.setColour("#FF2929"); break;
                case "#FF5722":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#FF5722"));
                    note.setColour("#FF5722"); break;
                case "#FF9800":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#FF9800"));
                    note.setColour("#FF9800"); break;
                case "#FFE719":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#FFE719"));
                    note.setColour("#FFE719"); break;
                case "#8BC34A":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#8BC34A"));
                    note.setColour("#8BC34A"); break;
                case "#4CAF50":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
                    note.setColour("#4CAF50"); break;
                case "#00BCD4":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#00BCD4"));
                    note.setColour("#00BCD4"); break;
                case "#2196F3":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#2196F3"));
                    note.setColour("#2196F3"); break;
                case "#3F51B5":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#3F51B5"));
                    note.setColour("#3F51B5"); break;
                case "#673AB7":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#673AB7"));
                    note.setColour("#673AB7"); break;
                case "#9C27B0":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#9C27B0"));
                    note.setColour("#9C27B0"); break;
                case "#E91E63":
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#E91E63"));
                    note.setColour("#E91E63"); break;
            }
        }

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
                selectColour(colours, 1);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#333333"));
                note.setColour("#333333");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 2);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF2929"));
                note.setColour("#FF2929");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 3);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF5722"));
                note.setColour("#FF5722");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 4);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FF9800"));
                note.setColour("#FF9800");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 5);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#FFE719"));
                note.setColour("#FFE719");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 6);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#8BC34A"));
                note.setColour("#8BC34A");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 7);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
                note.setColour("#4CAF50");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 8);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#00BCD4"));
                note.setColour("#00BCD4");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 9);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#2196F3"));
                note.setColour("#2196F3");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 10);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#3F51B5"));
                note.setColour("#3F51B5");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 11);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#673AB7"));
                note.setColour("#673AB7");
            }
        });
        colourPickerLayout.findViewById(R.id.viewColour12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 12);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#9C27B0"));
                note.setColour("#9C27B0");

            }
        });
        colourPickerLayout.findViewById(R.id.viewColour13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColour(colours, 13);
                noteColourIndicator.setBackgroundColor(Color.parseColor("#E91E63"));
                note.setColour("#E91E63");
            }
        });
    }

    private void selectColour(ImageView[] colours, int colourNumber) {
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
                        selectedImagePath = getPathFromUri(selectedImageUri);

                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);

        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }

    private void showAddURLDialog() {
        if (addURLDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.add_url_layout,
                    (ViewGroup) findViewById(R.id.addURLLayout)
            );
            builder.setView(view);

            addURLDialog = builder.create();
            if (addURLDialog.getWindow() != null) {
                addURLDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter a URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter a valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        webURL.setText(inputURL.getText().toString());
                        webURLLayout.setVisibility(View.VISIBLE);
                        addURLDialog.dismiss();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addURLDialog.dismiss();
                }
            });
        }

        addURLDialog.show();
    }

}