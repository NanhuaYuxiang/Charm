package com.science.strangertofriend.ui;

import com.science.strangertofriend.R;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class CallFragment extends Fragment implements OnClickListener{
	
	private CircleImageView btnCallAct;
	private View mFrag;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mFrag = inflater
				.inflate(R.layout.activity_call, container, false);
		init();
		return mFrag;
		
	}
	private void init() {
		// TODO Auto-generated method stub
		btnCallAct = (CircleImageView) mFrag.findViewById(R.id.btnCallAct);
		btnCallAct.setImageResource(R.drawable.call);
		btnCallAct.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnCallAct:
			Intent intentCall = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" +"110" ));
			startActivity(intentCall);
			break;

		default:
			break;
		}
	}

}
