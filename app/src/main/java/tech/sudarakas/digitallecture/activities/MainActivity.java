package tech.sudarakas.digitallecture.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import tech.sudarakas.digitallecture.R;
import tech.sudarakas.digitallecture.adapter.NoteAdapter;
import tech.sudarakas.digitallecture.database.NotesDatabase;
import tech.sudarakas.digitallecture.entities.Note;
import tech.sudarakas.digitallecture.listeners.NoteListener;

public class MainActivity extends AppCompatActivity implements NoteListener {

    public  static final int REQUEST_CODE_ADD_NOTE = 1; //new note
    public  static final int REQUEST_CODE_UPDATE_NOTE = 2;  //updated
    public  static final int REQUEST_CODE_SHOW_NOTES = 3; //all notes

    private RecyclerView mainRecyclerView;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;
    private int noteClickedPosition = -1;

    private ImageView imageAddNewNoteMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageAddNewNoteMain = (ImageView) findViewById(R.id.imageAddNewNoteMain);
        imageAddNewNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), NewNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });
        mainRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList, this);
        mainRecyclerView.setAdapter(noteAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTES);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), NewNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode){
        class GetNoteTask extends AsyncTask<Void, Void, List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase
                        .getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                //Log.d("Notes", notes.toString());
                if(requestCode == REQUEST_CODE_SHOW_NOTES){
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0, notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                    mainRecyclerView.smoothScrollToPosition(0);
                }else if(requestCode == REQUEST_CODE_UPDATE_NOTE){
                    noteList.remove(noteClickedPosition);
                    noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                    noteAdapter.notifyItemChanged(noteClickedPosition);
                }
            }
        }
        new GetNoteTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE);
        }else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode ==RESULT_OK){
            if(data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE);
            }
        }
    }
}
