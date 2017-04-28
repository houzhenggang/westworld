package com.realsight.westworld.bnanalysis.algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.realsight.westworld.bnanalysis.basic.Normalize;
import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.io.ReadFile;
import com.realsight.westworld.bnanalysis.io.WriteCSV;

public class Pearson {
	public Double[][] pearsonMatrix;
	public Normalize normal;
	
	public Pearson() throws Exception {       // read the default file "read.csv";
		getPearsonMatrix("read.csv");
	}
	
	public Pearson(String original_csv) throws Exception {     // read the file original_csv;
		getPearsonMatrix(original_csv);
	}
	
	private  double pairScore(int v1, int v2) {
		
		List<ArrayList<Double>> data = normal.normalizedData;
		
		int numAttr = data.size();
		if (numAttr == 0) {
			System.out.println("There is no data in in_data!!!");
			return 0;
		}
		int numInst = data.get(0).size();
		int N = numInst;
		
		
		double sumX = 0.0;
		double sumY = 0.0;
		double sumX_Sq = 0.0;
		double sumY_Sq = 0.0;
		double sumXY = 0.0;
		
		
		for (int i = 0; i < numInst; i++) {
			sumX += data.get(v1).get(i);
			sumY += data.get(v2).get(i);
			sumX_Sq += Math.pow(data.get(v1).get(i), 2);
			sumY_Sq += Math.pow(data.get(v2).get(i), 2);
			sumXY += data.get(v1).get(i) * data.get(v2).get(i);
		}
		
		double numerator = sumXY - sumX * sumY / N;
		double denominator = Math.sqrt((sumX_Sq - sumX * sumX / N) * (sumY_Sq - sumY * sumY / N));
		
		//��ĸ��Ϊ0
		if (denominator < 0.00000001)
			return 0;
		else
			return numerator/denominator;
	}
	
	private void getPearsonMatrix(String original_csv) throws Exception {
		
		System.out.println("========================\npearson����ԣ�\n");
		
		File outfile = new File("pearson_out_dir");
		if(outfile.exists()) {
            System.out.println("Ŀ���ļ��Ѵ��ڣ�");
            String[] file_list = outfile.list();
            for (int i = 0; i < file_list.length; i++) {
            	File delfile = new File(outfile+"/"+file_list[i]);
            	delfile.delete();  
                System.out.println("��ɾ��" + file_list[i]);  
            }
        }
		else {
			outfile.mkdir();
			System.out.println("����Ŀ¼�ɹ���");
		}
		
		normal = new Normalize(original_csv);
		
		int numAttr = normal.normalizedData.size();
		
		pearsonMatrix = new Double[numAttr][numAttr];
		for (int i = 0; i < numAttr; i++) {
			for (int j =0; j < numAttr; j++) {
				pearsonMatrix[i][j] = pairScore(i, j);
			}
		}
	}
	
	public Pair<ArrayList<Pair<String, Double>>, ArrayList<Pair<String, Double>>> getRelationRanking(int attr) {
		Pair<ArrayList<Pair<String, Double>>, ArrayList<Pair<String, Double>>> pair = new Pair<ArrayList<Pair<String, Double>>, ArrayList<Pair<String, Double>>>();
		Map<Double, String> posMap = new TreeMap<Double, String> ();
		Map<Double, String> negMap = new TreeMap<Double, String> ();
		String[] attrList = normal.readFile.attrList;
		int numAttr = attrList.length;
		for (int i = 0; i < numAttr; i++) {
			if (i == attr)
				continue;
			if (pearsonMatrix[attr][i] > 0)
				posMap.put(-pearsonMatrix[attr][i], attrList[i]);
			else
				negMap.put(pearsonMatrix[attr][i], attrList[i]);
		}
		
		ArrayList<Pair<String, Double>> list1 = new ArrayList<Pair<String, Double>>();
		
		//System.out.println("\n��" + attrList[attr] + "����أ�");
		for (Double it : posMap.keySet()) {
			//System.out.println(posMap.get(it) + "\t" + (-it));
			list1.add(new Pair<String, Double> (posMap.get(it), -it));
		}
		
		ArrayList<Pair<String, Double>> list2 = new ArrayList<Pair<String, Double>>();
		
		//System.out.println("\n��" + attrList[attr] +"����أ�");
		for (Double it : negMap.keySet()) {
			//System.out.println(negMap.get(it)+ "\t" + it);
			list1.add(new Pair<String, Double> (negMap.get(it), it));
		}
		pair.first = list1;
		pair.second = list2;
		return pair;
	}
	
	public void writeMatrixCSV() throws Exception {
		WriteCSV writeCSV = new WriteCSV();
		writeCSV.writeMatrixCSV(pearsonMatrix, normal.readFile.attrList, "pearson_out_dir", "pearson.csv");
	}
	
}
