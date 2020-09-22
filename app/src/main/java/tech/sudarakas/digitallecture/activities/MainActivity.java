package tech.sudarakas.digitallecture.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import tech.sudarakas.digitallecture.R;
import tech.sudarakas.digitallecture.database.NotesDatabase;
import tech.sudarakas.digitallecture.entities.Note;

public class MainActivity extends AppCompatActivity {

    public  static final int REQUEST_CODE_ADD_NOTE = 1;

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
                Log.d("Notes", notes.toString());
            }
        }
        new GetNoteTask().execute();
    }
}
