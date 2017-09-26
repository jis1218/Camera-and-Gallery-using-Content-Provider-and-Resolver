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

ACTION_PICK - Activity Action: Pick an item from the data, returning what was selected.
Input: getData() is URI containing a directory of data (vnd.android.cursor.dir/* ) from which to pick an item.
Output: The URI of the item that was picked. (안드로이드 홈피)
