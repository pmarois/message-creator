package com.polm.msg.creator;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MessageCreatorActivity extends Activity implements View.OnClickListener {

    /*TODO
     * - "read" parameter 
     * - "sentence size" parameter
     * - AsyncTask when inserting/deleting instead of a tread
     * - Loader when inserting
     * - Delete messages
     * - Warn user before critical action
     * - edittextBox only with number
     * - Move onCllick into the xml file
     */

    private static String TAG = "MessageCreatorActivity";

    private static Uri SMS_INBOX_URI = Uri.parse("content://sms/inbox");
    private static Uri SMS_SENTBOX_URI = Uri.parse("content://sms/sent");
    private static Uri SMS_URI = Uri.parse("content://sms");

    private EditText mNumberOfMessagesEditText;
    private EditText mContactNumberEditText;
    private EditText mAverageWordSizeEditText;
    private int mNumberOfMessages;
    private String mContactNumber;
    private int mAverageWordSize;
    private Button mInsertButton;
    private Button mDeleteButton;
    private Context mContext;
    //TODO move that into xml resource
    private String[] mTab = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    private ArrayList<String> mMessageList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        mNumberOfMessagesEditText = (EditText) findViewById(R.id.numberOfSMS);
        mContactNumberEditText = (EditText) findViewById(R.id.contactNumber);
        mAverageWordSizeEditText = (EditText) findViewById(R.id.averageWordSize);
        mInsertButton = (Button) findViewById(R.id.btnInsert);
        mDeleteButton = (Button) findViewById(R.id.btnDelete);

        mInsertButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
    }

    public void addSMSToList( int nbrMessages, int averageWordSize){
        Log.d(TAG, "addSMSToList" + nbrMessages + " " + averageWordSize);
        mMessageList = new ArrayList<String>();
        mMessageList.ensureCapacity(nbrMessages);
        for (int index=0; index< nbrMessages; index++){
            mMessageList.add(createRandomString(index, averageWordSize));
        }
    }

    public String createRandomString(int index, int averageWordSize){
        Log.d(TAG, "createRandomString : " + index + " " + averageWordSize);
        Random random = new Random();
        String s = String.valueOf(index);
        for (int i=0; i < averageWordSize; i++){
            int valeur = 0 + random.nextInt(25);
            s += mTab[valeur];
        }
        return s;
    }

    public void putAllMessagesIntoInbox(String contactNumber) {
        Log.d(TAG, "putAllMessagesIntoInbox");
        Iterator<String> iterator = mMessageList.iterator();

        while (iterator.hasNext()){
            String body = iterator.next();
            ContentValues values = new ContentValues(4);
            values.put("address", contactNumber);
            values.put("date", System.currentTimeMillis());
            values.put("read", "1");
            values.put("body", body);
            Log.d(TAG, "contentValues insert : " + values.toString());
            mContext.getContentResolver().insert(SMS_INBOX_URI, values);
        }
    }
    
    public int deleteMessagesFromInbox(String contactNumber) {
        Log.d(TAG, "deleteMessagesFromInbox");
        int count = 0;
        //TODO query
        Log.d(TAG, "deleted Messages : " + count);
        return count;
    }    
    
    public void onClick(View v){
        mNumberOfMessages = Integer.parseInt(mNumberOfMessagesEditText.getText().toString());
        mContactNumber = mContactNumberEditText.getText().toString();
        mAverageWordSize = Integer.parseInt(mAverageWordSizeEditText.getText().toString());

        switch (v.getId()) {
            case R.id.btnInsert:
                
                new Thread(new Runnable() {
                    public void run() {
                        addSMSToList(mNumberOfMessages, mAverageWordSize);
                        putAllMessagesIntoInbox(mContactNumber);
                    }
                }).start();
                
                break;
            case R.id.btnDelete:
                deleteMessagesFromInbox(mContactNumber);
                break;
            default:
                break;
        }
    }
}