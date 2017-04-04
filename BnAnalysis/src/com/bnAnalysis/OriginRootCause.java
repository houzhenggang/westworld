package com.bnAnalysis;

import java.util.List;

import com.application.RootCause;
import com.basic.Pair;

public class OriginRootCause {

	public OriginRootCause() {
		
	}
	
	public void run(String var) throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
	    RootCause rootCause = new RootCause(var, netica);
	    
		List<Pair<String, Double>> rank = rootCause.causeRank;
		System.out.println("\nhttp_times������");
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		
		netica.finalize();
	}
}
