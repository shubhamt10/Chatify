package com.shubhamt10.chatify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static int RC_PHOTO_PICKER = 1;
    private static int RC_IMAGE_ACTIVITY = 2;
    private String receiverName;
    private String receiverUid;
    private String senderName;
    private String senderUid;

    private ListView messageListView;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages = new ArrayList<>();

    private ImageButton imageButton;
    private EditText messageEditText;
    private Button sendButton;
    private ProgressBar progressBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference messageDatabaseReference;
    private ChildEventListener messageEventListener;
    private FirebaseStorage firebaseStorage;
    private StorageReference chatPhotosStorageReference;

    SharedPreferences chatPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        receiverName = intent.getStringExtra("receiverName");
        receiverUid = intent.getStringExtra("receiverUid");
        senderName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        chatPreferences = getSharedPreferences("charData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = chatPreferences.edit();
        editor.putString("receiverName",receiverName);
        editor.putString("receiverUid",receiverUid);
        editor.putString("senderName",senderName);
        editor.putString("senderUid",senderUid);

        editor.apply();

        imageButton = findViewById(R.id.photoPickerButton);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.chatProgressBar);

        setTitle(receiverName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseStorage = FirebaseStorage.getInstance();
        chatPhotosStorageReference = firebaseStorage.getReference().child("chatImages");

        firebaseDatabase = FirebaseDatabase.getInstance();
        messageDatabaseReference = firebaseDatabase.getReference().child("messages");
        attachMessagesReadListener();

        messageListView = findViewById(R.id.messageListView);
        messageAdapter = new MessageAdapter(this,R.layout.item_message,messages);
        messageListView.setAdapter(messageAdapter);

        if (messageDatabaseReference.getKey().isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
        }

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String messageText = messageEditText.getText().toString();
                Message message = new Message(messageText,senderName,receiverName,null,senderUid,receiverUid);

                messageDatabaseReference.push().setValue(message);

                View view2 = getCurrentFocus();
                if (view2 != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                }

                messageEditText.setText("");
            }
        });

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Message message = messages.get(i);
                if (message.getPhotoUrl() != null){
                    Intent imageIntent = new Intent(ChatActivity.this,ImageActivity.class);
                    imageIntent.putExtra("url",message.getPhotoUrl());
                    startActivity(imageIntent);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            final StorageReference photoRef = chatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri dlUri = uri;
                            Message message = new Message(null,senderName,receiverName,dlUri.toString(),senderUid,receiverUid);
                            messageDatabaseReference.push().setValue(message);
                        }
                    });
                }
            });
        }

    }

   private void attachMessagesReadListener(){

        if (messageEventListener == null){

            messageEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Message message = dataSnapshot.getValue(Message.class);

                    String mSenderUid = message.getSenderUid();
                    String mReceiverUid = message.getReceiverUid();

                    if ((mSenderUid.equals(senderUid) && mReceiverUid.equals(receiverUid)) || mSenderUid.equals(receiverUid) && mReceiverUid.equals(senderUid)){

                        messageAdapter.add(message);
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
            messageDatabaseReference.addChildEventListener(messageEventListener);
        }

    }

    private void detachMessageReadListener() {

        if (messageEventListener != null){
            messageDatabaseReference.removeEventListener(messageEventListener);
            messageEventListener = null;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        detachMessageReadListener();
        messageAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiverName = chatPreferences.getString("receiverName","");
        receiverUid = chatPreferences.getString("receiverUid","");
        senderName = chatPreferences.getString("senderName","");
        senderUid = chatPreferences.getString("senderUid","");
        attachMessagesReadListener();
        messageListView.setAdapter(messageAdapter);
    }
}
