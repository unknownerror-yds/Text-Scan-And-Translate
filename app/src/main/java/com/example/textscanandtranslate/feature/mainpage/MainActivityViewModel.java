package com.example.textscanandtranslate.feature.mainpage;

import androidx.lifecycle.ViewModel;

import com.google.mlkit.vision.common.InputImage;

public class MainActivityViewModel extends ViewModel {
    private InputImage inputImage;

    public InputImage getInputImage() {
        return inputImage;
    }

    public void setInputImage(InputImage inputImage) {
        this.inputImage = inputImage;
    }
}
