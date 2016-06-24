package com.trimble.agmantra.filecodec.fop;

import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.AgJob;
import com.trimble.agmantra.entity.Client;
import com.trimble.agmantra.entity.Commodity;
import com.trimble.agmantra.entity.Crop;
import com.trimble.agmantra.entity.Equipments;
import com.trimble.agmantra.entity.Farm;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.entity.Job;
import com.trimble.agmantra.entity.People;
import com.trimble.agmantra.entity.Units;
import java.util.ArrayList;

public class OpListValues {

   public int              iCompleted     = 1;
   public double           dOpWidth       = 0.0;

   public Client           client         = null;
   public Farm             farm           = null;
   public Field            field          = null;
   public Commodity        commodity      = null;
   public Crop             crop           = null;
   public Units            unit           = null;
   public People           people         = null;
   public Job              job            = null;
   public AgJob            agJob          = null;
   public Units            units          = null;
   public Equipments       equipments     = null;

   public ArrayList<Units> unitList       = null;

   public int              iTemplateType  = 0;
   public String           stTempTypeName = "";

   // Phase 2
   public String           stUnitDesc     = "";
   
   public String           stProjId      = "";

   public OpListValues(Client client, Farm farm, Field field,
         Commodity commodity, Crop crop, Units unit, People people, Job job,
         AgJob agJob, double dOpWidth, int iTemplateType,String stPrjName) {
      this.client = client;
      this.farm = farm;
      this.field = field;
      this.commodity = commodity;
      this.crop = crop;
      this.unit = unit;
      this.people = people;
      this.job = job;
      this.agJob = agJob;
      this.dOpWidth = dOpWidth;
      this.iTemplateType = iTemplateType;

      if (iTemplateType == AgDataStoreResources.ATT_TYPE_IMAGE) {
         stTempTypeName = AgDataStoreResources.PHOTO; 
      } else {
         stTempTypeName = AgDataStoreResources.TEMPLATETYPE_TEMPLATENAME_TFDTTAG[iTemplateType - 1];
      }
   }

}
