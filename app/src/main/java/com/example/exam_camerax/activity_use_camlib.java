package com.example.exam_camerax;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.media.Image;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    ImageView btn_cam_location;
    ImageView btn_capture;
    ImageView image_capture;
    CameraView camera;
    private Boolean camera_location_swtich = false;
    private String TAG = "profile";

    //?????? ?????? ??????
    FaceDetectorOptions realTimeOpts;
    ImageAnalysis imageAnalysis;

    ImageView ImageView_face, imageLC, imageRC;
    RelativeLayout RelativeLayout_face;
    InputImage image;
    Point p;
    Display display;

    private LinearLayout acti_use_camlib_effectContainer;
    private RelativeLayout effects_rel_container;
    private Timer timer;

    //????????? ?????? ??????
    private SparseBooleanArray mSelectedItems;
    private ImageView rabbit_effect;
    private ImageView beard_effect;
    private ImageView glasses_effect;
    private ImageView hat_effect;
    private Boolean effect_layout_switch = false; //?????? ???????????? ?????? / ?????? Bol
    private Boolean camera_capture = false;

    //????????? ?????? ??? ????????? ???????????? ??????
    private RelativeLayout after_capture_rel_container;
    private LinearLayout after_capture_save_container;
    private LinearLayout after_capture_back_container;
    private ImageView after_capture_user_img;


    float oldXvalue;
    float oldYvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_camlib);
        camera = findViewById(R.id.camera);
        btn_cam_location = findViewById(R.id.btn_cam_location);
        btn_cam_location.bringToFront();
        image_capture = findViewById(R.id.image_capture);
        btn_capture = findViewById(R.id.btn_capture);

        RelativeLayout_face = findViewById(R.id.RelativeLayout_face);
        acti_use_camlib_effectContainer = findViewById(R.id.acti_use_camlib_effectContainer);
        RelativeLayout_face.removeViewInLayout(imageLC);
        RelativeLayout_face.removeViewInLayout(imageRC);

        //????????? ?????? ??????
        rabbit_effect = findViewById(R.id.rabbit_effect);
        beard_effect = findViewById(R.id.beard_effect);
        glasses_effect = findViewById(R.id.glasses_effect);
        hat_effect = findViewById(R.id.hat_effect);
        acti_use_camlib_effectContainer = findViewById(R.id.acti_use_camlib_effectContainer);//?????? ?????? ????????????
        effects_rel_container =findViewById(R.id.effects_rel_container);//????????? ?????? ?????? ????????????
        mSelectedItems = new SparseBooleanArray(0); //????????? ?????? Bool??????
        mSelectedItems.put(1,false); //rabbit
        mSelectedItems.put(2,false); //beard
        mSelectedItems.put(3,false); //glasses
        mSelectedItems.put(4,false); //hat

        //????????? ?????? ??? ????????? ???????????? ??????
        after_capture_rel_container = findViewById(R.id.after_capture_rel_container);
        after_capture_user_img = findViewById(R.id.after_capture_user_img);
        after_capture_save_container = findViewById(R.id.after_capture_save_container);
        after_capture_back_container = findViewById(R.id.after_capture_back_container);



        p = new Point();
         display = getWindowManager().getDefaultDisplay();
        display.getSize(p);


//        //?????? ?????? ?????? ???????????? ??????
//        imageLC = new ImageView(activity_use_camlib.this);
//        imageLC.setImageResource(R.drawable.left_whiskers);
//        imageLC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
//
//
//        imageRC = new ImageView(activity_use_camlib.this);
//        imageRC.setImageResource(R.drawable.right_whiskers);
//        imageRC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
//
//
//        RelativeLayout_face.addView(imageLC);
//        RelativeLayout_face.addView(imageRC);
//
//        camera.setLifecycleOwner(this);
//        camera.setMode(Mode.PICTURE); // for pictures

//
//        timer = new Timer();
//        TimerTask TT = new TimerTask() {
//            @Override
//            public void run() {
//                // ??????????????? ??????
//                camera.takePictureSnapshot();
//            }
//        };
//        timer.schedule(TT, 0, 1500); //Timer ??????
//        //?????? ?????? ?????? ???????????? ??????

        acti_use_camlib_effectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(effect_layout_switch){
                    effects_rel_container.setVisibility(View.GONE);
                    effect_layout_switch= false;
                }else{
                    effects_rel_container.setVisibility(View.VISIBLE);
                    effect_layout_switch= true;
                    camera_capture = false;
                }
            }
        });


        //test ??????
        camera.setLifecycleOwner(this);
        camera.setMode(Mode.PICTURE); // for pictures
        timer = new Timer();

        rabbit_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effect_bol_init();
                timer.cancel();
                RelativeLayout_face.removeViewInLayout(imageLC);
                RelativeLayout_face.removeViewInLayout(imageRC);

                //?????? ?????? ?????? ???????????? ??????
                imageLC = new ImageView(activity_use_camlib.this);
                imageLC.setImageResource(R.drawable.left_whiskers);
                imageLC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));

                imageRC = new ImageView(activity_use_camlib.this);
                imageRC.setImageResource(R.drawable.right_whiskers);
                imageRC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));


                RelativeLayout_face.addView(imageLC);
                RelativeLayout_face.addView(imageRC);

                camera.setLifecycleOwner(activity_use_camlib.this);
                camera.setMode(Mode.PICTURE); // for pictures
                mSelectedItems.put(1,true);


                timer = new Timer();
                TimerTask TT = new TimerTask() {
                    @Override
                    public void run() {
                        // ??????????????? ??????
                        camera.takePictureSnapshot();
                    }
                };
                timer.schedule(TT, 0, 1000); //Timer ??????
                //?????? ?????? ?????? ???????????? ??????
            }
        });

        //test ??????
        beard_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effect_bol_init();
                timer.cancel();
                RelativeLayout_face.removeViewInLayout(imageLC);
                RelativeLayout_face.removeViewInLayout(imageRC);

                //?????? ?????? ?????? ???????????? ??????
                imageLC = new ImageView(activity_use_camlib.this);
                imageLC.setImageResource(R.drawable.beard);
                imageLC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));

                RelativeLayout_face.addView(imageLC);

                camera.setLifecycleOwner(activity_use_camlib.this);
                camera.setMode(Mode.PICTURE); // for pictures

                mSelectedItems.put(2,true);


                timer = new Timer();
                TimerTask TT = new TimerTask() {
                    @Override
                    public void run() {
                        // ??????????????? ??????
                        camera.takePictureSnapshot();
                    }
                };
                timer.schedule(TT, 0, 1000); //Timer ??????
                //?????? ?????? ?????? ???????????? ??????
            }
        });


        //test ??????
        glasses_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effect_bol_init();
                timer.cancel();
                RelativeLayout_face.removeViewInLayout(imageLC);
                RelativeLayout_face.removeViewInLayout(imageRC);

                //?????? ?????? ?????? ???????????? ??????
                imageLC = new ImageView(activity_use_camlib.this);
                imageLC.setImageResource(R.drawable.glasses);
                imageLC.setLayoutParams(new RelativeLayout.LayoutParams(500,500));

                RelativeLayout_face.addView(imageLC);

                camera.setLifecycleOwner(activity_use_camlib.this);
                camera.setMode(Mode.PICTURE); // for pictures

                mSelectedItems.put(3,true);

                 timer = new Timer();
                TimerTask TT = new TimerTask() {
                    @Override
                    public void run() {
                        // ??????????????? ??????
                        camera.takePictureSnapshot();
                    }
                };
                timer.schedule(TT, 0, 1000); //Timer ??????
                //?????? ?????? ?????? ???????????? ??????
            }
        });


        //test ??????
        hat_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effect_bol_init();
                timer.cancel();
                RelativeLayout_face.removeViewInLayout(imageLC);
                RelativeLayout_face.removeViewInLayout(imageRC);

                //?????? ?????? ?????? ???????????? ??????
                imageLC = new ImageView(activity_use_camlib.this);
                imageLC.setImageResource(R.drawable.hat);
                imageLC.setLayoutParams(new RelativeLayout.LayoutParams(300,300));

                RelativeLayout_face.addView(imageLC);

                camera.setLifecycleOwner(activity_use_camlib.this);
                camera.setMode(Mode.PICTURE); // for pictures

                mSelectedItems.put(4,true);


                timer = new Timer();
                TimerTask TT = new TimerTask() {
                    @Override
                    public void run() {
                        // ??????????????? ??????
                        camera.takePictureSnapshot();
                    }
                };
                timer.schedule(TT, 0, 1000); //Timer ??????
                //?????? ?????? ?????? ???????????? ??????
            }
        });




        //????????? ?????? Listener
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                // A Picture was taken!
                // Picture was taken!
                // If planning to show a Bitmap, we will take care of
                // EXIF rotation and background threading for you...
                Log.d(" onPictureTakenmSelectedItems", String.valueOf(mSelectedItems.toString()));


                Log.d(TAG+ " onPictureTakenResult", String.valueOf(result));

                Bitmap new_bitmap = CameraUtils.decodeBitmap(result.getData());
                Bitmap resizedBmp = Bitmap.createScaledBitmap(new_bitmap, (int) 720, (int) 720, true);

                Log.d("new_bitmaphight", String.valueOf(new_bitmap.getHeight()));
                Log.d("new_bitmagetWidth", String.valueOf(new_bitmap.getWidth()));
                Log.d("resizedBmpphight", String.valueOf(resizedBmp.getHeight()));
                Log.d("resizedBmpetWidth", String.valueOf(resizedBmp.getWidth()));
                Log.d("cameraWgetWidth", String.valueOf(camera.getWidth()));
                Log.d("cameraWidth", String.valueOf(camera.getWidth()));



//                image_capture.setImageBitmap(new_bitmap);



                realTimeOpts =
                        new FaceDetectorOptions.Builder()
                                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                                .setMinFaceSize((float) 0.1)
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

                                                Log.d(TAG+ " onSuccess", "?????????");
                                                Log.d(TAG+ " onSuccessfaces", String.valueOf(faces));
                                                Point p = new Point();
                                                Display display = getWindowManager().getDefaultDisplay();
                                                display.getSize(p);

                                                for (Face face : faces) {
                                                    Toast.makeText(getApplicationContext(), "??????",Toast.LENGTH_LONG).show();

                                                    if(mSelectedItems.get(1)){
                                                        //?????? ?????? ??????
                                                        FaceLandmark leftCheek = face.getLandmark(FaceLandmark.LEFT_EAR);

                                                        PointF leftEarPos = leftCheek.getPosition();
                                                        Log.d(TAG+ " leftEarPos", leftEarPos.toString());
                                                        Log.d(TAG+ " leftEarPos.x", String.valueOf(leftEarPos.x));
                                                        Log.d(TAG+ " leftEarPos.y", String.valueOf(leftEarPos.y));

                                                        FaceLandmark rightCheek = face.getLandmark(FaceLandmark.RIGHT_EAR);
                                                        PointF rightEarPos = rightCheek.getPosition();


                                                        float lcx = leftEarPos.x;
                                                        float lcy = leftEarPos.y;

                                                        float rcx = rightEarPos.x;
                                                        float rcy = rightEarPos.y;

                                                        imageLC.setX(p.x * lcx / new_bitmap.getWidth() - 100);
                                                        imageLC.setY(p.y * lcy / new_bitmap.getHeight() - 100);

                                                        imageRC.setX(p.x * rcx / new_bitmap.getWidth() - 100);
                                                        imageRC.setY(p.y * rcy / new_bitmap.getHeight() - 100);

                                                        //?????? ?????? ??????

                                                        if(camera_capture){
                                                            Log.d(TAG+ " camera_capture", String.valueOf(camera_capture));
                                                            timer.cancel(); //????????? ???????????? timer??? ?????????
                                                            camera_capture = false; // bol?????? false??? ????????????
                                                            RelativeLayout_face.removeViewInLayout(imageLC); //????????? ???????????? ???????????????
                                                            RelativeLayout_face.removeViewInLayout(imageRC);

                                                            after_capture_rel_container.setVisibility(View.VISIBLE);
                                                            after_capture_rel_container.bringToFront();

                                                            after_capture_user_img.setImageBitmap(new_bitmap);

//

                                                            Bitmap bitmap = ((BitmapDrawable)after_capture_user_img.getDrawable()).getBitmap();

                                                            Capture_img_faceDetection(bitmap);
                                                        }
                                                    }else if(mSelectedItems.get(2)){
                                                        //?????? ??????
                                                        if(face.getContour(FaceContour.UPPER_LIP_TOP) !=null){
                                                            imageLC.setVisibility(View.VISIBLE); //null??? ????????? ?????? ?????????


                                                            List<PointF> upperLipBottomContour =
                                                                    face.getContour(FaceContour.UPPER_LIP_TOP).getPoints();
                                                            Log.d(TAG+ " upperLipBottomContour.x", String.valueOf(upperLipBottomContour.get(5).x));
                                                            Log.d(TAG+ " upperLipBottomContour.y", String.valueOf(upperLipBottomContour.get(5).y));
                                                            float lcx = upperLipBottomContour.get(5).x;
                                                            float lcy = upperLipBottomContour.get(5).y;

                                                            imageLC.setX(p.x * lcx / new_bitmap.getWidth() - 70);
                                                            imageLC.setY(p.y * lcy / new_bitmap.getHeight() - 220);
                                                            //????????????
                                                        }else{
                                                            imageLC.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.",Toast.LENGTH_LONG).show();
                                                        }





                                                    }else if(mSelectedItems.get(3)){


                                                        if(face.getContour(FaceContour.NOSE_BRIDGE) !=null){
                                                            //?????? ??????
                                                            imageLC.setVisibility(View.VISIBLE); //null??? ????????? ?????? ?????????

                                                            List<PointF> upperLipBottomContour =
                                                                    face.getContour(FaceContour.NOSE_BRIDGE).getPoints();


                                                            Log.d(TAG+ " upperLipBottomContour.x", String.valueOf(upperLipBottomContour.get(0).x));
                                                            Log.d(TAG+ " upperLipBottomContour.y", String.valueOf(upperLipBottomContour.get(0).y));
                                                            float lcx = upperLipBottomContour.get(0).x;
                                                            float lcy = upperLipBottomContour.get(0).y;

                                                            imageLC.setX(p.x * lcx / new_bitmap.getWidth() - 170);
                                                            imageLC.setY(p.y * lcy / new_bitmap.getHeight() - 250);
                                                            //?????? ??????
                                                        }else{
                                                            imageLC.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.",Toast.LENGTH_LONG).show();
                                                        }

                                                    }else if(mSelectedItems.get(4)){
                                                        if(face.getContour(FaceContour.FACE) !=null){
                                                            //?????? ?????? ??????
                                                            List<PointF> upperLipBottomContour =
                                                                    face.getContour(FaceContour.FACE).getPoints();

                                                            Log.d(TAG+ " upperLipBottomContour.x", String.valueOf(upperLipBottomContour.get(30).x));
                                                            Log.d(TAG+ " upperLipBottomContour.y", String.valueOf(upperLipBottomContour.get(30).y));
                                                            float lcx = upperLipBottomContour.get(30).x;
                                                            float lcy = upperLipBottomContour.get(30).y;

                                                            imageLC.setX(p.x * lcx / new_bitmap.getWidth() - 100);
                                                            imageLC.setY(p.y * lcy / new_bitmap.getHeight() - 400);
                                                            //?????? ?????? ??????
                                                        }else{
                                                            RelativeLayout_face.removeViewInLayout(imageLC);
                                                            RelativeLayout_face.removeViewInLayout(imageRC);
                                                            Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.",Toast.LENGTH_LONG).show();
                                                        }



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
                                                Log.d(TAG+ " onFailure", "?????????");
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
                if(camera_location_swtich){
                    camera.setFacing(Facing.BACK);
                    camera_location_swtich=false;
                }else{
                    camera.setFacing(Facing.FRONT);
                    camera_location_swtich=true;
                }
            }
        });


        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera_capture = true;
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
            case  R.id.SEPIA:
                camera.setFilter(Filters.SEPIA.newInstance());

                break;
            case  R.id.DUOTONE:
                camera.setFilter(Filters.DUOTONE.newInstance());

                break;
            case  R.id.INVERT_COLORS:
                camera.setFilter(Filters.INVERT_COLORS.newInstance());

                break;
            case  R.id.POSTERIZE:
                camera.setFilter(Filters.POSTERIZE.newInstance());

                break;
            case  R.id.SATURATION:
                camera.setFilter(Filters.SATURATION.newInstance());

                break;
            case  R.id.SHARPNESS:
                camera.setFilter(Filters.SHARPNESS.newInstance());

                break;
            case  R.id.TEMPERATURE:
                camera.setFilter(Filters.TEMPERATURE.newInstance());

                break;
            case  R.id.TINT:
                camera.setFilter(Filters.TINT.newInstance());

                break;
            case  R.id.VIGNETTE:
                camera.setFilter(Filters.VIGNETTE.newInstance());

                break;





            default:
                break;
        }
    }

    public Bitmap byteArrayToBitmap( byte[] $byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
    }

    private void effect_bol_init(){
        for (int i = 0; i < mSelectedItems.size(); i++){
            mSelectedItems.put(i+1,false);
        }
        Log.d(TAG+ " effect_bol_init", mSelectedItems.toString());
    }

    private void Capture_img_faceDetection(Bitmap get_bitmap ){
        //?????? ?????? ?????? ???????????? ??????
        imageLC = new ImageView(activity_use_camlib.this);
        imageLC.setImageResource(R.drawable.left_whiskers);
        imageLC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
        imageLC.setVisibility(View.INVISIBLE);


        imageRC = new ImageView(activity_use_camlib.this);
        imageRC.setImageResource(R.drawable.right_whiskers);
        imageRC.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
        imageRC.setVisibility(View.INVISIBLE);

        after_capture_rel_container.addView(imageLC);
        after_capture_rel_container.addView(imageRC);


        InputImage capture_image = InputImage.fromBitmap(get_bitmap,0);


        // High-accuracy landmark detection and face classification
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);



        Task<List<Face>> result =
                detector.process(capture_image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        // Task completed successfully
                                        // ...

                                        for (Face face : faces) {
                                            FaceLandmark leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK);
                                            PointF leftEarPos = leftCheek.getPosition();
                                            Log.d(TAG+ " leftEarPos", leftEarPos.toString());
                                            Log.d(TAG+ " leftEarPos.x", String.valueOf(leftEarPos.x));
                                            Log.d(TAG+ " leftEarPos.y", String.valueOf(leftEarPos.y));

                                            FaceLandmark rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK);
                                            PointF rightEarPos = rightCheek.getPosition();


                                            float lcx = leftEarPos.x;
                                            float lcy = leftEarPos.y;

                                            float rcx = rightEarPos.x;
                                            float rcy = rightEarPos.y;

                                            imageLC.setX(p.x * lcx / get_bitmap.getWidth() - 100);
                                            imageLC.setY(p.y * lcy / get_bitmap.getHeight() - 100);
                                            imageLC.setVisibility(View.VISIBLE);
                                            imageLC.setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    move_effect_item(v,event);
                                                    return true;
                                                }

                                            });
                                            imageRC.setX(p.x * rcx / get_bitmap.getWidth() - 0);
                                            imageRC.setY(p.y * rcy / get_bitmap.getHeight() - 100);
                                            imageRC.setVisibility(View.VISIBLE);
                                            imageRC.setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    move_effect_item(v,event);
                                                    return true;
                                                }

                                            });

                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }

    private void move_effect_item(View v, MotionEvent event){
        int parentWidth = ((ViewGroup) v.getParent()).getWidth();    // ?????? View ??? Width
        int parentHeight = ((ViewGroup) v.getParent()).getHeight();    // ?????? View ??? Height

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // ??? ??????
            oldXvalue = event.getX();
            oldYvalue = event.getY();
            Log.d("viewTest", "oldXvalue : " + oldXvalue + " oldYvalue : " + oldYvalue);    // View ???????????? ????????? ????????? ?????? ?????????.
            Log.d("viewTest", "v.getX() : " + v.getX());    // View ??? ?????? ????????? ?????? ????????? ?????? ?????????.
            Log.d("viewTest", "RawX : " + event.getRawX() + " RawY : " + event.getRawY());    // View ??? ????????? ????????? ?????? ?????????.
            Log.d("viewTest", "v.getHeight : " + v.getHeight() + " v.getWidth : " + v.getWidth());    // View ??? Width, Height

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // ??? ?????? ???
            v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
            v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // ????????? ?????? ???

            if (v.getX() < 0) {
                v.setX(0);
            } else if ((v.getX() + v.getWidth()) > parentWidth) {
                v.setX(parentWidth - v.getWidth());
            }

            if (v.getY() < 0) {
                v.setY(0);
            } else if ((v.getY() + v.getHeight()) > parentHeight) {
                v.setY(parentHeight - v.getHeight());
            }

        }
    }

}