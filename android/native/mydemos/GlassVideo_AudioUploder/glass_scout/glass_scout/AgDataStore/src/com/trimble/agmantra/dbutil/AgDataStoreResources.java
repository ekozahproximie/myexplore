package com.trimble.agmantra.dbutil;

import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.entity.AttributeInfoEntity;
import com.trimble.agmantra.entity.Crop;
import com.trimble.agmantra.entity.FeatureType;
import com.trimble.agmantra.entity.JobType;
import com.trimble.agmantra.entity.Language;
import com.trimble.agmantra.entity.Mapping;
import com.trimble.agmantra.entity.PickList;
import com.trimble.agmantra.entity.TemplateType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AgDataStoreResources {

	public static final int FEATURE_TYPE_BOUNDARY = 1;
	public static final int FEATURE_TYPE_POLYGON = 2;
	public static final int FEATURE_TYPE_PATH = 3;
	public static final int FEATURE_TYPE_POINT = 4;
	
	public static final int DATATYPE_STRING = 1;
	public static final int DATATYPE_IMAGE = 2;
	public static final int DATATYPE_PICKLIST = 3;
	public static final int DATATYPE_STRINGARRAY = 4;
	public static final int DATATYPE_INTEGER = 5; // reserved
	public static final int DATATYPE_FLOAT = 6; // reserved
	public static final int DATATYPE_DATE = 7; // reserved
	public static final int DATATYPE_BOOLEAN = 8; // reserved
	public static final int DATATYPE_STRING_LENGTH = 50;
	public static final int DATATYPE_IMAGE_LENGTH = 60;
	public static final int DATATYPE_PICKLIST_LENGTH = 50;
	public static final int DATATYPE_FLOAT_LENGTH = 50;
	public static final int DATATYPE_STRINGARRAY_LENGTH = 100;

	// Diff types of template
	public static final int ATT_TYPE_INSECTS = 1;
	public static final int ATT_TYPE_WEEDS = 2;
	public static final int ATT_TYPE_DISEASE = 3;
	public static final int ATT_TYPE_CROP_CONDITION = 4;
	public static final int ATT_TYPE_OTHERS = 5;
	public static final int ATT_TYPE_NDVI = 6;
	public static final int ATT_TYPE_NDVI_REF = 7;
	public static final int ATT_TYPE_IMAGE = 1000;

	// Diff types of Job
	public static final int JOB_TYPE_BOUNDARY_MAPPING = 1;
	public static final int JOB_TYPE_MAP_SCOUTING = 2;
	public static final int JOB_TYPE_PHOTO = 3;
	public static final int JOB_TYPE_NO_JOB = 4;
	
	
	public static final String FEATURE_TYPE_BOUNDARY_NAME = "Boundary";
	public static final String FEATURE_TYPE_POLYGON_NAME = "Polygon";
	public static final String FEATURE_TYPE_PATH_NAME = "Path";
	public static final String FEATURE_TYPE_POINT_NAME = "Point";

	public static final String JOB_TYPE_BOUNDARY_MAPPING_NAME = "Boundary_Mapping";
	public static final String JOB_TYPE_MAP_SCOUTING_NAME = "Scout";
	public static final String JOB_TYPE_PHOTO_NAME = "Image";

	// Job status info
	public static final int JOB_STATUS_UNFINISHED = 0;
	public static final int JOB_STATUS_FINISHED = 1;
	public static final int JOB_STATUS_ENCODING = 2;
	public static final int JOB_STATUS_UNUPLOADED = 3;
	public static final int JOB_STATUS_UPLOADED = 4;
	
	
	public static final int UN_ASSOCIATED_BOUNDARY_WITH_FIELD = -1;
	
	//FDT Tags & Attributes Info	
	public static final String FLAGTYPE     = "FlagType";
	public static final String FLAGNAME="FlagName";
	public static final String __IMAGE="__IMAGE";
	public static final String DETAIL="Detail";
	public static final String SEVERITY="Severity";
	public static final String NOTES="Notes";
	public static final String LANGUAGE="Language";
	public static final String REGION="Region";
	public static final String CROP="Crop";
	public static final String GROWTHSTG="GrowthStg";
	public static final String PLANTCOUNT="PlantCount";
	public static final String CONDITION="Condition";
	public static final String PHOTO   = "Photo";
	
	public static final String UNIT   = "UNIT";
	
	
	


	
	
	//Attributes Info      
	public static final String GROWTHSTAGE="GrowthStage";
	//NDVI tag that should match in the string.xml file with 
	//corresponding field in layout XML file  
	
	public static final String CROP_NPER        = "Crop_Nper";
	public static final String CONVFACT         = "ConvFact";
	public static final String NDVI_REF         = "NDVI_ref";
    public static final String NDVI_NON_REF     = "NDVI_fp";
    public static final String NDVI_SOIL        = "NDVI_soil";
    public static final String NDVI_NUE         = "NUE_per";
    public static final String NDVI_MAX_YIELD   = "MaxYield";
    public static final String NDVI_RI          = "RespIndex";
    public static final String NDVI_NRATE       = "NRate";
    
    public static final String NRATEUNIT        = "NRateUnit";
    public static final String YIELDUNIT        = "YieldUnit";
    
    
    public static final String NDVI_FLAG_TYPE        = "NDVI_fp";
    public static final String NDVI_REF_FLAG_TYPE    = "NDVI_ref";
    
    public static final String FDT_TAG_NDVI         = "NDVI";
    public static final String FDT_TAG_NDVI_REF     = "NDVIRef";
	
    
    public static final int INSECT_DETAILS_ID	= 2;
    public static final int INSECT_SEVERITY_ID	= 3;
    public static final int INSERT_LANG_ID		= 5;
    public static final int WEED_DETAILS_ID		= 9;
    public static final int WEED_SEVERITY_ID    = 10;
    public static final int WEED_LANG_ID		= 12;
    public static final int DISEASE_DETAILS_ID  = 16;
    public static final int DISEASE_SEVERITY_ID	= 17;
    public static final int DISEASE_LANG_ID		= 19;
    public static final int CROP_ID          = 23;
    public static final int GROWTH_STAGE_ID  = 24;
    public static final int CROP_CONDITION_ID	= 26;
    public static final int CROP_LANG_ID		= 28;
    
    
	public static final int NDVI_CROP_ID     = 35;
	public static final int NDVI_UNIT_MAP_ID = 43;
	public static final int NDVI_NPER_ID     = 44;
	public static final int NDVI_CONV_ID     = 45;
	
	
	public static final int GROWTH_STAGE_CORN_GROUP_ID		=  1;
	
	public static final int GROWTH_STAGE_SOYBEANS_GROUP_ID	=  2;
	
	public static final int GROWTH_STAGE_WHEAT_GROUP_ID		=  3;
	
	public static final int GROWTH_STAGE_OTHER_GROUP_ID	=  4;
	
	public static final int LAN_LIST_GROUP_ID= 41;
	
	public static final int EN_LANG_GROUP_ID = 42;
	
	public static final int BG_LANG_GROUP_ID = 43;
	
	public static final int DA_LANG_GROUP_ID = 44;
	
	public static final int DE_LANG_GROUP_ID = 45;
	
	public static final int ES_LANG_GROUP_ID = 46;
	
	public static final int FR_LANG_GROUP_ID = 47;
	
	public static final int IT_LANG_GROUP_ID = 48;
	
	public static final int NL_LANG_GROUP_ID = 49;
	
	public static final int PL_LANG_GROUP_ID = 50;
	
	public static final int PT_LANG_GROUP_ID = 51;
	
	public static final int RU_LANG_GROUP_ID = 52;
	
	
	public static final int W_EN_LANG_GROUP_ID = 53;
	
	public static final int W_BG_LANG_GROUP_ID = 54;
	
	public static final int W_DA_LANG_GROUP_ID = 55;
	
	public static final int W_DE_LANG_GROUP_ID = 56;
	
	public static final int W_ES_LANG_GROUP_ID = 57;
	
	public static final int W_FR_LANG_GROUP_ID = 58;
	
	public static final int W_IT_LANG_GROUP_ID = 59;
	
	public static final int W_NL_LANG_GROUP_ID = 60;
	
	public static final int W_PL_LANG_GROUP_ID = 61;
	
	public static final int W_PT_LANG_GROUP_ID = 62;
	
	public static final int W_RU_LANG_GROUP_ID = 63;
	
	
	public static final int D_EN_LANG_GROUP_ID = 64;
	
	public static final int D_BG_LANG_GROUP_ID = 65;
	
	public static final int D_DA_LANG_GROUP_ID = 66;
	
	public static final int D_DE_LANG_GROUP_ID = 67;
	
	public static final int D_ES_LANG_GROUP_ID = 68;
	
	public static final int D_FR_LANG_GROUP_ID = 69;
	
	public static final int D_IT_LANG_GROUP_ID = 70;
	
	public static final int D_NL_LANG_GROUP_ID = 71;
	
	public static final int D_PL_LANG_GROUP_ID = 72;
	
	public static final int D_PT_LANG_GROUP_ID = 73;
	
	public static final int D_RU_LANG_GROUP_ID = 74;
	
	
	public static final int EN_CONDITION_GROUP_ID = 75;
	
	public static final int BG_CONDITION_GROUP_ID = 76;
	
	public static final int DA_CONDITION_GROUP_ID = 77;
	
	public static final int DE_CONDITION_GROUP_ID = 78;
	
	public static final int ES_CONDITION_GROUP_ID = 79;
	
	public static final int FR_CONDITION_GROUP_ID = 80;
	
	public static final int IT_CONDITION_GROUP_ID = 81;
	
	public static final int NL_CONDITION_GROUP_ID = 82;
	
	public static final int PL_CONDITION_GROUP_ID = 83;
	
	public static final int PT_CONDITION_GROUP_ID = 84;
	
	public static final int RU_CONDITION_GROUP_ID = 85;
	
	
	public static final int EN_CORN_GROWTH_GROUP_ID = 1;
	
	public static final int BG_CORN_GROWTH_GROUP_ID = 87;
	
	public static final int DA_CORN_GROWTH_GROUP_ID = 88;
	
	public static final int DE_CORN_GROWTH_GROUP_ID = 89;
	
	public static final int ES_CORN_GROWTH_GROUP_ID = 90;
	
	public static final int FR_CORN_GROWTH_GROUP_ID = 91;
	
	public static final int IT_CORN_GROWTH_GROUP_ID = 92;
	
	public static final int NL_CORN_GROWTH_GROUP_ID = 93;
	
	public static final int PL_CORN_GROWTH_GROUP_ID = 94;
	
	public static final int PT_CORN_GROWTH_GROUP_ID = 95;
	
	public static final int RU_CORN_GROWTH_GROUP_ID = 96;
	
	public static final int EN_WHEAT_GROWTH_GROUP_ID = 3;
		
	public static final int BG_WHEAT_GROWTH_GROUP_ID = 98;
	
	public static final int DA_WHEAT_GROWTH_GROUP_ID = 99;
	
	public static final int DE_WHEAT_GROWTH_GROUP_ID = 100;
	
	public static final int ES_WHEAT_GROWTH_GROUP_ID = 101;
	
	public static final int FR_WHEAT_GROWTH_GROUP_ID = 102;
	
	public static final int IT_WHEAT_GROWTH_GROUP_ID = 103;
	
	public static final int NL_WHEAT_GROWTH_GROUP_ID = 104;
	
	public static final int PL_WHEAT_GROWTH_GROUP_ID = 105;
	
	public static final int PT_WHEAT_GROWTH_GROUP_ID =106;
	
	public static final int RU_WHEAT_GROWTH_GROUP_ID = 107;
	
	public static final int EN_OTHER_GROWTH_GROUP_ID = 4;
	
	public static final int BG_OTHER_GROWTH_GROUP_ID = 109;
	
	public static final int DA_OTHER_GROWTH_GROUP_ID = 110;
	
	public static final int DE_OTHER_GROWTH_GROUP_ID = 111;
	
	public static final int ES_OTHER_GROWTH_GROUP_ID = 112;
	
	public static final int FR_OTHER_GROWTH_GROUP_ID = 113;
	
	public static final int IT_OTHER_GROWTH_GROUP_ID = 114;
	
	public static final int NL_OTHER_GROWTH_GROUP_ID = 115;
	
	public static final int PL_OTHER_GROWTH_GROUP_ID = 116;
	
	public static final int PT_OTHER_GROWTH_GROUP_ID =117;
	
	public static final int RU_OTHER_GROWTH_GROUP_ID = 118;
	
	public static final int EN_INSECTS_GROUP_ID = 119;
        
        public static final int BG_INSECTS_GROUP_ID = 120;
        
        public static final int DA_INSECTS_GROUP_ID = 121;
        
        public static final int DE_INSECTS_GROUP_ID = 122;
        
        public static final int ES_INSECTS_GROUP_ID = 123;
        
        public static final int FR_INSECTS_GROUP_ID = 124;
        
        public static final int IT_INSECTS_GROUP_ID = 125;
        
        public static final int NL_INSECTS_GROUP_ID = 126;
        
        public static final int PL_INSECTS_GROUP_ID = 127;
        
        public static final int PT_INSECTS_GROUP_ID =128;
        
        public static final int RU_INSECTS_GROUP_ID = 129;
	
        
        
	
	
	public static final String EN_LOCALE = "en";
	
	public static final String[] LOCALE_LANGUAGES =

	    {

		EN_LOCALE,

	    "bg", // Bulgarian //български

	    "da", // Danish //dansk

	    "de", // German //Deutsch

	    "es", // Spanish //español

	    "fr", // French //français 

	    "it", // Italian //italiano 

	    "nl", // Dutch //Nederlands

	    "pl", // Polish //polski 

	    "pt", // Portuguese //português

	    "ru", // Russian //pyckknm
	    
	    "zh" //Chines // 中文 ||中文
	    };
	/**
	 * 	Portuguese, Brazil (pt_BR)
	 *  Portuguese, Portugal (pt_PT)
	 */
	
	private static final String []SERVERITY_BG={"Слаб GPS сигнал","Средна","Добър GPS-сигнал" };//bg

	private static final String []SERVERITY_DA={"Lav","Middel","Høj" };//da

	private static final String []SERVERITY_DE={"Schlecht","Mittel","Hervorragend" };//de

	private static final String []SERVERITY_ES={"Bajo/a","Alto/a","Medio/a"}; //es
	
	private static final String []SERVERITY_FR={"Bas","Moyenne","Elevé" }; //fr
	
	private static final String []SERVERITY_IT={"Low","Medio", "Alto"}; //it

	private static final String []SERVERITY_NL={"Laag","Medium","Hoog" }; //nl
	
	private static final String []SERVERITY_PL={"Niski","Średni","Wysoki"}; //pl
	
	private static final String []SERVERITY_PT={"Baixo","Média","Alto"}; //pt
	
	private static final String []SERVERITY_RU={"Низк.","Средн.","Высок."}; // ru
	
	private static final String []SERVERITY_ZH={"Низк.","Средн.","Высок."}; // zh
	
	public static String[] TEMPLATETYPE_BG_CONDITION = { "Слаб","Средно",	"Над средното"};
	
	public static String[] TEMPLATETYPE_DA_CONDITION = { "Dårlig", "G.snit","Over g.snit"};

	public static String[] TEMPLATETYPE_DE_CONDITION = { "Schlecht", "Mittelwert","Über Mittelwert"};
	
	public static String[] TEMPLATETYPE_ES_CONDITION = { "Mala", "Promedio","Por encima del Promedio"};

	public static String[] TEMPLATETYPE_FR_CONDITION = { "Faible", "Moyenne","Au-dessus de la moyenne"};

	public static String[] TEMPLATETYPE_IT_CONDITION = { "Mediocre", "Media","Al di sopra della media"};

	public static String[] TEMPLATETYPE_NL_CONDITION= { "Matig", "Gemiddeld","Boven gemiddeld"};

	public static String[] TEMPLATETYPE_PL_CONDITION = { "Słaby", "Średnia","Po\"Średnia"};

	public static String[] TEMPLATETYPE_PT_CONDITION = { "Pobre", "Média","Sobre a Média"};

	public static String[] TEMPLATETYPE_RU_CONDITION = { "Плохой", "Сорняки","Выше среднего"};
	
	private static String[] TEMPLATETYPE_ZH_CONDITION = { "Плохой", "Сорняки","Выше среднего"};

	private static String [] GROWTH_STAGE_SOYBEANS={"VE - VC",	"V2 - V5", "R1 - R4", "R5 - R8"};
	
	public static String[] GROWTH_STAGE_CORN_BG = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Копринено черно" };

	public static String[] GROWTH_STAGE_CORN_DA = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Silkesort" };
	
	public static String[] GROWTH_STAGE_CORN_DE = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Seide Schwarz" };
	
	public static String[] GROWTH_STAGE_CORN_ES = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Silk Black" };
	
	public static String[] GROWTH_STAGE_CORN_FR = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Noir soie"};
	
	public static String[] GROWTH_STAGE_CORN_IT = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Nero seta"};
	
	public static String[] GROWTH_STAGE_CORN_NL = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Zijde zwart"};
	
	public static String[] GROWTH_STAGE_CORN_PL = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Czarny jedwab"};
	
	public static String[] GROWTH_STAGE_CORN_PT = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Seda preta"};
	
	public static String[] GROWTH_STAGE_CORN_RU = { "VE - V2", "V2 - V5",
		"V6 - Tassel", "Шелковисто-черный"};
	
	private static String[] GROWTH_STAGE_CORN_ZH = { "VE - V2", "V2 - V5",
           "V6 - 长出穗状雄花", "丝状黑色层"};



	public static String[] GROWTH_STAGE_WHEAT_BG = { "Есен/поникване", "Зима/Управление",
		"Пролет/цъфтеж", "Лято/зрялост"};
	
	
	public static String[] GROWTH_STAGE_WHEAT_DA = { "Efterår/Emergens", "Vinter/Fræsning",
		"Forår/Blomstring", "Sommer/Modning" };
	
	
	public static String[] GROWTH_STAGE_WHEAT_DE = { "Herbst/Ausschlagen", "Winter/Pflügen",
		"Frühling/Blüte", "Sommer/Reife" };
	
	
	public static String[] GROWTH_STAGE_WHEAT_ES = { "Otoño/Emergencia", "Invierno/Labranza",
		"Primavera/Floración", "Verano/Madurez"};
	
	
	public static String[] GROWTH_STAGE_WHEAT_FR = { "Automne/apparition", "Hiver/tallage",
		"Printemps/fleuraison", "Été/maturité" };
	
	
	public static String[] GROWTH_STAGE_WHEAT_IT = { "Caduta/Emergenza", "Inverno/Coltivazione",
		"Primavera/Fioritura", "Summer/Maturity" };
	
	
	public static String[] GROWTH_STAGE_WHEAT_NL = { "Herfst/opkomst", "Winter/uitlopen",
		"Lente/bloeien", "Zomer/volgroeidheid" };
	
	
	public static String[] GROWTH_STAGE_WHEAT_PL = { "Spadek/wyłonienie się", "Zima/Krzewienie",
		"Wiosna/Kwitnienie", "Lato/Dojrzewanie" };
		
	public static String[] GROWTH_STAGE_WHEAT_PT = { "Outono/Emergência", "Inverno/Perfilhamento",
		"Primavera/Florescimento", "Verão/Maturidade" };
	
	
	public static String[] GROWTH_STAGE_WHEAT_RU = { "Отсутствие/появление", "Зима/проращивание",
		"Весна/цветение", "Лето/созревание" };
	
	private static String[] GROWTH_STAGE_WHEAT_ZH = { "秋季/出苗","冬季/分蘖","春季/开花"," 夏季/成熟" };
	
	
	public static String[] GROWTH_STAGE_OTHER_BG = { "Подготовка за засаждане", "предварително появата",
		"Ранно поникване","Средата на сезона", "Края на сезона"};
	
	
	public static String[] GROWTH_STAGE_OTHER_DA = { "Præ-plante", "Præ-emergens",
		"Tidlig emergens", "Midt på sæsonen","Sent på sæsonen" };
	
	
	public static String[] GROWTH_STAGE_OTHER_DE = {"Vorpflanzen","Vorauflauf","Frühes Ausschlagen",
		"Mitte Saison","Spät Saison"};
	
	
	public static String[] GROWTH_STAGE_OTHER_ES = {"Previo a la Siembra","pre-emergencia","Emergencia Precoz",
		"A Mitad de Campaña","A Final de Campaña"};
	
	
	public static String[] GROWTH_STAGE_OTHER_FR = {"Pré-plante","pré-levée","Début d'apparition",
		"Mi-saison","Fin de saison"};
	
	
	public static String[] GROWTH_STAGE_OTHER_IT = { "Pre-piantare","pre-emergenza","Emergenza precedente",
		"Metà stagione","Fine stagione"};
	
	
	public static String[] GROWTH_STAGE_OTHER_NL = {"Vóór zaaien","Voor opkomst","Vroege opkomst",
		"Midden seizoen","Late seizoen"};
	
	
	public static String[] GROWTH_STAGE_OTHER_PL = { "Wstępne sadzenie","przedwschodowe",
		"Wczesne wyłonienie się","Sezon środkowy","Późny sezon"};
		
	public static String[] GROWTH_STAGE_OTHER_PT = {"Pré-planta","pré-emergência","Emergência precoce",
		"Meia estação","Final da estação"};
	

	public static String[] GROWTH_STAGE_OTHER_RU = {"Предпосадочная","предвсходовой","Начало роста",
		"Середина сезона","Конец сезона"};
	
	private static String[] GROWTH_STAGE_OTHER_ZH = {"种植前","出苗前", "出苗早期","季中","季末"};

	
	
	public static final int STATUS_AUTOGENERATED = 0x80000000;
	private static final String[][] PICKLIST_ITEMS = {
	                // Insects List
	   AgDataStorePickListEntry.INSECT_PICKLIST_EN
	    	,{
			// Insect Severity
			"Low",
			"Medium",
			"High"
			},
			// Weeds List
	  AgDataStorePickListEntry.WEED_PICKLIST_EN
			,{
			//Weed Severity
			"Low",
			"Medium",
			"High"
			},
			// Disease List
	 AgDataStorePickListEntry.DISEASE_PICKLIST_EN
			,{
			// Disease Severity
			"Low",
			"Medium",
			"High"
			},
			// Crop List
	AgDataStorePickListEntry.CROP_PICKLIST_EN
			,{
			// Growth Stage List
			"VE - V2", "V2 - V5", "V6 - Tassel", "Silk Black Layer" },
			GROWTH_STAGE_SOYBEANS,
			{"Fall/Emergence","Winter/Tillering", "Spring/Flowering", "Summer/Maturity"},
			{"Pre-Plant", "Pre-emergence", "Early Emergence", "Mid Season","Late Season"},
			// Crop condition List
			{"Poor", "Average", "Above Average" },
            //Crop for NDVI metric
			AgDataStorePickListEntry.NDVI_CROP_METRIC_EN,
            //Crop for NDVI imperial
			AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_EN,
			{"metric", "english"},
			 //% N for NDVI metric
			{"2.45",},{"2.30"},{"1.30"},{"1.25"},{"1.70"},{"2.10"},{"1.34"},{"3.10"},{"1.28"},
			//% N for NDVI imperial
			{"2.45",},{"2.30"},{"1.30"},{"1.25"},{"1.70"},{"2.10"},{"1.34"},{"3.10"},
			 //Conversion factor for NDVI metric
			{"1"}, {"1"},{"1"},{"1"},{"1"},{"1"},{"1"},{"1"},{"1"},
	          //Conversion factor for NDVI imperial
			{"60"},   {"60"},  {"56"},  {"56"},  {"48"},  {"54"},  {"56"},  {"50"},
			LOCALE_LANGUAGES,
			SERVERITY_BG,SERVERITY_DA,SERVERITY_DE,SERVERITY_ES,
			SERVERITY_FR,SERVERITY_IT,SERVERITY_NL,SERVERITY_PL,
			SERVERITY_PT,SERVERITY_RU,SERVERITY_ZH,
			
			SERVERITY_BG,SERVERITY_DA,SERVERITY_DE,SERVERITY_ES,
			SERVERITY_FR,SERVERITY_IT,SERVERITY_NL,SERVERITY_PL,
			SERVERITY_PT,SERVERITY_RU,SERVERITY_ZH,
			
			SERVERITY_BG,SERVERITY_DA,SERVERITY_DE,SERVERITY_ES,
			SERVERITY_FR,SERVERITY_IT,SERVERITY_NL,SERVERITY_PL,
			SERVERITY_PT,SERVERITY_RU,SERVERITY_ZH,
			
			TEMPLATETYPE_BG_CONDITION,TEMPLATETYPE_DA_CONDITION,TEMPLATETYPE_DE_CONDITION,TEMPLATETYPE_ES_CONDITION,
			TEMPLATETYPE_FR_CONDITION,TEMPLATETYPE_IT_CONDITION,TEMPLATETYPE_NL_CONDITION,TEMPLATETYPE_PL_CONDITION,
			TEMPLATETYPE_PT_CONDITION,TEMPLATETYPE_RU_CONDITION,TEMPLATETYPE_ZH_CONDITION,
			
			GROWTH_STAGE_CORN_BG,GROWTH_STAGE_CORN_DA,GROWTH_STAGE_CORN_DE,GROWTH_STAGE_CORN_ES,
			GROWTH_STAGE_CORN_FR,GROWTH_STAGE_CORN_IT,GROWTH_STAGE_CORN_NL,GROWTH_STAGE_CORN_PL,
			GROWTH_STAGE_CORN_PT,GROWTH_STAGE_CORN_RU,GROWTH_STAGE_CORN_ZH,
			
			GROWTH_STAGE_WHEAT_BG,GROWTH_STAGE_WHEAT_DA,GROWTH_STAGE_WHEAT_DE,GROWTH_STAGE_WHEAT_ES,
			GROWTH_STAGE_WHEAT_FR,GROWTH_STAGE_WHEAT_IT,GROWTH_STAGE_WHEAT_NL,GROWTH_STAGE_WHEAT_PL,
			GROWTH_STAGE_WHEAT_PT,GROWTH_STAGE_WHEAT_RU,GROWTH_STAGE_WHEAT_ZH,
			
			GROWTH_STAGE_OTHER_BG,GROWTH_STAGE_OTHER_DA,GROWTH_STAGE_OTHER_DE,GROWTH_STAGE_OTHER_ES,
			GROWTH_STAGE_OTHER_FR,GROWTH_STAGE_OTHER_IT,GROWTH_STAGE_OTHER_NL,GROWTH_STAGE_OTHER_PL,
			GROWTH_STAGE_OTHER_PT,GROWTH_STAGE_OTHER_RU,GROWTH_STAGE_OTHER_ZH,
			
			AgDataStorePickListEntry.INSECT_PICKLIST_BG,AgDataStorePickListEntry.INSECT_PICKLIST_DA,AgDataStorePickListEntry.INSECT_PICKLIST_DE,
			AgDataStorePickListEntry.INSECT_PICKLIST_ES,AgDataStorePickListEntry.INSECT_PICKLIST_FR,AgDataStorePickListEntry.INSECT_PICKLIST_IT,
			AgDataStorePickListEntry.INSECT_PICKLIST_NL,AgDataStorePickListEntry.INSECT_PICKLIST_PL,AgDataStorePickListEntry.INSECT_PICKLIST_PT,
			AgDataStorePickListEntry.INSECT_PICKLIST_RU,AgDataStorePickListEntry.INSECT_PICKLIST_ZH,
			
			AgDataStorePickListEntry.WEED_PICKLIST_BG,AgDataStorePickListEntry.WEED_PICKLIST_DA,AgDataStorePickListEntry.WEED_PICKLIST_DE,
                        AgDataStorePickListEntry.WEED_PICKLIST_ES,AgDataStorePickListEntry.WEED_PICKLIST_FR,AgDataStorePickListEntry.WEED_PICKLIST_IT,
                        AgDataStorePickListEntry.WEED_PICKLIST_NL,AgDataStorePickListEntry.WEED_PICKLIST_PL,AgDataStorePickListEntry.WEED_PICKLIST_PT,
                        AgDataStorePickListEntry.WEED_PICKLIST_RU,AgDataStorePickListEntry.WEED_PICKLIST_ZH,
                        
                        AgDataStorePickListEntry.DISEASE_PICKLIST_BG,AgDataStorePickListEntry.DISEASE_PICKLIST_DA,AgDataStorePickListEntry.DISEASE_PICKLIST_DE,
                        AgDataStorePickListEntry.DISEASE_PICKLIST_ES,AgDataStorePickListEntry.DISEASE_PICKLIST_FR,AgDataStorePickListEntry.DISEASE_PICKLIST_IT,
                        AgDataStorePickListEntry.DISEASE_PICKLIST_NL,AgDataStorePickListEntry.DISEASE_PICKLIST_PL,AgDataStorePickListEntry.DISEASE_PICKLIST_PT,
                        AgDataStorePickListEntry.DISEASE_PICKLIST_RU,AgDataStorePickListEntry.DISEASE_PICKLIST_ZH,
                        
                        AgDataStorePickListEntry.CROP_PICKLIST_BG,AgDataStorePickListEntry.CROP_PICKLIST_DA,AgDataStorePickListEntry.CROP_PICKLIST_DE,
                        AgDataStorePickListEntry.CROP_PICKLIST_ES,AgDataStorePickListEntry.CROP_PICKLIST_FR,AgDataStorePickListEntry.CROP_PICKLIST_IT,
                        AgDataStorePickListEntry.CROP_PICKLIST_NL,AgDataStorePickListEntry.CROP_PICKLIST_PL,AgDataStorePickListEntry.CROP_PICKLIST_PT,
                        AgDataStorePickListEntry.CROP_PICKLIST_RU, AgDataStorePickListEntry.CROP_PICKLIST_ZH,
                        
                        
                        
                        AgDataStorePickListEntry.NDVI_CROP_METRIC_BG,AgDataStorePickListEntry.NDVI_CROP_METRIC_DA,AgDataStorePickListEntry.NDVI_CROP_METRIC_DE,
                        AgDataStorePickListEntry.NDVI_CROP_METRIC_ES,AgDataStorePickListEntry.NDVI_CROP_METRIC_FR,AgDataStorePickListEntry.NDVI_CROP_METRIC_IT,
                        AgDataStorePickListEntry.NDVI_CROP_METRIC_NL,AgDataStorePickListEntry.NDVI_CROP_METRIC_PL,AgDataStorePickListEntry.NDVI_CROP_METRIC_PT,
                        AgDataStorePickListEntry.NDVI_CROP_METRIC_RU,AgDataStorePickListEntry.NDVI_CROP_METRIC_ZH,
                        
                        
                        AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_BG,AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_DA,AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_DE,
                        AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_ES,AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_FR,AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_IT,
                        AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_NL,AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_PL,AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_PT,
                        AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_RU, AgDataStorePickListEntry.NDVI_CROP_IMPERIAL_ZH,
                    
                    
			
            };

	//private static Integer[] TEMPLATETYPE_ID = { 1, 2, 3, 4, 5,1000 };
	private static Integer[] TEMPLATETYPE_ID = { 1, 2, 3, 4, 5, 6 ,7,1000};
	/*private static String[] TEMPLATETYPE_TEMPLATENAME = { "Insects", "Weeds",
			"Disease", "Crop Condition", "Other", "Photo" };*/
	
	public static String[] TEMPLATETYPE_TEMPLATENAME = { "Insects", "Weeds",
        "Diseases", "Crop Condition", "Others", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};

	public static String[] TEMPLATETYPE_BG_TEMPLATENAME = { "Насекоми",  "Плевели",
		 "Заболявания", "Състояние на културата",  "Други", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};
	
	public static String[] TEMPLATETYPE_DA_TEMPLATENAME = { "Insekter", "Ukrudt",
		"Sygdomme", "Afgrødeforhold", "Andre", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};

	public static String[] TEMPLATETYPE_DE_TEMPLATENAME = { "Insekten", "Unkraut",
		"Krankheiten", "Fruchtbedingungen", "Andere", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};
	
	public static String[] TEMPLATETYPE_ES_TEMPLATENAME = { "Insectos", "Maleza",
		"Enfermedades", "Condición del Cultivo", "Otros", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};

	public static String[] TEMPLATETYPE_FR_TEMPLATENAME = { "Insectes", "Mauvaises herbes",
		"Maladies", "État de la culture", "Autres", NDVI_FLAG_TYPE,"NDVI_réf" ,"Photo"};

	public static String[] TEMPLATETYPE_IT_TEMPLATENAME = { "Insetti", "Erbacce",
		"Malattie", "Condizioni del raccolto", "Altri", NDVI_FLAG_TYPE,"NDVI_rif" ,"Photo"};

	public static String[] TEMPLATETYPE_NL_TEMPLATENAME = { "Insecten", "Onkruiden",
		"Ziekten", "Gewas conditie", "Anders", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};

	public static String[] TEMPLATETYPE_PL_TEMPLATENAME = { "Insekty", "Zboża",
		"Choroby", "Stan uprawy", "Inne", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};

	public static String[] TEMPLATETYPE_PT_TEMPLATENAME = { "Insetos", "Ervas Daninhas",
		"Doenças", "Condição da Colheita", "Outros", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};

	public static String[] TEMPLATETYPE_RU_TEMPLATENAME = { "Насекомые", "Сорняки",
		"Болезни", "Состояние культуры", "Другие", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};
	
	private static String[] TEMPLATETYPE_ZH_TEMPLATENAME = { "虫害", "杂草",
           "病害", "作物生长情况", "其他", NDVI_FLAG_TYPE,NDVI_REF_FLAG_TYPE ,"Photo"};

	
	
	public static final String[] TEMPLATETYPE_TEMPLATENAME_TFDTTAG = { "Insect",
			"Weed", "Disease", "CropCondition", "Other",FDT_TAG_NDVI,FDT_TAG_NDVI_REF, "Photo" };

	private static String[] ATTRIBUTEINFO_NAME = 
	    { FLAGNAME, __IMAGE, DETAIL, SEVERITY, NOTES, LANGUAGE, REGION,
	      FLAGNAME, __IMAGE, DETAIL, SEVERITY, NOTES,LANGUAGE, REGION,
	      FLAGNAME, __IMAGE, DETAIL, SEVERITY,NOTES,LANGUAGE, REGION,
	      FLAGNAME, __IMAGE, CROP, GROWTHSTAGE,PLANTCOUNT, CONDITION, NOTES,LANGUAGE, REGION, 
	      FLAGNAME, __IMAGE, NOTES,
	      __IMAGE,
	      FLAGNAME,CROP,NDVI_REF,NDVI_NON_REF,NDVI_SOIL,NDVI_NUE,NDVI_MAX_YIELD,NDVI_RI,NDVI_NRATE,UNIT,CROP_NPER,CONVFACT,NRATEUNIT,YIELDUNIT,__IMAGE,LANGUAGE, REGION,
	      FLAGNAME,NDVI_REF};
	/**
	 * Every item in the pick list array has one entry match with there attribute info entity id.
	 * attribute info entity id for pick list entry.(pick list array or item have one attribute info entity id)
	 */
	private static long[] ATTRIBUTEINFO_NAME_MAPPING_PICKLIST_FIELD_INDEX={
	                                                                       INSECT_DETAILS_ID,INSECT_SEVERITY_ID,
	                                                                       WEED_DETAILS_ID,WEED_SEVERITY_ID,
	                                                                       DISEASE_DETAILS_ID,DISEASE_SEVERITY_ID,
	                                                                       CROP_ID,GROWTH_STAGE_ID,GROWTH_STAGE_ID,GROWTH_STAGE_ID,GROWTH_STAGE_ID,
	                                                                       CROP_CONDITION_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_UNIT_MAP_ID
	                                                                       
	                                                                       ,NDVI_NPER_ID,NDVI_NPER_ID,NDVI_NPER_ID,NDVI_NPER_ID,NDVI_NPER_ID,
	                                                                        NDVI_NPER_ID,NDVI_NPER_ID,NDVI_NPER_ID,NDVI_NPER_ID,
	                                                                       
	                                                                       NDVI_NPER_ID,NDVI_NPER_ID,NDVI_NPER_ID, NDVI_NPER_ID,NDVI_NPER_ID,
	                                                                       NDVI_NPER_ID,NDVI_NPER_ID,NDVI_NPER_ID,
	                                                                       
	                                                                       NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID,
	                                                                       NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID,
	                                                                       
	                                                                       NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID
	                                                                       ,NDVI_CONV_ID,NDVI_CONV_ID,NDVI_CONV_ID,
	                                                                       
	                                                                       -1,
	                                                                       
	                                                                       INSECT_SEVERITY_ID, INSECT_SEVERITY_ID, INSECT_SEVERITY_ID, INSECT_SEVERITY_ID, INSECT_SEVERITY_ID,
	                                                                       INSECT_SEVERITY_ID, INSECT_SEVERITY_ID, INSECT_SEVERITY_ID, INSECT_SEVERITY_ID, INSECT_SEVERITY_ID,
	                                                                       INSECT_SEVERITY_ID,
	                                                                       
	                                                                       WEED_SEVERITY_ID,WEED_SEVERITY_ID,WEED_SEVERITY_ID,WEED_SEVERITY_ID,WEED_SEVERITY_ID,
	                                                                       WEED_SEVERITY_ID,WEED_SEVERITY_ID,WEED_SEVERITY_ID,WEED_SEVERITY_ID,WEED_SEVERITY_ID,
	                                                                       WEED_SEVERITY_ID,
	                                                                      
	                                                                       DISEASE_SEVERITY_ID,DISEASE_SEVERITY_ID,DISEASE_SEVERITY_ID,DISEASE_SEVERITY_ID,DISEASE_SEVERITY_ID,
	                                                                       DISEASE_SEVERITY_ID,DISEASE_SEVERITY_ID,DISEASE_SEVERITY_ID,DISEASE_SEVERITY_ID,DISEASE_SEVERITY_ID,
	                                                                       DISEASE_SEVERITY_ID,
	                                                                      
	                                                                       CROP_CONDITION_ID,CROP_CONDITION_ID,CROP_CONDITION_ID,CROP_CONDITION_ID,CROP_CONDITION_ID,
	                                                                       CROP_CONDITION_ID,CROP_CONDITION_ID,CROP_CONDITION_ID,CROP_CONDITION_ID,CROP_CONDITION_ID,
	                                                                       CROP_CONDITION_ID,
	                                                                       
	                                                                       -1,-1,-1,-1,-1,
	                                                                       -1,-1,-1,-1,-1,
	                                                                       -1,
	                                                                       
	                                                                       -1,-1,-1,-1,-1,
	                                                                       -1,-1,-1,-1,-1,
	                                                                       -1,
	                                                                       
	                                                                       -1,-1,-1,-1,-1,
	                                                                       -1,-1,-1,-1,-1,
	                                                                       -1,
	                                                                       
	                                                                       INSECT_DETAILS_ID,INSECT_DETAILS_ID,INSECT_DETAILS_ID,INSECT_DETAILS_ID,INSECT_DETAILS_ID,
	                                                                       INSECT_DETAILS_ID,INSECT_DETAILS_ID,INSECT_DETAILS_ID,INSECT_DETAILS_ID,INSECT_DETAILS_ID,
	                                                                       INSECT_DETAILS_ID,
	                                                                       
	                                                                       WEED_DETAILS_ID,WEED_DETAILS_ID,WEED_DETAILS_ID,WEED_DETAILS_ID,WEED_DETAILS_ID,
	                                                                       WEED_DETAILS_ID,WEED_DETAILS_ID,WEED_DETAILS_ID,WEED_DETAILS_ID,WEED_DETAILS_ID,
	                                                                       WEED_DETAILS_ID,
	                                                                       
	                                                                       
	                                                                       DISEASE_DETAILS_ID,DISEASE_DETAILS_ID,DISEASE_DETAILS_ID,DISEASE_DETAILS_ID,DISEASE_DETAILS_ID,
	                                                                       DISEASE_DETAILS_ID,DISEASE_DETAILS_ID,DISEASE_DETAILS_ID,DISEASE_DETAILS_ID,DISEASE_DETAILS_ID,
	                                                                       DISEASE_DETAILS_ID,
	                                                                       
	                                                                       CROP_ID,CROP_ID,CROP_ID,CROP_ID,CROP_ID,
	                                                                       CROP_ID,CROP_ID,CROP_ID,CROP_ID,CROP_ID,
	                                                                       CROP_ID,
	                                                                       
	                                                                       NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,
	                                                                       NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,
	                                                                       NDVI_CROP_ID,
	                                                                       
	                                                                       NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,
                                                                               NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,NDVI_CROP_ID,
                                                                               NDVI_CROP_ID
	
									      };
	/**
	 * Every item in the pick list array has one entry in the Group id array.
         * Group id for pick list entry.(pick list array or item have one group id)
         */
	private static long[] ATTRIBU_MAPPING_PICKLIST_FIELD_GROUP_ID_INDEX= { EN_INSECTS_GROUP_ID,EN_LANG_GROUP_ID,AgDataStorePickListEntry.EN_WEEDS_GROUP_ID,
	                                                                        W_EN_LANG_GROUP_ID,AgDataStorePickListEntry.EN_DISEASE_GROUP_ID,
									        D_EN_LANG_GROUP_ID,AgDataStorePickListEntry.EN_CROP_GROUP_ID,
									        GROWTH_STAGE_CORN_GROUP_ID,
									        GROWTH_STAGE_SOYBEANS_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,
									        GROWTH_STAGE_OTHER_GROUP_ID,EN_CONDITION_GROUP_ID,
									        5,6,0,7,8,9,10,11,12,
	                                                                       13,14,15,16,17,18,19,20,
	                                                                       21,22,23,24,25,26,27,28,
	                                                                       29,30,31,32,33,34,35,36,
	                                                                       37,38,39,40,
	                                                                       LAN_LIST_GROUP_ID,
	                                                                       BG_LANG_GROUP_ID,
	                                                                       DA_LANG_GROUP_ID,
	                                                                       DE_LANG_GROUP_ID,
	                                                                       ES_LANG_GROUP_ID,
	                                                                       FR_LANG_GROUP_ID,
	                                                                       IT_LANG_GROUP_ID,
	                                                                       NL_LANG_GROUP_ID,
	                                                                       PL_LANG_GROUP_ID,
	                                                                       PT_LANG_GROUP_ID,
	                                                                       RU_LANG_GROUP_ID,
	                                                                       AgDataStorePickListEntry.ZH_LANG_GROUP_ID,
	                                                                      
	                                                                       W_BG_LANG_GROUP_ID,
	                                                                       W_DA_LANG_GROUP_ID,
	                                                                       W_DE_LANG_GROUP_ID,
	                                                                       W_ES_LANG_GROUP_ID,
	                                                                       W_FR_LANG_GROUP_ID,
	                                                                       W_IT_LANG_GROUP_ID,
	                                                                       W_NL_LANG_GROUP_ID,
	                                                                       W_PL_LANG_GROUP_ID,
	                                                                       W_PT_LANG_GROUP_ID,
	                                                                       W_RU_LANG_GROUP_ID,
	                                                                       AgDataStorePickListEntry.W_ZH_LANG_GROUP_ID,
	                                                                       
	                                                                       D_BG_LANG_GROUP_ID,
	                                                                       D_DA_LANG_GROUP_ID,
	                                                                       D_DE_LANG_GROUP_ID,
	                                                                       D_ES_LANG_GROUP_ID,
	                                                                       D_FR_LANG_GROUP_ID,
	                                                                       D_IT_LANG_GROUP_ID,
	                                                                       D_NL_LANG_GROUP_ID,
	                                                                       D_PL_LANG_GROUP_ID,
	                                                                       D_PT_LANG_GROUP_ID,
	                                                                       D_RU_LANG_GROUP_ID,
	                                                                       AgDataStorePickListEntry.D_ZH_LANG_GROUP_ID,
	                                                                       
	                                                                       BG_CONDITION_GROUP_ID,
	                                                                       DA_CONDITION_GROUP_ID,
	                                                                       DE_CONDITION_GROUP_ID,
	                                                                       ES_CONDITION_GROUP_ID,
	                                                                       FR_CONDITION_GROUP_ID,
	                                                                       IT_CONDITION_GROUP_ID,
	                                                                       NL_CONDITION_GROUP_ID,
	                                                                       PL_CONDITION_GROUP_ID,
	                                                                       PT_CONDITION_GROUP_ID,
	                                                                       RU_CONDITION_GROUP_ID,
	                                                                       AgDataStorePickListEntry.ZH_CONDITION_GROUP_ID,
	                                                                       
	                                                                       BG_CORN_GROWTH_GROUP_ID,
	                                                                       DA_CORN_GROWTH_GROUP_ID,
	                                                                       DE_CORN_GROWTH_GROUP_ID,
	                                                                       ES_CORN_GROWTH_GROUP_ID,
	                                                                       FR_CORN_GROWTH_GROUP_ID,
	                                                                       IT_CORN_GROWTH_GROUP_ID,
	                                                                       NL_CORN_GROWTH_GROUP_ID,
	                                                                       PL_CORN_GROWTH_GROUP_ID,
	                                                                       PT_CORN_GROWTH_GROUP_ID,
	                                                                       RU_CORN_GROWTH_GROUP_ID,
	                                                                       AgDataStorePickListEntry.ZH_CORN_GROWTH_GROUP_ID,
	                                                                       
	                                                                       BG_WHEAT_GROWTH_GROUP_ID,
	                                                                       DA_WHEAT_GROWTH_GROUP_ID,
	                                                                       DE_WHEAT_GROWTH_GROUP_ID,
	                                                                       ES_WHEAT_GROWTH_GROUP_ID,
	                                                                       FR_WHEAT_GROWTH_GROUP_ID,
	                                                                       IT_WHEAT_GROWTH_GROUP_ID,
	                                                                       NL_WHEAT_GROWTH_GROUP_ID,
	                                                                       PL_WHEAT_GROWTH_GROUP_ID,
	                                                                       PT_WHEAT_GROWTH_GROUP_ID,
	                                                                       RU_WHEAT_GROWTH_GROUP_ID,
	                                                                       AgDataStorePickListEntry.ZH_WHEAT_GROWTH_GROUP_ID,
	                                                                       
	                                                                       BG_OTHER_GROWTH_GROUP_ID,
	                                                                       DA_OTHER_GROWTH_GROUP_ID,
	                                                                       DE_OTHER_GROWTH_GROUP_ID,
	                                                                       ES_OTHER_GROWTH_GROUP_ID,
	                                                                       FR_OTHER_GROWTH_GROUP_ID,
	                                                                       IT_OTHER_GROWTH_GROUP_ID,
	                                                                       NL_OTHER_GROWTH_GROUP_ID,
	                                                                       PL_OTHER_GROWTH_GROUP_ID,
	                                                                       PT_OTHER_GROWTH_GROUP_ID,
	                                                                       RU_OTHER_GROWTH_GROUP_ID,
	                                                                       AgDataStorePickListEntry.ZH_OTHER_GROWTH_GROUP_ID,
	                                                                       
	                                                                       BG_INSECTS_GROUP_ID,
	                                                                       DA_INSECTS_GROUP_ID,
	                                                                       DE_INSECTS_GROUP_ID,
	                                                                       ES_INSECTS_GROUP_ID,
	                                                                       FR_INSECTS_GROUP_ID,
	                                                                       IT_INSECTS_GROUP_ID,
	                                                                       NL_INSECTS_GROUP_ID,
	                                                                       PL_INSECTS_GROUP_ID,
	                                                                       PT_INSECTS_GROUP_ID,
	                                                                       RU_INSECTS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.ZH_INSECTS_GROUP_ID,
	                                                                       
	                                                                       AgDataStorePickListEntry.BG_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.DA_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.DE_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.ES_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.FR_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.IT_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.NL_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.PL_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.PT_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.RU_WEEDS_GROUP_ID,
	                                                                       AgDataStorePickListEntry.ZH_WEEDS_GROUP_ID,
	                                                              
	                                                                       AgDataStorePickListEntry.BG_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.DA_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.DE_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.ES_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.FR_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.IT_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.NL_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.PL_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.PT_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.RU_DISEASE_GROUP_ID,
                                                                               AgDataStorePickListEntry.ZH_DISEASE_GROUP_ID,
                                                                               
                                                                               AgDataStorePickListEntry.BG_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.DA_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.DE_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.ES_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.FR_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.IT_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.NL_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.PL_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.PT_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.RU_CROP_GROUP_ID,
                                                                               AgDataStorePickListEntry.ZH_CROP_GROUP_ID,
                                                                               
                                                                               AgDataStorePickListEntry.BG_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.DA_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.DE_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.ES_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.FR_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.IT_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.NL_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.PL_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.PT_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.RU_NDVI_CROP_METRIC_GROUP_ID,
                                                                               AgDataStorePickListEntry.ZH_NDVI_CROP_METRIC_GROUP_ID,
                                                                               
                                                                               AgDataStorePickListEntry.BG_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.DA_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.DE_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.ES_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.FR_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.IT_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.NL_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.PL_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.PT_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.RU_NDVI_CROP_IMPERIAL_GROUP_ID,
                                                                               AgDataStorePickListEntry.ZH_NDVI_CROP_IMPERIAL_GROUP_ID
                                                                      
                                                                      
	                                                                       
	                                                                       };

	
	private static final long[] TEMPLATETYPE_TEMPLATENAME_FKID = { 
	    1, 1, 1, 1, 1, 1, 1, 
	    2, 2, 2, 2, 2, 2, 2,
	    3, 3, 3, 3, 3, 3, 3,
	    4, 4, 4, 4, 4, 4, 4, 4 ,4, 
	    5, 5, 5, 
	    1000,
	    6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
	    7,7};
	

	private static final int[] TEMPLATETYPE_TEMPLATENAME_DATATYPE = {
			DATATYPE_STRING, DATATYPE_IMAGE, DATATYPE_PICKLIST, DATATYPE_PICKLIST,DATATYPE_STRINGARRAY, DATATYPE_STRING,DATATYPE_STRING,
			DATATYPE_STRING, DATATYPE_IMAGE, DATATYPE_PICKLIST, DATATYPE_PICKLIST,DATATYPE_STRINGARRAY, DATATYPE_STRING,DATATYPE_STRING,
			DATATYPE_STRING, DATATYPE_IMAGE, DATATYPE_PICKLIST,	DATATYPE_PICKLIST,DATATYPE_STRINGARRAY, DATATYPE_STRING,DATATYPE_STRING,
			DATATYPE_STRING, DATATYPE_IMAGE, DATATYPE_PICKLIST,	DATATYPE_PICKLIST, DATATYPE_STRING, DATATYPE_PICKLIST,DATATYPE_STRINGARRAY,DATATYPE_STRING,DATATYPE_STRING, 
		    DATATYPE_STRING, DATATYPE_IMAGE, DATATYPE_STRINGARRAY,
			DATATYPE_IMAGE ,
			DATATYPE_STRING,DATATYPE_PICKLIST,DATATYPE_STRING,DATATYPE_STRING,DATATYPE_STRING,DATATYPE_STRING,DATATYPE_STRING,DATATYPE_STRING,DATATYPE_STRING,DATATYPE_STRING,
			DATATYPE_PICKLIST,DATATYPE_STRINGARRAY,DATATYPE_STRING,DATATYPE_STRING,DATATYPE_IMAGE, DATATYPE_STRING,DATATYPE_STRING,
			DATATYPE_STRING,  DATATYPE_STRINGARRAY
			};

	private static int[] TEMPLATETYPE_TEMPLATENAME_DATATYPE_LENGTH = {
			DATATYPE_STRING_LENGTH,DATATYPE_IMAGE_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_STRINGARRAY_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,
			DATATYPE_STRING_LENGTH,DATATYPE_IMAGE_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_STRINGARRAY_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,
			DATATYPE_STRING_LENGTH,DATATYPE_IMAGE_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_STRINGARRAY_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,
			DATATYPE_STRING_LENGTH,DATATYPE_IMAGE_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_STRINGARRAY_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,
			DATATYPE_STRING_LENGTH,DATATYPE_IMAGE_LENGTH,DATATYPE_STRINGARRAY_LENGTH,
			DATATYPE_IMAGE_LENGTH,
			DATATYPE_STRING_LENGTH,DATATYPE_PICKLIST_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,
			   DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,
			   DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,
			   DATATYPE_PICKLIST_LENGTH,DATATYPE_STRINGARRAY_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_IMAGE_LENGTH,DATATYPE_STRING_LENGTH,DATATYPE_STRING_LENGTH,
			DATATYPE_STRING_LENGTH,DATATYPE_STRINGARRAY_LENGTH
		
			};

	private static int[] TEMPLATETYPE_TEMPLATENAME_EDITABLE = { 
	    0, 0, 0, 0, 0, 0, 0,
	    0, 0, 0, 0, 0, 0, 0, 
	    0, 0, 0, 0, 0, 0, 0, 
	    0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 0, 0,  
	    0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0,0};

	
	private static int[] TEMPLATETYPE_TEMPLATENAME_ENCODEBLE = { 
        1, 1, 1, 1, 1,0, 0,
        1, 1, 1, 1, 1,0, 0, 
        1, 1, 1, 1, 1, 0, 0,
        1, 1, 1, 1, 1, 1, 1,0, 0,
        1, 1, 1,
        1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1,0, 0,
        1,1};
	
	
	private static int[] TEMPLATE_DISPLAYBLE = { 
        1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 
        1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1,
        1,1};
	
	
	private static String[] TEMPLATETYPE_TEMPLATENAME_DEFAULTVALUE = { 
	        "", "",	"1",       "78", "","","", // insects
			"", "", "82",      "145", "","","", // weeds
			"", "", "149",      "226", "","","", // disease
			"", "", "230",      "311", "0", "316", "","","", // crop condition
			"", "", "", // others
			"" ,//Photo
			"","","0.99","","0.15","55","90","","","","2.45","60","","","","","",//NDVI
			"","0.99"//NDVI Ref
			};
	
	private static String[] TEMPLATETYPE_MIN_VALUE = { 
        "", "", "", "", "","","",// insects
        "", "", "", "", "","","",// weeds
        "", "", "", "", "","","",// disease
        "", "", "", "", "", "", "","","",// crop condition
        "", "", "", // others
        "" ,//Photo
        "","",".0",".0",".0","30","","","","","","","","","","","",//NDVI
        "",".0"//NDVI Ref
        };
	
	
	private static String[] TEMPLATETYPE_MAX_VALUE = { 
        "", "", "", "", "","","",// insects
        "", "", "", "", "","","", // weeds
        "", "", "", "", "","","", // disease
        "", "", "", "", "", "", "","","", // crop condition
        "", "", "",  // others
        "" ,//Photo
        "","",".99",".99",".30","70","","","","","","","","","","","",//NDVI
        "","0.99"//NDVI Ref
        };
	
	private static String[] TEMPLATETYPE_CONDITION = { 
        "", "", String.valueOf(INSERT_LANG_ID), String.valueOf(INSERT_LANG_ID),"", "","",// insects
        "", "", String.valueOf(WEED_LANG_ID), String.valueOf(WEED_LANG_ID),"", "","",// weeds
        "", "", String.valueOf(DISEASE_LANG_ID), String.valueOf(DISEASE_LANG_ID),"", "","",// disease
        "", "", String.valueOf(CROP_LANG_ID)+","+String.valueOf(GROWTH_STAGE_ID), "", "", String.valueOf(CROP_LANG_ID), "","","",// crop condition
        "", "", "", // others
        "" ,//Photo
        "","43","","","","","","","","","35","44","","","","","",//NDVI
        "","" //NDVI Ref
        };
	
	private static String[] TEMPLATE_LAST_ENTER_VALUE = { 
		"", "",	"1",       "78", "","","", // insects
		"", "", "82",      "145", "","","", // weeds
		"", "", "149",      "226", "","","", // disease
		"", "", "230",      "311", "0", "316", "","","", // crop condition
        "", "", "", // others
        "" ,//Photo
        "","","0.99","","0.15","55","90","","","","2.45","60","","","","","",//NDVI
        "","0.99"//NDVI Ref
        };
	public static String[] jobType_name = { JOB_TYPE_BOUNDARY_MAPPING_NAME,
			JOB_TYPE_MAP_SCOUTING_NAME, JOB_TYPE_PHOTO_NAME };

	private static String[] FEATURETYPE_NAME = { FEATURE_TYPE_BOUNDARY_NAME,
			FEATURE_TYPE_POLYGON_NAME, FEATURE_TYPE_PATH_NAME,
			FEATURE_TYPE_POINT_NAME };

	private static final String[] TEMPLATETYPE_TEMPLATENAME_FDTTAG = { 
	    FLAGNAME,__IMAGE, DETAIL, SEVERITY, NOTES,LANGUAGE, REGION,
	    FLAGNAME, __IMAGE,DETAIL, SEVERITY, NOTES,LANGUAGE, REGION, 
	    FLAGNAME,__IMAGE,DETAIL, SEVERITY,NOTES,LANGUAGE, REGION,
	    FLAGNAME,__IMAGE,CROP,GROWTHSTG,PLANTCOUNT, CONDITION,NOTES,LANGUAGE, REGION,
	    FLAGNAME,__IMAGE,NOTES,
	    __IMAGE,
	    FLAGNAME,CROP,NDVI_REF,NDVI_NON_REF,NDVI_SOIL,NDVI_NUE,NDVI_MAX_YIELD,NDVI_RI,NDVI_NRATE,UNIT,CROP_NPER,CONVFACT,NRATEUNIT,YIELDUNIT,__IMAGE,LANGUAGE, REGION,
	    FLAGNAME,NDVI_REF
	    };

	private static final HashMap<String, Integer> GROWTH_STATE_MAPPING_CROP= new HashMap<String, Integer>(); 
	
	private void init(){
	    GROWTH_STATE_MAPPING_CROP.put("Corn", GROWTH_STAGE_CORN_GROUP_ID);
	    GROWTH_STATE_MAPPING_CROP.put("Soybeans", GROWTH_STAGE_SOYBEANS_GROUP_ID);
	    GROWTH_STATE_MAPPING_CROP.put("Wheat", GROWTH_STAGE_WHEAT_GROUP_ID);
	    
	    GROWTH_STATE_MAPPING_CROP.put("Царевица", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Соя", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Пшеница", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("Majs", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Soyabønner", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Hvede", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("Mais", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Sojabohne", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Weizen", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("Maïs", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Soja", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Blé", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("Granturco", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Fagioli di soia", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Frumento", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("Maïs", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Sojaboon", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Tarwe", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("Kukurydza", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Soja", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Pszenica", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("Milho", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Soja", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Trigo", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("Кукуруза", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Соевые бобы", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("Пшеница", GROWTH_STAGE_WHEAT_GROUP_ID);
            
            GROWTH_STATE_MAPPING_CROP.put("玉米", GROWTH_STAGE_CORN_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("大豆", GROWTH_STAGE_SOYBEANS_GROUP_ID);
            GROWTH_STATE_MAPPING_CROP.put("小麦", GROWTH_STAGE_WHEAT_GROUP_ID);
	}
	
	
   private static final int []LANG_GROUP_ID={
	   									EN_LANG_GROUP_ID,	W_EN_LANG_GROUP_ID,	D_EN_LANG_GROUP_ID,EN_CONDITION_GROUP_ID,
	   									BG_LANG_GROUP_ID, 	W_BG_LANG_GROUP_ID,	D_BG_LANG_GROUP_ID,BG_CONDITION_GROUP_ID,
	   									DA_LANG_GROUP_ID, 	W_DA_LANG_GROUP_ID,	D_DA_LANG_GROUP_ID,DA_CONDITION_GROUP_ID,
	   									DE_LANG_GROUP_ID,	W_DE_LANG_GROUP_ID,	D_DE_LANG_GROUP_ID,DE_CONDITION_GROUP_ID,
	   									ES_LANG_GROUP_ID, 	W_ES_LANG_GROUP_ID,	D_ES_LANG_GROUP_ID,ES_CONDITION_GROUP_ID,
	   									FR_LANG_GROUP_ID,	W_FR_LANG_GROUP_ID,	D_FR_LANG_GROUP_ID,FR_CONDITION_GROUP_ID,
	   									IT_LANG_GROUP_ID, 	W_IT_LANG_GROUP_ID,     D_IT_LANG_GROUP_ID,IT_CONDITION_GROUP_ID,
	   									NL_LANG_GROUP_ID,	W_NL_LANG_GROUP_ID,     D_NL_LANG_GROUP_ID,NL_CONDITION_GROUP_ID, 
	   									PL_LANG_GROUP_ID,	W_PL_LANG_GROUP_ID,	D_PL_LANG_GROUP_ID,PL_CONDITION_GROUP_ID,
	   									PT_LANG_GROUP_ID,	W_PT_LANG_GROUP_ID,	D_PT_LANG_GROUP_ID,PT_CONDITION_GROUP_ID,
	   									RU_LANG_GROUP_ID ,	W_RU_LANG_GROUP_ID,	D_RU_LANG_GROUP_ID,RU_CONDITION_GROUP_ID,
	   									AgDataStorePickListEntry.ZH_LANG_GROUP_ID,       AgDataStorePickListEntry.W_ZH_LANG_GROUP_ID,    
	   									AgDataStorePickListEntry.D_ZH_LANG_GROUP_ID,     AgDataStorePickListEntry.ZH_CONDITION_GROUP_ID,
	   									
                                             };
  
	
	
   private static final int []GROWTH_STA_GROUP_ID={
	   EN_CORN_GROWTH_GROUP_ID,EN_WHEAT_GROWTH_GROUP_ID,EN_OTHER_GROWTH_GROUP_ID,
	   BG_CORN_GROWTH_GROUP_ID,BG_WHEAT_GROWTH_GROUP_ID,BG_OTHER_GROWTH_GROUP_ID,
	   DA_CORN_GROWTH_GROUP_ID,DA_WHEAT_GROWTH_GROUP_ID,DA_OTHER_GROWTH_GROUP_ID,
	   DE_CORN_GROWTH_GROUP_ID,DE_WHEAT_GROWTH_GROUP_ID,DE_OTHER_GROWTH_GROUP_ID,
	   ES_CORN_GROWTH_GROUP_ID,ES_WHEAT_GROWTH_GROUP_ID,ES_OTHER_GROWTH_GROUP_ID,
	   FR_CORN_GROWTH_GROUP_ID,FR_WHEAT_GROWTH_GROUP_ID,FR_OTHER_GROWTH_GROUP_ID,
	   IT_CORN_GROWTH_GROUP_ID,IT_WHEAT_GROWTH_GROUP_ID,IT_OTHER_GROWTH_GROUP_ID,
	   NL_CORN_GROWTH_GROUP_ID,NL_WHEAT_GROWTH_GROUP_ID,NL_OTHER_GROWTH_GROUP_ID,
	   PL_CORN_GROWTH_GROUP_ID,PL_WHEAT_GROWTH_GROUP_ID,PL_OTHER_GROWTH_GROUP_ID,
	   PT_CORN_GROWTH_GROUP_ID,PT_WHEAT_GROWTH_GROUP_ID,PT_OTHER_GROWTH_GROUP_ID,
	   RU_CORN_GROWTH_GROUP_ID,RU_WHEAT_GROWTH_GROUP_ID,RU_OTHER_GROWTH_GROUP_ID,
	   AgDataStorePickListEntry.ZH_CORN_GROWTH_GROUP_ID, AgDataStorePickListEntry.ZH_WHEAT_GROWTH_GROUP_ID, AgDataStorePickListEntry.ZH_OTHER_GROWTH_GROUP_ID,
   }; 
   
  
	
   private static final int []GROWTH_STA_MAPPING_ID={
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID,
	   GROWTH_STAGE_CORN_GROUP_ID,GROWTH_STAGE_WHEAT_GROUP_ID,GROWTH_STAGE_OTHER_GROUP_ID
   };  
   
	private FarmWorksContentProvider manager = null;

	public AgDataStoreResources(FarmWorksContentProvider manager) {
		this.manager = manager;
		 init();
	}

	public void insertAllResource() {
		uploadTemplatelist();
		uploadAttributeinfo();
		//uplodPicklistItems();
		uploadJobType();
		uploadFeatureType();
		uploadCropList();
		insertLanguage();
	}

	protected void uploadTemplatelist() {
	    manager.deleteAllTemplateType();
	        
		List<TemplateType> list = new ArrayList<TemplateType>();
		int JOBTYPE[]={JOB_TYPE_BOUNDARY_MAPPING,JOB_TYPE_MAP_SCOUTING};
		
		int GSO_POLYLINE = 0x02;
	    int GSO_POINT = 0x04;
	    int GSO_POLYGON = 0x08;
	    int iFeatureTypeBoundary[]={GSO_POINT};
	    int iFeatureTypeMapScout[]={GSO_POINT,GSO_POLYLINE,GSO_POLYGON};
		TemplateType templateType=null;
		long iPrimaryKey=0;
		for (int iJobType : JOBTYPE) {
			int iFeatureAtype[]= (iJobType == JOB_TYPE_BOUNDARY_MAPPING)?
					iFeatureTypeBoundary:iFeatureTypeMapScout;
					for (int k = 0; k < iFeatureAtype.length; k++) {
						int iFeatureType=iFeatureAtype[k];
						int iLanguageIndex=0;
						for (String stlanguage : LOCALE_LANGUAGES) {
							
							for (int i = 0; i < TEMPLATETYPE_TEMPLATENAME.length; i++) {
								String stTemplateName=TEMPLATETYPE_TEMPLATENAME[i];
								if(iJobType == JOB_TYPE_BOUNDARY_MAPPING ){
									if((stTemplateName.equals(NDVI_FLAG_TYPE) ||
											stTemplateName.equals(NDVI_REF_FLAG_TYPE))){
										continue;
									}
									
								}else if(iJobType == JOB_TYPE_MAP_SCOUTING && iFeatureType ==  GSO_POLYLINE){
									if( !stTemplateName.equals(TEMPLATETYPE_TEMPLATENAME[4]) &&
											!stTemplateName.equals(NDVI_FLAG_TYPE) &&
											!stTemplateName.equals(NDVI_REF_FLAG_TYPE)){
										continue;
									}
								}
								stTemplateName=getTemplateNameString(iLanguageIndex, i);
								 templateType = new TemplateType(iPrimaryKey++,
										(int) TEMPLATETYPE_ID[i],
										stTemplateName,
										TEMPLATETYPE_TEMPLATENAME_TFDTTAG[i], null, 0,iJobType,iFeatureType,stlanguage);
								list.add(templateType);
							}
							iLanguageIndex++;
						}
						
					}
			
			
		}
		

		manager.insertTemplateType(list);

	}
	private String getTemplateNameString(int iLanguageIndex,int i){
		String stTemplateName=null;
		switch (iLanguageIndex) {
		case 0:
			stTemplateName=TEMPLATETYPE_TEMPLATENAME[i];
			break;
		case 1:
			stTemplateName=TEMPLATETYPE_BG_TEMPLATENAME[i];
			break;
		case 2:
			stTemplateName=TEMPLATETYPE_DA_TEMPLATENAME[i];
			break;
		case 3:
			stTemplateName=TEMPLATETYPE_DE_TEMPLATENAME[i];
			break;
		case 4:
			stTemplateName=TEMPLATETYPE_ES_TEMPLATENAME[i];
			break;
		case 5:
			stTemplateName=TEMPLATETYPE_FR_TEMPLATENAME[i];
			break;
		case 6:
			stTemplateName=TEMPLATETYPE_IT_TEMPLATENAME[i];
			break;
		case 7:
			stTemplateName=TEMPLATETYPE_NL_TEMPLATENAME[i];
			break;
		case 8:
			stTemplateName=TEMPLATETYPE_PL_TEMPLATENAME[i];
			break;
		case 9:
			stTemplateName=TEMPLATETYPE_PT_TEMPLATENAME[i];
			break;
		case 10:
			stTemplateName=TEMPLATETYPE_RU_TEMPLATENAME[i];
			break;
			
		case 11:
                   stTemplateName=TEMPLATETYPE_ZH_TEMPLATENAME[i];
                   break;
		default:
			break;
		} 
		return stTemplateName;
	}
	protected void uploadAttributeinfo() {
	    manager.deleteAllAttributeInfo();
	    
		List<AttributeInfoEntity> list = new ArrayList<AttributeInfoEntity>();

		for (int i = 0; i < AgDataStoreResources.TEMPLATETYPE_TEMPLATENAME_DATATYPE.length; i++) {

		    AttributeInfoEntity attributeInfoEntity = new AttributeInfoEntity(
                    (long) i,
                    TEMPLATETYPE_TEMPLATENAME_DATATYPE[i],
                    TEMPLATETYPE_TEMPLATENAME_DEFAULTVALUE[i],
                    TEMPLATETYPE_MIN_VALUE[i],
                    TEMPLATETYPE_MAX_VALUE[i],
                    TEMPLATETYPE_CONDITION[i],
                    TEMPLATE_DISPLAYBLE[i],
                    TEMPLATETYPE_TEMPLATENAME_EDITABLE[i],
                    TEMPLATE_LAST_ENTER_VALUE[i],
                    TEMPLATETYPE_TEMPLATENAME_FDTTAG[i],
                    ATTRIBUTEINFO_NAME[i],
                    TEMPLATETYPE_TEMPLATENAME_DATATYPE_LENGTH[i],
                    TEMPLATETYPE_TEMPLATENAME_ENCODEBLE[i],
                    TEMPLATETYPE_TEMPLATENAME_FKID[i]
                    );
            list.add(attributeInfoEntity);

		}

		manager.insertAttributeInfo(list);
	}

	protected void uplodPicklistItems() {
	    manager.deleteAllPickList();
	    manager.deleteAllMapping();
	    
	    int iMappingID=0;
		List<PickList> list = new ArrayList<PickList>();

		int iAllArributePickListLength=AgDataStoreResources.PICKLIST_ITEMS.length;
		int iToatlFieldMappingForPickList=ATTRIBUTEINFO_NAME_MAPPING_PICKLIST_FIELD_INDEX.length;
		if(iAllArributePickListLength != iToatlFieldMappingForPickList){
		    throw new IllegalAccessError("Mismatch length of pick list and picklist mapping length");
		}
		int iToatlFieldMappingForGroupIDMap=ATTRIBU_MAPPING_PICKLIST_FIELD_GROUP_ID_INDEX.length;
		if(iAllArributePickListLength != iToatlFieldMappingForGroupIDMap){
            throw new IllegalAccessError("Mismatch length of pick list and picklist groupid mapping length");
        }
		long lAutoIncrementKey=0;
		long lNDVIcropStartID=-1;
		long lNDVI_NPer_StartID=-1;
		int iLangGroupIndex=0;
		int iGrowthStageGroupIndex=0;
		int iDetailsPickList=0;
		for (int i = 0; i <iAllArributePickListLength ; i++) {

		    for (int j = 0; j < AgDataStoreResources.PICKLIST_ITEMS[i].length; j++) {
		    String stPickListItem=AgDataStoreResources.PICKLIST_ITEMS[i][j];
		    long pickListFieldID=ATTRIBUTEINFO_NAME_MAPPING_PICKLIST_FIELD_INDEX[i];
		    
		    if( pickListFieldID == CROP_ID ){
			    	if(GROWTH_STATE_MAPPING_CROP.containsKey(stPickListItem)){
			    		Integer groupID= GROWTH_STATE_MAPPING_CROP.get(stPickListItem);
				        if(groupID != null){
				        	int nextMappingID=groupID;
				        	if(nextMappingID == GROWTH_STAGE_SOYBEANS_GROUP_ID){
				        		// no mapping for soybeans
				        		nextMappingID=0;
				        	}
				            Mapping mapping = new Mapping((long)iMappingID++, GROWTH_STAGE_ID,(int)lAutoIncrementKey , groupID, 1,0,nextMappingID );
				            manager.insertMapping(mapping);
				        }
			    	}else{
			        	 Mapping mapping = new Mapping((long)iMappingID++, GROWTH_STAGE_ID,(int)lAutoIncrementKey , GROWTH_STAGE_OTHER_GROUP_ID, 1,0,GROWTH_STAGE_OTHER_GROUP_ID);
				         manager.insertMapping(mapping);
			    	}
		    }
		    //store for later access in Mapping table
		    if(lNDVIcropStartID == -1 && pickListFieldID == NDVI_CROP_ID){
		        lNDVIcropStartID=lAutoIncrementKey;
		    }else  if(lNDVI_NPer_StartID == -1 && pickListFieldID == NDVI_NPER_ID){
		        lNDVI_NPer_StartID=lAutoIncrementKey;
                    }
		    
		    
		    long lGroupID=ATTRIBU_MAPPING_PICKLIST_FIELD_GROUP_ID_INDEX[i];
		    if(pickListFieldID == NDVI_UNIT_MAP_ID){
		        if(j == 0){
		            Mapping mapping = new Mapping((long)iMappingID++, NDVI_UNIT_MAP_ID,(int)lAutoIncrementKey , 5, 1,0,0);
                            manager.insertMapping(mapping);
		        }else if(j == 1){
		            Mapping mapping = new Mapping((long)iMappingID++, NDVI_UNIT_MAP_ID,(int)lAutoIncrementKey , 6, 1,0,0);
                            manager.insertMapping(mapping);
		        }
		    }else if(pickListFieldID == NDVI_NPER_ID){
		        Mapping mapping = new Mapping((long)iMappingID++, NDVI_CROP_ID,(int)lNDVIcropStartID++ ,(int) lGroupID, 1,0,0);
		        
                        manager.insertMapping(mapping);
		    }else if(pickListFieldID == NDVI_CONV_ID){
                         Mapping mapping = new Mapping((long)iMappingID++, NDVI_NPER_ID,(int)lNDVI_NPer_StartID++ ,(int) lGroupID, 1,0,0);
                         manager.insertMapping(mapping);
                  }else if(lGroupID == LAN_LIST_GROUP_ID){
            	int [] SERVERITY_LIST={INSERT_LANG_ID,WEED_LANG_ID,DISEASE_LANG_ID};
            	
            	for (int iServerity : SERVERITY_LIST) {
            		Mapping mapping = new Mapping((long)iMappingID++, iServerity,(int)lAutoIncrementKey ,(int) LANG_GROUP_ID[iLangGroupIndex++], 1,0,0);
                    manager.insertMapping(mapping);
               }
            	Mapping mapping = new Mapping((long)iMappingID++, CROP_LANG_ID,(int)lAutoIncrementKey ,(int) LANG_GROUP_ID[iLangGroupIndex++], 1,0,0);
                manager.insertMapping(mapping);
                final int GROWTH_STAGE_SUB_COUNT=3;
                for(int index=0;index < GROWTH_STAGE_SUB_COUNT;index++){
                	int iMappingid=(int)GROWTH_STA_MAPPING_ID[iGrowthStageGroupIndex];
                	mapping = new Mapping((long)iMappingID++, CROP_LANG_ID,(int)lAutoIncrementKey , GROWTH_STA_GROUP_ID[iGrowthStageGroupIndex], 2,iMappingid,0);
                	iGrowthStageGroupIndex++;
                	manager.insertMapping(mapping);
                }
            
                int [] DETAILS_ATTR_ID_LIST={INSERT_LANG_ID,WEED_LANG_ID,DISEASE_LANG_ID,CROP_LANG_ID};
                
                for (int attributeIDlanguage : DETAILS_ATTR_ID_LIST) {
                    mapping = new Mapping((long)iMappingID++, 
                         attributeIDlanguage,(int)lAutoIncrementKey ,
                         (int) AgDataStorePickListEntry.DETAILS_PICKLIST_GROUP_ID[iDetailsPickList++],
                         1,0,0);
                   manager.insertMapping(mapping);
                }
               
            }
		     if(lGroupID >= AgDataStorePickListEntry.BG_NDVI_CROP_METRIC_GROUP_ID &&
		           lGroupID <= AgDataStorePickListEntry.ZH_NDVI_CROP_METRIC_GROUP_ID ){
		       Mapping mapping = new Mapping((long)iMappingID++, NDVI_CROP_ID,(int)lAutoIncrementKey ,(int) j+7, 1,0,0);
                        
                        manager.insertMapping(mapping);
		     }
		     
		     if(lGroupID >= AgDataStorePickListEntry.BG_NDVI_CROP_IMPERIAL_GROUP_ID &&
                           lGroupID <= AgDataStorePickListEntry.ZH_NDVI_CROP_IMPERIAL_GROUP_ID ){
                       Mapping mapping = new Mapping((long)iMappingID++, NDVI_CROP_ID,(int)lAutoIncrementKey ,(int) j+16, 1,0,0);
                        
                        manager.insertMapping(mapping);
                     }
		    
			PickList pickList = new PickList( lAutoIncrementKey++,
			        stPickListItem,
					stPickListItem,
					String.valueOf(lGroupID),
					pickListFieldID);
			list.add(pickList);
		    }
		}

		manager.insertPicklistTxn(list);
	}

	protected void uploadJobType() {
	    manager.deleteAllJobType();
	    
		List<JobType> list = new ArrayList<JobType>();
		for (int i = 0; i < AgDataStoreResources.jobType_name.length; i++) {
			JobType jobType = new JobType((long) i + 1,
					AgDataStoreResources.jobType_name[i], 0);
			list.add(jobType);
		}
		manager.insertjobTypeTxn(list);
	}

	protected void uploadFeatureType() {
	    manager.deleteAllFeatureType();
	    
		List<FeatureType> list = new ArrayList<FeatureType>();
		for (int i = 0; i < AgDataStoreResources.FEATURETYPE_NAME.length; i++) {
			FeatureType featureType = new FeatureType((long) i + 1,
					AgDataStoreResources.FEATURETYPE_NAME[i], 0);
			list.add(featureType);
		}
		manager.insertFeatureType(list);
	}

	protected void uploadCropList() {
	    manager.deleteAllCropList();
	    
		List<Crop> list = new ArrayList<Crop>();
		for (int i = 0; i < AgDataStorePickListEntry.CEEF_CROP_PICK_LIST.length; i++) {
			Crop crop = new Crop((long) i + 1,  AgDataStorePickListEntry.CEEF_CROP_PICK_LIST[i], false, "", 0, -1L);
			list.add(crop);
		}
		manager.insertCropList(list);
	}
	
	protected void insertLanguage() {
        manager.deleteAllLanguage();
        
        List<Language> list = new ArrayList<Language>();
        for (int i = 0; i < AgDataStoreResources.LOCALE_LANGUAGES.length; i++) {
            long id=1+i;
            Language language = new Language(id , AgDataStoreResources.LOCALE_LANGUAGES[i]);
            list.add(language);
        }
        manager.insertLanguageList(list);
    }

	

}
