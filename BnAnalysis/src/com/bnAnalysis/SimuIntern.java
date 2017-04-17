package com.bnAnalysis;

import java.util.List;

import com.basic.Pair;
import com.basic.SimuLoad;

public class SimuIntern {

	public SimuIntern() {
		
	}
	
	public void run(String simuStr, String var, float[] likelihood) throws Exception {
		NeticaApi netica = new NeticaApi();
		
		netica.loadNet();
		
//		netica.loadSimuNet();
		
		double res = netica.getExeption(new Pair<String, float[]> (simuStr, likelihood), var, "likelihood");
		netica.finalize();
		
		
		System.out.println("\n��ԭ��ģ���϶�" + simuStr + "��ѹ" + "����, " + var + "��������");
		System.out.println(res);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
