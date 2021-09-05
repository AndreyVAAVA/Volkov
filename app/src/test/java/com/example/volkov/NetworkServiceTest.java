package com.example.volkov;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static org.junit.Assert.*;

public class NetworkServiceTest {
    CallbackHandler handler = new CallbackHandler();

    @Test
    public void jsonCategorizedApiTest() {
        JSONPlaceHolderApi api = NetworkService.getInstance().getJSONApi();
        try {
            assertEquals(getPosts(LinkParams.LATEST), api.getPostsWithType(LinkParams.LATEST.getType(), 0, true).execute().body());
            assertEquals(getPosts(LinkParams.TOP), api.getPostsWithType(LinkParams.TOP.getType(), 0, true).execute().body());
            assertEquals(getPosts(LinkParams.HOT), api.getPostsWithType(LinkParams.HOT.getType(), 0, true).execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Posts getPosts(LinkParams linkParams) {
        URL url = null;
        try {
            url = new URL("https://developerslife.ru/" + linkParams.getType() + "/0?json=true");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(reader, Posts.class);
    }

}
