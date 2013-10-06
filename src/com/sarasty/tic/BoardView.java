package com.sarasty.tic;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

public class BoardView extends View {

	public BoardView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public static final int GRID_WIDTH = 6;
	
	private Bitmap mHumanBitmap;
	private Bitmap mComputerBitmap;
	
}
