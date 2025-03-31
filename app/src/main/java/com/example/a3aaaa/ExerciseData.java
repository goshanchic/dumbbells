package com.example.a3aaaa;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExerciseData {
    @SerializedName("exercise_name")
    private String exerciseName;

    @SerializedName("ideal_coordinates")
    private List<Float> idealCoordinates;

    @SerializedName("threshold")
    private float threshold;

    public String getExerciseName() {
        return exerciseName;
    }

    public List<Float> getIdealCoordinates() {
        return idealCoordinates;
    }

    public float getThreshold() {
        return threshold;
    }
}
