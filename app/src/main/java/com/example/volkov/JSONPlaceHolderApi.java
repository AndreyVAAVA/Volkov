package com.example.volkov;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {
    @GET("/{section}/{pageNumber}")
    public Call<List<Posts>> getPostsWithType(@Path("section") String section, @Path("pageNumber") String pageNumber, @Query("json") boolean isJson);

    @GET("/random")
    public Call<RandomPost> getRandomPost(@Query("json") boolean isJson);
}
