/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.mobile.android.templates
 *
 * File name:
 *		FlagInfo.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Jul 17, 2012 1:06:31 PM
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

package com.trimble.mobile.android.templates;

import java.io.Serializable;

/**
 * @author sprabhu
 */

public class FlagInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2658779517081259075L;

    public String stFlagName = null;

    public String stImageURI = null;

    public int iAttributeType = 0;

    public float fLat = 0;

    public float fLon = 0;

    public long lFeatureID = 0;
    
    public int iFeatureType = 0;

    /**
     * 
     */
    public FlagInfo(String stFlagName, String stImageURI, int iAttributeType,long lFeatureID,int iFeatureType) {
        this.stFlagName = stFlagName;
        this.stImageURI = stImageURI;
        this.iAttributeType = iAttributeType;
        this.lFeatureID= lFeatureID;
        this.iFeatureType=iFeatureType;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        boolean isSame = false;
        isSame =(o instanceof FlagInfo);
        
        FlagInfo otherFlagInfo = (FlagInfo)o;
        isSame = isSame && (stFlagName.equals(otherFlagInfo.stFlagName)
                && stImageURI != null && stImageURI.equals(otherFlagInfo.stImageURI)
                && iAttributeType == otherFlagInfo.iAttributeType
                && lFeatureID == otherFlagInfo.lFeatureID
                && iFeatureType == otherFlagInfo.iFeatureType); 
        return isSame;
    }

}
