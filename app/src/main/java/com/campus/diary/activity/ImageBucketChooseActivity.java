package com.campus.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.campus.diary.R;
import com.campus.diary.adapter.ImageBucketAdapter;
import com.campus.diary.model.ImageBucket;
import com.campus.diary.utils.CustomConstants;
import com.campus.diary.utils.ImageFetcher;
import com.campus.diary.utils.IntentConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class ImageBucketChooseActivity extends BaseActivity
{
	private ImageFetcher mHelper;
	private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
	private ListView mListView;
	private ImageBucketAdapter mAdapter;
	private int availableSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bucket_choose);

		mHelper = ImageFetcher.getInstance(getApplicationContext());
		initData();
		initView();
	}

	private void initData() {
		mDataList = mHelper.getImagesBucketList(false);
		availableSize = getIntent().getIntExtra(
				IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
				CustomConstants.MAX_IMAGE_SIZE);
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new ImageBucketAdapter(this, mDataList);
		mListView.setAdapter(mAdapter);
		addTitle("相册");
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				selectOne(position);

				Intent intent = new Intent(ImageBucketChooseActivity.this,
						ImageChooseActivity.class);
				intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
						(Serializable) mDataList.get(position).imageList);
				intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
						mDataList.get(position).bucketName);
				intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
						availableSize);

				startActivity(intent);
			}
		});
		setrightButton("取消",new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						PublishActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private void selectOne(int position) {
		int size = mDataList.size();
		for (int i = 0; i != size; i++)
		{
			if (i == position) mDataList.get(i).selected = true;
			else
			{
				mDataList.get(i).selected = false;
			}
		}
		mAdapter.notifyDataSetChanged();
	}

}
