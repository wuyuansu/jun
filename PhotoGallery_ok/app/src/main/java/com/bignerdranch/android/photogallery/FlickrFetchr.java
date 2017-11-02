package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/26.
 */

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "bcbcf1a022c37929d9f537741da18f4e";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);//用于开启网略
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK ) {
                throw new IOException(connection.getResponseMessage() +
                ": with" + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);//长度
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

//    public List<GalleryItem> fetchItems(Integer page) {
    private List<GalleryItem> downloadGalleryItem(String url) {
        List<GalleryItem> items = new ArrayList<>();

        try {
//            String url = Uri.parse("https://api.flickr.com/services/rest/")
//                    .buildUpon()
//                    .appendQueryParameter("method", "flickr.photos.getRecent")
//                    .appendQueryParameter("api_key", API_KEY)
//                    .appendQueryParameter("format", "json")
//                    .appendQueryParameter("nojsoncallback", "1")
//                    .appendQueryParameter("extras", "url_s")
//                    .appendQueryParameter("page", page.toString())
//                    .build().toString();
            String jsonString = getUrlString(url);

//            String jsonString = getUrlString(url);
            Log.i(TAG, "received json: " + jsonString);

            JSONObject jsonBody = new JSONObject(jsonString);
            JSONObject photosJsonBody = jsonBody.getJSONObject("photos");
            parseItems(items, photosJsonBody);
        } catch (IOException e) {
            Log.e(TAG, "failed to fetch items", e);
        }catch (JSONException e) {
//            e.printStackTrace();
            Log.i(TAG, "failed to parse json", e);
        }

        return items;
    }

    private String buildUrl(String method, String query) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);

        if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text", query);
        }

        return uriBuilder.build().toString();

    }

    public List<GalleryItem> fetchRecentPhotos() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItem(url);
    }

    public List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItem(url);
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws
                IOException, JSONException{
        Gson gson = new Gson();
        JSONArray jsonArray = jsonBody.getJSONArray("photo");
        GalleryItem[] galleryItems = gson.fromJson(jsonArray.toString(), GalleryItem[].class);

        for (GalleryItem item: galleryItems) {
            if (item.getUrl() == null) continue;

            items.add(item);
        }
    }
//    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws
//                IOException, JSONException{
//        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
//        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
//
//        for (int i = 0; i < photoJsonArray.length(); i++) {
//            JSONObject item = photoJsonArray.getJSONObject(i);
//
//            GalleryItem galleryItem = new GalleryItem();
//            galleryItem.setId(item.getString("id"));
//            galleryItem.setCaption(item.getString("title"));
//
//
//            if (!item.has("url_s")) {
//                continue;
//            }
//
////            galleryItem.setUrl(item.getString("url_s"));
//            items.add(galleryItem);
//        }
//    }
}
