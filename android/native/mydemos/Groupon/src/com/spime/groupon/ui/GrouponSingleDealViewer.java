package com.spime.groupon.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.spime.groupon.Deal;
import com.spime.groupon.Deals;
import com.spime.groupon.Option;
import com.spime.groupon.R;
import com.spime.groupon.TimeDiff;
import com.spime.groupon.Utils;

public class GrouponSingleDealViewer extends Activity implements ViewFactory{
	public static Deal myDeal =null; 
	private TextSwitcher mSwitcher;
	private ExpireCounter expireCounter;
	public class ExpireCounter extends CountDownTimer{

		public ExpireCounter(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mSwitcher.setText("done!");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mSwitcher.setText(timeCalculate(millisUntilFinished));

		}

		}


@Override
protected void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	setContentView(R.layout.singledealview);
	TextView tvMainTitle=(TextView)findViewById(R.id.maintxt);
	final ImageView imgMain=(ImageView)findViewById(R.id.mainimg);
	TextView tvValue=(TextView)findViewById(R.id.value);
	TextView tvDiscount=(TextView)findViewById(R.id.discount);
	TextView tvPrice=(TextView)findViewById(R.id.price);
	TextView tvHtml=(TextView)findViewById(R.id.htmltxt);
	tvMainTitle.setText(myDeal.stTitle);
	mSwitcher=(TextSwitcher)findViewById(R.id.timetoleft);
		mSwitcher.setFactory(this);
		Animation in = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in);
		Animation out = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out);
		mSwitcher.setInAnimation(in);
		mSwitcher.setOutAnimation(out);        
		TimeDiff diff=Utils.getTimetoLeft(Deals.iMyTimeZoneOffset,
				myDeal.stEndAt);
	expireCounter = new ExpireCounter(diff.lMyTime, 1100);
	expireCounter.start();
	Option option= myDeal.options.vecOption.elementAt(0);
	TextView tvBuy=(TextView)findViewById(R.id.buytxt);
	tvBuy.setMovementMethod(LinkMovementMethod.getInstance());
	String text = "<a href='"+option.stBuyUrl+"'>"+getString(R.string.buy)+"</a>";
	tvBuy.setText(Html.fromHtml(text)); 
	
	tvValue.setText(option.value.stFormattedAmount);
	tvPrice.setText(option.price.stFormattedAmount);
	tvDiscount.setText(option.discount.stFormattedAmount);
	tvHtml.setText(Html.fromHtml(myDeal.stHighlightsHtml));
	RelativeLayout toBeTippedBar=(RelativeLayout)findViewById(R.id.notTippedBar);
	RelativeLayout tippedBar=(RelativeLayout)findViewById(R.id.TippedBar);
	if(myDeal.isTipped == false){
		toBeTippedBar.setVisibility(View.VISIBLE);
		ProgressBar pg=(ProgressBar)findViewById(R.id.progress);
		final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
			ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null,
					null));
			String MyColor = "#FF00FF";
			pgDrawable.getPaint().setColor(Color.parseColor(MyColor));
			ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT,
					ClipDrawable.HORIZONTAL);
			pg.setProgressDrawable(progress);
			pg.setBackgroundDrawable(getResources().getDrawable(
					android.R.drawable.progress_horizontal));
			pg.setMax(myDeal.iTippingPoint);
			pg.setProgress(myDeal.iSoldQuantity);
			String stSpace=" ";
			TextView tvSoldQuantity=(TextView)findViewById(R.id.soldQuantity);
			String stBougt=stSpace+getString(R.string.bought);
			if(option.isLimitedQuantity){
				stBougt +=stSpace+getString(R.string.limit);
			}
			tvSoldQuantity.setText(myDeal.iSoldQuantity+stBougt);
			TextView tvTippingPointHigh=(TextView)findViewById(R.id.tippingPointHigh);
			tvTippingPointHigh.setText(myDeal.iTippingPoint+"");
			TextView tvToBeSold=(TextView)findViewById(R.id.tobedealtxt);
			tvToBeSold.setText(myDeal.iTippingPoint - myDeal.iSoldQuantity+stSpace+(getString(R.string.to_deal)));
			
			
	}else{
		tippedBar.setVisibility(View.VISIBLE);
		TextView tvSoldQuantity=(TextView)findViewById(R.id.TipsoldQuantity);
		String stSpace=" ";
		String stBougt=stSpace+getString(R.string.bought);
		if(option.isLimitedQuantity){
			stBougt +=stSpace+getString(R.string.limit);
		}
		tvSoldQuantity.setText(myDeal.iSoldQuantity+stBougt);
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
         //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date tappedTime = null;

			try {
				tappedTime = formatter.parse(myDeal.stTippedAt);
				TextView tvTippedTimeInfo=(TextView)findViewById(R.id.tippedTimeInfo);
				StringBuffer buffer = new StringBuffer();
				String stAM=tappedTime.getHours()/12 > 0 ? "PM":"AM";
				buffer.append(getString(R.string.tipped_at));
				buffer.append(stSpace);
				buffer.append(tappedTime.getHours()%12 == 0?tappedTime.getHours():tappedTime.getHours()%12);
				buffer.append(":");
				buffer.append(tappedTime.getMinutes());
				buffer.append(stAM);
				buffer.append(stSpace);
				buffer.append(getString(R.string.with));
				buffer.append(stSpace);
				buffer.append(myDeal.iTippingPoint);
				buffer.append(stSpace);
				buffer.append(getString(R.string.bought));
				
				tvTippedTimeInfo.setText(Utils.getTippedTimeZoneValue(Deals.iMyTimeZoneOffset,tappedTime.getHours(),tappedTime.getMinutes() ));
				
				buffer=null;
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
	}
	
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			
			imgMain.setImageDrawable((Drawable) message.obj);
		}
	};
	Thread thread = new Thread() {
		@Override
		public void run() {
			Drawable drawable = null;
			try {
				drawable = fetch(myDeal.stLargeImageUrl);
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			Message message = handler.obtainMessage(1, drawable);
			handler.sendMessage(message);
		}
	};
	thread.start();
}
private Drawable fetch(String urlString)
throws MalformedURLException, IOException {
DefaultHttpClient httpClient = new DefaultHttpClient();
HttpGet request = new HttpGet(urlString);
HttpResponse response = httpClient.execute(request);

Drawable drawable = Drawable.createFromStream(response.getEntity().getContent(), "src");
return drawable;
}
@Override
protected void onResume() {
	super.onResume();
	if(expireCounter != null){
		expireCounter.start();
	}
}
protected void onPause(){
	super.onPause();
	if(expireCounter != null){
		expireCounter.cancel();
	}
}
@Override
public View makeView() {
	
		TextView t = new TextView(this);
		t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		t.setTextSize(36);
		t.setTextColor(Color.parseColor("#494949"));
		return t;
	}

	public String timeCalculate(long ttime) {
		final long timeInMillis = ttime;

        final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));

        int remdr = (int) (timeInMillis % (24L * 60 * 60 * 1000));

        final int hours = remdr / (60 * 60 * 1000);

        remdr %= 60 * 60 * 1000;

        final int minutes = remdr / (60 * 1000);

        remdr %= 60 * 1000;

        final int seconds = remdr / 1000;
        String daysT="";
        String restT="";
        final int ms = remdr % 1000;
		if (days == 1)
			daysT = String.format("%d day ", days);
		if (days > 1)
			daysT = String.format("%d days ", days);
		restT = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		return daysT + restT;
	}
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		myDeal=null;
		
	}
}
