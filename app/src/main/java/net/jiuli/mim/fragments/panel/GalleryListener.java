package net.jiuli.mim.fragments.panel;

interface GalleryListener {
        void onSelectedCountChange(int count, boolean isImage);

        void onLoaded(int count);
    }