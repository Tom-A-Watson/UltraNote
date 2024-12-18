package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.ArrayList;

@Entity (tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo (name = "title")
    private String title;

    @ColumnInfo (name = "date_time")
    private String dateTime;

    @ColumnInfo (name = "subtitle")
    private String subtitle;

    @ColumnInfo (name = "note_text")
    private String noteText;

    @ColumnInfo (name = "image_path")
    private String imagePath;

    @ColumnInfo (name = "colour")
    private String colour;

    @ColumnInfo (name = "web_link")
    private String webLink;

//    @ColumnInfo (name = "checklist")
//    private ArrayList<String> checklistText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

//    public ArrayList<String> getChecklistText() {
//        return checklistText;
//    }
//
//    public void setChecklistText(ArrayList<String> checklistText) {
//        this.checklistText = checklistText;
//    }

    @NonNull
    @Override
    public String toString() {
        return title + " : " + dateTime;
    }
}
