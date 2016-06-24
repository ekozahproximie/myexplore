package com.spime.groupon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GrouponParser implements Runnable {
	private static final String ST_REQ_METHOD = "GET";
	private static final String DEALS="deals";
	private static final String COUNTRY="country";
	private static final String DIVISIONS="divisions";
	private static final String TIMEZONE="timezone";
	private static final String ISNOWMERCHANTENABLED="isNowMerchantEnabled";
	private static final String ISNOWCUSTOMERENABLED="isNowCustomerEnabled";
	private static final String PLACEMENTPRIORITY="placementPriority";
	private static final String SMALLIMAGEURL="smallImageUrl";
	private static final String MEDIUMIMAGEURL="mediumImageUrl";
	private static final String SAYS="says";
	private static final String WEBSITECONTENTHTML= "websiteContentHtml";
	private static final String EMAILCONTENTHTML="emailContentHtml";
	private static final String ANNOUNCEMENTTITLE="announcementTitle";
	private static final String DEALURL="dealUrl";
	private static final String STATUS="status";
	private static final String ISTIPPED="isTipped";
	private static final String TIPPINGPOINT="tippingPoint";
	private static final String SHIPPINGADDRESSREQUIRED="shippingAddressRequired";
	private static final String PITCHHTML="pitchHtml";
	private static final String ISNOWDEAL="isNowDeal";
	private static final String TIPPEDAT="tippedAt";
	private static final String ENDAT="endAt";
	private static final String TAGS="tags";
	
	private static final String OPTIONS="options";
	private static final String ID="id";
	private static final String MINIMUMPURCHASEQUANTITY="minimumPurchaseQuantity";
	private static final String EXPIRESAT="expiresAt";
	private static final String REDEMPTIONLOCATIONS="redemptionLocations";
	private static final String POSTALCODE="postalCode";
	private static final String PHONENUMBER="phoneNumber";
	private static final String STREETADDRESS1="streetAddress1";
	private static final String NAME="name";
	private static final String STATE="state";
	private static final String LNG="lng";
	private static final String LAT="lat";
	private static final String CITY="city";
	private static final String ISLIMITEDQUANTITY="isLimitedQuantity";
	private static final String BUYURL="buyUrl";
	private static final String DISCOUNT="discount";
	private static final String AMOUNT="amount";
	private static final String FORMATTEDAMOUNT="formattedAmount";
	private static final String CURRENCYCODE="currencyCode";
	private static final String TITLE="title";
	private static final String SOLDQUANTITY="soldQuantity";
	private static final String PRICE="price";
	private static final String DISCOUNTPERCENT="discountPercent";
	private static final String ISSOLDOUT="isSoldOut";
	private static final String REMAININGQUANTITY="remainingQuantity";
	private static final String MAXIMUMPURCHASEQUANTITY="maximumPurchaseQuantity";
	private static final String VALUE="value";
	private static final String INITIALQUANTITY="initialQuantity";//initialQuantity
	private static final String DETAILS="details";
	private static final String DESCRIPTION="description";
	private static final String LARGEIMAGEURL="largeImageUrl";
	private static final String HIGHLIGHTSHTML="highlightsHtml";
	private static final String MERCHANT="merchant";
	private static final String WEBSITEURL="websiteUrl";
	
	private String stURL = null;
	private Handler mHandler=null;
	int iServiceCode=0;
	public GrouponParser(String stURL,Handler mHandler,int iServiceCode) {
		this.mHandler=mHandler;
		this.iServiceCode=iServiceCode;
		this.stURL = stURL;
	
	}
	public void doJob(){
		Thread thread = new Thread(this);
		thread.start();
	}
	@Override
	public void run() {
		try {
			URL url = new URL(stURL);
			Log.i(GrouponManager.ST_TAG,stURL);
			
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod(ST_REQ_METHOD);
			if (httpURLConnection.getResponseCode() != 200) {
				String stMsg= httpURLConnection.getResponseMessage();
				if(stMsg== null){
					stMsg="Try again later";
				}
				Log.e(GrouponManager.ST_TAG,
						"Response:" +stMsg);
				String stColan=":";
				Map<String, List<String>> resMap= httpURLConnection.getHeaderFields();
				for (String st : resMap.keySet()) {
					StringBuffer buffer = new StringBuffer();
					buffer.append(st);
					buffer.append(stColan);
					buffer.append(resMap.get(st));
					Log.e(GrouponManager.ST_TAG,buffer.toString());
					buffer=null;
				}
				
				sendErrorMsg(stMsg);
				return;
			}
			switch (iServiceCode) {
			case GrouponManager.DIVISIONS:
				responseDivisionsParser(httpURLConnection.getInputStream());
				break;
			case GrouponManager.DEALS:
				responseDealParser(httpURLConnection.getInputStream());
				break;	
			default:
				break;
			}
			
			httpURLConnection.disconnect();
			httpURLConnection = null;
			url = null;
		} catch (MalformedURLException e) {
			sendErrorMsg(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			sendErrorMsg(e.getMessage());
			e.printStackTrace();
		} catch (Throwable e) {
			sendErrorMsg(e.getMessage());
			e.printStackTrace();
		}

	}
private void sendErrorMsg(String stMsg){
	Message message = new Message();
	message.what=iServiceCode;
	message.obj=stMsg;
	mHandler.sendMessage(message);
}
	private void responseDivisionsParser(InputStream inputStream) {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		
		Divisions divisions= null;
		String line;
		try {
			try {
				while ((line = in.readLine()) != null) {
					JSONObject jiObject= new JSONObject(line);
						JSONArray ja = new JSONArray(jiObject.getString(DIVISIONS));
						divisions=parseDivisions(ja);
						ja=null;
						jiObject=null;
			
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					in=null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream=null;
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			Message message = new Message();
			message.what=iServiceCode;
			message.obj=divisions;
			mHandler.sendMessage(message);
		}

	}
	private void responseDealParser(InputStream inputStream) {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		Deals deals=null;
		String line;
		try {
			try {
				while ((line = in.readLine()) != null) {
					JSONObject jiObject= new JSONObject(line);
					JSONArray ja = new JSONArray(jiObject.getString(DEALS));
					deals= new Deals();
					int iNumDeals=ja.length();
					//deals.vecDeal=new Vector<Deal>(iNumDeals);
					deals.hMapDealByTag=new HashMap<String, Vector<Deal>>(iNumDeals);
					Log.i(GrouponManager.ST_TAG,"Total Size:"+iNumDeals );
					for (int i = 0; i < iNumDeals; i++) {

						JSONObject jo = (JSONObject) ja.get(i);
						Deal deal=parseDeal(jo);
						if(deal.tags != null && deal.tags.vecTag != null
								&& deal.tags.vecTag.size() >0 ){
							Tag tag=deal.tags.vecTag.elementAt(0);
							addDealByTag(deal, tag.stName, deals);
						}else{
							String stOther="Others";
							addDealByTag(deal, stOther, deals);
						}
						//deals.vecDeal.add(deal);
						
//						for (@SuppressWarnings("unchecked")
//						Iterator<String> iterator = jo.keys(); iterator
//								.hasNext();) {
//							String stKey = (String) iterator.next();
//							Log.i(GrouponManager.ST_TAG,stKey+"="+jo.getString(stKey));
//						}
						jo=null;
					}
					Log.i(GrouponManager.ST_TAG,"Deal Complete" );
					ja=null;
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (JSONException e) {

			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					in=null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream=null;
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			Message message = new Message();
			message.what=iServiceCode;
			message.obj=deals;
			mHandler.sendMessage(message);
		}

	}
	private void addDealByTag(Deal deal,String stKey,Deals deals){
		Vector<Deal> vecDeal=deals.hMapDealByTag.get(stKey);
		if(vecDeal == null){
			vecDeal=new Vector<Deal>(25);
		}
		vecDeal.add(deal);
		deals.hMapDealByTag.put(stKey,vecDeal );
	}
	private Divisions parseDivisions(JSONArray ja){
		Divisions divisions= null;
		try {
			
			 divisions= new Divisions();
		divisions.vecDivisions=new Vector<Division>(5);
		
		for (int i = 0; i < ja.length(); i++) {

			JSONObject jo = (JSONObject) ja.get(i);
			Division division= new Division(
					jo.getBoolean(ISNOWCUSTOMERENABLED), 
					jo.getBoolean(ISNOWMERCHANTENABLED), 
					jo.getString(ID),
					jo.getString(TIMEZONE), 
					jo.getString(NAME), 
					jo.getString(COUNTRY), 
					jo.getDouble(LNG), 
					jo.getDouble(LAT));
			divisions.vecDivisions.add(division);
			division.iTimezoneOffsetInSeconds=jo.getInt("timezoneOffsetInSeconds");
			Log.i(GrouponManager.ST_TAG,division.toString());
			//jo.get("areas");
			//jo.get("timezoneOffsetInSeconds");
			jo=null;
		}
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return divisions;
	}
	private Deal parseDeal(JSONObject jo){
		Deal deal=null;
		try {
			deal = new Deal();
			deal.stID=jo.getString(ID);
			deal.stTitle=jo.getString(TITLE);
			deal.stPlacementPriority=jo.getString(PLACEMENTPRIORITY);
			deal.stSmallImageUrl=jo.getString(SMALLIMAGEURL);
			deal.stMediumImageUrl= jo.getString(MEDIUMIMAGEURL);
			deal.stLargeImageUrl= jo.getString(LARGEIMAGEURL);
			deal.stHighlightsHtml= jo.getString(HIGHLIGHTSHTML);
			
			//JSONObject joSays=jo.getJSONObject(SAYS);
			//deal.says=parseSays(joSays);
			//joSays=null;
			deal.stAnnouncementTitle=jo.getString(ANNOUNCEMENTTITLE);
			deal.tags=parseTags(jo);
			deal.stDealUrl=jo.getString(DEALURL);
			deal.stStatus=jo.getString(STATUS);
			deal.isTipped=jo.getBoolean(ISTIPPED);
			deal.iTippingPoint=jo.getInt(TIPPINGPOINT);
			deal.isSoldOut=jo.getBoolean(ISSOLDOUT);
			deal.iSoldQuantity=jo.getInt(SOLDQUANTITY);
			deal.isShippingAddressRequired=jo.getBoolean(SHIPPINGADDRESSREQUIRED);
			deal.options=parseOptions(jo);
			JSONObject joMerchant= jo.getJSONObject(MERCHANT);
			deal.merchant=parseMerchant(joMerchant);
			joMerchant=null;
			deal.stPitchHtml=jo.getString(PITCHHTML);
			deal.stEndAt=jo.getString(ENDAT);
			deal.isNowDeal=jo.getBoolean(ISNOWDEAL);
			if(deal.isTipped){ 
			deal.stTippedAt=jo.getString(TIPPEDAT);
			}
			
			
		
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return deal;
	}

	private Says parseSays(JSONObject jiObject){
		Says says=null;
		try {
			
			 says= new Says(jiObject.getString(WEBSITECONTENTHTML),
					 jiObject.getString(EMAILCONTENTHTML));
	
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return says;
	}

	private Merchant parseMerchant(JSONObject jiObject){
		Merchant merchant=null;
		try {
			
			merchant= new Merchant(jiObject.getString(WEBSITEURL),
					 jiObject.getString(NAME));
			
		
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return merchant;
	}
	private Options parseOptions(JSONObject jiObject){
		JSONArray ja;
		Options options=null;
		try {
			ja = new JSONArray(jiObject.getString(OPTIONS));
			options = new Options();
			options.vecOption=new Vector<Option>(1);
		
		for (int i = 0; i < ja.length(); i++) {

			JSONObject jo = (JSONObject) ja.get(i);
			Option option= new Option();
			option.iID=jo.getInt(ID);
			option.stTitle=jo.getString(TITLE);
			option.iSoldQuantity=jo.getInt(SOLDQUANTITY);
			option.isSoldOut=jo.getBoolean(ISSOLDOUT);
			JSONObject joPrice=new JSONObject(jo.getString(PRICE));
			Rate rate=parseRate(joPrice);
			option.price=new Price(rate);
			joPrice=null;rate=null;
			joPrice=new JSONObject(jo.getString(DISCOUNT));
			 rate=parseRate(joPrice);
			option.discount=new Discount(rate);
			joPrice=null;rate=null;
			joPrice=new JSONObject(jo.getString(VALUE));
			rate=parseRate(joPrice);
			option.value=new Value(rate);
			joPrice=null;rate=null;
			option.iDiscountPercent=jo.getInt(DISCOUNTPERCENT);
			option.isLimitedQuantity=jo.getBoolean(ISLIMITEDQUANTITY);
			if(option.isLimitedQuantity){
			option.iInitialQuantity=jo.getInt(INITIALQUANTITY);
			option.iRemainingQuantity=jo.getInt(REMAININGQUANTITY);
			}
			option.iMinimumPurchaseQuantity=jo.getInt(MINIMUMPURCHASEQUANTITY);
			option.iMaximumPurchaseQuantity=jo.getInt(MAXIMUMPURCHASEQUANTITY);
			option.stExpiresAt=jo.getString(EXPIRESAT);
			option.details=parseDetails(jo);
			option.locations=parseRedemptionLocations(jo);
			option.stBuyUrl=jo.getString(BUYURL);
			options.vecOption.add(option);
			//Log.i(GrouponManager.ST_TAG,division.toString());
			
			jo=null;
		}
		ja=null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return options;
	}
	private Rate parseRate(JSONObject joPrice){
		Rate rate= null;
		try{
			rate=new Rate();
			rate.iAmount=joPrice.getInt(AMOUNT);
			rate.stCurrencyCode=joPrice.getString(CURRENCYCODE);
			rate.stFormattedAmount=joPrice.getString(FORMATTEDAMOUNT);
	} catch (JSONException e) {
		e.printStackTrace();
	}
		return rate;
	}
	private RedemptionLocations parseRedemptionLocations(JSONObject jiObject){
		JSONArray ja;
		RedemptionLocations redemptionLocations = null;
		try {
			ja = new JSONArray(jiObject.getString(REDEMPTIONLOCATIONS));
			redemptionLocations = new RedemptionLocations();
			redemptionLocations.vecRedemptionLocation  =new Vector<RedemptionLocation>(1);
		
		for (int i = 0; i < ja.length(); i++) {

			JSONObject jo = (JSONObject) ja.get(i);
			RedemptionLocation location = new RedemptionLocation();
			location.dLat=jo.getDouble(LAT);
			location.dLng=jo.getDouble(LNG);
			location.stName=jo.getString(NAME);
			location.stPhoneNumber=jo.getString(PHONENUMBER);
			location.stStreet=jo.getString(STREETADDRESS1);
			location.stCity=jo.getString(CITY);
			location.stZip=jo.getString(POSTALCODE);
			location.stState=jo.getString(STATE);
			redemptionLocations.vecRedemptionLocation.add(location);
			jo=null;
		}
		ja=null;
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return redemptionLocations;
	}
	private Details parseDetails(JSONObject jiObject){
		JSONArray ja;
		Details details=null;
		try {
			ja = new JSONArray(jiObject.getString(DETAILS));
			details = new Details();
			details.vecDetail  =new Vector<Detail>(1);
		
		for (int i = 0; i < ja.length(); i++) {

			JSONObject jo = (JSONObject) ja.get(i);
			Detail detail = new Detail();
			detail.stDescription=jo.getString(DESCRIPTION);
			
			details.vecDetail.add(detail);
			jo=null;
		}
		ja=null;
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return details;
	}
	private Tags parseTags(JSONObject jiObject){
		JSONArray ja;
		Tags tags=null;
		try {
			ja = new JSONArray(jiObject.getString(TAGS));
			
			int iTagSize=ja.length();
			
		
		for (int i = 0; i < iTagSize; i++) {
				if(tags == null){
					tags = new Tags();
					tags.vecTag  =new Vector<Tag>(iTagSize);
				}
			JSONObject jo = (JSONObject) ja.get(i);
			Tag tag = new Tag();
			tag.stName=jo.getString(NAME);
			tags.vecTag.add(tag);
			jo=null;
		}
		ja=null;
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return tags;
	}
	
}
