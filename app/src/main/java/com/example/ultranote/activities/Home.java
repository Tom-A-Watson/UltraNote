package com.example.ultranote.activities;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import listeners.NotesListener;
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;
import database.NotesDatabase;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.ultranote.R;
import java.util.ArrayList;
import adapters.NotesAdapter;
import entities.Note;
import settings.UserSettings;
import utilities.Utilities;

/**
 * This class contains all functionality regarding user-interaction on the the Home page. Some of
 * this entails sending specific information to the CreateNote class via Intents.
 */
public class Home extends AppCompatActivity implements NotesListener, View.OnClickListener,
                                                       TextWatcher, TextView.OnEditorActionListener {
    private int noteClickedPosition = -1;
    private boolean galleryAccessIsNotGranted;
    private UserSettings settings;
    private Utilities u;
    private View homeView, urlView;
    private EditText searchInput, qTitleInput, urlInput;
    private TextView homeText;
    private ImageView backBtn, createNoteBtn, settingsBtn, searchIcon, qAddImgBtn, qAddURLBtn;
    private RecyclerView notesRecyclerView;
    private LinearLayout searchBar, qActions;
    private List<Note> list;
    private NotesAdapter adapter;
    private AlertDialog.Builder builder;

    // Request codes
    public static final int ADD_NOTE = 1;
    public static final int UPDATE_NOTE = 2;
    public static final int SHOW_NOTES = 3;
    public static final int SELECT_IMAGE = 4;
    public static final int STORAGE_PERMISSION = 5;
    public static final int VERIFY_URL = 6;

    // States
    private static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    private static final int VERTICAL = StaggeredGridLayoutManager.VERTICAL;

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
        Utilities.initHomeDrawables(this);
        Utilities.initGlobalColours(this);
        Utilities.urlDialog = null;

        settings = (UserSettings) getApplication();
        u = new Utilities(getApplication());
        list = new ArrayList<>();
        adapter = new NotesAdapter(list, this);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        homeText = findViewById(R.id.homeText);
        backBtn = findViewById(R.id.backButton);
        createNoteBtn = findViewById(R.id.createNoteBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        searchBar = findViewById(R.id.searchLayout);
        searchInput = findViewById(R.id.searchNotesInput);
        searchInput.addTextChangedListener(this);
        searchIcon = findViewById(R.id.searchIcon);
        qActions = findViewById(R.id.quickActionsLayout);
        qAddImgBtn = findViewById(R.id.quickAddImage);
        qAddURLBtn = findViewById(R.id.qAddURL);
        homeView = findViewById(R.id.homeView);
        qTitleInput = findViewById(R.id.quickTitleInput);
        qTitleInput.setOnEditorActionListener(this);
        urlView = LayoutInflater.from(this).inflate(
                R.layout.add_url_layout,
                findViewById(R.id.addURLLayout)
        );
        urlInput = urlView.findViewById(R.id.inputURL);
        builder = new AlertDialog.Builder(Home.this);
        galleryAccessIsNotGranted = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != GRANTED;

        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.quickAddImage).setOnClickListener(this);
        findViewById(R.id.qAddURL).setOnClickListener(this);
    }

    private void updateView() {
        if (settings.theme().equals(UserSettings.LIGHT_THEME)) {// Set components to light mode colours
            homeText.setTextColor(Utilities.black);    qTitleInput.setTextColor(Utilities.black);
            searchInput.setTextColor(Utilities.black);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            backBtn.setColorFilter(Utilities.black);        settingsBtn.setColorFilter(Utilities.lightGrey);
            qAddImgBtn.setColorFilter(Utilities.lightGrey); qAddURLBtn.setColorFilter(Utilities.lightGrey);
            searchIcon.setColorFilter(Utilities.darkGrey);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            homeView.setBackgroundColor(Utilities.white);
            searchBar.setBackgroundColor(Utilities.offWhite);
            qActions.setBackgroundColor(Utilities.offWhite);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            createNoteBtn.setBackground(Utilities.blue); qTitleInput.setBackground(Utilities.lightInput);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            searchInput.setHintTextColor(Utilities.black); qTitleInput.setHintTextColor(Utilities.black);

            return;
        }
        // Revert components to their default colours
        homeText.setTextColor(Utilities.white);    qTitleInput.setTextColor(Utilities.white);
        searchInput.setTextColor(Utilities.white);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        backBtn.setColorFilter(Utilities.white);       settingsBtn.setColorFilter(Utilities.offWhite);
        searchIcon.setColorFilter(Utilities.offWhite); qAddURLBtn.setColorFilter(Utilities.offWhite);
        qAddImgBtn.setColorFilter(Utilities.offWhite);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        homeView.setBackgroundColor(Utilities.darkGrey);
        searchBar.setBackgroundColor(Utilities.lightGrey);
        qActions.setBackgroundColor(Utilities.lightGrey);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        createNoteBtn.setBackground(Utilities.lightBlue); qTitleInput.setBackground(Utilities.darkInput);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        searchInput.setHintTextColor(Utilities.offWhite); qTitleInput.setHintTextColor(Utilities.offWhite);
    }

    public void openCreateNote(View view) {
        Intent intent = new Intent(this, CreateNote.class); startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, Settings.class); startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(reqCode, perms, results);

        if (reqCode == STORAGE_PERMISSION && results.length > 0) {
            if (results[0] == GRANTED) { u.selectImage(this, SELECT_IMAGE); }
            else { Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show(); }
        }
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
                //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
                    case UPDATE_NOTE: list.remove(noteClickedPosition);
                        if (noteIsDeleted) { adapter.notifyItemRemoved(noteClickedPosition); }
                        else {
                            list.add(noteClickedPosition, notes.get(noteClickedPosition));
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
        assert data != null;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_NOTE: getNotes(ADD_NOTE, false); break;
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
                case UPDATE_NOTE:
                    getNotes(UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false)); break;
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
                case SELECT_IMAGE:
                    Uri uri = data.getData();
                    if (uri != null) {
                        try { u.buildNoteWithImage(uri, getApplicationContext(), this, ADD_NOTE); }
                        catch (Exception e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } break;
            }
        }
    }

    /**
     * TextWatcher Implementations. Only 'onTextChanged' and 'afterTextChanged' are required for the search
     * feature. Notes can be searched by words/ letters located in any title, subtitle and even content :)
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
            noteWithTitle.putExtra("quickTitle", qTitleInput.getText().toString().trim());
            startActivityForResult(noteWithTitle, ADD_NOTE);
        }

        return true;
    }

    @Override @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            // Simple 1-line implementations
            case R.id.backButton: onBackPressed(); break;
            case R.id.qAddURL: Utilities.urlDialog = Utilities.showDialog(urlView, builder, this); break;
            case R.id.cancelAddURL: Utilities.urlDialog.dismiss(); break;
            case R.id.confirmAddURL: Utilities.validateURL(this, urlInput, Utilities.urlDialog); break;
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            case R.id.quickAddImage: if (galleryAccessIsNotGranted) {
                                        ActivityCompat.requestPermissions(
                                            Home.this,
                                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                            STORAGE_PERMISSION
                                        );
                                     } else { u.selectImage(this, SELECT_IMAGE); } break;
        }
    }
}