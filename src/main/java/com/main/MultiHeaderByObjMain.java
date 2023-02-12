package com.main;

import com.builder.ExcelTool;
import com.entity.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  根据对象<Entity>的多级表头数据生成excel
 */
public class MultiHeaderByObjMain {
    public static void main(String[] args) throws Exception {
        //实体类（entity）数据 多级表头,数据如下:===============================================================================
        //        登录名  姓名       aa
        //                      角色    部门
        List<TitleEntity> titleList = new ArrayList<>();
        TitleEntity titleEntity0 = new TitleEntity("0", null, "总表", null);
        TitleEntity titleEntity = new TitleEntity("11", "0", "登录名2", "u_login_id");
        TitleEntity titleEntity1 = new TitleEntity("1", "0", "姓名", "u_name");
        TitleEntity titleEntity11 = new TitleEntity("1_1", "1", "姓名1", "u_name");
        TitleEntity titleEntity2 = new TitleEntity("2", "0", "角色加部门", null);
        TitleEntity titleEntity3 = new TitleEntity("3", "2", "角色", "u_role");
        TitleEntity titleEntity4 = new TitleEntity("4", "2", "部门", "u_dep");
        TitleEntity titleEntity5 = new TitleEntity("33", "0", "角色加部门1", null);
        TitleEntity titleEntity6 = new TitleEntity("33_1", "33", "角色33", "u_role");
        TitleEntity titleEntity7 = new TitleEntity("33_2", "33_1", "部门33", "u_dep");
        TitleEntity titleEntity8 = new TitleEntity("44", "0", "角色加部门2", null);
        TitleEntity titleEntity9 = new TitleEntity("44_1", "44", "角色44", "u_role");
        TitleEntity titleEntity10 = new TitleEntity("44_2", "44", "部门44", "u_dep");
        TitleEntity titleEntity12 = new TitleEntity("44_3", "44_2", "44_2", "u_dep");
        titleList.add(titleEntity0);
        titleList.add(titleEntity);
        titleList.add(titleEntity1);
        titleList.add(titleEntity2);
        titleList.add(titleEntity3);
        titleList.add(titleEntity4);
        titleList.add(titleEntity5);
        titleList.add(titleEntity6);
        titleList.add(titleEntity7);
        titleList.add(titleEntity8);
        titleList.add(titleEntity9);
        titleList.add(titleEntity10);
        titleList.add(titleEntity11);
        titleList.add(titleEntity12);
        //单级的 行内数据
        List<Map<String, String>> rowList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("u_login_id", "登录名" + i);
            m.put("u_name", "张三" + i);
            m.put("u_role", "角色" + i);
            m.put("u_dep", "部门" + i);
            m.put("u_type", "用户类型" + i);
            rowList.add(m);
        }
        ExcelTool excelTool = new ExcelTool("实体类（entity）数据 多级表头表格", 20, 20);
        List<Column> titleData = excelTool.columnTransformer(titleList, "id", "pid", "content", "fieldName", "0");
        excelTool.exportExcel(titleData, rowList, "D://outExcel-multiObj.xls", true, true);
    }
}
