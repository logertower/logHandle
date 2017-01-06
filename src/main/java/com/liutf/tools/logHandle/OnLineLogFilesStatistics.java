package com.liutf.tools.logHandle;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计错误日志类型和出现频率
 *
 * @author ltf
 * @create 2016-09-29 下午 5:28
 **/
public class OnLineLogFilesStatistics {

    /**
     * 输入文件目录
     * 【可修改】修改输出文件目录在此
     */
    public static final String outFileUrl = "E:\\test\\" + System.currentTimeMillis() + ".txt";//输出文件目录


    /**
     * 日志文件网络地址数组
     * 【可修改】修改遍历的日志在此处增加、删除或修改（此处删除了敏感信息，请修改后使用）
     */
    public static final String[] STR_URL_ARRAY = {
            "*******/100.151/log.txt",
            "*******/100.152/log.txt"
    };


    public static void main(String[] args) throws Exception {


        //日志文件网络地址数组
        String[] strUrlArray = STR_URL_ARRAY;

        //统计map
        Map<String, Integer> statisticsMap = new HashMap<String, Integer>();

        //读取，解析文件
        for (String strUrl : strUrlArray) {
            readLog(strUrl, statisticsMap);
        }

        //输出日志统计结果
        for (String key : statisticsMap.keySet()) {
            FileWriter fileWriter = new FileWriter(new File(outFileUrl), true);
            fileWriter.write("--------------------------------------------");
            fileWriter.write("\n");
            fileWriter.write("日志出现频率：" + statisticsMap.get(key));
            fileWriter.write("\n");
            fileWriter.write(key);
            fileWriter.write("\n");

            fileWriter.close();
        }

        System.out.println("输出目录：" + outFileUrl);

    }

    /**
     * 读取日志
     */
    public static void readLog(String strUrl, Map<String, Integer> statisticsMap) throws IOException {
        System.out.println("开始解析文件：" + strUrl);

        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream fis = conn.getInputStream();

        StringBuilder sb = new StringBuilder();

        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fis.read(bytes)) != -1) {
            sb.append(new String(bytes, 0, len, "UTF-8"));
        }

        String logFileText = sb.toString();

        analyzeLog(logFileText, statisticsMap);

        fis.close();

        System.out.println("结束解析文件：" + strUrl);
    }


    /**
     * 统计日志
     */
    public static void analyzeLog(String logFileText, Map<String, Integer> statisticsMap) throws IOException {

        //日期正则2016-09-29 05:20:50,038
        String regex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}\\s";
        String[] timeLogArray = logFileText.split(regex);
        for (String timeLog : timeLogArray) {
            if (StringUtils.isNotBlank(timeLog)) {

                String timeLogHeader = timeLog.split("\\[ERROR\\]")[0] + "[ERROR]";

                if (statisticsMap.containsKey(timeLogHeader)) {
                    statisticsMap.put(timeLogHeader, statisticsMap.get(timeLogHeader) + 1);
                } else {
                    statisticsMap.put(timeLogHeader, 1);
                }
            }
        }
    }

}
