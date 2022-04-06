package com.example.qracutie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * Responsible for listing all QR codes of the app, and enabling deletion of these QR codes
 */
public class OwnerQRRecyclerviewAdapter extends RecyclerView.Adapter<OwnerQRRecyclerviewAdapter.ViewHolder> {
    private static final String TAG = "OwnerQRRecyclerviewAdapter";
    private ArrayList<String> QRNames = new ArrayList<>();
    private Context context;
    private CollectionReference db = FirebaseFirestore.getInstance().collection("GameQRCodes");

    /**
     * Setting the arraylist of QR code names
     * @param QRNames
     * @param context
     */
    public OwnerQRRecyclerviewAdapter(ArrayList<String> QRNames, Context context) {
        this.QRNames = QRNames;
        this.context = context;
    }

    /**
     * Sets the view and viewholder
     * @param parent
     * @param viewType
     * @return viewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_recyclerview_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * Controls for displaying as well as the deletion of items
     * @param holder
     * @param position
     */
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
    }

    // removes deleted QR code's document from firestore
    private void remFromDat(int pos){
        db.document(QRNames.get(pos)).delete();
    }

    /**
     * returns the item count
     * @return the size of QRNames
     */
    @Override
    public int getItemCount() {
        return QRNames.size();
    }

    /**
     * To aid with displaying
     */
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
