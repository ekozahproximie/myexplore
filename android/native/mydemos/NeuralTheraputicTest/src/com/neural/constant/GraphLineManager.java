/**
 * Copyright Trimble Inc., 2013 - 2014 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.neural.constant
 *
 * File name:
 *	    GraphLineManager.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Nov 24, 20131:14:43 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *  Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.neural.constant;



/**
 * @author sprabhu
 *
 */
public final class GraphLineManager {

   public static final int R_BICEP=0XFFB56660;
   public static final int L_BICEP=0XFFB1D5E2;
   public static final int R_TRICEP=0XFFDD7523;
   public static final int L_TRICEP=0XFFE2BE00;
   public static final int DEMO_MODE=0XFF7AB84A;
   
   

   
   private final int axisColors[] = { 
         L_BICEP,
         R_BICEP,
         L_TRICEP,
         R_TRICEP,
         DEMO_MODE
 };
  private final int graphLineColor[] = {
         L_BICEP, R_BICEP, L_TRICEP, R_TRICEP,
         DEMO_MODE
  };
  
  private static  GraphLineManager GRAPH_LINE_MANAGER =null;
   private GraphLineManager() {
      if(GRAPH_LINE_MANAGER != null){
         throw new IllegalAccessError("Use getInstance to optain object");
      }
   }
   
   public static synchronized GraphLineManager getInstance(){
       if(GRAPH_LINE_MANAGER == null){
          GRAPH_LINE_MANAGER = new GraphLineManager();
       }
       
       return GRAPH_LINE_MANAGER;
   }
   
   
   /**
    * @return the axisColors
    */
   public int[] getGraphAxisColors() {
      return axisColors;
   }
   
   
   /**
    * @return the graphLineColor
    */
   public int[] getGraphLineColor() {
      return graphLineColor;
   }
}
