package com.bnAnalysis;

import java.util.List;

import norsys.netica.Node;
import norsys.netica.Streamer;

public class SimuCPT {
	
	// �˴�����simu.dne�Ѿ����ڣ���Ҫ��Ҫд����⺯������������ҪĿ��������дһ��ģ����ѹ��ķֲ���.dne�ļ�

	public SimuCPT() {}
	
	public void getSimuCPT(String simu_var) throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
		
		List<Double> list_orig_dist = netica.getDistribution(simu_var);
		
		netica.finalize();
		
		netica.loadSimuNet();
		
		List<Double> list_simu_dist = netica.getDistribution(simu_var);
		
		System.out.println("ԭ�еķֲ���");
		
		for (int i = 0; i < list_orig_dist.size(); i++) {
			System.out.print(list_orig_dist.get(i)+"\t");
		}
		
		System.out.println("\n��ѹ��ķֲ���");
		
		for (int i = 0; i < list_simu_dist.size(); i++) {
			System.out.print(list_simu_dist.get(i)+"\t");
		}
		
		netica.finalize();
	}
	
	public void setSimuCPT(String var) throws Exception {
		
		NeticaApi netica = new NeticaApi();
		
		netica.loadSimuNet();
		List<Double> list_simu_dist = netica.getDistribution(var);
		
		netica.finalize();
		
		netica.loadNet();
		
		netica.loadRangeMap();
		
		System.out.println(netica.rangeMap);
		
		List<Double> list_orig_dist = netica.getDistribution(var);
		
		Node node = netica.net.getNode(var);
		float[] cpt = node.getCPTable(null);
		
		
		List<String> parents = netica.getParents(node.toString());
		int n = cpt.length;
		
		float[] cpt2 = new float[n];
		
		int row = 1;
		
		for (int i = 0; i < parents.size(); i++) {
			System.out.println(parents.get(i));
			
			row *= netica.rangeMap.get(parents.get(i)).length;
		}
		
		float[] sum = new float[row];
		
		for (int i = 0; i < row; i++) {
			sum[i] = 0;
		}
		int m = n/row;
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < m; j++) {
				sum[i] += cpt[i*m+j] * list_simu_dist.get(j) / list_orig_dist.get(j);
			}
		}
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < m; j++) {
				cpt2[i*m+j] = (float) (cpt[i*m+j] * list_simu_dist.get(j) / list_orig_dist.get(j)/sum[i]);
			}
		}
		
		System.out.println("cpt.length = " + cpt.length);
		System.out.println("cpt2.length= " + cpt2.length);
		
		for (int i = 0; i < cpt.length; i++) {
			System.out.print(cpt[i] + "\t");
		}
		System.out.println();
		
		for (int i = 0; i < cpt2.length; i++) {
			System.out.print(cpt2[i] + "\t");
		}
		System.out.println();
		
		node.deleteTables();
		node.setCPTable(cpt2);
		
		netica.net.write(new Streamer("netica_out_dir/Simu_CPT.dne"));
		
		System.out.println("�����д");
		
		netica.finalize();
		
	}
	
	public NeticaApi loadSimuCPT() throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.loadNet("netica_out_dir/Simu_CPT.dne");
		return netica;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		SimuCPT simuCPT = new SimuCPT();
		simuCPT.getSimuCPT("session_count");
		
	}
	
}
