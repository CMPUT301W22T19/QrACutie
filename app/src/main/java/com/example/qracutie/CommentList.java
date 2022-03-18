package com.example.qracutie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A container for displaying each comment attached to a given QR code. Creates textviews for
 * use in layout xml files. 
 *
 * Adapted in large parts from the MainActivity Class shown in CMPUT 301's labs
 */
public class CommentList extends ArrayAdapter<Comment> {

    private ArrayList<Comment> comments;
    private Context context;

    public CommentList(Context context, ArrayList<Comment> comments){
        super(context,0, comments);
        this.comments = comments;
        this.context = context;
    }

    /**
     * generates and returns a View element representing a single comment, which is part
     * of a ListView
     * @param position the index of the view within a list
     * @param convertView an old view to reuse
     * @param parent the list which contains the view
     * @return View element
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent,false);
        }

        Comment comment = comments.get(position);

        TextView commentName = view.findViewById(R.id.comment_text);
        TextView userName = view.findViewById(R.id.uid_text);

        commentName.setText(comment.getCommentName());
        userName.setText(comment.getUserName());

        return view;
    }
}
