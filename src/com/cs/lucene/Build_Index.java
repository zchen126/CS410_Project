package com.cs.lucene;

import java.awt.TextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Build_Index {

	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	
	public static void Build_index(String folder_path) throws URISyntaxException, IOException{
		File folder = new File (folder_path);
		File[] listOfFiles = folder.listFiles();
		
		String indexPath = "f:/cs410project/Transcripts_Index/THILI";
		Directory dir = FSDirectory.open(new File(indexPath));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);
		
		
		for (int i = 0; i < listOfFiles.length; i++) {
			  File f = listOfFiles[i]; 
			  if (f.isFile() && f.getName().endsWith(".txt")) {
				  InputStream is;
				    
				    try {
						is = new FileInputStream(f);
						
						 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
					     StringBuffer buffer = new StringBuffer();
					     String line;
					     
					     while((line = reader.readLine()) != null) {
							 buffer.append(line);
					     }
					     
					     if (buffer.length() > 0){
					    	 String text = buffer.toString();
					    	 buffer.delete(0, buffer.length());
					    	 
					    	 TextTokenizer tt = new TextTokenizer(text.toCharArray());
	                  		 text = tt.returnWord(text.length());
	                  		 TextNormalizer tn = new TextNormalizer();
	                  		 text = String.copyValueOf(tn.normalize(text.toCharArray()));
	                  		 
	                  		 Document doc = new Document();
	                  		
	                  		 Field idField = new StringField("doc_id", listOfFiles[i].getName().replaceAll(".txt", ""), Field.Store.YES);
	                  		 doc.add(idField);
	                  		 
	                  		 //Field contentField = new org.apache.lucene.document.TextField("content", text, Field.Store.YES);
	                  		 final FieldType ContentOptions = new FieldType(); 
	                  		 ContentOptions.setIndexed(true); 
	                  		 ContentOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); 
	                  		 ContentOptions.setStored(true); 
	                  		 ContentOptions.setStoreTermVectors(true); 
	                  		 ContentOptions.setTokenized(true);
	                  	
	                  		 Field contentField = new Field("content", text, ContentOptions);
	                  		 doc.add(contentField);
	            			
	            			writer.addDocument(doc);
	                  		 
					     }
				    }catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
			  }
		}
		writer.close();
	}
	
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		// TODO Auto-generated method stub
		
		String folder_path = "f:/cs410data/transctipts";
		Build_index(folder_path);

	}

}
