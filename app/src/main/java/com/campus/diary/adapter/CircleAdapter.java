package com.campus.diary.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.campus.diary.MyApplication;
import com.campus.diary.R;
import com.campus.diary.activity.ImagePagerActivity;
import com.campus.diary.activity.MainActivity;
import com.campus.diary.activity.SelfActivity;
import com.campus.diary.activity.SignInActivity;
import com.campus.diary.model.ActionItem;
import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CommentConfig;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavortItem;
import com.campus.diary.model.PhotoInfo;
import com.campus.diary.model.User;
import com.campus.diary.mvp.presenter.CirclePresenter;
import com.campus.diary.utils.GlideCircleTransform;
import com.campus.diary.utils.UrlUtils;
import com.campus.diary.view.CommentDialog;
import com.campus.diary.view.CommentListView;
import com.campus.diary.view.ExpandTextView;
import com.campus.diary.view.MultiImageView;
import com.campus.diary.view.PraiseListView;
import com.campus.diary.view.SnsPopupWindow;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class CircleAdapter extends BaseRecycleViewAdapter {

    public final static int TYPE_HEAD = 0;
    public static final int HEADVIEW_SIZE = 1;

    private CirclePresenter presenter;
    private Context context;
    public void setCirclePresenter(CirclePresenter presenter){
        this.presenter = presenter;
    }

    public CircleAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEAD;
        }

        int itemType = 0;
        CircleItem item = (CircleItem) datas.get(position-1);
        if (CircleItem.TYPE_URL.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_URL;
        } else if (CircleItem.TYPE_IMG.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_IMAGE;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == TYPE_HEAD){
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.head_circle, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_circle_item, parent, false);

            if(viewType == CircleViewHolder.TYPE_URL){
                viewHolder = new URLViewHolder(view);
            }else if(viewType == CircleViewHolder.TYPE_IMAGE){
                viewHolder = new ImageViewHolder(view);
            }
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final User user = DroiUser.getCurrentUser(User.class);
        if(getItemViewType(position)==TYPE_HEAD){
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            holder.headBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 如果用户是合法的且是匿名用户
                    if (user == null || (user != null  && user.isAnonymous())) {
                        context.startActivity(new Intent(context, SignInActivity.class));
                    }else {
                        context.startActivity(new Intent(context, SelfActivity.class));
                    }
                }
            });
        }else{
            final int circlePosition = position - HEADVIEW_SIZE;
            final CircleViewHolder holder = (CircleViewHolder) viewHolder;
            final CircleItem circleItem = (CircleItem) datas.get(circlePosition);
            final String circleId = circleItem.getObjectId();
            String name = circleItem.getUser().getNickName();
            String headImg = circleItem.getUser().getHeadUrl();
            final String content = circleItem.getContent();
            String createTime = circleItem.getCreateTime();
            final List<FavortItem> favortDatas = circleItem.getFavorters();
            final List<CommentItem> commentsDatas = circleItem.getComments();
            boolean hasFavort = circleItem.hasFavort();
            boolean hasComment = circleItem.hasComment();

            Glide.with(context).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.bg_no_photo).transform(new GlideCircleTransform(context)).into(holder.headIv);

            holder.nameTv.setText(name);
            holder.timeTv.setText(createTime);

            if(!TextUtils.isEmpty(content)){
                holder.contentTv.setExpand(circleItem.isExpand());
                holder.contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                    @Override
                    public void statusChange(boolean isExpand) {
                        circleItem.setExpand(isExpand);
                    }
                });

                holder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

            if(user.getObjectId().equals(circleItem.getUser().getObjectId())){
                holder.deleteBtn.setVisibility(View.VISIBLE);
            }else{
                holder.deleteBtn.setVisibility(View.GONE);
            }
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除
                    if(presenter!=null){
                        presenter.deleteCircle(circleId);
                    }
                }
            });
            if(hasFavort || hasComment){
                if(hasFavort){//处理点赞列表
                    holder.praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            String userName = favortDatas.get(position).getUser().getNickName();
                            String userId = favortDatas.get(position).getUser().getObjectId();
                            Toast.makeText(MyApplication.getContext(), userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.praiseListView.setDatas(favortDatas);
                    holder.praiseListView.setVisibility(View.VISIBLE);
                }else{
                    holder.praiseListView.setVisibility(View.GONE);
                }

                if(hasComment){//处理评论列表
                    holder.commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int commentPosition) {
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            if(user.getObjectId().equals(commentItem.getUser().getObjectId())){//复制或者删除自己的评论

                                CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
                                dialog.show();
                            }else{//回复别人的评论
                                if(presenter != null){
                                    // 如果用户是合法的且是匿名用户
                                    if (user == null || (user != null  && user.isAnonymous())) {
                                        return;
                                    }
                                    CommentConfig config = new CommentConfig();
                                    config.circleId = commentItem.getObjectId();
                                    config.circlePosition = circlePosition;
                                    config.commentPosition = commentPosition;
                                    config.commentType = CommentConfig.Type.REPLY;
                                    config.replyUser = commentItem.getUser();
                                    presenter.showEditTextBody(config);
                                }
                            }
                        }
                    });
                    holder.commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(int commentPosition) {
                            //长按进行复制或者删除
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
                            dialog.show();
                        }
                    });
                    holder.commentList.setDatas(commentsDatas);
                    holder.commentList.setVisibility(View.VISIBLE);

                }else {
                    holder.commentList.setVisibility(View.GONE);
                }
                holder.digCommentBody.setVisibility(View.VISIBLE);
            }else{
                holder.digCommentBody.setVisibility(View.GONE);
            }

            holder.digLine.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

            final SnsPopupWindow snsPopupWindow = holder.snsPopupWindow;
            //判断是否已点赞
            String curUserFavortId = circleItem.getCurUserFavortId(User.getCurrentUser(User.class).getObjectId());
            if(!TextUtils.isEmpty(curUserFavortId)){
                snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
            }else{
                snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
            }
            snsPopupWindow.update();
            snsPopupWindow.setmItemClickListener(new PopupItemClickListener(circlePosition, circleItem, curUserFavortId));
            holder.snsBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //弹出popupwindow
                    snsPopupWindow.showPopupWindow(view);
                }
            });

            holder.urlTipTv.setVisibility(View.GONE);
            switch (holder.viewType) {
                case CircleViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                    if(holder instanceof URLViewHolder){
                        String linkImg = circleItem.getLinkImg();
                        String linkTitle = circleItem.getLinkTitle();
                        Glide.with(context).load(linkImg).into(((URLViewHolder)holder).urlImageIv);
                        ((URLViewHolder)holder).urlContentTv.setText(linkTitle);
                        ((URLViewHolder)holder).urlBody.setVisibility(View.VISIBLE);
                        ((URLViewHolder)holder).urlTipTv.setVisibility(View.VISIBLE);
                    }

                    break;
                case CircleViewHolder.TYPE_IMAGE:// 处理图片
                    if(holder instanceof ImageViewHolder){
                        final List<PhotoInfo> photos = circleItem.getPhotos();
                        if (photos != null && photos.size() > 0) {
                            ((ImageViewHolder)holder).multiImageView.setVisibility(View.VISIBLE);
                            ((ImageViewHolder)holder).multiImageView.setList(photos);
                            ((ImageViewHolder)holder).multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    //imagesize是作为loading时的图片size
                                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                                    List<String> photoUrls = new ArrayList<String>();
                                    for(PhotoInfo photoInfo : photos){
                                        photoUrls.add(photoInfo.getIconUrl());
                                    }
                                    ImagePagerActivity.startImagePagerActivity(((MainActivity) context), photoUrls, position, imageSize);


                                }
                            });
                        } else {
                            ((ImageViewHolder)holder).multiImageView.setVisibility(View.GONE);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size()+1;//有head需要加1
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        public ImageView headBtn;
        public TextView headNickname;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headBtn = (ImageView) itemView.findViewById(R.id.head_button);
            headNickname = (TextView)itemView.findViewById(R.id.head_nickname);
            final User user = DroiUser.getCurrentUser(User.class);
            if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                if (user.getHeadIcon() != null) {
                    user.getHeadIcon().getInBackground(new DroiCallback<byte[]>() {
                        @Override
                        public void result(byte[] bytes, DroiError error) {
                            if (error.isOk()) {
                                if (bytes == null) {
                                    Log.i("", "bytes == null");
                                } else {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    headBtn.setImageBitmap(bitmap);
                                    headNickname.setText(user.getNickName());
                                }
                            }
                        }
                    }, null);
                }
            } else {
                headBtn.setImageResource(R.drawable.default_account_icon);
            }
        }
    }

    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener{
        private String mFavorId;
        //动态在列表中的位置
        private int mCirclePosition;
        private long mLasttime = 0;
        private CircleItem mCircleItem;

        public PopupItemClickListener(int circlePosition, CircleItem circleItem, String favorId){
            this.mFavorId = favorId;
            this.mCirclePosition = circlePosition;
            this.mCircleItem = circleItem;
        }

        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if(System.currentTimeMillis()-mLasttime<700)//防止快速点击操作
                        return;
                    User user = DroiUser.getCurrentUser(User.class);
                    // 如果用户是合法的且是匿名用户
                    if (user == null || (user != null  && user.isAnonymous())) {
                        context.startActivity(new Intent(context, SignInActivity.class));
                        return;
                    }
                    mLasttime = System.currentTimeMillis();
                    if(presenter != null){
                        DroiAnalytics.onEvent(context,"Favort");
                        if ("赞".equals(actionitem.mTitle.toString())) {
                            presenter.addFavort(mCirclePosition,mCircleItem.getObjectId());
                        } else {//取消点赞
                            presenter.deleteFavort(mCirclePosition, mFavorId);
                        }
                    }
                    break;
                case 1://发布评论
                    if(presenter != null){
                        User myUser = DroiUser.getCurrentUser(User.class);
                        // 如果用户是合法的且是匿名用户
                        if (myUser == null || (myUser != null  && myUser.isAnonymous())) {
                            context.startActivity(new Intent(context, SignInActivity.class));
                            return;
                        }
                        DroiAnalytics.onEvent(context,"Comment");
                        CommentConfig config = new CommentConfig();
                        config.circleId = mCircleItem.getObjectId();
                        config.circlePosition = mCirclePosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
