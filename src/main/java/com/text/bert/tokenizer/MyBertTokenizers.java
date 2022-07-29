package com.text.bert.tokenizer;

import java.io.File;
import com.text.bert.tokenizer.TokensData;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import com.google.common.io.Resources;
import com.text.bert.tokenizer.FullTokenizer;
import com.text.bert.tokenizer.BasicTokenizer;
public class MyBertTokenizers {
	
	//{"unk_token": "[UNK]", "sep_token": "[SEP]", "pad_token": "[PAD]", "cls_token": "[CLS]", "mask_token": "[MASK]"}
    private static final String SEPARATOR_TOKEN = "[SEP]";
    private static final String START_TOKEN = "[CLS]";
    private static final String END_TOKEN = "[SEP]";
    private static final String PAD_TOKEN = "[PAD]";
    private static final String MASK_TOKEN = "[MASK_TOKEN]";
    
    
	//static String vocabel_file_name  = "/text_parsing/src/main/resources/model_resource/zh_vocab.txt";
	
    private String vocabel_file_name = null;
	
	private FullTokenizer tokenizer = null;
	private BasicTokenizer base_tokenizer = null;
	//private BasicTokenizer base_tokenizer = null;
	
	//private WordpieceTokenizer wordPiece_tokenizer = null;
    
	private int special_tokens_start_token = -1;
	private int special_tokens_end_token = -1;
	private int special_tokens_pad_token = -1;
	
	
	public MyBertTokenizers(String vocabel_file_name) {
		// TODO Auto-generated constructor stub
		this.vocabel_file_name = vocabel_file_name;
		Path path = Paths.get(this.vocabel_file_name);
		this.tokenizer = new FullTokenizer(path ,false);
		this.base_tokenizer = new BasicTokenizer(false);
		//this.wordPiece_tokenizer = new WordpieceTokenizer(null)
		int[] special_tokens = this.tokenizer.convert(new String[] {START_TOKEN,END_TOKEN,PAD_TOKEN});
		this.special_tokens_start_token = special_tokens[0];
		this.special_tokens_end_token = special_tokens[1];
		this.special_tokens_pad_token = special_tokens[2];
	}
	
	
	public FullTokenizer getTokenizer() {
		return tokenizer;
	}
	
    public TokensData getInputs(final String[] sequence_list) {
    	
    	//Base Token
    	String[][] base_token_orgin = this.base_tokenizer.tokenize(sequence_list);
    	String[][] ids = this.tokenizer.tokenize(sequence_list);
    	this.tokenizer.getMapOfOrginToNewTokens(sequence_list);
    	
    	//get max length
    	int max_lens = 0;    			 
    	for (String[] strings : ids) {
			if(strings.length > max_lens) {
				max_lens = strings.length;
			}
    	}
    	//start + end token
    	max_lens = max_lens + 2;
		//System.out.println("max_len " + max_lens);
    	
    	//input_ids
    	long[][] ids_list = new long[ids.length][max_lens];
    	long[][] attention_mask_list = new long[ids.length][max_lens];
    	long[][] segment_list = new long[ids.length][max_lens];
    	//__init__ for 0
    	for (int i = 0; i < ids.length; i++) {
			for (int j = 0; j < max_lens; j++) {
				ids_list[i][j] = special_tokens_pad_token;
			}
		}
    	//attention_mask_list
    	for (int i = 0; i < ids.length; i++) {
			for (int j = 0; j < max_lens; j++) {
				attention_mask_list[i][j] = special_tokens_pad_token;
			}
		}
    	//segment_list
    	for (int i = 0; i < ids.length; i++) {
			for (int j = 0; j < max_lens; j++) {
				segment_list[i][j] = special_tokens_pad_token;
			}
		}  	
    	
    	for (int i = 0; i < ids.length; i++) {
    		final int[] ids1 =  tokenizer.convert(ids[i]);
    		//System.out.println();
    		ids_list[i][0] = special_tokens_start_token;
    		attention_mask_list[i][0] = 1;
    		for (int j = 0; j < max_lens; j++) {
    			
    			if (j < ids1.length) {
    				ids_list[i][j + 1] = ids1[j];
    				attention_mask_list[i][j + 1] = 1;
    			}
    			if (j == ids1.length) {
    				ids_list[i][j + 1] = special_tokens_end_token;
    				attention_mask_list[i][j+1] = 1;
    			}
    		}
		}
    	//ids_returns
    	//ids_list
    	TokensData result_data = new TokensData(ids,ids_list,attention_mask_list,segment_list);
    	result_data.setOrgin_true_token_data(base_token_orgin);
    	
    	return result_data;
    	
    }
	public static void main(String[] args) throws URISyntaxException {
		
		String vocabel_file_name  = "/text_parsing/src/main/resources/model_resource/en_vocab.txt";
		MyBertTokenizers myBertTokenizer = new MyBertTokenizers(vocabel_file_name);
		
		String[] string_text = {
				"大家好,我是马金民毕业于华中科技大学18888888，我住在中国淮安yyyywww",
				"India is a beautiful country , test##ing a function , unaffable"		
		};
		TokensData result = myBertTokenizer.getInputs(string_text);
		System.out.println(result);
		
		//[101, 1920, 2157, 1962, 117, 2769, 3221, 7716, 7032, 3696, 3684, 689, 754, 1290, 704, 4906, 2825, 1920, 2110, 10988, 10053, 8156, 8024, 2769, 857, 1762, 704, 1744, 3917, 2128, 9453, 12642, 9718, 102]
		//[101, 1920, 2157, 1962, 117, 2769, 3221, 7716, 7032, 3696, 3684, 689, 754, 1290, 704, 4906, 2825, 1920, 2110, 10988, 10053, 8156, 8024, 2769, 857, 1762, 704, 1744, 3917, 2128, 9453, 12642, 9718, 102]
		//[大, 家, 好, ,, 我, 是, 马, 金, 民, 毕, 业, 于, 华, 中, 科, 技, 大, 学, 1888, ##888, ##8, ，, 我, 住, 在, 中, 国, 淮, 安, yy, ##yy, ##www]
		//[大, 家, 好, ,, 我, 是, 马, 金, 民, 毕, 业, 于, 华, 中, 科, 技, 大, 学, 1888, ##888, ##8, ，, 我, 住, 在, 中, 国, 淮, 安, yy, ##yy, ##www]
		int lens = result.getInput_ids_list()[0].length;
		int numbers = result.getInput_ids_list().length;
		for (int j = 0; j < numbers;j++) {
			
			System.out.println();
			for (int i = 0; i < lens; i++) {
				
				
				System.out.print(result.getInput_ids_list()[j][i] + "\t");
				
			}
			System.out.println();
			for (int i = 0; i < lens; i++) {
				
				
				System.out.print(result.getInput_masks_list()[j][i] + "\t");
				
			}
			System.out.println();
			for (int i = 0; i < lens; i++) {
				
				
				System.out.print(result.getInput_segment_list()[j][i] + "\t");
				
			}
	}
		
	}

}
