package com.spime.groupon.ui;

import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.spime.groupon.Deals;
import com.spime.groupon.Division;
import com.spime.groupon.Divisions;
import com.spime.groupon.GrouponManager;
import com.spime.groupon.R;

public class GrouponCityList extends ListActivity implements
		ListView.OnScrollListener {
	private RemoveWindow mRemoveWindow = new RemoveWindow();
	private Handler mHandler = new Handler();
	private WindowManager mWindowManager;
	private TextView mDialogText;
	private boolean mShowing;
	private boolean mReady;
	private ViewGroup mContainer;
	private char mPrevLetter = Character.MIN_VALUE;
	private String mStrings[] = null;
	private ProgressDialog progressDialog = null;
	EfficientAdapter adapter = null;
	public Dialog errorDialog = null;
	private Vector<Short> vecDiviId=null;
	public static Vector<Division> vecDivision = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		mContainer = this.getListView();
		setContentView(R.layout.allcitylist);
		EditText userText=(EditText)findViewById(R.id.userText);
		userText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				String stSearch =s.toString().trim();
				//if(stSearch.equals(Utils.EM_ST))
				{
					doCitySearch(stSearch);
					}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
		sendDivisionRequest();
		getListView().setOnScrollListener(this);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				position=vecDiviId.get(position);
				Division division = vecDivision.get(position);
				sendDealsRequest(division);
			}
		});
		getListView().setClickable(true);
		LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mDialogText = (TextView) inflate.inflate(R.layout.list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);

		mHandler.post(new Runnable() {

			public void run() {
				mReady = true;
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_APPLICATION,
						WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
								| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);
				mWindowManager.addView(mDialogText, lp);
			}
		});
		// Since we are caching large views, we want to keep their cache
		// between each animation
		mContainer
				.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
	}

	private void doCitySearch(String stSearch) {
		stSearch=stSearch.trim().toLowerCase();
		if(vecDivision != null&& !stSearch.equals("")){
			 Vector<String> vecSeCity=new Vector<String>();
			 vecDiviId.clear();
			 vecDiviId=null;
			 vecDiviId= new Vector<Short>();
			short id=0;
			 for (Division division : vecDivision) {
			
				if(division.stName.toLowerCase().contains(stSearch)
			 || division.stName.toLowerCase().startsWith(stSearch)
			 ){
					vecDiviId.add(id);
					vecSeCity.add(division.stName);
			 }
				
				id++;
			}
			 mStrings=null;
			mStrings= new String[vecSeCity.size()];
			id=0;
			for (String stSearCity : vecSeCity) {
				mStrings[id++]=stSearCity;
			}
			setDisplayAdapter();
		}else if (vecDivision != null && stSearch.equals("")){
			displayList();
		}
		
	}

//	private void applyRotation(int position, float start, float end) {
//		// Find the center of the container
//		final float centerX = mContainer.getWidth() / 2.0f;
//		final float centerY = mContainer.getHeight() / 2.0f;
//
//		// Create a new 3D rotation with the supplied parameter
//		// The animation listener is used to trigger the next animation
//		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
//				centerX, centerY, 310.0f, true);
//		rotation.setDuration(500);
//		rotation.setFillAfter(true);
//		rotation.setInterpolator(new AccelerateInterpolator());
//		mContainer.startAnimation(rotation);
//	}

	private void sendDivisionRequest() {
		if (vecDivision == null) {
			showProcessDialog("City List");
			GrouponManager gManager = GrouponManager.getInstance();
			gManager.sendRequest(GrouponManager.DIVISIONS_URL,
					grouponResponseHandler, GrouponManager.DIVISIONS);
		}
	}

	private void sendDealsRequest(Division division) {

		showProcessDialog("All Deal List for " + division.stName);
		GrouponManager gManager = GrouponManager.getInstance();
		StringBuffer buffer = new StringBuffer();
		buffer.append(GrouponManager.DEALS_URL);
		buffer.append(GrouponManager.JSON);
		buffer.append("?");
		buffer.append(GrouponManager.DIVISION_ID);
		buffer.append(division.stID);
		buffer.append("&");
		buffer.append(GrouponManager.CLIENT_ID);
		buffer.append(GrouponManager.SHOW_ALL);
		gManager.sendRequest(buffer.toString(), grouponResponseHandler,
				GrouponManager.DEALS);
		Deals.iMyTimeZoneOffset=
			division.iTimezoneOffsetInSeconds;
		buffer = null;

	}

	private void errorDialog(String stErrorMsg) {

		errorDialog = new AlertDialog.Builder(this).setMessage(stErrorMsg)
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						errorDialog.cancel();
					}
				}).create();
		errorDialog.show();
	}

	private void showProcessDialog(String stTitle) {
		progressDialog = null;
		progressDialog = ProgressDialog.show(this, stTitle,
				"Please wait for Server Response...", true);

	}

	private void cancelProcessDialog() {
		if (progressDialog != null) {
			progressDialog.cancel();
		}
		if (errorDialog != null) {
			errorDialog.cancel();
		}
	}

	private void goToDealsListActivity(Deals deals) {
		Intent intentDeals = new Intent(this, GrouponDealsList.class);
		GrouponDealsList.deals = deals;
		startActivityForResult(intentDeals, 1);
		// applyRotation(0, 0, 90);
	}

	private Handler grouponResponseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			cancelProcessDialog();
			switch (msg.what) {
			case GrouponManager.DIVISIONS:
				if (msg.obj instanceof Divisions) {
					Divisions divisions = (Divisions) msg.obj;
					if (divisions != null) {
						vecDivision = divisions.vecDivisions;
						displayList();
					}
				} else {
					errorDialog((String) msg.obj);
				}
				break;
			case GrouponManager.DEALS:
				if (msg.obj instanceof Deals) {
					Deals deals = (Deals) msg.obj;
					if (deals != null && deals.hMapDealByTag != null) {

						goToDealsListActivity(deals);
					}
				} else {
					errorDialog((String) msg.obj);
				}
				break;
			default:
				break;
			}
		}
	};

	private void displayList() {
		if (vecDivision != null ) {
			mStrings=null;
			mStrings = new String[vecDivision.size()];
			vecDiviId=null;
			vecDiviId= new Vector<Short>(vecDivision.size());
			short i = 0;
			for (Division division : vecDivision) {
				vecDiviId.add(i);
				mStrings[i++] = division.stName;
				
			}	
			setDisplayAdapter();
		}
		
	}
private void setDisplayAdapter(){
	
	adapter = new EfficientAdapter(this, mStrings);
	setListAdapter(adapter);
	
	adapter.notifyDataSetChanged();
	getListView().invalidate();
	cancelProcessDialog();
}
	@Override
	protected void onResume() {
		super.onResume();
		displayList();
		mReady = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeWindow();

		mReady = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWindowManager.removeView(mDialogText);
		mReady = false;
		finish();
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		//int lastItem = firstVisibleItem + visibleItemCount - 1;
		try {
			if (mReady && mStrings != null &&mStrings.length >
			firstVisibleItem) {
				String stLeter=mStrings[firstVisibleItem];
				if(stLeter != null && stLeter.length() > 0 ){
				char firstLetter = stLeter.charAt(0);

				if (!mShowing && firstLetter != mPrevLetter) {

					mShowing = true;
					mDialogText.setVisibility(View.VISIBLE);

				}
				mDialogText.setText(((Character) firstLetter).toString());
				mHandler.removeCallbacks(mRemoveWindow);
				mHandler.postDelayed(mRemoveWindow, 3000);
				mPrevLetter = firstLetter;
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println(mStrings[firstVisibleItem]);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	private final class RemoveWindow implements Runnable {
		public void run() {
			removeWindow();
		}
	}

	private void removeWindow() {
		if (mShowing) {
			mShowing = false;
			mDialogText.setVisibility(View.INVISIBLE);
		}
	}

	private static class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private String stDATA[] = null;

		public EfficientAdapter(Context context, String stDATA[]) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.stDATA = stDATA;

		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return stDATA.length;
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return position;
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.singlecityname, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.tvCityName = (TextView) convertView
						.findViewById(R.id.tvCityName);
			
				
				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.

				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			holder.tvCityName.setText(stDATA[position]);
			
			convertView.setBackgroundColor(position %2==0 ?0xFFFFFFFF:0XFFE0E0E0); 
			return convertView;
		}

		private static class ViewHolder {
			private TextView tvCityName;

		}
	}
}
