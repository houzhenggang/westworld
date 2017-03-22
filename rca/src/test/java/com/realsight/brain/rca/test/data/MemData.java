package com.realsight.brain.rca.test.data;

import java.io.IOException;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.realsight.brain.rca.db.AppDataManager;
import com.realsight.brain.rca.util.Settings;

public class MemData implements Runnable{
	
	private static final Logger logger = LogManager.getLogger(MemData.class.getName());
	
	public MemData() {}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String jm_pre = Settings.getInstance().get("rca.appurl");
		String jm_uri = jm_pre + "monitoring?part=lastValue&graph=usedMemory";
		System.out.println(jm_uri);
		ClientResource jm_client = new ClientResource(jm_uri);
		String value = null;
		try {
			value = jm_client.get().getText().toString();
		} catch (ResourceException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Long timestamp = Calendar.getInstance().getTimeInMillis(); 
		logger.info("memory " + timestamp + " " + value);
		String sql = String.format("INSERT IGNORE INTO "
	    		+ "mem (ts,value) "
	    		+ "VALUES(%1$d,'%2$f'); ", 
	    		timestamp, Double.parseDouble(value));
	    AppDataManager.getInstance().execute(sql);
	}
}