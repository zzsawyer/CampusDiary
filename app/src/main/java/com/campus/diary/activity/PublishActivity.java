package com.campus.diary.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.campus.diary.R;
import com.campus.diary.adapter.ImagePublishAdapter;
import com.campus.diary.model.ImageItem;
import com.campus.diary.mvp.contract.PublishContract;
import com.campus.diary.mvp.presenter.PublishPresenter;
import com.campus.diary.utils.CommonUtils;
import com.campus.diary.utils.CustomConstants;
import com.campus.diary.utils.IntentConstants;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.droi.sdk.core.Core.getActivity;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class PublishActivity extends BaseActivity implements PublishContract.View,View.OnClickListener{
	private GridView mGridView;
	private EditText contentEdit;
	private ImagePublishAdapter mAdapter;
	private PublishContract.Presenter publishLogic;
	public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
	private ProgressDialog progressDialog;
	private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private LinearLayout layout;
	View selectPic;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		publishLogic = new PublishPresenter(PublishActivity.this);
		initData();
		initView();
	}

	protected void onPause() {
		super.onPause();
		saveTempToPref();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveTempToPref();
	}

	private void saveTempToPref() {
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		String prefStr = JSON.toJSONString(mDataList);
		sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

	}

	private void getTempFromPref() {
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
		if (!TextUtils.isEmpty(prefStr)) {
			List<ImageItem> tempImages = JSON.parseArray(prefStr,
					ImageItem.class);
			mDataList = tempImages;
		}
	}

	private void removeTempFromPref() {
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		getTempFromPref();
		List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
				.getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
		if (incomingDataList != null) {
			mDataList.addAll(incomingDataList);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		notifyDataChanged(); //当在ImageZoomActivity中删除图片时，返回这里需要刷新
	}

	@Override
	protected void onDestroy() {
		if (progressDialog!=null){
			progressDialog.dismiss();
		}
		super.onDestroy();
		mDataList.clear();
		removeTempFromPref();
	}


	public void initView() {
        addTitle("发日记");
		setBackButton();
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
		selectPic = findViewById(R.id.select_pic);
		layout = (LinearLayout) findViewById(R.id.pop_layout);
		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
		progressDialog=new ProgressDialog(PublishActivity.this);
		contentEdit =(EditText)findViewById(R.id.contentText);
		mGridView = (GridView) findViewById(R.id.gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mAdapter = new ImagePublishAdapter(this, mDataList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (position == getDataSize()) {
					CommonUtils.hideSoftInput(PublishActivity.this,contentEdit);
					selectPic.setVisibility(View.VISIBLE);
				} else {
					Intent intent = new Intent(PublishActivity.this,
							ImageZoomActivity.class);
					intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
							(Serializable) mDataList);
					intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
					startActivity(intent);
				}
			}
		});
		setrightButton("发送",new OnClickListener() {

			public void onClick(View v) {
				publishLogic.sendData(mDataList,contentEdit.getText().toString());
			}
		});
	}

	private int getDataSize() {
		return mDataList == null ? 0 : mDataList.size();
	}

	private int getAvailableSize() {
		int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
		if (availSize >= 0) {
			return availSize;
		}
		return 0;
	}

	public String getString(String s) {
		String path = null;
		if (s == null) return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void takePhoto() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		File vFile = new File(Environment.getExternalStorageDirectory()
				+ "/myimage/", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		if (!vFile.exists()) {
			File vDirPath = vFile.getParentFile();
			vDirPath.mkdirs();
		} else {
			if (vFile.exists()) {
				vFile.delete();
			}
		}
		path = vFile.getPath();
		Uri cameraUri = Uri.fromFile(vFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
					&& resultCode == -1 && !TextUtils.isEmpty(path)) {
				ImageItem item = new ImageItem();
				item.sourcePath = path;
				mDataList.add(item);
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_take_photo:
				takePhoto();
				selectPic.setVisibility(View.GONE);
				break;
			case R.id.btn_pick_photo:
				Intent intent = new Intent(PublishActivity.this,
						ImageBucketChooseActivity.class);
				intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
						getAvailableSize());
				startActivity(intent);
				selectPic.setVisibility(View.GONE);
				break;
			case R.id.btn_cancel:
				selectPic.setVisibility(View.GONE);
				break;
			default:
				break;
		}
	}

	private void notifyDataChanged()
	{
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void showToast(String result) {
		Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showLoading(String msg) {
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(msg);
		progressDialog.show();
	}

	@Override
	public void hideLoading() {
		progressDialog.dismiss();
	}

	@Override
	public void gotoMainActivity() {
		startActivity(new Intent(PublishActivity.this, MainActivity.class));
		finish();
	}
}