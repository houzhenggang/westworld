package com.realsight.westworld.bnanalysis.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.opencsv.CSVWriter;

public class WriteCSV {

	public WriteCSV() {
		
	}
	
	public void writeMapList(List<String[]> mapList, String[] attrList, String dir, String file) throws IOException {
		/*** ������� ***/
		System.out.println("���" + dir + "/" + file);
		
		File file_write = new File(dir + "/" + file);
		Writer writer = new FileWriter(file_write);
		CSVWriter csvWriter = new CSVWriter(writer, ',');
		
		for (int i = 0; i < mapList.size(); i++) {
			String[] stmp = new String[mapList.get(i).length+1];
			stmp[0] = attrList[i];
			for (int j = 0; j < mapList.get(i).length; j++) {
				stmp[j+1] = mapList.get(i)[j];
			}
			csvWriter.writeNext(stmp);
		}
		csvWriter.close();
	}
	
	public void writeMatrixCSV(Double[][] matrix, String[] attrList, String dir, String file) throws IOException {
		int numAttr = attrList.length;
		
		/***��������***/
		System.out.println("���" + dir + "/" + file);
		
		File file_write = new File(dir+ "/" +file);
        Writer writer = new FileWriter(file_write);
        CSVWriter csvWriter = new CSVWriter(writer, ',');
        
        String[] stmp = new String[numAttr+1];
        stmp[0] = "";
        for (int i = 1; i <= numAttr; i++) {
        	stmp[i] = attrList[i-1];
        }
        csvWriter.writeNext(stmp);
        
		for (int i = 1; i <= numAttr; i++) {
			stmp[0] = attrList[i-1];
			for (int j = 1; j <= numAttr; j++) {
				stmp[j] = Double.toString(matrix[i-1][j-1]);
			}
			csvWriter.writeNext(stmp);
		}
		csvWriter.close();
	}
}
