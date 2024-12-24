package com.example.ultranote.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import settings.UserSettings;

/**
 * This class contains all functionality regarding user-interaction on the the Home page. Some of
 * this entails sending specific information to the CreateNote class via Intents.
 */
public class Home extends AppCompatActivity implements NotesListener, View.OnClickListener,
                                                       TextWatcher, TextView.OnEditorActionListener {
    private View homeView, addURLView;
    private EditText searchInput, quickTitleInput, inputURL;
    private TextView homeText;
    private ImageView backBtn, createNoteBtn, settingsBtn, searchIcon, quickAddImageBtn, quickAddURLBtn;
    private RecyclerView notesRecyclerView;
    private LinearLayout searchLayout, quickActionsLayout;
    private List<Note> list;
    private NotesAdapter adapter;
    private AlertDialog addURLDialog;
    private AlertDialog.Builder builder;
    private UserSettings settings;
    private int noteClickedPosition = -1;
    private boolean galleryAccessIsNotGranted;

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
        initComponents();
        updateView();
        getNotes(SHOW_NOTES, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void initComponents() {
        list = new ArrayList<>();
        adapter = new NotesAdapter(list, this);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        settings = (UserSettings) getApplication();
        homeText = findViewById(R.id.homeText);
        backBtn = findViewById(R.id.backButton);
        createNoteBtn = findViewById(R.id.createNoteBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        searchLayout = findViewById(R.id.searchLayout);
        searchInput = findViewById(R.id.searchNotesInput);
        searchInput.addTextChangedListener(this);
        searchIcon = findViewById(R.id.searchIcon);
        quickActionsLayout = findViewById(R.id.quickActions);
        quickAddImageBtn = findViewById(R.id.quickAddImage);
        quickAddURLBtn = findViewById(R.id.quickAddURL);
        homeView = findViewById(R.id.homeView);
        quickTitleInput = findViewById(R.id.quickTitleInput);
        quickTitleInput.setOnEditorActionListener(this);
        addURLView = LayoutInflater.from(this).inflate(
                R.layout.add_url_layout,
                (ViewGroup) findViewById(R.id.addURLLayout)
        );
        inputURL = addURLView.findViewById(R.id.inputURL);
        builder = new AlertDialog.Builder(Home.this);
        galleryAccessIsNotGranted = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;

        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.quickAddImage).setOnClickListener(this);
        findViewById(R.id.quickAddURL).setOnClickListener(this);
    }

    private void updateView() {
        final int darkGrey = ContextCompat.getColor(this, R.color.primaryColour);
        final int lightGrey = ContextCompat.getColor(this, R.color.primaryColourLight);
        final int offWhite = ContextCompat.getColor(this, R.color.offWhite);
        final int black = ContextCompat.getColor(this, R.color.black);
        final int white = ContextCompat.getColor(this, R.color.white);
        final Drawable lightBlue = ContextCompat.getDrawable(this, R.drawable.add_note_button);
        final Drawable blue = ContextCompat.getDrawable(this, R.drawable.add_note_button_light);
        final Drawable light = ContextCompat.getDrawable(this, R.drawable.quick_note_light_background);
        final Drawable dark = ContextCompat.getDrawable(this, R.drawable.quick_note_background);

        if (settings.theme().equals(UserSettings.LIGHT_THEME)) {
            // Components are set to their specified light mode colours
            homeText.setTextColor(black);
            backBtn.setColorFilter(black);
            homeView.setBackgroundColor(white);
            settingsBtn.setColorFilter(lightGrey);
            createNoteBtn.setBackground(blue);
            searchLayout.setBackgroundColor(offWhite);
            searchInput.setHintTextColor(black);
            searchInput.setTextColor(black);
            searchIcon.setColorFilter(darkGrey);
            quickActionsLayout.setBackgroundColor(offWhite);
            quickTitleInput.setBackground(light);
            quickTitleInput.setTextColor(black);
            quickTitleInput.setHintTextColor(black);
            quickAddImageBtn.setColorFilter(lightGrey);
            quickAddURLBtn.setColorFilter(lightGrey);
            return;
        }

        // Components are reverted to their default colours
        homeText.setTextColor(white);
        backBtn.setColorFilter(white);
        homeView.setBackgroundColor(darkGrey);
        settingsBtn.setColorFilter(offWhite);
        createNoteBtn.setBackground(lightBlue);
        searchLayout.setBackgroundColor(lightGrey);
        searchInput.setHintTextColor(offWhite);
        searchInput.setTextColor(white);
        searchIcon.setColorFilter(offWhite);
        quickActionsLayout.setBackgroundColor(lightGrey);
        quickTitleInput.setBackground(dark);
        quickTitleInput.setTextColor(white);
        quickTitleInput.setHintTextColor(offWhite);
        quickAddImageBtn.setColorFilter(offWhite);
        quickAddURLBtn.setColorFilter(offWhite);
    }

    public void openCreateNote(View view) {
        Intent intent = new Intent(this, CreateNote.class); startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, Settings.class); startActivity(intent);
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

            @Override @SuppressLint("NotifyDataSetChanged")
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                switch (requestCode) {
                    case SHOW_NOTES: list.addAll(notes); adapter.notifyDataSetChanged(); break;
                    case ADD_NOTE: list.add(0, notes.get(0)); adapter.notifyItemInserted(0); break;

                    case UPDATE_NOTE: list.remove(noteClickedPosition);
                        if (noteIsDeleted) { adapter.notifyItemRemoved(noteClickedPosition); }
                        else {
                            Home.this.list.add(noteClickedPosition, notes.get(noteClickedPosition));
                            adapter.notifyItemChanged(noteClickedPosition);
                        } break;
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
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
                case UPDATE_NOTE:
                    if (data != null) {
                        getNotes(UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
                    } break;
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
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
            builder.setView(addURLView);
            addURLDialog = builder.create();

            if (addURLDialog.getWindow() != null) {
                addURLDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            addURLView.findViewById(R.id.confirmAddURL).setOnClickListener(this);
            addURLView.findViewById(R.id.cancelAddURL).setOnClickListener(this);
        }

        addURLDialog.show();
    }

    /**
     * TextWatcher Implementations. In this class I only need 'onTextChanged' and 'afterTextChanged' for
     * the search notes functionality. Notes can be searched by words/ letters located in any title,
     * subtitle and even content.
     */
    @Override
    public void onTextChanged(CharSequence cs, int i, int i1, int i2) { adapter.cancelTimer(); }
    @Override
    public void afterTextChanged(Editable s) {
        if (!list.isEmpty()) { adapter.searchNotes(s.toString()); }
    }
    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {} // Method not required

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

    @Override @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            // Simple 1-line implementations
            case R.id.backButton: onBackPressed(); break;
            case R.id.quickAddURL: quickAddURL(); break;
            case R.id.cancelAddURL: addURLDialog.dismiss(); break;
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            case R.id.confirmAddURL: inputURL.requestFocus();
                if (inputURL.getText().toString().trim().isEmpty()) {
                    Toast.makeText(Home.this,"Enter a URL", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                    Toast.makeText(Home.this,"Enter a valid URL", Toast.LENGTH_SHORT).show();
                } else {
                    addURLDialog.dismiss();
                    Intent noteWithURL = new Intent(getApplicationContext(), CreateNote.class);
                    noteWithURL.putExtra("isFromQuickActions", true);
                    noteWithURL.putExtra("quickActionType", "URL");
                    noteWithURL.putExtra("URL", inputURL.getText().toString());
                    startActivityForResult(noteWithURL, ADD_NOTE);
                } break;
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            case R.id.quickAddImage: if (galleryAccessIsNotGranted) {
                                        ActivityCompat.requestPermissions(
                                            Home.this,
                                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                            STORAGE_PERMISSION
                                        );
                                     } else { selectImage(); } break;
        }
    }
}