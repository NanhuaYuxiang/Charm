package com.science.strangertofriend.adapter;

import java.util.List;

import com.science.strangertofriend.R;
import com.science.strangertofriend.ui.AddTaskActivity;
import com.science.strangertofriend.utils.MyPopupWindow;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 
 * @author ÕÔöÎ     
 * @description ×Ô¶¨Òåspinner
 * @email apologizetoher@Gmail.com / 491498408@qq.com
 * @date 2015-10-26 ÏÂÎç7:17:04
 */
public class MySpinnerAdapter extends BaseAdapter{
	
	private int mPosition;
	private List<String> mItems;
	private MyPopupWindow mWindow;
	private AddTaskActivity mActivity;	
	private onItemClickListener mListener;
	
	public MySpinnerAdapter(MyPopupWindow window,AddTaskActivity activity,List<String> items){
		this.mActivity=activity;
		this.mItems=items;
		this.mWindow=window;
	}
	
	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	private class ViewHolder { // itemçš?4ä¸ªæ§ä»?
		public TextView text;
	}
			
	public View getView(final int arg0, final View arg1, ViewGroup arg2) {
		//è·å–è®¾ç½®å¥½çš„listener
		mListener=mWindow.getListener();
		View view=arg1;
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mActivity, R.layout.myspinner_list_item, null);
			holder = new ViewHolder();
			holder.text=(TextView) view.findViewById(R.id.tv_text);
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		holder.text.setText(mItems.get(arg0));
		
		view.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				mPosition=arg0;	
				mWindow.close();
				mListener.click(mPosition,arg1);
			}
		});
		return view;
	}
	//å®šä¹‰æ¥å£å’Œä¸€ä¸ªä¸ºå®ç°çš„æ–¹æ³?
	public interface onItemClickListener{
		public void click(int position,View view);
	}	
}
