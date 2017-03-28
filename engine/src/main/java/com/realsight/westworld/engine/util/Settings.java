package com.realsight.westworld.engine.util;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author shizifan
 * @date 16/8/2
 */


public class Settings {
    private static Settings settings;
    private Properties props = null;

    private Settings(){}

    public static Settings getInstance(){
        if (settings == null){
            settings = new Settings();
            settings.init("conf/brain.settings");
        }
        return settings;
    }

    private void init(String conf_path) {
        props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(conf_path);
            props.load(fis);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String get(String key){
        return props.getProperty(key);
    }

}