package xyz.liulingtong.model;

import xyz.liulingtong.enums.DataType;

import java.util.List;

public class DataMeta {
    private String key;
    /**
     * 0为未删除，1为已删除
     */
    private Integer delete;
    private DataType dataType;
    private List<DataLocation> dataLocations;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getDelete() {
        return delete;
    }

    public void setDelete(Integer delete) {
        this.delete = delete;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public List<DataLocation> getDataLocations() {
        return dataLocations;
    }

    public void setDataLocations(List<DataLocation> dataLocations) {
        this.dataLocations = dataLocations;
    }
}
