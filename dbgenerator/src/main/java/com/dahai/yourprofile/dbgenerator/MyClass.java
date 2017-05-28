package com.dahai.yourprofile.dbgenerator;
import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class MyClass {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "com.dahai.yourprofile.models");

        addProfile(schema);

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }

    private static void addProfile(Schema schema) {
        Entity profile = schema.addEntity("Profile");
        profile.addIdProperty();
        profile.addStringProperty("title").notNull();
        profile.addShortProperty("startHour");
        profile.addShortProperty("startMinute");
        profile.addShortProperty("startTotalMinute");
        profile.addIntProperty("daysOfWeek");
        profile.addBooleanProperty("enabled");
        profile.addIntProperty("audioModel");

    }
}
