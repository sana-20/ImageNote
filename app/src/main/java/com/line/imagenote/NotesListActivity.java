package com.line.imagenote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.line.imagenote.db.DBHelper;
import com.line.imagenote.models.Note;
import com.line.imagenote.models.adapter.NotesListAdapter;
import com.line.imagenote.models.listener.NotesListListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 어플이 시작할 때 가장 먼저 나타나며, 저장된 노트 리스트를 보여준다.
 */

public class NotesListActivity extends AppCompatActivity implements NotesListListener {
    private static final String TAG = "NotesListActivity";

    DBHelper databaseHandler;

    private TextView txt_empty;
    private RecyclerView recycler_notes;
    private FloatingActionButton fab;

    private NotesListAdapter notesListAdapter;
    private ArrayList<Note> notesList;

    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        initViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }


    private void initViews() {
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


    /**
     * 카메라, 외부 저장소 접근 권한을 확인한다.
     */
    private boolean hasPermissions(String[] permissions) {
        int result;
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        showNotesList();
        showEmptyListMessage();
    }

    /**
     * DB에서 저장된 노트를 최신순으로 불러와 보여준다.
     */
    private void showNotesList() {
        databaseHandler = new DBHelper(this);
        notesList = databaseHandler.getAllNotes();
        notesListAdapter = new NotesListAdapter(this, notesList,this);
        recycler_notes.setAdapter(notesListAdapter);
    }

    /**
     * 저장된 노트가 하나도 없을 경우, 알림 텍스트 메시지가 나타난다.
     */
    private void showEmptyListMessage() {
        if (notesListAdapter.getItemCount() == 0) {
            txt_empty.setVisibility(View.VISIBLE);
        } else if (txt_empty.getVisibility() == View.VISIBLE) {
            txt_empty.setVisibility(View.GONE);
        }
    }

    /**
     * 각각의 노트를 클릭했을 때, 해당 노트의 상세 보기 화면으로 이동한다.
     * noteId와 isUpdate(편집여부)를 인텐트로 넘겨준다.
     * @param position
     */
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
