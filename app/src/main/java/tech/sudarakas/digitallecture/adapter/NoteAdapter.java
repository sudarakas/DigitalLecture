package tech.sudarakas.digitallecture.adapter;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tech.sudarakas.digitallecture.R;
import tech.sudarakas.digitallecture.entities.Note;
import tech.sudarakas.digitallecture.listeners.NoteListener;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewPlaceholder>{

    private List<Note> notes;
    private NoteListener noteListener;
    private Timer timer;
    private List<Note> noteSource;

    public NoteAdapter(List<Note> notes,NoteListener noteListener) {

        this.notes = notes;
        this.noteListener = noteListener;
        noteSource = notes;
    }

    @NonNull
    @Override
    public NoteViewPlaceholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewPlaceholder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.note_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewPlaceholder holder, final int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteListener.onNoteClicked(notes.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewPlaceholder extends RecyclerView.ViewHolder{
        private TextView textTitle,textSubtitle,textDateTime;
        private LinearLayout layoutNote;
        private RoundedImageView imageNote;

        public NoteViewPlaceholder(@NonNull View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            textSubtitle = (TextView) itemView.findViewById(R.id.textSubtitle);
            textDateTime = (TextView) itemView.findViewById(R.id.textDateTime);
            layoutNote = (LinearLayout) itemView.findViewById(R.id.layoutNote);
            imageNote = (RoundedImageView) itemView.findViewById(R.id.imageNote);
        }

        void setNote(Note note){
            textTitle.setText(note.getTitle());
            if(note.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else{
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(note.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#303030"));
            }

            if(note.getImageSrc() != null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImageSrc()));
                imageNote.setVisibility(View.VISIBLE);
            }else{
                imageNote.setVisibility(View.GONE);
            }
        }
    }

    public void searchNotes(final String keyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (keyword.trim().isEmpty()) {
                    notes = noteSource;
                } else {
                    ArrayList<Note> tempNotes = new ArrayList<>();
                    for (Note note : noteSource) {
                        if (note.getTitle().toLowerCase().contains(keyword.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(keyword.toLowerCase())
                                || note.getNoteContent().toLowerCase().contains(keyword.toLowerCase())) {
                            tempNotes.add(note);
                        }
                    }
                    notes = tempNotes;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }
}
