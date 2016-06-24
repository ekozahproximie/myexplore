package com.trimble.scout.acdc.exception;


public class FileNameException extends Exception{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   
   
   public static final String INVALIDFILENAME="InvalidFileName";
   
   
   public static final String  INVALIDFILEID="InvalidFileID";

   
   
   
   public static final String INVALIDTICKET="TicketInvalid";
   
   public static final String DeviceNotRegistered="DeviceNotRegistered";
   private String message;
   private String details;
   
   public FileNameException() {
      
   }
   public FileNameException(String message) {
      this(message, null, null);
   }

   public FileNameException(String message, String details) {
      this(message, details, null);
   }
   
   public FileNameException(String message, String details, Exception innerException) {
      this.details = details;
      this.message = message;
//      this.innerException = innerException;
   }

   @Override
   public String getMessage() {
      return message;
   }
   
   public void setMessage(String message) {
      this.message = message;
   }

   /**
    * The details of an exception are not for the end-user to see... they are 
    * really more for the developer to see.  If you have a nice way to show these
    * in a "more info" or "details" screen, that is fine.  otherwise you can 
    * probably just log them or discard them.
    * 
    * @return
    */
   public String getDetails() {
      return details;
   }
   
   public void setDetails(String details) {
      this.details = details;
   }
   
}
