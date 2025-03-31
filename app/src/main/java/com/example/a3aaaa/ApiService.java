package com.example.a3aaaa;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("https://mocki.io/v1/284961d8-b054-41d9-a1ac-a6bbc75387ed") //  JSON
    Call<ExerciseData> getExerciseData();
}
