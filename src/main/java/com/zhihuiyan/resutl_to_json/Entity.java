package com.zhihuiyan.resutl_to_json;

import java.util.List;

public class Entity {
	private String word;
	private int start;
	private int end;
	private String type;
	private String chinese_name;
	private List<String> chinese_name_list; 
	private String flag_list = null;
	private String language = "zh";

	
	public List<String> getChinese_name_char() {
		return chinese_name_list;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLanguage() {
		return language;
	}
	
	public Entity(String word, String flag_list,int start, int end, String type, List<String>  chinese_name_list) {
		// TODO Auto-generated constructor stub
		this.word = word;
		this.start = start;
		this.end = end;
		this.type = type;
		this.chinese_name = "";
		this.flag_list = flag_list;
		this.chinese_name_list = chinese_name_list;
	}

	
	//实体清洗
	
	public Entity clearEntity() {
		
		
		if (this.flag_list.charAt(0) == 'B' && this.flag_list.length() == 1){
			return null;
		}else if (this.flag_list.charAt(0) == 'I' || this.flag_list.charAt(0) == 'E') {
			return null;
		}
		
		return this;
	}
	
	
	public String getEntity_flag() {
		return flag_list;
	}
	public void setFlag_list(String flag_list) {
		this.flag_list = flag_list;
	}
	
	public String getChinese_name() {
		return chinese_name;
	}
	public void setChinese_name(String chinese_name) {
		this.chinese_name = chinese_name;
	}
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Entity [word=" + word + ", start=" + start + ", end=" + end + ", type=" + type + ", chinese_name="
				+ chinese_name + ", flag_list=" + flag_list + "]";
	}

}