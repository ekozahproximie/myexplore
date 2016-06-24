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
 *      com.trimble.zipreader
 *
 * File name:
 *		TestActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Nov 29, 2012 10:34:41 PM
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



package com.trimble.zipreader;




import com.trimble.zipreader.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author sprabhu
 *
 */

public class TestActivity extends Activity {
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    private TextView tvResult=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tvResult=(TextView)findViewById(R.id.result);
        
    }
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.execute:
                byte [] result=NativeZipReader.loadFileFromZipFile("hello", "hai");
                if(result != null){
                    tvResult.setText(new String(result)); 
                }
                break;

            default:
                break;
        }
    }
}
