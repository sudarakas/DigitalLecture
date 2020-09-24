package tech.sudarakas.digitallecture.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import tech.sudarakas.digitallecture.R;
import tech.sudarakas.digitallecture.database.NotesDatabase;
import tech.sudarakas.digitallecture.entities.Note;

public class NewNoteActivity extends AppCompatActivity {

        private ImageView imageBack,imageDone,imageNote;
        private EditText noteTitleInput,noteSubtitleInput,noteInput;
        private TextView dateTimeText, webURLText;
        private View viewSubtitleIndicator;
        private LinearLayout layoutWebURL;

        private String selectedNoteColor;
        private String selectedImagePath;

        private AlertDialog dialogAddURL;

        private Note existingNote;

        private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
        private static final int REQUEST_CODE_SELECT_IMAGE = 2;

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
            imageNote = (ImageView) findViewById(R.id.imageNote);
            webURLText = (TextView) findViewById(R.id.textWEBURL);
            layoutWebURL = (LinearLayout) findViewById(R.id.layoutWEBURL);

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
            selectedImagePath = "";

            if(getIntent().getBooleanExtra("isViewOrUpdate", false)){
                existingNote = (Note) getIntent().getSerializableExtra("note");
                setViewUpdateNote();
            }

            findViewById(R.id.imageURLDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webURLText.setText(null);
                    layoutWebURL.setVisibility(View.GONE);
                }
            });

            findViewById(R.id.imageImageDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageNote.setImageBitmap(null);
                    imageNote.setVisibility(View.GONE);
                    findViewById(R.id.imageImageDelete).setVisibility(View.GONE);
                    selectedImagePath="";
                }
            });

            initOptionsMenu();
            setViewSubtitleIndicator();
        }

        private void setViewUpdateNote(){
            noteTitleInput.setText(existingNote.getTitle());
            noteSubtitleInput.setText(existingNote.getSubtitle());
            noteInput.setText(existingNote.getNoteContent());
            dateTimeText.setText(existingNote.getDateTime());

            if(existingNote.getImageSrc() != null && !existingNote.getImageSrc().trim().isEmpty()){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(existingNote.getImageSrc()));
                imageNote.setVisibility(View.VISIBLE);
                findViewById(R.id.imageImageDelete).setVisibility(View.VISIBLE);
                selectedImagePath = existingNote.getImageSrc();
            }

            if(existingNote.getLink() != null && !existingNote.getLink().trim().isEmpty()){
                webURLText.setText(existingNote.getLink());
                layoutWebURL.setVisibility(View.VISIBLE);
            }

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
            note.setImageSrc(selectedImagePath);

            if(layoutWebURL.getVisibility() == View.VISIBLE){
                note.setLink(webURLText.getText().toString());
            }

            if(existingNote != null){
                note.setId(existingNote.getId());
            }

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

        if(existingNote != null && existingNote.getColor() != null && !existingNote.getColor().trim().isEmpty()){
            switch (existingNote.getColor()){
                case "#FFCF44":
                    layoutOptionsMenu.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#B15DFF":
                    layoutOptionsMenu.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#FF6859":
                    layoutOptionsMenu.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#72DEFF":
                    layoutOptionsMenu.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

        //Add Image to Note
        layoutOptionsMenu.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            NewNoteActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else{
                    chooseImage();
                }
            }
        });

//        Add URL to Note
        layoutOptionsMenu.findViewById(R.id.layoutAddURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();
            }
        });
    }

    //Change the color of subtitle indicator
    private void setViewSubtitleIndicator(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    //Select the image from the file
    private void chooseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                chooseImage();
            }else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri selectImageUri = data.getData();
                if(selectImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageImageDelete).setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectImageUri);
                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri){
            String filePath;
        Cursor cursor = getContentResolver().query(contentUri, null, null,null,null);
        if(cursor == null){
            filePath = contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }

    private void showAddURLDialog() {
            if(dialogAddURL == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(NewNoteActivity.this);
                View view = LayoutInflater.from(this).inflate(
                        R.layout.layout_add_url,
                        (ViewGroup) findViewById(R.id.layoutAddURLDialog)
                        );
                builder.setView(view);

                dialogAddURL = builder.create();
                if(dialogAddURL.getWindow() != null){
                    dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                final EditText inputURL = view.findViewById(R.id.inputURL);
                inputURL.requestFocus();

                view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(inputURL.getText().toString().trim().isEmpty()){
                            Toast.makeText(NewNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                        }else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                            Toast.makeText(NewNoteActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
                        }else {
                            webURLText.setText(inputURL.getText().toString());
                            layoutWebURL.setVisibility(View.VISIBLE);
                            dialogAddURL.dismiss();
                        }
                    }
                });

                view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogAddURL.dismiss();
                    }
                });
            }
            dialogAddURL.show();
    }
}
