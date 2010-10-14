package net.todd.scorekeeper;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PickPlayersView {
	private final LinearLayout mainView;
	private final TableLayout allPlayersTable;
	private final Context context;

	private Listener selectedPlayersChangedListener;
	private boolean isCurrentPlayerSelected;
	private int currentPlayer;

	private final ListenerManager cancelButtonListenerManager = new ListenerManager();
	private final ListenerManager nextButtonListenerManager = new ListenerManager();
	private final ListenerManager selectedPlayersChangedListenerManager = new ListenerManager();

	public PickPlayersView(Context context) {
		this.context = context;

		mainView = new LinearLayout(context);
		mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mainView.setBackgroundColor(0xFF3399CC);
		mainView.setOrientation(LinearLayout.VERTICAL);

		TextView title = new TextView(context);
		title.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		title.setText("Pick the players");
		title.setTextSize(30);
		title.setTextColor(0xFF000000);
		title.setGravity(Gravity.CENTER_HORIZONTAL);
		mainView.addView(title);

		LinearLayout controlView = new LinearLayout(context);
		controlView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		controlView.setOrientation(LinearLayout.HORIZONTAL);
		controlView.setGravity(Gravity.CENTER_HORIZONTAL);
		mainView.addView(controlView);

		Button cancelButton = new Button(context);
		cancelButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		cancelButton.setText("Cancel");
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelButtonListenerManager.notifyListeners();
			}
		});
		controlView.addView(cancelButton);

		Button nextButton = new Button(context);
		nextButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		nextButton.setText("Next");
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextButtonListenerManager.notifyListeners();
			}
		});
		controlView.addView(nextButton);

		allPlayersTable = new TableLayout(context);
		allPlayersTable.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		allPlayersTable.setColumnStretchable(0, true);
		mainView.addView(allPlayersTable);
	}

	public View getView() {
		return mainView;
	}

	public void setAllPlayers(List<Player> allPlayers) {
		allPlayersTable.removeAllViews();
		for (final Player player : allPlayers) {
			TableRow playerRow = new TableRow(context);
			playerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT));
			allPlayersTable.addView(playerRow);

			TextView playerName = new TextView(context);
			playerName.setText(player.getName());
			playerName.setTextSize(30);
			playerName.setTextColor(0xFF000000);
			playerRow.addView(playerName);

			CheckBox playerSelection = new CheckBox(context);
			playerSelection.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					isCurrentPlayerSelected = isChecked;
					currentPlayer = player.getId();
					selectedPlayersChangedListener.handle();
				}
			});
			playerRow.addView(playerSelection);
		}
	}

	public void addSelectedPlayersChangedListener(Listener listener) {
		selectedPlayersChangedListenerManager.addListener(listener);
	}

	public boolean isCurrentPlayerSelected() {
		return isCurrentPlayerSelected;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public void addNextButtonListener(final Listener listener) {
		nextButtonListenerManager.addListener(listener);
	}

	public void addCancelButtonListener(final Listener listener) {
		cancelButtonListenerManager.addListener(listener);
	}

	public void popupErrorMessage() {
		new AlertDialog.Builder(context).setMessage("You must select at least 2 players.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
	}
}
