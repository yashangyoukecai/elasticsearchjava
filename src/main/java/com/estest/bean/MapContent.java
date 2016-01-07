package com.estest.bean;

/**
 * Created by wklmogujie on 16/1/7.
 */
public class MapContent {
    //字段名
    private String field;
    //字段类型
    private String type;
    //分词规则
    private String index;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
