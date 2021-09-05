package com.example.volkov;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CallbackHandler {
    List<PostOptimized> posts = new ArrayList<>();
    private LinkParams postType;
    private View view;

    interface Callback{
        void preLoading(Post post);
        void loading(List<PostOptimized> loadedPosts, LinkParams typeOfPost);
    }

    private Callback callback;

    public void registerCallBack(Callback callback){
        this.callback = callback;
    }

    public void initView(View view) {
        this.view = view;
    }

    void preLoad(){
        NetworkService.getInstance()
                .getJSONApi()
                .getRandomPost(true)
                .enqueue(new retrofit2.Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Post preLoadPost = response.body();
                callback.preLoading(preLoadPost);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.d(TAG, "Error while getting JSON");
                Post errorPost = new Post();
                errorPost.setGifURL("error");
                callback.preLoading(errorPost);
            }
        });
    }
    void load(LinkParams kindOfPost, int pageNumber){
        posts.clear();
        this.postType = kindOfPost;
        JSONPlaceHolderApi api = NetworkService.getInstance().getJSONApi();
        if (postType == LinkParams.RANDOM) {
            api.getRandomPost(true).enqueue(new retrofit2.Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    Post randomPost = response.body();
                    posts.add(PostOptimized.builder().gifLink(randomPost.getGifURL())
                            .description(randomPost.getDescription()).build());
                    callback.loading(posts, postType);
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.d(TAG, "Error while getting JSON");
                    callback.loading(posts, postType);
                }
            });
        } else {
            Call<Posts> postsWithType = api.getPostsWithType(postType.getType(), pageNumber, true);
            postsWithType.enqueue(new retrofit2.Callback<Posts>() {
                @Override
                public void onResponse(Call<Posts> call, Response<Posts> response) {
                    List<Post> themAll = response.body().getResult();
                    if (themAll.size() == 0) {
                        LinkParams perm = postType;
                        postType = LinkParams.RANDOM;
                        load(LinkParams.RANDOM, pageNumber);
                        postType = perm;
                        Snackbar snackbar = Snackbar.make(view, "Эта категория не работает. Была загружена рандомная gif", Snackbar.LENGTH_SHORT);
                        snackbar.setAction("Close", v1 -> snackbar.dismiss()).show();
                    } else {
                        themAll.forEach(x -> {
                            posts.add(PostOptimized.builder().gifLink(x.getGifURL())
                                    .description(x.getDescription()).build());
                        });
                        callback.loading(posts, postType);
                    }
                }

                @Override
                public void onFailure(Call<Posts> call, Throwable t) {
                    Log.d(TAG, "Error while getting JSON");
                    callback.loading(posts, postType);
                }
            });
        }
    }
}
