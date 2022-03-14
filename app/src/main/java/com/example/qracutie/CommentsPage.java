package com.example.qracutie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommentsPage extends AppCompatActivity {

    // Declare the variables so that you will be able to reference it later.
    ListView commentList;
    ArrayAdapter<Comment> commentAdapter;
    ArrayList<Comment> commentDataList;

    final String TAG = "Sample";
    Button addCommentButton;
    EditText addCommentEditText;
    FirebaseFirestore db;

    CommentList customList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        // Get a top level reference to the collection
        final CollectionReference collectionReference = db.collection("GameQRCodes/11/comments/");

        commentList = findViewById(R.id.comment_list);

        addCommentButton = findViewById(R.id.add_comment_button);
        addCommentEditText = findViewById(R.id.add_comment_field);

        String []commentText ={"Wow this QR code is so pretty", "Almost as pretty as me", "WTF", "You heard me", "I did not", "Text carries no sound"};
        String []userID = {"batiuk", "batiuk", "hindle", "batiuk", "hindle", "hindle"};
        String []timeStamp = {"1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21"};

        commentDataList = new ArrayList<>();

        for(int i=0;i<commentText.length;i++){
            commentDataList.add((new Comment(commentText[i], userID[i], timeStamp[i])));
        }

        commentAdapter = new CommentList(this, commentDataList);






        commentList.setAdapter(commentAdapter);





        addCommentButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Retrieving the comment name and the province name from the EditText fields
                final String commentText = addCommentEditText.getText().toString();



                // TEMP
                final String userName = "batiuk";


                // https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                final String formattedDate = df.format(c);

                // https://cloud.google.com/firestore/docs/manage-data/add-data#javaandroid
                Map<String, Object> data = new HashMap<>();

                if (commentText.length() > 0 && userName.length() > 0) {

                    // If there’s some data in the EditText field, then we create a new key-value pair.
                    data.put("date", formattedDate);
                    data.put("text", commentText);
                    data.put("uid", userName);

                }

                // The set method sets a unique id for the document
                collectionReference
                        .document(commentText)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // These are a method which gets executed when the task is succeeded

                                Log.d(TAG, "Data has been added successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // These are a method which gets executed if there’s any problem
                                Log.d(TAG, "Data could not be added!" + e.toString());
                            }
                        });

                // Setting the fields to null so that user can add a new city
                addCommentEditText.setText("");
            }
        });

        collectionReference.orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {

                // Clear the old list
                commentDataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, String.valueOf(doc.getData().get("date")));

                    String text = (String) doc.getData().get("text");
                    String date = (String) doc.getData().get("date");
                    String uid = (String) doc.getData().get("uid");

                    commentDataList.add(new Comment(text, uid, date)); // Adding the cities and provinces from FireStore
                }
                commentAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
            }
        });
    }
}
