package com.science.strangertofriend.fragment;

import com.science.strangertofriend.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ������������û��ɵ�
 * 
 * @author lilin
 * @date 2015��11��9�� .����11:54:55
 * @blog www.gaosililn.iteye.com
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class Task_Publish_UnComplete extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// container.getChildAt(0);
		inflater.inflate(R.layout.task_publish_uncomplete_fragment, null, false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
