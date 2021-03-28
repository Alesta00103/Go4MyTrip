package com.aleksandra.go4mytrip;

public class NoteModel {
    String IdNote;
    String NoteTitle;
    String TextNote;
    String DateTime;
    String NoteColor;
    String Link;
    String Image;

    public NoteModel() {

    }

    public NoteModel(String idNote, String noteTitle, String textNote, String dateTime, String noteColor, String link, String image) {
        IdNote = idNote;
        NoteTitle = noteTitle;
        TextNote = textNote;
        DateTime = dateTime;
        NoteColor = noteColor;
        Link = link;
        Image = image;

    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getNoteColor() {
        return NoteColor;
    }

    public void setNoteColor(String noteColor) {
        NoteColor = noteColor;
    }

    public String getIdNote() {
        return IdNote;
    }

    public void setIdNote(String idNote) {
        IdNote = idNote;
    }

    public String getNoteTitle() {
        return NoteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        NoteTitle = noteTitle;
    }

    public String getTextNote() {
        return TextNote;
    }

    public void setTextNote(String textNote) {
        TextNote = textNote;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }
}
