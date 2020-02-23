package com.line.imagenote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.line.imagenote.db.DBHelper;

/**
 * 노트에 첨부된 이미지 파일을 원본으로 보고, 삭제할 수 있다.
 */
public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";

    private ImageView img_photo;
    private DBHelper databaseHandler;

    private int photoId;
    private long noteId;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // NoteActivity에서 이미지 관련 데이터를 넘겨받아 변수에 넣어준다.
        photoId = getIntent().getIntExtra("photoId", 0);
        noteId = getIntent().getLongExtra("noteId", 0);
        imagePath = getIntent().getStringExtra("imagePath");

        // 이미지를 넣어준다.
        img_photo = findViewById(R.id.img_photo);
        Glide.with(this)
                .load(imagePath)
                .into(img_photo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.btn_delete:
                openDialog();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openDialog() {
        AlertDialog dialog = new AlertDialog.Builder(PhotoActivity.this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.confirm_delete_photo))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHandler = new DBHelper(getApplicationContext());

                        databaseHandler.deleteAttachment(photoId);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updatePhoto", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();

                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_black_24dp))
                .show();
    }
}
