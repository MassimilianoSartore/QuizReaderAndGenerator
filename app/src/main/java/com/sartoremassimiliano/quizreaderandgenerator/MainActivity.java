package com.sartoremassimiliano.quizreaderandgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SEARCH = 10009;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickButtonCreateQuiz(View v)
    {
        Intent MyIntent = new Intent(this, QuizGeneratorActivity.class);
        startActivity(MyIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyStoragePermissions(this);
    }

    public void onClickButtonReadQuiz(View v)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(Intent.createChooser(intent,
                "Load a file from directory"), REQUEST_CODE_SEARCH);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH) {
            try {
                Uri uri = data.getData();
                if(uri!=null) {
                    Intent MyIntent = new Intent(this, QuizReaderActivity.class);
                    MyIntent.putExtra("filepath", uri.toString());
                    startActivity(MyIntent);
                }
                else
                {
                    Log.i("0", "Error uri empty");
                }

            }catch (Exception ex) {
                Toast.makeText(this, "Error during file choose!", Toast.LENGTH_LONG).show();
                Log.d("Main error:", ex.toString());
            }
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}