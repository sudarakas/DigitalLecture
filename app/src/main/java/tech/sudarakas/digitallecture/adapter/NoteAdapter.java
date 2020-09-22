package tech.sudarakas.digitallecture.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.sudarakas.digitallecture.R;
import tech.sudarakas.digitallecture.entities.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewPlaceholder>{
    private List<Note> notes;

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
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
    public void onBindViewHolder(@NonNull NoteViewPlaceholder holder, int position) {
        holder.setNote(notes.get(position));
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

        public NoteViewPlaceholder(@NonNull View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            textSubtitle = (TextView) itemView.findViewById(R.id.textSubtitle);
            textDateTime = (TextView) itemView.findViewById(R.id.textDateTime);
        }

        void setNote(Note note){
            textTitle.setText(note.getTitle());
            if(note.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else{
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(note.getDateTime());
        }
    }
}
