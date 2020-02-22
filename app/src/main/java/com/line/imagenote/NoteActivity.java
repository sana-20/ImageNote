package com.line.imagenote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.line.imagenote.db.DBHelper;
import com.line.imagenote.models.Attachment;
import com.line.imagenote.models.Note;
import com.line.imagenote.models.adapter.ViewPagerAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity {
    private static final String TAG = "NoteActivity";

    private DBHelper databaseHandler;
    private EditText et_title, et_content;

    private boolean updatePhoto;

    // 원래 메모 내용
    private String title, content;
    private long noteId;
    private boolean isUpdate;

    private AlertDialog dialog;

    private AlertDialog alertDialog;

    // 이미지
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    WormDotsIndicator dotsIndicator;

    private ArrayList<Attachment> photoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.e(TAG, "onCreate: 1");

        Log.d(TAG, "onCreate: ");

        // sqlite의 id값을 받아온다.
        isUpdate = getIntent().getBooleanExtra("isUpdate", false);
        Log.d(TAG, "onCreate: " + noteId + isUpdate);

        if (!isUpdate) {
            noteId = Calendar.getInstance().getTimeInMillis();
        } else {
            noteId = getIntent().getLongExtra("noteId", 0);
        }

        databaseHandler = new DBHelper(getApplicationContext());

        initViews();


    }

    private void initViews() {
        // 툴바에 backButton을 나타내주고, 제목은 없애준다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 메모의 제목과 내용
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);

        // 메모의 이미지
        viewPager = findViewById(R.id.view_pager);

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        Log.e(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        Log.e(TAG, "onResume: ");

        photoList = new ArrayList<>();

        //TODO: onResume에 넣어야 하나??
        if (isUpdate) {
            Log.e(TAG, "onCreate: 2");
            getNote();
        }

        //TODO: onResume에 넣어야 함!!!
         dotsIndicator =  findViewById(R.id.worm_dots_indicator);

        adapter = new ViewPagerAdapter(this, photoList, noteId);
        viewPager.setAdapter(adapter);

        dotsIndicator.setViewPager(viewPager);

        adapter.notifyDataSetChanged();


        if (photoList.isEmpty()) {
            viewPager.setVisibility(View.GONE);
            dotsIndicator.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            dotsIndicator.setVisibility(View.VISIBLE);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        Log.e(TAG, "onStop: ");
    }


    // 이미 생성된 메모를 보거나 편집하려는 경우 SQLite에서 메모를 가져온다.
    private void getNote() {
        Log.e(TAG, "getNote: ");
        Log.d(TAG, "getNote: ");
        Note noteItem = databaseHandler.getNoteById(noteId);
        title = noteItem.getTitle();
        content = noteItem.getContent();

        Log.d(TAG, "initViews: " + photoList);

        photoList = databaseHandler.getAttachments(noteId, "image");
        Log.d(TAG, "getNote: " + photoList);
        Log.e(TAG, "getNote: " + photoList);

        et_title.setText(title);
        et_content.setText(content);

    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
        Log.d(TAG, "onRestart: ");
//        content = et_content.getText().toString().trim();
//        if (getCurrentFocus() != null)
//            getCurrentFocus().clearFocus();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause: ");
        Log.d(TAG, "onPause: ");
        if (!isChangingConfigurations()) {
            saveNote();
        }

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed: ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.btn_photo:
                // 권한 확인한다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 권한 상태 확인한다.
                    if (!hasPermissions(PERMISSIONS)) {
                        // 권한 설정이 안 되어 있다면 사용자에게 요청한다.
                        requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }
                return true;

            case R.id.item_camera:
                dispatchTakePictureIntent();
                return true;

            case R.id.item_gallery:
                if (!checkStoragePermission()) {
                    // 저장공간 권한 허가 안 됨. 요청해야 함.
                    requestStoargePermission();
                } else {
                    // 권한 설정 됨
                    Log.d(TAG, "onClick: 갤러리 사진 클릭");
                    openGallery();

                }
                return true;

            case R.id.item_url:
                openURLDialog();
                return true;

            case R.id.btn_delete:
                dialog = new AlertDialog.Builder(NoteActivity.this)
                        .setTitle(getString(R.string.confirm_delete))
                        .setMessage(getString(R.string.confirm_delete_text))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                databaseHandler.deleteNote(noteId);

                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_black_24dp))
                        .show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openURLDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_url, null, false);
        builder.setView(view);

        final Button ButtonSubmit = view.findViewById(R.id.button_dialog_submit);
        final Button ButtonCancel = view.findViewById(R.id.button_dialog_cancel);
        final EditText editText = view.findViewById(R.id.edit_url);

        alertDialog = builder.create();
        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ButtonSubmit.setEnabled(false);

                String stringURL = editText.getText().toString().trim();

                CheckImage task = new CheckImage();
                task.execute(stringURL);

            }
        });


        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

//    @Override
//    public void onPhotoClicked(int position) {
//        Intent intent = new Intent(this, PhotoActivity.class);
//
//        intent.putExtra("noteId", noteId);
//        intent.putExtra("position", position);
//        intent.putExtra("photoList", photoList);
//        startActivityForResult(intent, REQUEST_IMAGE_DELETE);
//
//    }

    private class CheckImage extends AsyncTask<String, Void, Void> {
        boolean isImage;
        String stringURL;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.dismiss();

            if (isImage) {
                databaseHandler.insertAttachment(noteId, stringURL, "image");
                onResume();

            } else {
                Toast.makeText(NoteActivity.this, R.string.message_url_error, Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected Void doInBackground(String... voids) {
            stringURL = voids[0];

            Log.d(TAG, "doInBackground: " + stringURL);
            try {
                URL url = new URL(stringURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.connect();

                String contentType = httpURLConnection.getHeaderField("Content-Type");
                Log.d(TAG, "doInBackground: " + contentType); //image/jepg
                isImage = contentType.startsWith("image/");
                Log.d(TAG, "doInBackground: " + isImage); //true

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }


    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_GET = 2;
    static final int REQUEST_IMAGE_DELETE = 3;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "dispatchTakePictureIntent: " + ex.getMessage());
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);

                String url = photoURI.toString();
                databaseHandler.insertAttachment(noteId, url, "image");

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }


    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: PhotoPath" + currentPhotoPath);
        return image;
    }


    // 이미지 작업결과를 보여준다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    Log.e(TAG, "onActivityResult: ");
                    break;

                case REQUEST_IMAGE_GET:
                    try {
                        // 사진 데이터의 Uri를 가져온다.
                        Uri uri = data.getData();
                        String url = getRealPath(uri);
                        Log.d(TAG, "onActivityResult: " + uri);
                        Log.e(TAG, "onActivityResult: " + url);
                        databaseHandler.insertAttachment(noteId, url, "image");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case REQUEST_IMAGE_DELETE:
                    int deleteNo = data.getIntExtra("deleteNo", 0);
                    photoList.remove(deleteNo);
                    break;

            }
        }
    }

    /**
     * 이미지 데이터의 uri로부터 파일경로를 얻는 메소드이다.
     *
     * @ param Uri 이미지주소
     * @ return String 파일경로
     * 참고 https://stackoverflow.com/questions/13209494/how-to-get-the-full-file-path-from-uri
     * api 레벨마다 경로 얻는 방법이 다름
     */
    private String getRealPath(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private void saveNote() {
        Log.e(TAG, "saveNote: ");
        Log.d(TAG, "saveNote: 노트 저장");
        // 현재 제목과 내용을 가져온다.
        String newTitle = et_title.getText().toString().trim().replace("/", " ");
        String newContent = et_content.getText().toString().trim();

        // 제목과 내용 사진이 모두 비었을 경우, 저장하지 않는다.
        if (TextUtils.isEmpty(newTitle) && TextUtils.isEmpty(newContent) && photoList.isEmpty()) {
            Log.d(TAG, "saveNote: 케이스1 저장 안함");
            return;
        }

        // 변경사항이 없으면 저장하지 않는다.
        if (newTitle.equals(title) && newContent.equals(content) && !updatePhoto) {
            Log.d(TAG, "saveNote: ");
            return;
        }

        if (TextUtils.isEmpty(newTitle)) {
            newTitle = "빈 제목";
        }

        Note note;
        if (isUpdate) {
            note = new Note(noteId, Calendar.getInstance().getTimeInMillis(), newTitle, newContent);
        } else {
            note = new Note(noteId, noteId, newTitle, newContent);
        }
        databaseHandler.insertNote(note, isUpdate);

    }


    /**
     * 카메라, 갤러리 권한 관련 코드
     */
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    private static final int STOARGE_REQUEST_CODE = 400;
    String storagePermission[];

    private void requestStoargePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STOARGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    // handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STOARGE_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean writeStoargeAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (writeStoargeAccepted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "권한 거부됨", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }


}
