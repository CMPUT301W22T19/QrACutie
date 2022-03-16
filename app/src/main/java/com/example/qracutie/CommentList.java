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

    /**
     * Constructor for the object
     * @param context
     * @param comments
     */
    public CommentList(Context context, ArrayList<Comment> comments){
        super(context,0, comments);
        this.comments = comments;
        this.context = context;
    }

    /**
     * Gets view object containing comments and users who created them that were passed to the
     * constructor. For use on comments page of app
     * @param position
     * @param convertView
     * @param parent
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
