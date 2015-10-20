package com.science.strangertofriend.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * @description 阻尼效果的scrollview
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-8
 * 
 */

public class DampView extends ScrollView {

	private static final int LEN = 0xc8;
	private static final int DURATION = 500;
	private static final int MAX_DY = 200;
	private Scroller mScroller;
	TouchTool tool;
	int left, top;
	float startX, startY, currentX, currentY;
	int imageViewH;
	int rootW, rootH;
	ImageView imageView;
	boolean scrollerType;

	private GestureDetector mGestureDetector;
	private int Scroll_height = 0;
	private int view_height = 0;
	protected Field scrollView_mScroller;
	private static final String TAG = "DampView";

	public DampView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public DampView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);

		mGestureDetector = new GestureDetector(context, new YScrollDetector());
		setFadingEdgeLength(0);
	}

	public DampView(Context context) {
		super(context);

	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (!mScroller.isFinished()) {
			return super.onTouchEvent(event);
		}
		currentX = event.getX();
		currentY = event.getY();
		imageView.getTop();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			left = imageView.getLeft();
			top = imageView.getBottom();
			rootW = getWidth();
			rootH = getHeight();
			imageViewH = imageView.getHeight();
			startX = currentX;
			startY = currentY;
			tool = new TouchTool(imageView.getLeft(), imageView.getBottom(),
					imageView.getLeft(), imageView.getBottom() + LEN);
			break;
		case MotionEvent.ACTION_MOVE:
			if (imageView.isShown() && imageView.getTop() >= 0) {
				if (tool != null) {
					int t = tool.getScrollY(currentY - startY);
					if (t >= top && t <= imageView.getBottom() + LEN) {
						android.view.ViewGroup.LayoutParams params = imageView
								.getLayoutParams();
						params.height = t;
						imageView.setLayoutParams(params);
					}
				}
				scrollerType = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			scrollerType = true;
			mScroller.startScroll(imageView.getLeft(), imageView.getBottom(),
					0 - imageView.getLeft(),
					imageViewH - imageView.getBottom(), DURATION);
			invalidate();
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			imageView.layout(0, 0, x + imageView.getWidth(), y);
			invalidate();
			if (!mScroller.isFinished() && scrollerType && y > MAX_DY) {
				android.view.ViewGroup.LayoutParams params = imageView
						.getLayoutParams();
				params.height = y;
				imageView.setLayoutParams(params);
			}
		}
	}

	public class TouchTool {

		private int startX, startY;

		public TouchTool(int startX, int startY, int endX, int endY) {
			super();
			this.startX = startX;
			this.startY = startY;
		}

		public int getScrollX(float dx) {
			int xx = (int) (startX + dx / 2.5F);
			return xx;
		}

		public int getScrollY(float dy) {
			int yy = (int) (startY + dy / 2.5F);
			return yy;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			stopAnim();
		}
		boolean ret = super.onInterceptTouchEvent(ev);
		boolean ret2 = mGestureDetector.onTouchEvent(ev);
		return ret && ret2;
	}

	// Return false if we're scrolling in the x direction
	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (Math.abs(distanceY) > Math.abs(distanceX)) {
				return true;
			}
			return false;
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		boolean stop = false;
		if (Scroll_height - view_height == t) {
			stop = true;
		}

		if (t == 0 || stop == true) {
			try {
				if (scrollView_mScroller == null) {
					scrollView_mScroller = getDeclaredField(this, "mScroller");
				}

				Object ob = scrollView_mScroller.get(this);
				if (ob == null || !(ob instanceof Scroller)) {
					return;
				}
				Scroller sc = (Scroller) ob;
				sc.abortAnimation();

			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	private void stopAnim() {
		try {
			if (scrollView_mScroller == null) {
				scrollView_mScroller = getDeclaredField(this, "mScroller");
			}

			Object ob = scrollView_mScroller.get(this);
			if (ob == null) {
				return;
			}
			Method method = ob.getClass().getMethod("abortAnimation");
			method.invoke(ob);
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}
	}

	@Override
	protected int computeVerticalScrollRange() {
		Scroll_height = super.computeVerticalScrollRange();
		return Scroll_height;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed == true) {
			view_height = b - t;
		}
	}

	@Override
	public void requestChildFocus(View child, View focused) {
		if (focused != null && focused instanceof WebView) {
			return;
		}
		super.requestChildFocus(child, focused);
	}

	/**
	 * 获取一个对象隐藏的属性，并设置属性为public属性允许直接访问
	 * 
	 * @return {@link Field} 如果无法读取，返回null；返回的Field需要使用者自己缓存，本方法不做缓存�?
	 */
	public static Field getDeclaredField(Object object, String field_name) {
		Class<?> cla = object.getClass();
		Field field = null;
		for (; cla != Object.class; cla = cla.getSuperclass()) {
			try {
				field = cla.getDeclaredField(field_name);
				field.setAccessible(true);
				return field;
			} catch (Exception e) {

			}
		}
		return null;
	}
}
