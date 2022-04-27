package adapters;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ultranote.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import entities.Note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;

    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.note_widget_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
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

    static class NoteViewHolder extends RecyclerView.ViewHolder
    {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout widgetLayout;
        RoundedImageView noteImage;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            widgetLayout = itemView.findViewById(R.id.widgetLayout);
            noteImage = itemView.findViewById(R.id.noteImage);
        }

        void setNote(Note note) {
            textTitle.setText(note.getTitle());
            if (note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(note.getSubtitle());
            }

            textDateTime.setText(note.getDateTime());

            GradientDrawable gd = (GradientDrawable) widgetLayout.getBackground();
            if (note.getColour() != null) {
                gd.setColor(Color.parseColor(note.getColour()));
            } else {
                gd.setColor(Color.parseColor("#333333"));
            }

            if (note.getImagePath() != null) {
                noteImage.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                noteImage.setVisibility(View.VISIBLE);
            } else {
                noteImage.setVisibility(View.GONE);
            }
        }
    }

}
