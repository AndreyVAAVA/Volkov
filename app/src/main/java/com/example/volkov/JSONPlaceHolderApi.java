package com.example.volkov;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {
    /**
     * Allow you to get posts from http://developerslife.ru/ web page.
     * Only from latest, top and hot categories, otherwise use other methods.
     * @param section pass here what type of post do you want. Only latest, top or hot types.
     * @param pageNumber pass here number of page that you want to download.
     * @param isJson pass here how do you want to see it as JSON or as HTML.
     *               For parsing recommended to use true, because otherwise you must to change NetworkService class.
     * @return Posts represented in Call.
     */
    @GET("/{section}/{pageNumber}")
    public Call<Posts> getPostsWithType(@Path("section") String section, @Path("pageNumber") int pageNumber, @Query("json") boolean isJson);

    /**
     * Allow you to get post from http://developerslife.ru/ web page.
     * Only from random category, otherwise use other methods.
     * @param isJson pass here how do you want to see it as JSON or as HTML.
     *               For parsing recommended to use true, because otherwise you must to change NetworkService class.
     * @return Post represented in Call.
     */
    @GET("/random")
    public Call<Post> getRandomPost(@Query("json") boolean isJson);
}
