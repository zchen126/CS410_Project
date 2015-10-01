package com.cs.lucene;

import java.util.Map;

public class Improved_BM25 {
	
	public static Double BM25 (Double landa, Double k1, Double b, Double k3, Integer Avg_d, Integer r_d, Map<String, Double[]> r_tweight, Map<String, Double>q_weight){
		Double result = 0.0;
		
		for (String word : q_weight.keySet()){
			
			if (r_tweight.containsKey(word)){
				Double tf_q = q_weight.get(word);
				Double tf_c = r_tweight.get(word)[0];
				Double tf_d = r_tweight.get(word)[1];
				Double idf = r_tweight.get(word)[2];
				
				
				Double w_BM_tf_d = landa * (k1+1) * tf_d / (k1*((1-b) + b * (r_d / Avg_d)) + tf_d);
				Double w_BM_tf_c = (1-landa) * (k1 + 1) * tf_c / (k1 + tf_c);
				Double w_BM_tf_q = (k3 + 1) * tf_q / (k3 + tf_q);
				Double w_BM25 = idf * (w_BM_tf_d + w_BM_tf_c)* w_BM_tf_q;
				result += w_BM25;
			}
		}
		
		return result;
	}
	

}
