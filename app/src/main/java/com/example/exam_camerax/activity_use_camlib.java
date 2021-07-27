package com.example.exam_camerax;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.filter.Filter;
import com.otaliastudios.cameraview.filter.Filters;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;
import com.otaliastudios.cameraview.size.SizeSelector;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class activity_use_camlib extends AppCompatActivity {

    Button btn_cam_location;
    ImageView btn_capture;
    ImageView image_capture;
    CameraView camera;
    private String TAG = "profile";

    //얼굴 인식 관련
    FaceDetectorOptions realTimeOpts;
    ImageAnalysis imageAnalysis;

    ImageView ImageView_face, imageLC, imageRC;
    RelativeLayout RelativeLayout_face;
    InputImage image;
    Point p;
    Display display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_camlib);
        camera = findViewById(R.id.camera);
        btn_cam_location = findViewById(R.id.btn_cam_location);
        image_capture = findViewById(R.id.image_capture);
        btn_capture = findViewById(R.id.btn_capture);

        RelativeLayout_face = findViewById(R.id.RelativeLayout_face);
        RelativeLayout_face.removeViewInLayout(imageLC);
        RelativeLayout_face.removeViewInLayout(imageRC);

         p = new Point();
         display = getWindowManager().getDefaultDisplay();
        display.getSize(p);


        imageLC = new ImageView(activity_use_camlib.this);
        imageLC.setImageResource(R.drawable.beard);
        imageLC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));


        RelativeLayout_face.addView(imageLC);


        camera.setLifecycleOwner(this);
        camera.setMode(Mode.PICTURE); // for pictures


        Timer timer = new Timer();
        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                // 반복실행할 구문
                camera.takePictureSnapshot();
            }
        };
        timer.schedule(TT, 0, 1500); //Timer 실행


        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                // A Picture was taken!
                // Picture was taken!
                // If planning to show a Bitmap, we will take care of
                // EXIF rotation and background threading for you...


                Log.d(TAG+ " onPictureTakenResult", String.valueOf(result));
                result.getData();

                Bitmap new_bitmap = CameraUtils.decodeBitmap(result.getData());
                Log.d("new_bitmaphight", String.valueOf(new_bitmap.getHeight()));
                Log.d("new_bitmagetWidth", String.valueOf(new_bitmap.getWidth()));
                Log.d("cameraWgetWidth", String.valueOf(camera.getWidth()));
                Log.d("cameraWidth", String.valueOf(camera.getWidth()));



//                image_capture.setImageBitmap(new_bitmap);



                realTimeOpts =
                        new FaceDetectorOptions.Builder()
                                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                                .build();


                FaceDetector detector = FaceDetection.getClient(realTimeOpts);

                image = InputImage.fromBitmap(new_bitmap,0);


                Task<List<Face>> result2 =
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



                                                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                                    // nose available):

                                                    FaceLandmark leftCheek = face.getLandmark(FaceLandmark.LEFT_EYE);
                                                    PointF leftEarPos = leftCheek.getPosition();
                                                    Log.d(TAG+ " leftEarPos", leftEarPos.toString());
                                                    Log.d(TAG+ " leftEarPos.x", String.valueOf(leftEarPos.x));
                                                    Log.d(TAG+ " leftEarPos.y", String.valueOf(leftEarPos.y));

//                                                    float lcx = leftEarPos.x;
//                                                    float lcy = leftEarPos.y;

                                                    //얼굴인식 되는 코드


                                                    // If contour detection was enabled:
//                                                    List<PointF> leftEyeContour =
//                                                            face.getContour(FaceContour.LEFT_EYE).getPoints();
//                                                    Log.d("디버그태그", "");
                                                    List<PointF> upperLipBottomContour =
                                                            face.getContour(FaceContour.UPPER_LIP_TOP).getPoints();


                                                    Log.d(TAG+ " upperLipBottomContour.x", String.valueOf(upperLipBottomContour.get(5).x));
                                                    Log.d(TAG+ " upperLipBottomContour.y", String.valueOf(upperLipBottomContour.get(5).y));
                                                    float lcx = upperLipBottomContour.get(5).x;
                                                    float lcy = upperLipBottomContour.get(5).y;

                                                    imageLC.setX(p.x * lcx / new_bitmap.getWidth() - 70);
                                                    imageLC.setY(p.y * lcy / new_bitmap.getHeight() - 220);

                                                    // If classification was enabled:
                                                    if (face.getSmilingProbability() != null) {
                                                        float smileProb = face.getSmilingProbability();
                                                        Log.d(TAG+ " smileProb", String.valueOf(smileProb));
                                                    }
                                                    if (face.getRightEyeOpenProbability() != null) {
                                                        float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                        Log.d(TAG+ " rightEyeOpenProb", String.valueOf(rightEyeOpenProb));

                                                    }


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

            }

            @Override
            public void onVideoTaken(VideoResult result) {
                Log.d(TAG+ " onVideoTaken", String.valueOf(result));
                // A Video was taken!

            }

            // And much more
        });

        btn_cam_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.setFacing(Facing.FRONT);
            }
        });


        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });
    }


    public void filter(View v){
        switch(v.getId()){
            case  R.id.original:
                camera.setFilter(Filters.NONE.newInstance());

                break;
            case  R.id.auto_fix:
                camera.setFilter(Filters.AUTO_FIX.newInstance());

                break;
            case  R.id.black_and_white:
                camera.setFilter(Filters.BLACK_AND_WHITE.newInstance());

                break;
            case  R.id.brightness:
                camera.setFilter(Filters.BRIGHTNESS.newInstance());

                break;
            case  R.id.documentary:
                camera.setFilter(Filters.DOCUMENTARY.newInstance());

                break;
            case  R.id.fill_light:
                camera.setFilter(Filters.FILL_LIGHT.newInstance());

                break;
            case  R.id.gamma:
                camera.setFilter(Filters.GAMMA.newInstance());

                break;
            case  R.id.grain:
                camera.setFilter(Filters.GRAIN.newInstance());

                break;
            case  R.id.grayscale:
                camera.setFilter(Filters.GRAYSCALE.newInstance());

                break;
            case  R.id.hue:
                camera.setFilter(Filters.HUE.newInstance());

                break;
            case  R.id.sepia:
                camera.setFilter(Filters.SEPIA.newInstance());

                break;




            default:
                break;
        }
    }

    public Bitmap byteArrayToBitmap( byte[] $byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
    }

}