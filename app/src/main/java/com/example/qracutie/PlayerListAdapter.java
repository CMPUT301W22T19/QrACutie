package com.example.qracutie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An array adapter which is used to display a list of Player profiles within the
 * MainActivity. For each player, displays an image and an amount of points
 */
public class PlayerListAdapter extends ArrayAdapter<Player> {
    private ArrayList<Player> players;
    private Context context;
    private String display = "pointTotal";

    /**
     * generates a new instance of the PlayerListAdapter class
     * @param context the context of the caller class
     * @param players an array list of Player objects
     */
    public PlayerListAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
        this.context = context;
        this.players = players;
    }

    /**
     * Sets a specific display "type" so that the adapter knows wether to print
     * the player's total points, total codes, or highest score
     * @param display the type of player attribute to display
     */
    public void setDisplay(String display) {
        this.display = display;
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
        Glide.with(context).clear(playerImage);
        if (!player.getProfileImage().equals("")) {
            Glide.with(context).asBitmap().load(Uri.parse(player.getProfileImage()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Remove this to achieve faster image loading, but less accurate
                    .skipMemoryCache(true) // Remove this to achieve faster image loading, but less accurate
                    .into(playerImage);
        } else {
            playerImage.setImageResource(R.drawable.default_profile_pic);
        }

        // set player username
        TextView playerUsername = view.findViewById(R.id.player_list_name);
        playerUsername.setText(player.getUsername());

        // set player score
        TextView playerScore = view.findViewById(R.id.player_list_score);
        String score = "";
        switch (display) {
            case "pointTotal":
                //playerScore.setText(String.valueOf(player.getPointTotal()));
                score = String.valueOf(player.getPointTotal());
                break;
            case "totalCodes":
                //playerScore.setText(String.valueOf(player.getTotalCodes()));
                score = String.valueOf(player.getTotalCodes());
                break;
            case "highestQRCode":
                //playerScore.setText(String.valueOf(player.getHighestQRCode()));
                score = String.valueOf(player.getHighestQRCode());
                break;
        }
        playerScore.setText(score);

        // set player rank
        TextView playerRank = view.findViewById(R.id.player_list_rank);
        String rank = "#" + String.valueOf(position + 1);
        playerRank.setText(rank);

        // set click listener on entire view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).openPlayerCollectionActivity(player);
            }
        });

        return view;
    }
}