package com.line.imagenote.models.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.line.imagenote.R;
import com.line.imagenote.models.NoteItem;

import java.util.ArrayList;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesViewHolder> {
    private static final String TAG = "NotesListAdapter";

    private Context mContext;
    private ArrayList<NoteItem> mNotesList;
    private NotesListListener listener;

    public interface NotesListListener {
        void onNoteClicked(int position);
    }


    public NotesListAdapter(Context context, ArrayList<NoteItem> notesList, NotesListListener listener) {
        this.mContext = context;
        this.mNotesList = notesList;
        this.listener = listener;
    }


    public class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mNote;
        private ImageView mImage;


        public NotesViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.txt_title);
            mNote = itemView.findViewById(R.id.txt_note);
            mImage = itemView.findViewById(R.id.img_thumbnail);

        }

    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_note, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, final int position) {

        holder.mTitle.setText(mNotesList.get(position).getTitle());
        holder.mNote.setText(mNotesList.get(position).getContent());

        ArrayList photoList = mNotesList.get(position).getPhotoList();
        if(photoList.isEmpty()){
            holder.mImage.setVisibility(View.GONE);
        }
        else{
            String imageUrl = mNotesList.get(position).getPhotoList().get(0);
            Log.d(TAG, "onBindViewHolder: "+ imageUrl);

            Glide.with(mContext)
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.mImage);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != mNotesList ? mNotesList.size() : 0);
    }


}