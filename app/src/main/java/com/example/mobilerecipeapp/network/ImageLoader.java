package com.example.mobilerecipeapp.network;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilerecipeapp.R;

public class ImageLoader {

    public static void load(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(R.mipmap.ic_launcher);
            return;
        }

        ImageRequest request = new ImageRequest(
                imageUrl,
                imageView::setImageBitmap,
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                null,
                error -> imageView.setImageResource(R.mipmap.ic_launcher)
        );

        Volley.newRequestQueue(context).add(request);
    }
}