package com.line.imagenote.models.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.line.imagenote.R;
import com.line.imagenote.db.DBHelper;
import com.line.imagenote.models.Attachment;
import com.line.imagenote.models.Note;
import com.line.imagenote.models.NoteItem;

import java.util.ArrayList;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesViewHolder> {
    private static final String TAG = "NotesListAdapter";

    private Context mContext;
    private ArrayList<Note> mNotesList;
    private NotesListListener listener;
    private DBHelper databaseHandler;

    public interface NotesListListener {
        void onNoteClicked(int position);
    }


    public NotesListAdapter(Context context, ArrayList<Note> notesList, NotesListListener listener) {
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

        Log.d(TAG, "onBindViewHolder: " + mNotesList.get(position).getTimeCreated());

        databaseHandler = new DBHelper(mContext);
        String imageUri = databaseHandler.getThumbnail(mNotesList.get(position).getTimeCreated());
        Log.d(TAG, "onBindViewHolder: " + imageUri);

        if(!imageUri.equals("")){
            Log.d(TAG, "onBindViewHolder: 보여라");
            Glide.with(mContext)
                    .load(imageUri)
                    .centerCrop()
                    .into(holder.mImage);
            holder.mImage.setVisibility(View.VISIBLE);
        }
        else{
            Log.d(TAG, "onBindViewHolder: 안보인다");
            holder.mImage.setVisibility(View.GONE);
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