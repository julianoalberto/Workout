package com.example.workoutevo.model;

/**
 * Created by jalberto on 11/24/17.
 */

public class Step {
    public static final String MEDIA_TYPE_VIDEO = "video";
    public static final String MEDIA_TYPE_IMAGE = "image";

    private String mediaURI;
    private String mediaType;
    private int sequence;
    private String instructions;
    private String name;

    private int time;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getMediaURI() {
        return mediaURI;
    }

    public void setMediaURI(String mediaURI) {
        this.mediaURI = mediaURI;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
