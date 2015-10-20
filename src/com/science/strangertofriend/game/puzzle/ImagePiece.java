package com.science.strangertofriend.game.puzzle;

import android.graphics.Bitmap;

/**
 * @description
 * 
 * @author ĞÒÔËScience ³ÂÍÁŸö
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-24
 * 
 */

public class ImagePiece {
	private int index;
	private Bitmap bitmap;

	public ImagePiece() {
	}

	public ImagePiece(int index, Bitmap bitmap) {
		this.index = index;
		this.bitmap = bitmap;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public String toString() {
		return "ImagePiece [index=" + index + ", bitmap=" + bitmap + "]";
	}

}
