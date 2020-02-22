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

import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";

    ImageView img_photo;
    private DBHelper databaseHandler;
    private int photoId;
    private long noteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        databaseHandler = new DBHelper(getApplicationContext());

        // 툴바에 backButton을 나타내주고, 제목은 없애준다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // 이미지를 넣어준다.
        img_photo = findViewById(R.id.img_photo);
        Glide.with(this)
                .load(getIntent().getStringExtra("uri"))
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

                        databaseHandler.deleteAttachment(getIntent().getIntExtra("photoId", 0));
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
