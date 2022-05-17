package adapters;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ultranote.R;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import entities.Note;
import listeners.NotesListener;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> notesSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
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
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setNote(notes.get(position));
        holder.widgetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position), position);
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

    public void searchNotes(final String searchInput) {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (searchInput.trim().isEmpty()) {
                    notes = notesSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();

                    for (Note note : notesSource) {
                        if (note.getTitle().toLowerCase().contains(searchInput.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchInput.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchInput.toLowerCase())) {

                            temp.add(note);
                        }
                    }

                    notes = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
