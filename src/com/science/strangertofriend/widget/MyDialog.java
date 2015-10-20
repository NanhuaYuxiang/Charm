package com.science.strangertofriend.widget;

import android.content.Context;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @description Sweet-alert-dialog-library开源动画提示框
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-4-26
 * 
 */

public class MyDialog {

	private Context mContext;

	public MyDialog(Context context) {
		mContext = context;
	}

	// 成功提示框
	public void successDialog(String text) {
		new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
				.setTitleText(text).show();
	}

	// 失败提示框
	public void errorDialog(String title, String content) {
		new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
				.setTitleText(title).setContentText(content).show();
	}
}
