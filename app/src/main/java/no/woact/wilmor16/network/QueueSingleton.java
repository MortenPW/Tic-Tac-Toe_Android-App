package no.woact.wilmor16.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Morten
 */

public class QueueSingleton {
    // Based on https://developer.android.com/training/volley/requestqueue.html
    // Volley handles async

    private static QueueSingleton mInstance;
    private RequestQueue mRequestQueue;

    // Removed to avoid potential memory leaks- just means we need to call methods with context
    //private static Context mCtx;

    private QueueSingleton(Context context) {
        mRequestQueue = getRequestQueue(context);
    }

    public static synchronized QueueSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new QueueSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, Context context) {
        getRequestQueue(context).add(req);
    }
}