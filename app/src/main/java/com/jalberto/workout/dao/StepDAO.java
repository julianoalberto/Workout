package com.jalberto.workout.dao;

import com.jalberto.workout.model.Step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Created by jalberto on 11/25/17.
 */

public class StepDAO {
    private InputStream inputStream;
    HashMap<Integer, Step> stepsMap;

    public static final String SEPARATOR = ",";
    public static final String COMMENT = "#";

    public StepDAO(InputStream inputStream) throws IOException
    {
        this.inputStream = inputStream;
        load();
    }

    private void load() throws IOException
    {
        stepsMap = new HashMap<Integer, Step>();
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        String line = "";
        StringTokenizer st = null;
        while ((line = reader.readLine()) != null)
        {
            if (!line.startsWith(COMMENT))
            {
                st = new StringTokenizer(line, SEPARATOR);
                int sequence = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                String instructions = st.nextToken();
                int time = Integer.parseInt(st.nextToken());
                String mediaURI = st.nextToken();
                String mediaType = st.nextToken();

                Step step = new Step();
                step.setSequence(sequence);
                step.setName(name);
                step.setInstructions(instructions);
                step.setTime(time);
                step.setMediaURI(mediaURI);
                step.setMediaType(mediaType);

                stepsMap.put(sequence, step);
            }
        }
    }

    public HashMap<Integer, Step> getAllSteps()
    {
        return stepsMap;
    }

    public TreeSet<Integer> stepsSequenceSet()
    {
        TreeSet<Integer> stepsSequenceSet = new TreeSet<Integer>();

        for (Integer sequence : stepsMap.keySet())
        {
            stepsSequenceSet.add(sequence);
        }

        return stepsSequenceSet;
    }

}
