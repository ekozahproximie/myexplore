package com.trimble.agmantra.filecodec.fop;

import java.util.List;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.Equipments;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.entity.IconGroup;
import com.trimble.agmantra.entity.People;
import com.trimble.agmantra.entity.Supply;
import com.trimble.agmantra.entity.TankMix;
import com.trimble.agmantra.entity.Units;
import com.trimble.agmantra.utils.Utils;

public class FlsSchema {

   private OpListValues opListVal = null;
   
   private FarmWorksContentProvider mContentProvider=null;

   public FlsSchema(OpListValues opListValues,FarmWorksContentProvider mContentProvider) {

      this.opListVal = opListValues;
      this.mContentProvider=mContentProvider;

   }

   /**
    * 
    * @return
    */
   public String makeFLS() {

      StringBuilder buffer = new StringBuilder();

		
		long iProjId = mContentProvider.getProjectId();
		String stProjName = Utils.getHexaStringFromLong(iProjId);
		buffer.append(String.format(FOPAttribsTag.TAG_FLS_START, stProjName));

      // Include the client list only if the client ID is not 0x80000000
      if (opListVal.client != null && opListVal.client.getId() != null) {
         if (opListVal.client.getId() != FarmWorksContentProvider.D_UNKNOWN_CFFE_ID) {
            buffer.append(makeClientList());
         }
      }
      
      buffer.append(makeCommodityList());
      buffer.append(makeCropList());
      buffer.append(makeEquipmentList(opListVal.equipments));
      
      // Include the farm list only if the farm ID is not 0x80000000
      if (opListVal.farm != null && opListVal.farm.getId() != null) {
         if (opListVal.farm.getId() != FarmWorksContentProvider.D_UNKNOWN_CFFE_ID) {
            buffer.append(makeFarmList());
         }
      }
      // buffer.append(makeUnitList(opListVal.unitList));
      buffer.append(makeFieldList());
      buffer.append(makeUnitList(opListVal.unitList));

      buffer.append(makePeopleList(null));
      buffer.append(makeSupplyList(null));

      if (opListVal.agJob.getJobTypeId() != null
            && opListVal.agJob.getJobType() != null) {

         buffer.append(makeOpTypeList(
               opListVal.agJob.getJobTypeId().toString(), opListVal.agJob
                     .getJobType().toString()));
      }

      buffer.append(makeTankMixList(null));
      buffer.append(makeIconGroupList(null));
      buffer.append(makeSupplyTypeList(null));

      buffer.append(makeChargeUnitList(false));
      buffer.append(makeActionList(false));
      buffer.append(makeGrowthStageList(false));
      buffer.append(makeAppMethodList(false));
      buffer.append(makeSoilConditionList(false));
      buffer.append(makeSoilTypeList(false));
      buffer.append(makeWindDirList(false));
      buffer.append(makeSkyConditionList(false));
      buffer.append(makeNoteTypeList(false));
      buffer.append(makePestList(false));
      buffer.append(makeCarrierList(false));
      buffer.append(makeQuickNoteList(false));

      buffer.append(FOPAttribsTag.TAG_FLS_END);

      return buffer.toString();
   }

   /**
    * 
    * @param clients
    * @return
    */
   private String makeClientList(/* List<Client> clients */) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_CLIENT_LIST_START);

      /*
       * if (clients != null) { for (Client client : clients) { if (client !=
       * null) { buffer.append(makeClient(client.getId().toString(),
       * client.getDesc())); } } }
       */
      if (opListVal.client != null && opListVal.client.getId() != null) {
         if (opListVal.client.getId() != FarmWorksContentProvider.D_UNKNOWN_CFFE_ID) {
            buffer.append(makeClient(
                  Utils.getHexaStringFromLong(opListVal.client.getId()),
                  Utils.getEscapedXMLString(opListVal.client.getDesc())));
         }
      }

      buffer.append(FOPAttribsTag.TAG_CLIENT_LIST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @return
    */
   private String makeClient(String stXID, String stDesc) {
      StringBuilder buffer = new StringBuilder();
      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_CLIENT_START_END);
         return buffer.toString();
      }
      String stClientOpen = String.format(FOPAttribsTag.TAG_CLIENT_OPEN, stXID);

      buffer.append(stClientOpen);
      if (stDesc != null) {
         buffer.append(FOPAttribsTag.TAG_DESC_START);
         buffer.append(stDesc);
         buffer.append(FOPAttribsTag.TAG_DESC_END);

      }
      buffer.append(FOPAttribsTag.TAG_CLIENT_END);

      return buffer.toString();
   }

   /**
    * 
    * @param lstcommodity
    * @return
    */
   private String makeCommodityList(/* List<Commodity> lstcommodity */) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_COMMODTIY_LIST_START);

      /*
       * if (lstcommodity != null && lstcommodity.size() > 0) { for (Commodity
       * commodity : lstcommodity) { if (commodity != null) {
       * buffer.append(makeCommodity(commodity.getId().toString(),
       * commodity.getName(), "", commodity.getUnitConv() .toString(), "", "",
       * "")); } } }
       */

		// if (opListVal.commodity != null) {
		//
		// if (opListVal.commodity.getUnitConv() != null
		// && opListVal.commodity.getId() != null) {
		//
		// String stUnitConv = opListVal.commodity.getUnitConv() != null ?
		// opListVal.commodity
		// .getUnitConv().toString() : "";
		//
		// makeCommodity(Utils.getHexaStringFromLong(opListVal.commodity
		// .getId()), opListVal.commodity.getName(), "",
		// stUnitConv, "", "", "");
		// }
		// }

		if (opListVal.crop != null) {
			boolean isCropNamePresent = opListVal.crop.getDesc()
					.equalsIgnoreCase(FarmWorksContentProvider.UNKNOWN) ? false
					: true;
			if (isCropNamePresent) {
				String stUnitConv = String
						.valueOf(Unit.UNIT_CONVERSION_SQUARE_METER);

				buffer.append(makeCommodity(
						Utils.getHexaStringFromLong(opListVal.crop.getId()),
						Utils.getEscapedXMLString(opListVal.crop.getDesc()),
						String.valueOf(Unit.UNIT_TYPE_ACRE), stUnitConv, "0",
						"0", "0"));
			}
		}

      buffer.append(FOPAttribsTag.TAG_COMMODTIY_LIST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @param stUnitType
    * @param stUnitconv
    * @param stDry_moisture
    * @param stShrink_amount
    * @param stShrink_step
    * @return
    */
   private String makeCommodity(String stXID, String stDesc, String stUnitType,
         String stUnitconv, String stDry_moisture, String stShrink_amount,
         String stShrink_step) {
		StringBuilder buffer = new StringBuilder();

		if (stXID == null) {
			buffer.append(FOPAttribsTag.TAG_COMMODTIY_START_END);
			return buffer.toString();
		}

		String stClientOpen = String.format(FOPAttribsTag.TAG_COMMODTIY_OPEN,
				stXID);

		buffer.append(stClientOpen);
		if (stDesc != null) {
			buffer.append(FOPAttribsTag.TAG_DESC_START);
			buffer.append(stDesc);
			buffer.append(FOPAttribsTag.TAG_DESC_END);

		}
		if (stUnitType != null) {
			buffer.append(FOPAttribsTag.TAG_UNIT_START);
			buffer.append(stUnitType);
			buffer.append(FOPAttribsTag.TAG_UNIT_END);

		}
		if (stUnitconv != null) {
			buffer.append(FOPAttribsTag.TAG_UNIT_CONV_START);
			buffer.append(stUnitconv);
			buffer.append(FOPAttribsTag.TAG_UNIT_CONV_END);

		}

		if (stDry_moisture != null) {
			buffer.append(FOPAttribsTag.TAG_DRY_MOISTR_START);
			buffer.append(stDry_moisture);
			buffer.append(FOPAttribsTag.TAG_DRY_MOISTR_END);

		}

		if (stShrink_amount != null) {
			buffer.append(FOPAttribsTag.TAG_SHRINK_AMT_START);
			buffer.append(stShrink_amount);
			buffer.append(FOPAttribsTag.TAG_SHRINK_AMT_END);

		}

		if (stShrink_step != null) {
			buffer.append(FOPAttribsTag.TAG_SHRINK_STP_START);
			buffer.append(stShrink_step);
			buffer.append(FOPAttribsTag.TAG_SHRINK_STP_END);

		}

      buffer.append(FOPAttribsTag.TAG_COMMODTIY_END);

      return buffer.toString();
   }

   /**
    * 
    * @param crops
    * @return
    */
   private String makeCropList(/* List<Crop> crops */) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_CROP_LIST_START);
      /*
       * if (crops != null && crops.size() > 0) { for (Crop crop : crops) { if
       * (crop != null) { buffer.append(makeCrop(crop.getDesc().toString(),
       * crop.getDesc(), "", crop.getCommodityId().toString())); } } }
       */

      // TODO:: selva to clear this
      if (opListVal.crop != null) {

			boolean isCropNamePresent = opListVal.crop.getDesc()
					.equalsIgnoreCase(FarmWorksContentProvider.UNKNOWN) ? false
					: true;
			if (isCropNamePresent) {

				String stCommoId = Utils.getHexaStringFromLong(opListVal.crop
						.getId());
				String stCropDesc = opListVal.agJob.getCropdesc();
				String[] stSplitCropDesc = stCropDesc.split(" ");
				buffer.append(makeCrop(
						Utils.getHexaStringFromLong(opListVal.crop.getId()),
						opListVal.agJob.getCropdesc(), stSplitCropDesc[0],
						stCommoId));
      }
		}
      buffer.append(FOPAttribsTag.TAG_CROP_LIST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @param stYear
    * @param stcommodityId
    * @return
    */
   private String makeCrop(String stXID, String stDesc, String stYear,
         String stcommodityId) {
      StringBuilder buffer = new StringBuilder();
      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_CROP_START_END);
         return buffer.toString();
      }

      String stCropOpen = String.format(FOPAttribsTag.TAG_CROP_OPEN, stXID);

      buffer.append(stCropOpen);
      if (stDesc != null) {
         buffer.append(FOPAttribsTag.TAG_DESC_START);
         buffer.append(stDesc);
         buffer.append(FOPAttribsTag.TAG_DESC_END);

      }
      if (stYear != null) {
         buffer.append(FOPAttribsTag.TAG_YEAR_START);
         buffer.append(stYear);
         buffer.append(FOPAttribsTag.TAG_YEAR_END);

      }
      if (stcommodityId != null) {
         buffer.append(FOPAttribsTag.TAG_COMMODTIY_START);
         buffer.append(stcommodityId);
         buffer.append(FOPAttribsTag.TAG_COMMODTIY_END);

      }
      buffer.append(FOPAttribsTag.TAG_CROP_END);

      return buffer.toString();
   }

   /**
    * 
    * @param equipemnts
    * @return
    */
   private String makeEquipmentList(Equipments equipemnts) {

      StringBuilder buffer = new StringBuilder();

      if (equipemnts == null) {
         buffer.append(FOPAttribsTag.TAG_EQP_LST_START_END);
         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_EQP_LST_START);

      /*
       * if (equipemnts != null && equipemnts.size() > 0) { for (Equipments
       * equipemnt : equipemnts) { if (equipemnt != null) {
       * buffer.append(makeEquipment("", "")); } } }
       */

      buffer.append(makeEquipment(Utils.getHexaStringFromLong(10), ""));
      buffer.append(FOPAttribsTag.TAG_EQP_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @return
    */
   private String makeEquipment(String stXID, String stDesc) {
      StringBuilder buffer = new StringBuilder();

      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_EQP_START_END);
         return buffer.toString();
      }

      String stEquipOpen = String.format(FOPAttribsTag.TAG_EQP_START, stXID);
      buffer.append(stEquipOpen);

      if (stDesc != null) {
         buffer.append(FOPAttribsTag.TAG_DESC_START);
         buffer.append(stDesc);
         buffer.append(FOPAttribsTag.TAG_DESC_END);

      }

      buffer.append(FOPAttribsTag.TAG_EQP_END);

      return buffer.toString();
   }

   /**
    * 
    * @param farms
    * @return
    */
   private String makeFarmList(/* List<Farm> farms */) {

      StringBuilder buffer = new StringBuilder();

      /*
       * if (farms == null) {
       * buffer.append(FOPAttribsTag.TAG_FARM_LST_START_END);
       * 
       * return buffer.toString(); }
       */

      buffer.append(FOPAttribsTag.TAG_FARM_LST_START);

      /*
       * if (farms != null && farms.size() > 0) { for (Farm farm : farms) { if
       * (farm != null) { buffer.append(makeFarm(farm.getId().toString(),
       * farm.getDesc(), opListVal.field)); } } }
       */
      /*if (opListVal.farm != null && opListVal.farm.getId()!=null) {
         if (opListVal.farm.getId() != FarmWorksContentProvider.D_UNKNOWN_CFFE_ID) {
            buffer.append(makeFarm(
                  Utils.getHexaStringFromLong(opListVal.farm.getId()),
                  opListVal.farm.getDesc(), opListVal.field));
         }
      }*/
      
      if (opListVal != null && opListVal.farm != null && opListVal.farm.getId() != null) {
         int iFarmStatus = opListVal.farm.getStatus();
         long lFarmId = opListVal.farm.getId();
         if (0 != (iFarmStatus & AgDataStoreResources.STATUS_AUTOGENERATED)) {
                 lFarmId = FarmWorksContentProvider.D_UNKNOWN_CFFE_ID;
         }

         if (lFarmId != FarmWorksContentProvider.D_UNKNOWN_CFFE_ID) {

                 buffer.append(makeFarm(Utils.getHexaStringFromLong(lFarmId),
                                 Utils.getEscapedXMLString(opListVal.farm.getDesc()), opListVal.field));
         }
      }

      buffer.append(FOPAttribsTag.TAG_FARM_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @param field
    * @return
    */
   private String makeFarm(String stXID, String stDesc, Field field) {
      StringBuilder buffer = new StringBuilder();

      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_FARM_OPEN);
         return buffer.toString();
      }

      String stFarmOpen = String.format(FOPAttribsTag.TAG_FARM_OPEN, stXID);

      buffer.append(stFarmOpen);
      if (stDesc != null) {
         buffer.append(FOPAttribsTag.TAG_DESC_START);
         buffer.append(stDesc);
         buffer.append(FOPAttribsTag.TAG_DESC_END);

      }

      double dArea = 0;

      if (opListVal.field.getArea() != null) {
         dArea =  Utils.getAcresFromSquareMeter(Double.valueOf(opListVal.field
               .getArea()));
      }

      buffer.append(makeArea(String.valueOf(Unit.UNIT_TYPE_ACRE),
            String.valueOf(dArea), Unit.UNIT_ACRE_SHORT));

      // Include the clientid only if the clientID is not 0x80000000
      String stClientID = Utils.getHexaStringFromLong(opListVal.farm
            .getClientId());
      if (!stClientID.equalsIgnoreCase(Utils
            .getHexaStringFromLong(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID) ) ) {
         buffer.append(makeClientId(Utils.getHexaStringFromLong(opListVal.farm
            .getClientId())));
      }
      buffer.append(FOPAttribsTag.TAG_FARM_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stUnitType
    * @param stAreaVal
    * @param stUnitDesc
    * @return
    */
   private String makeArea(String stUnitType, String stAreaVal,
         String stUnitDesc) {

      StringBuilder buffer = new StringBuilder();

      String stAreaOpen = String
            .format(FOPAttribsTag.TAG_AREA_OPEN, stUnitType);

      buffer.append(stAreaOpen);
      if (stAreaVal != null) {
         buffer.append(stAreaVal);

         buffer.append(FOPAttribsTag.TAG_UNIT_DESC_START);
         buffer.append(stUnitDesc);
         buffer.append(FOPAttribsTag.TAG_UNIT_DESC_END);

      }

      buffer.append(FOPAttribsTag.TAG_AREA_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stClientId
    * @return
    */
   private String makeClientId(String stClientId) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_CLIENT_ID_START);

      if (stClientId != null) {
         buffer.append(stClientId);
      }
      buffer.append(FOPAttribsTag.TAG_CLIENT_ID_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stFarmId
    * @return
    */
   private String makeFarmId(String stFarmId) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_FARM_ID_START);

      if (stFarmId != null) {
         buffer.append(stFarmId);
      }
      buffer.append(FOPAttribsTag.TAG_FARM_ID_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stRevision
    * @param stModified
    * @return
    */
   private String makeBoundry(String stRevision, String stModified) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_BOUNDRY_START);

      if (stRevision != null) {
         buffer.append(FOPAttribsTag.TAG_REVISION_START);
         buffer.append(stRevision);
         buffer.append(FOPAttribsTag.TAG_REVISION_END);

      }
      if (stModified != null) {
         buffer.append(FOPAttribsTag.TAG_MODIFIED_START);
         buffer.append(stModified);
         buffer.append(FOPAttribsTag.TAG_MODIFIED_END);

      }
      buffer.append(FOPAttribsTag.TAG_BOUNDRY_END);

      return buffer.toString();
   }

   /**
    * 
    * @param fields
    * @return
    */
   private String makeFieldList(/* List<Field> fields */) {

      StringBuilder buffer = new StringBuilder();

      /*
       * if (fields == null) {
       * buffer.append(FOPAttribsTag.TAG_FIELD_LST_START_END);
       * 
       * return buffer.toString(); }
       */
      buffer.append(FOPAttribsTag.TAG_FIELD_LST_START);

      /*
       * if (fields != null && fields.size() > 0) { for (Field field : fields) {
       * if (field != null) {
       * 
       * buffer.append(makeField(field.getId().toString(), field .getDesc(),
       * field.getArea(), opListVal.unit, field.getId() .toString(),
       * field.getFarmId().toString(), field .getBoundaryRevision().toString(),
       * field .getBoundaryModified().toString())); } } }
       */
      if (opListVal != null && opListVal.field != null) {

         String stRevision = opListVal.field.getBoundaryRevision() != null ? opListVal.field
               .getBoundaryRevision().toString() : "1";
         String stModified = opListVal.field.getBoundaryModified() != null ? opListVal.field
               .getBoundaryModified().toString() : "1";
         int iFarmStatus = opListVal.farm.getStatus();
         long lFarmId = opListVal.farm.getId();
         if (0 != (iFarmStatus & AgDataStoreResources.STATUS_AUTOGENERATED)) {
                 lFarmId = FarmWorksContentProvider.D_UNKNOWN_CFFE_ID;
         }

         int iClientStatus = opListVal.client.getStatus();
         long lClientId = opListVal.client.getId();
         if (0 != (iClientStatus & AgDataStoreResources.STATUS_AUTOGENERATED)) {
            lClientId = FarmWorksContentProvider.D_UNKNOWN_CFFE_ID;
         }

         buffer.append(makeField(
               Utils.getHexaStringFromLong(opListVal.field.getId()),
               Utils.getEscapedXMLString(opListVal.field.getDesc()), opListVal.field.getArea(),
               opListVal.unit, Utils.getHexaStringFromLong(lClientId),
               Utils.getHexaStringFromLong(lFarmId), stRevision, stModified));
      }

      buffer.append(FOPAttribsTag.TAG_FIELD_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @param stAreaVal
    * @param unit
    * @param stClientId
    * @param stFarmID
    * @param stBndrRevised
    * @param stBndryModified
    * @return
    */
   private String makeField(String stXID, String stDesc, String stAreaVal,
         Units unit, String stClientId, String stFarmID, String stBndrRevised,
         String stBndryModified) {

      StringBuilder buffer = new StringBuilder();

      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_FIELD_START_END);
         return buffer.toString();
      }

      String stFiledOpen = String.format(FOPAttribsTag.TAG_FIELD_OPEN, stXID);

      buffer.append(stFiledOpen);

      if (stDesc != null) {
         buffer.append(FOPAttribsTag.TAG_DESC_START);
         buffer.append(stDesc);
         buffer.append(FOPAttribsTag.TAG_DESC_END);
      }

      double dArea = 0;
      if (opListVal.field.getArea() != null) {
         dArea = Utils.getAcresFromSquareMeter(Double.valueOf(opListVal.field
               .getArea()));
      }

      buffer.append(makeArea(String.valueOf(Unit.UNIT_TYPE_ACRE),
            String.valueOf(dArea), Unit.UNIT_ACRE_SHORT));

      // Do not include the tags clientid and farmid if they are
      // 0x80000000
      if (!stClientId.equalsIgnoreCase(Utils
            .getHexaStringFromLong(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID)))
      buffer.append(makeClientId(stClientId));

      if (!stFarmID.equalsIgnoreCase(Utils
            .getHexaStringFromLong(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID)))
         buffer.append(makeFarmId(stFarmID));

      buffer.append(makeBoundry(stBndrRevised, stBndryModified));

      buffer.append(FOPAttribsTag.TAG_FIELD_END);

      return buffer.toString();
   }

   /**
    * 
    * @param units
    * @return
    */
   private String makeUnitList(List<Units> units) {

      StringBuilder buffer = new StringBuilder();

      // if (units == null) {
      // buffer.append(FOPAttribsTag.TAG_UNIT_LST_START_END);

      // return buffer.toString();
      // }

      buffer.append(FOPAttribsTag.TAG_UNIT_LST_START);

      if (units != null && units.size() > 0) {
         for (Units unit : units) {
            if (unit != null) {
               buffer.append(makeUnit(Constants.ST_EMPTY, Constants.ST_EMPTY,
                     Constants.ST_EMPTY, Constants.ST_EMPTY,
                     Constants.ST_EMPTY, Constants.ST_EMPTY, Constants.ST_EMPTY));
            }
         }
      }

		buffer.append(makeUnit(Unit.UNIT_TYPE_ACRE, Unit.UNIT_ACRE_DESC,
				Unit.UNIT_ACRE_SHORT, Unit.UNIT_ACRE_SINGULAR, Unit.UNIT_TYPE,
				Unit.UNIT_METRIC,
				String.valueOf(Unit.UNIT_CONVERSION_SQUARE_METER)));

      buffer.append(FOPAttribsTag.TAG_UNIT_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @param stShort
    * @param stSingular
    * @param stType
    * @param stMetric
    * @param stUnitConv
    * @return
    */
   private String makeUnit(String stXID, String stDesc, String stShort,
         String stSingular, String stType, String stMetric, String stUnitConv) {

      StringBuilder buffer = new StringBuilder();

      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_UNIT_START_END);
         return buffer.toString();
      }

      String stUnitOpen = String.format(FOPAttribsTag.TAG_UNIT_OPEN, stXID);

      buffer.append(stUnitOpen);

      buffer.append(String.format(FOPAttribsTag.TAG_DESC, stDesc));
      buffer.append(String.format(FOPAttribsTag.TAG_SHORT, stShort));
      buffer.append(String.format(FOPAttribsTag.TAG_SINGULAR, stSingular));
      buffer.append(String.format(FOPAttribsTag.TAG_TYPE, stType));
      buffer.append(String.format(FOPAttribsTag.TAG_METRIC, stMetric));
      buffer.append(String.format(FOPAttribsTag.TAG_UNITCONV, stUnitConv));

		buffer.append(FOPAttribsTag.TAG_SLASH_END);

      return buffer.toString();
   }

   /**
    * 
    * @param peoples
    * @return
    */
   private String makePeopleList(List<People> peoples) {

      StringBuilder buffer = new StringBuilder();

      if (peoples == null) {
         buffer.append(FOPAttribsTag.TAG_PEOPLE_LST_START_END);

         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_PEOPLE_LST_START);

      if (peoples != null && peoples.size() > 0) {
         for (People people : peoples) {
            if (people != null) {
               buffer.append(makePeople("", "", ""));
            }
         }
      }
      buffer.append(FOPAttribsTag.TAG_PEOPLE_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @param stEpa
    * @return
    */
   private String makePeople(String stXID, String stDesc, String stEpa) {

      StringBuilder buffer = new StringBuilder();

      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_PEOPLE_START_END);
         return buffer.toString();
      }

      String stPeopleOpen = String.format(FOPAttribsTag.TAG_PEOPLE_OPEN, stXID);

      buffer.append(stPeopleOpen);

      if (stDesc != null) {
         buffer.append(FOPAttribsTag.TAG_DESC_START);
         buffer.append(stDesc);
         buffer.append(FOPAttribsTag.TAG_DESC_END);
      }

      if (stEpa != null) {
         buffer.append(FOPAttribsTag.TAG_EPA_NUM_START);
         buffer.append(stEpa);
         buffer.append(FOPAttribsTag.TAG_EPA_NUM_END);
      }

      buffer.append(FOPAttribsTag.TAG_UNIT_END);

      return buffer.toString();
   }

   /**
    * 
    * @param supply
    * @return
    */
   private String makeSupplyList(List<Supply> supply) {

      StringBuilder buffer = new StringBuilder();

      if (supply == null) {
         buffer.append(FOPAttribsTag.TAG_SUPPLY_LST_START_END);

         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_SUPPLY_LST_START);

      if (supply != null && supply.size() > 0) {
         for (Supply people : supply) {
            if (people != null) {
               buffer.append(makeSupply());
            }
         }
      }
      buffer.append(FOPAttribsTag.TAG_SUPPLY_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @return
    */
   private String makeSupply() {

      StringBuilder buffer = new StringBuilder();

      return buffer.toString();

   }

   /**
    * 
    * @param tankmixs
    * @return
    */
   private String makeTankMixList(List<TankMix> tankmixs) {

      StringBuilder buffer = new StringBuilder();

      if (tankmixs == null) {
         buffer.append(FOPAttribsTag.TAG_TANK_MIX_LST_START_END);

         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_TANK_MIX_LST_START);

      if (tankmixs != null && tankmixs.size() > 0) {
         for (TankMix tankmix : tankmixs) {
            if (tankmix != null) {
               buffer.append(makeTankMix());
            }
         }
      }
      buffer.append(FOPAttribsTag.TAG_TANK_MIX_LST_END);

      return buffer.toString();
   }

   private String makeTankMix() {
      StringBuilder buffer = new StringBuilder();
      return buffer.toString();
   }

   /**
    * 
    * @param icongroups
    * @return
    */
   private String makeIconGroupList(List<IconGroup> icongroups) {

      StringBuilder buffer = new StringBuilder();

      if (icongroups == null) {
         buffer.append(FOPAttribsTag.TAG_ICON_GRP_LST_START_END);

         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_ICON_GRP_LST_START);

      if (icongroups != null && icongroups.size() > 0) {
         for (IconGroup icongrp : icongroups) {
            if (icongrp != null) {
               buffer.append(makeIconGroup());
            }
         }
      }
      buffer.append(FOPAttribsTag.TAG_ICON_GRP_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @return
    */
   private String makeIconGroup() {
      StringBuilder buffer = new StringBuilder();

      return buffer.toString();

   }

   /**
    * 
    * @param supplys
    * @return
    */
   private String makeSupplyTypeList(List<Supply> supplys) {

      StringBuilder buffer = new StringBuilder();

      if (supplys == null) {
         buffer.append(FOPAttribsTag.TAG_SUPPLY_TYPE_LST_START_END);

         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_SUPPLY_TYPE_LST_START);

      if (supplys != null && supplys.size() > 0) {
         for (Supply supply : supplys) {
            if (supply != null) {

               buffer.append(makeSupplyType());
            }
         }
      }
      buffer.append(FOPAttribsTag.TAG_SUPPLY_TYPE_LST_END);

      return buffer.toString();
   }

   private String makeSupplyType() {
      StringBuilder buffer = new StringBuilder();

      return buffer.toString();

   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @return
    */
   private String makeOpTypeList(String stXID, String stDesc) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_OPTYPE_LST_START);

      /*
       * if (opTypes == null) {
       * buffer.append(FOPAttribsTag.TAG_SUPPLY_TYPE_LST_START_END); return
       * buffer.toString(); }
       * 
       * buffer.append(FOPAttribsTag.TAG_SUPPLY_TYPE_LST_START); if (opTypes !=
       * null && opTypes.size() > 0) { for (OpType opType : opTypes) { if
       * (opType != null) { // buffer.append(makeOpType()); } } }
       * buffer.append(FOPAttribsTag.TAG_SUPPLY_TYPE_LST_END);
       */

      buffer.append(makeOpType(
            Utils.getHexaStringFromLong(opListVal.iTemplateType),
            opListVal.stTempTypeName));
      buffer.append(FOPAttribsTag.TAG_OPTYPE_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stXID
    * @param stDesc
    * @return
    */
   private String makeOpType(String stXID, String stDesc) {

	  //Template type id unified for all types of Scouting jobs. Id is matched with that of 
	  //farm works office.
	  stXID = "0x1C4";
	  stDesc = "Scouting";
      StringBuilder buffer = new StringBuilder();

      String stOpTypeOpen = String.format(FOPAttribsTag.TAG_OPTYPE_OPEN, stXID);
      buffer.append(stOpTypeOpen);
      buffer.append(FOPAttribsTag.ST_SPACE);
      buffer.append(String.format(FOPAttribsTag.TAG_DESC, stDesc));
      buffer.append(FOPAttribsTag.ST_SPACE);
      buffer.append(FOPAttribsTag.TAG_SLASH_END);

      return buffer.toString();

   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeChargeUnitList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_CHRG_UNIT_LST_START_END);

         return buffer.toString();
      }
      /*
       * buffer.append(FOPAttribsTag.TAG_CHRG_UNIT_LST_START); if (chargeUnit !=
       * null && chargeUnit.size() > 0) { for (ChargeUnit chargeunit :
       * chargeunit) { if (chargeunit != null) { // buffer.append(makePeople());
       * } } }
       */
      buffer.append(FOPAttribsTag.TAG_CHRG_UNIT_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeActionList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_ACTION_LST_START_END);

         return buffer.toString();
      }

      /*
       * buffer.append(FOPAttribsTag.TAG_ACTION_LST_START); if (actionLists !=
       * null && actionLists.size() > 0) { for (ActionList actionList :
       * actionLists) { if (actionList != null) { //
       * buffer.append(makePeople()); } } }
       * buffer.append(FOPAttribsTag.TAG_ACTION_LST_END);
       */

      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeGrowthStageList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_GRTH_STAGE_LST_START_END);

         return buffer.toString();
      }

      /*
       * buffer.append(FOPAttribsTag.TAG_GRTH_STAGE_START); if (growthStages !=
       * null && growthStages.size() > 0) { for (GrowthStage gtage :
       * growthStages) { if (gtage != null) {
       * 
       * // buffer.append(makePeople()); } } }
       * buffer.append(FOPAttribsTag.TAG_GRTH_STAGE_END);
       */

      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeAppMethodList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_APP_METHOD_LST_START_END);

         return buffer.toString();
      }

      /*
       * buffer.append(FOPAttribsTag.TAG_APP_METHOD_LST_START); if (AppMethods
       * != null && AppMethods.size() > 0) { for (Appmethod AppMethods :
       * AppMethods) { if (AppMethods != null) { //
       * buffer.append(makeAppMethod()); } } }
       * buffer.append(FOPAttribsTag.TAG_APP_METHOD_LST_END);
       */

      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeSoilConditionList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_SOIL_CONTN_LST_START_END);

         return buffer.toString();
      }
      // Phase - II
      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeSoilTypeList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_SOIL_TYPE_LST_START_END);

         return buffer.toString();
      }
      // Phase - II
      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeWindDirList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_WIND_DIR_LST_START_END);

         return buffer.toString();
      }
      // Phase - II
      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeSkyConditionList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_SKY_CONTN_LST_START_END);

         return buffer.toString();
      }
      // Phase - II
      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeNoteTypeList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_NOTE_TYPE_LST_START_END);

         return buffer.toString();
      }
      // Phase - II
      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makePestList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_PEST_LST_START_END);

         return buffer.toString();
      }
      // Phase - II
      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeCarrierList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_CARRIER_LST_START_END);

         return buffer.toString();
      }
      // Phase - II
      return buffer.toString();
   }

   /**
    * 
    * @param isAvailable
    * @return
    */
   private String makeQuickNoteList(boolean isAvailable) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvailable) {
         buffer.append(FOPAttribsTag.TAG_QCK_NOTE_LST_START_END);

         return buffer.toString();
      }
      // Phase - II
      return buffer.toString();
   }

}
