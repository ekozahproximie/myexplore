package com.spime.groupon.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.spime.groupon.Deal;
import com.spime.groupon.Deals;
import com.spime.groupon.R;
import com.spime.groupon.Tag;
import com.spime.groupon.TimeDiff;
import com.spime.groupon.Utils;


public class GrouponDealsList extends ListActivity{
	public static Deals deals = null;
	
	private static Map<String, Drawable> drawableMap = new HashMap<String, Drawable>();
	private static Vector<Deal> vecMyDeal=null;
	private String stTags[]=null;
	private boolean isFirstOnFire=true;
public GrouponDealsList() {
	
}
@Override
protected void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	setContentView(R.layout.alldeallist);
	
	setListView();
}
@Override
protected void onStart() {
	
	super.onStart();
}
@Override
protected void onRestart() {
	super.onRestart();
}
@Override
protected void onResume() {
	super.onResume();
}
@Override
protected void onPause() {
	super.onPause();
	if(vecMyDeal != null){
	vecMyDeal.clear();
	vecMyDeal=null;
	}
	
}
@Override
protected void onStop() {
	super.onStop();
}
@Override
protected void onDestroy() {
	super.onDestroy();
}
private void setListView() {
	System.out.println("init time");
    Spinner tagSpinner= (Spinner)findViewById(R.id.spinner1);
    Set<String> set= deals.hMapDealByTag.keySet();
     stTags=new String[set.size()+1];
    int i=0;
    stTags[i++]=getString(R.string.all);
    vecMyDeal = new Vector<Deal>(deals.hMapDealByTag.size());
    for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
		String stTag =  iterator.next();
		stTags[i++]=stTag;
		
		addToList(stTag);
		
	}
   
    tagSpinner.setOnItemSelectedListener(
            new OnItemSelectedListener() {
                public void onItemSelected(
                        AdapterView<?> parent, View view, int position, long id) {
                	if(isFirstOnFire){
                		isFirstOnFire=false;
                		return;
                	}
                	vecMyDeal=deals.hMapDealByTag.get(stTags[position]);
                	if(vecMyDeal== null){
                		Set<String> set=deals.hMapDealByTag.keySet();
                		 vecMyDeal = new Vector<Deal>(deals.hMapDealByTag.size());
                		    for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
                				String stTag =  iterator.next();
                				addToList(stTag);
                				
                			}
                    }
                	setListAdapter();
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //showToast("Spinner2: unselected");
                }
            });
    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String> (this,
    		android.R.layout.simple_spinner_item,stTags);
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    tagSpinner.setAdapter(spinnerArrayAdapter);
    System.out.println("end init time");

    setListAdapter();
	//listView.invalidate();
}
private void setListAdapter(){
	final EfficientAdapter adapter=new EfficientAdapter(this,this);
	getListView().setAdapter(adapter);
	getListView().setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			if(vecMyDeal != null){
			Deal deal = vecMyDeal.get(position);
			sendToDealViewPage(deal);
			}
		}
	});
}
private void addToList(String stTag){
	Vector<Deal> VecTemp =deals.hMapDealByTag.get(stTag);
	for (Deal deal : VecTemp) {
		vecMyDeal.add(deal);
	}
}
private void sendToDealViewPage(Deal deal){
	Intent intentDisDeal=new Intent(this,GrouponSingleDealViewer.class);
	GrouponSingleDealViewer.myDeal=deal;
	startActivity(intentDisDeal);
}

private static  class EfficientAdapter extends BaseAdapter {
	private Context context=null;
		private static class ViewHolder {
			private	TextView tvAnnouncementTitle;
			private	TextView tvtagTitle;
			private	TextView tvExpireday;
			private	ImageView img_view;
			private	String stMediumImageUrl=null;
			public ViewHolder() {

			}
			public void fetchDrawableOnThread(final String urlString) {
				
				if (drawableMap.containsKey(urlString)) {
					img_view.setImageDrawable(drawableMap.get(urlString));
				}
				final Handler handler = new Handler() {
					@Override
					public void handleMessage(Message message) {
						
						img_view.setImageDrawable((Drawable) message.obj);
					}
				};
				Thread thread = new Thread() {
					@Override
					public void run() {
						Drawable drawable = fetchDrawable(urlString);
						Message message = handler.obtainMessage(1, drawable);
						handler.sendMessage(message);
					}
				};
				thread.start();
			}

			public Drawable fetchDrawable(String urlString) {
				if (drawableMap.containsKey(urlString)) {
					return drawableMap.get(urlString);
				}
				Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
				try {
					InputStream is = fetch(urlString);
					Drawable drawable = Drawable.createFromStream(is, "src");
					drawableMap.put(urlString, drawable);
					Log.d(this.getClass().getSimpleName(),
							"got a thumbnail drawable: " + drawable.getBounds()
									+ ", " + drawable.getIntrinsicHeight() + ","
									+ drawable.getIntrinsicWidth() + ", "
									+ drawable.getMinimumHeight() + ","
									+ drawable.getMinimumWidth());
					return drawable;
				} catch (MalformedURLException e) {
					Log.e(this.getClass().getSimpleName(), "fetchDrawable failed",
							e);
					img_view.setBackgroundResource(R.drawable.icon);
					return null;
				} catch (IOException e) {
					Log.e(this.getClass().getSimpleName(), "fetchDrawable failed",
							e);
					img_view.setBackgroundResource(R.drawable.icon);
					return null;
				}catch (NullPointerException e) {
					Log.e(this.getClass().getSimpleName(), "fetchDrawable failed",
							e);
					img_view.setBackgroundResource(R.drawable.icon);
					return null;
				}
			}

			private InputStream fetch(String urlString)
					throws MalformedURLException, IOException {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet(urlString);
				HttpResponse response = httpClient.execute(request);
				return response.getEntity().getContent();
			}
		}

		private LayoutInflater mInflater;
		private ImageLoader imageLoader=null;
		private Activity activity=null;
		public EfficientAdapter(Context context,Activity activity) {
			this.activity=activity;
			mInflater = LayoutInflater.from(context);
			this.context=context;
			imageLoader=new ImageLoader(context);
		}

		public int getCount() {
			if (deals != null && deals.hMapDealByTag != null && 
					vecMyDeal != null) {
				return vecMyDeal.size();
			} else {
				return 0;
			}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				System.out.println("if "+position);
				convertView = mInflater.inflate(R.layout.dealinlist, null);
				holder = new ViewHolder();
				holder.tvAnnouncementTitle = (TextView) convertView
						.findViewById(R.id.announcementTitle);
				holder.tvtagTitle = (TextView) convertView
						.findViewById(R.id.tagTitle);
				holder.img_view = (ImageView) convertView
						.findViewById(R.id.dealimg);
				holder.tvExpireday = (TextView) convertView
				.findViewById(R.id.expireDay);
				convertView.setTag(holder);

			} else {
				System.out.println("else "+position);
				holder = (ViewHolder) convertView.getTag();
				
			}
			//convertView.setBackgroundColor(position %2==0 ?0xFFFFFFFF:0XFFB2B300);
			fillDealInfo(holder, position);
			return convertView;
		}

		private void fillDealInfo(ViewHolder holder, int position) {
			if(deals != null && deals.hMapDealByTag != null && vecMyDeal!=null){
				Deal deal = vecMyDeal.get(position);
				if(deal.tags != null && deal.tags.vecTag != null){
					String stTags="";
					for (Tag tag :deal.tags.vecTag) {
						
						if(!stTags.equals("") ){
							stTags+=" & ";
						}
						stTags+=tag.stName;
						
					}
					holder.tvtagTitle.setText(stTags);
				}
				holder.tvAnnouncementTitle.setText(deal.stAnnouncementTitle);
				
				holder.img_view.setTag(deal.stMediumImageUrl);      
				  imageLoader.DisplayImage(deal.stMediumImageUrl, activity,holder.img_view, 84);      
				
				  //holder.fetchDrawableOnThread(deal.stMediumImageUrl);
				
				TimeDiff diff=Utils.getTimetoLeft(Deals.iMyTimeZoneOffset,
						deal.stEndAt);
				StringBuffer  buffer = new StringBuffer();
				if(diff.iDay > 1 ){
				buffer.append(diff.iDay);
				buffer.append(context.getString(R.string.days));
				buffer.append(" ");
				}else if(diff.iDay == 1 ){
					buffer.append(diff.iDay);
					buffer.append(context.getString(R.string.day));
					buffer.append(" ");
				}
				
				buffer.append(diff.iHours);
				buffer.append(Utils.STR_COLON);
				buffer.append(diff.iMins);
				buffer.append(Utils.STR_COLON);
				buffer.append(diff.iSeconds);
				//System.out.println(deal.stEndAt+" " +buffer.toString());
				holder.tvExpireday.setText(buffer.toString());
			}
			
		}

		
	}
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		deals.hMapDealByTag.clear();
		deals.hMapDealByTag=null;
		vecMyDeal.clear();
		vecMyDeal=null;
		stTags=null;
		drawableMap.clear();
		//drawableMap=null;
		deals=null;
	}
}
