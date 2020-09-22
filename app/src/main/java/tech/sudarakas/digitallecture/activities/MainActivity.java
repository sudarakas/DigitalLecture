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

public class MainActivity extends AppCompatActivity {

    public  static final int REQUEST_CODE_ADD_NOTE = 1;

    private RecyclerView mainRecyclerView;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;

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
        noteAdapter = new NoteAdapter(noteList);
        mainRecyclerView.setAdapter(noteAdapter);

        getNotes();
    }

    private void getNotes(){
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
                if(noteList.size() == 0){
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }else {
                    noteList.add(0,notes.get(0));
                    noteAdapter.notifyDataSetChanged();
                }

                mainRecyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNoteTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes();
        }
    }
}
