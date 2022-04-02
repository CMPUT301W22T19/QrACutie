package com.example.qracutie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OwnerRecyclerviewAdapter extends RecyclerView.Adapter<OwnerRecyclerviewAdapter.ViewHolder>{
    private static final String TAG = "OwnerRecyclerviewAdapter";
    private ArrayList<String> playerNames = new ArrayList<>();
    private ArrayList<String> playerImages = new ArrayList<>();
    private String username;
    private Context context;
    private Integer selected = -1;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
    private CollectionReference db = FirebaseFirestore.getInstance().collection("users");

    public OwnerRecyclerviewAdapter(String username,ArrayList<String> playerNames, ArrayList<String> playerImages, Context context) {
        this.username = username;
        this.playerNames = playerNames;
        this.playerImages = playerImages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(playerImages.get(position) != "default image") {
            Glide.with(context).asBitmap().load(Uri.parse(playerImages.get(position))).into(holder.playerImage);
        }
        holder.playerName.setText(playerNames.get(position));

        holder.itemView.findViewById(R.id.del_player_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playerNames.get(position).equals(username)){
                    SharedPreferences sharedPreferences  = context.getApplicationContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().commit();
                }
                remFromDat(position);
                playerNames.remove(position);
                playerImages.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, playerNames.size());
                notifyItemRangeChanged(position, playerImages.size());
            }
        });

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = position;
                notifyDataSetChanged();
            }
        });
        if(selected == position){
            holder.itemView.setBackgroundColor(Color.parseColor("#0000FF"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }*/
    }

    private void remFromDat(int pos){
        storageReference.child(playerNames.get(pos)+".jpeg").delete();
        db.document(playerNames.get(pos)).delete();

    }

    @Override
    public int getItemCount() {
        return playerNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView playerImage;
        TextView playerName;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerImage = (ImageView) itemView.findViewById(R.id.playerImage);
            playerName = (TextView) itemView.findViewById(R.id.player_name);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.recycler_list_item);
        }
    }
}
