package com.realsight.westworld.bnanalysis.service;

import com.realsight.westworld.bnanalysis.algorithm.Pearson;

public class Relation {

	public void getPearson() throws Exception {
		Pearson pearson = new Pearson();
	}
	
	public void getEntropy() {
		
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("ִ��pearson����Է�����");
		
		Pearson pearson = new Pearson();
		
		pearson.writeMatrixCSV();
		pearson.normal.writeCSV();
	}
}
