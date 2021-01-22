package com.sartoremassimiliano.quizreaderandgenerator;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class QuizReaderActivity extends AppCompatActivity {

    private Uri filepath;
    private String commands;
    private LinearLayout.LayoutParams layoutParams;
    private int quizN;
    private int result;
    private List<Quiz> list;
    private TextView textViewTimer;
    private int secondTimer;
    private boolean timer;
    private int secondTimerStart;
    private CountDownTimer cTimer;

    private class Quiz
    {
        private int type;
        private CheckBox checkBox1;
        private CheckBox checkBox2;
        private CheckBox checkBox3;
        private RadioButton rdYes;
        private RadioButton rdNo;
        private int result;
        private String answer;
        private EditText editTextCQA;
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_reader);
        timer = false;
        result=0;
        quizN = 0;
        TextView textViewQuizName = findViewById(R.id.textViewQuizTitle);
        textViewTimer = findViewById(R.id.textViewTimer);
        Intent ActIntent = getIntent();
        LinearLayout linearLayout = findViewById(R.id.LinearLayoutReader);
        filepath = Uri.parse(ActIntent.getStringExtra("filepath"));
        commands = readFromFile();
        layoutParams  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        list=new ArrayList<>();
        String tmp = "";
        try
        {
            for (int j=1; j<commands.length()-1; j++)
            {
                if (commands.charAt(j)=='|' || j==commands.length()-2)
                {
                    j++;
                    StringTokenizer tokens = new StringTokenizer(tmp, "#");
                    switch(tokens.nextToken()) {
                        case "TITLE":
                            textViewQuizName.setText(tokens.nextToken());
                            break;
                        case "QYN":
                            TextView textViewQYN = new TextView(this);
                            textViewQYN.setText(tokens.nextToken());
                            textViewQYN.setLayoutParams(layoutParams);
                            textViewQYN.setTextSize(getResources().getDimension(R.dimen.textsize));
                            linearLayout.addView(textViewQYN);
                            Quiz qynQuestion = new Quiz();
                            RadioGroup rg = new RadioGroup(this);
                            qynQuestion.rdYes = new RadioButton(this);
                            qynQuestion.rdNo = new RadioButton(this);
                            qynQuestion.rdYes.setText("Yes");
                            qynQuestion.rdNo.setText("No");
                            rg.addView(qynQuestion.rdYes);
                            rg.addView(qynQuestion.rdNo);
                            rg.setLayoutParams(layoutParams);
                            linearLayout.addView(rg);
                            qynQuestion.type = 1;
                            if (Integer.parseInt(tokens.nextToken()) == 0){
                                qynQuestion.result = 0;
                            }
                            else {
                                qynQuestion.result = 1;
                            }
                            list.add(qynQuestion);
                            quizN++;
                            break;
                        case "MCQ":
                            TextView textViewMCQ = new TextView(this);
                            textViewMCQ.setText(tokens.nextToken());
                            textViewMCQ.setLayoutParams(layoutParams);
                            textViewMCQ.setTextSize(getResources().getDimension(R.dimen.textsize));
                            linearLayout.addView(textViewMCQ);
                            Quiz mcqQuestion = new Quiz();
                            mcqQuestion.checkBox1 = new CheckBox(this);
                            mcqQuestion.checkBox2 = new CheckBox(this);
                            mcqQuestion.checkBox3 = new CheckBox(this);
                            mcqQuestion.result = Integer.parseInt(tokens.nextToken());
                            mcqQuestion.checkBox1.setText(tokens.nextToken());
                            mcqQuestion.checkBox2.setText(tokens.nextToken());
                            mcqQuestion.checkBox3.setText(tokens.nextToken());
                            linearLayout.addView(mcqQuestion.checkBox1);
                            linearLayout.addView(mcqQuestion.checkBox2);
                            linearLayout.addView(mcqQuestion.checkBox3);
                            mcqQuestion.type = 2;
                            list.add(mcqQuestion);
                            quizN++;
                            break;
                        case "CQ":
                            TextView textViewCQ = new TextView(this);
                            textViewCQ.setText(tokens.nextToken());
                            textViewCQ.setLayoutParams(layoutParams);
                            textViewCQ.setTextSize(getResources().getDimension(R.dimen.textsize));
                            Quiz cqQuestion = new Quiz();
                            cqQuestion.editTextCQA = new EditText(this);
                            cqQuestion.editTextCQA.setLayoutParams(layoutParams);
                            cqQuestion.answer = tokens.nextToken();
                            cqQuestion.type = 3;
                            linearLayout.addView(textViewCQ);
                            linearLayout.addView(cqQuestion.editTextCQA);
                            list.add(cqQuestion);
                            quizN++;
                            break;
                        case "TIMER":
                            secondTimerStart = secondTimer = Integer.parseInt(tokens.nextToken());
                            timer = true;
                            Log.i("rr", secondTimer+" ");
                            int p1 = secondTimer % 60;
                            int p2 = secondTimer / 60;
                            int p3 = p2 % 60;
                            p2 = p2 / 60;
                            textViewTimer.setText( LocalTime.of(p3,p2,p1).toString());
                            break;
                        default:
                            exitFun("Error: file corrupted");
                            break;
                    }
                    tmp = "";
                    if(j==commands.length()-2)
                        break;
                }
                tmp += commands.charAt(j);
            }
            if(quizN==0)
            {
                exitFun("Error: empty quiz!");
            }
        }catch (Exception ex)
        {
            exitFun("Error: file corrupted");
            Log.i("tag","e",ex);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(timer) {
            cTimer = new CountDownTimer((secondTimerStart+1)*1000, 1000) {

                @RequiresApi(api = Build.VERSION_CODES.O)
                public void onTick(long millisUntilFinished) {
                    secondTimer--;
                    if(secondTimer>=1) {
                        timer=true;
                        if (secondTimer < secondTimerStart / 4) {
                            textViewTimer.setTextColor(Color.RED);
                        }
                        int p1 = secondTimer % 60;
                        int p2 = secondTimer / 60;
                        int p3 = p2 % 60;
                        p2 = p2 / 60;
                        Log.i("tag", p1 + " " + p2 + " " + p3 + " ");
                        textViewTimer.setText(LocalTime.of(p3, p2, p1).toString());
                    }
                    else
                    {
                        textViewTimer.setText("00:00:00");
                    }
                }
                public void onFinish() {
                }
            }.start();
        }
    }


    public void onClickResult(View v)
    {
        closeTimer();
        result();
    }

    public void onClickClose(View v)
    {
        closeTimer();
        this.finish();
    }

    private void exitFun(String toastText)
    {
        closeTimer();
        Toast.makeText(getApplicationContext(), toastText , Toast.LENGTH_LONG).show();
        this.finish();
    }

    private void closeTimer()
    {
        if(timer)
        {
            cTimer.onFinish();
        }
    }

    private void result()
    {
        result = 0;
        quizN = 0;
        for (int i=0; i<list.size();i++)
        {
            switch (list.get(i).type)
            {
                case 1:
                    quizN++;
                    if (list.get(i).rdYes.isChecked() && list.get(i).result==1)
                    {
                        result++;
                    }
                    else
                    {
                        if (list.get(i).rdNo.isChecked() && list.get(i).result==0)
                        {
                            result++;
                        }
                    }
                    break;
                case 2:
                    quizN++;
                    switch (list.get(i).result)
                    {
                        case 1:
                            if (list.get(i).checkBox1.isChecked() && !list.get(i).checkBox2.isChecked() && !list.get(i).checkBox3.isChecked())
                            {
                                result++;
                            }
                            break;
                        case 2:
                            if (!list.get(i).checkBox1.isChecked() && list.get(i).checkBox2.isChecked() && !list.get(i).checkBox3.isChecked())
                            {
                                result++;
                            }
                            break;
                        case 3:
                            if (list.get(i).checkBox1.isChecked() && list.get(i).checkBox2.isChecked() && !list.get(i).checkBox3.isChecked())
                            {
                                result++;
                            }
                            break;
                        case 4:
                            if (!list.get(i).checkBox1.isChecked() && !list.get(i).checkBox2.isChecked() && list.get(i).checkBox3.isChecked())
                            {
                                result++;
                            }
                            break;
                        case 5:
                            if (list.get(i).checkBox1.isChecked() && !list.get(i).checkBox2.isChecked() && list.get(i).checkBox3.isChecked())
                            {
                                result++;
                            }
                            break;
                        case 6:
                            if (!list.get(i).checkBox1.isChecked() && list.get(i).checkBox2.isChecked() && list.get(i).checkBox3.isChecked())
                            {
                                result++;
                            }
                            break;
                        case 7:
                            if (list.get(i).checkBox1.isChecked() && list.get(i).checkBox2.isChecked() && list.get(i).checkBox3.isChecked())
                            {
                                result++;
                            }
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "Error: something is missing!", Toast.LENGTH_LONG).show();
                            return;
                    }
                    break;
                case 3:
                    quizN++;
                    if(list.get(i).answer.toLowerCase().equals(list.get(i).editTextCQA.getText().toString().toLowerCase()))
                    {
                        result++;
                    }
                    break;
            }

        }
        Intent MyIntent = new Intent(this, QuizResultActivity.class);
        MyIntent.putExtra("result",Integer.toString(result));
        MyIntent.putExtra("quizN",Integer.toString(quizN));
        startActivity(MyIntent);
        this.finish();
    }

    private String readFromFile()
    {
        String ret = "";
        try
        {
            InputStream inputStream = getContentResolver().openInputStream(filepath);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
}
