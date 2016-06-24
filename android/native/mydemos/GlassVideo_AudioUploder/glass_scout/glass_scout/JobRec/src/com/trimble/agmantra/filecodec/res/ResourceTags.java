package com.trimble.agmantra.filecodec.res;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.trimble.agmantra.dbutil.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class ResourceTags {
	
	/*
	 * No.of. resource tags supported in 'fls' file
	 * It can be modified based on the resource tag set specifications
	 * 23 - this is the current tag set count
	 */
	private final int RES_TAG_COUNT = 23;

	/* 
	 * contains the key-string hash map of all resource tags
	 */
	private Hashtable<String, Integer> resources = new Hashtable<String, Integer>(RES_TAG_COUNT);
	private String sLOGTAG="Resource Parsing";  
	private int eventType=0;
	
	//various vectors	
	private ArrayList<ComResAttribs> arrNoteTypeList;
	private ArrayList<ComResAttribs> arrOpTypeList;
	private ArrayList<ComResAttribs> arrWindDirList;
	private ArrayList<ComResAttribs> arrSkyConditionList;
	private ArrayList<ComResAttribs> arrAppMethodList;
	private ArrayList<ComResAttribs> arrSoilConditionList;
	private ArrayList<ComResAttribs> arrSoilTypeList;
	private ArrayList<ComResAttribs> arrGrowthStageList;
	private ArrayList<ComResAttribs> arrSupplyTypeList;
	private ArrayList<ComResAttribs> arrIconGroupList;
	private ArrayList<ComResAttribs> arrPestList;
	
	private ArrayList<ResAttribUnit> arrUnitList;
	private ArrayList<ResAttribUnit> arrChargeUnitList;
	private ArrayList<ResAttribCommodity> arrCommodityList;
	private ArrayList<ResAttribCrop> arrCropList;
	private ArrayList<ResAttribCarrier> arrCarrierList;
	private ArrayList<ResAttribPeople> arrPeopleList;
	private ArrayList<ResAttribEquipment> arrEquipmentList;
	
	private ArrayList<ComResAttribsEx> arrClientList;
	private ArrayList<ResAttribFarm> arrFarmList;
	private ArrayList<ResAttribField> arrFieldList;
	
	/* 
	 * constructor
	 */
	public ResourceTags() {		
		resources.put("note_type_list", 1); //put(1, "note_type");
		resources.put("optype_list", 2); //put(2, "optype");
		resources.put("wind_dir_list", 3); //put(3, "wind_dir");
		resources.put("sky_condition_list", 4); //put(4, "sky_condition");
		resources.put("app_method_list", 5); //put(5, "app_method");
		resources.put("soil_condition_list", 6); //put(6, "soil_condition");
		resources.put("soil_type_list", 7); //put(7, "soil_type");
		resources.put("growth_stage_list", 8); //put(8, "growth_stage");
		resources.put("supply_type_list", 9); //put(9, "supply_type");
		resources.put("icongroup_list", 10); //put(10, "icongroup");
		resources.put("pest_list", 11); //put(11, "pest");
		resources.put("unit_list", 12); //put(12, "unit");
		resources.put("charge_unit_list", 13); //put(13, "charge_unit");
		resources.put("commodity_list", 14); //put(14, "commodity");
		resources.put("crop_list", 15); //put(15, "crop");
		resources.put("carrier_list", 16); //put(16, "carrier");
		resources.put("people_list", 17); //put(17, "people");
		resources.put("equipment_list", 18); //put(18, "equipment");
		resources.put("supply_list",19 ); //put(19, "supply");
		resources.put("tankmix_list", 20); //put(20, "tankmix");
		resources.put("client_list", 21); //put(21, "client");
		resources.put("farm_list", 22); //put(22, "farm");
		resources.put("field_list", 23); //put(23, "field");
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		//clean all array-lists
		arrNoteTypeList.clear();
		arrOpTypeList.clear();
		arrWindDirList.clear();
		arrSkyConditionList.clear();
		arrAppMethodList.clear();
		arrSoilConditionList.clear();
		arrSoilTypeList.clear();
		arrGrowthStageList.clear();
		arrSupplyTypeList.clear();
		arrIconGroupList.clear();
		arrPestList.clear();
		
		arrUnitList.clear();
		arrChargeUnitList.clear();
		arrCommodityList.clear();
		arrCropList.clear();
		arrCarrierList.clear();
		arrPeopleList.clear();
		arrEquipmentList.clear();
		
		arrClientList.clear();
		arrFarmList.clear();
		arrFieldList.clear();
	}

	public void parseElement(String sResName, XmlPullParser xmlpullparser) throws Exception{
		Log.i(sLOGTAG, "TAG_NAME : " + "Inside ParseTag() with " + sResName);
		if(resources.isEmpty()){
			Log.i(sLOGTAG, "TAG_NAME : " + "resources hash is empty" );
			return;
		}
		int nTag = 0;
					
		try{
		nTag = resources.get(sResName);
		}
		catch(Exception exp){
			Log.i(sLOGTAG, "EXCEPTION : " + exp.getMessage());
			return;
		}
		
		Log.i(sLOGTAG, "TAG_NAME : " + "Obtained int tag");
		
		switch(nTag){
//			case 1 :	//note_type_list
//			{
//				arrNoteTypeList =new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("note_type".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "note_type", xmlpullparser))
//			        		arrNoteTypeList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("note_type_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}				
//				Log.i(sLOGTAG, "TAG_NAME : " + "note_type");
//			}
//			break;
//			case 2:		//optype_list
//			{
//				arrOpTypeList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("optype".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "optype", xmlpullparser))
//			        		arrOpTypeList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("optype_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "optype");
//			}
//			break;
//			case 3:		//wind_dir_list
//			{
//				arrWindDirList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("wind_dir".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "wind_dir", xmlpullparser))
//			        		arrWindDirList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("wind_dir_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "wind_dir");
//			}
//			break;
//			case 4:		//sky_condition_list
//			{
//				arrSkyConditionList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("sky_condition".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "sky_condition", xmlpullparser))
//			        		arrSkyConditionList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("sky_condition_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "sky_condition");
//			}
//			break;
//			case 5:		//app_method_list
//			{
//				arrAppMethodList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("app_method".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "app_method", xmlpullparser))
//			        		arrAppMethodList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("app_method_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "app_method");
//			}
//			break;
//			case 6:		//soil_condition_list
//			{
//				arrSoilConditionList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("soil_condition".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "soil_condition", xmlpullparser))
//			        		arrSoilConditionList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("soil_condition_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "soil_condition");
//			}
//			break;
//			case 7:		//soil_type_list
//			{
//				arrSoilTypeList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("soil_type".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "soil_type", xmlpullparser))
//			        		arrSoilTypeList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("soil_type_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "soil_type");
//			}
//			break;
//			case 8:		//growth_stage_list
//			{
//				arrGrowthStageList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("growth_stage".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "growth_stage", xmlpullparser))
//			        		arrGrowthStageList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("growth_stage_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "growth_stage");
//			}
//			break;
//			case 9:		//supply_type_list
//			{
//				arrSupplyTypeList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("supply_type".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "supply_type", xmlpullparser))
//			        		arrSupplyTypeList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("supply_type_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "supply_type");
//			}
//			break;
//			case 10:		//icongroup_list
//			{
//				arrIconGroupList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("icongroup".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "icongroup", xmlpullparser))
//			        		arrIconGroupList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("icongroup_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "icongroup");
//			}
//			break;
//			case 11:		//pest_list
//			{
//				arrPestList = new ArrayList<ComResAttribs>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("pest".equals(xmlpullparser.getName()))){
//			        	ComResAttribs attr = new ComResAttribs();			        	
//			        	if(setComResAttribs(attr, "pest", xmlpullparser))
//			        		arrPestList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("pest_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "pest");
//			}
//			break;
//			case 12:		//unit_list
//			{
//				setArrUnitList(new ArrayList<ResAttribUnit>());
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("unit".equals(xmlpullparser.getName()))){
//				    	ResAttribUnit attr = new ResAttribUnit();			        	
//			        	if(setResAttribUnits(attr, "unit", xmlpullparser))
//			        		getArrUnitList().add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("unit_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "unit");
//			}
//			break;
//			case 13:		//charge_unit_list
//			{
//				arrChargeUnitList = new ArrayList<ResAttribUnit>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("charge_unit".equals(xmlpullparser.getName()))){
//				    	ResAttribUnit attr = new ResAttribUnit();			        	
//			        	if(setResAttribUnits(attr, "charge_unit", xmlpullparser))
//			        		arrChargeUnitList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("charge_unit_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "charge_unit");
//			}
//			break;
//			case 14:		//commodity_list
//			{
//				arrCommodityList = new ArrayList<ResAttribCommodity>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("commodity".equals(xmlpullparser.getName()))){
//				    	ResAttribCommodity attr = new ResAttribCommodity();			        	
//			        	if(setResAttribCommodities(attr, xmlpullparser))
//			        		arrCommodityList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("commodity_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "commodity");
//			}
//			break;
//			case 15:		//crop_list
//			{
//				arrCropList = new ArrayList<ResAttribCrop>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("crop".equals(xmlpullparser.getName()))){
//				    	ResAttribCrop attr = new ResAttribCrop();			        	
//			        	if(setResAttribCrops(attr, xmlpullparser))
//			        		arrCropList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("crop_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "crop");
//			}
//			break;
//			case 16:		//carrier_list
//			{
//				arrCarrierList = new ArrayList<ResAttribCarrier>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("carrier".equals(xmlpullparser.getName()))){
//				    	ResAttribCarrier attr = new ResAttribCarrier();			        	
//			        	if(setResAttribCarrier(attr, xmlpullparser))
//			        		arrCarrierList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("carrier_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "carrier");
//			}				
//			break;
//			case 17:		//people_list
//			{
//				arrPeopleList = new ArrayList<ResAttribPeople>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("people".equals(xmlpullparser.getName()))){
//				    	ResAttribPeople attr = new ResAttribPeople();			        	
//			        	if(setResAttribPeople(attr, xmlpullparser))
//			        		arrPeopleList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("people_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "people");
//			}
//			break;
//			case 18:		//equipment_list
//			{
//				arrEquipmentList = new ArrayList<ResAttribEquipment>();
//				
//				eventType=xmlpullparser.getEventType();
//				//parse through nodes				
//				while(eventType!=xmlpullparser.END_DOCUMENT){
//				    if( (eventType==xmlpullparser.START_TAG) && ("equipment".equals(xmlpullparser.getName()))){
//				    	ResAttribEquipment attr = new ResAttribEquipment();			        	
//			        	if(setResAttribEquipment(attr, xmlpullparser))
//			        		arrEquipmentList.add(attr);				        	
//				    }
//				    else if( (eventType==xmlpullparser.END_TAG) && ("equipment_list".equals(xmlpullparser.getName()))){
//				    		break;
//				    }				   
//				    eventType= xmlpullparser.next();
//				}
//				Log.i(sLOGTAG, "TAG_NAME : " + "equipment");
//			}
//			break;
//			case 19:
//				Log.i(sLOGTAG, "TAG_NAME : " + "supply");
//				break;
//			case 20:
//				Log.i(sLOGTAG, "TAG_NAME : " + "tankmix");
//				break;
			case 21:		//client_list
			{
				setArrClientList(new ArrayList<ComResAttribsEx>());
				
				eventType=xmlpullparser.getEventType();
				//parse through nodes				
				while(eventType!=XmlPullParser.END_DOCUMENT){
				    if( (eventType==XmlPullParser.START_TAG) && ("client".equals(xmlpullparser.getName()))){
				    	ComResAttribsEx attr = new ComResAttribsEx();			        	
			        	if(setComResAttribsEx(attr, "client", xmlpullparser))
			        		getArrClientList().add(attr);				        	
				    }
				    else if( (eventType==XmlPullParser.END_TAG) && ("client_list".equals(xmlpullparser.getName()))){
				    		break;
				    }				   
				    eventType= xmlpullparser.next();
				}
				Log.i(sLOGTAG, "TAG_NAME : " + "client");
			}
			break;
			case 22:		//farm_list
			{
				setArrFarmList(new ArrayList<ResAttribFarm>());
				
				eventType=xmlpullparser.getEventType();
				//parse through nodes				
				while(eventType!=XmlPullParser.END_DOCUMENT){
				    if( (eventType==xmlpullparser.START_TAG) && ("farm".equals(xmlpullparser.getName()))){
				    	ResAttribFarm attr = new ResAttribFarm();			        	
			        	if(setResAttribFarm(attr, xmlpullparser))
			        		getArrFarmList().add(attr);				        	
				    }
				    else if( (eventType==XmlPullParser.END_TAG) && ("farm_list".equals(xmlpullparser.getName()))){
				    		break;
				    }				   
				    eventType= xmlpullparser.next();
				}
				Log.i(sLOGTAG, "TAG_NAME : " + "farm");
			}
			break;
			case 23:		//field_list
			{
				setArrFieldList(new ArrayList<ResAttribField>());
				
				eventType=xmlpullparser.getEventType();
				//parse through nodes				
				while(eventType!=XmlPullParser.END_DOCUMENT){
				    if( (eventType==XmlPullParser.START_TAG) && ("field".equals(xmlpullparser.getName()))){
				    	ResAttribField attr = new ResAttribField();			        	
			        	if(setResAttribField(attr, xmlpullparser))
			        		getArrFieldList().add(attr);				        	
				    }
				    else if( (eventType==XmlPullParser.END_TAG) && ("field_list".equals(xmlpullparser.getName()))){
				    		break;
				    }				   
				    eventType= xmlpullparser.next();
				}
				Log.i(sLOGTAG, "TAG_NAME : " + "field");
			}
			break;
			default:
				Log.i(sLOGTAG, "TAG_NAME : " + "Unsupported node");
				break;			
		}
		
	}

	private boolean setComResAttribs(ComResAttribs attr, String sTagName, XmlPullParser xmlpullparser) throws XmlPullParserException, IOException{
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();
		
		//if params are stacked as attributes
		if( nAttrCount > 0 ){
			for(int i = 0; i < nAttrCount; i++ ){    		
				switch(i){
	    		case 0: //id
	    			attr.setID(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'id' is set" );
	    			isAnyAttrSet  = true;
	    			break;				        		
	    		case 1: //description
	    			attr.setDescription(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'desc' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 2: //locked
	    			attr.setLocked(Integer.parseInt(xmlpullparser.getAttributeValue(i)));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'locked' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		default:
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - unknown parameter has come" );
	    			break;
	    		}
	    	}
		}
		//if params are stacked as nodes
		else{
			boolean bNodeCompleted = false;
			while(!bNodeCompleted){
				if( (eventType==XmlPullParser.END_TAG) && (sTagName.equals(xmlpullparser.getName()))){
					bNodeCompleted = true;
				}
				else{
					eventType = xmlpullparser.next();
					if(eventType==XmlPullParser.START_TAG){
						String sAttrName = xmlpullparser.getName();
						if( sAttrName.equalsIgnoreCase("id") ){
							xmlpullparser.next(); // go to text
							attr.setID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'id' is " + attr.getID());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("desc") ){
							xmlpullparser.next(); // go to text
							attr.setDescription(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'desc' is " + attr.getDescription() );
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("locked") ){
							xmlpullparser.next(); // go to text
							attr.setLocked( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'locked' is " + attr.getLocked() );
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}						
						else
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - unknown parameter has come" );						
					}
				}
			}			    
		}
    	
    	return isAnyAttrSet;
	}
	
	private boolean setComResAttribsEx(ComResAttribsEx attr, String sTagName, XmlPullParser xmlpullparser) throws XmlPullParserException, IOException{
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();
		
		//if params are stacked as attributes
		if( nAttrCount > 0 ){
			for (int i = 0; i < nAttrCount; i++) {
				switch (i) {
				case 0: // id
					attr.setID(xmlpullparser.getAttributeValue(i));
					Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'id' is set");
					isAnyAttrSet = true;
					break;
				case 1: // description
					attr.setDescription(xmlpullparser.getAttributeValue(i));
					Log.i(sLOGTAG, "TAG_NAME : " + sTagName
							+ " - 'desc' is set");
					isAnyAttrSet = true;
					break;
				case 2: // locked
					attr.setLocked(Integer.parseInt(xmlpullparser
							.getAttributeValue(i)));
					Log.i(sLOGTAG, "TAG_NAME : " + sTagName
							+ " - 'locked' is set");
					isAnyAttrSet = true;
					break;
				case 3: // deleted
					attr.setDeleted(Integer.parseInt(xmlpullparser
							.getAttributeValue(i)));
					Log.i(sLOGTAG, "TAG_NAME : " + sTagName
							+ " - 'deleted' is set");
					isAnyAttrSet = true;
					break;
				default:
					Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - unknown parameter has come" );
					break;
				}
			}
		}
		//if params are stacked as nodes
		else{
			boolean bNodeCompleted = false;
			while(!bNodeCompleted){
				if( (eventType==XmlPullParser.END_TAG) && (sTagName.equals(xmlpullparser.getName()))){
					bNodeCompleted = true;
				}
				else{
					eventType = xmlpullparser.next();
					if(eventType==XmlPullParser.START_TAG){
						String sAttrName = xmlpullparser.getName();
						if( sAttrName.equalsIgnoreCase("id") ){
							xmlpullparser.next(); // go to text
							attr.setID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'id' is " + attr.getID());
			    			isAnyAttrSet  = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("desc") ){
							xmlpullparser.next(); // go to text
							attr.setDescription(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'desc' is " + attr.getDescription());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("locked") ){
							xmlpullparser.next(); // go to text
							attr.setLocked( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'locked' is " + attr.getLocked());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("deleted") ){
							xmlpullparser.next(); // go to text
							attr.setDeleted( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'deleted' is " + attr.getDeleted());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - unknown parameter has come" );				
					}
				}
			}			    
		}
    	return isAnyAttrSet;
	}
	
	private boolean setResAttribFarm(ResAttribFarm attr, XmlPullParser xmlpullparser) throws XmlPullParserException, IOException{
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();
		
		//if params are stacked as attributes
		if( nAttrCount > 0 ){
			for(int i = 0; i < nAttrCount; i++ ){    		
				switch(i){
	    		case 0: //id
	    			attr.setID(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "farm" + " - 'id' is set" );
	    			isAnyAttrSet  = true;
	    			break;				        		
	    		case 1: //description
	    			attr.setDescription(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "farm" + " - 'desc' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 2: //area
	    			attr.setArea(Float.parseFloat(xmlpullparser.getAttributeValue(i)));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "farm" + " - 'area' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 3: //areaunit
	    			attr.setAreaUnit(xmlpullparser.getAttributeValue(i));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "farm" + " - 'unit' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 4: //clientid
	    			attr.setClientID(xmlpullparser.getAttributeValue(i));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "farm" + " - 'clientid' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		default:
	    			Log.i(sLOGTAG, "TAG_NAME : " + "farm" + " - unknown parameter has come" );
	    			break;
	    		}
	    	}	
		}
		//if params are stacked as nodes
		else{
			boolean bNodeCompleted = false;
			while(!bNodeCompleted){
				if( (eventType==XmlPullParser.END_TAG) && ("farm".equalsIgnoreCase(xmlpullparser.getName()))){
					bNodeCompleted = true;
				}
				else{
					eventType = xmlpullparser.next();
					if(eventType==XmlPullParser.START_TAG){
						String sAttrName = xmlpullparser.getName();
						if( sAttrName.equalsIgnoreCase("id") ){
							xmlpullparser.next(); // go to text
							attr.setID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : farm - 'id' is " + attr.getID());
			    			isAnyAttrSet  = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("desc") ){
							xmlpullparser.next(); // go to text
							attr.setDescription(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : farm - 'desc' is " + attr.getDescription());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("locked") ){
							xmlpullparser.next(); // go to text
							attr.setLocked( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : farm - 'locked' is " + attr.getLocked());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("deleted") ){
							xmlpullparser.next(); // go to text
							attr.setDeleted( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : farm - 'deleted' is " + attr.getDeleted());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("clientid") ){
							xmlpullparser.next(); // go to text
							attr.setClientID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : farm - 'clientid' is " + attr.getClientID());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("area") ){
							xmlpullparser.next(); // go to text
							attr.setArea(Float.parseFloat(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : farm - 'area' is " + attr.getArea());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("unit") ){
							xmlpullparser.next(); // go to text
							attr.setAreaUnit(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : farm - 'area unit' is " + attr.getAreaUnit() );
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else
							Log.i(sLOGTAG, "TAG_NAME : " + "farm" + " - unknown parameter has come" );
					}
				}
			}			    
		}
    	
    	return isAnyAttrSet;
	}
	
	private boolean setResAttribField(ResAttribField attr, XmlPullParser xmlpullparser) throws XmlPullParserException, IOException{
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();

		//if params are stacked as attributes
		if( nAttrCount > 0){
			for(int i = 0; i < nAttrCount; i++ ){    		
				switch(i){
	    		case 0: //id
	    			attr.setID(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "field" + " - 'id' is set" );
	    			isAnyAttrSet  = true;
	    			break;				        		
	    		case 1: //description
	    			attr.setDescription(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "field" + " - 'desc' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 2: //area
	    			attr.setArea(Float.parseFloat(xmlpullparser.getAttributeValue(i)));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "field" + " - 'area' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 3: //areaunit
	    			attr.setAreaUnit(xmlpullparser.getAttributeValue(i));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "field" + " - 'unit' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 4: //farmid
	    			attr.setFarmID(xmlpullparser.getAttributeValue(i));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "field" + " - 'farmid' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		default:
	    			Log.i(sLOGTAG, "TAG_NAME : " + "field" + " - unknown parameter has come" );
	    			break;
	    		}
	    	}		
		}
		//if params are stacked as nodes
		else{
			boolean bNodeCompleted = false;
			while(!bNodeCompleted){
				if( (eventType==XmlPullParser.END_TAG) && ("field".equalsIgnoreCase(xmlpullparser.getName()))){
					bNodeCompleted = true;
				}
				else{
					eventType = xmlpullparser.next();
					if(eventType==XmlPullParser.START_TAG){
						String sAttrName = xmlpullparser.getName();
						if( sAttrName.equalsIgnoreCase("id") ){
							xmlpullparser.next(); // go to text
							attr.setID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : field - 'id' is " + attr.getID());
			    			isAnyAttrSet  = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("desc") ){
							xmlpullparser.next(); // go to text
							attr.setDescription(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : field - 'desc' is " + attr.getDescription());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("locked") ){
							xmlpullparser.next(); // go to text
							attr.setLocked( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : field - 'locked' is " + attr.getLocked());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("deleted") ){
							xmlpullparser.next(); // go to text
							attr.setDeleted( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : field - 'deleted' is " + attr.getDeleted() );
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("farmid") ){
							xmlpullparser.next(); // go to text
							attr.setFarmID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : field - 'farmid' is " + attr.getFarmID());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("area") ){
							xmlpullparser.next(); // go to text
							attr.setArea(Float.parseFloat(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : field - 'area' is " + attr.getArea());
			    			isAnyAttrSet = true;
			    			eventType = xmlpullparser.next(); //go to start-tag		    			
							if( (eventType == XmlPullParser.START_TAG) && ("unit".equalsIgnoreCase(xmlpullparser.getName()))){
								xmlpullparser.next(); // go to text
								attr.setAreaUnit(xmlpullparser.getText());
								Log.i(sLOGTAG, "TAG_NAME : field - 'area unit' is " + attr.getAreaUnit() );
				    			isAnyAttrSet = true;
								eventType = xmlpullparser.next(); //go to end								
							}
						}
						else if( sAttrName.equalsIgnoreCase("boundary") ){
							boolean bBdryNodeCompleted = false;
							while(!bBdryNodeCompleted){
								if( (eventType==XmlPullParser.END_TAG) && ("boundary".equalsIgnoreCase(xmlpullparser.getName()))){
									bBdryNodeCompleted = true;
								}	
								else{
									eventType = xmlpullparser.next(); // go to start-tag
									if(eventType==XmlPullParser.START_TAG){
										sAttrName = xmlpullparser.getName();
										if( sAttrName.equalsIgnoreCase("revision") ){
											xmlpullparser.next(); // go to text
											attr.setBdryRevision( Integer.parseInt(xmlpullparser.getText()));
											Log.i(sLOGTAG, "TAG_NAME : field - 'bdry - revision' is " + attr.getBdryRevision() );
							    			isAnyAttrSet = true;
											eventType = xmlpullparser.next(); //go to end
										}
										else if( sAttrName.equalsIgnoreCase("modified") ){
											xmlpullparser.next(); // go to text
											attr.setBdryModified( Integer.parseInt(xmlpullparser.getText()));
											Log.i(sLOGTAG, "TAG_NAME : field - 'bdry - modified' is " + attr.getBdryModified() );
							    			isAnyAttrSet = true;
											eventType = xmlpullparser.next(); //go to end
										}
									}
								}
							}
						}						
						else
							Log.i(sLOGTAG, "TAG_NAME : " + "field" + " - unknown parameter has come" );	
					}
				}
			}			    
		}
    	return isAnyAttrSet;
	}
	
	private boolean setResAttribUnits(ResAttribUnit attr, String sTagName, XmlPullParser xmlpullparser) throws XmlPullParserException, IOException{
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();

		//if params are stacked as attributes
		if( nAttrCount > 0){
			for(int i = 0; i < nAttrCount; i++ ){    		
				switch(i){
	    		case 0: //id
	    			attr.setID(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'id' is set" );
	    			isAnyAttrSet  = true;
	    			break;				        		
	    		case 1: //description
	    			attr.setDescription(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'desc' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 2: //short
	    			attr.setShort(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'short' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 3: //singular
	    			attr.setSingular(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'singular' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 4: //type
	    			attr.setType(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'type' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 5: //metric
	    			attr.setMetricSystem(Integer.parseInt(xmlpullparser.getAttributeValue(i)));
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'metric' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 6: //unitconv
	    			attr.setUnitConv(Float.parseFloat(xmlpullparser.getAttributeValue(i)));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'unitconv' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		default:
	    			Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - unknown parameter has come" );
	    			break;
	    		}
	    	}	
		}
		//if params are stacked as nodes
		else{
			boolean bNodeCompleted = false;
			while(!bNodeCompleted){
				if( (eventType==XmlPullParser.END_TAG) && (sTagName.equalsIgnoreCase(xmlpullparser.getName()))){
					bNodeCompleted = true;
				}
				else{
					eventType = xmlpullparser.next();
					if(eventType==XmlPullParser.START_TAG){
						String sAttrName = xmlpullparser.getName();
						if( sAttrName.equalsIgnoreCase("id") ){
							xmlpullparser.next(); // go to text
							attr.setID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'id' is " + attr.getID() );
			    			isAnyAttrSet  = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("desc") ){
							xmlpullparser.next(); // go to text
							attr.setDescription(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'desc' is " + attr.getDescription() );
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("locked") ){
							xmlpullparser.next(); // go to text
							attr.setLocked( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'locked' is " + attr.getLocked() );
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("short") ){
							xmlpullparser.next(); // go to text
							attr.setShort(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'short' is " + attr.getShort() );
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("singular") ){
							xmlpullparser.next(); // go to text
							attr.setSingular(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'singular' is " + attr.getSingular());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("type") ){
							xmlpullparser.next(); // go to text
							attr.setType(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'type' is " + attr.getType());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("metric") ){
							xmlpullparser.next(); // go to text
							attr.setMetricSystem( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - 'metric' is " + attr.getMetricSystem()); 
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("unitconv") ){
							xmlpullparser.next(); // go to text
							attr.setUnitConv(Float.parseFloat(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : field - 'unitconv' is " + attr.getUnitConv());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}						
						else
							Log.i(sLOGTAG, "TAG_NAME : " + sTagName + " - unknown parameter has come" );						
					}
				}
			}			    
		}
    	
    	return isAnyAttrSet;
	}
	
	private boolean setResAttribCommodities(ResAttribCommodity attr, XmlPullParser xmlpullparser) throws XmlPullParserException, IOException{
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();

		//if params are stacked as attributes
		if( nAttrCount > 0){
			for(int i = 0; i < nAttrCount; i++ ){    		
				switch(i){
	    		case 0: //id
	    			attr.setID(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "commodity" + " - 'id' is set" );
	    			isAnyAttrSet  = true;
	    			break;				        		
	    		case 1: //description
	    			attr.setDescription(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "commodity" + " - 'desc' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 2: //unit
	    			attr.setUnit(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "commodity" + " - 'unit' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 3: //unitconv
	    			attr.setUnitConv(Float.parseFloat(xmlpullparser.getAttributeValue(i)));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "commodity" + " - 'unitconv' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		default:
	    			Log.i(sLOGTAG, "TAG_NAME : " + "commodity" + " - unknown parameter has come" );
	    			break;
	    		}
	    	}	
		}
		//if params are stacked as nodes
		else{
			boolean bNodeCompleted = false;
			while(!bNodeCompleted){
				if( (eventType==XmlPullParser.END_TAG) && ("commodity".equalsIgnoreCase(xmlpullparser.getName()))){
					bNodeCompleted = true;
				}
				else{
					eventType = xmlpullparser.next();
					if(eventType==XmlPullParser.START_TAG){
						String sAttrName = xmlpullparser.getName();
						if( sAttrName.equalsIgnoreCase("id") ){
							xmlpullparser.next(); // go to text
							attr.setID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : commodity - 'id' is " + attr.getID());
			    			isAnyAttrSet  = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("desc") ){
							xmlpullparser.next(); // go to text
							attr.setDescription(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : commodity - 'desc' is " + attr.getDescription() );
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("locked") ){
							xmlpullparser.next(); // go to text
							attr.setLocked( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : commodity - 'locked' is " + attr.getLocked());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("unit") ){
							xmlpullparser.next(); // go to text
							attr.setUnit(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : commodity - 'unit' is " + attr.getUnit());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("unitconv") ){
							xmlpullparser.next(); // go to text
							attr.setUnitConv(Float.parseFloat(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : commodity - 'unitconv' is " + attr.getUnitConv());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}						
						else
							Log.i(sLOGTAG, "TAG_NAME : " + "commodity" + " - unknown parameter has come" );			
					}
				}
			}			    
		}
    	return isAnyAttrSet;
	}
	
	private boolean setResAttribCrops(ResAttribCrop attr, XmlPullParser xmlpullparser) throws XmlPullParserException, IOException{
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();

		//if params are stacked as attributes
		if( nAttrCount > 0){
			for(int i = 0; i < nAttrCount; i++ ){    		
				switch(i){
	    		case 0: //id
	    			attr.setID(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "crop" + " - 'id' is set" );
	    			isAnyAttrSet  = true;
	    			break;				        		
	    		case 1: //description
	    			attr.setDescription(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "crop" + " - 'desc' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 2: //commodity
	    			attr.setCommodity(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "crop" + " - 'commodity' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 3: //unitconv
	    			attr.setYear(xmlpullparser.getAttributeValue(i));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "crop" + " - 'year' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		default:
	    			Log.i(sLOGTAG, "TAG_NAME : " + "crop" + "unknown attribute has come" );
	    			break;
	    		}
	    	}	
		}
		//if params are stacked as nodes
		else{
			boolean bNodeCompleted = false;
			while(!bNodeCompleted){
				if( (eventType==XmlPullParser.END_TAG) && ("crop".equalsIgnoreCase(xmlpullparser.getName()))){
					bNodeCompleted = true;
				}
				else{
					eventType = xmlpullparser.next();
					if(eventType==XmlPullParser.START_TAG){
						String sAttrName = xmlpullparser.getName();
						if( sAttrName.equalsIgnoreCase("id") ){
							xmlpullparser.next(); // go to text
							attr.setID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : crop - 'id' is " + attr.getID());
			    			isAnyAttrSet  = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("desc") ){
							xmlpullparser.next(); // go to text
							attr.setDescription(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : crop - 'desc' is " + attr.getDescription());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("locked") ){
							xmlpullparser.next(); // go to text
							attr.setLocked( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : crop - 'locked' is " + attr.getLocked());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("commodity") ){
							xmlpullparser.next(); // go to text
							attr.setCommodity(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : crop - 'commodity' is " + attr.getCommodity());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("year") ){
							xmlpullparser.next(); // go to text
							attr.setYear(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : crop - 'year' is " + attr.getYear());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}						
						else
							Log.i(sLOGTAG, "TAG_NAME : " + "crop" + " - unknown parameter has come" );						
					}
				}
			}			    
		}
    	
    	return isAnyAttrSet;
	}
	
	private boolean setResAttribPeople(ResAttribPeople attr, XmlPullParser xmlpullparser){
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();
		
    	for(int i = 0; i < nAttrCount; i++ ){    		
			switch(i){
    		case 0: //id
    			attr.setID(xmlpullparser.getAttributeValue(i));
    			Log.i(sLOGTAG, "TAG_NAME : " + "people" + " - 'id' is set" );
    			isAnyAttrSet  = true;
    			break;				        		
    		case 1: //description
    			attr.setDescription(xmlpullparser.getAttributeValue(i));
    			Log.i(sLOGTAG, "TAG_NAME : " + "people" + " - 'desc' is set" );
    			isAnyAttrSet = true;
    			break;
    		case 2: //epa_num
    			attr.setEPANumber(xmlpullparser.getAttributeValue(i));
    			Log.i(sLOGTAG, "TAG_NAME : " + "people" + " - 'epa_num' is set" );
    			isAnyAttrSet = true;
    			break;
    		default:
    			Log.i(sLOGTAG, "TAG_NAME : " + "people" + "unknown attribute has come" );
    			break;
    		}
    	}
    	return isAnyAttrSet;
	}
	
	private boolean setResAttribEquipment(ResAttribEquipment attr, XmlPullParser xmlpullparser){
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();
		
    	for(int i = 0; i < nAttrCount; i++ ){    		
			switch(i){
    		case 0: //id
    			attr.setID(xmlpullparser.getAttributeValue(i));
    			Log.i(sLOGTAG, "TAG_NAME : " + "equipment" + " - 'id' is set" );
    			isAnyAttrSet  = true;
    			break;				        		
    		case 1: //description
    			attr.setDescription(xmlpullparser.getAttributeValue(i));
    			Log.i(sLOGTAG, "TAG_NAME : " + "equipment" + " - 'desc' is set" );
    			isAnyAttrSet = true;
    			break;
    		case 2: //icon_group
    			attr.setIconGroup(xmlpullparser.getAttributeValue(i));
    			Log.i(sLOGTAG, "TAG_NAME : " + "equipment" + " - 'icon_group' is set" );
    			isAnyAttrSet = true;
    			break;
    		case 3: //charge_units
    			attr.setChargeUnits(xmlpullparser.getAttributeValue(i));
    			Log.i(sLOGTAG, "TAG_NAME : " + "equipment" + " - 'charge_units' is set" );
    			isAnyAttrSet = true;
    			break;
    		case 4: //locked
    			attr.setLocked(Integer.parseInt(xmlpullparser.getAttributeValue(i)));
    			Log.i(sLOGTAG, "TAG_NAME : " + "equipment" + " - 'locked' is set" );
    			isAnyAttrSet = true;
    			break;
    		case 5: //width
    			attr.setWidth(Float.parseFloat(xmlpullparser.getAttributeValue(i)));
    			Log.i(sLOGTAG, "TAG_NAME : " + "equipment" + " - 'width' is set" );
    			isAnyAttrSet = true;
    			break;
    		default:
    			Log.i(sLOGTAG, "TAG_NAME : " + "equipment" + "unknown attribute has come" );
    			break;
    		}
    	}
    	return isAnyAttrSet;
	}
	
	private boolean setResAttribCarrier(ResAttribCarrier attr, XmlPullParser xmlpullparser) throws XmlPullParserException, IOException{
		boolean isAnyAttrSet = false;
		int nAttrCount = xmlpullparser.getAttributeCount();

		//if params are stacked as attributes
		if( nAttrCount > 0){
			for(int i = 0; i < nAttrCount; i++ ){    		
				switch(i){
	    		case 0: //id
	    			attr.setID(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "carrier" + " - 'id' is set" );
	    			isAnyAttrSet  = true;
	    			break;				        		
	    		case 1: //description
	    			attr.setDescription(xmlpullparser.getAttributeValue(i));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "carrier" + " - 'desc' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 2: //locked
	    			attr.setLocked( Integer.parseInt(xmlpullparser.getAttributeValue(i)));
	    			Log.i(sLOGTAG, "TAG_NAME : " + "carrier" + " - 'locked' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		case 3: //unit
	    			attr.setUnit(xmlpullparser.getAttributeValue(i));    			
	    			Log.i(sLOGTAG, "TAG_NAME : " + "carrier" + " - 'unit' is set" );
	    			isAnyAttrSet = true;
	    			break;
	    		default:
	    			Log.i(sLOGTAG, "TAG_NAME : " + "carrier" + " - unknown parameter has come" );
	    			break;
	    		}
	    	}	
		}
		//if params are stacked as nodes
		else{
			boolean bNodeCompleted = false;
			while(!bNodeCompleted){
				if( (eventType==XmlPullParser.END_TAG) && ("carrier".equalsIgnoreCase(xmlpullparser.getName()))){
					bNodeCompleted = true;
				}
				else{
					eventType = xmlpullparser.next();
					if(eventType==XmlPullParser.START_TAG){
						String sAttrName = xmlpullparser.getName();
						if( sAttrName.equalsIgnoreCase("id") ){
							xmlpullparser.next(); // go to text
							attr.setID(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : carrier - 'id' is " + attr.getID());
			    			isAnyAttrSet  = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("desc") ){
							xmlpullparser.next(); // go to text
							attr.setDescription(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : carrier - 'desc' is " + attr.getDescription());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("locked") ){
							xmlpullparser.next(); // go to text
							attr.setLocked( Integer.parseInt(xmlpullparser.getText()));
							Log.i(sLOGTAG, "TAG_NAME : carrier - 'locked' is " + attr.getLocked());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else if( sAttrName.equalsIgnoreCase("unit") ){
							xmlpullparser.next(); // go to text
							attr.setUnit(xmlpullparser.getText());
							Log.i(sLOGTAG, "TAG_NAME : carrier - 'unit ' is " + attr.getUnit());
			    			isAnyAttrSet = true;
							eventType = xmlpullparser.next(); //go to end
						}
						else
							Log.i(sLOGTAG, "TAG_NAME : carrier - unknown parameter has come" );						
					}
				}
			}			    
		}
    	
    	return isAnyAttrSet;
	}


	public ArrayList<ComResAttribsEx> getArrClientList() {
		return arrClientList;
	}

	public void setArrClientList(ArrayList<ComResAttribsEx> arrClientList) {
		this.arrClientList = arrClientList;
	}

	public ArrayList<ResAttribFarm> getArrFarmList() {
		return arrFarmList;
	}

	public void setArrFarmList(ArrayList<ResAttribFarm> arrFarmList) {
		this.arrFarmList = arrFarmList;
	}

	public ArrayList<ResAttribField> getArrFieldList() {
		return arrFieldList;
	}

	public void setArrFieldList(ArrayList<ResAttribField> arrFieldList) {
		this.arrFieldList = arrFieldList;
	}

	public ArrayList<ResAttribUnit> getArrUnitList() {
		return arrUnitList;
	}

	public void setArrUnitList(ArrayList<ResAttribUnit> arrUnitList) {
		this.arrUnitList = arrUnitList;
	}
}
