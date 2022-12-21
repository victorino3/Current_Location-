package com.victorino.maptest;


import android.app.Application;
import android.util.Log;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;

class MapRendererOptInApplication extends Application implements OnMapsSdkInitializedCallback {

    @Override
    public void onCreate() {
        super.onCreate();
        MapsInitializer.initialize(getApplicationContext(), Renderer.LATEST, this);
    }

    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                Log.d("MapsDemo", "The latest version of the renderer is used.");
                break;
            case LEGACY:
                Log.d("MapsDemo", "The legacy version of the renderer is used.");
                break;
        }
    }
}

