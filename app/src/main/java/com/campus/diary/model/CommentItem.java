package com.campus.diary.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class CommentItem extends DroiObject{
	@DroiExpose
	private String circleId;
	@DroiReference
	private User user;
	@DroiReference
	private User toReplyUser;
	@DroiExpose
	private String content;

	public String getCircleId() {
		return circleId;
	}
	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public User getToReplyUser() {
		return toReplyUser;
	}
	public void setToReplyUser(User toReplyUser) {
		this.toReplyUser = toReplyUser;
	}
	
}
