package com.example.xyzreader.util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Singleton wrapper for the Bus - messages between service & activity
 */

public class EventBus {
    private static Bus theBus = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        return theBus;
    }

    public static void register(Object object) {
        theBus.register(object);
    }

    public static void post(final Object object) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        if (Looper.myLooper() == Looper.getMainLooper()) {
            theBus.post(object);
        }
        else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    theBus.post(object);
                }
            });
        }
    }
}
