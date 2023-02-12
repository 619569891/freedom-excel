package com.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by P丶少 on 2023/2/12.
 */
@Data
public class Column {
    /**
     * 单元格内容
     */
    private String content;
    /**
     * 字段名称，用户导出表格时反射调用
     */
    private String fieldName;
    /**
     * 这个单元格的集合
     */
    private List<Column> cellList = new ArrayList<Column>();

    private int totalRow;
    private int totalCol;
    /**
     * excel第几行
     */
    private int row;
    /**
     * excel第几列
     */
    private int col;
    /**
     * excel 跨多少行
     */
    private int rLen;
    /**
     * excel跨多少列
     */
    private int cLen;
    /**
     * 是否有子节点
     */
    private boolean hasChildren;
    /**
     * 树的级别 从0开始
     */
    private int treeStep;
    private String id;
    private String pid;

    public Column() {
    }

    public Column(String content, String fieldName) {
        this.content = content;
        this.fieldName = fieldName;
    }

    public Column(String fieldName, String content, int treeStep) {
        this.treeStep = treeStep;
        this.fieldName = fieldName;
        this.content = content;
    }
}