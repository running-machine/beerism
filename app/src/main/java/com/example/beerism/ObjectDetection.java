// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.beerism;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ObjectDetection extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ObjectDetection";
    // 서버에 있는 모델
    private static final String HOSTED_MODEL_NAME = "beer-deteter";
    // 로컬에 있는 모델
    private static final String LOCAL_MODEL_ASSET = "model.tflite";
    private static final String LOCAL_MODEL_NAME = "asset";//"beer_20191010105516";
    // assets 하위에 있는 정답(라벨 파일)
    private static final String LABEL_PATH = "label.txt";
    //인식 결과값 상한치
    private static final int RESULTS_TO_SHOW = 3;
    /**
     * Dimensions of inputs.
     */
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int DIM_IMG_SIZE_X = 512;
    private static final int DIM_IMG_SIZE_Y = 512;
    // Float32 형으로 이미지 탐지 결과를 받았을 때 올바른 Label을 추출하기 위한 객체
    private final PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float>
                                o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    /* Preallocated buffers for storing image data. */
    // 이미지 탐지를 위해 bitmap을 재조정할 때 사용할 크기
    private final int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y ];

    private ImageView mImageView; // 사용자에게 보이는 이미지
    private ImageButton mRunCustomModelButton; // 탐지를 시작하는 버튼
    private Bitmap mSelectedImage; // image detection에 사용되는 bitmap 객체
    private GraphicOverlay mGraphicOverlay; // image detection 결과를 보여주는 graphic 객체
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;
    /**
     * Labels corresponding to the output of the vision model.
     */
    private List<String> mLabelList;
    /**
     * An instance of the driver class to run model inference with Firebase.
     */
    private FirebaseModelInterpreter mInterpreter;
    /**
     * Data configuration of input & output data of model.
     */
    private FirebaseModelInputOutputOptions mDataOptions;

    /**
     * 특정 위치의 그림을 bitmap 객체로 반환하는 메소드
     * @param context
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mImageView = findViewById(R.id.view_finder);

        mRunCustomModelButton = findViewById(R.id.capture_button);

        mGraphicOverlay = findViewById(R.id.graphic_overlay);

        Uri receivedUri = getIntent().getParcelableExtra("imageUri");
        mSelectedImage = convertUriToBitmap(receivedUri);
        mImageView.setImageBitmap(mSelectedImage);

        initCustomModel();
        mRunCustomModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runModelInference();
            }
        });
//        Spinner dropdown = findViewById(R.id.spinner);
//        String[] items = new String[]{"Test Image 1 (Text)", "Test Image 2 (Text)", "Test Image 3" +
//                " (Face)", "Test Image 4 (Object)", "Test Image 5 (Object)"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
//                .simple_spinner_dropdown_item, items);
//        dropdown.setAdapter(adapter);
//        dropdown.setOnItemSelectedListener(this);
    }

    private void initCustomModel() {
        mLabelList = loadLabelList(this);

        int[] inputDims = {DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE};
        int[] outputDims = {DIM_BATCH_SIZE, mLabelList.size()};
        try {
            // 이미지 탐지 전 어떤 크기로 탐지를 할지 정의
            mDataOptions =
                    new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.INT32, new int[]{1,512,512,3})
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32,new int[]{1,20,4})
                            .build();

            // 모델 불러오는 순서 : FirebaseModelDownloadConditions로 초기 설정 ->
            //      remotemodel과 localmodel을 manager와 model option에 등록 ->
            //      model option을 interpreter에 등록 후 탐지 전에 interpreter를 통해 탐지
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions
                    .Builder()
                    .requireWifi()
                    .build();

            FirebaseRemoteModel remoteModel = new FirebaseRemoteModel.Builder(HOSTED_MODEL_NAME)
                    .enableModelUpdates(true)
                    .setInitialDownloadConditions(conditions)
                    .setUpdatesDownloadConditions(conditions)
                    .build();

            FirebaseLocalModel localModel = new FirebaseLocalModel.Builder(LOCAL_MODEL_NAME)
                    .setAssetFilePath(LOCAL_MODEL_ASSET).build();

            FirebaseModelManager manager = FirebaseModelManager.getInstance();
            manager.registerRemoteModel(remoteModel);
            manager.registerLocalModel(localModel);

            FirebaseModelOptions modelOptions =
                    new FirebaseModelOptions.Builder()
                            .setRemoteModelName(HOSTED_MODEL_NAME)
                            .setLocalModelName(LOCAL_MODEL_NAME)
                            .build();

//            manager.downloadRemoteModelIfNeeded(remoteModel)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.v(TAG, "모델 사용 가능");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.v(TAG, "모델 사용 불가능");
//                    }
//                });

            mDataOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, inputDims)
                    .setOutputFormat(0, FirebaseModelDataType.INT32, outputDims)
                    .build();

            mInterpreter = FirebaseModelInterpreter.getInstance(modelOptions);
        } catch (FirebaseMLException e) {
            showToast("모델 셋팅 오류");
            e.printStackTrace();
        }
    }

    // 앱에서 모델을 사용하기 위한 메소드
    private void runModelInference() {
        if (mInterpreter == null) {
            Log.e(TAG, "이미지 분류 초기화 실패");
            return;
        }

        // Create input data.
        ByteBuffer imgData = convertBitmapToByteBuffer(mSelectedImage);
//        ByteBuffer imgData2 = convertBitmapToByteBuffer2(mSelectedImage);
//        float[][][][] input = convertBitmapToInputArray(mSelectedImage);

        try {
            // interpreter를 실행하기 위해 필요한 입력(특정 자료형으로 변환된 image)
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder().add(imgData).build();

            // Here's where the magic happens!!
            Log.v(TAG, "분석 시작");
            mInterpreter
                    .run(inputs, mDataOptions)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            showToast("인식 오류");
                        }
                    })
//                    .addOnSuccessListener(new OnSuccessListener<FirebaseModelOutputs>() {
//                        @Override
//                        public void onSuccess(FirebaseModelOutputs firebaseModelOutputs) {
//                            Log.v(TAG, "데이터 추출 시작");
//                            firebaseModelOutputs.getOutput(0);
//                            Log.v(TAG, "데이터 추출 완료");
//                        }
//                    });
                    .continueWith(new Continuation<FirebaseModelOutputs, List<String>>() {
                        @Override
                        public List<String> then(Task<FirebaseModelOutputs> task) {
                            List<String> topLabels = null;
                            try {
                                Log.v(TAG, "데이터 추출 시작");
                                byte[][][] label = task.getResult().getOutput(0);

//                                byte[][] labelProbArray = task.getResult().<byte[][]>getOutput(0); // 오류뜸
//                                FirebaseModelOutputs result = task.getResult();
//                                float output = result.getOutput(0);
//                                float[][] output = result.getOutput(0);
//
//                                Log.v(TAG, "데이터 추출 완료");
//
//                                topLabels = getTopLabels(output);
//                                Log.v(TAG, "라벨 구하기 완료");
//
//                                탐지된 객체 overlay 표시
//                                mGraphicOverlay.clear();
//                                GraphicOverlay.Graphic labelGraphic = new LabelGraphic(mGraphicOverlay, topLabels);
//                                mGraphicOverlay.add(labelGraphic);
//
//                                Log.v(TAG, "오버레이 표시 완료");
//                                for (String label : topLabels) {
//                                    Log.v(TAG, label);
//                                }
                                Log.v(TAG, "분석 완료");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return topLabels;
                        }
                    });
        } catch (FirebaseMLException e) {
            e.printStackTrace();
            showToast("모델 실행중 오류 발생");
        }
    }

    /**
     * Gets the top label.txt in the results.
     */
    private synchronized List<String> getTopLabels(int[][] labelProbArray) {
        // label 정렬
        for (int i = 0; i < mLabelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(mLabelList.get(i), (labelProbArray[0][i] &
                            0xff) / 255.0f));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }

        // 나온 점수에 따라 점수와 label를 정리
        List<String> result = new ArrayList<>();
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            result.add(label.getKey() + ":" + label.getValue());
        }

        Log.d(TAG, "label.txt: " + result.toString());

        return result;
    }

    // 라벨 txt파일 읽어오기 함수
    private List<String> loadLabelList(Activity activity) {
        List<String> labelList = new ArrayList<>();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(activity.getAssets().open
                             (LABEL_PATH)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read label.txt list.", e);
        }
        return labelList;
    }

    /**
     * bitmap을 tensor FLOAT32 형과 맞추기 위해 4차원 float 배열로 변환하는 메소드
     * @param bitmap
     * @return
     */
    private float[][][][] convertBitmapToInputArray(Bitmap bitmap) {
        // [START mlkit_bitmap_input]

        bitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true);

        int batchNum = 0;
        float[][][][] input = new float[DIM_BATCH_SIZE][DIM_IMG_SIZE_X][DIM_IMG_SIZE_Y][DIM_PIXEL_SIZE];
        for (int x = 0; x < DIM_IMG_SIZE_X; x++) {
            for (int y = 0; y < DIM_IMG_SIZE_Y; y++) {
                int pixel = bitmap.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
            }
        }
        // [END mlkit_bitmap_input]

        return input;
    }

    /**
     * Writes Image data into a {@code ByteBuffer}.
     */
    private synchronized ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(4* DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true);
        imgData.rewind();
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight());

        // Convert the image to int points.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                imgData.putFloat((byte) ((val >> 16) & 0xFF));
                imgData.putFloat((byte) ((val >> 8) & 0xFF));
                imgData.putFloat((byte) (val & 0xFF));
            }
        }
        return imgData;
    }

    /**
     * bitmap을 tensor Byte 형과 맞추기 위해 ByteBuffer로 변환하는데 float 형태로 저장하는 메소드
     * @param bitmap
     * @return
     */
    private ByteBuffer convertBitmapToByteBuffer2(Bitmap bitmap) {
        // float 형태로 저장 되므로 4(float 형의 크기)가 곱해짐
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true);
        bitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        int pixel = 0;
        final int IMAGE_MEAN = 128;
        final float IMAGE_STD = 128f;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat((((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                byteBuffer.putFloat((((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                byteBuffer.putFloat((((val) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
            }
        }

        return byteBuffer;
    }

    // Functions for loading images from app assets.
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();

        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;

        return new Pair<>(targetWidth, targetHeight);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        mGraphicOverlay.clear();
        switch (position) {
            case 0:
                mSelectedImage = getBitmapFromAsset(this, "Please_walk_on_the_grass.jpg");
                break;
            case 1:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "nl2.jpg");
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "grace_hopper.jpg");
                break;
            case 3:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "tennis.jpg");
                break;
            case 4:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "mountain.jpg");
                break;
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    /**
     * 입력 받는 Uri를 Bitmap으로 변환하는 메소드
     * 다른 화면에서 카메라 촬영이나 앨범 선택으로 이미지를 넘겨받을 때 Uri로 넘기 받기 때문에 사용
     * @param uri
     * @return
     */
    private Bitmap convertUriToBitmap(Uri uri) {
        Bitmap image = null;

        try {
            InputStream image_stream;

            try {
                image_stream = this.getContentResolver().openInputStream(uri);
                image = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    // 파이어베이스비전 이미지를 호출하기전 위치 변환
    private class YourAnalyzer implements ImageAnalysis.Analyzer {
        private int degreesToFirebaseRotation(int degrees) {
            switch (degrees) {
                case 0:
                    return FirebaseVisionImageMetadata.ROTATION_0;
                case 90:
                    return FirebaseVisionImageMetadata.ROTATION_90;
                case 180:
                    return FirebaseVisionImageMetadata.ROTATION_180;
                case 270:
                    return FirebaseVisionImageMetadata.ROTATION_270;
                default:
                    throw new IllegalArgumentException("각도는 0,90,180,270도 만 가능합니다.");
            }
        }

        @Override
        public void analyze(ImageProxy image, int rotationDegrees) {
            if (image == null || image.getImage() == null) {
                return;
            }

            Image mediaImage = image.getImage();
            int rotation = degreesToFirebaseRotation(rotationDegrees);

            FirebaseVisionImage image3 = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
        }
    }
}