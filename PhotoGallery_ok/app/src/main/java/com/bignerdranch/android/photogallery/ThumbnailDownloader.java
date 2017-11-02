package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/10/27.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;

    private boolean mHasQuit = false;
    private Handler mHandler;
    private ConcurrentHashMap<T, String> mRequestMao = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListner<T> mTThumbnailDownloadListner;
    private LruCache<String, Bitmap> mLruCache = new LruCache<>(200);
    Bitmap bitmap;

    public interface ThumbnailDownloadListner<T> {
        void onThumbnaiDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListner(ThumbnailDownloadListner<T> listner) {
        mTThumbnailDownloadListner = listner;
    }

    public ThumbnailDownloader(Handler handler) {
        super(TAG);
        mResponseHandler = handler;
    }

    public ThumbnailDownloader() {
        super(TAG);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "get a url:" + url);

        if (url == null) {
            mRequestMao.remove(target);
        } else {
            mRequestMao.put(target, url);
            mHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    @Override
    protected void onLooperPrepared() {
//        super.onLooperPrepared();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T object = (T) msg.obj;
                    Log.i(TAG, "get a request for url:" + mRequestMao.get(object));
                    handleRequest(object);
                } else if (msg.what == MESSAGE_PRELOAD) {
                    String url = (String) msg.obj;
                    download(url);
                }
            }
        };
    }

    private void download(String url) {
        Bitmap bitmap1;

        if (url == null) return ;

        bitmap1 = mLruCache.get(url);
        if (bitmap1 != null) return ;

        try {
            byte[] bitmapsByte = new FlickrFetchr().getUrlBytes(url);
            bitmap1 = BitmapFactory.decodeByteArray(bitmapsByte, 0, bitmapsByte.length);
            mLruCache.put(url, bitmap1);
        }catch (IOException e) {
            Log.i(TAG, "error download image", e);
        }
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMao.get(target);
            if (url == null) return;
            bitmap = mLruCache.get(url);
            if (bitmap == null) {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory
                        .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mLruCache.put(url, bitmap);
            }


            Log.i(TAG, "bitmap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRequestMao.get(target) != url ||
                            mHasQuit) {
                        return;
                    }

                    mRequestMao.remove(target);
                    mTThumbnailDownloadListner.onThumbnaiDownloaded(target, bitmap);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "error downloading image", e);
        }
    }

    public Bitmap getCachedImage(String url) {
        return mLruCache.get(url);
    }

    public void preloadImage(String url) {
        mResponseHandler.obtainMessage(MESSAGE_PRELOAD).sendToTarget();
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
