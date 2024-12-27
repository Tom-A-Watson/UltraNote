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
    private final NotesListener notesListener;
    private Timer timer;
    private final List<Note> notesSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    @NonNull @Override
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

    public void searchNotes(final String searchInput) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchInput.trim().isEmpty()) {
                    notes = notesSource;
                } else {
                    ArrayList<Note> searchResult = new ArrayList<>();

                    for (Note note : notesSource) {
                        if (note.getTitle().toLowerCase().contains(searchInput.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchInput.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchInput.toLowerCase())) {

                            searchResult.add(note);
                        }
                    }

                    notes = searchResult;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override @SuppressLint("NotifyDataSetChanged")
                    public void run() { notifyDataSetChanged(); }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) { timer.cancel(); }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder
    {
        int black = Color.parseColor("#000000");
        String[] lightNoteColours = { "#FFE719", "#8BC34A", "#4CAF50", "#00BCD4", "#FF9800" };
        TextView title, subtitle, dateTime;
        LinearLayout widgetLayout;
        GradientDrawable gd;
        RoundedImageView widgetImage;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            subtitle = itemView.findViewById(R.id.textSubtitle);
            dateTime = itemView.findViewById(R.id.textDateTime);
            widgetLayout = itemView.findViewById(R.id.widgetLayout);
            widgetImage = itemView.findViewById(R.id.widgetImage);
            gd = (GradientDrawable) widgetLayout.getBackground();
        }

        void setNote(Note note) {
            title.setText(note.getTitle());
            dateTime.setText(note.getDateTime());

            if (note.getSubtitle().trim().isEmpty()) {
                subtitle.setVisibility(View.GONE);
            } else {
                subtitle.setText(note.getSubtitle());
            }

            if (note.getColour() != null) {
                gd.setColor(Color.parseColor(note.getColour()));

                for (String lightColour : lightNoteColours) {
                    if (note.getColour().equals(lightColour)) {
                        title.setTextColor(black);
                        subtitle.setTextColor(black);
                        dateTime.setTextColor(black);
                    }
                }
            } else {
                gd.setColor(Color.parseColor("#333333"));
            }

            if (note.getImagePath() != null) {
                widgetImage.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                widgetImage.setVisibility(View.VISIBLE);
            } else {
                widgetImage.setVisibility(View.GONE);
            }
        }
    }
}
