package com.zhqc.business.generator.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

public class ExcelUtil {

    /**
     * 导出Excel
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param wb HSSFWorkbook对象
     * @return
     */
    public static XSSFWorkbook getHSSFWorkbook(String sheetName,String []title,String [][]values, XSSFWorkbook wb){

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new XSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        XSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        XSSFRow row = sheet.createRow(0);

        //声明列对象
        XSSFCell cell = null;
        XSSFDrawing p=sheet.createDrawingPatriarch();
        String lastCell = getLastCell(title.length);
        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            if(i == 0){
                //获取批注对象
                //(int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2, int row2)
                //前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
                XSSFComment comment = p.createCellComment(new XSSFClientAnchor(0, 0, 0,
                        0, (short) 3, 3, (short) 5, 6));
                // 输入批注信息
                comment.setString(new XSSFRichTextString("jx:area(lastCell=\""+lastCell+"\")\n" +
                        "jx:each(items=\"list\" var=\"vo\" lastCell=\""+lastCell+"\" multisheet=\"sheetNames\")"));
                // 添加作者,选中B5单元格,看状态栏
                cell.setCellComment(comment);
            }
            cell.setCellValue(title[i]);
        }

        //创建内容
        for(int i=0;i<values.length;i++){
            row = sheet.createRow(i + 1);
            for(int j=0;j<values[i].length;j++){
                //将内容按顺序赋给对应的列对象
                cell = row.createCell(j);
                cell.setCellValue("${obj."+values[i][j]+"}");
                if(j == 0){
                    //获取批注对象
                    //(int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2, int row2)
                    //前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
                    XSSFComment comment = p.createCellComment(new XSSFClientAnchor(0, 0, 0,
                            0, (short) 3, 3, (short) 5, 6));
                    // 输入批注信息
                    comment.setString(new XSSFRichTextString("jx:each(items=\"vo.child\" var=\"obj\" lastCell=\""+lastCell+"\")"));
                    // 添加作者,选中B5单元格,看状态栏
                    cell.setCellComment(comment);
                }
            }
        }
        return wb;
    }
    private static String getLastCell(Integer length){
        return CellReference.convertNumToColString(length)+2;
    }
}