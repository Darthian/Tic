package com.sarasty.tic;

import android.view.*;
import android.app.*;
import android.content.*;
import android.widget.*;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	private TicGame mGame;
	
	private Button mBoardButtons[];

	private TextView mInfoTextView;

	static final int DIALOG_DIFFICULTY_ID = 0;
	static final int DIALOG_QUIT_ID = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBoardButtons = new Button[mGame.BOARD_SIZE];

		mBoardButtons[0] = (Button) findViewById(R.id.one);
		mBoardButtons[1] = (Button) findViewById(R.id.two);
		mBoardButtons[2] = (Button) findViewById(R.id.three);
		mBoardButtons[3] = (Button) findViewById(R.id.four);
		mBoardButtons[4] = (Button) findViewById(R.id.five);
		mBoardButtons[5] = (Button) findViewById(R.id.six);
		mBoardButtons[6] = (Button) findViewById(R.id.seven);
		mBoardButtons[7] = (Button) findViewById(R.id.eight);
		mBoardButtons[8] = (Button) findViewById(R.id.nine);
		
		mInfoTextView = (TextView) findViewById(R.id.information);
		
		mGame = new TicGame();
		
		startNewGame();
	}
	
	public class ButtonClickListener implements View.OnClickListener {

		int location;

		public ButtonClickListener(int location) {
			this.location = location;
		}

		@Override
		public void onClick(View view) {

			if (mBoardButtons[location].isEnabled()) {
				setMove(TicGame.HUMAN_PLAYER, location);

				int winner = mGame.checkForWinner();

				if (winner == 0) {
					mInfoTextView.setText("It's Android'sturn.");
					int move = mGame.getComputerMove();
					setMove(TicGame.COMPUTER_PLAYER, move);
					winner = mGame.checkForWinner();
				}

				if (winner == 0)
					mInfoTextView.setText("It's your turn.");
				else if (winner == 1)
					mInfoTextView.setText("It's a tie!");
				else if (winner == 2)
					mInfoTextView.setText("You won!");
				else
					mInfoTextView.setText("Android won!");

			}
		}		
	}
	
	private void setMove(char player, int location) {

		mGame.setMove(player, location);
		mBoardButtons[location].setEnabled(false);
		mBoardButtons[location].setText(String.valueOf(player));

		if (player == TicGame.HUMAN_PLAYER)
			mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
		else
			mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
	private void startNewGame() {
		mGame.clearBoard();
		for (int i = 0; i < mBoardButtons.length; i++) {

			mBoardButtons[i].setText("");
			mBoardButtons[i].setEnabled(true);
			mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));

		}
		mInfoTextView.setText("You go first.");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_game:
			startNewGame();
			return true;
		case R.id.ai_difficulty:
			showDialog(DIALOG_DIFFICULTY_ID);
			return true;
		case R.id.quit:
			showDialog(DIALOG_QUIT_ID);
			return true;
		}
		return false;
	}
	
//	@Override
	protected Dialog onCreateDialog(int id) {
		final AlertDialog levelDialog;
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
		case DIALOG_DIFFICULTY_ID:

			builder.setTitle(R.string.difficulty_choose);

			final CharSequence[] levels = {
					getResources().getString(R.string.difficulty_easy),
					getResources().getString(R.string.difficulty_harder),
					getResources().getString(R.string.difficulty_expert) };

			// TODO: Set selected, an integer (0 to n-1), for the Difficulty
			// dialog.
			// selected is the radio button that should be selected.

			int selected = -1;

			builder.setSingleChoiceItems(levels, selected,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							
							switch (item) {
							case 0:
								mGame.setDifficultyLevel(DifficultyLevel.Easy);
								break;
							case 1:
								mGame.setDifficultyLevel(DifficultyLevel.Harder);
								break;
							case 2:
								mGame.setDifficultyLevel(DifficultyLevel.Expert);
								break;
							}
							
							dialog.dismiss(); // Close dialog

							// TODO: Set the diff level of mGame based on which
							// item was selected.

							// Display the selected difficulty level
							Toast.makeText(getApplicationContext(),
									levels[item], Toast.LENGTH_SHORT).show();
						}
					});
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
	
	

}
