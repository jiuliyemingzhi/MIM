package net.jiuli.common.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.ArrayMap;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.jiuli.common.R;
import net.jiuli.common.app.Application;
import net.jiuli.common.utils.StreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Face {

    private final static class FaceI {

        private static final ArrayMap<String, Bean> FACE_MAP;

        private static final List<FaceTab> FACE_TABS;

        private static final ArrayMap<String, WeakReference<AnimatedImageSpan>> ANIMATED_IMAGE_SPAN_MAP = new ArrayMap<>();


        static {

            FACE_TABS = new ArrayList<>();

            FACE_MAP = new ArrayMap<>();
            Context context = Application.getInstance();

            FaceTab faceTab = initAssetsFace(context);
            if (faceTab != null) {
                FACE_TABS.add(faceTab);
            }

            faceTab = initResourceFace(context);
            if (faceTab != null) {
                FACE_TABS.add(faceTab);
            }

            for (FaceTab tab : FACE_TABS) {
                for (Bean bean : tab.getFaces()) {
                    FACE_MAP.put(bean.key, bean);
                }
            }
            Collections.unmodifiableCollection(FACE_TABS);
        }

        private static FaceTab initResourceFace(Context context) {
            ArrayList<Bean> beans = new ArrayList<>();
            Resources resources = context.getResources();
            String packageName = context.getApplicationInfo().packageName;
            for (int i = 0; i < 142; i++) {
                String key = String.format(Locale.ENGLISH, "fb%03d", i);
                String resStr = String.format(Locale.ENGLISH, "face_base_%03d", i);
                int drawableId = resources.getIdentifier(resStr, "drawable", packageName);
                if (drawableId != 0) {
                    beans.add(new Bean(key, drawableId));
                }
            }
            if (beans.isEmpty()) {
                return null;
            }

            return new FaceTab("NAME", beans.get(0).preview, beans);
        }


        private static FaceTab initAssetsFace(Context context) {

            String faceAsset = "face-t.zip";
            String faceCacheDir = String.format("%s/face/ft", context.getFilesDir());
            File faceFolder = new File(faceCacheDir);
            if (!faceFolder.exists()) {
                if (faceFolder.mkdirs()) {
                    try {
                        InputStream open = context.getAssets().open(faceAsset);
                        File faceSource = new File(faceFolder, "source.zip");
                        StreamUtil.copy(open, faceSource);
                        StreamUtil.unZip(faceSource, faceFolder);
                        StreamUtil.delete(faceSource.getAbsolutePath());
                    } catch (IOException e) {
                        return null;
                    }
                }
            }

            File infoFile = new File(faceFolder, "info.json");
            Gson gson = new Gson();

            JsonReader reader;
            try {
                reader = gson.newJsonReader(new FileReader(infoFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            FaceTab tab = gson.fromJson(reader, FaceTab.class);
            for (Bean face : tab.faces) {
                face.preview = String.format("%s%s%s", faceCacheDir, File.separator, face.preview);
                face.source = String.format("%s%s%s", faceCacheDir, File.separator, face.source);
            }
            return tab;
        }

    }


    public static List<FaceTab> all() {
        return FaceI.FACE_TABS;
    }

    public static ArrayMap<String, Bean> beans() {
        return FaceI.FACE_MAP;
    }


    public static void inputFace(@NonNull final Context context, final Editable editable, final Bean bean, final int size, final int start, final int end) {
        Glide.with(context)
                .load(bean.preview)
                .asBitmap()
                .placeholder(R.drawable.default_face)
                .into(new SimpleTarget<Bitmap>(size, size) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Spannable spannable = new SpannableString(String.format("[%s]", bean.key));


                        ImageSpan imageSpan = new ImageSpan(context, resource, ImageSpan.ALIGN_BOTTOM);

                        spannable.setSpan(imageSpan, 0, spannable
                                .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        editable.replace(start, end, spannable);

                    }
                });
    }


    public static final int TYPE_CHAT = 0;
    public static final int TYPE_ACTIVE = 1;


    public static void decode(@NonNull final TextView target, @NonNull final String content, float size, int type) {
        final int finalSize = (int) size;

        if (TextUtils.isEmpty(content)) {
            return;
        }
        final SpannableString spannable = new SpannableString(content);

        Pattern pattern = Pattern.compile("(\\[f[tb]\\d{3}])");
        final Context context = target.getContext();
        final Matcher matcher = pattern.matcher(content);

        if (type == TYPE_ACTIVE) {
            while (matcher.find()) {
                String key = matcher.group().replaceAll("[\\[\\]]", "");
                Bean bean = FaceI.FACE_MAP.get(key);
                final int start = matcher.start();
                final int end = matcher.end();
                if (bean != null) {
                    Glide.with(context)
                            .load(bean.preview)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(finalSize, finalSize) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    ImageSpan imageSpan = new ImageSpan(context, resource, ImageSpan.ALIGN_BOTTOM);
                                    spannable.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    target.setText(spannable);

                                }
                            });
                }
            }
            target.setText(spannable);
            return;
        }
        ArrayMap<String, AnimatedGifDrawable> drawableMap = new ArrayMap<>();
        while (matcher.find()) {
            String key = matcher.group().replaceAll("[\\[\\]]", "");
            WeakReference<AnimatedImageSpan> reference = FaceI.ANIMATED_IMAGE_SPAN_MAP.get(key);
            if (reference == null) {
                Bean bean = FaceI.FACE_MAP.get(key);
                if (bean != null) {
                    InputStream is;
                    if (bean.source instanceof String) {
                        try {
                            is = new FileInputStream(String.valueOf(bean.source));
                        } catch (FileNotFoundException e) {
                            is = null;
                            e.printStackTrace();
                        }
                    } else {
                        is = context.getResources().openRawResource((int) bean.source);
                    }

                    reference = new WeakReference<>(new AnimatedImageSpan(new AnimatedGifDrawable(is, target, finalSize)));
                    FaceI.ANIMATED_IMAGE_SPAN_MAP.put(key, reference);
                }
            } else {
                AnimatedGifDrawable drawable = drawableMap.get(key);
                if (drawable == null) {
                    reference.get().put(target);
                } else {
                    WeakReference<AnimatedImageSpan> weakReference = new WeakReference<>(new AnimatedImageSpan());
                    weakReference.get().setDrawable(drawable);
                    spannable.setSpan(weakReference.get(), matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    continue;
                }
            }
            spannable.setSpan(reference.get(), matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            drawableMap.put(key, reference.get().getAnimatedGifDrawable());
        }

        target.setText(spannable);
    }

    public static class FaceTab {
        private List<Bean> faces;

        private String name;

        private Object preview;

        public FaceTab(String name, Object preview, List<Bean> beans) {
            this.name = name;
            this.faces = beans;
            this.preview = preview;
        }


        public List<Bean> getFaces() {
            return faces == null ? faces = new ArrayList<>() : faces;
        }

        public void setFaces(List<Bean> face) {
            this.faces = face;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getPreview() {
            return preview;
        }

        public void setPreview(Object preview) {
            this.preview = preview;
        }
    }

    public static class Bean {
        private String key;
        private String desc;
        private Object source;
        private Object preview;

        Bean(String key, Object preview) {
            this.key = key;
            this.preview = preview;
            this.source = preview;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Object getSource() {
            return source;
        }

        public void setSource(Object source) {
            this.source = source;
        }

        public Object getPreview() {
            return preview;
        }

        public void setPreview(Object preview) {
            this.preview = preview;
        }
    }
}
