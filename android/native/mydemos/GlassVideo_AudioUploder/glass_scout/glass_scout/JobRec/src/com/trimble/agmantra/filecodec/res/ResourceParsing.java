package com.trimble.agmantra.filecodec.res;


import java.io.IOException;  
import java.io.InputStream;  
import java.util.Hashtable;
  
import org.xmlpull.v1.XmlPullParser;  
import org.xmlpull.v1.XmlPullParserException;  
import org.xmlpull.v1.XmlPullParserFactory;  
  
import com.trimble.agmantra.dbutil.Log;  
  
public class ResourceParsing {  
  
    protected XmlPullParser xmlpullparser;  
    String output;  
    String sLOGTAG="Resource Parsing";
    Boolean hasFlsReached = false;
    public ResourceTags resTags = new ResourceTags();
    
    public ResourceParsing(InputStream is) throws Exception {  
  
        XmlPullParserFactory factory = null;  
        try {  
            factory = XmlPullParserFactory.newInstance();  
        } catch (XmlPullParserException e) {  
            e.printStackTrace();  
        }  
        factory.setNamespaceAware(true);  
        try {  
            xmlpullparser = factory.newPullParser();  
        } catch (XmlPullParserException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
  
  
        try {  
            xmlpullparser.setInput(is, "UTF-8");  
        } catch (XmlPullParserException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
  
  
        int eventType = 0;  
        try {  
            eventType = xmlpullparser.getEventType();  
        } catch (XmlPullParserException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        while (eventType != XmlPullParser.END_DOCUMENT) {  
  
            parseTag(eventType);  
            try {  
                eventType = xmlpullparser.next();  
            } catch (XmlPullParserException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
        Log.i(sLOGTAG,"END_DOCUMENT : " + "finished - parsing");
  
    }  
  
    void parseTag(int event) throws Exception
    {  
		switch (event){
		
		case XmlPullParser.START_DOCUMENT:
			Log.i(sLOGTAG, "START_DOCUMENT : " + "started - parsing");
			break;
		// end_document to be handled if needed

		case XmlPullParser.START_TAG: 
			String sElemName = xmlpullparser.getName();
			
			//skip 'fls' node --- to be handled if needed 
			if( !hasFlsReached && sElemName.equals("fls") ){
				hasFlsReached = true;
				break;
			}
			resTags.parseElement(sElemName, xmlpullparser);
			
/*			int nAttribs = xmlpullparser.getAttributeCount();

			if (nAttribs > 0) {
				resTags.parseElement(sElemName, xmlpullparser);				
				}
*/
				/*
				 * // display Log.i(sLOGTAG, "START_TAG : " + sElemName +
				 * "  ATTR_COUNT : " + Integer.toString(nAttribs)); for (int i =
				 * 0; i < nAttribs; i++) { Log.i(sLOGTAG, "ATTRIB_NAME : " +
				 * xmlpullparser.getAttributeName(i) + "  ATTRIB_VALUE : " +
				 * xmlpullparser.getAttributeValue(i)); }
				 */			
			break;

		case XmlPullParser.END_TAG: 
			Log.i(sLOGTAG, "END_TAG : " + xmlpullparser.getName());
			// Log.i(sLOGTAG,"\n");		
			break;

		case XmlPullParser.TEXT: 
			output = xmlpullparser.getText();
			/*String sReturn = new String("\n");
			String sEmpty = new String("");
			if (output.length() > 0 && (!(output.equals(sReturn)))
					&& (!(output.equals(sEmpty)))) {
				Log.i(sLOGTAG, "TEXT : " + "value = " + output + "Length : "
						+ Integer.toString(output.length()));
			}
			
			//
			if (output.length() > 0 && (!(output.equals(sReturn)))
					&& (!(output.equals(sEmpty)))) {
				Log.i(sLOGTAG, "TEXT : " + "value = " + output + "Length : "
						+ Integer.toString(output.length()));
			}*/
			Log.i(sLOGTAG, "TEXT : " + "value = " + output);
			
			break;
    	}
    }
}  