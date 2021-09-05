package com.example.volkov;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {
    @GET("/{section}/{pageNumber}")
    public Call<Posts> getPostsWithType(@Path("section") String section, @Path("pageNumber") int pageNumber, @Query("json") boolean isJson);

    @GET("/random")
    public Call<Post> getRandomPost(@Query("json") boolean isJson);
}
