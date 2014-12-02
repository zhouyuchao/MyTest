package com.zsuper.mytest.upgrade;

import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

public class PropertiesUtil {
    public static String getProperties(Context context, String fileName, String key,
            String defaultValue) {
        Properties props = new Properties();
        try {
            InputStream in = context.getAssets().open(fileName);
            props.load(in);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return props.getProperty(key, defaultValue);
    }
}
