package com.example.workoutevo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.media.MediaPlayer;

import android.net.Uri;
import android.provider.AlarmClock;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.workoutevo.dao.StepDAO;
import com.example.workoutevo.model.Step;



import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends Activity
{

    HashMap<Integer, Step> steps;
    Step currentStep;
    StepDAO stepDAO;

    VideoView stepVideoView;
    MediaController mediaController;
    ImageView stepImageView;
    Spinner stepsSpinner;
    ImageButton timerButton;
    ImageButton nextButton;
    ImageButton previousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI
        timerButton = (ImageButton) findViewById(R.id.timerButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        previousButton = (ImageButton) findViewById(R.id.previousButton);
        stepVideoView = (VideoView) findViewById(R.id.stepVideoView);
        mediaController= new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);
        stepVideoView.setMediaController(mediaController);
        stepVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                mp.setLooping(true);
            }
        });
        stepImageView = (ImageView) findViewById(R.id.stepImageView);
        stepsSpinner = (Spinner) findViewById(R.id.stepsSpinner);

        loadSteps();

        currentStep = steps.get(loadLastStep());

        createStepsSpinner();

        goToStep(currentStep);
    }

    private void createStepsSpinner()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, getStepsDescriptionList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stepsSpinner.setAdapter(adapter);

        stepsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID)
            {
                goToStep(steps.get(myPosition + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }

        });
    }

    private List<String> getStepsDescriptionList() {
        ArrayList<String> descriptionList = new ArrayList<String>();

        for (Integer sequence : stepDAO.stepsSequenceSet())
        {
            Step step = steps.get(sequence);
            descriptionList.add(String.format(getString(R.string.steps_label), step.getSequence(), steps.size(), step.getName()));
        }

        return descriptionList;
    }

    private void updateTimerButton(Step step)
    {
        if (step.getTime() > 0)
        {
            timerButton.setVisibility(View.VISIBLE);
            timerButton.setEnabled(true);
            timerButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    startTimer(view);
                }
            });
        }
        else
        {
            timerButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateInstructionsTextView(Step step)
    {
        TextView instructionsTextView = (TextView) findViewById(R.id.instructionsTextView);
        instructionsTextView.setText(step.getInstructions());
    }

    private void goToStep(Step step)
    {
        updateInstructionsTextView(step);
        updateTimerButton(step);
        displayMedia(step);
        updateSpinner(step);
        updatePreviousButton(step);
        updateNextButton(step);
        currentStep = step;
    }

    private void updateNextButton(Step step)
    {
        if (step.getSequence() == steps.size())
        {
            nextButton.setImageResource(R.drawable.ic_home_white_48dp);
        }
        else
        {
            nextButton.setImageResource(R.drawable.ic_arrow_forward_white_48dp);
        }
    }

    private void updatePreviousButton(Step step)
    {
        if (step.getSequence() == 1)
        {
            previousButton.setEnabled(false);
        }
        else
        {
            previousButton.setEnabled(true);
        }
    }

    private void updateSpinner(Step step)
    {
        stepsSpinner.setSelection(step.getSequence() - 1);
    }

    public void nextStep(View view)
    {
        int nextStep = currentStep.getSequence() + 1;
        if (nextStep < steps.size() + 1)
        {
            currentStep = steps.get(nextStep);
            goToStep(currentStep);
        }
        else
        {
            currentStep = steps.get(1);
            goToStep(currentStep);
        }
    }

    public void previousStep(View view)
    {
        int previousStep = currentStep.getSequence() - 1;
        if (previousStep > 0)
        {
            currentStep = steps.get(previousStep);
            goToStep(currentStep);
        }
    }

    public void startTimer(View view)
    {
        timerButton.setEnabled(false);

        int seconds = currentStep.getTime();
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void loadSteps()
    {
        try {
            InputStream in = getResources().openRawResource(R.raw.steps);
            stepDAO = new StepDAO(in);
            steps = stepDAO.getAllSteps();
        } catch (IOException e) {
            Log.e("ERROR", "Error loading steps: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayImage(Step step)
    {
        stepImageView.setImageResource(getResources().getIdentifier(step.getMediaURI(), "raw", this.getPackageName()));
        stepImageView.setVisibility(View.VISIBLE);
    }

    private void displayVideo(Step step)
    {
        int videoResourceId = getResources().getIdentifier(step.getMediaURI(), "raw", this.getPackageName());
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+videoResourceId);
        stepVideoView.setVideoURI(uri);
        stepVideoView.requestFocus();
        stepVideoView.setVisibility(View.VISIBLE);
        stepVideoView.start();
    }

    private void hideVideo()
    {
        stepVideoView.stopPlayback();
        stepVideoView.setVisibility(View.INVISIBLE);
    }

    private void hideImage()
    {
        stepImageView.setVisibility(View.INVISIBLE);
    }

    private void displayMedia(Step step)
    {
        if (step.getMediaType().equals(Step.MEDIA_TYPE_IMAGE))
        {
            hideVideo();
            displayImage(step);
        }
        else if (step.getMediaType().equals(Step.MEDIA_TYPE_VIDEO))
        {
            hideImage();
            displayVideo(step);
        }
    }

    private int loadLastStep()
    {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        return preferences.getInt(getString(R.string.last_step), 1);
    }

    private void saveLastStep()
    {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.last_step), currentStep.getSequence());
        editor.commit();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saveLastStep();
    }
}
