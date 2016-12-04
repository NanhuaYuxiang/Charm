package com.science.strangertofriend.utils;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * @author MatrixCV
 *         FaceRect是用于表示人脸检测的结果，其中包括了 人脸的角度�?得分、检测框位置、关键点
 */
public class FaceRect {
	public float score;

	public Rect bound = new Rect();
	public Point point[];

	public Rect raw_bound = new Rect();
	public Point raw_point[];

	@Override
	public String toString() {
		return bound.toString();
	}
}
