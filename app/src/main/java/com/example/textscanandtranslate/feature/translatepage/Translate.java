package com.example.textscanandtranslate.feature.translatepage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.textandtranslate.R;

public class Translate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        String transferText = "";
        Intent intent = getIntent();

        if (intent != null) {
            // Display the transferred text in the TextView
            transferText = intent.getStringExtra("transferredText");
        }

        //TranslateFragment fragment = TranslateFragment.newInstance(transferredText);
        TranslateFragment fragment = TranslateFragment.newInstance(transferText);

//        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow();
//        }

    }
}