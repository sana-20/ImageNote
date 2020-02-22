package com.line.imagenote;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.line.imagenote.db.DBHelper;
import com.line.imagenote.models.Note;
import com.line.imagenote.models.adapter.NotesListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class NotesListActivity extends AppCompatActivity implements NotesListAdapter.NotesListListener {
    private static final String TAG = "NotesListActivity";

    private TextView txt_empty;
    private RecyclerView recycler_notes;
    private FloatingActionButton fab;

    private NotesListAdapter notesListAdapter;
    private ArrayList<Note> notesList;
    DBHelper databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        initViews();

    }

    private void initViews() {
        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesListActivity.this, NoteActivity.class);
                intent.putExtra("isUpdate", false);
                startActivity(intent);
            }
        });

        txt_empty = findViewById(R.id.txt_empty);

        setRecyclerView();
    }


    @Override
    protected void onResume() {
        super.onResume();

        showNotesList();
        showEmptyListMessage();
    }

    private void showNotesList() {
        databaseHandler = new DBHelper(this);
        notesList = databaseHandler.getAllNotes(); // SQLite에서 데이터를 가져온다.
        notesListAdapter = new NotesListAdapter(this, notesList,this);
        recycler_notes.setAdapter(notesListAdapter);
    }

    private void showEmptyListMessage() {
        if (notesListAdapter.getItemCount() == 0) {
            txt_empty.setVisibility(View.VISIBLE);
        } else if (txt_empty.getVisibility() == View.VISIBLE) {
            txt_empty.setVisibility(View.GONE);
        }
    }


    private void setRecyclerView() {
        recycler_notes = findViewById(R.id.recycler_notes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotesListActivity.this);
        recycler_notes.setLayoutManager(linearLayoutManager);

        recycler_notes.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


    }

    @Override
    public void onNoteClicked(int position) {
        long noteId = notesList.get(position).getTimeCreated();
        Log.d(TAG, "onNoteClicked: " + noteId );
        Intent intent = new Intent(NotesListActivity.this, NoteActivity.class);
        intent.putExtra("noteId", noteId);
        intent.putExtra("isUpdate", true);
        startActivity(intent);
    }
}
