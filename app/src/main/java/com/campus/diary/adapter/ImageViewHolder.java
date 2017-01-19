package com.campus.diary.adapter;

import android.view.View;
import android.view.ViewStub;

import com.campus.diary.R;
import com.campus.diary.view.MultiImageView;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class ImageViewHolder extends CircleViewHolder {
    /** 图片*/
    public MultiImageView multiImageView;

    public ImageViewHolder(View itemView){
        super(itemView, TYPE_IMAGE);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_imgbody);
        View subView = viewStub.inflate();
        MultiImageView multiImageView = (MultiImageView) subView.findViewById(R.id.multiImagView);
        if(multiImageView != null){
            this.multiImageView = multiImageView;
        }
    }
}
