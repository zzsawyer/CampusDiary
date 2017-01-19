#使用MVP+RxAndroid+DroiBaaS打造云后台App—校园日记
##为什么想做校园日记？
前段时间支付宝的校园日记功能火爆异常，但是却昙花一现，可是在社会上还是引起了一阵自媒体浪潮，其实这就是人的本性的释放，人的本性就有喜欢嘚瑟，爱表现自己的成分。在我理解中大部分能火起来的App都有能抓住人的一部分本性需求，所以我就想开发一个校园日记的App，让它成为最时尚的大学生社交活动App，专为广大在校童鞋们打造的校园日记分享软件。可以上传自己的自拍照片、美食图片、心情感想等日记，实现随时随地分享自己，展现自己的需求。  

APP功能分解：
![](http://ojx2540cr.bkt.clouddn.com/%E6%A0%A1%E5%9B%AD%E6%97%A5%E8%AE%B0.png)
核心功能就是个人日记的展示，其实这个最终的样子做出来应该和朋友圈非常类似
![](http://ojx2540cr.bkt.clouddn.com/Screenshot_20170118-205700.jpg)
##为什么选用MVP+RxAndroid+DroiBaaS
技术架构选型：
对程序进行架构设计的原因，归根到底是为了提高生产力。通过设计使程序模块化，能够更简单的读懂code以及方便维护和测试。整体的App架构选用MVP来搭建，结合最近比较火热的RxAndroid实现观察者事务模式就能够做到模块内部的高聚合和模块之间的低耦合，模块内被的高聚合。
由于开发的应用需要搭建云服务器和数据库，所以也选用了最近比较流行的一站式后端云服务DroiBaaS来实现所有云后台功能。后面会讲到具体用到哪些功能和怎么来使用这些功能。

###为什么选用MVP模式？
![](http://ojx2540cr.bkt.clouddn.com/MVP.png)
以上是MVP的工作原理图。其中Presenter操作View和Mode都是通过接口来实现直接的调用。
传统的MVC模式很难把View和Controller分开，总是直接在View的事件响应函数里完成了Controller的代码，而MVP就完全解决了这个问题。MVP的工作流程如下：Presenter负责逻辑的处理，Model提供数据，View负责显示。 
作为一种新的模式，在MVP中View并不直接使用Model，它们之间的通信是通过Presenter来进行的，所有的交互都发生在Presenter内部。这样的话最大可能降低了View和Model之间的耦合性，维护和测试起来都是异常的简单方便。
###为什么选用RxAndroid？
最主要是两个字简洁，RxAndroid是RxJava的扩展，它的异步调用随着程序逻辑变得越来越复杂，它的链式调用依然能够保持简洁。
RxAndroid的回调方法主要有三个，onNext()，onError(),onCompleted()。
•	onNext() 对于Subscribler我们可以理解为接收数据。
•	onCompleted() 观测的事件的队列任务都完成了，当不再有onNext()发送数据时，onCompleted事件被触发。
•	onError() 当事件异常时响应此方法，一旦此方法被触发，队列自动终止，不再发送任何数据。
###为什么选用DroiBaaS？  
在这之前我的云后台都来自于阿里云+后端工程师，但是我只是个Android工程师，所以我需要一个更加简单方便的云后台生产工具。我选择云后台，希望能满足我以下几个要求：

 1. 服务器环境我不会搭建，所以更别提维护了，比如CentOS+Nginx+PHP+MySQL，我也只是听说而已
 2. 我更不会写Server端的Code，因为我只会安卓App开发，而且这应用只是我个人开发，也找不到其他人来帮我写server code
 3. 最好能有现成的可视化管理后台，这样以后管理和运营起来也方便
 4. 花钱尽量少,最好免费，毕竟是个人兴趣和尝试，不希望试错成本太高
 5. 能一站式尽量一站式，虽然我也可以用友盟的统计+极光的推送+酷传的代发布+百度的广告+啥啥啥，不过毕竟麻烦么不是。。  

综合这些需求，我发现最近新鲜出炉的专为APP开发者提供一站式整合云后端的服务——BaaS比较适合我来使用。无需租用服务器和开发服务器端程序，只需集成BaaS平台提供相应SDK就能够实现各种云后台的功能，比如云数据库，用户系统搭建，推送通道，用户反馈收集，版本管理和数据统计的功能，这些功能对于App的开发以及之后的运营都是必须的。目前国内的几家BaaS云服务提供商，比如leanCloud、DroiBaaS、Bmob、Maxleap，目前都处于创业阶段，因为本身BaaS还处于一个概念期，到普及还需要一段时间，但是用过之后真心觉得相当好用。我相信选用BaaS来搭建App的云后台这将是之后个人开发者以及中小企业开发者的趋势。  

DroiBaaS相比其他几家的优势在于：

 1. 提供沙箱和生产两种模式，沙箱完成调试再发布生产环境上线，避免了调试和测试对正式版本的数据污染
 2. DroiObject使用相当的方便，注解的编程方式相比其他几家还是有不错的便利性
 3. 有渠道和广告背景，App开发出来之后能够提供一定的推广和变现的帮助
 4. 文档比较全面而且详细，SDK集成方式简单，打电话咨询过，客服态度不错，很耐心也很专业
 5. 免费额度相比较其他几家还是比较有优势的，虽然我也不知道会用到多少，但是多一点总归是好的

 
##系统框架设计：
###代码架构如下
![](http://ojx2540cr.bkt.clouddn.com/%E4%BB%A3%E7%A0%81%E6%9E%B6%E6%9E%84.png)
使用MVP模式来开发的好处就是代码架构非常的清晰明了，个人还是比较注重代码的逻辑性以及可读性
###用到的框架及生产工具
日记的展示界面用了SuperRecyclerView——使RecyclerView更加容易使用的Android类库
图片加载与缓存用了Glide
DroiBaaS的网络请求都是基于OKHttp的，所以OKHttp和OKio是必须用到的网络框架
Json的生成和解析用的FastJson
响应式编程用的是RxAndroid
高度整合封装的云服务BaaS作为第二代云计算的产物，为App的云后台开发提供了非常便利的生产工具，提高了开发效率、缩短了上线时间、降低了开发成本，这必将是一个潮流和趋势，我还是比较看好的。
所有的云端功能，如推送、自更新、用户反馈、统计、云数据、用户管理功能全部是用DroiBaaS的SDK来实现
###日记展示UML架构设计：
![](http://ojx2540cr.bkt.clouddn.com/1.png)
MainActvity继承CircleContract.View接口，
CirclePresenter继承CircleContract.Presenter接口
MainActvity 生成一个CirclePresenter对象同时把自身传入CirclePresenter
MainActvity调用CircleContract.Presenter的各种数据获取接口，CirclePresenter从云端获取到数据后调用CircleContract.View的界面更新接口通知MainActivity来刷新View
整个MVP架构相当的清晰明了，使用MVP最大的好处就在此处，代码简洁，同时简化了Activity的逻辑，利于以后的调试和单元测试，新功能加起来也非常的方便
数据库设计如UML图所示
主要是四个主要交互数据类，这个四个类同时也是后DroiBaaS云后台数据库保存的数据类
CircleItem：日记内容Data
FavortItem：用户点赞Data
CommetItem：用户评论Data
User：用户Data
##详细代码设计
日记MVP+RxAndroid代码如下：
整个工程代码比较多，我在这里只贴了日记展示关键逻辑的代码，整个工程的源码请参考文档最后的GitHub的链接
CircleContract.java  
Model和View的中间接口类
```
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
```
CirclePresenter.java
此类使用RxAndroid从云端获取数据再发回给View进行异步展示，在这个类中可以看出使用RxAndroid处理异步逻辑非常得心用手，推荐大家使用。
```
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
}    
```
MainActivity.java
日记展现类，通过CirclePresenter获取的数据后再调用View的接口来展示和更新数据。
```
public class MainActivity extends BaseActivity implements CircleContract.View{

	protected static final String TAG = MainActivity.class.getSimpleName();
	private CircleAdapter circleAdapter;
	private LinearLayout edittextbody;
	private EditText editText;
	private ImageView sendIv;

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
			}
		});
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

	@Override
	public void showLoading(String msg) {
	}

	@Override
	public void hideLoading() {
	}

	@Override
	public void showToast(String error) {
		Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
	}
}
```
###云端逻辑实现
前面已经提到了整个App没有搭建自己的服务器也没有编写自己的Server端程序，整个云端逻辑都是通过DroiBaaS来提供，具体用到了如下功能 

 - 使用Core SDK的来搭建App的用户系统，管理云数据（日记、点赞、评论、照片等）
 - 使用版本更新SDK来完成应用的自更新，包括手动更新，给以后的APP更新提供通道
 - 使用用户反馈SDK来收集用户的建议和意见，持续改进自己的产品
 - 使用统计SDK来获取统计用户的新增、日活、以及其他自定义事件
 - 使用消息推送SDK来完成应用的推送功能，以后能够做一些营销或者是事务通知

但是如何来使用，下面我来按步骤一一介绍。
其实官网也有快速入门文档，根据快速入门文档来操作，也能很快上手。
链接：http://www.droibaas.com/html/doc_24138.html

 1. 注册DroiBaaS帐号
在网址栏输入www.droibaas.com或者在百度输入DroiBaaS进行搜索，打开官网后，点击右上角的“注册”按钮，在跳转页面填入你的手机、设置密码，收到验证码填入后就能激活你的DroiBaaS账户，然后就可以用DroiBaaS的各种SDK来轻松开发应用了。
 2. 网站后台创建应用
使用注册好的账号登录进入DroiBaaS控制台后，点击控制台界面左上角“创建应用”，在弹出框输入你应用的名称，然后点击确认，你就拥有了一个等待开发的应用。
 3. 获取应用密钥
选择你要开发的应用，进入该应用的应用管理界面
在跳转页面，进入应用设置/安全密钥，点击复制，即可得到AppID
 4. 下载和安装DroiBaaS SDK
在官网上点击上方的下载按钮就能够下载到DroiBaaS的SDK，比较好的是还能够支持打包下载，就不需要我一个一个的去下载了，还是挺方便
安装SDK流程比较简单根据快速入门以及其他SDK的引导的安装步骤来操作就OK，DroiBaaS使用的是GitHub的远程仓库，这样做的好处有两个
 - 省去了拷贝aar包到lib目录的步骤，自动从网上下载
 - 每次编译发布的时候都能够用到SDK的最新版本
但也存在缺点，因为GitHub的网站被墙了，在国内访问还是比较慢的，所以在下载aar包的时候有时候速度会比较慢。

 5. 使用DroiBaaS功能

一）.搭建App用户系统
DroiBaaS的Core SDK提供了DroiUser类能够用来建立用户系统，在这里我创建了一个类User继承于DroiUser,在这个类中添加一些自己App需要的属性，比如：nickName、headUrl、headIcon等
```
public class User extends DroiUser {
   @DroiExpose
   private String headUrl;
   @DroiExpose
   private DroiFile headIcon;
   @DroiExpose
   private String nickName;

   public User(){
   }
   public String getNickName() {
      return nickName;
   }
   public void setNickName(String nickName) {
      this.nickName = nickName;
   }
   public String getHeadUrl() {
      return headUrl;
   }
   public void setHeadUrl(String headUrl) {
      this.headUrl = headUrl;
   }
   public DroiFile getHeadIcon() {
      return headIcon;
   }
   public void setHeadIcon(DroiFile headIcon) {
      this.headIcon = headIcon;
   }
}
```
注册用户
```
User user = new User();
user.setUserId(username);
user.setPassword(password);
DroiPermission permission = new DroiPermission();
permission.setPublicReadPermission(true);
user.setPermission(permission);
DroiError droiError = user.signUp();
```
登录
```
DroiError droiError = new DroiError();
User user = DroiUser.login(username, password, User.class, droiError);
修改密码
DroiUser myUser = DroiUser.getCurrentUser();
if (myUser != null && myUser.isAuthorized() && !myUser.isAnonymous()) {
    DroiError droiError = myUser.changePassword(oldPassword, newPassword);
    subscriber.onNext(droiError);
}
```
上传头像
```
DroiFile headIcon = new DroiFile(new File(path));
User user = DroiUser.getCurrentUser(User.class);
DroiPermission permission = new DroiPermission();
permission.setPublicReadPermission(true);
permission.setPublicWritePermission(false);
headIcon.setPermission(permission);
user.setHeadIcon(headIcon);
user.saveInBackground(new DroiCallback<Boolean>() {
    @Override
    public void result(Boolean aBoolean, DroiError droiError) {
        if (aBoolean) {
            Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
        }
    }
});
```
二）.使用云数据来管理日记内容
创建一个日记data类CircleItem继承于DroiObject，使用save函数就能够在云端的数据库保存日记数据了。相当的简单和方便，传统的使用方式往往还要服务端编写一个接口，客户端和服务端定好相应的协议，使用http协议并携带相应的数据来访问接口，才会完成相应的操作。使用DroiBaaS的云数据功能，大大简化了流程，下面我们来看一看具体的使用
```
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
}
```
发布日记：
```
CircleItem data = new CircleItem();
data.setContent(content);
data.setCreateTime(Core.getTimestamp().toString());
data.setUser(User.getCurrentUser(User.class));
data.setType("2");
data.setPhotos(createPhotos(items));
DroiPermission permission = new DroiPermission();
permission.setPublicReadPermission(true);
permission.setPublicWritePermission(false);
data.setPermission(permission);
DroiError droiError = data.save();
```
查询获取日记、评论、点赞数据

```
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
```
所有Save的数据都会在云端以数据库的形式保存，方便下次查询，如下图
App中用到的数据类（CircleItem，FavortItem，CommetItem，User，Photo）在云端都会生成相应的表格,我原来需要通过N个步骤才能实现的云端数据存储，现在只需要调用DroiObject.Save就能一键保存至云端并生产相应表格。
![](http://ojx2540cr.bkt.clouddn.com/%E4%BA%91%E6%95%B0%E6%8D%AE.png)
三）.DroiBaaS其他功能——自更新、用户反馈、统计、推送功能
手动更新和用户反馈功能可以通过在“我的”页面点击按钮来调用，推送功能可以在初始化时添加，统计功能按照自己的统计需求进行打点上传数据，使用这些SDK都需要再Application的onCreate中进行初始化
```
public class MyApplication extends Application {

   private static Context mContext;
   @Override
   public void onCreate() {
      super.onCreate();
      mContext = getApplicationContext();
      Core.initialize(this);
      DroiFeedback.initialize(this);
      DroiUpdate.initialize(this);
      DroiAnalytics.initialize(this);
      DroiPush.initialize(this);

   }
}
```
1.版本更新：当我们的产品在重大Bug修复、功能增加、增加变现入口的时候，需要对我们的App进行升级，升级的成功率至关重要，一个好的自更新SDK能省不少事。
1)	版本更新SDK在此工程中，总共在两处添加接口调用。 一次是在应用进入时，在入口Activity的onCreate中，主要实现在应用进入的时候自动检查是否有更新，有更新的话会帮你下载并安装（同时支持静默更新和强制更新），添加了如下代码：
DroiUpdate.update(this);
还有一次是在我的页面中，通过手动点击的方式调用来检查云端是否有版本需要更新：
DroiUpdate.manualUpdate(mContext)
2)	在DroiBaaS后台配置自更新，配置界面如下
![](http://ojx2540cr.bkt.clouddn.com/%E8%87%AA%E6%9B%B4%E6%96%B0.png)
2.用户反馈：我们需要通过意见反馈来知道用户对应用的评价以及反馈，帮助我们持续改进App，通过点击按钮进入反馈的界面：
```
@Override
public void onClick(View v) {
    switch (v.getId()) {
        // 其他case
        case R.id.mine_frag_update:
            //手动更新
            DroiFeedback.callFeedback(mContext);
            break;
    }
}
```
所有的用户反馈DroiBaaS的控制台都能够看得到，你还可以选择对某些反馈进行回复，App用户也能看到相应的回复，如图
![](http://ojx2540cr.bkt.clouddn.com/%E7%94%A8%E6%88%B7%E5%8F%8D%E9%A6%88.png)
3.消息推送：通过消息推送增加应用的日活，方便活动的推广等。只需在Application中添加一行代码即可实现：
```
DroiPush.initialize(this);
```
在DroiBaaS后台可以发送推送通知
![](http://ojx2540cr.bkt.clouddn.com/%E6%8E%A8%E9%80%81.png)
4.	统计功能：大数据时代，大家对于数据也越来越看重，怎么样收集自己App的用户数据，以利于分析用户分行为，为之后产品改进以及运营提供重要的策略指导。
那么我在App尝试在哪些地方打点记录用户行为，具体如下：
1）	每个页面的跳转，主要是记录页面的访问记录以及每个页面的停留时间，DroiBaaS的统计SDK本身提供了记录页面访问的方式，我只需要在BaseActivity里面加上相应代码即可。
```
@Override
protected void onResume() {
    super.onResume();
    DroiAnalytics.onResume(BaseActivity.this);
}

@Override
protected void onPause() {
    super.onPause();
    DroiAnalytics.onPause(BaseActivity.this);
}
```
2）	用户点赞和评论按钮记录，主要是为了记录用户的活跃时间段以及互动的意愿。按钮的点击记录通过DroiBaaS统计SDK的自定义事件来实现
```
@Override
public void onItemClick(ActionItem actionitem, int position) {
    switch (position) {
        case 0://点赞、取消点赞
                DroiAnalytics.onEvent(context,"Favort");
            break;
        case 1://发布评论
                DroiAnalytics.onEvent(context,"Comment");
            break;
        default:
            break;
    }
}
```
在DroiBaaS后台能够看到所有用户的详细使用数据了
![](http://ojx2540cr.bkt.clouddn.com/%E7%BB%9F%E8%AE%A1.png)

##开发总结
整个开发过程大概是一周时间，之后大概又花了一周时间做了一些UI和逻辑的优化，相比在企业中完成一个App的开发上线动辄两三个月的开发周期来说，已经是很快很快了，这主要得益于几点

 1. 前期的系统框架设计尽量全面完善，这样给后期的Coding的工作省下不少时间
 2. 使用了不少的开源框架，省了不少的事情，而且比自己写代码也要稳定高效
 3. 最重要的是后台功能开发采用了DroiBaaS，相比传统的开发方式，这个省下来的时间最多

目前我的日记功能还没开发完成，下一步还计划加入支付功能，可以进行打赏；再添加聊天功能，用户之间可以进行一些交互；再添加分享视频以及一键分享到其他社交平台功能。其中支付和IM功能我也咨询过DroiBaaS，他们后续也都会支持，很赞！云后台服务的高度封装化，给App的开发带来了巨大的便利，新形态的第二代一站式后端云服务也必将是未来3到5年炙手可热的开发工具。


文档最后放上福利：[源码工程GitHub链接](https://github.com/zzsawyer/CampusDiary)





