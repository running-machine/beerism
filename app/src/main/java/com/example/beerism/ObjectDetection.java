package com.example.beerism;


import android.media.Image;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

public class ObjectDetection {

    FirebaseVisionImage image;

    private class suitableImage implements ImageAnalysis.Analyzer {

        private int degeersToFirebaseRotation(int degress) {
            switch (degress) {
                case 0:
                    return FirebaseVisionImageMetadata.ROTATION_0;
                case 90:
                    return FirebaseVisionImageMetadata.ROTATION_90;
                case 180:
                    return FirebaseVisionImageMetadata.ROTATION_180;
                case 270:
                    return FirebaseVisionImageMetadata.ROTATION_270;
                default:
                    throw new IllegalArgumentException(
                            "Rotation must be 0, 90, 180, or 270.");
            }
        }

        @Override
        public void analyze(ImageProxy image, int rotationDegrees) {
            if(image ==null || image.getImage()==null){
                return;
            }
            Image mediaImage = image.getImage();
            int rotation = degeersToFirebaseRotation(rotationDegrees);
            FirebaseVisionImage visionImage = FirebaseVisionImage.fromMediaImage(mediaImage,rotation);

        }
    }

}
