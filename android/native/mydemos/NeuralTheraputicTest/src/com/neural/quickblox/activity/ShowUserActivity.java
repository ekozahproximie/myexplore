package com.neural.quickblox.activity;

import static com.neural.quickblox.definitions.Consts.POSITION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.neural.activity.NeuralBaseActivity;
import com.neural.demo.R;
import com.neural.quickblox.helper.DataHolder;
import com.quickblox.module.users.model.QBUser;

public class ShowUserActivity extends NeuralBaseActivity {

    private TextView loginTextView;
    private TextView emailTextView;
    private TextView fullNameTextView;
    private TextView phoneTextView;
    private TextView webSiteTextView;
    private TextView tagsTextView;

    private int position;


    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_show_user);
        
        initUI();
        fillAllFields();
    }

    private void initUI() {
       
        loginTextView = (TextView) findViewById(R.id.login_textview);
        emailTextView = (TextView) findViewById(R.id.email_textview);
        fullNameTextView = (TextView) findViewById(R.id.full_name_textview);
        phoneTextView = (TextView) findViewById(R.id.phone_textview);
        webSiteTextView = (TextView) findViewById(R.id.web_site_textview);
        tagsTextView = (TextView) findViewById(R.id.tags_textview);
    }
    protected void fillField(TextView textView, String value) {
       if (!TextUtils.isEmpty(value)) {
           textView.setText(value);
       }
   }
    public void onClick(final View view){
       final QBUser qbUser =DataHolder.getDataHolder().getQBUser(position);
       final String stLoginUserId= SignInActivity.getStringPrefrenceValue(getApplicationContext(), 
             SignInActivity.USER_ID, null);
       if(stLoginUserId != null && qbUser.getId() ==  Integer.parseInt(stLoginUserId)){
          Toast.makeText(getApplicationContext(), getString(R.string.Select_non_logged), 
                Toast.LENGTH_LONG).show();
          return;
       }
       SignInActivity.storeStringValue(this, qbUser.getFullName(), SignInActivity.CALLEE_USER_NAME);
       SignInActivity.storeStringValue(this, String.valueOf(qbUser.getId()), SignInActivity.CALLEE_USER_ID);
       setResult(RESULT_OK);
       finish();
    }
    private void fillAllFields() {
        position = getIntent().getIntExtra(POSITION, 0);
        fillField(loginTextView, DataHolder.getDataHolder().getQBUser(position).getLogin());
        fillField(emailTextView, DataHolder.getDataHolder().getQBUser(position).getEmail());
        fillField(fullNameTextView, DataHolder.getDataHolder().getQBUser(position).getFullName());
        fillField(phoneTextView, DataHolder.getDataHolder().getQBUser(position).getPhone());
        fillField(webSiteTextView, DataHolder.getDataHolder().getQBUser(position).getWebsite());
        fillField(tagsTextView, DataHolder.getDataHolder().getQBUser(position).getTags().toString());
    }
}