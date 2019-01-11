package com.shubhamt10.chatify;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {

    private Intent mainIntent;
    private Intent chatIntent;

    private ProgressBar progressBar;
    private ListView userListView;
    private UserAdapter userAdapter;
    private ArrayList<User> users = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener userEventListener;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mainIntent = new Intent(UserListActivity.this,MainActivity.class);
        chatIntent = new Intent(UserListActivity.this,ChatActivity.class);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = findViewById(R.id.usersProgressBar);

        userListView = findViewById(R.id.userListView);
        userAdapter = new UserAdapter(this,R.layout.item_user,users);
        userListView.setAdapter(userAdapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chatIntent.putExtra("receiverName",users.get(i).getName());
                chatIntent.putExtra("receiverUid",users.get(i).getUid());
                startActivity(chatIntent);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference().child("users");

        attachUsersReadListener();

    }

    private void attachUsersReadListener(){

        if (userEventListener == null){
            userEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    progressBar.setVisibility(View.INVISIBLE);
                    User user = dataSnapshot.getValue(User.class);
                    String uid = user.getUid();

                    if (!currentUser.getUid().equals(uid)){
                        System.out.println(user.getName());
                        userAdapter.add(user);
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            userDatabaseReference.addChildEventListener(userEventListener);
        }

    }

    private void detachUsersReadListener(){

        if (userEventListener != null) {
            userDatabaseReference.removeEventListener(userEventListener);
            userEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        detachUsersReadListener();
        userAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachUsersReadListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_list_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.user_activity_menu:
                FirebaseAuth.getInstance().signOut();
                startActivity(mainIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
