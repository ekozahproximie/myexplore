package com.neural.quickblox.activity;

import static com.neural.quickblox.definitions.Consts.APP_ID;
import static com.neural.quickblox.definitions.Consts.AUTH_KEY;
import static com.neural.quickblox.definitions.Consts.AUTH_SECRET;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import com.neural.activity.NeuralBaseActivity;
import com.neural.demo.R;
import com.neural.quickblox.definitions.QBQueries;
import com.neural.quickblox.helper.DataHolder;
import com.neural.quickblox.managers.QBManager;
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserResult;

public class SignInActivity extends NeuralBaseActivity implements QBCallback {

    private EditText loginEditText;
    private EditText passwordEditText;
    
    
    public static final String FULL_USER_NAME="user_name";
    
    public static final String LOGIN_NAME="login_name";
    
    public static final String PASS_WORD="password";
    
    public static final String USER_ID="user_id";
    
    public static final String CALLEE_USER_ID="callee_user_id";
    
    public static final String CALLEE_USER_NAME="callee_user_name";
    
    
    private static final String LAST_LOGIN_NAME="last_login_name";
    
    private static final String LAST_PASS_WORD="last_password";

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_sign_in);
        
        initUI();
    }

    private void initUI() {
        
        loginEditText = (EditText) findViewById(R.id.login_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        loginEditText.setText(getStringPrefrenceValue(this, LAST_LOGIN_NAME, null));
        passwordEditText.setText(getStringPrefrenceValue(this, LAST_PASS_WORD, null));
        
//        boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
//        if (isDebuggable){
//           loginEditText.setText("nttest");
//           passwordEditText.setText("nttest123");
//        }
           
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                showDialog(1);
             // http://quickblox.com/developers/Getting_application_credentials
                QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
             
                QBAuth.createSession(this,QBQueries.QB_QUERY_AUTHORIZE_APP);
                
                break;
        }
    }

    @Override
    public void onComplete(Result result) {
       
      
    }

    @Override
    public void onComplete(Result result, Object query) {
        QBQueries qbQueryType = (QBQueries) query;
        
        if (result.isSuccess() && qbQueryType == QBQueries.QB_QUERY_AUTHORIZE_APP) {
           final String login =loginEditText.getText().toString().trim();
           final String password =passwordEditText.getText().toString().trim();
         // Sign in application with user.
         // You can create user on admin.quickblox.com, Users module or through QBUsers.signUp method

           QBManager.singIn(login, 
                 password , this, 
                 QBQueries.QB_QUERY_SIGN_IN_QB_USER);
        }else  if (result.isSuccess() && qbQueryType == QBQueries.QB_QUERY_SIGN_IN_QB_USER) {
           dismissDialog(1);      
           storeResult(result);
                 
        } else {
           clear(result);
           dismissDialog(1);
        }
       
    }
    
   @Override
   protected Dialog onCreateDialog(int id) {
     final ProgressDialog progressDialog= new ProgressDialog(this);
      progressDialog.setCancelable(false);
      progressDialog.setCanceledOnTouchOutside(false); 
      progressDialog.setMessage(getResources().getString(R.string.please_wait));
      return progressDialog;
   }
    private void clear(final Result result){
       clear(getApplicationContext());
       showLong(SignInActivity.this, result.getErrors().get(0));
    }
    public static void clear(final Context context){
       if(context == null){
          return;
       }
       storeStringValue(context, null, FULL_USER_NAME);
       storeStringValue(context, null, LOGIN_NAME);
       storeStringValue(context, null, PASS_WORD);
       storeStringValue(context, null, USER_ID);
    }
    private void storeResult(final Result result){
       setResult(RESULT_OK);
       // return QBUserResult for singIn query
       
       QBUserResult qbUserResult = (QBUserResult) result;
       final QBUser qbUser =qbUserResult.getUser();
       DataHolder.getDataHolder().setSignInQbUser(qbUser);
       final String stPassword=passwordEditText.getText().toString().trim();
       final String stUserName=loginEditText.getText().toString().trim();
       final String stQbUserName=qbUser.getFullName();
       storeStringValue(this, (stQbUserName == null 
             || stQbUserName.trim().length() == 0)?stUserName:stQbUserName, FULL_USER_NAME);
       storeStringValue(this, stUserName, LOGIN_NAME);
       storeStringValue(this, stPassword, PASS_WORD);
       
       storeStringValue(this, stUserName, LAST_LOGIN_NAME);
       storeStringValue(this, stPassword, LAST_PASS_WORD);
       
       storeStringValue(this, String.valueOf(qbUser.getId()), USER_ID);
       // password does not come, so if you want use it somewhere else, try something like this:
       DataHolder.getDataHolder().setSignInUserPassword(
             stPassword);
       showLong(SignInActivity.this, getResources().getString(R.string.user_successfully_sign_in));
       finish();
    }
    public static String getStringPrefrenceValue(final Context  context ,final String stKey,
          final String stDefalutValue){
       final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
       return preferences.getString(stKey, stDefalutValue);
    }
    
    public static void storeStringValue(final Context context,final String stValue,
          final String stKey){
       final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
       final Editor editor = preferences.edit();
       editor.putString(stKey, stValue);
       editor.commit();
    }
}