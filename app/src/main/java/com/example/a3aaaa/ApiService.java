package com.example.a3aaaa;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("v1/ad347c6f-a5fd-414d-976f-3490f9522df7")
    Call<ExerciseData> getExerciseData();
}

