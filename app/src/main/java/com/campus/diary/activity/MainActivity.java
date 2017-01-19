package com.campus.diary.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.campus.diary.R;
import com.campus.diary.adapter.CircleAdapter;
import com.campus.diary.model.CircleItem;
import com.campus.diary.model.CommentConfig;
import com.campus.diary.model.CommentItem;
import com.campus.diary.model.FavortItem;
import com.campus.diary.mvp.contract.CircleContract;
import com.campus.diary.mvp.presenter.CirclePresenter;
import com.campus.diary.utils.CommonUtils;
import com.campus.diary.view.CommentListView;
import com.campus.diary.view.DivItemDecoration;
import com.campus.diary.view.UpLoadDialog;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.droi.sdk.core.Core.getActivity;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class MainActivity extends BaseActivity implements CircleContract.View{

	protected static final String TAG = MainActivity.class.getSimpleName();
	private CircleAdapter circleAdapter;
	private LinearLayout edittextbody;
	private EditText editText;
	private ImageView sendIv;

	private int screenHeight;
	private int editTextBodyHeight;
	private int titleHeight;
	private int currentKeyboardH;
	private int selectCircleItemH;
	private int selectCommentItemOffset;

	private CirclePresenter presenter;
	private CommentConfig commentConfig;
	private SuperRecyclerView recyclerView;
	private RelativeLayout bodyLayout;
	private LinearLayoutManager layoutManager;

    private final static int TYPE_PULLREFRESH = 1;
    private final static int TYPE_UPLOADREFRESH = 2;
    private UpLoadDialog uploadDialog;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		presenter = new CirclePresenter(this);
		initView();

        //实现自动下拉刷新功能
        recyclerView.getSwipeToRefresh().post(new Runnable(){
            @Override
            public void run() {
                recyclerView.setRefreshing(true);//执行下拉刷新的动画
                refreshListener.onRefresh();//执行数据加载操作
            }
        });
		DroiUpdate.update(this);
	}

    @Override
    protected void onDestroy() {
        if(presenter !=null){
            presenter.recycle();
        }
        super.onDestroy();
    }

    @SuppressLint({ "ClickableViewAccessibility", "InlinedApi" })
	private void initView() {

        initTitle();
        initUploadDialog();

		recyclerView = (SuperRecyclerView) findViewById(R.id.recyclerView);
		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.addItemDecoration(new DivItemDecoration(2, true));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

		recyclerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (edittextbody.getVisibility() == View.VISIBLE) {
					updateEditTextBodyVisible(View.GONE, null);
					return true;
				}
				return false;
			}
		});

        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
				presenter.loadData(TYPE_PULLREFRESH);
            }
        };
        recyclerView.setRefreshListener(refreshListener);

		recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
			}

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if(newState == RecyclerView.SCROLL_STATE_IDLE){
					Glide.with(MainActivity.this).resumeRequests();
				}else{
					Glide.with(MainActivity.this).pauseRequests();
				}

			}
		});

		circleAdapter = new CircleAdapter(this);
		circleAdapter.setCirclePresenter(presenter);
        recyclerView.setAdapter(circleAdapter);
		
		edittextbody = (LinearLayout) findViewById(R.id.editTextBodyLl);
		 editText = (EditText) findViewById(R.id.circleEt);
		sendIv = (ImageView) findViewById(R.id.sendIv);
		sendIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (presenter != null) {
					//发布评论
					String content =  editText.getText().toString().trim();
					if(TextUtils.isEmpty(content)){
						Toast.makeText(MainActivity.this, "评论内容不能为空...", Toast.LENGTH_SHORT).show();
						return;
					}
					presenter.addComment(content, commentConfig);
				}
				updateEditTextBodyVisible(View.GONE, null);
			}
		});

		setViewTreeObserver();
	}

    private void initUploadDialog() {
        uploadDialog = new UpLoadDialog(this);
    }

    private void initTitle() {
		addTitle("校园日记");
		setrightButton("发日记",new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, PublishActivity.class));
//				DatasUtil.createCircleDatas();
			}
		});
    }


    private void setViewTreeObserver() {
		bodyLayout = (RelativeLayout) findViewById(R.id.bodyLayout);
		final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
		swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
            public void onGlobalLayout() {

                Rect r = new Rect();
				bodyLayout.getWindowVisibleDisplayFrame(r);
				int statusBarH =  getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
				if(r.top != statusBarH ){
					//在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
					r.top = statusBarH;
				}
                int keyboardH = screenH - (r.bottom - r.top);
				Log.d(TAG, "screenH＝ "+ screenH +" &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);

                if(keyboardH == currentKeyboardH){//有变化时才处理，否则会陷入死循环
                	return;
                }

				currentKeyboardH = keyboardH;
            	screenHeight = screenH;//应用屏幕的高度
            	editTextBodyHeight = edittextbody.getHeight();
				RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
				titleHeight = titleBar.getHeight();

                if(keyboardH<150){//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
				//偏移listview
				if(layoutManager!=null && commentConfig != null){
					layoutManager.scrollToPositionWithOffset(commentConfig.circlePosition + CircleAdapter.HEADVIEW_SIZE, getListviewOffset(commentConfig));
				}
            }
        });
	}

	/**
	 * 获取状态栏高度
	 * @return
	 */
	private int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
           if(edittextbody != null && edittextbody.getVisibility() == View.VISIBLE){
        	   //edittextbody.setVisibility(View.GONE);
			   updateEditTextBodyVisible(View.GONE, null);
        	   return true;
           }
        }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void update2DeleteCircle(String circleId) {
		List<CircleItem> circleItems = circleAdapter.getDatas();
		for(int i=0; i<circleItems.size(); i++){
			if(circleId.equals(circleItems.get(i).getObjectId())){
				circleItems.remove(i);
				circleAdapter.notifyDataSetChanged();
				//circleAdapter.notifyItemRemoved(i+1);
				return;
			}
		}
	}

	@Override
	public void update2AddFavorite(int circlePosition, FavortItem addItem) {
		if(addItem != null){
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
			if(item.getFavorters() == null){
				List<FavortItem> favorts = new ArrayList<>();
				item.setFavorters(favorts);
			}
            item.getFavorters().add(addItem);
			circleAdapter.notifyDataSetChanged();
            //circleAdapter.notifyItemChanged(circlePosition+1);
		}
	}

	@Override
	public void update2DeleteFavort(int circlePosition, String favortId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
		List<FavortItem> items = item.getFavorters();
		for(int i=0; i<items.size(); i++){
			if(favortId.equals(items.get(i).getObjectId())){
				items.remove(i);
				circleAdapter.notifyDataSetChanged();
                //circleAdapter.notifyItemChanged(circlePosition+1);
				return;
			}
		}
	}

	@Override
	public void update2AddComment(int circlePosition, CommentItem addItem) {
		if(addItem != null){
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
			if(item.getComments() == null){
				List<CommentItem> comments = new ArrayList<>();
				item.setComments(comments);
			}
            item.getComments().add(addItem);
			circleAdapter.notifyDataSetChanged();
            //circleAdapter.notifyItemChanged(circlePosition+1);
		}
		//清空评论文本
		 editText.setText("");
	}

	@Override
	public void update2DeleteComment(int circlePosition, String commentId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
		List<CommentItem> items = item.getComments();
		for(int i=0; i<items.size(); i++){
			if(commentId.equals(items.get(i).getObjectId())){
				items.remove(i);
				circleAdapter.notifyDataSetChanged();
                //circleAdapter.notifyItemChanged(circlePosition+1);
				return;
			}
		}
	}

	@Override
	public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
		this.commentConfig = commentConfig;
		edittextbody.setVisibility(visibility);

		measureCircleItemHighAndCommentItemOffset(commentConfig);

		if(View.VISIBLE==visibility){
			 editText.requestFocus();
			//弹出键盘
			CommonUtils.showSoftInput( editText.getContext(),  editText);

		}else if(View.GONE==visibility){
			//隐藏键盘
			CommonUtils.hideSoftInput( editText.getContext(),  editText);
		}
	}

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {
		if(datas == null){
			recyclerView.removeMoreListener();
			recyclerView.hideMoreProgress();
			return;
		}
        if (loadType == TYPE_PULLREFRESH){
            recyclerView.setRefreshing(false);
            circleAdapter.setDatas(datas);
        }else if(loadType == TYPE_UPLOADREFRESH){
            circleAdapter.getDatas().addAll(datas);
        }
        circleAdapter.notifyDataSetChanged();

        if(datas != null && circleAdapter.getDatas().size()<45 + CircleAdapter.HEADVIEW_SIZE){
            recyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                    presenter.loadData(TYPE_UPLOADREFRESH);
                }
            }, 1);
        }else{
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }

    }


    /**
	 * 测量偏移量
	 * @param commentConfig
	 * @return
	 */
	private int getListviewOffset(CommentConfig commentConfig) {
		if(commentConfig == null)
			return 0;
		//这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
		//int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - editTextBodyHeight - titleHeight;
		if(commentConfig.commentType == CommentConfig.Type.REPLY){
			//回复评论的情况
			listviewOffset = listviewOffset + selectCommentItemOffset;
		}
        Log.i(TAG, "listviewOffset : " + listviewOffset);
		return listviewOffset;
	}

	private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig){
		if(commentConfig == null)
			return;

		int firstPosition = layoutManager.findFirstVisibleItemPosition();
		//只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = layoutManager.getChildAt(commentConfig.circlePosition + CircleAdapter.HEADVIEW_SIZE - firstPosition);

		if(selectCircleItem != null){
			selectCircleItemH = selectCircleItem.getHeight();
		}

		if(commentConfig.commentType == CommentConfig.Type.REPLY){
			//回复评论的情况
			CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
			if(commentLv!=null){
				//找到要回复的评论view,计算出该view距离所属动态底部的距离
				View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
				if(selectCommentItem != null){
					//选择的commentItem距选择的CircleItem底部的距离
					selectCommentItemOffset = 0;
					View parentView = selectCommentItem;
					do {
						int subItemBottom = parentView.getBottom();
						parentView = (View) parentView.getParent();
						if(parentView != null){
							selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
						}
					} while (parentView != null && parentView != selectCircleItem);
				}
			}
		}
	}


	@Override
	public void showLoading(String msg) {
//		recyclerView.showMoreProgress();
	}

	@Override
	public void hideLoading() {
//		recyclerView.hideMoreProgress();
	}

	@Override
	public void showToast(String error) {
		Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
	}
}
