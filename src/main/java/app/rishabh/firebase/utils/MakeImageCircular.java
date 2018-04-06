package app.rishabh.firebase.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import app.rishabh.firebase.R;

/**
 * Created by Rishabh Saxena
 * rishabh.saxena@appinventiv.com
 * Appinventiv Technologies Pvt. Ltd.
 * on 7/3/17.
 */

public class MakeImageCircular {

    //Method Below is used for making image circular with drawable id
    public static void makeImageCircular(final Context context, int id, final ImageView imageView) {
        com.bumptech.glide.Glide.with(context).load(id).asBitmap().placeholder(R.drawable.ic_side_menu_img_placeholder).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    //Method Below is used for making image circular with Contact Image Uri
    public static void makeImageCircularWithUri(final Context context, String uri, final ImageView imageView) {
        com.bumptech.glide.Glide.with(context).load(uri).asBitmap().placeholder(R.drawable.ic_side_menu_img_placeholder).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

}
