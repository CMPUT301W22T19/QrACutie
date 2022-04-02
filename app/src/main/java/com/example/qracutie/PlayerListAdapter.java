package com.example.qracutie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An array adapter which is used to display a list of Player profiles within the
 * MainActivity. For each player, displays an image and an amount of points
 */
public class PlayerListAdapter extends ArrayAdapter<Player> {
    private ArrayList<Player> players;
    private Context context;

    /**
     * generates a new instance of the PlayerListAdapter class
     * @param context the context of the caller class
     * @param players an array list of Player objects
     */
    public PlayerListAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
        this.players = players;
    }

    /**
     * generates and returns a View element representing a single Player, which is part
     * of a ListView
     * @param position the index of the view within a list
     * @param convertView an old view to reuse
     * @param parent the list which contains the view
     * @return View element
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_player, parent, false);
        }

        Player player = players.get(position);

        // set player image
        ImageView playerImage = view.findViewById(R.id.player_list_image);
        playerImage.setImageBitmap(player.getProfilePic());

        // set player username
        TextView playerUsername = view.findViewById(R.id.player_list_name);
        playerUsername.setText(player.getUsername());

        // set player score
        TextView playerScore = view.findViewById(R.id.player_list_score);
        playerScore.setText(player.getTotalCodes());

        // set player rank
        TextView playerRank = view.findViewById(R.id.player_list_rank);
        playerScore.setText(player.getTotalCodes());

        return view;
    }
}