/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name: ATSDBGen
 *
 * Module Name: com.trimble.ag.ats
 *
 * File name: ATSDbGenModule.java
 *
 * Author: sprabhu
 *
 * Created On: 27-Oct-2015 10:58:09 pm
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.ats;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

import java.io.File;

/**
 * @author sprabhu
 *
 */
public class ATSDbGenModule {

   /**
    *
    */
   public ATSDbGenModule() {

   }

   public static final int SCHEMA_VERSION = 1;

   public static void main(String[] args) throws Exception {
      atsDBGen();

   }

   private static void atsDBGen() throws Exception {

      try {
         final String Package_DAO = "com.trimble.ag.ats.dao";
         final String Package_Entity = "com.trimble.ag.ats.entity";
         Schema schema = new Schema(SCHEMA_VERSION, Package_Entity);
         schema.enableKeepSectionsByDefault();
         schema.setDefaultJavaPackageDao(Package_DAO);
         addEntity_Location(schema);
         addEntity_Settings(schema);
         Entity organzation = addEntity_Organzation(schema);
         Entity user = addEntity_User(schema, organzation);
         addEntity_User_Organization(schema, user, organzation);
         final DaoGenerator  daoGenerator = new DaoGenerator();
         daoGenerator.generateAll(schema, ".."+File.separator+"ATSLocationService"+File.separator+"src"+File.separator);
      } finally {

      }
   }
   private static Entity addEntity_Organzation(Schema schema) {
      Entity organization = schema.addEntity("Organization");
      organization.addStringProperty("orgId").primaryKey();
      organization.addStringProperty("name");
      organization.addBooleanProperty("isPrimaryOrg");
      return organization;
}

private static Entity addEntity_User(Schema schema, Entity organization) {

      Entity user = schema.addEntity("User");
      user.addLongProperty("UserId").primaryKey().autoincrement();
      user.addLongProperty("serverUserId");
      user.addStringProperty("userName");
      user.addStringProperty("firstName");
      user.addStringProperty("lastName");
      user.addStringProperty("contactID");
      user.addStringProperty("region");
      user.addBooleanProperty("minitourshown");
      user.addStringProperty("orgId").notNull();

      return user;
}
private static Entity addEntity_User_Organization(Schema schema,
      Entity user, Entity organization) {

Entity user_organization = schema.addEntity("User_Organization");
user_organization.addIdProperty().primaryKey().autoincrement();
user_organization.addIntProperty("serverMode");

Property orgId = user_organization.addStringProperty("orgId").notNull()
              .getProperty();
user_organization.addToOne(organization, orgId);

Property userId = user_organization.addLongProperty("userId").notNull()
              .getProperty();
user_organization.addToOne(user, userId);

user.addToMany(user_organization, userId);
organization.addToMany(user_organization, orgId);

return user_organization;
}
   private static Entity addEntity_Location(Schema schema) {
      Entity organization = schema.addEntity("Location");
      organization.addLongProperty("locationId").primaryKey();
      organization.addDoubleProperty("latitude");
      organization.addDoubleProperty("longtitude");
      organization.addDoubleProperty("speed");
      organization.addDoubleProperty("heading");
      organization.addLongProperty("time");
      organization.addBooleanProperty("isSynced");
      organization.addStringProperty("orgId");
      organization.addDoubleProperty("altitude");
      return organization;
   }
   private static Entity addEntity_Settings(Schema schema) {
      Entity settings = schema.addEntity("Settings");
      settings.addStringProperty("settingsKey").primaryKey();
      settings.addStringProperty("settingsValue");
      settings.addLongProperty("Type");






      return settings;
   }
}
