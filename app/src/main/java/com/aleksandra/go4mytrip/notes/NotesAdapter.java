package com.aleksandra.go4mytrip.notes;

import android.content.Context;
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

import com.aleksandra.go4mytrip.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    private Context context;
    private List<NoteModel> mData;
    private NotesListener notesListener;

    public NotesAdapter(Context context, List<NoteModel> mData, NotesListener notesListener) {
        this.context = context;
        this.mData = mData;
        this.notesListener = notesListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.item_note, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.setNote(mData.get(position));

        holder.layoutColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesListener.onNoteClicked(mData.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView textDateTime;
        TextView textNote;
        TextView linkNote;
        RoundedImageView image;
        LinearLayout layoutColor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textTitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textNote = itemView.findViewById(R.id.textNote);
            layoutColor = itemView.findViewById(R.id.item_note_layout);
            linkNote = itemView.findViewById(R.id.linkNote);
            image = itemView.findViewById(R.id.imageNote);

        }

        void setNote(NoteModel note) {
            title.setText(note.getNoteTitle());
            textDateTime.setText(note.getDateTime());
            textNote.setText(note.getTextNote());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutColor.getBackground();
            if (note.getNoteColor() != null) {
                gradientDrawable.setColor(Color.parseColor(note.getNoteColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if (note.getLink().trim().isEmpty()) {
                linkNote.setVisibility(View.GONE);

            } else {
                linkNote.setVisibility(View.VISIBLE);
                linkNote.setText(note.getLink());
            }
            if (note.getImage() != null) {
                image.setImageBitmap(BitmapFactory.decodeFile(note.getImage()));
                image.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.GONE);
            }


        }
    }
}
