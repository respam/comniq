/*
 * Copyright (c) 2017. Satya Prakash Mahapatra
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.respam.comniq.models;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by S P Mahapatra on 3/24/2017.
 */
public class POIexcelExporter {
    int lastRow = 0;
    Boolean checked = false;

    public void createFile() throws IOException {
        String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
        File file = new File(path + File.separator + "POImovieInfo.xlsx");

        // Blank Workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Movies");

        // Data for Labels
        Map<String, Object[]> label = new TreeMap<>();
        label.put("1", new Object[] {"Poster", "Title", "Release Date", "Metascore", "IMDB Rating",
        "Plot", "IMDB URL", "Genre", "Director", "Actors", "Rating", "Runtime"});

        System.out.println(label);
        // Iterate over label and write to sheet
        Set<String> keyset = label.keySet();

        Font font = workbook.createFont();
        font.setFontHeight((short) 240);
        font.setFontName("Courier New");
        font.setBold(true);

        for(String key : keyset) {
            Row row = sheet.createRow(0);
            Object[] objArr = label.get(key);
            int cellnum = 0;
            for(Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if(cellnum == 2 ) {
                    XSSFCellStyle titleStyle = workbook.createCellStyle();
                    titleStyle.setFont(font);
                    cell.setCellStyle(titleStyle);
                }
                System.out.println((String) obj);
                cell.setCellValue((String) obj);
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            System.out.println("Excel File Created");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void excelWriter(JSONObject parsedObj, int rownum) throws IOException {
        String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
        File file = new File(path + File.separator + "POImovieInfo.xlsx");



        if(!file.exists()) {
            System.out.println("Entered Condition");
            createFile();
        }

        if(file.exists() && checked.equals(false)) {
            findLastRow();
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            XSSFSheet sheet = workbook.getSheet("Movies");

            Map<String, Object[]> label = new TreeMap<>();
            label.put("1", new Object[] {"", parsedObj.get("Title"), parsedObj.get("Released"), parsedObj.get("Metascore"), parsedObj.get("imdbRating"),
                    parsedObj.get("Plot"), parsedObj.get("imdbID"), parsedObj.get("Genre"), parsedObj.get("Director"), parsedObj.get("Actors"),
                     parsedObj.get("Rated"), parsedObj.get("Runtime")});

            Set<String> keyset = label.keySet();
            rownum = rownum + lastRow;
            for(String key : keyset) {

                Row row = sheet.createRow(rownum++);
                Object[] objArr = label.get(key);
                int cellnum = 0;
                for(Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    System.out.println((String) obj);
                    cell.setCellValue((String) obj);
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            System.out.println("Movie Added");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void findLastRow() throws IOException {
        String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
        File file = new File(path + File.separator + "POImovieInfo.xlsx");

        try {
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet("Movies");
            XSSFRow sheetRow = sheet.getRow(lastRow);

            while (sheetRow != null) {
                lastRow = lastRow + 1;
                sheetRow = sheet.getRow(lastRow);
            }
            checked = true;
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
