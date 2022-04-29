package listeners;

import entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
