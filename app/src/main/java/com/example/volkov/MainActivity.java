package com.example.volkov;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.volkov.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private boolean isLost = false;
    private LinkParams postType = LinkParams.LATEST;
    private int postNumber = 0;
    private int pageNumber = 0;
    private int pageNumberLatest = 0;
    private int pageNumberTop = 0;
    private int pageNumberHot = 0;
    private boolean isCategoryWorking = true;
    private ArrayList<PostOptimized> posts = new ArrayList<>();
    JSONPlaceHolderApi api = NetworkService.getInstance().getJSONApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.forward.setOnClickListener(v -> {
            binding.progressCircular.setVisibility(ProgressBar.VISIBLE);
            binding.gifHolder.setVisibility(ImageView.INVISIBLE);
            if (!isCategoryWorking && postNumber == posts.size()-1) {
                isCategoryWorking = true;
                Snackbar snackbar = Snackbar.make(view, "Эта категория не работает. Была загружена рандомная gif", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Close", v1 -> snackbar.dismiss()).show();
                getNewPosts();
            }
            if (postNumber == posts.size() - 4) getNewPosts();
            if (postNumber == posts.size()) {
                binding.gifHolder.setImageResource(R.drawable.ic_baseline_cloud_queue_24);
                binding.textOfGif.setText("Произошла ошибка при загрузке данных, проверьте подключение к сети");
                getNewPosts();
            } else {
                Glide.with(this).asGif()
                        .load(posts.get(postNumber).getGifLink())
                        .error(R.drawable.ic_baseline_cloud_queue_24)
                        .into(binding.gifHolder);
                binding.textOfGif.setText(posts.get(postNumber).getDescription());
                if (!isCategoryWorking) {

                }
                postNumber++;
            }

            binding.progressCircular.setVisibility(ProgressBar.INVISIBLE);
            binding.gifHolder.setVisibility(ImageView.VISIBLE);
            //Snackbar.make(v, "forward", Snackbar.LENGTH_SHORT).show();
        });
        binding.back.setOnClickListener(v -> {
            if (postNumber > 0) {
                binding.progressCircular.setVisibility(ProgressBar.VISIBLE);
                binding.gifHolder.setVisibility(ImageView.INVISIBLE);
                postNumber--;
                Glide.with(this).asGif()
                        .load(posts.get(postNumber).getGifLink())
                        .error(R.drawable.ic_baseline_cloud_queue_24)
                        .into(binding.gifHolder);
                binding.textOfGif.setText(posts.get(postNumber).getDescription());
                binding.progressCircular.setVisibility(ProgressBar.INVISIBLE);
                binding.gifHolder.setVisibility(ImageView.VISIBLE);
            }
            //Snackbar.make(v, "back", Snackbar.LENGTH_SHORT).show();
        });
        binding.categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabText = tab.getText().toString();
                if (postType == LinkParams.LATEST) pageNumberLatest = pageNumber;
                else if (postType == LinkParams.HOT) pageNumberLatest = pageNumber;
                else pageNumberHot = pageNumber;
                if (tabText.equals(getString(R.string.latest))) {
                    postType = LinkParams.LATEST;
                    pageNumber = pageNumberLatest;
                }
                else if (tabText.equals(getString(R.string.top))) {
                    postType = LinkParams.TOP;
                    pageNumber = pageNumberTop;
                }
                else {
                    postType = LinkParams.HOT;
                    pageNumber = pageNumberHot;
                }
                Snackbar.make(view, postType.getType(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //getNewPosts();
    }

    public void getNewPosts() {
         if (postType == LinkParams.RANDOM) {
             api.getRandomPost(true).enqueue(new Callback<Post>() {
                 @Override
                 public void onResponse(Call<Post> call, Response<Post> response) {
                     Post post = response.body();
                     posts.add(PostOptimized.builder().gifLink(post.getGifURL()).description(post.getDescription()).build());
                 }

                 @Override
                 public void onFailure(Call<Post> call, Throwable t) {
                     Log.d(TAG, "Error while getting JSON");
                 }
             });
         } else {
             Call<Posts> postsWithType = api.getPostsWithType(postType.getType(), pageNumber, true);
             postsWithType.enqueue(new Callback<Posts>() {
                 @Override
                 public void onResponse(Call<Posts> call, Response<Posts> response) {
                     pageNumber++;
                     List<Post> themAll = response.body().getResult();
                     if (themAll.size() == 0) {
                         LinkParams perm = postType;
                         postType = LinkParams.RANDOM;
                         getNewPosts();
                         isCategoryWorking = false;
                         postType = perm;
                         pageNumber--;
                     }
                     themAll.forEach(x -> {
                         posts.add(PostOptimized.builder().gifLink(x.getGifURL()).description(x.getDescription()).build());
                     });
                 }

                 @Override
                 public void onFailure(Call<Posts> call, Throwable t) {
                     Log.d(TAG, "Error while getting JSON");
                 }
             });
         }
    }
}