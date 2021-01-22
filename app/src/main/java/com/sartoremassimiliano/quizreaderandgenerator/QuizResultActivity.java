package com.sartoremassimiliano.quizreaderandgenerator;

import android.content.Intent;
import android.os.Bundle ;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class QuizResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent ActIntent = getIntent();
        setContentView(R.layout.quiz_result);
        LottieAnimationView laptopWorking = findViewById(R.id.laptopWorking);
        LottieAnimationView backToSchoolAnimation = findViewById(R.id.backToSchool);
        LottieAnimationView partyAnimation = findViewById(R.id.danceParty);
        TextView textViewResult = findViewById(R.id.textViewResult);
        LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        float result =Float.parseFloat(ActIntent.getStringExtra("result"));
        float quizN = Float.parseFloat(ActIntent.getStringExtra("quizN"));
        double percentage = result/quizN*100.0;
        if (percentage>60.0)
        {
            backToSchoolAnimation.setVisibility(View.GONE);
            laptopWorking.setVisibility(View.GONE);
            partyAnimation.setVisibility(View.VISIBLE);
            textViewResult.setText("Well done!\nScore:" + Integer.toString((int) Math.round((result/quizN)*100)) +"/100");
        }
        else
        {
            partyAnimation.setVisibility(View.GONE);
            if(percentage<40.0) {
                laptopWorking.setVisibility(View.GONE);
                backToSchoolAnimation.setVisibility(View.VISIBLE);
                textViewResult.setText("You have to study more!\nScore:" + Integer.toString((int) Math.round((result / quizN) * 100)) + "/100");
            }
            else
            {
                backToSchoolAnimation.setVisibility(View.GONE);
                laptopWorking.setVisibility(View.VISIBLE);
                textViewResult.setText("Keep working hard!\nScore:" + Integer.toString((int) Math.round((result / quizN) * 100)) + "/100");
            }
        }
        textViewResult.setLayoutParams(layoutParams);
        textViewResult.setTextSize(getResources().getDimension(R.dimen.textsize));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finishAndRemoveTask();
    }

    @Override
    public void onBackPressed()
    {
        Toast.makeText(getApplicationContext(), "You can't go back", Toast.LENGTH_LONG).show();
    }

    public void onClickCloseQuiz(View v)
    {
        this.finish();
    }
}

