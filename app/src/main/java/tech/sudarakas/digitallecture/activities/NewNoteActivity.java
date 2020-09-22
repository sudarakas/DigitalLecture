package tech.sudarakas.digitallecture.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

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
        private View viewSubtitleIndicator;

        private String selectedNoteColor;

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
            viewSubtitleIndicator = (View) findViewById(R.id.viewSubtitleIndicator);

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

            selectedNoteColor = "#303030";
            initOptionsMenu();
            setViewSubtitleIndicator();
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
            note.setColor(selectedNoteColor);

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

    private void initOptionsMenu(){
            final LinearLayout layoutOptionsMenu = findViewById(R.id.layoutOptionsMenu);
            final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutOptionsMenu);
            layoutOptionsMenu.findViewById(R.id.textOptionsMenu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });

            final ImageView imageColor1 = layoutOptionsMenu.findViewById(R.id.imageColor1);
            final ImageView imageColor2 = layoutOptionsMenu.findViewById(R.id.imageColor2);
            final ImageView imageColor3 = layoutOptionsMenu.findViewById(R.id.imageColor3);
            final ImageView imageColor4 = layoutOptionsMenu.findViewById(R.id.imageColor4);
            final ImageView imageColor5 = layoutOptionsMenu.findViewById(R.id.imageColor5);

            //Shift the color
            layoutOptionsMenu.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedNoteColor = "#303030";
                    imageColor1.setImageResource(R.drawable.ic_done);
                    imageColor2.setImageResource(0);
                    imageColor3.setImageResource(0);
                    imageColor4.setImageResource(0);
                    imageColor5.setImageResource(0);
                    setViewSubtitleIndicator();
                }
            });

        layoutOptionsMenu.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FFCF44";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setViewSubtitleIndicator();
            }
        });

        layoutOptionsMenu.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#B15DFF";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setViewSubtitleIndicator();
            }
        });

        layoutOptionsMenu.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FF6859";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setViewSubtitleIndicator();
            }
        });

        layoutOptionsMenu.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#72DEFF";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setViewSubtitleIndicator();
            }
        });
    }

    private void setViewSubtitleIndicator(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }
}
