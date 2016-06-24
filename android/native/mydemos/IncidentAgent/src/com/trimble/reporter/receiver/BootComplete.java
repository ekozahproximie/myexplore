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
 *      com.trimble.reporter.receiver
 *
 * File name:
 *		BootComplete.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 15, 2012 10:45:10 AM
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

package com.trimble.reporter.receiver;

import com.trimble.reporter.service.AgentLocationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author sprabhu
 */

public class BootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null) {

            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                    || intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {

                Intent serviceIntent = new Intent(context, AgentLocationService.class);

                context.startService(serviceIntent);
            }
        }
    }

}
