package com.example.ultranote.activities;

import static utilities.Utilities.ENTER;
import static utilities.Utilities.GRANTED;
import static utilities.Utilities.SHORT;
import static utilities.Utilities.VERTICAL;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import listeners.NotesListener;
import java.util.List;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import database.NotesDatabase;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
    private UserSettings settings;
    private Utilities u;
    private Context appContext;
    private View homeView, urlV;
    private EditText searchInput, qTitleInput, input;
    private TextView homeText;
    private ImageView backBtn, createNoteBtn, settingsBtn, searchIcon, qAddImgBtn, qAddURLBtn;
    private RecyclerView notesRecyclerView;
    private LinearLayout searchBar, qActions;
    private List<Note> list;
    private NotesAdapter a;
    private int noteIndex = -1;

    // Request codes
    public static final int ADD_NOTE = 1;
    public static final int UPDATE_NOTE = 2;
    public static final int SHOW_NOTES = 3;
    public static final int SELECT_IMAGE = 4;
    public static final int STORAGE_PERM = 5;
    public static final int ACTION_DONE = 6;

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
        Utilities.initHomeListeners(this, this, this);
        Utilities.initHomeDrawables(this);
        Utilities.initGlobalColours(this);
        Utilities.urlDialog = null;

        settings = (UserSettings) getApplication();
        u = new Utilities(getApplication());
        appContext = getApplicationContext();
        list = new ArrayList<>();
        a = new NotesAdapter(list, this);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setAdapter(a);
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
        qAddImgBtn = findViewById(R.id.qAddImg);
        qAddURLBtn = findViewById(R.id.addURL);
        homeView = findViewById(R.id.homeView);
        qTitleInput = findViewById(R.id.quickTitleInput);
        qTitleInput.setOnEditorActionListener(this);
        urlV = LayoutInflater.from(this).inflate(R.layout.addurl, findViewById(R.id.urlLayout));
        input = urlV.findViewById(R.id.inputURL);
    }

    private void updateView() {
        if (u.appThemeIsLightTheme(settings)) {           // Set components to light mode colours \\
        //-|Text Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -||
            homeText.setTextColor(Utilities.black);    qTitleInput.setTextColor(Utilities.black);
            searchInput.setTextColor(Utilities.black);
        //-|Icon Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -||
            backBtn.setColorFilter(Utilities.black);        settingsBtn.setColorFilter(Utilities.lightGrey);
            qAddImgBtn.setColorFilter(Utilities.lightGrey); qAddURLBtn.setColorFilter(Utilities.lightGrey);
            searchIcon.setColorFilter(Utilities.darkGrey);
        //-|Component Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ||
            homeView.setBackgroundColor(Utilities.white);
            searchBar.setBackgroundColor(Utilities.offWhite);
            qActions.setBackgroundColor(Utilities.offWhite);
        //-|Component Backgrounds|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ||
            createNoteBtn.setBackground(Utilities.blue); qTitleInput.setBackground(Utilities.lightInput);
        //-|EditText Placeholder Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - -//
            searchInput.setHintTextColor(Utilities.black); qTitleInput.setHintTextColor(Utilities.black);

            return;
        }                                           // Revert components to their default colours \\
    //-|Text Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -||
        homeText.setTextColor(Utilities.white);    qTitleInput.setTextColor(Utilities.white);
        searchInput.setTextColor(Utilities.white);
    //-|Icon Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -||
        backBtn.setColorFilter(Utilities.white);       settingsBtn.setColorFilter(Utilities.offWhite);
        searchIcon.setColorFilter(Utilities.offWhite); qAddURLBtn.setColorFilter(Utilities.offWhite);
        qAddImgBtn.setColorFilter(Utilities.offWhite);
    //-|Component Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ||
        homeView.setBackgroundColor(Utilities.darkGrey);
        searchBar.setBackgroundColor(Utilities.lightGrey);
        qActions.setBackgroundColor(Utilities.lightGrey);
    //-|Component Backgrounds|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ||
        createNoteBtn.setBackground(Utilities.lightBlue); qTitleInput.setBackground(Utilities.darkInput);
    //-|EditText Placeholder Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -//
        searchInput.setHintTextColor(Utilities.offWhite); qTitleInput.setHintTextColor(Utilities.offWhite);
    }

    //TODO: Attempt to implement global method for opening activities in Utilities, using switch view.getId()
    public void openCreateNote(View view) {
        Intent intent = new Intent(this, CreateNote.class); startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, Settings.class); startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(reqCode, perms, results);

        if (reqCode == STORAGE_PERM && results.length > 0) {
            if (results[0] == GRANTED) { u.selectImage(this, SELECT_IMAGE); }
            else { Toast.makeText(this, getString(R.string.denied), SHORT).show(); }
        }
    }

    /**                           -=-= NotesListener Implementation =-=-
     * When a note is clicked, its object representation and index are obtained. The object is sent to CreateNote
     * for data-mapping. The index is passed to the adapter so that it knows which note widget to update.
     * @param note - The note that was clicked
     * @param clickedIndex - The note's index in the collection
     */
    @Override
    public void onNoteClicked(Note note, int clickedIndex) {
        noteIndex = clickedIndex;
        Intent intent = new Intent(appContext, CreateNote.class);
        intent.putExtra("viewExisting", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, UPDATE_NOTE);
    }

    private void getNotes(final int requestCode, final boolean deleted) {
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>>
        {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getDatabase(appContext).noteDao().getAllNotes();
            }

            @Override @SuppressLint("NotifyDataSetChanged")
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                switch (requestCode) {
                    case SHOW_NOTES: list.addAll(notes); a.notifyDataSetChanged(); break;
                    case ADD_NOTE: list.add(0, notes.get(0)); a.notifyItemInserted(0); break;
                    case UPDATE_NOTE: u.notifyAdapterOfUpdateOrDelete(a, list, notes, deleted, noteIndex); break;
                }
            }
        }

        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        boolean deleteBool = data.getBooleanExtra("isDeleted", false);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_NOTE: getNotes(ADD_NOTE, false); break;
                case UPDATE_NOTE: getNotes(UPDATE_NOTE, deleteBool); break;
                case SELECT_IMAGE: u.attemptBuildNoteWithImg(data, this, this); break;
            }
        }
    }

    /**                           -=-= TextWatcher Implementations =-=-
     * Only 'onTextChanged' and 'afterTextChanged' are required for the search feature. Notes can be
     * searched by words/ letters located in any title, subtitle and even content :)
     */
    @Override
    public void onTextChanged(CharSequence cs, int i, int i1, int i2) { a.cancelTimer(); }
    @Override
    public void afterTextChanged(Editable s) {
        if (!list.isEmpty()) { a.searchNotes(s.toString()); }
    }
    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {} // Method not required

    @Override
    public boolean onEditorAction(TextView textView, int a, KeyEvent evt) {
        String trimmedTitle = qTitleInput.getText().toString().trim();

        if (!trimmedTitle.isEmpty() && (a == ACTION_DONE || evt.getKeyCode() == ENTER)) {
            Intent noteWithTitle = new Intent(appContext, CreateNote.class);
            noteWithTitle.putExtra("isFromQuickActions", true);
            noteWithTitle.putExtra("quickActionType", "title");
            noteWithTitle.putExtra("quickTitle", trimmedTitle);
            startActivityForResult(noteWithTitle, ADD_NOTE); return true;
        }

        Toast.makeText(this, getString(R.string.qtitle_error), SHORT).show(); return false;
    }

    @Override @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: onBackPressed(); break;
            case R.id.addURL: Utilities.urlDialog = Utilities.showDialog(this, this, this, urlV); break;
            case R.id.cancelAddURL: Utilities.urlDialog.dismiss(); break;
            case R.id.confirmAddURL: Utilities.validateURL(this, input); break;
            case R.id.qAddImg: u.reqPermOrSelectImg(this, appContext, STORAGE_PERM, SELECT_IMAGE); break;
        }
    }
}