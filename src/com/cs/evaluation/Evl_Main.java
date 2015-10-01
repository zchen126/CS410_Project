package com.cs.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evl_Main {

	/**
	 * @param args
	 * @author SeleenaJ
	 * As to Relevance, we use MAP to evaluate the performance.
	 */
	public static Double getMAP(Map<String, Integer> goldtruth, List<String> predict){
		Double result = 0.0;
		int ith_reldoc = 0;
		int num_reldoc = 0;
		for (String dkey : goldtruth.keySet()){
			if (goldtruth.get(dkey) == 1)
				num_reldoc ++;
		}
		System.out.println("Num of relevant docs: " + num_reldoc);
		
		for (int i = 0 ; i < num_reldoc ; i ++){
			String doc_id = predict.get(i);
			if (goldtruth.containsKey(doc_id) && goldtruth.get(doc_id) == 1){
				ith_reldoc ++;
				result += 1.0 * ith_reldoc/(i+1);
			}
			
		}
		
		result = result / num_reldoc;
		
		return result;
	}
	
	public static Map<String, Integer> Read_Gold (String filepath){
		Map<String, Integer> gold = new HashMap<String, Integer>();
		File f = new File(filepath);
		try 
        {
            InputStream is = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
   		 	
	    	 while((line = reader.readLine()) != null) {
	    		 if (line.contains("Doc_ID"))
	    			 continue;
	    		 String[] temp = line.split(",");
	    		 gold.put(temp[0], Integer.parseInt(temp[1]));
	    	 }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
		
		
		return gold;
	}
	
	public static List<String> Read_Predict (String filepath){
		List<String> predict = new ArrayList<String>();
		
		File f = new File(filepath);
		try 
        {
            InputStream is = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
   		 	
	    	 while((line = reader.readLine()) != null) {
	    		 if (line.contains("Doc_ID"))
	    			 continue;
	    		 String[] temp = line.split(",");
	    		 predict.add(temp[0]);
	    	 }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
		
		return predict;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String gold_path = "f:/cs410data/Gold_THILI.csv";
		
		//When you generate csv file for retrieved results, you need to sort them in excel file and then provide the path to here.
		String predict_path = "f:/cs410data/Baseline_BM25.csv";
		
		Map<String, Integer> gold = Read_Gold(gold_path);
		List<String> predict = Read_Predict(predict_path);
		
		Double MAP = getMAP(gold, predict);
		
		System.out.println(MAP);
	}

}
