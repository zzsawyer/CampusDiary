package com.campus.diary.mvp.presenter;

import android.view.View;

import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CommentConfig;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavortItem;
import com.campus.diary.model.PhotoInfo;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.CircleContract;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiPermission;
import com.droi.sdk.core.DroiQuery;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class CirclePresenter implements CircleContract.Presenter{
	private CircleContract.View view;
	private  static int index = 0;
	public CirclePresenter(CircleContract.View view){
		this.view = view;
	}

	@Override
	public void loadData(final int loadType){
		if(loadType == 1){
			index = 0;
		}
		getCircleData()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<List<CircleItem>>() {
					@Override
					public void onCompleted() {
						view.hideLoading();
					}

					@Override
					public void onError(Throwable e) {
						view.showToast("网络错误！");
					}

					@Override
					public void onNext(List<CircleItem> data) {
						view.update2loadData(loadType, data);
					}
				});
	}


	/**
	 * 
	* @Title: deleteCircle 
	* @Description: 删除动态 
	* @param  circleId     
	* @return void    返回类型 
	* @throws
	 */
	@Override
	public void deleteCircle(final String circleId){
		deleteCircleData(circleId)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<Boolean>() {
				@Override
				public void onCompleted() {
					view.hideLoading();
				}

				@Override
				public void onError(Throwable e) {
					view.showToast("网络错误！");
				}

				@Override
				public void onNext(Boolean result) {
					if (result) {
						view.update2DeleteCircle(circleId);
					}else{
						view.showToast("删除数据失败，请重试！");
					}
				}
			});
}
	/**
	 * 
	* @Title: addFavort 
	* @Description: 点赞
	* @param  circlePosition     
	* @return void    返回类型 
	* @throws
	 */
	@Override
	public void addFavort(final int circlePosition,String circleId){
		createFavort(circleId)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<FavortItem>() {
				@Override
				public void onCompleted() {
					view.hideLoading();
				}

				@Override
				public void onError(Throwable e) {
					view.showToast("网络错误！");
				}

				@Override
				public void onNext(FavortItem data) {
					if (data != null) {
						view.update2AddFavorite(circlePosition, data);
					}else{
						view.showToast("点赞失败！");
					}
				}
			});
	}
	/**
	 * 
	* @Title: deleteFavort 
	* @Description: 取消点赞 
	* @param @param circlePosition
	* @param @param favortId     
	* @return void    返回类型 
	* @throws
	 */
	@Override
	public void deleteFavort(final int circlePosition, final String favortId){
		deleteFavort(favortId)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<Boolean>() {
				@Override
				public void onCompleted() {
					view.hideLoading();
				}

				@Override
				public void onError(Throwable e) {
					view.showToast("网络错误！");
				}

				@Override
				public void onNext(Boolean result) {
					if (result) {
						view.update2DeleteFavort(circlePosition, favortId);
					}else{
						view.showToast("删除数据失败，请重试！");
					}
				}
			});
	}
	
	/**
	 * 
	* @Title: addComment 
	* @Description: 增加评论
	* @param  content
	* @param  config  CommentConfig
	* @return void    返回类型 
	* @throws
	 */
	@Override
	public void addComment(String content,final CommentConfig config){
		if(config == null){
			return;
		}
		createComment(content,config)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<CommentItem>() {
				@Override
				public void onCompleted() {
					view.hideLoading();
				}

				@Override
				public void onError(Throwable e) {
					view.showToast("网络错误！");
				}

				@Override
				public void onNext(CommentItem data) {
					if (data != null) {
						view.update2AddComment(config.circlePosition, data);
					}else{
						view.showToast("评论提交失败！");
					}
				}
			});
	}
	
	/**
	 * 
	* @Title: deleteComment 
	* @Description: 删除评论 
	* @param @param circlePosition
	* @param @param commentId     
	* @return void    返回类型 
	* @throws
	 */
	@Override
	public void deleteComment(final int circlePosition,final String commentId){
		deleteComment(commentId)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<Boolean>() {
				@Override
				public void onCompleted() {
					view.hideLoading();
				}

				@Override
				public void onError(Throwable e) {
					view.showToast("网络错误！");
				}

				@Override
				public void onNext(Boolean result) {
					if (result) {
						view.update2DeleteComment(circlePosition, commentId);
					}else{
						view.showToast("删除数据失败，请重试！");
					}
				}
			});
	}

	/**
	 *
	 * @param commentConfig
	 */
	@Override
	public void showEditTextBody(CommentConfig commentConfig){
        if(view != null){
            view.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
	}


    /**
     * 清除对外部对象的引用，反正内存泄露。
     */
    public void recycle(){
        this.view = null;
    }

	public static Observable<List<CircleItem>> getCircleData() {
		return Observable.create(new Observable.OnSubscribe<List<CircleItem>>() {
			@Override
			public void call(final Subscriber<? super List<CircleItem>> subscriber) {
				try {
					DroiQuery query = DroiQuery.Builder.newBuilder().limit(3).orderBy("createTime",false).offset(index * 3).query(CircleItem.class).build();
					DroiError droiError = new DroiError();
					List<CircleItem> circleData = query.runQuery(droiError);
					if(circleData != null && circleData.size() != 0){
						for(CircleItem item:circleData) {
							//query comment data
							DroiCondition cond = DroiCondition.cond("circleId", DroiCondition.Type.EQ, item.getObjectId());
							DroiQuery cquery = DroiQuery.Builder.newBuilder().where(cond).query(CommentItem.class).build();
							List<CommentItem> cdata = cquery.runQuery(null);
							if (cdata != null && cdata.size() != 0) {
								item.setComments(cdata);
							}
							//query favor data
							DroiQuery fquery = DroiQuery.Builder.newBuilder().where(cond).query(FavortItem.class).build();
							List<FavortItem> fdata = fquery.runQuery(null);
							if (fdata != null && fdata.size() != 0) {
								item.setFavorters(fdata);
							}
							//get photo file uri
							List<PhotoInfo> photos = item.getPhotos();
							for (PhotoInfo photo:photos) {
								photo.setIconUrl(photo.getIcon().getUri().toString());
							}
							item.setPhotos(photos);
							//get user head url
							User user = item.getUser();
							if(user.getHeadIcon() != null){
								user.setHeadUrl(user.getHeadIcon().getUri().toString());
							}
							item.setUser(user);
						}
						index++;
						subscriber.onNext(circleData);
					}else{
						subscriber.onNext(null);
					}
				} catch (Exception e) {
					subscriber.onError(e);
				}finally{
					subscriber.onCompleted();
				}
			}
		});
	}

	public static Observable<Boolean> deleteCircleData(final String id) {
		return Observable.create(new Observable.OnSubscribe<Boolean>() {
			@Override
			public void call(final Subscriber<? super Boolean> subscriber) {
				try {
					DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, id);
					DroiQuery query = DroiQuery.Builder.newBuilder().where(cond).query(CircleItem.class).build();
					DroiError droiError = new DroiError();
					List<CircleItem> circleData = query.runQuery(droiError);
					Boolean isDelete = false;
					if(circleData != null && circleData.size() != 0) {
						for (CircleItem item : circleData) {
							DroiError error = item.delete();
							if(error.isOk()) {
								isDelete = true;
								break;
							}
						}
					}
					subscriber.onNext(isDelete);
				} catch (Exception e) {
					subscriber.onError(e);
				}finally{
					subscriber.onCompleted();
				}
			}
		});
	}

	public static Observable<CommentItem> createComment(final String content,final CommentConfig config) {
		return Observable.create(new Observable.OnSubscribe<CommentItem>() {
			@Override
			public void call(final Subscriber<? super CommentItem> subscriber) {
				try {
					CommentItem item = new CommentItem();
					item.setContent(content);
					item.setUser(User.getCurrentUser(User.class));
					item.setCircleId(config.circleId);
					if (config.commentType == CommentConfig.Type.REPLY) {
						item.setToReplyUser(config.replyUser);
					}
					DroiPermission permission = new DroiPermission();
					permission.setPublicReadPermission(true);
					permission.setPublicWritePermission(false);
					item.setPermission(permission);
					DroiError droiError = item.save();
					if(droiError.isOk()) {
						subscriber.onNext(item);
					}
				} catch (Throwable e) {
					subscriber.onError(e);
				}finally{
					subscriber.onCompleted();
				}
			}
		});
	}

	public static Observable<Boolean> deleteComment(final String id) {
		return Observable.create(new Observable.OnSubscribe<Boolean>() {
			@Override
			public void call(final Subscriber<? super Boolean> subscriber) {
				try {
					DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, id);
					DroiQuery query = DroiQuery.Builder.newBuilder().where(cond).query(CommentItem.class).build();
					DroiError droiError = new DroiError();
					List<CommentItem> data = query.runQuery(droiError);
					Boolean isDelete = false;
					if(data != null && data.size() != 0) {
						for (CommentItem item : data) {
							DroiError error = item.delete();
							if(error.isOk()) {
								isDelete = true;
								break;
							}
						}
					}
					subscriber.onNext(isDelete);
				} catch (Exception e) {
					subscriber.onError(e);
				}finally{
					subscriber.onCompleted();
				}
			}
		});
	}

	public static Observable<FavortItem> createFavort(final String circleId) {
		return Observable.create(new Observable.OnSubscribe<FavortItem>() {
			@Override
			public void call(final Subscriber<? super FavortItem> subscriber) {
				try {
					FavortItem item = new FavortItem();
					item.setUser(User.getCurrentUser(User.class));
					item.setCircleId(circleId);
					DroiPermission permission = new DroiPermission();
					permission.setPublicReadPermission(true);
					permission.setPublicWritePermission(false);
					item.setPermission(permission);
					DroiError droiError = item.save();
					if(droiError.isOk()) {
						subscriber.onNext(item);
					}
				} catch (Exception e) {
					subscriber.onError(e);
				}finally{
					subscriber.onCompleted();
				}
			}
		});
	}

	public static Observable<Boolean> deleteFavort(final String id) {
		return Observable.create(new Observable.OnSubscribe<Boolean>() {
			@Override
			public void call(final Subscriber<? super Boolean> subscriber) {
				try {
					DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, id);
					DroiQuery query = DroiQuery.Builder.newBuilder().where(cond).query(FavortItem.class).build();
					DroiError droiError = new DroiError();
					List<FavortItem> data = query.runQuery(droiError);
					Boolean isDelete = false;
					if(data != null && data.size() != 0) {
						for (FavortItem item : data) {
							DroiError error = item.delete();
							if(error.isOk()) {
								isDelete = true;
								break;
							}
						}
					}
					subscriber.onNext(isDelete);
				} catch (Exception e) {
					subscriber.onError(e);
				}finally{
					subscriber.onCompleted();
				}
			}
		});
	}
}
