package net.todd.scorekeeper;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ManagePlayersView {
	private final Context context;
	
	private final LinearLayout mainView;
	private final TableLayout tableView;
	private final EditText playerNameText;
	private final Button addPlayerButton;

	private int playerToRemove;
	private IListener playerRemovedListener;
	
	public ManagePlayersView(Context context) {
		this.context = context;
		
		mainView = new LinearLayout(context);
		mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mainView.setOrientation(LinearLayout.VERTICAL);
		mainView.setBackgroundColor(0xFF3399CC);
		
		TextView title = new TextView(context);
		title.setText("Manager Players");
		title.setTextSize(30);
		title.setTextColor(0xFF000000);
		title.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		title.setGravity(Gravity.CENTER_HORIZONTAL);
		mainView.addView(title);

		tableView = new TableLayout(context);
		tableView.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
		tableView.setColumnStretchable(0, true);
		mainView.addView(tableView);

		TableRow controlsRow = new TableRow(context);
		controlsRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		tableView.addView(controlsRow);
		
		playerNameText = new EditText(context);
		controlsRow.addView(playerNameText);
		
		addPlayerButton = new Button(context);
		addPlayerButton.setText("Add Player");
		controlsRow.addView(addPlayerButton);
	}

	public View getView() {
		return mainView;
	}
	
	public String getPlayerNameText() {
		return playerNameText.getText().toString();
	}
	
	public void clearPlayerNameText() {
		playerNameText.setText("");
	}
	
	public void addAddPlayerButtonListener(final IListener listener) {
		addPlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.handle();
			}
		});
	}
	
	private void addPlayer(final int playerId, String playerName) {
		TableRow playerRow = new TableRow(context);
		playerRow.setId(playerId);
		playerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		tableView.addView(playerRow);

		TextView player = new TextView(context);
		player.setText(playerName);
		player.setTextSize(30);
		player.setTextColor(0xFF000000);
		playerRow.addView(player);
		
		Button removePlayerButton = new Button(context);
		removePlayerButton.setText("Remove");
		removePlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playerToRemove = playerId;
				playerRemovedListener.handle();
			}
		});
		playerRow.addView(removePlayerButton);
	}
	
	public void addPlayerRemoveListener(IListener listener) {
		playerRemovedListener = listener;
	}
	
	public int getPlayerToRemove() {
		return playerToRemove;
	}

	public void setPlayers(List<Player> players) {
		clearCurrentPlayers();
	
		for (Player player : players) {
			addPlayer(player.getId(), player.getName());
		}
	}

	private void clearCurrentPlayers() {
		while (tableView.getChildCount() > 1) {
			tableView.removeViewAt(tableView.getChildCount() - 1);		
		}
	}
}