package com.neural.quickblox;

import static com.neural.quickblox.definitions.Consts.APP_ID;
import static com.neural.quickblox.definitions.Consts.AUTH_KEY;
import static com.neural.quickblox.definitions.Consts.AUTH_SECRET;
import static com.neural.quickblox.definitions.Consts.POSITION;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.neural.activity.NeuralBaseActivity;
import com.neural.demo.R;
import com.neural.quickblox.activity.ShowUserActivity;
import com.neural.quickblox.adapter.UserListAdapter;
import com.neural.quickblox.definitions.QBQueries;
import com.neural.quickblox.helper.DataHolder;
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.result.QBUserPagedResult;

public class UsersListActivity extends NeuralBaseActivity implements
      QBCallback, AdapterView.OnItemClickListener {

   /**
    * 
    */
   private static final int SHOW_USER_INFO = 1000;
   private UserListAdapter  usersListAdapter;
   private ListView         usersList;
   protected ProgressDialog progressDialog;
   private boolean          isLogin        = false;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_users_list);
      progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(false);
      progressDialog.setMessage(getResources().getString(R.string.please_wait));

      initUI();

      progressDialog.show();
      // ================= QuickBlox ===== Step 1 =================
      // Initialize QuickBlox application with credentials.
      // Getting app credentials --
      //http://quickblox.com/developers/Getting_application_credentials
      QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
      // Authorize application
      QBAuth.createSession(this, QBQueries.QB_QUERY_AUTHORIZE_APP);
   }

   private void initUI() {
      usersList = (ListView) findViewById(R.id.users_listview);
   }

   private void initUsersList() {
      usersListAdapter = new UserListAdapter(this);
      usersList.setAdapter(usersListAdapter);
      usersList.setOnItemClickListener(this);
   }

   @Override
   public void onResume() {
      super.onResume();
      if (DataHolder.getDataHolder().getSignInQbUser() != null) {

      }
      if (usersListAdapter != null) {
         usersListAdapter.notifyDataSetChanged();
      }
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      if (isLogin) {
         // destroy session after app close
         QBAuth.deleteSession(this);
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == SHOW_USER_INFO) {
         if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
         }
      }
   }

   private void getAllUser() {
      // Get all users for the current app
      QBUsers.getUsers(new QBCallback() {

         @Override
         public void onComplete(Result result) {
            // return QBUserPagedResult for getUsers query
            QBUserPagedResult qbUserPagedResult = (QBUserPagedResult) result;
            DataHolder.getDataHolder().setQbUsersList(
                  qbUserPagedResult.getUsers());
            runOnUiThread(new Runnable() {

               @Override
               public void run() {
                  isLogin = true;
                  initUsersList();
                  if (usersListAdapter != null) {
                     usersListAdapter.notifyDataSetChanged();
                  }
               }
            });
            progressDialog.hide();
         }

         @Override
         public void onComplete(Result result, Object o) {}
      });
   }

   @Override
   public void onComplete(Result result) {

   }

   @Override
   public void onComplete(Result result, Object query) {
      QBQueries qbQueryType = (QBQueries) query;
      if (result.isSuccess()) {
         switch (qbQueryType) {
            case QB_QUERY_AUTHORIZE_APP:
               getAllUser();
               break;
            default:
               break;
         }
      } else {
         showLong(UsersListActivity.this, result.getErrors().get(0));
         progressDialog.hide();
      }

   }

   

   @Override
   public void onItemClick(AdapterView<?> adapterView, View view, int position,
         long id) {

      startShowUserActivity(position);

   }

   private void startShowUserActivity(int position) {
      final Intent intent = new Intent(this, ShowUserActivity.class);
      intent.putExtra(POSITION, position);
      startActivityForResult(intent, SHOW_USER_INFO);
   }
}