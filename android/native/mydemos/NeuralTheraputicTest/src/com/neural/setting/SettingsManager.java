package com.neural.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.neural.sensor.NtDeviceManagement;

public final class SettingsManager {

	/**
    * 
    */
   private static final int DEAULT_TIME_BASE = 10;

   private static final String GRAPH_STYLE="graph_style";
	
	private static final String GRID_DISPLAY="grid";
	
	private static final String TIME_BASE="time_base";
	
	private static final String MUTE_ON="time_on";
	
	private static final String VIDEO_ZOOM="video_zoom";
	
	private static final String EMG_SCALE="emg_scale";
	
	private static final String EMG_OFFSET="emg_offset";
	
	private static final String TRAGET_MODE="traget_mode";
	
	private static final String CURRENT_TRAGET_VALUE="current_traget_value";
	
	private static final String BOUNDARY_MODE="boundary_mode";
	
	private static final String BOUNDARY_MODE_RANGE="boundary_mode_range";
	
	private static final String DISPLAY_MODE="display_mode";
	
	private static final String GRID_COLOR="grid_color";
	
	private static final String BASE_COLOR="base_color";
	
	private static final String PRE_DRAWN_MODE="pre_drawn_mode";
	
	private static final String DEMO_MODE="demo_mode";
	
	public static final int BAR_STYLE=1;
	
	public static final int LINEAR_STYLE=2;
	
	
	public static final int VIDEO_VIEW			= 1;
	
	public static final int GRAPH_VIEW			= 2;
	
	public static final int VIDEO_GRAPH_VIEW 	= 3;
	
	
	public static final int D_GRID_COLOR 		= 0xFFAAAAAA;//Color.argb(192, 255, 255, 64);
	
	public static final int D_BASE_COLOR 		= Color.argb(192, 255, 255, 64);
	
	public static final int MAX_TIME_SCALE=25;
	
	public static final int MIN_TIME_SCALE=5;
	
	public static final int MAX_EMG_OFF_SCALE=25;
        
	public static final int DEFAULT_EMG_OFF_SCALE=MAX_EMG_OFF_SCALE;
	
        public static final int MIN_EMG_OFF_SCALE=1;
        
        
        public static final int MIN_CONSTANT_VALUE=40;
        
        public static final float MAX_CONSTANT_VALUE=360;
	
	private static SettingsManager manager =null;
	private SettingsManager(){
		if(manager != null){
			 throw new IllegalAccessError("use getInstance method");
		}
	}
	
	public static SettingsManager getInstance(){
		if(manager == null){
			manager = new SettingsManager();
		}
		return manager;
	}
	
	public void storeGraphDisplayStyle(final int iGraphStyle,final Context context){
		if(context == null){
			return;
		}
		if(iGraphStyle > LINEAR_STYLE || iGraphStyle < BAR_STYLE){
			return;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(GRAPH_STYLE, iGraphStyle); 
		editor.commit();
	}
	
	public int getGraphStyle(final Context context){
		if(context == null){
			return BAR_STYLE;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int iGraphStyle=preferences.getInt(GRAPH_STYLE,BAR_STYLE);
		
		return iGraphStyle;
		
	}
	
	public void storeGridDisplay(final boolean gridDisplay,final Context context){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putBoolean(GRID_DISPLAY, gridDisplay); 
		editor.commit();
	}
	
	public boolean getGridDisplay(final Context context){
		if(context == null){
			return false;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final boolean gridDisplay=preferences.getBoolean(GRID_DISPLAY, false);
		
		return gridDisplay;
		
	}
	
	public void storeTimeBase(final int timebase,final Context context){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(TIME_BASE, timebase); 
		editor.commit();
		final NtDeviceManagement deviceManagement =NtDeviceManagement.getDefaultDeviceManager(context);
		deviceManagement.setTimeBase(timebase);
	}
	
	public double getTimeBase(final Context context){
		if(context == null){
			return DEAULT_TIME_BASE;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int timeBase=preferences.getInt(TIME_BASE, DEAULT_TIME_BASE);
		return timeBase;
		
	}
	
	public void storeMute(final boolean muteOn,final Context context){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putBoolean(MUTE_ON, muteOn); 
		editor.commit();
	}
	
	public boolean getMuteOn(final Context context){
		if(context == null){
			return false;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final boolean muteON=preferences.getBoolean(MUTE_ON, false);
		return muteON;
		
	}
	
	public void storeVideoZoom(final int videoZoom,final Context context){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(VIDEO_ZOOM, videoZoom); 
		editor.commit();
	}
	
	public int getVideoZoom(final Context context){
		if(context == null){
			return 10;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int videoZoom=preferences.getInt(VIDEO_ZOOM, 10);
		return videoZoom;
		
	}
	
	public void storeEMGScale(final int emgScale,final Context context,final String stKey){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(EMG_SCALE+stKey, emgScale);
		editor.commit();
	}
	
	public int getEMGScale(final Context context,final String stKey){
		if(context == null){
			return MAX_EMG_OFF_SCALE;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int emgOffset=MAX_EMG_OFF_SCALE - preferences.getInt(EMG_SCALE+stKey, DEFAULT_EMG_OFF_SCALE);
		return emgOffset;
		
	}
	
	public static final int EMG_OFFSET_CENTER=15;
	
	
	
	public void storeEMGOffset(final int emgOffset,final Context context,final String stKey){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(EMG_OFFSET+stKey, emgOffset); 
		editor.commit();
	}
	
	public int getEMGOffset(final Context context,final String stKey){
		if(context == null){
			return EMG_OFFSET_CENTER;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int emg_Offset=preferences.getInt(EMG_OFFSET+stKey, EMG_OFFSET_CENTER);
		return emg_Offset;
		
	}
	
	public void storeTragetMode(final int tragetMode,final Context context){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(TRAGET_MODE, tragetMode); 
		editor.commit();
	}
	
	public int getTragetMode(final Context context){
		if(context == null){
			return 10;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int traget_Mode=preferences.getInt(TRAGET_MODE, 10);
		return traget_Mode;
		
	}
	
	public void storeTragetRangeValue(final int tragetRange,final Context context){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(CURRENT_TRAGET_VALUE, tragetRange); 
		editor.commit();
	}
	
	public int getTragetRangeValue(final Context context){
		if(context == null){
			return 0;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int traget_Range=preferences.getInt(CURRENT_TRAGET_VALUE, 0);
		return traget_Range;
		
	}
	
	public void storeBoundaryMode(final boolean boundarymode,final Context context){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putBoolean(BOUNDARY_MODE, boundarymode); 
		editor.commit();
	}
	
	public boolean isBoundaryModeEnable(final Context context){
		if(context == null){
			return false;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final boolean boundaryMode=preferences.getBoolean(BOUNDARY_MODE, false);
		return boundaryMode;
		
	}
	
	public void storeBoundaryModeRange(final int boundaryModeRange,final Context context){
		if(context == null){
			return;
		}
		
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(BOUNDARY_MODE_RANGE, boundaryModeRange); 
		editor.commit();
	}
	
	public int getBoundaryModeRange(final Context context){
		if(context == null){
			return 10;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int traget_Range=preferences.getInt(BOUNDARY_MODE_RANGE, 10);
		return traget_Range;
		
	}
	public void storeDisplayMode(final int iDisplayMode,final Context context){
		if(context == null){
			return;
		}
		if(iDisplayMode > VIDEO_GRAPH_VIEW || iDisplayMode < VIDEO_VIEW){
			return;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(DISPLAY_MODE, iDisplayMode); 
		editor.commit();
	}
	public int getDisplayMode(final Context context){
		if(context == null){
			return VIDEO_GRAPH_VIEW;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int iDisplayMode=preferences.getInt(DISPLAY_MODE, VIDEO_GRAPH_VIEW);
		return iDisplayMode;
		
	}
	
	public void storeGridColor(final int iGridColor,final Context context){
		if(context == null){
			return;
		}
	
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(GRID_COLOR, iGridColor); 
		editor.commit();
	}
	public int getGridColor(final Context context){
		if(context == null){
			return D_GRID_COLOR;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int iGridColor=preferences.getInt(GRID_COLOR, D_GRID_COLOR);
		return iGridColor;
		
	}
	
	public void storeBaseColor(final int iBaseColor,final Context context){
		if(context == null){
			return;
		}
	
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor =preferences.edit();
		editor.putInt(BASE_COLOR, iBaseColor); 
		editor.commit();
	}
	public int getBaseColor(final Context context){
		if(context == null){
			return D_BASE_COLOR;
		}
		final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
		final int iGridColor=preferences.getInt(BASE_COLOR, D_BASE_COLOR);
		return iGridColor;
		
	}
	public boolean isFreezeEnabled(final Context context){
           if(context  == null){
              return false;
           }
           int iTragetMode=getTragetMode(context);
           return iTragetMode >=61 && iTragetMode <= 80 ;
        }
	public boolean isPreDrawnEnabled(final Context context){
	   if(context  == null){
	      return false;
	   }
	   int iTragetMode=getTragetMode(context);
	   return iTragetMode >=41 && iTragetMode <= 60 ;
	}
	
	public boolean isConstantModeEnabled(final Context context){
           if(context  == null){
              return false;
           }
           int iTragetMode=getTragetMode(context);
           return iTragetMode >=21 && iTragetMode <= 40 ;
        }
	public void storeDemoMode(final boolean isDemoMode,final Context context){
           if(context == null){
                   return;
           }
   
           final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
           final Editor editor =preferences.edit();
           editor.putBoolean(DEMO_MODE, isDemoMode); 
           editor.commit();
   }
	
	public boolean isDemoModeEnabled(final Context context){
           if(context  == null){
              return false;
           }
           final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
           return preferences.getBoolean(DEMO_MODE, false) ;
        }	
}
