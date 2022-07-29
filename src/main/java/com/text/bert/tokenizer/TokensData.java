package com.text.bert.tokenizer;

import java.util.Arrays;

public class TokensData {
	
	public static String CLS = "[CLS]";
	public static String SEP = "[SEP]";
	public static String PAD = "[PAD]";
	
	//word piece tokens
	private String[][] base_token_data = null;
	
	//orgin true token no word piece tokens
	
	private long[][] input_ids_list = null;
	
	private String[][] orgin_true_token_data = null;
	
	private long[][] input_masks_list = null;
	
	private long[][] input_segment_list = null;

	
	private String[][] padding_token_data = null;
	
	
	public String[][] getOrgin_true_token_data() {
		return orgin_true_token_data;
	}
	public void setOrgin_true_token_data(String[][] orgin_true_token_data) {
		this.orgin_true_token_data = orgin_true_token_data;
	}
	
	public String[][] getPadding_token_data() {
		return padding_token_data;
	}
	
	
	public String[][] getPaddingBaseTokenData(String[][] tokens){
		
		String[][] _padding_token_data = null;
		if (tokens  != null){
			
			int max_len = 0;
			for (int i = 0; i < tokens.length; i++) {
				
				if (tokens[i].length > max_len) {
					max_len = tokens[i].length;
				}
			_padding_token_data = new String[tokens.length][max_len];
			
			for (int j = 0; j < _padding_token_data.length; j++) {
				
				//this.padding_token_data[j][0] = CLS;
				for (int j2 = 0; j2 < _padding_token_data[j].length; j2++) {
					if (j2 < tokens[j].length) {						
						_padding_token_data[j][j2] = tokens[j][j2];
					}else if(j2 == tokens.length) {
						//this.padding_token_data[j][j2] = SEP;
						_padding_token_data[j][j2] = PAD;
					}else {
						_padding_token_data[j][j2] = PAD;
					}
				}
			}
		}
	}
		return _padding_token_data;
}
	public void getPaddingBaseTokenData(){
		
		if (this.base_token_data  != null){
			
			int max_len = 0;
			for (int i = 0; i < this.base_token_data.length; i++) {
				
				if (this.base_token_data[i].length > max_len) {
					max_len = this.base_token_data[i].length;
				}
			this.padding_token_data = new String[this.base_token_data.length][max_len];
			
			for (int j = 0; j < this.padding_token_data.length; j++) {
				
				//this.padding_token_data[j][0] = CLS;
				for (int j2 = 0; j2 < this.padding_token_data[j].length; j2++) {
					if (j2 < this.base_token_data[j].length) {						
						this.padding_token_data[j][j2] = this.base_token_data[j][j2];
					}else if(j2 == this.base_token_data[j].length) {
						//this.padding_token_data[j][j2] = SEP;
						this.padding_token_data[j][j2] = PAD;
					}else {
						this.padding_token_data[j][j2] = PAD;
					}
				}
			}
		}
	}
}
	
	
	
	public String[][] getBase_token_data() {
		return base_token_data;
	}

	public void setBase_token_data(String[][] base_token_data) {
		this.base_token_data = base_token_data;
	}

	public long[][] getInput_ids_list() {
		return input_ids_list;
	}

	public void setInput_ids_list(long[][] input_ids_list) {
		this.input_ids_list = input_ids_list;
	}

	public long[][] getInput_masks_list() {
		return input_masks_list;
	}

	public void setInput_masks_list(long[][] input_masks_list) {
		this.input_masks_list = input_masks_list;
	}

	public long[][] getInput_segment_list() {
		return input_segment_list;
	}

	public void setInput_segment_list(long[][] input_segment_list) {
		this.input_segment_list = input_segment_list;
	}


	@Override
	public String toString() {
		return "TokensData [base_token_data=" + Arrays.toString(base_token_data) + ", input_ids_list="
				+ Arrays.toString(input_ids_list) + ", input_masks_list=" + Arrays.toString(input_masks_list)
				+ ", input_segment_list=" + Arrays.toString(input_segment_list) + ", padding_token_data="
				+ Arrays.toString(padding_token_data) + "]";
	}


	public TokensData(String[][] base_token_data, long[][] input_ids_list, long[][] input_masks_list,
			long[][] input_segment_list) {
		super();
		this.base_token_data = base_token_data;
		this.input_ids_list = input_ids_list;
		this.input_masks_list = input_masks_list;
		this.input_segment_list = input_segment_list;
		this.getPaddingBaseTokenData();
	}
	

	
}
