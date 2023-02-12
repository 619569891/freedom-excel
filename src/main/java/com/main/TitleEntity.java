package com.main;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 表头的实体类： 在具体的项目里，可以是你从数据库里查询出来的数据
 * @author P丶少
 */
@Data
@AllArgsConstructor
public class TitleEntity {
    public  String id;
    public  String pid;
    //内容
    public  String content;
    //字段
    public  String fieldName;

    public TitleEntity(){}

}
