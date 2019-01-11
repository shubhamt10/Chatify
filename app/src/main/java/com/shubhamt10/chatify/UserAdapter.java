package com.shubhamt10.chatify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(Context context, int resource,List<User> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_user,parent,false);
        }

        User user = getItem(position);

        String username = user.getName();
        String[] parts = username.split(" ");

        ImageView textImageView = convertView.findViewById(R.id.textimageview);
        TextView userTextView = convertView.findViewById(R.id.userTextView);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(user.getUid());

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig().round();

        TextDrawable drawable = builder.build(String.valueOf(username.charAt(0)), color);

        textImageView.setImageDrawable(drawable);
        userTextView.setText(parts[0]);

        return convertView;
    }
}
