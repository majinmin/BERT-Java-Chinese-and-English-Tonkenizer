package com.zhihuiyan.onnx;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;

import com.text.bert.tokenizer.MyBertTokenizers;
import com.text.bert.tokenizer.TokensData;
import com.zhihuiyan.resutl_to_json.Entity;
import com.zhihuiyan.resutl_to_json.ZH_OnToNotes_18_ResultToJson;
public class MainForZH_18 {
	
	static OrtEnvironment env = OrtEnvironment.getEnvironment();
	static String vocabel_file_name  = "/text_parsing/src/main/resources/model_resource/zh_vocab.txt";
	static MyBertTokenizers objectMyBertTokenier =  new MyBertTokenizers(vocabel_file_name);
	static String onnxPath = "F:\\onnx_java\\albert-mode-optimized.onnx";
	static OrtSession session = null;
	static {
		try {
			session = env.createSession(onnxPath,new OrtSession.SessionOptions());
		} catch (OrtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static int[] convertInts(List<Integer> list) {
	    int[] output = new int[list.size()];
	    for (int i = 0; i < list.size(); i++) {
	      output[i] = list.get(i);
	    }
	    return output;
	  }
	
	  public static int pred(float[] probabilities) {
		    float maxVal = Float.NEGATIVE_INFINITY;
		    int idx = 0;
		    for (int i = 0; i < probabilities.length; i++) {
		      if (probabilities[i] > maxVal) {
		        maxVal = probabilities[i];
		        idx = i;
		      }
		    }
		    return idx;
		  }
	
	public static float[] softmax(float[] input) {

		    double[] tmp = new double[input.length];
		    double sum = 0.0;
		    for (int i = 0; i < input.length; i++) {
		      double val = Math.exp(input[i]);
		      sum += val;
		      tmp[i] = val;
		    }

		    float[] output = new float[input.length];
		    for (int i = 0; i < output.length; i++) {
		      output[i] = (float) (tmp[i] / sum);
		    }

		    return output;
		  }
	
	public static List<List<List<Entity>>> predict(String[] string_text) throws OrtException, IOException {
		TokensData result_tokens_of_strings = objectMyBertTokenier.getInputs(string_text);
		long[][] input_ids = result_tokens_of_strings.getInput_ids_list();
		long[][] token_type_ids = result_tokens_of_strings.getInput_segment_list();
		long[][] attention_mask = result_tokens_of_strings.getInput_masks_list();
		
		OnnxTensor input_ids_map = OnnxTensor.createTensor(env, input_ids);
		OnnxTensor token_type_ids_map = OnnxTensor.createTensor(env, token_type_ids);
		OnnxTensor attention_mask_map = OnnxTensor.createTensor(env, attention_mask);
		
		Map<String, OnnxTensor> map_data = new HashMap<>();
		map_data.put("input_ids", input_ids_map);
		map_data.put("token_type_ids", token_type_ids_map);
		map_data.put("attention_mask", attention_mask_map);
		
		Result out = session.run(map_data);
		//预测转化
		float[][][] one = (float[][][])out.get(0).getValue();
		int[][] result_ids = new int[input_ids.length][input_ids[0].length];
		for (int i = 0 ; i < result_ids.length ; i ++ ) {
			for (int j = 0 ; j < result_ids[i].length ; j++) {
				
				result_ids[i][j] = pred(one[i][j]);
				
			}
		}
		
		int[][] result_list = new int[one.length][one[0].length];
		for(int i = 0 ; i < one.length ; i++) {
			for (int j = 0; j < one[i].length;j++) {
				result_list[i][j] =  pred(one[i][j]);
			}
		}
		//预测结果
		List<List<List<Entity>>> result = ZH_OnToNotes_18_ResultToJson.predictPosProcess(string_text,result_list);
		
		return result;
		
	}
	public static void main(String[] args) throws OrtException, IOException {
		
		
		//注意   maxLen  外部控制   最大 256
		while(true) {
			Scanner scanner = new Scanner(System.in);
			String[] temp = {scanner.nextLine()};
			long begin = System.currentTimeMillis();
			List<List<List<Entity>>> result = predict(temp);
			long end = System.currentTimeMillis();
			System.out.println(result);
			System.out.println("程序运行的时间为："+(end-begin)+"ms");
			System.out.println();
		}
		
		
		
//		String[] string_text = {
//				"大家好,我是马金民毕业于华中科技大学18888888，我住在中国淮安yyyywww",
//				"大家好,我是王德发毕业于华中科技大学",
//				"张小明在武汉海关携带枪支进入中国境内"	
//		};
	}
}