package com.estest.bean;

/**
 * Created by wklmogujie on 16/1/4.
 */
public class Medicine {

    private Integer id;
    private String name;
    private String function;

    public Medicine() {
        super();
    }

    public Medicine(Integer id, String name, String function) {
        super();
        this.id = id;
        this.name = name;
        this.function = function;
    }

    //getter and  setter ()


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFunction() {
        return function;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
