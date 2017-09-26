package com.example.galleryproject;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends BaseActivity {

    ImageView imageView;

    @Override
    public void init() {
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
    }
    // 저장된 파일의 경로를 가지는 컨텐츠 URI
    Uri fileUri = null;
    // 카메라로 가는 버튼
    public void onCamera(View view) {
        //카메라 앱 띄워서 결과 이미지 저장하기
        // 1. Intent 만들기
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 2. 호환성 체크
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            // 실제 파일이 저장되는 파일 객체
            try {
                File photoFile = createFile();
                //갤러리에서 나오지 않을 때
                refreshMedia(photoFile);
                fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQ_CAMERA);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 실제 파일이 저장되는 곳에 권한이 부여되어 있어야 한다.
            // 롤리팝부터는 File Provider를 선언해줘야한다. 이것은 Manifest에서 할 수 있다.
        }else{
            startActivityForResult(intent, REQ_CAMERA);
        }
    }

    private void refreshMedia(File photoFile) {
        MediaScannerConnection.scanFile(this,
                new String[] {photoFile.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener(){
                    public void onScanCompleted(String path, Uri uri){

                    }
                });
    }

    //이미지를 저장하기 위해 쓰기 권한이 있는 빈 파일을 생성해두는 함수
    private File createFile() throws IOException { //호출한 쪽으로 IOException 전이됨
        // 임시 파일명
        String tempFileName = "Temp_" + System.currentTimeMillis();
        // 임시파일 저장용 디렉토리 생성
        File tempDir = new File(Environment.getExternalStorageDirectory() + "/CameraN/");
        // 생성 체크
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }
        //실제 임시 파일을 생성
        File tempFile = File.createTempFile(tempFileName, ".jpg", tempDir);

        tempFile.deleteOnExit();

        return tempFile;
    }


    private static final int REQ_GALLERY = 333;
    private static final int REQ_CAMERA = 222;

    //갤러리로 가는 버튼
    public void onGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri imageUri;
            switch (requestCode) {
                case REQ_GALLERY:
                    if (data != null) {
                        imageUri = data.getData();
                        imageView.setImageURI(imageUri);
                    }
                    break;

                case REQ_CAMERA:
                    if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                        imageUri = data.getData();
                    }else{
                        imageUri = fileUri;
                    }
                    imageView.setImageURI(imageUri);
                    break;
            }
        }
    }
}
