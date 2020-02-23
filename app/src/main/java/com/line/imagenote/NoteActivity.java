package com.line.imagenote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.line.imagenote.db.DBHelper;
import com.line.imagenote.models.Attachment;
import com.line.imagenote.models.Note;
import com.line.imagenote.models.adapter.ViewPagerAdapter;
import com.line.imagenote.models.listener.PhotoListener;
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


/**
 * 노트 상세보기/편집/삭제 할 수 있다.
 */
public class NoteActivity extends AppCompatActivity implements PhotoListener {
    private static final String TAG = "NoteActivity";
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_GET = 2;
    private static final int REQUEST_IMAGE_DELETE = 3;

    private DBHelper databaseHandler;

    private ArrayList<Attachment> photoList;

    ViewPager viewPager;
    ViewPagerAdapter adapter;
    WormDotsIndicator dotsIndicator;

    private EditText et_title, et_content;

    private String title, content;
    private long noteId;
    private boolean isUpdate;
    private boolean updatePhoto;

    private AlertDialog deleteDialog;
    private AlertDialog urlDialog;

    private String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        databaseHandler = new DBHelper(getApplicationContext());

        isUpdate = getIntent().getBooleanExtra("isUpdate", false);

        if (!isUpdate) {
            // 노트를 최초 생성하는 경우, noteId를 현재 시간으로 설정해준다.
            noteId = Calendar.getInstance().getTimeInMillis();
        } else {
            // 노트를 편집하는 경우, 기존의 noteId를 받아온다.
            noteId = getIntent().getLongExtra("noteId", 0);
        }

        Log.d(TAG, "onCreate: " + isUpdate + " " + noteId);

        initViews();
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);

        viewPager = findViewById(R.id.view_pager);
        dotsIndicator = findViewById(R.id.worm_dots_indicator);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isUpdate) {
            getNote();
        }
        getImage();

    }

    /**
     * 해당 noteId의 제목과 본문을 DB에서 가져온 다음, editText에 보여준다.
     */
    private void getNote() {
        Note noteItem = databaseHandler.getNoteById(noteId);
        title = noteItem.getTitle();
        content = noteItem.getContent();

        et_title.setText(title);
        et_content.setText(content);
    }

    /**
     * 해당 noteId에 첨부된 파일 중 이미지를 가져와 viewPager에 보여준다.
     */
    private void getImage() {
        photoList = new ArrayList<>();

        photoList = databaseHandler.getAttachments(noteId, "image");

//        adapter = new ViewPagerAdapter(this, photoList, noteId);

        adapter = new ViewPagerAdapter(this, photoList, this);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager(viewPager);
        adapter.notifyDataSetChanged();

        // 이미지가 없는 경우, viewPager와 indicator는 나타나지 않는다.
        if (photoList.isEmpty()) {
            viewPager.setVisibility(View.GONE);
            dotsIndicator.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            dotsIndicator.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onRestart() {
        super.onRestart();
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

            case R.id.item_camera:
                dispatchTakePictureIntent();
                return true;

            case R.id.item_gallery:
                openGallery();
                return true;

            case R.id.item_url:
                openURLDialog();
                return true;

            case R.id.btn_delete:
                openDeleteDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 카메라 메뉴를 선택했을 경우, 기기의 카메라 앱을 통해 사진을 촬영한 후 이미지 파일을 가져온다.
     * 참고: https://developer.android.com/training/camera/photobasics
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // 파일이 성공적으로 생성된 경우, onActivityResult 를 호출한다.
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * 카메라로 촬영한 이미지 파일을 생성한다.
     * @return File
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // 파일 이름을 만든다.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // 파일의 주소를 받아온다.
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     * 갤러리 메뉴를 선택했을 경우, 기기의 갤러리 앱을 통해 이미지 파일을 가져온다.
     */
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    /**
     * 이미지 주소 메뉴를 선택했을 경우, 주소를 입력할 수 있는 다이얼로그가 나타난다.
     */
    private void openURLDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_url, null, false);
        builder.setView(view);

        final Button ButtonSubmit = view.findViewById(R.id.button_dialog_submit);
        final Button ButtonCancel = view.findViewById(R.id.button_dialog_cancel);
        final EditText editText = view.findViewById(R.id.edit_url);

        urlDialog = builder.create();
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
                urlDialog.dismiss();
            }
        });

        urlDialog.show();
    }


    /**
     * 사용자가 입력한 이미지 주소가 올바른지 확인하는 AsyncTask 클래스이다.
     */
    private class CheckImage extends AsyncTask<String, Void, Void> {
        boolean isImage;
        String stringURL;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            urlDialog.dismiss();

            if (isImage) {
                // db에 이미지 주소를 저장한다.
                savePhoto(stringURL);
                onResume();
            } else {
                // 이미지 주소가 잘못된 경우, 에러 메시지를 띄워준다.
                Toast.makeText(NoteActivity.this, R.string.message_url_error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(String... voids) {
            stringURL = voids[0];

            try {
                URL url = new URL(stringURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.connect();

                // URL 주소의 헤더 항목에서 content-type이 이미지인지 확인한다.
                String contentType = httpURLConnection.getHeaderField("Content-Type");
                isImage = contentType.startsWith("image/");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * 메뉴바의 휴지통 버튼을 눌렀을 경우, 노트 삭제 확인 다이얼로그가 나타난다.
     */
    private void openDeleteDialog() {
        deleteDialog = new AlertDialog.Builder(NoteActivity.this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.confirm_delete_text))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // DB에서 노트를 삭제해 준다.
                        databaseHandler.deleteNote(noteId);

                        deleteDialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDialog.dismiss();
                    }
                })
                .setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_black_24dp))
                .show();
    }

    /**
     * 이미지를 클릭했을 때, 이미지 원본파일을 보고 삭제할 수 있는 PhotoActivity로 이동한다.
     * @param position
     */
    @Override
    public void onPhotoClicked(int position) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("noteId", noteId);
        intent.putExtra("photoId", photoList.get(position).getPhotoId());
        intent.putExtra("imagePath", photoList.get(position).getUri());
        startActivityForResult(intent, REQUEST_IMAGE_DELETE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    savePhoto(currentPhotoPath);
                    Log.d(TAG, "onActivityResult: " + currentPhotoPath);
                    break;

                case REQUEST_IMAGE_GET:
                    try {
                        Uri uri = data.getData();
                        String url = getRealPath(uri);
                        savePhoto(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case REQUEST_IMAGE_DELETE:
                    updatePhoto = data.getBooleanExtra("updatePhoto", false);
                    Log.d(TAG, "onActivityResult: "+ updatePhoto);
                    break;
            }
        }
    }


    /**
     * 갤러리에서 가져온 이미지 데이터의 uri로부터 파일경로를 얻는다.
     * @ param Uri 이미지주소
     * @ return String 파일경로
     * 참고 https://stackoverflow.com/questions/13209494/how-to-get-the-full-file-path-from-uri
     */
    private String getRealPath(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

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


    @Override
    public void onPause() {
        saveNote();

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * onPause가 호출되었을 때, 수정사항이 있는 경우 노트를 저장해 준다.
     */
    private void saveNote() {
        // 현재 제목과 내용을 가져온다.
        String newTitle = et_title.getText().toString().trim().replace("/", " ");
        String newContent = et_content.getText().toString().trim();

        // 제목, 내용이 모두 비었을 경우, 저장하지 않는다.
        if (TextUtils.isEmpty(newTitle) && TextUtils.isEmpty(newContent) && !updatePhoto) {
            return;
        }

        // 변경사항이 없으면 저장하지 않는다.
        if (newTitle.equals(title) && newContent.equals(content) && !updatePhoto) {
            return;
        }

        // 제목이 없는 경우 제목을 설정해준다.
        if (TextUtils.isEmpty(newTitle)) {
            newTitle = "빈 제목";
        }

        Note note;
        if (isUpdate) {
            note = new Note(noteId, Calendar.getInstance().getTimeInMillis(), newTitle, newContent);
        } else {
            note = new Note(noteId, noteId, newTitle, newContent);
        }
        databaseHandler.insertNote(note);
    }

    /**
     * 이미지 파일을 저장한다.
     */
    private void savePhoto(String imagePath) {
        updatePhoto =true;

        // 새로 노트를 생성해서 이미지 파일을 추가한 경우, 노트를 먼저 저장해 주어야 한다.
        if(!isUpdate){
            saveNote();
        }
        databaseHandler.insertAttachment(noteId, imagePath, "image");
    }

}
