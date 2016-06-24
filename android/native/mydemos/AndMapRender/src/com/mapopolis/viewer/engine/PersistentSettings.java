
package com.mapopolis.viewer.engine;


import java.util.Vector;

import android.graphics.Color;
import android.graphics.Paint;

public class PersistentSettings

{
    public static int currentTextFont = com.mapopolis.viewer.draw.LabelDraw.FontBold;

    public static Paint WaterColor = new Paint();
    
    public static Paint GreenColor = new Paint();
    public static Paint FacilityColor = new Paint();
    public static Paint BackgroundColor = new Paint();
    public static Paint Type1StreetColor =new Paint(); 
    public static Paint Type2StreetColor = new Paint();
    public static Paint Type3StreetColor =new Paint();
    public static Paint Type4StreetColor = new Paint();
    public static Paint Type5StreetColor = new Paint();
    public static Paint SelectedItemColor = new Paint();
    public static Paint RouteStreetColor = new Paint();
    public static Paint LightLandmarkColor =new Paint(); 
    public static Paint NormalButtonLetterColor = new Paint();
    public static Paint LightButtonLetterColor =new Paint(); 
    
    public static Paint BoxColor = new Paint();
    public static Paint ScaleBarColor = new Paint();
    public static Paint HouseColor =  new Paint();
    public static Paint NormalStreetColor = new Paint();
    public static Paint StreetLabelColor = new Paint();
   
    public static Paint AirportColor = new Paint();
    public static Paint BoldLandmarkColor =new Paint();
    public static Paint CityLabelColor = new Paint();
    public static Paint SkyColor = new Paint();
    public static Paint Color1 = new Paint();
    public static Paint Color2 = new Paint();
    public static Paint Color3 = new Paint();

    // initially at program start everything has default settings

    // persistent settings includes favorites
static{
	
}
    PersistentSettings()

    {
    	WaterColor.setColor(Color.BLUE);
    	GreenColor.setColor(Color.GREEN);
    	FacilityColor.setColor(Color.CYAN);
    	BackgroundColor.setColor(Color.WHITE);
    	Type1StreetColor.setColor(Color.RED);
    	Type2StreetColor.setColor(Color.GRAY);
    	Type3StreetColor.setColor(Color.GRAY);
    	Type4StreetColor.setColor(Color.GRAY);
    	Type5StreetColor.setColor(Color.GRAY);
    	SelectedItemColor.setColor(Color.RED);
    	RouteStreetColor.setColor(Color.RED);
    	LightLandmarkColor.setColor(Color.BLUE);
    	NormalButtonLetterColor.setColor(Color.BLUE);
    	LightButtonLetterColor.setColor(Color.BLUE);
    	BoxColor.setColor(Color.BLUE);
    	HouseColor.setColor(Color.RED);
    	NormalStreetColor.setColor(Color.BLUE);
    	StreetLabelColor.setColor(Color.BLACK);
    	AirportColor.setColor(Color.RED);
    	BoldLandmarkColor.setColor(Color.RED);
    	CityLabelColor.setColor(Color.GREEN);
    	SkyColor.setColor(Color.BLUE);
    	Color1.setColor(Color.BLUE);
    	Color2.setColor(Color.BLUE);
    	Color3.setColor(Color.BLUE);
    	
    }

    public static void initFromString(String s)

    {

    }

    public static String getAsString()

    {
        return "";
    }

    public static void setType1StreetColor(Color color)

    {

    }

    public static void setType2StreetColor(Color color)

    {

    }

    public static void setType3StreetColor(Color color)

    {

    }

    public static void setType4StreetColor(Color color)

    {

    }

    public static void setType5StreetColor(Color color)

    {

    }

    public static void setWaterColor(Color color)

    {

    }

    public static void setFacilityColor(Color color)

    {

    }

    public static void setGreenSpaceColor(Color color)

    {

    }

    public static void setRouteColor(Color color)

    {

    }

    public static void setBackgroundColor(Color color)

    {

    }

    public static void setShowSplashScreen(boolean show)

    {

    }

    public static void setRegenerateRouteAtStart(boolean reg)

    {

    }

    public static void setUseMetricUnits(boolean metric)

    {

    }

    public static void setShowLandmarks(boolean show)

    {

    }

    public static void setShowCityNames(boolean show)

    {

    }

    public static void setMapFont(int font)

    {

    }

    public static void setAutoScroll(boolean auto)

    {

    }

    public static void setAutoRotate(boolean auto)

    {

    }

    public static void setHighwayUsePreference(int highway)

    {

    }

    public static void setVoicePromptLevel(int voicePromptLevel)

    {

    }

    public static void setStartGPSAtProgramStart(int start)

    {

    }

    public static Vector getFavorites()

    {
        return null;
    }
}