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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by S P Mahapatra on 3/24/2017.
 */
public class POIexcelExporter {
    int lastRow = 0;
    Boolean checked = false;
    int pictureureIdx;
    Drawing drawing;
    ClientAnchor anchor;


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

        // Iterate over label and write to sheet
        Set<String> keyset = label.keySet();

        // Setting Style for the Label Row
        Font font = workbook.createFont();
        font.setFontHeight((short) 240);
        font.setFontName("Courier New");
        font.setBold(true);
        XSSFCellStyle labelStyle = workbook.createCellStyle();
        labelStyle.setWrapText(true);
        labelStyle.setFont(font);

        // Setting column widths
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 8500);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 3500);
        sheet.setColumnWidth(5, 9500);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 4000);
        sheet.setColumnWidth(8, 3500);
        sheet.setColumnWidth(9, 4000);
        sheet.setColumnWidth(10, 3000);
        sheet.setColumnWidth(11, 4000);

        // Freezing the first row
        sheet.createFreezePane(0, 1);

        // Filling each cell with Label data
        for(String key : keyset) {
            Row row = sheet.createRow(0);
            Object[] objArr = label.get(key);
            int cellnum = 0;
            for(Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                cell.setCellStyle(labelStyle);
                cell.setCellValue((String) obj);
            }
        }

        // Writing the excel file
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

        String thumbnailPath = System.getProperty("user.home") + File.separator + "comniq" +
                File.separator + "output" + File.separator + "thumbnails";
        File posterFile = new File(thumbnailPath + File.separator + parsedObj.get("Title") + ".jpg");

        if(!file.exists()) {
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

            // Setting Style for the Label Row

            XSSFCellStyle contentStyle = workbook.createCellStyle();
            contentStyle.setWrapText(true);
            contentStyle.setVerticalAlignment(VerticalAlignment.TOP);

            rownum = rownum + lastRow;

            if(posterFile.exists()) {
                InputStream imageStream = new FileInputStream(thumbnailPath + File.separator +
                        parsedObj.get("Title") + ".jpg");
                byte[] imageBytes = IOUtils.toByteArray(imageStream);
                pictureureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
                imageStream.close();

                CreationHelper helper = workbook.getCreationHelper();
                drawing = sheet.createDrawingPatriarch();
                anchor = helper.createClientAnchor();

            }

            for(String key : keyset) {

                Row row = sheet.createRow(rownum++);
                row.setHeight((short) 2000);
                Object[] objArr = label.get(key);
                int cellnum = 0;
                for(Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    cell.setCellStyle(contentStyle);
                    cell.setCellValue((String) obj);
                }
                if(posterFile.exists()) {
                    anchor.setCol1(0);
                    anchor.setRow1(rownum - 1);
                    anchor.setCol2(0);
                    anchor.setRow2(rownum - 1);
                    Picture pict = drawing.createPicture(anchor, pictureureIdx);
                    pict.resize(1, 1);
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
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
