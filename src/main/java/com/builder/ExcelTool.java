package com.builder;

import com.entity.Column;
import lombok.Data;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel处理工具
 * 概念-> 表头数据:报表的表头
 * 行内数据:表头以下的数据
 * 功能:动态生成单级，多级Excel表头
 * 备注：tree型结构数据的root节点的id必须为零（0）
 * Created by P丶少 on 2018/3/2.
 * 修改:
 * 2019/03/18 修复生成跨列的bug
 * 2019/03/20 修复集合存在root的时候，生成不了动态表头
 *
 * @param <T>
 */
@Data
public class ExcelTool<T> {

    /**
     * excel 对象
     */
    private HSSFWorkbook workbook;
    /**
     * 表格标题
     */
    private String title;
    /**
     * 单元格宽度
     */
    private int colWidth = 20;
    /**
     * 单元格行高度
     */
    private int rowHeight = 20;
    /**
     * 表头样式
     */
    private HSSFCellStyle styleHead;
    /**
     * 主体样式
     */
    private HSSFCellStyle styleBody;
    /**
     * 日期格式化,默认yyyy-MM-dd HH:mm:ss
     */
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 无参数 初始化 对象
     */
    public ExcelTool() {
        this.title = "sheet1";
        this.workbook = new HSSFWorkbook();
        init(0);
    }

    /**
     * 有参数 初始化 对象
     *
     * @param title
     * @param colWidth
     * @param rowHeight
     * @param dateFormat
     */
    public ExcelTool(String title, int colWidth, int rowHeight, String dateFormat) {
        this.colWidth = colWidth;
        this.rowHeight = rowHeight;
        this.title = title;
        this.workbook = new HSSFWorkbook();
        this.sdf = new SimpleDateFormat(dateFormat);
        init(0);
    }

    public ExcelTool(String title, int colWidth, int rowHeight) {
        this.colWidth = colWidth;
        this.rowHeight = rowHeight;
        this.title = title;
        this.workbook = new HSSFWorkbook();
        init(0);
    }

    public ExcelTool(String title, int colWidth, int rowHeight, int flag) {
        this.colWidth = colWidth;
        this.rowHeight = rowHeight;
        this.title = title;
        this.workbook = new HSSFWorkbook();
        init(flag);
    }

    public ExcelTool(String title) {
        this.title = title;
        this.workbook = new HSSFWorkbook();
        init(0);
    }

    //内部统一调用的样式初始化
    private void init(int styleFlag) {
        this.styleHead = this.workbook.createCellStyle();
        this.styleHead.setAlignment(HorizontalAlignment.CENTER);// 左右居中
        this.styleHead.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
        //this.styleHead.setRightBorderColor(HSSFColor.BLACK.index);
        //this.styleHead.setBottomBorderColor(HSSFColor.BLACK.index);
        switch (styleFlag) {
            case 1:
                this.styleBody = this.workbook.createCellStyle();
                this.styleBody.setAlignment(HorizontalAlignment.LEFT);// 左右居中ALIGN_CENTER
                this.styleBody.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
                this.styleBody.setBorderRight(BorderStyle.THIN);// 边框的大小
                this.styleBody.setBorderBottom(BorderStyle.THIN);// 边框的大小
                break;
            default:
                this.styleBody = this.workbook.createCellStyle();
                this.styleBody.setAlignment(HorizontalAlignment.CENTER);// 左右居中ALIGN_CENTER
                this.styleBody.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
                this.styleBody.setBorderRight(BorderStyle.THIN);// 边框的大小
                this.styleBody.setBorderBottom(BorderStyle.THIN);// 边框的大小
                break;
        }
    }

    /**
     * 导出表格 无返回
     *
     * @param headerCellList 表头数据
     * @param rowList        行内数据
     * @param filePath       保存路径
     * @param flag           是否加载行数据
     * @throws Exception
     */
    public void exportExcel(List<Column> headerCellList, List<T> rowList, String filePath, boolean flag) throws Exception {
        splitDataToSheets(rowList, headerCellList, flag, false);
        save(this.workbook, filePath);
    }

    /**
     * 导出表格 无返回
     *
     * @param headerCellList 表头数据
     * @param rowList        行内数据
     * @param FilePath       保存路径
     * @param flag           是否加载行数据
     * @param rowFlag
     * @throws Exception
     */
    public void exportExcel(List<Column> headerCellList, List<T> rowList, String FilePath, boolean flag, boolean rowFlag) throws Exception {
        splitDataToSheets(rowList, headerCellList, flag, rowFlag);
        save(this.workbook, FilePath);
    }

    /**
     * 返回workbook
     *
     * @param listTpamscolumn 表头数据
     * @param datas           行内数据
     * @param flag            是否写入行内数据
     * @return
     * @throws Exception
     */
    public HSSFWorkbook exportWorkbook(List<Column> listTpamscolumn, List<T> datas, boolean flag) throws Exception {
        splitDataToSheets(datas, listTpamscolumn, flag, false);
        return this.workbook;
    }

    /**
     * 导出表格 有返回值
     *
     * @param headerCellList 表头数据
     * @param datas           行内数据
     * @param flag            只输出表头数据
     * @param rowFlag
     * @return
     * @throws Exception
     */
    public InputStream exportExcel(List<Column> headerCellList, List<T> datas, boolean flag, boolean rowFlag) throws Exception {
        splitDataToSheets(datas, headerCellList, flag, rowFlag);
        return save(this.workbook);
    }

    /**
     * 导出Excel,适用于web导出excel
     *
     * @param sheet           excel
     * @param data            行内数据
     * @param headerCellList 表头数据
     * @param flag            只输出表头数据
     * @param rowFlag         输出展示数据的结构(表头下面行的数据)
     * @throws Exception
     */
    private void writeSheet(HSSFSheet sheet, List<T> data, List<Column> headerCellList, boolean flag, boolean rowFlag) throws Exception {
        sheet.setDefaultColumnWidth(colWidth);
        sheet.setDefaultRowHeightInPoints(rowHeight);
        sheet = createHead(sheet, headerCellList.get(0).getTotalRow(), headerCellList.get(0).getTotalCol());
        createHead(headerCellList, sheet, 0);
        if (flag) {
            writeSheetContent(headerCellList, data, sheet, headerCellList.get(0).getTotalRow(), rowFlag);
        }
    }

    /**
     * 拆分sheet，因为每个sheet不能超过65535，否则会报异常
     *
     * @param data            行内数据
     * @param headerCellList 表头数据
     * @param flag            只输出表头数据
     * @param rowFlag         输出展示数据的结构(表头下面行的数据)
     * @throws Exception
     */
    private void splitDataToSheets(List<T> data, List<Column> headerCellList, boolean flag, boolean rowFlag) throws Exception {
        int dataCount = data.size();
        int maxColumn = 65535;
        int pieces = dataCount / maxColumn;
        for (int i = 1; i <= pieces; i++) {
            HSSFSheet sheet = this.workbook.createSheet(this.title + i);
            List<T> subList = data.subList((i - 1) * maxColumn, i * maxColumn);
            writeSheet(sheet, subList, headerCellList, flag, rowFlag);
        }
        HSSFSheet sheet = this.workbook.createSheet(this.title + (pieces + 1));
        writeSheet(sheet, data.subList(pieces * maxColumn, dataCount), headerCellList, flag, rowFlag);
    }

    /**
     * 把数据写入到单元格
     *
     * @param headerCellList 表头数据
     * @param datas           行内数据
     * @param sheet           工作表（excel分页）
     * @throws Exception void
     */
    private void writeSheetContent(List<Column> headerCellList, List<T> datas, HSSFSheet sheet, int rowIndex, boolean rowFlag) throws Exception {
        HSSFRow row = null;
        List<Column> listCol = new ArrayList<>();
        rowFlag = false;
        if (rowFlag) {//暂时没有用 后面扩展用
            for (int i = 0, index = rowIndex; i < datas.size(); i++, index++) {
                row = sheet.createRow(index);//创建行
                for (int j = 0; j < headerCellList.size(); j++) {
                    createColl(row, j, headerCellList.get(j).getFieldName(), datas.get(i));
                }
            }
        } else {
            getColumnList(headerCellList, listCol);
            for (int i = 0, index = rowIndex; i < datas.size(); i++, index++) {
                row = sheet.createRow(index);//创建行
                for (int j = 0; j < listCol.size(); j++) {
                    Column c = listCol.get(j);
                    createCol(row, c, datas.get(i));
                }

            }
        }
    }

    /**
     * 根据list 来创建单元格 暂时没有用
     *
     * @param row
     * @param j
     * @param finame
     * @param t
     */
    private void createColl(HSSFRow row, int j, String finame, T t) {
        HSSFCell cell = row.createCell(j);  //创建单元格
        cell.setCellStyle(this.styleBody); //设置单元格样式
        String text = "";
        if (t instanceof List) {
            List<Map> temp = (List<Map>) t;
            if (j >= temp.size()) {
                return;
            }
            text = String.valueOf(temp.get(j).get(finame) == null ? "" : temp.get(j).get(finame));
        }
        HSSFRichTextString richString = new HSSFRichTextString(text);
        cell.setCellValue(richString);
    }

    /**
     * 把column的columnList整理成一个list<column> 过滤表头的脏数据
     *
     * @param list    表头数据
     * @param listCol 返回新的list
     */
    private void getColumnList(List<Column> list, List<Column> listCol) {
        for (Column column : list) {
            if (column.getFieldName() != null) {
                listCol.add(column);
            }
            List<Column> listChildren = column.getCellList();
            if (listChildren.size() > 0) {
                getColumnList(listChildren, listCol);
            }
        }
    }

    /**
     * 保存Excel到InputStream，此方法适合web导出excel
     *
     * @param workbook
     * @return
     */
    private InputStream save(HSSFWorkbook workbook) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            InputStream bis = new ByteArrayInputStream(bos.toByteArray());
            return bis;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存excel到本机指定的路径
     *
     * @param workbook
     * @param filePath
     * @throws IOException
     */
    private void save(HSSFWorkbook workbook, String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            workbook.write(fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (null != fOut) {
                fOut.close();
            }
        } catch (Exception e1) {
        }
    }

    /**
     * 创建行
     *
     * @param row         Excel对应的行
     * @param tpamscolumn 当前单元格属性
     * @param v
     * @param j
     * @return
     * @throws Exception
     */
    public int createRowVal(HSSFRow row, Column tpamscolumn, T v, int j) throws Exception {
        //遍历标题
        if (tpamscolumn.getCellList() != null && tpamscolumn.getCellList().size() > 0) {
            for (int i = 0; i < tpamscolumn.getCellList().size(); i++) {
                createRowVal(row, tpamscolumn.getCellList().get(i), v, j);
            }
        } else {
            createCol(row, tpamscolumn, v);
        }
        return j;

    }

    /**
     * 创建单元格
     *
     * @param row         Excel对应的行
     * @param tpamscolumn 当前单元格对象
     * @param v
     * @throws Exception
     */
    public void createCol(HSSFRow row, Column tpamscolumn, T v) throws Exception {
        HSSFCell cell = row.createCell(tpamscolumn.getCol());  //创建单元格
        cell.setCellStyle(this.styleBody); //设置单元格样式
        final Object[] value = {null};
        if (v instanceof Map) {
            Map m = (Map) v;
            m.forEach((k, val) -> {
                if (k.equals(tpamscolumn.getFieldName()) && !tpamscolumn.isHasChildren()) {
                    value[0] = val;
                }
            });
        } else {
            Class<?> cls = v.getClass();// 拿到该类
            Field[] fields = cls.getDeclaredFields();// 获取实体类的所有属性，返回Field数组
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true); // 设置些属性是可以访问的
                if (tpamscolumn.getFieldName().equals(f.getName()) && !tpamscolumn.isHasChildren())// && !tpamscolumn.isHasChilren()
                {
                    value[0] = f.get(v);
                }
                if (value[0] instanceof Date) {
                    value[0] = parseDate((Date) value[0]);
                }
            }
        }
        if (value[0] != null) {
            HSSFRichTextString richString = new HSSFRichTextString(value[0].toString());
            cell.setCellValue(richString);
        }

    }

    /**
     * 时间转换
     *
     * @param date
     * @return String
     */
    private String parseDate(Date date) {
        String dateStr = "";
        try {
            dateStr = this.sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }


    /**
     * 根据数据的行数和列数，在excel创建单元格cell
     *
     * @param sheetCo excel分页
     * @param r       excel 行数
     * @param c       excel 列数
     * @return
     */
    public HSSFSheet createHead(HSSFSheet sheetCo, int r, int c) {
        for (int i = 0; i < r; i++) {
            HSSFRow row = sheetCo.createRow(i);
            for (int j = 0; j < c; j++) {
                HSSFCell cell = row.createCell(j);
            }
        }
        return sheetCo;
    }

    /**
     * 使用递归 在excel写入表头数据 支持单级，多级表头的创建
     *
     * @param cellList 表头数据
     * @param sheetCo  哪个分页
     * @param rowIndex 当前Excel的第几行
     */
    public void createHead(List<Column> cellList, HSSFSheet sheetCo, int rowIndex) {
        HSSFRow row = sheetCo.getRow(rowIndex);
//        if(row == null)row = sheetCo.createRow(rowIndex);
        int len = cellList.size();//当前行 有多少列
        for (int i = 0; i < len; i++) {//i是headers的索引，n是Excel的索引 多级表头
            Column tpamscolumn = cellList.get(i);
            //创建这一行的第几列单元格
            int r = tpamscolumn.getRow();
            int rLen = tpamscolumn.getRLen();
            int c = tpamscolumn.getCol();
            int cLen = tpamscolumn.getCLen();
            int endR = r + rLen;
            //解决表头导出时多一行问题
            if(endR > r){
                endR--;
            }
            int endC = c + cLen;
            if (endC > c) {
                endC--;
            }
            HSSFCell cell = row.getCell(c);
            HSSFRichTextString text = new HSSFRichTextString(tpamscolumn.getContent());
            cell.setCellStyle(this.styleHead); //设置表头样式
            cell.setCellValue(text);
            // 合并单元格
            CellRangeAddress cra = new CellRangeAddress(r, endR, c, endC);
            //todo debug
            if (cra.getNumberOfCells() > 1) {
                sheetCo.addMergedRegion(cra);
            }

            // 使用RegionUtil类为合并后的单元格添加边框
            RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheetCo); // 下边框
            RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheetCo); // 左边框
            RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheetCo); // 有边框

            if (tpamscolumn.isHasChildren()) {
                rowIndex = r + 1;
                createHead(tpamscolumn.getCellList(), sheetCo, rowIndex);
            }
        }
    }


    /**
     * 转换成column对象
     * 支持List<T>的数据结构:map String ，只能是单级的数据
     *
     * @param list 需要转换的数据
     * @return
     */
    public List<Column> columnTransformer(List<T> list) {
        List<Column> lc = new ArrayList<>();
        if (list.get(0) instanceof Map) {
            final int[] i = {1};
            for (Map<String, String> m : (List<Map<String, String>>) list) {
                m.forEach((k, val) -> {
                    Column tpamscolumn = new Column();
                    tpamscolumn.setId(String.valueOf(i[0]));
                    tpamscolumn.setPid("0");
                    tpamscolumn.setContent(k);
                    tpamscolumn.setFieldName(val);
                    lc.add(tpamscolumn);
                    i[0]++;
                });
            }
        } else {
            int i = 1;
            for (String s : (List<String>) list) {
                Column tpamscolumn = new Column();
                tpamscolumn.setId(String.valueOf(i));
                tpamscolumn.setPid("0");
                tpamscolumn.setContent(s);
                tpamscolumn.setFieldName(null);
                lc.add(tpamscolumn);
                i++;
            }
        }
        setParm(lc, "0");//处理一下
        List<Column> s = TreeTool.buildByRecursive(lc, "0");
        setColNum(lc, s, s);
        return s;
    }

    /**
     * 转换成column对象 返回tree数据结构
     * 支持：List<map>、某个具体对象（entity）数据的转换
     *
     * @param list      需要转换的数据
     * @param id        当前节点id 字段的名称  主键
     * @param pid       父节点id 字段的名称
     * @param content   填写表头单元格内容的 字段名称
     * @param fieldName 填写行内数据对的 字段名称
     * @param rootid    rootid的值
     * @return
     * @throws Exception
     */
    public List<Column> columnTransformer(List<T> list, String id, String pid, String content, String fieldName, String rootid) throws Exception {
        List<Column> lc = new ArrayList<>();
        if (list.get(0) instanceof Map) {
            for (Map m : (List<Map>) list) {
                Column tpamscolumn = new Column();
                //java8 以上的遍历方式
                m.forEach((k, val) -> {
                    if (id.equals(k)) {
                        tpamscolumn.setId(String.valueOf(val));
                    }
                    if (pid.equals(k)) {
                        tpamscolumn.setPid((String) val);
                    }
                    if (content.equals(k)) {
                        tpamscolumn.setContent((String) val);
                    }
                    if (fieldName != null && fieldName.equals(k)) {
                        tpamscolumn.setFieldName((String) val);
                    }
                });
                lc.add(tpamscolumn);
            }
        } else {
            for (T t : list) {//反射
                Column tpamscolumn = new Column();
                Class cls = t.getClass();
                Field[] fs = cls.getDeclaredFields();
                for (int i = 0; i < fs.length; i++) {
                    Field f = fs[i];
                    f.setAccessible(true); // 设置些属性是可以访问的
                    if (id.equals(f.getName()) && f.get(t) != null) {
                        tpamscolumn.setId(f.get(t).toString());
                    }
                    if (pid.equals(f.getName()) && f.get(t) != null) {
                        tpamscolumn.setPid(f.get(t).toString());
                    }
                    //if (pid.equals(f.getName()) && ( f.get(t) == null || "".equals(f.get(t)))) tpamscolumn.setPid("0");
                    if (content.equals(f.getName()) && f.get(t) != null) {
                        tpamscolumn.setContent(f.get(t).toString());
                    }
                    if (f.get(t) != null && fieldName != null && fieldName.equals(f.getName())) {
                        tpamscolumn.setFieldName(f.get(t).toString());
                    }
                }
                lc.add(tpamscolumn);
            }
        }
        setParm(lc, rootid);//处理一下
        List<Column> s = TreeTool.buildByRecursive(lc, rootid);
        setColNum(lc, s, s);
        return s;
    }

    /**
     * 设置基础的参数
     *
     * @param list
     */
    public static void setParm(List<Column> list, String rootid) {
        int row = 0;//excel第几行
        int rLen = 0; //excel 跨多少行
        int totalRow = TreeTool.getMaxStep(list);
        int totalCol = TreeTool.getDownChildren(list, rootid);
        for (int i = 0; i < list.size(); i++) {
            Column poit = list.get(i);
            int tree_step = TreeTool.getTreeStep(list, poit.getPid(), 0);//往上遍历tree
            poit.setTreeStep(tree_step);
            poit.setRow(tree_step);//设置第几行
            //判断是否有节点
            boolean hasCh = TreeTool.hasChild(list, poit);
            poit.setHasChildren(hasCh);
            if (hasCh) {
                poit.setRLen(0);//设置跨多少行
            } else {
                if (tree_step < totalRow) {
                    rLen = totalRow - tree_step;
                }
                poit.setRLen(rLen);
            }
//            boolean flag=false;//控制只有root 节点才有总的行数信息
//            if(rootid == null && rootid == poit.getId() )flag = true;
//            if(rootid != null && rootid.equals(poit.getId()))flag = true;
//            if(flag){
//
//            }
            poit.setTotalRow(totalRow);
            poit.setTotalCol(totalCol);
        }
    }

    /**
     * 设置基础的参数
     *
     * @param list     所有list数据，一条一条
     * @param treeList 转成tree结构的list
     */
    public static void setColNum(List<Column> list, List<Column> treeList, List<Column> flist) {
//        int col = pcIndex;//excel第几列
//        int cLen ;//xcel跨多少列
        List<Column> new_list = new ArrayList<>();//新的遍历list
        for (int i = 0; i < treeList.size(); i++) {
            Column poit = treeList.get(i);
//            String temp_id = TreeTool.getStepFid(list,poit.getId() ,1);
            int col = TreeTool.getFCol(list, poit.getPid()).getCol();
            int brotherCol = TreeTool.getBrotherChilNum(list, poit);
            poit.setCol(col + brotherCol);
            int cLen = TreeTool.getDownChildren(list, poit.getId());
            if (cLen <= 1) {
                cLen = 0;
            }
//            else  cLen--;
            poit.setCLen(cLen);//设置跨多少列
            if (poit.getCellList().size() > 0) {
                new_list.addAll(poit.getCellList());
            }
        }
        if (new_list.size() > 0) {
            setColNum(list, new_list, flist);
        }
    }
//========上部分是导出excel的使用（生成excel），下部分是解析excel，由于excel导入==================================================================================================================================

    /**
     * 根据HSSFCell类型设置数据
     *
     * @param cell 单元格
     * @return
     */
    public static String getCellFormatValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            switch (cell.getCellType()) { // 判断当前Cell的Type

                case NUMERIC:  // 如果当前Cell的Type为NUMERIC
                case FORMULA: {
                    // 判断当前的cell是否为Date
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = sdf.format(date);
                    } else { // 如果是纯数字
                        cellvalue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case STRING:  // 如果当前Cell的Type为STRIN
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                default:  // 默认的Cell值
                    cellvalue = "";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     *
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbookType(InputStream inStr, String fileName) throws Exception {
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (".xls".equals(fileType)) {
            wb = new HSSFWorkbook(inStr);  //2003-
        } else if (".xlsx".equals(fileType)) {
            wb = new XSSFWorkbook(inStr);  //2007+
        } else {
            throw new Exception("导入格式错误");
        }
        return wb;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private static String getStringCellValue(Cell cell) {
        String strCell = "";
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                strCell = cell.getStringCellValue().trim();
                break;
            case NUMERIC:
                strCell = String.valueOf(cell.getNumericCellValue()).trim();
                break;
            case BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            //case BLANK:
            //    strCell = "";
            //    break;
            default:
                strCell = "";
                break;
        }
        if ("".equals(strCell)) {
            return "";
        }
        return strCell;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    private boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    private String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getStringCellValue(fCell);
                }
            }
        }
        return "";
    }

    /**
     * 获取excel的值 返回的 List<List<String>>的数据结构
     *
     * @param fileUrl  文件路径
     * @param sheetNum 工作表（第几分页[1,2,3.....]）
     * @return List<List < String>>
     */
    public List<List<String>> getExcelValues(String fileUrl, int sheetNum) throws Exception {
        List<List<String>> values = new ArrayList<List<String>>();
        File file = new File(fileUrl);
        InputStream is = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(is);
        int sheetCount = sheetNum - 1; //workbook.getNumberOfSheets();//sheet 数量,可以只读取手动指定的sheet页
        //int sheetCount1= workbook.getNumberOfSheets();
        Sheet sheet = workbook.getSheetAt(sheetCount); //读取第几个工作表sheet
        int rowNum = sheet.getLastRowNum();//有多少行
        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);//第i行
            if (row == null) {//过滤空行
                continue;
            }
            List<String> list = new ArrayList<>();
            int colCount = sheet.getRow(0).getLastCellNum();//用表头去算有多少列，不然从下面的行计算列的话，空的就不算了
            for (int j = 0; j < colCount; j++) {//第j列://+1是因为最后一列是空 也算进去
                Cell cell = row.getCell(j);
                String cellValue;
                boolean isMerge = false;
                if (cell != null) {
                    isMerge = isMergedRegion(sheet, i, cell.getColumnIndex());
                }
                //判断是否具有合并单元格
                if (isMerge) {
                    cellValue = getMergedRegionValue(sheet, row.getRowNum(), cell.getColumnIndex());
                } else {
                    cellValue = getStringCellValue(cell);
                }
                list.add(cellValue);
            }
            values.add(list);
        }
        return values;
    }

    /**
     * 判断整行是否为空
     *
     * @param row    excel得行对象
     * @param maxRow 有效值得最大列数
     */
    private static boolean CheckRowNull(Row row, int maxRow) {
        int num = 0;
        for (int j = 0; j < maxRow; j++) {
            Cell cell = row.getCell(j);
            if (cell == null || "".equals(cell) || cell.getCellType() == CellType.BLANK) {
                num++;
            }
        }
        if (maxRow == num) {
            return true;
        }
        return false;
    }

    /**
     * 根据sheet数获取excel的值 返回List<List<Map<String,String>>>的数据结构
     *
     * @param fileUrl  文件路径
     * @param sheetNum 工作表（第几分页[1,2,3.....]）
     * @return List<List < Map < String, String>>>
     */
    public List<List<Map<String, String>>> getExcelMapVal(String fileUrl, int sheetNum) throws Exception {
        List<List<Map<String, String>>> values = new ArrayList<List<Map<String, String>>>();
        File file = new File(fileUrl);
        InputStream is = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(is);
        int sheetCount = sheetNum - 1; //workbook.getNumberOfSheets();//sheet 数量,可以只读取手动指定的sheet页
        //int sheetCount1= workbook.getNumberOfSheets();
        Sheet sheet = workbook.getSheetAt(sheetCount); //读取第几个工作表sheet
        int rowNum = sheet.getLastRowNum();//有多少行
        Row rowTitle = sheet.getRow(0);//第i行
        int colCount = sheet.getRow(0).getLastCellNum();//用表头去算有多少列，不然从下面的行计算列的话，空的就不算了
        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);//第i行
            if (row == null || CheckRowNull(row, colCount)) {//过滤空行
                continue;
            }
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (int j = 0; j < colCount; j++) {//第j列://+1是因为最后一列是空 也算进去
                Map<String, String> map = new HashMap<>();
                Cell cell = row.getCell(j);
                Cell cellTitle = rowTitle.getCell(j);
                String cellValue;
                String cellKey = getStringCellValue(cellTitle);
                boolean isMerge = false;
                if (cell != null) {
                    isMerge = isMergedRegion(sheet, i, cell.getColumnIndex());
                }
                //判断是否具有合并单元格
                if (isMerge) {
                    cellValue = getMergedRegionValue(sheet, row.getRowNum(), cell.getColumnIndex());
                } else {
                    cellValue = getStringCellValue(cell);
                }
                map.put(cellKey, cellValue);
                list.add(map);
            }
            values.add(list);
        }
        return values;
    }

    /**
     * 获取当前excel的工作表sheet总数
     *
     * @param fileUrl
     * @return
     * @throws Exception
     */
    public int hasSheetCount(String fileUrl) throws Exception {
        File file = new File(fileUrl);
        InputStream is = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(is);
        int sheetCount = workbook.getNumberOfSheets();
        return sheetCount;
    }
}
