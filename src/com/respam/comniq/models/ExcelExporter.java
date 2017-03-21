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

import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by S P Mahapatra on 3/21/2017.
 */
public class ExcelExporter {

    private WritableCellFormat timesBold;
    private WritableCellFormat times;

    public void createFile() throws IOException, WriteException{
        String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
        File file = new File(path + File.separator + "movieInfo.xls");
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Movies", 0);
        WritableSheet excelSheet = workbook.getSheet(0);
        createLabel(excelSheet);

        workbook.write();
        workbook.close();
    }

    private void createLabel(WritableSheet excelSheet) throws WriteException{
        // create a times font for rows
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(true);

        // create a bold font for headings
        WritableFont times12ptBold = new WritableFont(
                WritableFont.TIMES, 12, WritableFont.BOLD, false,
                UnderlineStyle.NO_UNDERLINE);
        timesBold = new WritableCellFormat(times12ptBold);
        // Lets automatically wrap the cells
        timesBold.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBold);
        cv.setAutosize(true);

        // Write a few headers
        addCaption(excelSheet, 0, 0, "Poster");
        addCaption(excelSheet, 1, 0, "Title");
        addCaption(excelSheet, 2, 0, "Release Date");
        addCaption(excelSheet, 3, 0, "Metascore");
        addCaption(excelSheet, 4, 0, "IMDB Rating");
        addCaption(excelSheet, 5, 0, "Plot");
        addCaption(excelSheet, 6, 0, "IMDB URL");
        addCaption(excelSheet, 7, 0, "Genre");
        addCaption(excelSheet, 8, 0, "Director");
        addCaption(excelSheet, 9, 0, "Actors");
        addCaption(excelSheet, 10, 0, "Rating");
        addCaption(excelSheet, 11, 0, "Runtime");


    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBold);
        sheet.addCell(label);
    }

    public void excelWriter(JSONObject jsonObj) throws IOException, WriteException, BiffException{
        String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
        File file = new File(path + File.separator + "movieInfo.xls");
        if(!file.exists()) {
            createFile();
        }

        Workbook outFile = Workbook.getWorkbook(file);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook = Workbook.createWorkbook(file, outFile, wbSettings);

        // Create cell font and format
        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 10);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);

        WritableSheet excelSheet = workbook.getSheet("Movies");
        cellFormat.setWrap(true);

        Cell[] cell = excelSheet.getColumn(1);
        int len = cell.length;
        Label title, release, metascore, imdbRate, plot, imdbURL, genre, director, actors, rating, runtime;
        title = new Label(1, len,(String) jsonObj.get("Title"),cellFormat);
        release = new Label(2, len,(String) jsonObj.get("Released"),cellFormat);
        metascore = new Label(3, len,(String) jsonObj.get("Metascore"),cellFormat);
        imdbRate = new Label(4, len,(String) jsonObj.get("imdbRating"),cellFormat);
        plot = new Label(5, len,(String) jsonObj.get("Plot"),cellFormat);
        imdbURL = new Label(6, len,"http://www.imdb.com/title/" + (String) jsonObj.get("imdbID"),cellFormat);
        genre = new Label(7, len,(String) jsonObj.get("Genre"),cellFormat);
        director = new Label(8, len,(String) jsonObj.get("Director"),cellFormat);
        actors = new Label(9, len,(String) jsonObj.get("Actors"),cellFormat);
        rating = new Label(10, len,(String) jsonObj.get("Rated"),cellFormat);
        runtime = new Label(11, len,(String) jsonObj.get("Runtime"),cellFormat);

        excelSheet.addCell(title);
        excelSheet.addCell(release);
        excelSheet.addCell(metascore);
        excelSheet.addCell(imdbRate);
        excelSheet.addCell(plot);
        excelSheet.addCell(imdbURL);
        excelSheet.addCell(genre);
        excelSheet.addCell(director);
        excelSheet.addCell(actors);
        excelSheet.addCell(rating);
        excelSheet.addCell(runtime);

        workbook.write();
        workbook.close();

    }
}
