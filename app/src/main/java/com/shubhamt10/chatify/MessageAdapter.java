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

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, int resource,List<Message> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView,ViewGroup parent) {

        if (convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message,parent,false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);

        Message message = getItem(position);

        boolean isPhoto = message.getPhotoUrl() != null ;

        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .asBitmap()
                    .apply(new RequestOptions().override(300,300))
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        }else {
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTextView.setText(message.getText());
        }

        nameTextView.setText(message.getSenderName());

        return convertView;
    }


}
