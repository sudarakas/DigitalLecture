package tech.sudarakas.digitallecture.listeners;

import tech.sudarakas.digitallecture.entities.Note;

public interface NoteListener {
    void onNoteClicked(Note note, int position);
}
