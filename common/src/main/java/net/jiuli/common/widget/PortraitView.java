package net.jiuli.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.jiuli.common.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jiuli on 17-8-26.
 */

public class PortraitView extends CircleImageView {

    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RequestManager manager, String url) {
        manager
                .load(url == null ? "" : url)
                .placeholder(R.drawable.default_portrait)
                .centerCrop().dontAnimate()
                .into(this);

    }
}
