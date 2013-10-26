package com.sarasty.tic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TicGame mGame;

	private static Button mBoardButtons[];

	private TextView mInfoTextView;
	private TextView mAndroidScoreTextView;
	private TextView mHumanScoreTextView;
	private TextView mTiesTextView;

	private static int mAndroidScore = 0;
	private static int mHumanScore = 0;
	private static int mTies = 0;

	private BoardView mBoardView;
	private boolean mGameOver;

//	static final int DIALOG_DIFFICULTY_ID = 0;
	static final int DIALOG_QUIT_ID = 1;
	static final int DIALOG_CLEAN_ID = 2;

	private MediaPlayer mHumanMediaPlayer;
	private MediaPlayer mComputerMediaPlayer;

	private char mGoFirst;

	private SharedPreferences mPrefs;
	
	private boolean mSoundOn = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.new_game:
			startNewGame();
			return true;
		case R.id.settings: 
		    startActivityForResult(new Intent(this, Settings.class), 0);     	
		    return true;
		case R.id.clean_score:
			showDialog(DIALOG_CLEAN_ID);
			return true;
		case R.id.quit:
			showDialog(DIALOG_QUIT_ID);
			return true;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		mBoardButtons = new Button[mGame.BOARD_SIZE];

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mInfoTextView = (TextView) findViewById(R.id.information);
		mAndroidScoreTextView = (TextView) findViewById(R.id.androidScore);
		mHumanScoreTextView = (TextView) findViewById(R.id.humanScore);
		mTiesTextView = (TextView) findViewById(R.id.ties);

		mGame = new TicGame();
		mBoardView = (BoardView) findViewById(R.id.board);
		mBoardView.setGame(mGame);
		mBoardView.setOnTouchListener(mTouchListener);

		displayScores();

		startNewGame();
		mGameOver = false;

		if (savedInstanceState == null) {
			startNewGame();
		}

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Restore the scores
		mHumanScore = mPrefs.getInt("mHumanScore", 0);
		mAndroidScore = mPrefs.getInt("mAndroidScore", 0);
		mTies = mPrefs.getInt("mTies", 0);
		
		// Restore the scores from the persistent preference data source
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);     
		mSoundOn = mPrefs.getBoolean("sound", true);
		String difficultyLevel = mPrefs.getString("difficulty_level", getResources().getString(R.string.difficulty_harder));
		if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
			mGame.setDifficultyLevel(DifficultyLevel.Easy);
		else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
			mGame.setDifficultyLevel(DifficultyLevel.Harder);
		else
			mGame.setDifficultyLevel(DifficultyLevel.Expert);  

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
		mGame.setBoardState(savedInstanceState.getCharArray("board"));
		mGameOver = savedInstanceState.getBoolean("mGameOver");
		mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
		mHumanScore = savedInstanceState.getInt("mHumanScore");
		mAndroidScore = savedInstanceState.getInt("mAndroidScore");
		mTies = savedInstanceState.getInt("mTies");

		mGoFirst = savedInstanceState.getChar("mGoFirst");
	}

	private void displayScores() {

		mHumanScoreTextView.setText("Victorias: " + mHumanScore);
		mAndroidScoreTextView.setText("Derrotas: " + mAndroidScore);
		mTiesTextView.setText("Empates: " + mTies);
	}

	// Listen for touches on the board
	private OnTouchListener mTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {

			// Determine which cell was touched
			int col = (int) event.getX() / mBoardView.getBoardCellWidth();
			int row = (int) event.getY() / mBoardView.getBoardCellHeight();
			int pos = row * 3 + col;

			final Handler handler = new Handler();

			if (!mGameOver && setMove(TicGame.HUMAN_PLAYER, pos)) {

				setMove(TicGame.HUMAN_PLAYER, pos);
				if (mSoundOn)
					mHumanMediaPlayer.start();

				int winner = mGame.checkForWinner();

				if (winner == 0) {
					mInfoTextView.setText(R.string.turn_computer);
					int move = mGame.getComputerMove();
					setMove(TicGame.COMPUTER_PLAYER, move);
					if(mSoundOn)
						mComputerMediaPlayer.start();
					winner = mGame.checkForWinner();
				}

				if (winner == 0)
					mInfoTextView.setText(R.string.turn_human);
				else if (winner == 1) {
					mInfoTextView.setText(R.string.result_tie);
					mTies += 1;
					mTiesTextView.setText("Empates: " + mTies);

					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							startNewGame();
						}
					}, 1000);
				} else if (winner == 2) {
					mHumanScore++;
				    mHumanScoreTextView.setText(Integer.toString(mHumanScore));
				    String defaultMessage = getResources().getString(R.string.result_human_wins);
				    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
				} else {
					mInfoTextView.setText(R.string.result_computer_wins);
					mAndroidScore += 1;
					mAndroidScoreTextView.setText("Derrotas: " + mAndroidScore);
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							startNewGame();
						}
					}, 1000);
				}
			}
			// So we aren't notified of continued events when finger is
			// moved
			return false;
		}
	};

	private boolean setMove(char player, int location) {

		if (mGame.setMove(player, location)) {
			mBoardView.invalidate(); // Redraw the board
			return true;
		}
		return false;
	}

	private void startNewGame() {

		// mGame.clearBoard();
		mGameOver = false;
		mGame.clearBoard();
		mBoardView.invalidate();
		mInfoTextView.setText(R.string.turn_human);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
		case DIALOG_CLEAN_ID:

			// Se crea el dialogo de borrado de los puntajes
			builder.setTitle(R.string.clean_message)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mAndroidScore = 0;
									mHumanScore = 0;
									mTies = 0;
									displayScores();
								}
							}).setNegativeButton(R.string.no, null);
			dialog = builder.create();

			break;

		case DIALOG_QUIT_ID:

			// Create the quit confirmation dialog
			builder.setMessage(R.string.quit_question)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									MainActivity.this.finish();
								}
							}).setNegativeButton(R.string.no, null);
			dialog = builder.create();

			break;
		}
		return dialog;
	}

	@Override
	protected void onResume() {

		super.onResume();

		mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.xsound);
		mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.osound);
	}

	@Override
	protected void onPause() {

		super.onPause();

		mHumanMediaPlayer.release();
		mComputerMediaPlayer.release();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

		outState.putCharArray("board", mGame.getBoardState());
		outState.putBoolean("mGameOver", mGameOver);
		outState.putInt("mHumanScore", Integer.valueOf(mHumanScore));
		outState.putInt("mAndroidScore", Integer.valueOf(mAndroidScore));
		outState.putInt("mTies", Integer.valueOf(mTies));
		outState.putCharSequence("info", mInfoTextView.getText());
		outState.putChar("mGoFirst", mGoFirst);
	}

	@Override
	protected void onStop() {

		super.onStop();

		// Save the current scores
		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putInt("mHumanScore", mHumanScore);
		ed.putInt("mAndroidScore", mAndroidScore);
		ed.putInt("mTies", mTies);

		ed.commit();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   	   	
	   if (requestCode == RESULT_CANCELED) {
	    	// Apply potentially new settings      
	        	
	     	mSoundOn = mPrefs.getBoolean("sound", true);
	        	
	     	String difficultyLevel = mPrefs.getString("difficulty_level", 
				getResources().getString(R.string.difficulty_harder));

	   if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
	        		mGame.setDifficultyLevel(DifficultyLevel.Easy);
	      else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
		    		mGame.setDifficultyLevel(DifficultyLevel.Harder);
	      else
		    		mGame.setDifficultyLevel(DifficultyLevel.Expert);    	       	
	      
	   }
	}
}
