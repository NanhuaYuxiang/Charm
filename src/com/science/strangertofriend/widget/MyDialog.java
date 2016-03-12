package com.science.strangertofriend.widget;

import android.content.Context;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

/**
 * @description Sweet-alert-dialog-library��Դ������ʾ��
 * 
 * 
 */

public class MyDialog {

	private Context mContext;

	public MyDialog(Context context) {
		mContext = context;
	}

	// �ɹ���ʾ��
	public void successDialog(String text) {
		new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
				.setTitleText(text).show();
	}

	// ʧ����ʾ��
	public void errorDialog(String title, String content) {
		new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
				.setTitleText(title).setContentText(content).show();
	}
	
}
