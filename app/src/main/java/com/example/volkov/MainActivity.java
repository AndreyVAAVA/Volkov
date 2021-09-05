package com.example.volkov;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.volkov.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CallbackHandler.Callback{
    private ActivityMainBinding binding;
    private LinkParams postType = LinkParams.LATEST;
    private int postNumber = 0;
    private int pageNumber = 0;
    private int pageNumberLatest = 0;
    private int pageNumberTop = 0;
    private int pageNumberHot = 0;
    private ArrayList<PostOptimized> posts = new ArrayList<>();
    CallbackHandler handler = new CallbackHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.forward.setOnClickListener(v -> {
            binding.progressCircular.setVisibility(ProgressBar.VISIBLE);
            binding.gifHolder.setVisibility(ImageView.INVISIBLE);
            binding.forward.setEnabled(false);
            if (postNumber == posts.size()) {
                handler.registerCallBack(this);
                handler.load(postType, pageNumber);
            } else {
                Glide.with(this).asGif()
                        .load(posts.get(postNumber).getGifLink())
                        .listener(new RequestListener<GifDrawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                binding.progressCircular.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.progressCircular.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        })
                        .error(R.drawable.ic_baseline_cloud_queue_24)
                        .into(binding.gifHolder);
                binding.textOfGif.setText(posts.get(postNumber).getDescription());
                postNumber++;
                binding.gifHolder.setVisibility(ImageView.VISIBLE);
                binding.forward.setEnabled(true);
            }
        });
        binding.back.setOnClickListener(v -> {
            if (postNumber > 0) {
                binding.progressCircular.setVisibility(ProgressBar.VISIBLE);
                binding.gifHolder.setVisibility(ImageView.INVISIBLE);
                postNumber--;
                Glide.with(this).asGif()
                        .load(posts.get(postNumber).getGifLink())
                        .listener(new RequestListener<GifDrawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                binding.progressCircular.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.progressCircular.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        })
                        .error(R.drawable.ic_baseline_cloud_queue_24)
                        .into(binding.gifHolder);
                binding.textOfGif.setText(posts.get(postNumber).getDescription());
                binding.gifHolder.setVisibility(ImageView.VISIBLE);
            }
        });
        binding.categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabText = tab.getText().toString();
                if (postType == LinkParams.LATEST) pageNumberLatest = pageNumber;
                else if (postType == LinkParams.TOP) pageNumberTop = pageNumber;
                else if (postType == LinkParams.HOT) pageNumberHot = pageNumber;
                if (tabText.equals(getString(R.string.latest))) {
                    postType = LinkParams.LATEST;
                    pageNumber = pageNumberLatest;
                }
                else if (tabText.equals(getString(R.string.top))) {
                    postType = LinkParams.TOP;
                    pageNumber = pageNumberTop;
                }
                else if (tabText.equals(getString(R.string.hot))) {
                    postType = LinkParams.HOT;
                    pageNumber = pageNumberHot;
                } else {
                    postType = LinkParams.RANDOM;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        handler.registerCallBack(this);
        handler.preLoad();
        handler.initView(view);
    }

    @Override
    public void preLoading(Post post) {
        if (post.getGifURL().equals("error")) {
            binding.gifHolder.setImageResource(R.drawable.ic_baseline_cloud_queue_24);
            binding.textOfGif.setText("Произошла ошибка при загрузке данных, проверьте подключение к сети");
        } else {
            posts.add(PostOptimized.builder().gifLink(post.getGifURL()).description(post.getDescription()).build());
            Glide.with(this).asGif()
                    .load(post.getGifURL())
                    .error(R.drawable.ic_baseline_cloud_queue_24)
                    .into(binding.gifHolder);
            binding.textOfGif.setText(post.getDescription());
            postNumber++;
        }
        binding.progressCircular.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void loading(List<PostOptimized> loadedPosts, LinkParams postType) {
        if (postType != LinkParams.RANDOM) pageNumber++;
        posts.addAll(loadedPosts);
        if (posts.size() == postNumber) {
            binding.progressCircular.setVisibility(View.INVISIBLE);
            binding.gifHolder.setImageResource(R.drawable.ic_baseline_cloud_queue_24);
            binding.textOfGif.setText("Произошла ошибка при загрузке данных, проверьте подключение к сети");
        } else {
            Glide.with(this).asGif()
                    .load(posts.get(postNumber).getGifLink())
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            binding.progressCircular.setVisibility(View.INVISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.progressCircular.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .error(R.drawable.ic_baseline_cloud_queue_24)
                    .into(binding.gifHolder);

            binding.textOfGif.setText(posts.get(postNumber).getDescription());
            postNumber++;
        }
        binding.gifHolder.setVisibility(ImageView.VISIBLE);
        binding.forward.setEnabled(true);
    }
}