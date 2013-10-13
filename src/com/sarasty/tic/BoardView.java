package com.sarasty.tic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BoardView extends View {

	// private mTouchListener mTouchListener = new mTouchListener();
	//
	// public void setOnTouchListener(mTouchListener mTouchListener){
	// this.mTouchListener = mTouchListener;
	// }

	public static final int GRID_WIDTH = 10;
	public static final int BLACK = 00;

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private Bitmap mHumanBitmap;
	private Bitmap mComputerBitmap;

	private TicGame mGame;

	public BoardView(Context context) {
		super(context);
		initialize();
	}

	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public void initialize() {
		mHumanBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.x);
		mComputerBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.o);
	}

	public void setGame(TicGame mGame) {
		this.mGame = mGame;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Determine the width and height of the View
		int boardWidth = getWidth();
		int boardHeight = getHeight();

		// Make thick, light gray lines
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(BLACK);

		// Draw the two vertical board lines
		int cellWidth = boardWidth / 3;
		canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
		canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);
		canvas.drawLine(0, cellWidth, boardHeight, cellWidth, mPaint);
		canvas.drawLine(0, cellWidth * 2, boardHeight, cellWidth * 2, mPaint);

		// Draw all the X and O images
		for (int i = 0; i < TicGame.BOARD_SIZE; i++) {
			int col = i % 3;
			int row = i / 3;

			// Define the boundaries of a destination rectangle for the
			// image
			int left = (cellWidth + GRID_WIDTH / 2) * col;
			int top = (cellWidth + GRID_WIDTH / 2) * row;
			int right = (cellWidth + GRID_WIDTH / 2) * (col + 1);
			int bottom = (cellWidth + GRID_WIDTH / 2) * (row + 1);

			if (mGame != null
					&& mGame.getBoardOccupant(i) == TicGame.HUMAN_PLAYER) {
				canvas.drawBitmap(mHumanBitmap, null, // src
						new Rect(left, top, right, bottom), // dest
						null);
			} else if (mGame != null
					&& mGame.getBoardOccupant(i) == TicGame.COMPUTER_PLAYER) {
				canvas.drawBitmap(mComputerBitmap, null, // src
						new Rect(left, top, right, bottom), // dest
						null);
			}
		}
	}

	public int getBoardCellWidth() {
		return getWidth() / 3;
	}

	public int getBoardCellHeight() {
		return getHeight() / 3;
	}
}
