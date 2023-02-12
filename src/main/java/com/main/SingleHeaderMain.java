package com.main;

import com.builder.ExcelTool;
import com.entity.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleHeaderMain {
    public static void main(String[] args) throws Exception {
        //单级的表头==============================================================
        Map<String, String> map = new HashMap<String, String>();
        map.put("登录名", "u_login_id");
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("用户名", "u_name");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("角色", "u_role");
        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("部门", "u_dep");
        Map<String, String> map4 = new HashMap<String, String>();
        map4.put("用户类型", "u_type");
        List<Map<String, String>> titleList = new ArrayList<>();
        titleList.add(map);
        titleList.add(map1);
        titleList.add(map2);
        titleList.add(map3);
        titleList.add(map4);
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
        ExcelTool excelTool = new ExcelTool("单级表头的表格", 15, 20);
        List<Column> titleData = excelTool.columnTransformer(titleList);
        excelTool.exportExcel(titleData, rowList, "D://outExcel-single.xls", true);
    }
}
