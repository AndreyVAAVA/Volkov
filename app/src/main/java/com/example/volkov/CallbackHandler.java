package com.example.volkov;

import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CallbackHandler {
    List<PostOptimized> posts = new ArrayList<>();
    private LinkParams postType;
    private View view;

    interface Callback{
        /**
         * Method for classes, that uses Callback classes.
         * This method load gif. Recommended to use only for activity start, otherwise use loading() method.
         * @param post pass here post, that you downloaded.
         * @see #loading(List, LinkParams)
         */
        void preLoading(Post post);
        /**
         * Method for classes, that uses Callback classes.
         * This method load gif. Recommended to use only for activity start, otherwise use loading() method.
         * @param loadedPosts pass here posts, that you downloaded.
         * @param postType pass here type of post that you downloaded.
         *                 It provides us ability to correctly increment number of page value.
         *                 (pageNumber variable)
         */
        void loading(List<PostOptimized> loadedPosts, LinkParams postType);
    }

    private Callback callback;

    /**
     * Pass here class that later will be called in other methods.
     * (right now it has been used in load() and preLoad() methods)
     * @param callback class, that implements Callback interface.
     */
    public void registerCallBack(Callback callback){
        this.callback = callback;
    }

    /**
     * This method initialize view, that will be used in other methods
     * to call some UI elements, such as Snackbar.
     * @param view pass here view of you activity/fragment.
     */
    public void initView(View view) {
        this.view = view;
    }

    /**
     * This is callback method, that load random Post and then calls preLoading() method
     * of class (that implements Callback interface) that previously was given into constructor.
     * Use it only for loading post while activity/fragment creates.
     * Before start view variable and callback variable must be initialized.
     * @see #registerCallBack(Callback)
     */
    public void preLoad(){
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

    /**
     * This is callback method, that load Posts into ArrayList and then calls loading() method
     * of class (that implements Callback interface) that previously was given into constructor.
     * Before start callback variable must be initialized. Recommended to initialize view variable.
     * @param kindOfPost pass here kindOfPost that you want to download.
     *                   (latest, top, hot, random)
     * @param pageNumber pass here number of page that you want to download from these categories:
     *                   latest, top, hot. If you want random post, then pass any number, that you want.
     * @see #initView(View)
     * @see #registerCallBack(Callback)
     */
    public void load(LinkParams kindOfPost, int pageNumber){
        if (callback != null) {
            posts.clear();
            this.postType = kindOfPost;
            JSONPlaceHolderApi api = NetworkService.getInstance().getJSONApi();
            if (postType == LinkParams.RANDOM) {
                api.getRandomPost(true).enqueue(new retrofit2.Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        Post randomPost = response.body();
                        posts.add(PostOptimized.builder().gifLink(randomPost.getGifURL())
                                .description(randomPost.getDescription()).previewURL(randomPost.getPreviewURL()).build());
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
                            if (view != null) {
                                Snackbar snackbar = Snackbar.make(view, "Эта категория не работает. Была загружена рандомная gif", Snackbar.LENGTH_SHORT);
                                snackbar.setAction("Close", v1 -> snackbar.dismiss()).show();
                            }
                        } else {
                            themAll.forEach(x -> {
                                posts.add(PostOptimized.builder().gifLink(x.getGifURL())
                                        .description(x.getDescription()).previewURL(x.getPreviewURL()).build());
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
}
