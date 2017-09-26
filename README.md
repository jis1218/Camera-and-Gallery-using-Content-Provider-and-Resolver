앱에서 카메라 실행시키는 과정
MediaStore라는 클래스 - Provider 중에 하나
The Media provider contains meta data for all available media on both internal and external storage devices. 
안드로이드 싸이트에 가보면 MediaStore.Audio, MediaStore.Files, MediaStore.Images, MediaStore.Video 등등 여러가지 클래스를 제공한다.
ACTION_IMAGE_CAPTURE : Standard Intent action that can be sent to have the camera application capture an image and return it.

1. intent에 아래 String 값을 넣어준다.
```java
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
```

2. 임시파일 저장용 디렉토리 생성 및 임시파일 생성
```java
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
```
3. 아래와 같이 intent를 날려준다.
```java
fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQ_CAMERA);
```
4. onActivityResult로 받으면서 마무리
```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (resultCode == RESULT_OK) {
    switch (requestCode) {
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
```
```
fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
```
BuildConfig.APPLICATION_ID는 내가 만든 앱 이름이고 파라미터로 authorities가 추가되어야 하는 칸인데 .provider가 붙은 걸 보니  manifest에 등록되어 있는 <provider>의 android:authorities를 추가해주는 것 같다.

intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)에

MediaStore.EXTRA_OUTPUT - The name of the Intent-extra used to indicate a content resolver Uri to be used to store the requested image or video.

putExtra의 두번째 파라미터로 fileUri를 넘겨줬는데 Uri 클래스는 Parcelable interface를 implementing 하므로 넣어줄 수 있다.
```java
intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
```

갤러리에서 선택한 사진을 인텐트로 보내주는 방법, ACTION_PICK을 이용하면 된다.
```java
//갤러리로 가는 버튼
public void onGallery(View view) {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, REQ_GALLERY);
}
```

ACTION_PICK - Activity Action: Pick an item from the data, returning what was selected.
Input: getData() is URI containing a directory of data (vnd.android.cursor.dir/* ) from which to pick an item.
Output: The URI of the item that was picked. (안드로이드 홈피)

FileProvider - FileProvider is a special subclass of ContentProvider that facilitates secure sharing of files associated with an app by creating a content:// Uri for a file instead of a file:/// Uri.
A content URI allows you to grant read and write access using temporary access permissions. When you create an Intent containing a content URI, in order to send the content URI to a client app, you can also call Intent.setFlags() to add permissions. These permissions are available to the client app for as long as the stack for a receiving Activity is active. For an Intent going to a Service, the permissions are available as long as the Service is running.
In comparison, to control access to a file:/// Uri you have to modify the file system permissions of the underlying file. The permissions you provide become available to any app, and remain in effect until you change them. This level of access is fundamentally insecure.
