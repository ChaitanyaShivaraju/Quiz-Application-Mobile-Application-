package com.techpassappmaster.realquiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Play_Activity extends AppCompatActivity {

    private TextView tv_score, tv_noOfQues, tvTime, tv_question;
    private Button next_button;
    private RadioGroup radio_g;
    private RadioButton rb1, rb2, rb3, rb4;

    //    timer
    private CountDownTimer countDownTimer;
    public static final long COUNT_DOWN_IN_MILLIS = 30000;
    private static final long COUNT_DOWN_INTERVAL = 1000;
    private long timeLeft_milliseconds;
    private ColorStateList default_color;

    //    ads
    private FrameLayout adContainerView;
    private AdView adView;

    //    ques & and
    public static int score = 0, correct = 0, wrong = 0, skip = 0;
    int qIndex = 0;
    int update_que_no = 1;

    //    String array for Ques, ans, options
    String[] questions =
            {
                    "Q.1. If a computer has more than one processor then it is known as?",
                    "Q.2. Full form of URL is?",
                    "Q.3. One kilobyte (KB) is equal to",
                    "Q.4. Father of ‘C’ programming language?",
                    "Q.5. SMPS stands for",
                    "Q.6. What is a floppy disk used for",
                    "Q.7. Which operating system is developed and used by Apple Inc?",
                    "Q.8. Random Access Memory (RAM) is which storage of device?",
                    "Q.9. Who is the founder of the Internet?",
                    "Q.10. Which one is the first search engine in internet?",
            };
    String[] answers =
            {
                    "Multiprocessor",
                    "Uniform Resource Locator",
                    "1,024 bytes",
                    "Dennis Ritchie",
                    "Switched mode power supply",
                    "To store information",
                    "iOS",
                    "Primay",
                    "Tim Berners-Lee",
                    "Archie",
            };

    String[] opt = {
            "Uniprocess", "Multiprocessor", "Multithreaded", "Multiprogramming",
            "Uniform Resource Locator", "Uniform Resource Linkwrong", "Uniform Registered Link", "Unified Resource Link",
            "1,000 bits", "1,024 bytes", "1,024 megabytes", "1,024 gigabytes",
            "Dennis Ritchie", "Prof Jhon Kemeny", "Thomas Kurtz", "Bill Gates",
            "Switched mode power supply", "Start mode power supply", "Store mode power supply", "Single mode power supply",
            "To unlock the computer", "To store information", "To erase the computer screen", "To make the printer work",
            "Windows", "Android", "iOS", "UNIX",
            "Primay", "Secondary", "Teriary", "Off line",
            "Vint Cerf", "Charles Babbage", "Tim Berners-Lee", "None of these",
            "Google", "Archie", "Altavista", "WAIS",
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initializeViews();


        next_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (radio_g.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(Play_Activity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                } else {
                    showNextQue();
                }

            }

        });
    }

    @SuppressLint("SetTextI18n")
    private void initializeViews() {
        tv_score = findViewById(R.id.score);
        tv_noOfQues = findViewById(R.id.tv_noOfQues);

        tvTime = findViewById(R.id.txtTime);
        next_button = findViewById(R.id.button3);
        tv_question = findViewById(R.id.tv_que);

        radio_g = findViewById(R.id.answersgrp);
        rb1 = findViewById(R.id.radioButton);
        rb2 = findViewById(R.id.radioButton2);
        rb3 = findViewById(R.id.radioButton3);
        rb4 = findViewById(R.id.radioButton4);

        tv_noOfQues.setText(update_que_no + "/10");
        tv_question.setText(questions[qIndex]);

        rb1.setText(opt[0]);
        rb2.setText(opt[1]);
        rb3.setText(opt[2]);
        rb4.setText(opt[3]);

        default_color = tvTime.getTextColors();


        timeLeft_milliseconds = COUNT_DOWN_IN_MILLIS;

        startCountDown();

    }

    @SuppressLint("SetTextI18n")
    private void showNextQue() {

        checkAnswer();

        if (update_que_no < 10) {
            tv_noOfQues.setText(update_que_no + 1 + "/10");
            update_que_no++;
        }

        if (qIndex < questions.length) {

            tv_question.setText(questions[qIndex]);

            rb1.setText(opt[qIndex * 4]);
            rb2.setText(opt[qIndex * 4 + 1]);
            rb3.setText(opt[qIndex * 4 + 2]);
            rb4.setText(opt[qIndex * 4 + 3]);

        } else {
            score = correct;
            Intent in = new Intent(getApplicationContext(), Result_Activity.class);
            in.putExtra("isKey", true);
            startActivity(in);
            finish();
        }

        radio_g.clearCheck();

    }

    @SuppressLint("SetTextI18n")
    private void checkAnswer() {

        if (radio_g.getCheckedRadioButtonId() == -1) {
            skip++;
            timeOver_Dialog();
        }
        else {

            RadioButton checkedRadioButton = findViewById(radio_g.getCheckedRadioButtonId());
            String checkAnswer = checkedRadioButton.getText().toString();

            if (checkAnswer.equals(answers[qIndex])) {
                correct++;
                tv_score.setText("Score :-" + correct);
                correct_Dialog();
                countDownTimer.cancel();
            } else {
                wrong++;
                wrong_Dialog();
                countDownTimer.cancel();
            }
        }

        qIndex++;

    }

    private void startCountDown() {
//        String timeFormat = String.format(Locale.getDefault(), "Time : %02d", second);
//        int second = (int) (timeLeftInMilis / 1000) % 60;

        countDownTimer = new CountDownTimer(timeLeft_milliseconds, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeLeft_milliseconds = millisUntilFinished;

                int second = (int) TimeUnit.MILLISECONDS.toSeconds(timeLeft_milliseconds);

//              %02d  format the integer with 2 digit
                String timeFormat = String.format(Locale.getDefault(), "Time : %02d", second);

                tvTime.setText(timeFormat);

                if (timeLeft_milliseconds < 10000) {
                    tvTime.setTextColor(Color.RED);
                } else {
                    tvTime.setTextColor(default_color);
                }

            }

            @Override
            public void onFinish() {

                showNextQue();
//                timeLeftInMilis = 0;
//                updateCountDownTxt();
            }
        }.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDownTimer.cancel();
    }

  /*  @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
  /*  @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    public void correct_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Play_Activity.this);
        View view = LayoutInflater.from(Play_Activity.this).inflate(R.layout.correct_dialoag, null);
        builder.setView(view);

        Button button = view.findViewById(R.id.correct_ok);
        TextView tvDialog_score = view.findViewById(R.id.tvDialog_score);

        final AlertDialog alertDialog = builder.create();


        tvDialog_score.setText("Score: " + correct);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startCountDown();
                timeLeft_milliseconds = COUNT_DOWN_IN_MILLIS;
                alertDialog.dismiss();
            }
        });

        alertDialog.show();


    }

    public void wrong_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Play_Activity.this);
        View view;
        view = LayoutInflater.from(Play_Activity.this).inflate(R.layout.wrong_dialog, null);
        builder.setView(view);

        Button button = view.findViewById(R.id.wrong_ok);
        TextView tv_wrongDialog_correctAns = view.findViewById(R.id.tv_wrongDialog_correctAns);

        final AlertDialog alertDialog = builder.create();

        tv_wrongDialog_correctAns.setText("Correct Ans: " + answers[qIndex]);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeLeft_milliseconds = COUNT_DOWN_IN_MILLIS;
                startCountDown();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    public void timeOver_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Play_Activity.this);
        View view;
        view = LayoutInflater.from(Play_Activity.this).inflate(R.layout.time_over_dialog, null);
        builder.setView(view);

        Button button = view.findViewById(R.id.timeOver_ok);

        final AlertDialog alertDialog = builder.create();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeLeft_milliseconds = COUNT_DOWN_IN_MILLIS;
                startCountDown();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
}
