package com.trimble.agmantra.acdc.request;

import com.trimble.agmantra.acdc.ACDCApi;

public class GetDeviceFileReq {

 
   public String               stTicket = null;
   public String               stFileId = null;

   public String getQueryString() {
      StringBuffer buffer = new StringBuffer();
      if (stTicket == null) {
         stTicket = "";
      }
      buffer.append(ACDCApi.TICKET_PARAM);
      buffer.append(stTicket);

      return buffer.toString();

   }

}
