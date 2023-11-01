package com.example.textandtranslate;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button pickimage = null;
    ImageView imgview = null;
    MaterialToolbar toolbar = null;
    TextInputEditText textView = null;
    InputImage inputImage = null;

    private DataBaseHelper databaseHelper;

    private MainActivityViewModel mainActivityViewModel;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    // When using Devanagari script library
    TextRecognizer recognizer =
            TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pickimage = findViewById(R.id.idBtnPickImage);
        imgview = findViewById(R.id.idIVImage);
        textView = findViewById(R.id.text_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        databaseHelper = new DataBaseHelper(this);
        toolbar = findViewById(R.id.topAppBar);
//      toolbar.inflateMenu(R.menu.top_app_bar);

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Initialize the ActivityResultLauncher for picking an image
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // The user has successfully picked an image
                        Uri selectedImageUri = result.getData().getData();
                        if(selectedImageUri!=null){
                            imgview.setImageURI(selectedImageUri);
                            try {
                                inputImage = InputImage.fromFilePath(getApplicationContext(),selectedImageUri);
                                mainActivityViewModel.setInputImage(inputImage);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
        );

        //onClicking the pick image button, it will get directed to openGallery...
        pickimage.setOnClickListener(view -> {
            if (hasStoragePermission()) {
                // Create an intent to pick an image from the gallery
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageLauncher.launch(pickImageIntent);
            } else {
                requestStoragePermission();
            }
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.textScan){
                    if(inputImage!=null){
                        imageProcess(inputImage);
                        saveTextToDatabase();
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Image is not Selected",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    return true;
                }
                else if(id == R.id.translateText){
                    openTranslateActivity();
                    return true;
                }
                return false;
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.themeChange) {
                // Replace this with your button click action
                // For example, open a new activity or show a dialog
                // You can also handle the button click here directly.
                // Add your logic here.
                // Toggle between light and dark themes


                return true;
            }
            if(item.getItemId() == R.id.more){
                Intent intent = new Intent(this,DataRecordPast.class);
                startActivity(intent);
            }
            return false;
        });

        toolbar.getMenu().findItem(R.id.deleteAll).setVisible(false);

    }

    private void saveTextToDatabase() {
        String text = textView.getText().toString();
        if (!text.isEmpty()) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DataBaseHelper.COLUMN_TEXT, text);

            db.insert(DataBaseHelper.TABLE_NAME, null, values);
            db.close();

            //inputText.setText(""); // Clear the input field
        }
    }

    private void openTranslateActivity() {
        Intent intent = new Intent(this, Translate.class);
        String textToTransfer = textView.getText().toString();
        intent.putExtra("transferredText", textToTransfer);
        startActivity(intent);
    }

    //check if the app has persmission...
    private boolean hasStoragePermission() {
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission == PermissionChecker.PERMISSION_GRANTED;
    }

    //will request for permission...
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    //when the permission is granted...
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            // Permission granted, open the image picker
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pickImageIntent);
        }
    }


    //function for extracting text...
    private void imageProcess(InputImage image){
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // ...
                                StringBuilder detectedText = new StringBuilder();
                                for (Text.TextBlock textBlock : visionText.getTextBlocks()) {
                                    detectedText.append(textBlock.getText()).append("\n");
                                }
                                //extractText(visionText);
                                // Update the TextView with the recognized text
                                textView.setText(detectedText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...

                                        Toast toast = Toast.makeText(getApplicationContext(),"Error detecting text",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });

    }

}