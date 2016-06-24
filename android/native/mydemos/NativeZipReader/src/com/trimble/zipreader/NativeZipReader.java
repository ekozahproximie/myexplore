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
 *		NativeZipReader.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Nov 29, 2012 10:33:33 PM
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

/**
 * @author sprabhu
 *
 */

public class NativeZipReader {
    static{
        System.loadLibrary("NativeZipReader");
    }
    public static native byte[] loadFileFromZipFile(String zipFileName, String filePath);
    
}
