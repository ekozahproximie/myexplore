package com.spime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	public static String stCookie = "JSESSIONID=C1BF1E633EB9F70E49A3070DE74E39BC.d701; __utmc=247637761; __utma=247637761.543768415.1279556484.1279556484.1279556484.1; __utmb=247637761.3.10.1279556484; __utmz=247637761.1279556485.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utma=21639229.1017410694.1279556527.1279556527.1279556527.1; __utmb=21639229.3.10.1279556527; __utmc=21639229; __utmz=21639229.1279556527.1.1.utmcsr=wwwd.way2sms.com|utmccn=(referral)|utmcmd=referral|utmcct=/content/index.html";
	public static String standCookie = ";__utmc=247637761; __utma=247637761.543768415.1279556484.1279556484.1279556484.1; __utmb=247637761.3.10.1279556484; __utmz=247637761.1279556485.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utma=21639229.1017410694.1279556527.1279556527.1279556527.1; __utmb=21639229.3.10.1279556527; __utmc=21639229; __utmz=21639229.1279556527.1.1.utmcsr=wwwd.way2sms.com|utmccn=(referral)|utmcmd=referral|utmcct=/content/index.html";
	public static String ST_ERROR_MSG = null;
	public static String ST_LOGIN_URL = null;

	public static  HttpURLConnection getURL(final String stURL,
			final String stMethod, final String stContent,
			final String stReferer, final String stCookie,
			final boolean islogout) {

		HttpURLConnection uc = null;
		try {
			final URL u = new URL(stURL);
			uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc
					.setRequestProperty(
							"User-Agent",
							"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB6.5; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C)");
			uc.setRequestProperty("Content-Length", String.valueOf(stContent
					.length()));

			if (islogout) {
				uc.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded; charset=UTF-8");
				uc.setRequestProperty("x-requested-with", "XMLHttpRequest");
			} else {
				uc.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
			}

			uc
					.setRequestProperty("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			uc.setRequestProperty("Keep-Alive", "115");
			uc.setRequestProperty("Cache-Control", "no-cache");
			uc.setRequestProperty("Connection", "keep-alive");
			uc.setRequestProperty("Referer", stReferer);
			if (stCookie != null) {
				uc.setRequestProperty("Cookie", stCookie);
			}
			uc.setRequestMethod(stMethod);

			uc.setInstanceFollowRedirects(false);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(uc
					.getOutputStream()), true);
			pw.print(stContent);
			pw.flush();
			pw.close();

			return uc;
		} catch (MalformedURLException ex) {
			ST_ERROR_MSG = ex.getMessage();
			Log.e("Error", ex.getMessage(), ex);
		} catch (IOException ex) {
			ST_ERROR_MSG = "No server connection";
			Log.e("Error", ex.getMessage(), ex);
		} catch (Throwable ex) {
			ST_ERROR_MSG = ex.getMessage();
			Log.e("Error", ex.getMessage(), ex);
		}
		return uc;
	}

	public static boolean login(String stUserID, String stPasword,
			Context context) {
		HttpURLConnection uc=null;
		try {
			Log.v("Utils","Enter login ");
			String content = "username=" + stUserID + "&password=" + stPasword
					+ "&login=Login";
			String stURL = context.getString(R.string.login_url);
			String stReferURL = context.getString(R.string.login_refererurl);
			 uc = Utils.getURL(stURL, "POST", content,
					stReferURL, null, false);
			 
			if (Utils.ST_ERROR_MSG != null) {
				System.out.println(ST_ERROR_MSG);
				return false;
			}
			
			
			String cookie = uc.getHeaderField("Set-Cookie");
			String location = uc.getHeaderField("location");
			Utils.ST_LOGIN_URL = location;
			Utils.stCookie = cookie + Utils.standCookie;
			 
			int iResponseCode = uc.getResponseCode();
			 
			if (iResponseCode != Integer.parseInt(context
					.getString(R.string.login_redirectcode))) {
				Utils.ST_ERROR_MSG=context.getString(R.string.login_redirectcodeFail);
				return false;
			}
			if (location.contains(context.getString(R.string.login_errorurl))) {
				Utils.ST_ERROR_MSG=context.getString(R.string.login_waring);
				return false;
			}
			Log.v("Utils","Authentication done");
			uc=null;
			uc = Utils.getURL(location, "GET", content, stReferURL,
					Utils.stCookie, false);
			
			iResponseCode = uc.getResponseCode();
			if (iResponseCode != Integer.parseInt(context
					.getString(R.string.login_sucesscode))) {
				Utils.ST_ERROR_MSG=context.getString(R.string.login_sucesscodeFail);
				return false;
			}
			
			Utils.ST_ERROR_MSG=context.getString(R.string.login_loginSucess);
		
			Log.v("Utils","Exist login ");
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block

			Utils.ST_ERROR_MSG = e.getMessage();
			Log.v("Utils Error", e.getMessage());
			return false;
		}finally{
			uc=null;
		}
	}

	public static void newUser(String stUserName, int day, int month, int year,
			String stMail, String stPhoneno, Context context) {
		HttpURLConnection uc=null;
		BufferedReader br =null;
		try {
			Log.v("Utils","Enter newUser ");
			
			String content = "";

			String stURL = context.getString(R.string.createuser_url);
			String stReferURL = "";
			 uc = Utils.getURL(stURL, "GET", content,
					stReferURL, null, false);
			if (Utils.ST_ERROR_MSG != null) {
				return;
			}

			String temp = null;
			String stValidationText = null;
			 br = new BufferedReader(new InputStreamReader(uc
					.getInputStream()));
			while ((temp = br.readLine()) != null) {
				if (temp.contains("validateForm('")) {
					stValidationText = temp.substring(
							temp.lastIndexOf("'") - 4, temp.lastIndexOf("'"));
				}
			}
			br.close();
			

			int iResponseCode = uc.getResponseCode();
			if (iResponseCode != Integer.parseInt(context
					.getString(R.string.login_sucesscode))) {
				Utils.ST_ERROR_MSG =context.getString(R.string.login_redirectcodeFail);
				return;
			}
			String cookie = uc.getHeaderField("Set-Cookie");
			Utils.stCookie = cookie + Utils.standCookie;

			content = "HiddenAction=UserInsert" + "&mrCombo=Mr"
					+ "&tfUserName=" + stUserName + "&birth_Day=" + day
					+ "&birth_Month=" + month + "&birth_Year=" + year
					+ "&tfUserID=" + stMail + "&professCombo=1"
					+ "&cityCombo=84" + "&income="
					+ URLEncoder.encode("Below 10,000", "UTF-8")
					+ "&tfMobileNum=" + stPhoneno + "&tfMobileNum1="
					+ stPhoneno + "&textcode=" + stValidationText
					+ "&chkdell=checkbox" + "&checkaccept=checkbox";
			stURL = context.getString(R.string.forget_subcriber_main_url);
			stReferURL = context.getString(R.string.subcri_refererurl);
			// content =URLEncoder.encode(content, "UTF-8");
			uc = Utils.getURL(stURL, "POST", content, stReferURL,
					Utils.stCookie, false);
			if (Utils.ST_ERROR_MSG != null) {
				return;
			}
			iResponseCode = uc.getResponseCode();
			if (iResponseCode != Integer.parseInt(context
					.getString(R.string.login_redirectcode))) {
				Utils.ST_ERROR_MSG =context.getString(R.string.login_redirectcodeFail);
				return;
			}

			
			Utils.ST_ERROR_MSG =context.getString(R.string.sms_verification);
			Log.v("Utils","Exist newUser ");			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			uc = null;
			br =null;
		}
	}

	public static void forgetPaaword(String stPhoneNo, Context context) {
		HttpURLConnection uc=null;
		try {
			Log.v("Utils","Enter forgetPaaword ");
			
			String content = "";
			String stURL = context.getString(R.string.forget_url);
			String stReferURL = context.getString(R.string.forget_refererurl);
			 uc = Utils.getURL(stURL, "GET", content,
					stReferURL, Utils.stCookie, false);
			if (Utils.ST_ERROR_MSG != null) {
				return;
			}

			String temp = null;
			String stValidationText = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(uc
					.getInputStream()));
			while ((temp = br.readLine()) != null) {
				if (temp.contains("validateForm('")) {
					stValidationText = temp.substring(
							temp.lastIndexOf("'") - 4, temp.lastIndexOf("'"));
				}
			}
			br.close();
			br = null;
			uc = null;
			content = "HiddenAction=ForgotPass" + "&tfEmailId="
					+ "&tfMobileNo=" + stPhoneNo + "&textcode="
					+ stValidationText;

			stURL = context.getString(R.string.forget_subcriber_main_url);
			uc = Utils.getURL(stURL, "GET", content, stReferURL,
					Utils.stCookie, false);
			
			int iResponseCode = uc.getResponseCode();
			if (iResponseCode != Integer.parseInt(context
					.getString(R.string.login_redirectcode))) {
				Utils.ST_ERROR_MSG=context.getString(R.string.login_sucesscodeFail);
				return;
			}

			Utils.ST_ERROR_MSG=context.getString(R.string.forgetpassword_msg);
			Log.v("Utils","Exist forgetPaaword ");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Utils.ST_ERROR_MSG=e.getMessage();
		}finally{
			uc = null;
		}
	}

	public static void sendSMS(String stTOMobileNo, String stMsg,
			Context context) {
		HttpURLConnection uc =null;
		try {
			Log.v("Utils","Enter sendSMS ");
			
		String content = "custid=undefined" + "&HiddenAction=instantsms"
				+ "&Action=asdvga54234f" + "&login=" + "&pass=" + "&MobNo="
				+ stTOMobileNo + "&textArea=" + stMsg;
		String stURL = context.getString(R.string.sendsms_url);
		String stReferURL = context.getString(R.string.sendsms_refererurl);
		
		 uc = Utils.getURL(stURL, "POST", content, stReferURL,
				Utils.stCookie, false);
		if (Utils.ST_ERROR_MSG != null) {
			return;
		}
		int iResponseCode = 0;
	
			iResponseCode = uc.getResponseCode();
		
		if (iResponseCode != Integer.parseInt(context
				.getString(R.string.login_sucesscode))) {
			Utils.ST_ERROR_MSG=context.getString(R.string.sendsms_msgerror);
			return;
		}
		
		Utils.ST_ERROR_MSG=null;
		Log.v("Utils","Exist sendSMS ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.ST_ERROR_MSG=e.getMessage();
			return;
		}finally{
			uc = null;
		}
		
	}

	public static void logout(Context context) {
		String stContent = null;
		HttpURLConnection uc= null;
		try {
			Log.v("Utils","Enter logout ");
		try {
			
			stContent = "folder=" + URLEncoder.encode("inbox", "UTF-8");
		} catch (UnsupportedEncodingException e) {
		//	 TODO Auto-generated catch block
			Log.v("error", e.getMessage());
		}
		
		
		String stURL = context.getString(R.string.logout_url);
		String stReferURL = context.getString(R.string.logout_refererurl);
		
		 uc = Utils.getURL(stURL, "POST", stContent,
				stReferURL, stCookie, true);
		if (Utils.ST_ERROR_MSG != null) {
			return;
		}
		int iResponseCode = 0;
	
			iResponseCode = uc.getResponseCode();
		
		if (iResponseCode != Integer
				.parseInt(context.getString(R.string.login_sucesscode))) {
			
			Utils.ST_ERROR_MSG=context.getString(R.string.logout_fail);
			return;
		}
		
		uc = null;
		Utils.ST_ERROR_MSG=context.getString( R.string.logout_sucess);
		Log.v("Utils","Exist logout ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.ST_ERROR_MSG=e.getMessage();
			
			return;
		}finally{
			uc=null;
		}
	}
	
	public static boolean isPhoneNumberValid(String stUserID, Context context) {
		if (stUserID == null || stUserID.equals("")) {
			Toast.makeText(context, R.string.login_invalidPhoneno,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (stUserID.charAt(0) != '9' && stUserID.charAt(0) != '8'
				&& stUserID.charAt(0) != '7') {
			Toast.makeText(context, R.string.login_invalidPhoneno,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (stUserID.length() != 10) {
			Toast.makeText(context, R.string.login_invalidPhoneno,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
}
