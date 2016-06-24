/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.reporter
 *
 * File name:
 *		CatagoryListActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 14, 2012 9:09:58 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */



package com.trimble.reporter;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author sprabhu
 *
 */

public class CatagoryListActivity extends BaseActivity {
    private ArrayAdapter<String> arrAdapter      = null;
    private int                  iItemIndex      = 0;
    private String               stItemsArray[]  = null;
    private String               sPicklistTitle  = null;
    private int                  nPickListType   = 1;
    private int                  iSelectionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.template_picklist);

       Intent intent = getIntent();
       Bundle bundle = intent.getExtras();
       if (bundle != null) {
          sPicklistTitle = bundle
                .getString(ReporterActivity.PICKLIST_TITLE);
          stItemsArray = bundle
                .getStringArray(ReporterActivity.PICKLIST_ARR_ITEMS);
          nPickListType = bundle.getInt(ReporterActivity.PICKLIST_TYPE);
          iSelectionIndex = bundle
                .getInt(ReporterActivity.SELECTION_INDEX);
       }

       onInit();
    }

    public void onClick(View onClick) {
       switch (onClick.getId()) {
          case R.id.picklist_done:
             sendResult(iSelectionIndex);
             break;
          default:
             break;
       }
    }

    private void onInit() {
       // set title
       TextView txtTitle = (TextView) findViewById(R.id.picklist_title);
       txtTitle.setText(sPicklistTitle);

       // set list
       ListView lstvCropName = (ListView) findViewById(R.id.lst_picklist);
       arrAdapter = new ArrayAdapter<String>(this, R.layout.settingslvelem,
             stItemsArray);
       lstvCropName.setAdapter(arrAdapter);
       lstvCropName.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
       lstvCropName.setItemChecked(iSelectionIndex, true);
       lstvCropName.setSelectionFromTop(iSelectionIndex, 10);
       lstvCropName.setOnItemClickListener(new OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> arg0, View view, int position,
                long id) {
             iItemIndex = position;
             sendResult(iItemIndex);
          }
       });
    }

    private void sendResult(int iIndex) {
       Intent intent = new Intent(this, ReporterActivity.class);
       intent.putExtra(ReporterActivity.PICKLIST_ITEM,
             stItemsArray[iIndex]);
       intent.putExtra(ReporterActivity.SELECTION_INDEX,
               iIndex);
       intent.putExtra(ReporterActivity.PICKLIST_TYPE, nPickListType);
       setResult(RESULT_OK, intent);
       finish();
    }
    
}
