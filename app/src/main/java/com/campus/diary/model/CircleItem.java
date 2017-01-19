package com.campus.diary.model;

import android.text.TextUtils;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;
import com.droi.sdk.core.DroiReferenceObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class CircleItem extends DroiObject{

	public final static String TYPE_URL = "1";
	public final static String TYPE_IMG = "2";
	@DroiExpose
	private String content;
	@DroiExpose
	private String createTime;
	@DroiExpose
	private String type;//1:链接  2:图片
	@DroiExpose
	private String linkImg;
	@DroiExpose
	private String linkTitle;
	@DroiExpose
	private List<DroiReferenceObject> photos;
	@DroiReference
	private User user;

	private List<FavortItem> favorters;
	private List<CommentItem> comments;

	private boolean isExpand;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<FavortItem> getFavorters() {
		return favorters;
	}
	public void setFavorters(List<FavortItem> favorters) {
		this.favorters = favorters;
	}
	public List<CommentItem> getComments() {
		return comments;
	}
	public void setComments(List<CommentItem> comments) {
		this.comments = comments;
	}
	public String getLinkImg() {
		return linkImg;
	}
	public void setLinkImg(String linkImg) {
		this.linkImg = linkImg;
	}
	public String getLinkTitle() {
		return linkTitle;
	}
	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	public List<PhotoInfo> getPhotos() {
		return ref2Photos(photos);
	}
	public void setPhotos(List<PhotoInfo> photos) {
		this.photos = photos2Ref(photos);
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public void setExpand(boolean isExpand){
		this.isExpand = isExpand;
	}

	public boolean isExpand(){
		return this.isExpand;
	}

	public boolean hasFavort(){
		if(favorters!=null && favorters.size()>0){
			return true;
		}
		return false;
	}
	
	public boolean hasComment(){
		if(comments!=null && comments.size()>0){
			return true;
		}
		return false;
	}
	
	public String getCurUserFavortId(String curUserId){
		String favortid = "";
		if(!TextUtils.isEmpty(curUserId) && hasFavort()){
			for(FavortItem favorter : favorters){
				if(curUserId.equals(favorter.getUser().getObjectId())){
					favortid = favorter.getObjectId();
					return favortid;
				}
			}
		}
		return favortid;
	}


	public  List<DroiReferenceObject> photos2Ref(List<PhotoInfo> objs) {
		List<DroiReferenceObject> casts = new ArrayList();
		if (objs != null) {
			for (PhotoInfo obj : objs) {
				DroiReferenceObject ref = new DroiReferenceObject();
				ref.setDroiObject(obj);
				casts.add(ref);
			}
		}
		return casts;
	}


	public List<PhotoInfo> ref2Photos(List<DroiReferenceObject> refs) {
		List<PhotoInfo> casts = new ArrayList();
		if (refs != null) {
			for (DroiReferenceObject ref : refs) {
				casts.add((PhotoInfo) ref.droiObject());
			}
		}
		return casts;
	}
}
