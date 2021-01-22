package com.sartoremassimiliano.quizreaderandgenerator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalTime;

public class QuizGeneratorActivity extends AppCompatActivity {

    private static final int WRITE_REQUEST_CODE = 43;
    private Button buttonSaveQuiz;
    private String quizString;
    private EditText editTextMCQ;
    private EditText editTextMCQC1;
    private EditText editTextMCQC2;
    private EditText editTextMCQC3;
    private CheckBox checkBoxMCQ1;
    private CheckBox checkBoxMCQ2;
    private CheckBox checkBoxMCQ3;
    private EditText editTextQuizName;
    private boolean timerAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_generator);
        editTextQuizName = findViewById(R.id.editTextQuizName);
        editTextMCQ = findViewById(R.id.editTextMCQ);
        editTextMCQC1 = findViewById(R.id.editTextMCQC1);
        editTextMCQC2 = findViewById(R.id.editTextMCQC2);
        editTextMCQC3 = findViewById(R.id.editTextMCQC3);
        checkBoxMCQ1 = findViewById(R.id.checkBoxMCQ1);
        checkBoxMCQ2 = findViewById(R.id.checkBoxMCQ2);
        checkBoxMCQ3 = findViewById(R.id.checkBoxMCQ3);
        buttonSaveQuiz = findViewById(R.id.buttonSaveQuiz);
        quizString = "";
    }


    public void onClickAddYesOrNo(View v) {
        EditText question = findViewById(R.id.editTextYesorNoQuestion);
        RadioButton radioButtonYes = findViewById(R.id.radioButtonYes);
        RadioButton radioButtonNo = findViewById(R.id.radioButtonNo);
        StringBuilder command = new StringBuilder();
        if (!question.toString().matches("") && (radioButtonNo.isChecked() || radioButtonYes.isChecked())) {
            command.append("QYN#" + question.getText().toString());
            if (radioButtonNo.isChecked()) {
                command.append("#0#|");
            } else {
                command.append("#1#|");
            }
            quizString += command.toString();
            question.setText("");
            radioButtonNo.setChecked(false);
            radioButtonYes.setChecked(false);
            Toast.makeText(this, "Question added", Toast.LENGTH_LONG).show();
            Log.i("f",quizString );
        } else {
            Toast.makeText(this, "Error: something is missing!", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickAddCQ(View v)
    {
        StringBuilder command = new StringBuilder();
        EditText editTextCQA = findViewById(R.id.editTextCQA);
        EditText editTextCQQ = findViewById(R.id.editTextCQQ);
        if(!editTextCQA.getText().toString().matches("") && !editTextCQA.getText().toString().matches(" ") && !editTextCQQ.getText().toString().matches("") && !editTextCQQ.getText().toString().matches(" "))
        {
            command.append("CQ#"+editTextCQQ.getText().toString()+"#"+editTextCQA.getText().toString()+"#|");
            quizString += command.toString();
            Toast.makeText(this, "Question added", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "Error: something is missing!", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickAddMCQ(View v) {
        int counterCB = 0;
        if (checkBoxMCQ1.isChecked() && !(editTextMCQC1.getText().toString().matches("")||editTextMCQC1.getText().toString().matches(" "))) {counterCB++;}
        if (checkBoxMCQ2.isChecked() && !(editTextMCQC2.getText().toString().matches("")||editTextMCQC2.getText().toString().matches(" "))) {counterCB+=2;}
        if (checkBoxMCQ3.isChecked() && !(editTextMCQC3.getText().toString().matches("")||editTextMCQC3.getText().toString().matches(" "))) {counterCB+=4;}
        if(!editTextMCQ.getText().toString().matches("") && counterCB>0)
        {
            StringBuilder command = new StringBuilder();
            command.append("MCQ#"+editTextMCQ.getText().toString());
            switch (counterCB)
            {
                case 1:
                    command.append("#1#");
                    break;
                case 2:
                    command.append("#2#");
                    break;
                case 3:
                    command.append("#3#");
                    break;
                case 4:
                    command.append("#4#");
                    break;
                case 5:
                    command.append("#5#");
                    break;
                case 6:
                    command.append("#6#");
                    break;
                case 7:
                    command.append("#7#");
                    break;
                default:
                    Toast.makeText(this, "Error: something is missing!", Toast.LENGTH_LONG).show();
                    return;
            }
            command.append(editTextMCQC1.getText().toString()+"#");
            command.append(editTextMCQC2.getText().toString()+"#");
            command.append(editTextMCQC3.getText().toString()+"#|");
            quizString += command.toString();
            editTextMCQ.setText("");
            editTextMCQC1.setText("");
            editTextMCQC2.setText("");
            editTextMCQC3.setText("");
            checkBoxMCQ1.setChecked(false);
            checkBoxMCQ2.setChecked(false);
            checkBoxMCQ3.setChecked(false);
            Log.i("f",quizString );
            Toast.makeText(this, "Question added", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "Error: something is missing!", Toast.LENGTH_LONG).show();
        }
    }


    public void onClickSaveButton(View v) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            buttonSaveQuiz.setEnabled(false);
        } else {
            createFile();
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public void onClickClose(View v)
    {
        this.finish();
    }

    public void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, editTextQuizName.getText().toString());
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickAddTimer(View v)
    {
        EditText editTextTimer = findViewById(R.id.editTextTimer);
        if(!timerAdded)
        {
            if (!editTextTimer.getText().toString().equals(""))
            {
                try {
                    LocalTime myObj = LocalTime.parse(editTextTimer.getText().toString());
                    Log.i("t", editTextTimer.getText().toString() + " "+myObj.toSecondOfDay() );
                    quizString+="TIMER#"+myObj.toSecondOfDay()+"#|";
                    timerAdded=true;
                    Toast.makeText(this, "Timer added", Toast.LENGTH_LONG).show();
                }catch (Exception ex)
                {
                    Toast.makeText(this, "Error: something went wrong with the timer!", Toast.LENGTH_LONG).show();
                    Log.e("Timer storing process:", "Exception:" , ex);
                }
            }
            else
            {
                Toast.makeText(this, "Error: something is missing!", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this, "Error: timer already added!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                OutputStream os = getContentResolver().openOutputStream(uri);
                PrintStream ps = new PrintStream(os);
                ps.print("TITLE#"+editTextQuizName.getText().toString()+"#|"+quizString);
                ps.flush();
                ps.close();
                Toast.makeText(this, "File Saved!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("File process creation:", "Exception writing to " + uri.toString(), e);
            }
        }
    }
}

