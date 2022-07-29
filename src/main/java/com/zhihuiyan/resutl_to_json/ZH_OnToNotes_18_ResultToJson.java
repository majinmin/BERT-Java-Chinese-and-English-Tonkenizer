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

import com.text.bert.tokenizer.MyBertTokenizers;
import com.text.bert.tokenizer.TokensData;

public class ZH_OnToNotes_18_ResultToJson {
	
	public static  HashMap<String,Integer> label_2_id = null;
	public static  HashMap<Integer,String> id_2_label = null;
	public static  MyBertTokenizers myBertTokenizer = null;
	
	public static Map<String, Integer> vocaMap = null;
	static {		
		String vocabel_file_name  = "/text_parsing/src/main/resources/model_resource/zh_vocab.txt";
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
		
		String json_file_1 = new String("/text_parsing/src/main/resources/model_resource/json_of_labels_18_ontonote.json");
		//String json_file_1 = new String("/text_parsing/src/main/resources/model_resource/json_of_labels_18_ontonote.json");
        File file=new File(json_file_1);
        
        String content= FileUtils.readFileToString(file,"UTF-8");
        
        label_2_id = getLabelMap(content,"label2id");
        id_2_label = getLabelMap(content,"id2label");
        
	}
	
	
	public static void main(String[] args) throws IOException {
		
		String[] string_text = {
				"大家好,我是王德发毕业于华中科技大学",
				"大家好,我是王德发毕业于华中科技大学"		
		};
		
		int[][] result_list = {{0, 0, 0, 0, 0, 0, 0, 13, 14, 14, 0, 0, 0, 15, 16, 16, 16, 31, 31, 31},
				               {0, 0, 0, 0, 0, 0, 0, 13, 14, 14, 0, 0, 0, 15, 16, 16, 16, 16, 16, 0}};
		
		
		
		List<List<List<Entity>>> result = predictPosProcess(string_text,result_list);
		
		System.out.println(result);
	}
	
	public static List<List<List<Entity>>> predictPosProcess(String [] stringTextList,int[][] result_list) throws IOException {
		
		
		TokensData result_tokens_of_strings = myBertTokenizer.getInputs(stringTextList);
		//已经获取result_tokens_of_strings
		String[][] tokens_list = result_tokens_of_strings.getPadding_token_data();
		
		List<List<List<Entity>>> predict_result_list_  = new ArrayList<List<List<Entity>>>();
		for (int i = 0; i < tokens_list.length; i++) {
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
			List<List<Entity>> final_result = result_to_json(tokens_temp , result);
			predict_result_list_.add(final_result);
			
		}
		return predict_result_list_;
	}
	
	private static List<List<Entity>> result_to_json(List<String> tokens, List<String> tags) {
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
		for (int i = 0; i < tags.size(); i++) {
			String token = tokens.get(i);
			String tag = tags.get(i);
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

					Entity entity = new Entity(entity_name,flag_list, entity_start, idx, last_tag.substring(2),chinese_name_strings);
					String entity_class = entity.getType();
					if (map_of_english_entities_list.containsKey(entity.getType())) {
						
						entity.setChinese_name(english_chinese_map.get(entity_class));
						map_of_english_entities_list.get(entity_class).add(entity);
					}
					
					entity_name = "";
					flag_list = "";
					chinese_name_strings = new ArrayList<String>();
				}
				
				entity_name += token.replace("##", "");
				flag_list += tag.charAt(0);
				chinese_name_strings.add(token);
				
				entity_start = idx;
			} else if (tag.charAt(0) == 'I' || tag.charAt(0) == 'E') {
				
				entity_name += token.replace("##", "");;
				flag_list += tag.charAt(0);
				chinese_name_strings.add(token);
				
			} else if (tag.charAt(0) == 'O' || tag.charAt(0) == '[') { 
			//  } else if (tag.charAt(0) == 'O') { 
				
				if (entity_name != "") {
					Entity entity = new Entity(entity_name,flag_list, entity_start, idx, last_tag.substring(2),chinese_name_strings);

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
			Entity entity = new Entity(entity_name, flag_list,entity_start, idx, last_tag.substring(2),chinese_name_strings);
			
			String entity_class = entity.getType();
			if (map_of_english_entities_list.containsKey(entity.getType())) {
				
				entity.setChinese_name(english_chinese_map.get(entity_class));
				map_of_english_entities_list.get(entity_class).add(entity);
			}
			
		}
		for (Map.Entry<String, List<Entity>> entry : map_of_english_entities_list.entrySet()) {
			
			resuList.add(entry.getValue().stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
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
