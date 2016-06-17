package com.darin.drisian;

import android.content.Intent;
import android.net.Uri;

import com.google.android.apps.muzei.api.Artwork;
import com.darin.drisian.data.WallData;

import java.util.ArrayList;
import java.util.Random;

public class MuzeiArtSource extends com.google.android.apps.muzei.api.MuzeiArtSource {

    public MuzeiArtSource() {
        super("MuzeiArtSource");
    }

    protected void onUpdate(int reason) {
        ArrayList<WallData> walls = ((Supplier) getApplicationContext()).getWallpapers(this);
        WallData data = walls.get(new Random().nextInt(walls.size()));

        publishArtwork(new Artwork.Builder()
                .imageUri(Uri.parse(data.url))
                .title(data.name)
                .byline(data.authorName)
                .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(data.url)))
                .build());
    }
}
