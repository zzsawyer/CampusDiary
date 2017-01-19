package com.campus.diary.mvp.contract;

import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CommentConfig;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavortItem;

import java.util.List;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public interface CircleContract {

    interface View extends BaseView{
        void update2DeleteCircle(String circleId);
        void update2AddFavorite(int circlePosition, FavortItem addItem);
        void update2DeleteFavort(int circlePosition, String favortId);
        void update2AddComment(int circlePosition, CommentItem addItem);
        void update2DeleteComment(int circlePosition, String commentId);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void update2loadData(int loadType, List<CircleItem> datas);
    }

    interface Presenter {
        void loadData(int loadType);
        void deleteCircle(final String circleId);
        void addFavort(final int circlePosition,final String circleId);
        void deleteFavort(final int circlePosition, final String favortId);
        void addComment(String content,final CommentConfig config);
        void deleteComment(final int circlePosition, final String commentId);
        void showEditTextBody(CommentConfig commentConfig);

    }
}
