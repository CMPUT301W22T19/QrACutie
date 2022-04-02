package com.example.qracutie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OwnerQRRecyclerviewAdapter extends RecyclerView.Adapter<OwnerQRRecyclerviewAdapter.ViewHolder> {
    private static final String TAG = "OwnerQRRecyclerviewAdapter";
    private ArrayList<String> QRNames = new ArrayList<>();
    private Context context;
    private CollectionReference db = FirebaseFirestore.getInstance().collection("GameQRCodes");

    public OwnerQRRecyclerviewAdapter(ArrayList<String> QRNames, Context context) {
        this.QRNames = QRNames;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_recyclerview_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(QRNames.get(position));

        holder.itemView.findViewById(R.id.del_Qr_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remFromDat(position);
                QRNames.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, QRNames.size());
            }
        });
        //holder.itemView.setSelected(true);

        // From: YouTube
        // URL: https://www.youtube.com/watch?v=hYWk4m-7mwA&ab_channel=EDMTDev
        // Author: EDMT Dev

        // From: stackoverflow
        // URL: https://stackoverflow.com/a/59344531
        // Author: Hamed Rahimi
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //notifyDataSetChanged();
                //QRNames.remove(position);
                //Toast.makeText(context.getApplicationContext(), Integer.toString(selected), Toast.LENGTH_SHORT).show();
                //notifyItemRemoved(position);
                //notifyItemRangeChanged(position, QRNames.size());
            }
        });*/
        /*if(selected == position){
            holder.itemView.setBackgroundColor(Color.parseColor("#0000FF"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    private void remFromDat(int pos){
        db.document(QRNames.get(pos)).delete();
    }

    @Override
    public int getItemCount() {
        return QRNames.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeLayout;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.QR_recycler_list_item);
            textView = (TextView) itemView.findViewById(R.id.QR_string_list_item);
        }
    }
}
