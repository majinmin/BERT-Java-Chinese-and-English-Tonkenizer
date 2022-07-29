package com.text.bert.tokenizer;

import java.util.HashMap;

public class My_Map extends HashMap<String, Integer>{
	//"unk_token": "[UNK]"
	private static int UNK_TOKEN = 100;
	
    public int getMy(String tokens) {
    	int unknow_tokens = UNK_TOKEN;
    	return this.getOrDefault(tokens, unknow_tokens);
    }

}
