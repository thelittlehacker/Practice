package com.example.machinetest.payload;

import java.util.List;

public class Response {
    private String fileName;
    private List<String> columnName;

    public Response(String fileName, List<String> columnName) {
        this.fileName = fileName;
        this.columnName = columnName;
    }

    public Response() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getColumnName() {
        return columnName;
    }

    public void setColumnName(List<String> columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return "Response{" +
                "fileName='" + fileName + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
    }
}


