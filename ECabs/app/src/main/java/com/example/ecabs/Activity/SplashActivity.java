package com.example.ecabs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecabs.R;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private float from;
    private float to;
    private Animation animation;
    private String ver = "version 1.0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progress_bar);

        TextView verTxt = findViewById(R.id.verTxt);

        verTxt.setText(ver);


        from = 0; // Set your desired initial progress value
        to = 100; // Set your desired final progress value

        animation = new ProgressBarAnimation(progressBar, from, to);
        animation.setDuration(2000); // Set the duration of the animation
        progressBar.startAnimation(animation);
    }

    private class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;

        private float from;
        private float to;

        ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            this.progressBar = progressBar;

            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);

            if (value == to) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish(); // Optional: finish the SplashActivity after starting the MainActivity
            }
        }
    }

}
