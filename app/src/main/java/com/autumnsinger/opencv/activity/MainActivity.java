package com.autumnsinger.opencv.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;


import com.autumnsinger.opencv.R;
import com.autumnsinger.opencv.adapter.ImageAdapter;
import com.autumnsinger.opencv.util.BindUtil;
import com.autumnsinger.opencv.util.Callback;
import com.autumnsinger.opencv.util.ConstantMorph;
import com.autumnsinger.opencv.util.Invoker;
import com.autumnsinger.opencv.util.PictureUtils;
import com.autumnsinger.opencv.util.Util;
import com.autumnsinger.opencv.view.DragLayout;
import com.autumnsinger.opencv.view.DragLayout.DragListener;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.opencv.android.Utils;

import me.nereo.multi_image_selector.MultiImageSelector;

public class MainActivity extends Activity {

	private DragLayout dl;
	private GridView gv_img;
	private ImageAdapter adapter;
	private ListView lv;
	private TextView tv_noimg;
	private ImageView iv_icon, iv_bottom;
	private RadioGroup threshold_type_group;
	private RadioGroup  morph_type_group, morph_ele_type_group;//形态学操作
	private SeekBar minValue, maxValue;//二值化进度条
	private TextView minValueText, maxValueText;//二值化进度
	private ImageView picture,picture2, picture_result;
	private SeekBar x_distanceBar, y_distanceBar;//x,y坐标平移比例进度条
	private SeekBar scalingBar;//缩放比例
	private SeekBar rotatingBar;//旋转角度
	private SeekBar morphBar;//形态学操作Element大小
	private TextView ratatingText;//显示旋转角度
	private TextView morphText;//形态学操作Element大小

	private EditText sigma_x, sigma_y;//高斯模糊的取值
	private Button saveResultImg;//保存图片

	private WebView baidu_st_webview;

	private ArrayList<String> mSelectPath;
	public static int THRESHOLD_TYPE = 0;
	public static final int TAKE_PHOTO = 1;
	public static final int CROP_PHOTO = 2;
	private static final int REQUEST_IMAGE = 3;

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Util.initImageLoader(this);
		initDragLayout();
		initView();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

		shake();
	}

	private void initDragLayout() {
		dl = (DragLayout) findViewById(R.id.dl);
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
				lv.smoothScrollToPosition(new Random().nextInt(30));
			}

			@Override
			public void onClose() {
				shake();
			}

			@Override
			public void onDrag(float percent) {
				ViewHelper.setAlpha(iv_icon, 1 - percent);
			}
		});
	}

	private void initView() {
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
		gv_img = (GridView) findViewById(R.id.gv_img);
		tv_noimg = (TextView) findViewById(R.id.iv_noimg);
		picture = (ImageView) findViewById(R.id.picture);
		picture2 = (ImageView) findViewById(R.id.picture2);
		picture_result = (ImageView) findViewById(R.id.picture_result);
		threshold_type_group = (RadioGroup) findViewById(R.id.threshold_type_group) ;
		morph_type_group = (RadioGroup) findViewById(R.id.morph_type_group) ;
		morph_ele_type_group = (RadioGroup) findViewById(R.id.morph_ele_type_group) ;

		minValue = (SeekBar) findViewById(R.id.minValue) ;
		maxValue = (SeekBar) findViewById(R.id.maxValue) ;
		x_distanceBar = (SeekBar) findViewById(R.id.x_distanceBar) ;
		y_distanceBar = (SeekBar) findViewById(R.id.y_distanceBar) ;
		scalingBar = (SeekBar) findViewById(R.id.scalingBar) ;
		rotatingBar = (SeekBar) findViewById(R.id.rotatingBar) ;
		morphBar = (SeekBar) findViewById(R.id.morphBar) ;

		minValueText = (TextView) findViewById(R.id.minValueText);
		maxValueText = (TextView) findViewById(R.id.maxValueText);
		ratatingText = (TextView) findViewById(R.id.ratatingText);
		morphText = (TextView) findViewById(R.id.morphText);

		sigma_x = (EditText) findViewById(R.id.sigma_x);
		sigma_y = (EditText) findViewById(R.id.sigma_y);

		saveResultImg = (Button) findViewById(R.id.saveResultImg);

		baidu_st_webview = (WebView) findViewById(R.id.baidu_st_webview) ;

		gv_img.setFastScrollEnabled(true);
		adapter = new ImageAdapter(this);
		gv_img.setAdapter(adapter);
		gv_img.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent = new Intent(MainActivity.this,
						ImageActivity.class);
				intent.putExtra("path", adapter.getItem(position));
				startActivity(intent);
			}
		});

		saveResultImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Bitmap image = ((BitmapDrawable) picture_result.getDrawable()).getBitmap(); // obtaining the Bitmap
					saveImageToGallery(getApplicationContext(), image);
					Util.t(getApplicationContext(), "Save Result Image OK");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		//绑定一个匿名监听器
		threshold_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				//获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton)findViewById(radioButtonId);
				switch (rb.getText().toString()){
					case "Binary": THRESHOLD_TYPE = 0;break;
					case "Binary Inverted": THRESHOLD_TYPE = 1;break;
					case "Threshold Truncated": THRESHOLD_TYPE = 2;break;
					case "Threshold to Zero": THRESHOLD_TYPE = 3;break;
					case "Threshold to Zero Inverted": THRESHOLD_TYPE = 4;break;

				}
			}
		});

		//二值化功能所需
		BindUtil.binaryBarBind(picture, picture_result , minValue, maxValue, minValueText, maxValueText);

		//图片平移功能所需
		BindUtil.xy_distanceBarBind(picture, picture_result , x_distanceBar, y_distanceBar);

		//图片缩放功能所需
		BindUtil.scalingBarBind(picture, picture_result , scalingBar);
		//图片旋转
		BindUtil.rotatingBarBind(picture, picture_result , rotatingBar, ratatingText);

		//形态学
		BindUtil.bindMorphOperate(this, picture, picture_result, morph_type_group, morph_ele_type_group, morphText, morphBar);


		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				R.layout.item_text, new String[]{"Pick image",
				"Histogram Calculation","Histogram Equalization",
				"Operation Add","Operation Subtract","Operation Multiply ","Operation Divide",
				"Pic Translate","Pic Scaling","Pic Rotating",
				"AverageBlur","MedianBlur","GaussianBlur",
				"Edge Detection Roberts","Edge Detection Prewitt","Edge Detection Sobel","Edge Detection Canny",
				"To binaryImage","To binaryImage by Otsu","Distance Transform","Skeleton","Reconstruction",
				"To Grey Photo",
				"Morphology Operation",
				"Baidu ShiTu", "Baidu ST Browser"
				}));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				findViewById(R.id.binaryLayout).setVisibility(View.GONE);
				findViewById(R.id.translateLayout).setVisibility(View.GONE);
				findViewById(R.id.scalingLayout).setVisibility(View.GONE);
				findViewById(R.id.rotatingLayout).setVisibility(View.GONE);
				findViewById(R.id.morphLayout).setVisibility(View.GONE);
				findViewById(R.id.gaussianblurLayout).setVisibility(View.GONE);
				findViewById(R.id.baidu_st_webview).setVisibility(View.GONE);
				findViewById(R.id.img_display).setVisibility(View.VISIBLE);

				if(position == 0){
					//选择图片
					pickImage();
				}else if(position == 1){
					//Histogram Calculation
					PictureUtils.calcHist(picture, picture_result);
				}else if(position == 2){
					//Histogram Equalization
					PictureUtils.equalHist(picture, picture_result);
				}else if(position == 3){
					//图片加操作
					PictureUtils.operateTwoPic(picture, picture2, picture_result, 0 );
				}else if(position == 4){
					//图片减法操作
					PictureUtils.operateTwoPic(picture, picture2, picture_result, 1 );
				}else if(position == 5){
					//图片乘法操作
					PictureUtils.operateTwoPic(picture, picture2, picture_result, 2 );
				}else if(position == 6){
					//图片除法操作
					PictureUtils.operateTwoPic(picture, picture2, picture_result, 3 );
				}else if(position == 7){
					findViewById(R.id.translateLayout).setVisibility(View.VISIBLE);
					//平移
					PictureUtils.translate(picture, picture_result , x_distanceBar, y_distanceBar);
				}else if(position == 8){
					findViewById(R.id.scalingLayout).setVisibility(View.VISIBLE);
					//缩放
					PictureUtils.scaling(picture, picture_result , scalingBar);
				}else if(position == 9){
					findViewById(R.id.rotatingLayout).setVisibility(View.VISIBLE);
					//旋转
					PictureUtils.rotating(picture, picture_result , rotatingBar);
				}else if(position == 10){
					//AverageBlur
					PictureUtils.toBlur(picture, picture_result, 0, null, null);
				}else if(position == 11){
					//MedianBlur
					PictureUtils.toBlur(picture, picture_result, 1, null, null);
				}else if(position == 12){
					Util.t(getApplicationContext(), "loading...");
					//GaussianBlur
					findViewById(R.id.gaussianblurLayout).setVisibility(View.VISIBLE);
					PictureUtils.toBlur(picture, picture_result, 2, sigma_x, sigma_y);
				}else if(position == 13){
					Util.t(getApplicationContext(), "loading...");
					//边缘检测 Roberts
					PictureUtils.edgeDetectationRoberts(picture, picture_result);
				}else if(position == 14){
					//边缘检测 Prewitt
					PictureUtils.edgeDetectationPrewitt(picture, picture_result);
				}else if(position == 15){
					//边缘检测 Sobel
					PictureUtils.edgeDetectationSobel(picture, picture_result);
				}else if(position == 16){
					//边缘检测 Canny
					PictureUtils.edgeDetectationdCanny(picture, picture_result);
				}else if(position == 17){
					findViewById(R.id.binaryLayout).setVisibility(View.VISIBLE);
					//二值化
					PictureUtils.binaryPic(picture, picture_result, minValue, maxValue);
				}else if(position == 18){
					//二值化 Otsu
					PictureUtils.binaryPicByOtsu(picture, picture_result);
				}else if(position == 19){
					//距离变换
					PictureUtils.distanceTransform(picture, picture_result);
				}else if(position == 20){
					//骨架
					PictureUtils.skeleton(picture, picture_result);
				}else if(position == 21){
					//Reconstruction
					PictureUtils.reconstruction(picture, picture_result);
				}else if(position == 22){
					//灰度化
					PictureUtils.greyPic(picture, picture_result);
				}else if(position == 23){
					findViewById(R.id.morphLayout).setVisibility(View.VISIBLE);
					//Morphology Operation
					PictureUtils.morphologyOperate(picture, picture_result, ConstantMorph.MORPH_OPERATION_TYPE, ConstantMorph.MORPH_ELEMENT, morphBar);
				}else if(position == 24){
					Util.t(getApplicationContext(), "loading...");
					findViewById(R.id.img_display).setVisibility(View.GONE);
					findViewById(R.id.baidu_st_webview).setVisibility(View.VISIBLE);
					//点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
					baidu_st_webview.setOnKeyListener(new View.OnKeyListener() {
						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							if (event.getAction() == KeyEvent.ACTION_DOWN) {
								if (keyCode == KeyEvent.KEYCODE_BACK && baidu_st_webview.canGoBack()) {  //表示按返回键时的操作
									baidu_st_webview.goBack();   //后退
									//webview.goForward();//前进
									return true;    //已处理
								}
							}
							return false;
						}
					});
					baidu_st_webview.getSettings().setJavaScriptEnabled(true);
					baidu_st_webview.setWebViewClient(new WebViewClient() {
						public boolean shouldOverrideUrlLoading(WebView view, String url)
						{ //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
							view.loadUrl(url);
							return true;
						}
					});
					baidu_st_webview.loadUrl("http://image.baidu.com/search/wiseindex?tn=wiseindex&fr=shitu");
				}else if(position == 25){
					Util.t(getApplicationContext(), "loading...");
					String url = "http://image.baidu.com/search/wiseindex?tn=wiseindex"; // web address
					Intent b_intent = new Intent(Intent.ACTION_VIEW);
					b_intent.setData(Uri.parse(url));
					startActivity(b_intent);
				}else {
					Util.t(getApplicationContext(), "click " + position);
				}
			}
		});
		iv_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dl.open();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadImage();
	}

	/**
	 * 通过拍照或者从相册中选择图片
	 */
	private void pickImage() {
		boolean showCamera = true;
		int maxNum = 2;

		MultiImageSelector selector = MultiImageSelector.create(MainActivity.this);
		selector.showCamera(showCamera);
		selector.count(maxNum);

		selector.multi();

		selector.origin(mSelectPath);
		selector.start(MainActivity.this, REQUEST_IMAGE);
	}
	private void loadImage() {
		new Invoker(new Callback() {
			@Override
			public boolean onRun() {
//				adapter.addAll(Util.getGalleryPhotos(MainActivity.this));
				return adapter.isEmpty();
			}

			@Override
			public void onBefore() {
				// 转菊花
			}

			@Override
			public void onAfter(boolean b) {
				adapter.notifyDataSetChanged();
				if (b) {
					//tv_noimg.setVisibility(View.VISIBLE);
					//chooseFromAlbum.setVisibility(View.VISIBLE);
					picture.setVisibility(View.VISIBLE);
				} else {
					tv_noimg.setVisibility(View.GONE);
					String s = "file://" + adapter.getItem(0);
					//ImageLoader.getInstance().displayImage(s, iv_icon);
					//ImageLoader.getInstance().displayImage(s, iv_bottom);
				}
//				shake();
			}
		}).start();

	}

	private void shake() {
		iv_icon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case TAKE_PHOTO:
				if (resultCode == RESULT_OK) {
					Bundle extras = data.getExtras();
					Bitmap imageBitmap = (Bitmap) extras.get("data");
					picture.setImageBitmap(imageBitmap);
				}
				break;
			case CROP_PHOTO:
				if (resultCode == RESULT_OK) {
					try {
						Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
						picture.setImageBitmap(bitmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case REQUEST_IMAGE:
				if(resultCode == RESULT_OK){
					mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
					for(int i = 0; i<mSelectPath.size(); i++){
						String p = mSelectPath.get(i);
						if(i == 0){
							ImageLoader.getInstance().displayImage("file://" + p, picture);
						}else if(i == 1){
							ImageLoader.getInstance().displayImage("file://" + p, picture2);
							picture2.setVisibility(View.VISIBLE);
						}
					}
					mSelectPath.clear();
				}
				break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private  void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(), "AutumnSinger");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		String fileName = "AutumnSinger" + timeStamp + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
	}

	private class MyWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			if (Uri.parse(url)
					.getHost()
					.equals("http://www.cnblogs.com/mengdd/archive/2013/02/27/2935811.html")
					|| Uri.parse(url).getHost()
					.equals("http://music.baidu.com/"))
			{
				// This is my web site, so do not override; let my WebView load
				// the page

				// 这是官网上的例子，但是我点击特定链接的时候仍然是用浏览器而不是用自己的WebView打开，加上下面这句view.loadUrl(url)仍然是用浏览器，无解，不知道哪里出了问题
				// view.loadUrl(url);
				return false;
			}
			// Otherwise, the link is not for a page on my site, so launch
			// another Activity that handles URLs
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
		}
	}
}
