package com.zhihuiyan.resutl_to_json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.text.bert.tokenizer.FullTokenizer;
import com.text.bert.tokenizer.MyBertTokenizers;
import com.text.bert.tokenizer.TokensData;

public class EN_OnToNotes_18_ResultToJson_Base_Bert {
	
	public static  HashMap<String,Integer> label_2_id = null;
	public static  HashMap<Integer,String> id_2_label = null;
	public static  MyBertTokenizers myBertTokenizer = null;
	
	
	
	public static boolean isNumeric(final CharSequence cs) {
        // 判断是否为空，如果为空则返回false
        if (cs.length() == 0) {
            return false;
        }
        // 通过 length() 方法计算cs传入进来的字符串的长度，并将字符串长度存放到sz中
        final int sz = cs.length();
        // 通过字符串长度循环
        for (int i = 0; i < sz; i++) {
            // 判断每一个字符是否为数字，如果其中有一个字符不满足，则返回false
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        // 验证全部通过则返回true
        return true;
    }
	public static String[]  special_char_list = {
		"`","~","!","@","#","$","%","^","&","*","(",")","+","=","|","{","}","'",":",";","[","]",",",".","<",">","/","?","~","！","@",
		"#","￥","%","…","…","……","&","..",".","...","&","*","（","）","——","—","|","【","】","‘","’","；","：","”","“","。","，","、","？","\\",	
		" "};
	
	public static boolean isSpecialChar(String token) {
		
		for (int i = 0; i < special_char_list.length; i++) {
			if (token.strip().equals(special_char_list[i].strip()) || isNumeric(token)){
				return true;
			}
		}
		return false;
	}
	
	
	public static Map<String, Integer> vocaMap = null;
	static {		
		String vocabel_file_name  = "/text_parsing/src/main/resources/model_resource/en_vocab.txt";
		vocaMap = readLineListWithVocab(vocabel_file_name);
		myBertTokenizer = new MyBertTokenizers(vocabel_file_name);
		try {
			readJsonFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static HashMap getLabelMap(String content,String keys_) {
		HashMap map_ = null;
		if (keys_.equals("id2label")) {
			map_ = new HashMap<Integer,String>();
		}
		else {
			map_ = new HashMap<String,Integer>();
		}
        JSONObject jsonObject=new JSONObject(content);
        JSONObject jsonObj = jsonObject.getJSONObject(keys_);
        Iterator<String> json_iterator = jsonObj.keys();
        
        while(json_iterator.hasNext()) {
            String one = (String)json_iterator.next();
            //System.out.println(one.getClass());
            String two = String.valueOf(jsonObj.get(one));
            //System.out.println(one + " " + two);
            if (keys_.equals("id2label")){
            	map_.put(Integer.valueOf(one), two);
            }
            if (keys_.equals("label2id")){
            	map_.put(one, Integer.valueOf(two));
            }
        }
        return map_;
	}
	
	public static void readJsonFiles() throws IOException {
		
		String json_file_1 = new String("/text_parsing/src/main/resources/model_resource/en_json_of_labels_18_ontonote.json");
        File file=new File(json_file_1);
        
        String content= FileUtils.readFileToString(file,"UTF-8");
        
        label_2_id = getLabelMap(content,"label2id");
        id_2_label = getLabelMap(content,"id2label");
        
	}
	
	
	public static void main(String[] args) throws IOException {
		
		
		//List<String> test = new ArrayList<>();
		//System.out.println(test.get(100));
		
		
		String[] string_text = {
				"India is a beautiful country , test##ing a function , unaffable",
				"A rapidly expanding wildfire near Yosemite National Park, California’s largest of the season, at 17,000 acres, prompted thousands of evacuations Monday and sent smoke to the San Francisco Bay Area and Sacramento.",
						
		};
		
		int[][] result_list = {
							   
				               {0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 29, 30, 30, 30, 0},
				               {0,0,0,0,0,0,0,5,6,6,6,6,0,9,0,0,0,0,15,16,0,0,23,24,24,24,0,0,27,0,0,0,15,0,0,0,0,11,12,12,12,12,0,9,0,0},		               
		};
		
		
		
		List<List<List<Entity>>> result = predictPosProcess(string_text,result_list);
		
		System.out.println(result);
	}
	
	
	
	public static void  startMaping(
			String[][] result_of_pad_true_orgin_data,
			//base token
			String[][] base_token,
			List<List<Integer>> map_of_base_tokens_orgin_to_new_postion,
			int[][] result_list
			) {
		//获取真实base_token长度
		for (int i = 0; i < result_list.length; i++) {
			//single sentence 
			String[] orgin_base_token = base_token[i];
			int[] predict_label = result_list[i];
			//去除PAD SEP CLS
			int[] clear_predict_label = new int[orgin_base_token.length];
			for (int j_ = 0; j_ < clear_predict_label.length; j_++) {
				clear_predict_label[j_] = predict_label[j_ + 1];
			}
			//clear_predict_label
			//flag
			int flag = -1;
			List<Integer> current_orgin_new_map =   map_of_base_tokens_orgin_to_new_postion.get(i);
			List<Integer> new_predict_result_single_sentence = new ArrayList<>();
			for (int j = 0; j < clear_predict_label.length; j++) {
				int current_int_ = clear_predict_label[j];
				if (current_int_ != flag) {
					//new_predict_result_single_sentence.add();
				}else {
				}
			}
		}
	}
	
	
	public static List<List<List<Entity>>> predictPosProcess(String [] stringTextList,int[][] result_list) throws IOException {
		
		
		FullTokenizer full_token = myBertTokenizer.getTokenizer();
		//还是在base tokenizer基础上
		List<List<Integer>> map_of_base_tokens_orgin_to_new_postion = full_token.getMapOfOrginToNewTokens(stringTextList);
		
		
		TokensData result_tokens_of_strings = myBertTokenizer.getInputs(stringTextList);
		String[][] orgin_base_token = result_tokens_of_strings.getOrgin_true_token_data();
		//获取PAD的orgin true base token
		String[][] result_of_pad_tre_orgin_data = result_tokens_of_strings.getPaddingBaseTokenData(result_tokens_of_strings.getOrgin_true_token_data());
		
		//System.out.println();
		
		//已经获取result_tokens_of_strings
		String[][] tokens_list = result_tokens_of_strings.getPadding_token_data();
		
		List<List<List<Entity>>> predict_result_list_  = new ArrayList<List<List<Entity>>>();
		for (int i = 0; i < tokens_list.length; i++) {
			String[] temp_result_of_pad_tre_orgin_data = result_of_pad_tre_orgin_data[i];
			List<Integer> single_sentence_word_map_of_base_tokens_orgin_to_new_postion = map_of_base_tokens_orgin_to_new_postion.get(i);
			//预测结果 list
			int[] result_ = result_list[i];
			//tokens_list_for.add(Arrays.asList(result_tokens_of_strings.getBase_token_data()[i]));
			List<String> tokens_temp =Arrays.asList(tokens_list[i]);
			
			List<String> result = new ArrayList<String>();
			for (int j = 1; j < result_.length - 1; j++) {
				String currentlabel = id_2_label.get(result_[j]);
				//if (currentlabel == "[CLS]" || currentlabel == "[SEP]"|| currentlabel == "[PAD]") {
				//	continue;
				//}
				result.add(currentlabel);
			}
			List<List<Entity>> final_result = result_to_json(tokens_temp ,
					result,
					single_sentence_word_map_of_base_tokens_orgin_to_new_postion,
					//原生未切
					temp_result_of_pad_tre_orgin_data);
			predict_result_list_.add(final_result);
			
		}
		return predict_result_list_;
	}
	
	private static List<List<Entity>> result_to_json(List<String> tokens,
				   List<String> tags,
			       List<Integer> map_of_base_tokens_orgin_to_new_postion,
			       String[] orgin_base_token
			) {
		Map<String, String> english_chinese_map = new HashMap();
		List<List<Entity>> resuList = new ArrayList<List<Entity>>();
		
		
		
		Map<String, List<Entity>> map_of_english_entities_list = new HashMap<String,List<Entity>>();
		String[] chinese_name = {"其他数字","日期（相对日期）","事件","基础设施","国家、城市地区","指定语言","法律文件","地名","数额",
								 "民族、宗教或政治团体","顺序单元","组织机构","百分比值","姓名","实体产品","测量相关","时间","书名、歌名等"};
		String[] english_name = {"CARDINAL","DATE","EVENT","FAC","GPE","LANGUAGE","LAW","LOC","MONEY",
								 "NORP","ORDINAL","ORG","PERCENT","PERSON","PRODUCT","QUANTITY","TIME","WORK_OF_ART"};
		
		for (int i = 0; i < chinese_name.length; i++) {
			List<Entity> temp_entity = new ArrayList<Entity>();
			english_chinese_map.put(english_name[i], chinese_name[i]);
			map_of_english_entities_list.put(english_name[i], temp_entity);
		}
		String entity_name = "";
		String flag_list = "";
		List<String> chinese_name_strings = new ArrayList<String>();
		int entity_start = 0;
		int idx = 0;
		String last_tag = "";

		int flag_number = -1;
		boolean need_insert = false;
		//tokens 是已经切的
		//tags 与token 对齐，但是是去除PAD的
		for (int i = 0; i < tags.size(); i++) {
			String token = tokens.get(i);
			String tag = tags.get(i);
			//System.out.println(tag);
			
			if (map_of_base_tokens_orgin_to_new_postion.get(i)  != flag_number){
				//System.out.println(map_of_base_tokens_orgin_to_new_postion.get(i));
				flag_number = map_of_base_tokens_orgin_to_new_postion.get(i);
				need_insert = true;
			}else {
				need_insert = false;
			}
			String orgin_token = orgin_base_token[map_of_base_tokens_orgin_to_new_postion.get(i)];
			token = orgin_token;
			if (tag.charAt(0) == 'S') {
//        		self.append(token, idx, idx+1, tag[2:]);
				Entity entity = new Entity(token.replace("##", ""),token.replace("##", ""), idx, idx + 1, last_tag.substring(2),chinese_name_strings);
				String entity_class = entity.getType();
				
				if (map_of_english_entities_list.containsKey(entity.getType())) {
					
					entity.setChinese_name(english_chinese_map.get(entity_class));
					map_of_english_entities_list.get(entity_class).add(entity);
				}
				
			} else if (tag.charAt(0) == 'B') {
				
				if (entity_name != "") {
					
					Entity entity = new Entity(entity_name.strip().strip(),flag_list, entity_start, idx, last_tag.substring(2),chinese_name_strings);
					String entity_class = entity.getType();
					if (map_of_english_entities_list.containsKey(entity.getType())) {
						
						entity.setChinese_name(english_chinese_map.get(entity_class));
						map_of_english_entities_list.get(entity_class).add(entity);
					}
					
					entity_name = "";
					flag_list = "";
					chinese_name_strings = new ArrayList<String>();
				}
				if (isSpecialChar(token)) {
					if (need_insert) {
						entity_name += token.replace("##", "");
					}
				}else {
					if (need_insert) {
					entity_name += " " + token.replace("##", "");
					}
				}
				
				flag_list += tag.charAt(0);
				chinese_name_strings.add(token);
				
				entity_start = idx;
			} else if (tag.charAt(0) == 'I' || tag.charAt(0) == 'E') {
				
				if (isSpecialChar(token)) {
					if(need_insert) {
						entity_name += token.replace("##", "");
					}
				}else {
					if(need_insert) {
						entity_name +=" " + token.replace("##", "");
					}
				}
				flag_list += tag.charAt(0);
				chinese_name_strings.add(token);
				
			} else if (tag.charAt(0) == 'O' || tag.charAt(0) == '[') { 
			//  } else if (tag.charAt(0) == 'O') { 
				
				if (entity_name != "") {
					Entity entity = new Entity(entity_name.strip(),flag_list, entity_start, idx, last_tag.substring(2),chinese_name_strings);

					String entity_class = entity.getType();
					if (map_of_english_entities_list.containsKey(entity.getType())) {
						
						entity.setChinese_name(english_chinese_map.get(entity_class));
						map_of_english_entities_list.get(entity_class).add(entity);
					}
					
					entity_name = "";
					flag_list = "";
					chinese_name_strings = new ArrayList<String>();
				}
			} else {
				
				entity_name = "";
				flag_list = "";
				chinese_name_strings = new ArrayList<String>();
				
				entity_start = idx;
			}
			idx += 1;
			last_tag = tag;
		}
		if (entity_name != "") {
			Entity entity = new Entity(entity_name.strip(), flag_list,entity_start, idx, last_tag.substring(2),chinese_name_strings);
			
			String entity_class = entity.getType();
			if (map_of_english_entities_list.containsKey(entity.getType())) {
				
				entity.setChinese_name(english_chinese_map.get(entity_class));
				map_of_english_entities_list.get(entity_class).add(entity);
			}
			
		}
		for (Map.Entry<String, List<Entity>> entry : map_of_english_entities_list.entrySet()) {
			
			resuList.add(entry.getValue());
		}

		
		return resuList;
	}

	private static Map<String, Integer> readLineListWithVocab(String path) {

		Map<String, Integer> map = new HashMap<>();
		
		String line = null;

		int index = 0;
		try {
			BufferedReader bw = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

			while ((line = bw.readLine()) != null) {
				map.put(line, index);
				index++;
			}
			bw.close();
		} catch (Exception e) {
		}
		return map;
	}
}

/*
 * [O  , O,  O,  O , O,  O,   B-PERSON, I-PERSON, I-PERSON, O, O,  B-ORG, B-ORG, I-ORG, I-ORG, I-ORG, I-ORG, E-ORG, O,   O,   O]
 * [大 , 家, 好, , , 我, 是,  王,        德,        发,     毕, 业, 于,    华,    中,    科,    技,    大,     学,  yy, ##yy, ##www]
 *0 0 0 0 0 0 0 14 32 50 0 0 12 12 30 30 30 30 48 0 0 0 0
 *0 0 0 0 0 0 0 14 32 32 0 0 12 12 30 30 30 30 30 0 0 0 0 
 *
 */

/**
 * A, rapidly, expanding, wild, ##fire, near, Yo, ##se, ##mite, National, Park, ,, California, ’, s, largest, of, the, season, ,, at, 17, ,, 000, acres, ,, prompted, thousands, of, evacuation, ##s, Monday, and, sent, smoke, to, the, San, Francisco, Bay, Area, and, Sacramento, .]
 * 0,       0,         0,    0,      0,    0,  5,    6,      6,        6,     6,0,          9, 0, 0,       0,  0,  15,16,0,0,23,24,24,24,0,0,27,0,0,0,15,0,0,0,0,11,12,12,12,12,0,9,0
 */


