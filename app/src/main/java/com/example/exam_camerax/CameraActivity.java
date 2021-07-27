package com.example.exam_camerax;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.os.Bundle;
import android.os.Environment;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.BiMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity implements CameraXConfig.Provider {
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private TextView textView;
    private ImageCapture imageCapture;
    private Button capture_btn;

    ImageView ImageView_face, imageLC, imageRC;
    ConstraintLayout RelativeLayout_face;


    @Nullable private VisionImageProcessor imageProcessor;

    private String TAG = "CameraActivity";

    //얼굴 인식 관련
    FaceDetectorOptions realTimeOpts;
    ImageAnalysis imageAnalysis;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        textView = findViewById(R.id.orientation);
        capture_btn = findViewById(R.id.capture_btn);
        RelativeLayout_face = findViewById(R.id.RelativeLayout_face);
        //얼굴 인식 관련




        //CameraProview 사용 가능 여부 확인
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
        //CameraProview 사용 가능 여부 확인


        //뷰 캡쳐하기
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().toString();
                File file = new File(path, "capture-" + System.currentTimeMillis() + ".jpeg");
                Log.d(TAG+ " filePath", file.getAbsolutePath());


                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(ContextCompat.getMainExecutor(CameraActivity.this), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        Image image_get = image.getImage();
                        Log.d(TAG+ " onCaptureSuccess", String.valueOf(image_get));
                        Bitmap capture_bitmap = toBitmap(image_get);
                        image_Analysis(capture_bitmap);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                });

//                imageCapture.takePicture(outputFileOptions,ContextCompat.getMainExecutor(CameraActivity.this),
//                        new ImageCapture.OnImageSavedCallback() {
//                            @Override
//                            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
//                                // insert your code here.
//                                Log.d(TAG+ " onImageSavedgetSavedUri", String.valueOf(outputFileResults.getSavedUri()));
//                                Log.d(TAG+ " onImageSavedgettoString", String.valueOf(outputFileResults.toString()));
//                                Toast.makeText(getApplicationContext(), "찍혔어요",Toast.LENGTH_LONG).show();
//                            }
//                            @Override
//                            public void onError(ImageCaptureException error) {
//                                // insert your code here.
//                                Log.d(" Camerror", String.valueOf(error));
//                                Toast.makeText(getApplicationContext(), "에러났어요",Toast.LENGTH_LONG).show();
//
//                            }
//                        }
//                );
            }
        });
    }

    private void image_Analysis(Bitmap new_bitmap){
        int orientation = Integer.parseInt(textView.getText().toString());

        InputImage image = InputImage.fromBitmap(new_bitmap,orientation); //여기에서 이미지 분석이 들어감
                // Real-time contour detection
                realTimeOpts =
                        new FaceDetectorOptions.Builder()
                                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                                .build();



                FaceDetector detector = FaceDetection.getClient(realTimeOpts);

                Task<List<Face>> result =
                        detector.process(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<Face>>() {
                                            @Override
                                            public void onSuccess(List<Face> faces) {
                                                // Task completed successfully
                                                // ...
                                                Log.d(TAG+ " onSuccess", "들어옴");
                                                Log.d(TAG+ " onSuccessfaces", String.valueOf(faces));
                                                Point p = new Point();
                                                Display display = getWindowManager().getDefaultDisplay();
                                                display.getSize(p);

                                                for (Face face : faces) {
                                                    Toast.makeText(getApplicationContext(), "발견",Toast.LENGTH_LONG).show();


                                                    Rect bounds = face.getBoundingBox();
                                                    float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                                    float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                                    Log.d(TAG+ " rotY", String.valueOf(rotY));
                                                    Log.d(TAG+ " roZ", String.valueOf(rotZ));


                                                    //                                                        얼굴인식 되는 코드
                                                    imageLC = new ImageView(CameraActivity.this);
                                                    imageLC.setImageResource(R.drawable.beard);


                                                    imageLC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
                                                    RelativeLayout_face.addView(imageLC);

                                                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                                    // nose available):

                                                    FaceLandmark leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK);
                                                    PointF leftEarPos = leftCheek.getPosition();
                                                    Log.d(TAG+ " leftEarPos", leftEarPos.toString());
                                                    Log.d(TAG+ " leftEarPos", String.valueOf(leftEarPos.describeContents()));
                                                    float lcx = (float) 193.20035;
                                                    float lcy = (float) 383.85687;
                                                    imageLC.setX(p.x * lcx / 1280 + 70);
                                                    imageLC.setY(p.y * lcy /720 - 0);
                                                    //얼굴인식 되는 코드



//                                                    FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
//                                                    if (leftEar != null) {
//                                                        PointF leftEarPos = leftEar.getPosition();
//                                                        Log.d(TAG+ " onSuccessleftEarPos", String.valueOf(leftEarPos));
//                                                    }

                                                }
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                Log.d(TAG+ " onFailure", "들어옴");
                                                Log.d(TAG+ " onFailure", String.valueOf(e));

                                            }
                                        });



//                image.close();

    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
         imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int rotationDegrees = image.getImageInfo().getRotationDegrees();
                Log.d(TAG + " rotationDegrees", String.valueOf(rotationDegrees));

                Log.d(" analyze", "들어놈");
                Image mediaImage = image.getImage();
                Log.d("mediaImage", String.valueOf(mediaImage));
                InputImage image_getMedia =
                        InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());


                // Real-time contour detection
                realTimeOpts =
                        new FaceDetectorOptions.Builder()
                                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                                .build();



                FaceDetector detector = FaceDetection.getClient(realTimeOpts);

                Task<List<Face>> result =
                        detector.process(image_getMedia)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<Face>>() {
                                            @Override
                                            public void onSuccess(List<Face> faces) {
                                                // Task completed successfully
                                                // ...
                                                Log.d(TAG+ " onSuccess", "들어옴");
                                                Log.d(TAG+ " onSuccessfaces", String.valueOf(faces));
                                                Point p = new Point();
                                                Display display = getWindowManager().getDefaultDisplay();
                                                display.getSize(p);

                                                for (Face face : faces) {
                                                    Toast.makeText(getApplicationContext(), "발견",Toast.LENGTH_LONG).show();


                                                    Rect bounds = face.getBoundingBox();
                                                    float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                                    float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                                    Log.d(TAG+ " rotY", String.valueOf(rotY));
                                                    Log.d(TAG+ " roZ", String.valueOf(rotZ));


                                                    //                                                        얼굴인식 되는 코드
                                                    imageLC = new ImageView(CameraActivity.this);
                                                    imageLC.setImageResource(R.drawable.beard);


                                                    imageLC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
                                                    RelativeLayout_face.addView(imageLC);

                                                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                                    // nose available):

                                                    FaceLandmark leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK);
                                                    PointF leftEarPos = leftCheek.getPosition();
                                                    Log.d(TAG+ " leftEarPos", leftEarPos.toString());
                                                    Log.d(TAG+ " leftEarPos", String.valueOf(leftEarPos.describeContents()));
                                                    float lcx = (float) 193.20035;
                                                    float lcy = (float) 383.85687;
                                                    imageLC.setX(p.x * lcx / 1280 + 70);
                                                    imageLC.setY(p.y * lcy /720 - 0);
                                                    //얼굴인식 되는 코드



//                                                    FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
//                                                    if (leftEar != null) {
//                                                        PointF leftEarPos = leftEar.getPosition();
//                                                        Log.d(TAG+ " onSuccessleftEarPos", String.valueOf(leftEarPos));
//                                                    }

                                                }
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                Log.d(TAG+ " onFailure", "들어옴");
                                                Log.d(TAG+ " onFailure", String.valueOf(e));

                                            }
                                        });



//                image.close();
            }
        });

        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                textView.setText(Integer.toString(orientation));
            }
        };
        orientationEventListener.enable();

        //카메라 선택 및 수명 주기와 사용 사례 결합
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        //이미지 캡쳐 관련
        imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();

        cameraProvider.bindToLifecycle((LifecycleOwner)this,
                cameraSelector,
                imageCapture,
                imageAnalysis,
                preview);


        //카메라 선택 및 수명 주기와 사용 사례 결합

    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    private Bitmap toBitmap(Image image) {
        Log.d(TAG+ " toBitmap", "들어옴");
        Log.d(TAG+ " toBitmap", String.valueOf(image));

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}