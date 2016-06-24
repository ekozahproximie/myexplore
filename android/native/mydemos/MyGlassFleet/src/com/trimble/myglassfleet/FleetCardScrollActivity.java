package com.trimble.myglassfleet;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.trimble.assetservice.AssetUpdateReceiver;
import com.trimble.vilicus.db.VilicusContentProvider;
import com.trimble.vilicus.entity.Asset;

import java.util.ArrayList;
import java.util.List;

public class FleetCardScrollActivity extends Activity implements
      View.OnClickListener {

   private CardScrollView                   mCardScrollView;
   private ExampleCardScrollAdapter         adapter                   = null;
   private transient VilicusContentProvider vilicusContentProvider    = null;
   private transient boolean                isAssetRecieverRegistered = false;
   private transient AssetUpdateReceiver    assetUpdateReceiver       = null;
   private GestureDetector                  mGestureDetector;
   private AudioManager                     mAudioManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      vilicusContentProvider = VilicusContentProvider.getInstance(this);
      mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

      mGestureDetector = createGestureDetector(this);
      initIcons();
      final ArrayList<Card> mCards = createCards();

      mCardScrollView = new CardScrollView(this);
      mCardScrollView.setHorizontalScrollBarEnabled(true);

      adapter = new ExampleCardScrollAdapter(mCards);
      mCardScrollView.setAdapter(adapter);

      assetUpdateReceiver = new AssetUpdateReceiver(this);
      setContentView(mCardScrollView);
   }

   private GestureDetector createGestureDetector(Context context) {
      final GestureDetector gestureDetector = new GestureDetector(context);
      // Create a base listener for generic gestures
      gestureDetector.setBaseListener(new GestureDetector.BaseListener() {

         @Override
         public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP) {
               // do something on tap
               playSoundEffect(Sounds.TAP);
               // return true;
            } else if (gesture == Gesture.TWO_TAP) {
               // do something on two finger tap

               // return true;
            } else if (gesture == Gesture.SWIPE_RIGHT) {
               // do something on right (forward) swipe
               // return true;
            } else if (gesture == Gesture.SWIPE_LEFT) {
               // do something on left (backwards) swipe
               // return true;
            } else if (gesture == Gesture.SWIPE_DOWN) {
               setResult(RESULT_CANCELED, null);
               playSoundEffect(Sounds.DISMISSED);
               finish();
               return true;
            }
            return false;
         }
      });
      gestureDetector.setFingerListener(new GestureDetector.FingerListener() {

         @Override
         public void onFingerCountChanged(int previousCount, int currentCount) {
            // do something on finger count changes
         }
      });
      gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {

         @Override
         public boolean onScroll(float displacement, float delta, float velocity) {

            // do something on scrolling

            return false;
         }
      });
      return gestureDetector;
   }

   @Override
   public boolean onKeyDown(int keycode, KeyEvent event) {
      if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
         // user tapped touchpad, do something
         return false;
      }

      return super.onKeyDown(keycode, event);
   }

   /*
    * Send generic motion events to the gesture detector
    */
   @Override
   public boolean onGenericMotionEvent(MotionEvent event) {
      if (mGestureDetector != null) {
         return mGestureDetector.onMotionEvent(event);
      }
      return false;
   }

   public void updateNewAsset() {
      final ArrayList<Card> mCards = createCards();
      adapter.setCards(mCards);
   }

   /**
    * Plays a sound effect, overridable for testing.
    */
   protected void playSoundEffect(int soundId) {
      mAudioManager.playSoundEffect(soundId);
   }

   private ArrayList<Card> createCards() {

      Card card = null;

      final List<Asset> assets = vilicusContentProvider.getAllAssets();
      final ArrayList<Card> mCards = new ArrayList<Card>(assets.size());
      int id = 0;
      for (Asset asset : assets) {
         card = new Card(this);
         View cardView = card.getView();
         // To receive touch events from the touchpad, the view should be
// focusable.
         cardView.setOnClickListener(this);
         cardView.setFocusable(true);
         cardView.setFocusableInTouchMode(true);
         cardView.setId(id);

         card.setText(asset.getName());
         card.setFootnote(asset.getStatus());
         card.setImageLayout(Card.ImageLayout.FULL);
         // final Intent menuIntent = new Intent(this, FleetMenuActivity.class);
         // card.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
         // card.publish(LiveCard.PublishMode.REVEAL); // or SILENT
         card.addImage(getAssetIcon(asset, this));
         mCards.add(card);
      }
      return mCards;
   }

   @Override
   public void onClick(View v) {

   }

   private class ExampleCardScrollAdapter extends CardScrollAdapter {

      private List<Card> mCards;

      public ExampleCardScrollAdapter(final List<Card> mCards) {
         this.mCards = mCards;
      }

      @Override
      public int getPosition(Object item) {
         return mCards.indexOf(item);
      }

      @Override
      public int getCount() {
         return mCards.size();
      }

      @Override
      public Object getItem(int position) {
         return mCards.get(position);
      }

      /**
       * Returns the amount of view types.
       */
      @Override
      public int getViewTypeCount() {
         return Card.getViewTypeCount();
      }

      /**
       * Returns the view type of this card so the system can figure out if it
       * can be recycled.
       */
      @Override
      public int getItemViewType(int position) {
         return mCards.get(position).getItemViewType();
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
         return mCards.get(position).getView(convertView, parent);
      }

      public void setCards(List<Card> mCards) {
         this.mCards = mCards;
         notifyDataSetChanged();
      }
   }

   @Override
   protected void onResume() {

      super.onResume();
      if (isAssetRecieverRegistered == false) {
         registerReceiver(assetUpdateReceiver, null);
         isAssetRecieverRegistered = true;
      }
      mCardScrollView.activate();
   }

   @Override
   protected void onPause() {
      super.onPause();
      if (isAssetRecieverRegistered == true) {
         unregisterReceiver(assetUpdateReceiver);
         isAssetRecieverRegistered = false;
      }
      mCardScrollView.deactivate();
   }

   private transient Drawable assetTractorIcon;

   private transient Drawable assetFloatorIcon;

   private transient Drawable assetCombineIcon;

   private transient Drawable assetSugarcaneHarvestorIcon;

   private transient Drawable assetLargeRowCropIcon;

   private transient Drawable assetMediumRowCropIcon;

   private transient Drawable assetSmallRowCropIcon;

   private transient Drawable assetGenericIcon;

   private void initIcons() {
      assetTractorIcon = getResources().getDrawable(R.drawable.blue_tractor);
      assetFloatorIcon = getResources().getDrawable(R.drawable.blue_floater);
      assetGenericIcon = getResources().getDrawable(R.drawable.blue_tractor);
      assetCombineIcon = getResources().getDrawable(R.drawable.blue_combine);
      assetLargeRowCropIcon = getResources().getDrawable(
            R.drawable.blue_tractor);
      assetMediumRowCropIcon = getResources().getDrawable(
            R.drawable.blue_tractor);
      assetSmallRowCropIcon = getResources().getDrawable(
            R.drawable.blue_tractor);
      assetSugarcaneHarvestorIcon = getResources().getDrawable(
            R.drawable.blue_tractor);
   }

   private Drawable getAssetIcon(final Asset asset, final Context context) {

      if (context == null) {
         return null;
      }
      Drawable icon = context.getResources().getDrawable(
            R.drawable.blue_tractor);

      if (asset != null) {
         if (asset.getType().equals(
               context.getResources().getString(R.string.vehicle_tractor))) {
            icon = assetTractorIcon;
         } else if (asset.getType().equals(
               context.getResources().getString(R.string.vehicle_generic))) {
            icon = assetGenericIcon;
         } else if (asset.getType().equals(
               context.getResources().getString(R.string.vehicle_floater))) {
            icon = assetFloatorIcon;
         } else if (asset.getType().equals(
               context.getResources().getString(R.string.vehicle_combine))) {
            icon = assetCombineIcon;
         } else if (asset.getType().equals(
               context.getResources()
                     .getString(R.string.vehicle_large_row_crop))) {
            icon = assetLargeRowCropIcon;
         } else if (asset.getType().equals(
               context.getResources().getString(
                     R.string.vehicle_medium_row_crop))) {
            icon = assetMediumRowCropIcon;
         } else if (asset.getType().equals(
               context.getResources().getString(
                     R.string.vehicle_smalll_row_crop))) {
            icon = assetSmallRowCropIcon;
         } else if (asset.getType().equals(
               context.getResources().getString(
                     R.string.vehicle_sugarcane_harvestor))) {
            icon = assetSugarcaneHarvestorIcon;
         } else {
            icon = assetTractorIcon;
         }
      }

      return icon;
   }
}
