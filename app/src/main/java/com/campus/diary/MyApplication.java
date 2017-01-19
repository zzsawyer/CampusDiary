package com.campus.diary;

import android.app.Application;
import android.content.Context;

import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.push.DroiPush;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavortItem;
import com.campus.diary.model.PhotoInfo;
import com.campus.diary.model.User;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class MyApplication extends Application {

	private static Context mContext;
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		Core.initialize(this);
		DroiObject.registerCustomClass(User.class);
		DroiObject.registerCustomClass(PhotoInfo.class);
		DroiObject.registerCustomClass(FavortItem.class);
		DroiObject.registerCustomClass(CommentItem.class);
		DroiObject.registerCustomClass(CircleItem.class);
		DroiFeedback.initialize(this);
		DroiUpdate.initialize(this);
		DroiAnalytics.initialize(this);
		DroiPush.initialize(this);

	}

	public static Context getContext(){
		return mContext;
	}


}
