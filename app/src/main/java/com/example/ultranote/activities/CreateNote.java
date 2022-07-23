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

public class CreateNote extends AppCompatActivity implements View.OnClickListener {

    final Note note = new Note();
    private EditText noteTitleInput, noteSubtitleInput, noteInput;
    private TextView textDateTime, webURL;
    private View noteColourIndicator;
    private ImageView noteImage;
    private String selectedImagePath;
    private LinearLayout webURLLayout;
    private AlertDialog addURLDialog, deleteNoteDialog;
    private Note existingNote;
    private final SimpleDateFormat singleLineDate = new SimpleDateFormat(
            "EEEE dd MMMM yyyy HH:mm a", Locale.getDefault());

    // Request codes
    private static final int STORAGE_PERMISSION = 1;
    private static final int SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnoteactivity);
        initNoteOptions();

        noteTitleInput = findViewById(R.id.noteTitleInput);
        noteSubtitleInput = findViewById(R.id.noteSubtitleInput);
        noteInput = findViewById(R.id.noteInput);
        textDateTime = findViewById(R.id.textDateTime);
        textDateTime.setText(singleLineDate.format(new Date()));
        noteColourIndicator = findViewById(R.id.noteColourIndicator);
        noteImage = findViewById(R.id.noteImage);
        selectedImagePath = "";
        webURL = findViewById(R.id.webUrl);
        webURLLayout = findViewById(R.id.webUrlLayout);

        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.saveButton).setOnClickListener(this);
        findViewById(R.id.addURL).setOnClickListener(this);
        findViewById(R.id.deleteURL).setOnClickListener(this);
        findViewById(R.id.addImage).setOnClickListener(this);
        findViewById(R.id.deleteImage).setOnClickListener(this);

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            existingNote = (Note) getIntent().getSerializableExtra("note");
            viewOrUpdateNote();
        }

        if (getIntent().getBooleanExtra("isFromQuickActions", false)) {
            String type = getIntent().getStringExtra("quickActionType");

            if (type != null) {
                switch (type) {
                    case "title":
                        noteTitleInput.setText((getIntent().getStringExtra("quickTitle"))); break;
                    case "image":
                        selectedImagePath = getIntent().getStringExtra("imagePath");
                        noteImage.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                        noteImage.setVisibility(View.VISIBLE);
                        findViewById(R.id.deleteImage).setVisibility(View.VISIBLE); break;
                    case "URL":
                        webURL.setText(getIntent().getStringExtra("URL"));
                        webURLLayout.setVisibility(View.VISIBLE); break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        textDateTime = findViewById(R.id.textDateTime);
        textDateTime.setText(singleLineDate.format(new Date()));
        initNoteOptions();
    }

    private void saveNote() {
        final LinearLayout noteOptionsLayout = findViewById(R.id.noteOptionsLayout);
        final BottomSheetBehavior<LinearLayout> options = BottomSheetBehavior.from(noteOptionsLayout);

        if (noteTitleInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show(); return;
        } else if (noteInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "The note requires some content!", Toast.LENGTH_SHORT).show(); return;
        }

        if (webURLLayout.getVisibility() == View.VISIBLE) { note.setWebLink(webURL.getText().toString()); }
        if (existingNote != null) { note.setId(existingNote.getId()); }
        if (options.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            options.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note); return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                Intent homePage = new Intent(CreateNote.this, Home.class);

                setResult(RESULT_OK, intent);
                homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(homePage);
            }
        }

        TextView multiLineDate = findViewById(R.id.textDateTime);
        multiLineDate.setText(new SimpleDateFormat("EEEE dd MMMM yyyy \nHH:mm a",
                Locale.getDefault()).format((new Date())));

        note.setTitle(noteTitleInput.getText().toString());
        note.setSubtitle(noteSubtitleInput.getText().toString());
        note.setNoteText(noteInput.getText().toString());
        note.setDateTime(multiLineDate.getText().toString());
        note.setImagePath(selectedImagePath);

        new SaveNoteTask().execute();
    }

    private void deleteNote() {
        if (deleteNoteDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.delete_note_layout,
                    (ViewGroup) findViewById(R.id.deleteNoteLayout)
            );
            builder.setView(view);
            deleteNoteDialog = builder.create();

            if (deleteNoteDialog.getWindow() != null) {
                deleteNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.deleteNote).setOnClickListener(this);
            view.findViewById(R.id.cancelDeleteNote).setOnClickListener(this);
        }

        deleteNoteDialog.show();
    }

    private void initNoteOptions() {
        findViewById(R.id.noteOptionsText).setOnClickListener(this);
        findViewById(R.id.noteColourIndicator).setOnClickListener(this);

        final ImageView grey = findViewById(R.id.imageColour1);
        final ImageView red = findViewById(R.id.imageColour2);
        final ImageView orange = findViewById(R.id.imageColour3);
        final ImageView lightOrange = findViewById(R.id.imageColour4);
        final ImageView yellow = findViewById(R.id.imageColour5);
        final ImageView lightGreen = findViewById(R.id.imageColour6);
        final ImageView green = findViewById(R.id.imageColour7);
        final ImageView lightBlue = findViewById(R.id.imageColour8);
        final ImageView blue = findViewById(R.id.imageColour9);
        final ImageView indigo = findViewById(R.id.imageColour10);
        final ImageView purple = findViewById(R.id.imageColour11);
        final ImageView violet = findViewById(R.id.imageColour12);
        final ImageView lightMaroon = findViewById(R.id.imageColour13);
        final ImageView[] colours = new ImageView[] { grey, red, orange, lightOrange, yellow, lightGreen,
                green, lightBlue, blue, indigo, purple, violet, lightMaroon };

        if (existingNote != null) {
            findViewById(R.id.deleteBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.deleteBtn).setOnClickListener(this);
        }

        for (int i = 0; i < colours.length; i++) {
            final int j = i;

            colours[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectColour(colours, j + 1);
                    switch (j) {
                        case 0: note.setColour("#333333");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#333333"));
                            break;
                        case 1: note.setColour("#FF2929");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#FF2929"));
                            break;
                        case 2: note.setColour("#FF5722");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#FF5722"));
                            break;
                        case 3: note.setColour("#FF9800");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#FF9800"));
                            break;
                        case 4: note.setColour("#FFE719");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#FFE719"));
                            break;
                        case 5: note.setColour("#8BC34A");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#8BC34A"));
                            break;
                        case 6: note.setColour("#4CAF50");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
                            break;
                        case 7: note.setColour("#00BCD4");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#00BCD4"));
                            break;
                        case 8: note.setColour("#2196F3");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#2196F3"));
                            break;
                        case 9: note.setColour("#3F51B5");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#3F51B5"));
                            break;
                        case 10: note.setColour("#673AB7");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#673AB7"));
                            break;
                        case 11: note.setColour("#9C27B0");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#9C27B0"));
                            break;
                        case 12: note.setColour("#E91E63");
                            noteColourIndicator.setBackgroundColor(Color.parseColor("#E91E63"));
                            break;
                    }
                }
            });
        }

        if (existingNote != null && existingNote.getColour() != null
                && !existingNote.getColour().trim().isEmpty()) {
            switch (existingNote.getColour()) {
                case "#FF2929": note.setColour("#FF2929"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#FF2929"));
                    colours[1].setImageResource(R.drawable.ic_done); break;
                case "#FF5722": note.setColour("#FF5722"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#FF5722"));
                    colours[2].setImageResource(R.drawable.ic_done); break;
                case "#FF9800": note.setColour("#FF9800"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#FF9800"));
                    colours[3].setImageResource(R.drawable.ic_done); break;
                case "#FFE719": note.setColour("#FFE719"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#FFE719"));
                    colours[4].setImageResource(R.drawable.ic_done); break;
                case "#8BC34A": note.setColour("#8BC34A"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#8BC34A"));
                    colours[5].setImageResource(R.drawable.ic_done); break;
                case "#4CAF50": note.setColour("#4CAF50"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
                    colours[6].setImageResource(R.drawable.ic_done); break;
                case "#00BCD4": note.setColour("#00BCD4"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#00BCD4"));
                    colours[7].setImageResource(R.drawable.ic_done); break;
                case "#2196F3": note.setColour("#2196F3"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#2196F3"));
                    colours[8].setImageResource(R.drawable.ic_done); break;
                case "#3F51B5": note.setColour("#3F51B5"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#3F51B5"));
                    colours[9].setImageResource(R.drawable.ic_done); break;
                case "#673AB7": note.setColour("#673AB7"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#673AB7"));
                    colours[10].setImageResource(R.drawable.ic_done); break;
                case "#9C27B0": note.setColour("#9C27B0"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#9C27B0"));
                    colours[11].setImageResource(R.drawable.ic_done); break;
                case "#E91E63": note.setColour("#E91E63"); colours[0].setImageResource(0);
                    noteColourIndicator.setBackgroundColor(Color.parseColor("#E91E63"));
                    colours[12].setImageResource(R.drawable.ic_done); break;
            }
        }
    }

    private void toggleNoteOptions(BottomSheetBehavior<LinearLayout> bsb) {
        if (bsb.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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
            startActivityForResult(intent, SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, perms, results);

        if (requestCode == STORAGE_PERMISSION && results.length > 0) {
            if (results[0] == PackageManager.PERMISSION_GRANTED) { selectImage(); }
            else { Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show(); }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        noteImage.setImageBitmap(bitmap);
                        noteImage.setVisibility(View.VISIBLE);
                        selectedImagePath = getPath(selectedImageUri);

                        findViewById(R.id.deleteImage).setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPath(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);

        if (cursor == null) { filePath = contentUri.getPath(); }
        else {
            cursor.moveToFirst();
            int i = cursor.getColumnIndex("_data");
            filePath = cursor.getString(i);
            cursor.close();
        }

        return filePath;
    }

    private void addURL() {
        if (addURLDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
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

            view.findViewById(R.id.confirmAddURL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNote.this, "Enter a URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNote.this, "Enter a valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        webURL.setText(inputURL.getText().toString());
                        webURLLayout.setVisibility(View.VISIBLE);
                        addURLDialog.dismiss();
                    }
                }
            });

            view.findViewById(R.id.cancelAddURL).setOnClickListener(this);
        }

        addURLDialog.show();
    }

    private void viewOrUpdateNote() {
        noteTitleInput.setText(existingNote.getTitle());
        noteSubtitleInput.setText(existingNote.getSubtitle());
        noteInput.setText(existingNote.getNoteText());
        textDateTime.setText(existingNote.getDateTime());

        if (existingNote.getImagePath() != null && !existingNote.getImagePath().trim().isEmpty()) {
            noteImage.setImageBitmap(BitmapFactory.decodeFile(existingNote.getImagePath()));
            noteImage.setVisibility(View.VISIBLE);
            selectedImagePath = existingNote.getImagePath();

            findViewById(R.id.deleteImage).setVisibility(View.VISIBLE);
        }

        if (existingNote.getWebLink() != null && !existingNote.getWebLink().trim().isEmpty()) {
            webURL.setText(existingNote.getWebLink());
            webURLLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        final LinearLayout noteOptionsLayout = findViewById(R.id.noteOptionsLayout);
        final BottomSheetBehavior<LinearLayout> options = BottomSheetBehavior.from(noteOptionsLayout);

        switch (view.getId()) {
            // Simple 1-line implementations
            case R.id.backButton: onBackPressed(); break;
            case R.id.saveButton: saveNote(); break;
            case R.id.addURL: addURL(); break;
            case R.id.cancelAddURL: addURLDialog.dismiss(); break;
            case R.id.deleteURL: webURL.setText(null); webURLLayout.setVisibility(View.GONE); break;
            case R.id.noteColourIndicator: case R.id.noteOptionsText: toggleNoteOptions(options); break;
            case R.id.deleteBtn: options.setState(BottomSheetBehavior.STATE_COLLAPSED); deleteNote(); break;
            case R.id.cancelDeleteNote: deleteNoteDialog.dismiss(); break;

            case R.id.addImage:   // Ask the user for permission to access the gallery
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            CreateNote.this,
                            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                            STORAGE_PERMISSION
                    );
                } else { selectImage(); } break;

            case R.id.deleteImage:   // Delete the image from the note
                noteImage.setImageBitmap(null); noteImage.setVisibility(View.GONE);
                findViewById(R.id.deleteImage).setVisibility(View.GONE); selectedImagePath = ""; break;

            case R.id.deleteNote:   // Delete the note, then throw a toast message to confirm this
                class DeleteNoteTask extends AsyncTask<Void, Void, Void>
                {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(existingNote);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent intent = new Intent();
                        intent.putExtra("isNoteDeleted", true);
                        setResult(RESULT_OK, intent);
                        Toast.makeText(CreateNote.this, "'" + existingNote.getTitle() + "'"
                                + " deleted", Toast.LENGTH_LONG).show(); finish();
                    }
                }

                new DeleteNoteTask().execute(); break;
        }
    }
}