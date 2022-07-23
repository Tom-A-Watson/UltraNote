package com.example.ultranote.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.ultranote.R;
import java.util.ArrayList;
import java.util.List;
import adapters.NotesAdapter;
import database.NotesDatabase;
import entities.Note;
import listeners.NotesListener;

/**
 * This class contains all functionality regarding user-interaction on the the Home page. Some of
 * this entails sending specific information to the CreateNote class via Intents.
 */
public class Home extends AppCompatActivity implements NotesListener, View.OnClickListener,
                                                       TextWatcher, TextView.OnEditorActionListener {
    private EditText searchInput, quickTitleInput;
    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    private AlertDialog addURLDialog;
    private int noteClickedPosition = -1;

    // Request codes
    public static final int ADD_NOTE = 1;
    public static final int UPDATE_NOTE = 2;
    public static final int SHOW_NOTES = 3;
    public static final int SELECT_IMAGE = 4;
    public static final int STORAGE_PERMISSION = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getNotes(SHOW_NOTES, false);

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        searchInput = findViewById(R.id.searchNotesInput);
        quickTitleInput = findViewById(R.id.quickTitleInput);
        notesRecyclerView.setAdapter(notesAdapter);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.quickAddImage).setOnClickListener(this);
        findViewById(R.id.quickAddURL).setOnClickListener(this);
        searchInput.addTextChangedListener(this);
        quickTitleInput.setOnEditorActionListener(this);
    }

    public void openCreateNote(View view) {
        Intent intent = new Intent(this, CreateNote.class); startActivity(intent);
    }

    private void selectImage() {
        Intent selectedImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (selectedImg.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(selectedImg, SELECT_IMAGE);
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

    private String getPath(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);

        if (cursor == null) { filePath = contentUri.getPath(); }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNote.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, UPDATE_NOTE);
    }

    private void getNotes(final int requestCode, final boolean noteIsDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>>
        {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                if (requestCode == SHOW_NOTES) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == UPDATE_NOTE) {
                    noteList.remove(noteClickedPosition);

                    if (noteIsDeleted) { notesAdapter.notifyItemRemoved(noteClickedPosition); }
                    else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
        }

        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_NOTE: getNotes(ADD_NOTE, false); break;

                case UPDATE_NOTE:
                    if (data != null) {
                        getNotes(UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
                    } break;

                case SELECT_IMAGE:
                    if (data != null) {
                        Uri selectedImageUri = data.getData();

                        if (selectedImageUri != null) {
                            try {
                                String selectedImagePath = getPath(selectedImageUri);
                                Intent noteWithImage = new Intent(getApplicationContext(), CreateNote.class);
                                noteWithImage.putExtra("isFromQuickActions", true);
                                noteWithImage.putExtra("quickActionType", "image");
                                noteWithImage.putExtra("imagePath", selectedImagePath);
                                startActivityForResult(noteWithImage, ADD_NOTE);
                            } catch (Exception e) {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } break;
            }
        }
    }

    private void quickAddURL() {
        if (addURLDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
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
                        Toast.makeText(Home.this, "Enter a URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(Home.this, "Enter a valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        addURLDialog.dismiss();
                        Intent noteWithURL = new Intent(getApplicationContext(), CreateNote.class);
                        noteWithURL.putExtra("isFromQuickActions", true);
                        noteWithURL.putExtra("quickActionType", "URL");
                        noteWithURL.putExtra("URL", inputURL.getText().toString());
                        startActivityForResult(noteWithURL, ADD_NOTE);
                    }
                }
            });

            view.findViewById(R.id.cancelAddURL).setOnClickListener(this);
        }

        addURLDialog.show();
    }

    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {} // Method not required

    @Override
    public void onTextChanged(CharSequence cs, int i, int i1, int i2) { notesAdapter.cancelTimer(); }

    @Override
    public void afterTextChanged(Editable s) {
        if (noteList.size() != 0) { notesAdapter.searchNotes(s.toString()); }
    }

    @Override
    public boolean onEditorAction(TextView textView, int a, KeyEvent evt) {
        if ((evt != null && (a == EditorInfo.IME_ACTION_DONE ||
                evt.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
            Intent noteWithTitle = new Intent(getApplicationContext(), CreateNote.class);
            noteWithTitle.putExtra("isFromQuickActions", true);
            noteWithTitle.putExtra("quickActionType", "title");
            noteWithTitle.putExtra("quickTitle", quickTitleInput.getText().toString().trim());
            startActivityForResult(noteWithTitle, ADD_NOTE);
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Simple 1-line implementations
            case R.id.backButton: onBackPressed(); break;
            case R.id.quickAddURL: quickAddURL(); break;
            case R.id.cancelAddURL: addURLDialog.dismiss(); break;

            case R.id.quickAddImage:   // Ask the user for permission to access the gallery
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            Home.this,
                            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                            STORAGE_PERMISSION
                    );
                } else { selectImage(); } break;
        }
    }
}