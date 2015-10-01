package com.cs.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class TextTokenizer {
	private List<String> words = new ArrayList<String>();
	private int pos = 0;
	private int size = 0;
	private Pattern regex_ws = Pattern.compile("\\s+");
	
	// YOU MUST IMPLEMENT THIS METHOD
	public TextTokenizer( char[] texts ) throws FileNotFoundException, URISyntaxException {
		
		URL resource = TextTokenizer.class.getResource("stop_words.txt");
		File file = new File(resource.toURI());
		FileInputStream fis = new FileInputStream(file);
  		
  		StopwordsRemover remover = new StopwordsRemover(fis);

		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
		List<Character> a_word = new ArrayList<Character>();
		int ix = 0;
		int len = texts.length;
		
		for(; ix < len; ++ix)
		{
			if(Character.isAlphabetic(texts[ix]) || texts[ix] == '-')
			{
				a_word.add(texts[ix]);
				if(ix == len - 1)
				{
					this.add_word(a_word);
					a_word.clear();
					break;
				}
			}
			else
			{
				if(!remover.isStopword(a_word))
					this.add_word(a_word);
				
				a_word.clear();
			}
		}
		
		this.size = this.words.size();
	}
	
	public String returnWord(int num){
		String r_words = "";
		int _n = 0;
		for (String word : this.words){
			if(_n != 0)
				r_words += " ";
			r_words += word;
			
			if(num > -1 && _n > num)
				break;
			_n++;
		}
		
		return r_words;
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document; or return null if it is the end of the document
		char ret_v[] = null;
		
		if(pos < size)
		{
			ret_v = words.get(pos).toCharArray();
			pos++;
		}
		return ret_v;
	}
	
	/**
	 * Get document length
	 * @param empty
	 * @return int document length
	 */
	public int docLen()
	{
		return this.words.size();
	}
	
	/**
	 * 
	 * @param a_word
	 */
	private void add_word(List<Character> a_word)
	{
		if(a_word.size() > 0)
		{
			String w = "";
			for(int j = 0; j < a_word.size(); ++j)
				w += a_word.get(j);
			
			this.words.add(w);
		}
	}

}
