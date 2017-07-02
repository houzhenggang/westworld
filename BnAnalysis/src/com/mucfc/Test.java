package com.mucfc;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class Test {

//	private static Logger logger = Logger.getLogger(Test.class);
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // System.out.println("This is println message.");  

    	Logger logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    	logger.setLevel(Level.WARN);
        // ��¼debug�������Ϣ  
        logger.debug("This is debug message.");  
        // ��¼info�������Ϣ  
        logger.info("This is info message.");  
        // ��¼error�������Ϣ  
        logger.error("This is error message.");  
    }  
}
