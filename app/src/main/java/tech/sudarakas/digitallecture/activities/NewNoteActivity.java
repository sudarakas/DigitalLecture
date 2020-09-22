package tech.sudarakas.digitallecture.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tech.sudarakas.digitallecture.R;
import tech.sudarakas.digitallecture.database.NotesDatabase;
import tech.sudarakas.digitallecture.entities.Note;

public class NewNoteActivity extends AppCompatActivity {

        private ImageView imageBack,imageDone;
        private EditText noteTitleInput,noteSubtitleInput,noteInput;
        private TextView dateTimeText;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_note);

            imageBack = (ImageView) findViewById(R.id.imageBack);
            noteTitleInput = (EditText) findViewById(R.id.noteTitleInput);
            noteSubtitleInput = (EditText) findViewById(R.id.noteSubtitleInput);
            noteInput = (EditText) findViewById(R.id.noteInput);
            dateTimeText = (TextView) findViewById(R.id.dateTimeText);
            imageDone = (ImageView) findViewById(R.id.imageDone);

            //Back button on the canvas
            imageBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            //Display the date and time
            dateTimeText.setText(
                    new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                            .format(new Date())
            );

            //Call save note function
            imageDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveNote();
                }
            });
        }

    //Save new note function
    private void saveNote() {
            if(noteTitleInput.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Note title can not be empty!", Toast.LENGTH_LONG).show();
                return;
            }else if(noteSubtitleInput.getText().toString().trim().isEmpty() &&
            noteInput.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Note can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            final Note note = new Note();
            note.setTitle(noteTitleInput.getText().toString());
            note.setSubtitle(noteSubtitleInput.getText().toString());
            note.setNoteContent(noteInput.getText().toString());
            note.setDateTime(dateTimeText.getText().toString());

            @SuppressLint("StaticFieldLeak")
            class SaveNoteTask extends AsyncTask<Void, Void, Void>{
                @Override
                protected Void doInBackground(Void... voids) {
                    NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            new SaveNoteTask().execute();

    }
}
