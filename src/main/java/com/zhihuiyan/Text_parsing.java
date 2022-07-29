package com.zhihuiyan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class Text_parsing {

	public static void sentPost(String url, Integer id,String content ) {
        //定义发送数据
        JSONObject param = new JSONObject();
        param.put( "id"   , id.toString());
        param.put( "text" , content);

        //定义接收数据
        JSONObject result = new JSONObject();
         
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        //请求参数转JOSN字符串

        StringEntity entity = new StringEntity(param.toString(), "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        try {
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = JSON.parseObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
                System.out.println(url);
                System.out.println(result);
                //System.out.println(result.getClass());
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.put("error", "连接错误！");

        }
    }
		
	
    public static void main(String[] args) {
        //数字检测：证件之类
//        String url = "http://192.168.4.179:5000/numbers_detect";
//    	String content = "张小明,男 , 身份证号432511199017233345，0517-85344958,电话18888888888,2021-07-01在武汉海关携带枪支进入中国境内####张小明,男 , 身份证号32088219950825316X####ssdfdsf";
//        sentPost(url, 1, content);
//        System.out.println("-------------------------");
        //危险物品检测：枪支 炸弹 之类
        //String url = "http://192.168.4.179:5000/danger_items_detect";
//        url = "http://192.168.4.179:5000/danger_items_detect_english";
//    	//content = "2021-07-01在武汉海关携带枪支进入中国境内张小明,男 , 身份证号432511199017233345####炸弹####";
//        content = "gun,18888888888,The Strawberry Supermoon\" rises against Dufu River Pavilion in Changsha, central China Hunan Province, June 14, 2022. (Photo/VCG)####The \"Strawberry Supermoon\" lights up the sky in Nanjing, east China Jiangsu Province, June 14, 2022. (Photo: China News Service/Yang Bo)";
//        sentPost(url, 2, content);
        
//        url = "http://192.168.4.179:5000/danger_items_detect_chinese";
//    	//content = "2021-07-01在武汉海关携带枪支进入中国境内张小明,男 , 身份证号432511199017233345####炸弹####";
//        content = "在武汉海关携带枪支进入中国境内";
//        sentPost(url, 2, content);
//        System.out.println("-------------------------");
        //中文实体 模型 chinese_robert_clue  10类
        //String url = "http://192.168.4.179:5000/chinese_robert_clue";
//        url = "http://192.168.4.179:5000/chinese_robert_clue";
//        content = "中国世界第一,19801150992,32088219950825361X ####0517-85344958,中国世界第一####张小明,男，身份证号432511199017233345，0517-85344958,电话18888888888,2021-07-01在武汉海关携带枪支进入中国境内";
//        sentPost(url, 3, content);
        //中文实体 模型 chinese_zh_albert_ner 18类
        //String url = "http://192.168.4.179:5000/chinese_zh_albert_ner";
//        url = "http://192.168.4.179:5000/chinese_zh_albert_ner";
//        content = "中国世界第一,19801150992,32088219950825361X ####0517-85344958,中国世界第一####张小明,男，身份证号432511199017233345，0517-85344958,电话18888888888,2021-07-01在武汉海关携带枪支进入中国境内";
//        sentPost(url, 4, content);
        
        //中文实体识别 all
        /*
         * 包含 chinese_zh_albert_ner（18类）、chinese_robert_clue（10类）、数字识别、危险物品识别
         * */
        //String url = "http://192.168.4.179:5000//chinese_all";
       
        String url  = "http://192.168.4.175:5000/chinese_all";
        String content = "6月23日，呼和浩特海关隶属白塔机场海关邮件监管现场对来自英国的邮单号为UH470831435GB、UH484263445GB、UH470831344GB的邮件查验，查获电话卡150张，收件地址为内蒙古赤峰市同一地址：ZhengjiangPdwIndustrialCoLtdChina；13947657021,RuYiJiaYuan,13dong121；YuanBaoShanQu,PingZhuangZhenChifeng；024076China。该邮件无收件人姓名。申报信息均为“50giffgaffsims”。";
        sentPost(url, 5, content);
        
        //英文实体识别 
        //url = "http://192.168.4.179:5000///english";
        url = "http://192.168.4.175:5000/english";
        content = "ZhengjiangPdwIndustrialCoLtdChina；";
        sentPost(url, 6, content);
        
        //英文识别识别 all
        
        /*
         * 包含 英文模型18类、数字识别、危险物品识别
         * */
        //String url = "http://192.168.4.179:5000///english_all";
//        url = "http://192.168.4.179:5000/english_all";
//        content = "gun,18888888888,The Strawberry Supermoon\" rises against Dufu River Pavilion in Changsha, central China Hunan Province, June 14, 2022. (Photo/VCG)####The \"Strawberry Supermoon\" lights up the sky in Nanjing, east China Jiangsu Province, June 14, 2022. (Photo: China News Service/Yang Bo)";
//        sentPost(url, 6, content);
        
        
    }

}