package com.example.volkov;

import androidx.annotation.NonNull;
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
import com.example.volkov.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private boolean isLost = false;
    private String selectCategory = "Latest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.forward.setOnClickListener(v -> {
            binding.gifHolder.setVisibility(ImageView.INVISIBLE);
            binding.progressCircular.setVisibility(ProgressBar.VISIBLE);
            Glide.with(this).asBitmap().load("https://static.devli.ru/public/images/previews/202009/3c2dbbe9-da67-4df3-8790-0fa3d995ceeb.jpg").into(binding.gifHolder);
            binding.gifHolder.setVisibility(ImageView.VISIBLE);
            Snackbar.make(v, "forward", Snackbar.LENGTH_SHORT).show();
        });
        binding.back.setOnClickListener(v -> {
            Glide.with(this).asGif()
                    .load("https://static.devli.ru/public/images/gifs/202009/3c2dbbe9-da67-4df3-8790-0fa3d995ceeb.gif")
                    .error(R.drawable.ic_baseline_cloud_queue_24)
                    .into(binding.gifHolder);
            binding.progressCircular.setVisibility(ProgressBar.INVISIBLE);
            //binding.gifHolder.setVisibility(ImageView.VISIBLE);
            Snackbar.make(v, "back", Snackbar.LENGTH_SHORT).show();
        });
        binding.categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Log.e(TAG, "The default network is now: " + network);
                if (isLost) {
                    Snackbar.make(view, "Connection established", Snackbar.LENGTH_LONG).show();
                    isLost = false;
                }
            }

            @Override
            public void onLost(Network network) {
                Log.e(TAG, "The application no longer has a default network. The last default network was " + network);
                isLost = true;
            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                Log.e(TAG, "The default network changed capabilities: " + networkCapabilities);
            }

            @Override
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                Log.e(TAG, "The default network changed link properties: " + linkProperties);
            }
        });
        Glide.with(this).asGif()
                .load("http://static.devli.ru/public/images/gifs/201303/db650743-ae9a-4639-9807-3e3de92f36da.gif")
                .error(R.drawable.ic_baseline_cloud_queue_24)
                .into(binding.gifHolder);
        binding.textOfGif.setText("Crazy stuff");
        NetworkService.getInstance()
                .getJSONApi()
                .getRandomPost(true)
                .enqueue(new Callback<RandomPost>() {
                    @Override
                    public void onResponse(Call<RandomPost> call, Response<RandomPost> response) {
                        RandomPost post = response.body();
                        Snackbar.make(view, post.getGifURL(), Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<RandomPost> call, Throwable t) {
                        Snackbar.make(view, "Error", Snackbar.LENGTH_SHORT).show();
                    }
                });

    }
}