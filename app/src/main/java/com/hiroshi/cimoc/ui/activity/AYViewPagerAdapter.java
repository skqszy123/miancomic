package com.hiroshi.cimoc.ui.activity;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.hiroshi.cimoc.R;
import com.hiroshi.cimoc.ui.custom.photo.PhotoDraweeView;
import com.hiroshi.cimoc.ui.custom.photo.PhotoDraweeViewController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;

/**
 * Project：Cimoc-master
 * Author: sunkeqiang
 * Version: 1.0.0
 * Description：
 * Date：2016/8/26 11:53
 * Modification  History:
 * Why & What is modified:
 */
public class AYViewPagerAdapter extends com.hiroshi.cimoc.ui.adapter.BaseAdapter<String> {
    public static final int MODE_PAGE = 0;
    public static final int MODE_STREAM = 1;

    @IntDef({MODE_PAGE, MODE_STREAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PictureMode {}

    private PipelineDraweeControllerBuilder builder;
    private PhotoDraweeViewController.OnSingleTapListener listener;
    private @PictureMode int mode;
    private boolean split = false;


    public AYViewPagerAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        switch (mode) {
            default:
            case MODE_PAGE:
                return new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        outRect.set(0, 0, 0, 0);
                    }
                };
            case MODE_STREAM:
                return new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        outRect.set(0, 10, 0, 10);
                    }
                };
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_picture, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.reader_image_view) PhotoDraweeView photoView;

        public ViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PhotoDraweeView draweeView = ((ViewHolder) holder).photoView;
        switch (mode) {
            case MODE_PAGE:
                draweeView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                draweeView.setHorizontalMode();
                builder.setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo == null) {
                            return;
                        }
                        draweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                    }
                });
                break;
            case MODE_STREAM:
                draweeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                draweeView.setVerticalMode();
                builder.setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);
                        if (imageInfo != null) {
                            draweeView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                        }
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo != null) {
                            draweeView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                            draweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }
                });
                break;
        }
        draweeView.setOnSingleTapListener(listener);
        builder.setTapToRetryEnabled(true);
        Log.e("image url:",mDataSet.get(position));

        draweeView.setController(builder.setUri(mDataSet.get(position)).build());

    }

    public void setAutoSplit(boolean split) {
        this.split = split;
    }
    public void setControllerBuilder(PipelineDraweeControllerBuilder builder) {
        this.builder = builder;
    }
    public void setSingleTapListener(PhotoDraweeViewController.OnSingleTapListener listener) {
        this.listener = listener;
    }
}
