package com.shubhamt10.chatify;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, int resource,List<Message> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView,ViewGroup parent) {

        Message message = getItem(position);
        ImageView imageView;
        TextView textView;

            if (message.getSenderName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_sent_message,parent,false);
                imageView = convertView.findViewById(R.id.sentMessageImage);
                textView = convertView.findViewById(R.id.sentMessageText);
            }else {
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_received_message,parent,false);
                imageView = convertView.findViewById(R.id.receivedMessageImage);
                textView = convertView.findViewById(R.id.receivedMessageText);
            }


        boolean isPhoto = message.getPhotoUrl() != null ;

        if (isPhoto) {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(imageView.getContext())
                    .asBitmap()
                    .apply(new RequestOptions().override(300,300))
                    .load(message.getPhotoUrl())
                    .into(imageView);
        }else {
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            textView.setText(message.getText());
        }

        return convertView;
    }


}
