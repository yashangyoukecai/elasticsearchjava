package com.estest.esDao;


import com.estest.bean.Medicine;
import com.estest.tool.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wklmogujie on 16/1/4.
 */
public class DataFactory {

    public static DataFactory dataFactory = new DataFactory();

    private DataFactory(){

    }

    public DataFactory getInstance(){
        return dataFactory;
    }

    public static List<String> getInitJsonData(){
        List<String> list = new ArrayList<String>();
        String data1  = JsonUtil.obj2JsonData(new Medicine(11, "k", "k"));
        String data2  = JsonUtil.obj2JsonData(new Medicine(12, "l", "l"));
        String data3  = JsonUtil.obj2JsonData(new Medicine(13, "m", "m"));
        String data4  = JsonUtil.obj2JsonData(new Medicine(14, "n", "n"));
        String data5  = JsonUtil.obj2JsonData(new Medicine(15, "o", "o"));
        list.add(data1);
        list.add(data2);
        list.add(data3);
        list.add(data4);
        list.add(data5);
        return list;
    }
}
