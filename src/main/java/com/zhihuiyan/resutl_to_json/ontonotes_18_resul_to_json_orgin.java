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

public class ontonotes_18_resul_to_json_orgin {
	
	public static  HashMap<String,Integer> label_2_id = null;
	public static  HashMap<Integer,String> id_2_label = null;
	
	public static MyBertTokenizers mytoken = null;
	public static Map<String, Integer> vocaMap = null;
	static {		
		String vocabel_file_name  = "/text_parsing/src/main/java/com/text/bert/tokenizer/zh_vocab.txt";
		vocaMap = readLineListWithVocab(vocabel_file_name);
		mytoken = new MyBertTokenizers(vocabel_file_name);
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
		
		String json_file_1 = new String("/text_parsing/src/main/resources/model_resource/json_of_labels.json");
        File file=new File(json_file_1);
        
        String content= FileUtils.readFileToString(file,"UTF-8");
        
        label_2_id = getLabelMap(content,"label2id");
        id_2_label = getLabelMap(content,"id2label");
        
	}
	
	
	public static void main(String[] args) throws IOException {
		
		String[] string_text = {
				"大家好,我是王德发毕业于dsfsdf华中科技大学yyyywww",
				"大家好,我是王德发毕业于华中科技大学"		
		};
		
		int[][] result_list = {{0,0,0,0,0,0,0,14,32,50,0,0,0,0,0,0,0,12,30,30,30,30,48,0,0,0,0},
				               {0,0,0,0,0,0,0,14,32,50,0,0,12,30,30,30,30,30,48,0,0,0,0,0,0,0,0}};
		
		
		
		List<List<List<Entity>>> result = predictPosProcess(string_text,result_list);
		
		System.out.println(result);
	}
	
	public static List<List<List<Entity>>> predictPosProcess(String [] stringTextList,int[][] result_list) throws IOException {
		
		
		TokensData result_tokens_of_strings = mytoken.getInputs(stringTextList);
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
		
		List<Entity> CARDINAL = new ArrayList<Entity>();
		english_chinese_map.put("CARDINAL", "其他数字");
		List<Entity> DATE = new ArrayList<Entity>();
		english_chinese_map.put("DATE", "日期（相对日期）");
		List<Entity> EVENT = new ArrayList<Entity>();
		english_chinese_map.put("EVENT", "事件");
		List<Entity> FAC = new ArrayList<Entity>();
		english_chinese_map.put("FAC", "基础设施");
		List<Entity> GPE = new ArrayList<Entity>();
		english_chinese_map.put("GPE", "国家、城市地区");
		List<Entity> LANGUAGE = new ArrayList<Entity>();
		english_chinese_map.put("LANGUAGE", "指定语言");
		List<Entity> LAW = new ArrayList<Entity>();
		english_chinese_map.put("LAW", "法律文件");
		List<Entity> LOC = new ArrayList<Entity>();
		english_chinese_map.put("LOC", "地名");
		List<Entity> MONEY = new ArrayList<Entity>();
		english_chinese_map.put("MONEY", "数额");
		List<Entity> NORP = new ArrayList<Entity>();
		english_chinese_map.put("NORP", "民族、宗教或政治团体");
		List<Entity> ORDINAL = new ArrayList<Entity>();
		english_chinese_map.put("ORDINAL", "顺序单元");
		List<Entity> ORG = new ArrayList<Entity>();
		english_chinese_map.put("ORG", "组织机构");
		List<Entity> PERCENT = new ArrayList<Entity>();
		english_chinese_map.put("PERCENT", "百分比值");
		List<Entity> PERSON = new ArrayList<Entity>();
		english_chinese_map.put("PERSON", "姓名");
		List<Entity> PRODUCT = new ArrayList<Entity>();
		english_chinese_map.put("PRODUCT", "实体产品");
		List<Entity> QUANTITY = new ArrayList<Entity>();
		english_chinese_map.put("QUANTITY", "测量相关");
		List<Entity> TIME = new ArrayList<Entity>();
		english_chinese_map.put("TIME", "时间");
		List<Entity> WORK_OF_ART = new ArrayList<Entity>();
		english_chinese_map.put("WORK_OF_ART", "书名、歌名等");
		
		String entity_name = "";
		String flag_list = "";
		List<String> chinese_name_strings = new ArrayList();
		int entity_start = 0;
		int idx = 0;
		String last_tag = "";
		for (int i = 0; i < tags.size(); i++) {

			String token = tokens.get(i);
			String tag = tags.get(i);
			
			
			
			if (tag.charAt(0) == 'S') {
//        		self.append(token, idx, idx+1, tag[2:]);

				Entity entity = new Entity(token.replace("##", ""),token.replace("##", ""), idx, idx + 1, last_tag.substring(2),chinese_name_strings);

				if (entity.getType().equals("CARDINAL")) {
					entity.setChinese_name(english_chinese_map.get("CARDINAL"));
					CARDINAL.add(entity);
					
				} else if (entity.getType().equals("DATE")) {
					entity.setChinese_name(english_chinese_map.get("DATE"));
					DATE.add(entity);
				} else if (entity.getType().equals("EVENT")) {
					entity.setChinese_name(english_chinese_map.get("EVENT"));
					EVENT.add(entity);
				}else if (entity.getType().equals("FAC")) {
					entity.setChinese_name(english_chinese_map.get("FAC"));
					FAC.add(entity);
				}else if (entity.getType().equals("GPE")) {
					entity.setChinese_name(english_chinese_map.get("GPE"));
					GPE.add(entity);
				}else if (entity.getType().equals("LANGUAGE")) {
					entity.setChinese_name(english_chinese_map.get("LANGUAGE"));
					LANGUAGE.add(entity);
				}else if (entity.getType().equals("LAW")) {
					entity.setChinese_name(english_chinese_map.get("LAW"));
					LAW.add(entity);
				}else if (entity.getType().equals("LOC")) {
					entity.setChinese_name(english_chinese_map.get("LOC"));
					LOC.add(entity);
				}else if (entity.getType().equals("MONEY")) {
					entity.setChinese_name(english_chinese_map.get("MONEY"));
					MONEY.add(entity);
				}else if (entity.getType().equals("NORP")) {
					entity.setChinese_name(english_chinese_map.get("NORP"));
					NORP.add(entity);
				}else if (entity.getType().equals("ORDINAL")) {
					entity.setChinese_name(english_chinese_map.get("ORDINAL"));
					ORDINAL.add(entity);
				}else if (entity.getType().equals("ORG")) {
					entity.setChinese_name(english_chinese_map.get("ORG"));
					ORG.add(entity);
				}else if (entity.getType().equals("PERCENT")) {
					entity.setChinese_name(english_chinese_map.get("PERCENT"));
					PERCENT.add(entity);
				}else if (entity.getType().equals("PERSON")) {
					entity.setChinese_name(english_chinese_map.get("PERSON"));
					PERSON.add(entity);
				}else if (entity.getType().equals("PRODUCT")) {
					entity.setChinese_name(english_chinese_map.get("PRODUCT"));
					PRODUCT.add(entity);
				}else if (entity.getType().equals("QUANTITY")) {
					entity.setChinese_name(english_chinese_map.get("QUANTITY"));
					QUANTITY.add(entity);
				}else if (entity.getType().equals("TIME")) {
					entity.setChinese_name(english_chinese_map.get("TIME"));
					TIME.add(entity);
				}else if (entity.getType().equals("WORK_OF_ART")) {
					entity.setChinese_name(english_chinese_map.get("WORK_OF_ART"));
					WORK_OF_ART.add(entity);
				}
			} else if (tag.charAt(0) == 'B') {
				if (entity_name != "") {

					Entity entity = new Entity(entity_name,flag_list, entity_start, idx, last_tag.substring(2),chinese_name_strings);

					if (entity.getType().equals("CARDINAL")) {
						entity.setChinese_name(english_chinese_map.get("CARDINAL"));
						CARDINAL.add(entity);
					} else if (entity.getType().equals("DATE")) {
						entity.setChinese_name(english_chinese_map.get("DATE"));
						DATE.add(entity);
					} else if (entity.getType().equals("EVENT")) {
						entity.setChinese_name(english_chinese_map.get("EVENT"));
						EVENT.add(entity);
					}else if (entity.getType().equals("FAC")) {
						entity.setChinese_name(english_chinese_map.get("FAC"));
						FAC.add(entity);
					}else if (entity.getType().equals("GPE")) {
						entity.setChinese_name(english_chinese_map.get("GPE"));
						GPE.add(entity);
					}else if (entity.getType().equals("LANGUAGE")) {
						entity.setChinese_name(english_chinese_map.get("LANGUAGE"));
						LANGUAGE.add(entity);
					}else if (entity.getType().equals("LAW")) {
						entity.setChinese_name(english_chinese_map.get("LAW"));
						LAW.add(entity);
					}else if (entity.getType().equals("LOC")) {
						entity.setChinese_name(english_chinese_map.get("LOC"));
						LOC.add(entity);
					}else if (entity.getType().equals("MONEY")) {
						entity.setChinese_name(english_chinese_map.get("MONEY"));
						MONEY.add(entity);
					}else if (entity.getType().equals("NORP")) {
						entity.setChinese_name(english_chinese_map.get("NORP"));
						NORP.add(entity);
					}else if (entity.getType().equals("ORDINAL")) {
						entity.setChinese_name(english_chinese_map.get("ORDINAL"));
						ORDINAL.add(entity);
					}else if (entity.getType().equals("ORG")) {
						entity.setChinese_name(english_chinese_map.get("ORG"));
						ORG.add(entity);
					}else if (entity.getType().equals("PERCENT")) {
						entity.setChinese_name(english_chinese_map.get("PERCENT"));
						PERCENT.add(entity);
					}else if (entity.getType().equals("PERSON")) {
						entity.setChinese_name(english_chinese_map.get("PERSON"));
						PERSON.add(entity);
					}else if (entity.getType().equals("PRODUCT")) {
						entity.setChinese_name(english_chinese_map.get("PRODUCT"));
						PRODUCT.add(entity);
					}else if (entity.getType().equals("QUANTITY")) {
						entity.setChinese_name(english_chinese_map.get("QUANTITY"));
						QUANTITY.add(entity);
					}else if (entity.getType().equals("TIME")) {
						entity.setChinese_name(english_chinese_map.get("TIME"));
						TIME.add(entity);
					}else if (entity.getType().equals("WORK_OF_ART")) {
						entity.setChinese_name(english_chinese_map.get("WORK_OF_ART"));
						WORK_OF_ART.add(entity);
					}
					entity_name = "";
					flag_list = "";
					chinese_name_strings = new ArrayList();
				}
				
				entity_name += token.replace("##", "");
				flag_list += tag.charAt(0);
				chinese_name_strings.add(token);
				
				entity_start = idx;
			} else if (tag.charAt(0) == 'I' || tag.charAt(0) == 'E') {
				
				entity_name += token.replace("##", "");;
				flag_list += tag.charAt(0);
				chinese_name_strings.add(token);
				
			} else if (tag.charAt(0) == 'O') {
				if (entity_name != "") {
					Entity entity = new Entity(entity_name,flag_list, entity_start, idx, last_tag.substring(2),chinese_name_strings);

					if (entity.getType().equals("CARDINAL")) {
						entity.setChinese_name(english_chinese_map.get("CARDINAL"));
						CARDINAL.add(entity);
					} else if (entity.getType().equals("DATE")) {
						entity.setChinese_name(english_chinese_map.get("DATE"));
						DATE.add(entity);
					} else if (entity.getType().equals("EVENT")) {
						entity.setChinese_name(english_chinese_map.get("EVENT"));
						EVENT.add(entity);
					}else if (entity.getType().equals("FAC")) {
						entity.setChinese_name(english_chinese_map.get("FAC"));
						FAC.add(entity);
					}else if (entity.getType().equals("GPE")) {
						entity.setChinese_name(english_chinese_map.get("GPE"));
						GPE.add(entity);
					}else if (entity.getType().equals("LANGUAGE")) {
						entity.setChinese_name(english_chinese_map.get("LANGUAGE"));
						LANGUAGE.add(entity);
					}else if (entity.getType().equals("LAW")) {
						entity.setChinese_name(english_chinese_map.get("LAW"));
						LAW.add(entity);
					}else if (entity.getType().equals("LOC")) {
						entity.setChinese_name(english_chinese_map.get("LOC"));
						LOC.add(entity);
					}else if (entity.getType().equals("MONEY")) {
						entity.setChinese_name(english_chinese_map.get("MONEY"));
						MONEY.add(entity);
					}else if (entity.getType().equals("NORP")) {
						entity.setChinese_name(english_chinese_map.get("NORP"));
						NORP.add(entity);
					}else if (entity.getType().equals("ORDINAL")) {
						entity.setChinese_name(english_chinese_map.get("ORDINAL"));
						ORDINAL.add(entity);
					}else if (entity.getType().equals("ORG")) {
						entity.setChinese_name(english_chinese_map.get("ORG"));
						ORG.add(entity);
					}else if (entity.getType().equals("PERCENT")) {
						entity.setChinese_name(english_chinese_map.get("PERCENT"));
						PERCENT.add(entity);
					}else if (entity.getType().equals("PERSON")) {
						entity.setChinese_name(english_chinese_map.get("PERSON"));
						PERSON.add(entity);
					}else if (entity.getType().equals("PRODUCT")) {
						entity.setChinese_name(english_chinese_map.get("PRODUCT"));
						PRODUCT.add(entity);
					}else if (entity.getType().equals("QUANTITY")) {
						entity.setChinese_name(english_chinese_map.get("QUANTITY"));
						QUANTITY.add(entity);
					}else if (entity.getType().equals("TIME")) {
						entity.setChinese_name(english_chinese_map.get("TIME"));
						TIME.add(entity);
					}else if (entity.getType().equals("WORK_OF_ART")) {
						entity.setChinese_name(english_chinese_map.get("WORK_OF_ART"));
						WORK_OF_ART.add(entity);
					}
					
					entity_name = "";
					flag_list = "";
					chinese_name_strings = new ArrayList();
				}
			} else {
				
				entity_name = "";
				flag_list = "";
				chinese_name_strings = new ArrayList();
				
				entity_start = idx;
			}
			idx += 1;
			last_tag = tag;
		}
		if (entity_name != "") {
			Entity entity = new Entity(entity_name, flag_list,entity_start, idx, last_tag.substring(2),chinese_name_strings);
			if (entity.getType().equals("CARDINAL")) {
				entity.setChinese_name(english_chinese_map.get("CARDINAL"));
				CARDINAL.add(entity);
			} else if (entity.getType().equals("DATE")) {
				entity.setChinese_name(english_chinese_map.get("DATE"));
				DATE.add(entity);
			} else if (entity.getType().equals("EVENT")) {
				entity.setChinese_name(english_chinese_map.get("EVENT"));
				EVENT.add(entity);
			}else if (entity.getType().equals("FAC")) {
				entity.setChinese_name(english_chinese_map.get("FAC"));
				FAC.add(entity);
			}else if (entity.getType().equals("GPE")) {
				entity.setChinese_name(english_chinese_map.get("GPE"));
				GPE.add(entity);
			}else if (entity.getType().equals("LANGUAGE")) {
				entity.setChinese_name(english_chinese_map.get("LANGUAGE"));
				LANGUAGE.add(entity);
			}else if (entity.getType().equals("LAW")) {
				entity.setChinese_name(english_chinese_map.get("LAW"));
				LAW.add(entity);
			}else if (entity.getType().equals("LOC")) {
				entity.setChinese_name(english_chinese_map.get("LOC"));
				LOC.add(entity);
			}else if (entity.getType().equals("MONEY")) {
				entity.setChinese_name(english_chinese_map.get("MONEY"));
				MONEY.add(entity);
			}else if (entity.getType().equals("NORP")) {
				entity.setChinese_name(english_chinese_map.get("NORP"));
				NORP.add(entity);
			}else if (entity.getType().equals("ORDINAL")) {
				entity.setChinese_name(english_chinese_map.get("ORDINAL"));
				ORDINAL.add(entity);
			}else if (entity.getType().equals("ORG")) {
				entity.setChinese_name(english_chinese_map.get("ORG"));
				ORG.add(entity);
			}else if (entity.getType().equals("PERCENT")) {
				entity.setChinese_name(english_chinese_map.get("PERCENT"));
				PERCENT.add(entity);
			}else if (entity.getType().equals("PERSON")) {
				entity.setChinese_name(english_chinese_map.get("PERSON"));
				PERSON.add(entity);
			}else if (entity.getType().equals("PRODUCT")) {
				entity.setChinese_name(english_chinese_map.get("PRODUCT"));
				PRODUCT.add(entity);
			}else if (entity.getType().equals("QUANTITY")) {
				entity.setChinese_name(english_chinese_map.get("QUANTITY"));
				QUANTITY.add(entity);
			}else if (entity.getType().equals("TIME")) {
				entity.setChinese_name(english_chinese_map.get("TIME"));
				TIME.add(entity);
			}else if (entity.getType().equals("WORK_OF_ART")) {
				entity.setChinese_name(english_chinese_map.get("WORK_OF_ART"));
				WORK_OF_ART.add(entity);
			}
		}
		
		
		resuList.add(CARDINAL.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(DATE.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(EVENT.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(FAC.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(GPE.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(LANGUAGE.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(LAW.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(LOC.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(MONEY.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(NORP.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(ORDINAL.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(ORG.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(PERCENT.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(PERSON.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(PRODUCT.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(QUANTITY.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(TIME.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		resuList.add(WORK_OF_ART.stream().filter(final_entity ->final_entity.clearEntity() != null ).collect(Collectors.toList()));
		
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
