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

package com.respam.comniq;

import com.respam.comniq.models.MovieListParser;
import com.respam.comniq.models.OMDBParser;
import com.respam.comniq.models.POIexcelExporter;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Controller {
//    List<String> excluded = new ArrayList<>();
    Set excluded = new HashSet();
    private Task parseWorker;
    private Task requestWorker;

    @FXML
    private TextArea textArea;

    @FXML
    private TextArea excludeArea;

    @FXML
    private TextFlow textFlow;

    @FXML
    public ProgressBar progressBar;

    String localMovies;
    Boolean progBarStatus = false;

    @FXML
    private TextField inputPath;

    @FXML
    protected void excludeButtonAction() {
        DirectoryChooser edc = new DirectoryChooser();
        edc.setInitialDirectory(new File(System.getProperty("user.home")));
        edc.setTitle("Exclude Directories");
        File excludeDC = edc.showDialog(null);
        if (null != excludeDC) {
            excludeArea.appendText(excludeDC.getName());
            excludeArea.appendText(";");
        }
    }

    @FXML
    protected void exportButtonAction() {
        progressBar.setVisible(true);
        outputText();
        progressBar.relocate(0, 100);
        textFlow.getChildren().clear();
        parseWorker = createExportWorker();
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(parseWorker.progressProperty());
        new Thread(parseWorker).start();
    }

    @FXML
    protected void handleLocalButtonAction() {
        progressBar.setVisible(true);
        String exdMovies = excludeArea.getText();
        excluded = new HashSet(Arrays.asList(exdMovies.split(";")));
        localMovies = inputPath.getText();
        File loc = new File(localMovies);

        if (loc.isDirectory() == false) {
            Text errorMsg = new Text(" Not a valid Movies Directory");
            errorMsg.setFill(Color.WHITE);
            textFlow.getChildren().clear();
            textFlow.getChildren().add(errorMsg);
        }

        if (progBarStatus == false) {
            progressBar.relocate(0, 100);
            progBarStatus = true;
        }

        if (progBarStatus == true) {
            progressBar.relocate(0, 100);
        }

        if (inputPath.getText().trim().isEmpty()) {
            Text errorMsg = new Text(" Enter a valid Movies Directory");
            errorMsg.setFill(Color.WHITE);
            textFlow.getChildren().clear();
            textFlow.getChildren().add(errorMsg);
        } else if (loc.isDirectory() == true && !(inputPath.getText().trim().isEmpty())) {
            textFlow.getChildren().clear();
            parseWorker = createWorker();
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().bind(parseWorker.progressProperty());
            new Thread(parseWorker).start();
        }
    }

    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                localMovies = inputPath.getText();

                File loc = new File(localMovies);
                JSONArray jsonArr = new JSONArray();
                String[] folders = loc.list();

                for (int i = 0; i < folders.length; i++) {
                    JSONObject jsonObj = new JSONObject();

                    if(!(excluded.contains(folders[i]))) {
                        if (folders[i].charAt(0) == '(' || folders[i].charAt(0) == '{') {
                            String[] movieYear = folders[i].split("[({})]");
                            jsonObj.put("movie", movieYear[2].trim());
                            jsonObj.put("year", movieYear[1]);
                        } else if ((folders[i].charAt(folders[i].length() - 1) == ')') || (folders[i].charAt(folders[i].length() - 1) == '}')) {
                            String[] movieName = folders[i].split("[({]");
                            String[] movieYear = movieName[1].split("[)}]");
                            jsonObj.put("movie", movieName[0].trim());
                            jsonObj.put("year", movieYear[0]);
                        } else {
                            jsonObj.put("movie", folders[i].trim());
                            jsonObj.put("year", null);
                        }

                        jsonArr.add(jsonObj);
                    }
                    updateProgress(i, folders.length - 1);
                }

                MovieListParser mp = new MovieListParser();
                mp.JSONWriter(jsonArr);
                return true;
            }
        };
    }

    @FXML
    public void handleOMDBButtonAction() {
        progressBar.setVisible(true);
        progressBar.relocate(0, 100);
        textFlow.getChildren().clear();
        requestWorker = OMDBworker();
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(requestWorker.progressProperty());
        new Thread(requestWorker).start();
        textArea.setEditable(false);
        textArea.clear();
        textArea.appendText("Click on Export Button once OMDB Parse is complete...");
    }

    public Task OMDBworker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                OMDBParser op = new OMDBParser();
                JSONParser parser = new JSONParser();
                String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
                try {
                    Object obj = parser.parse(new FileReader(path + File.separator + "LocalList.json"));
                    JSONArray parsedArr = (JSONArray) obj;

                    // Loop JSON Array
                    for (int i = 0; i < parsedArr.size(); i++) {
                        JSONObject parsedObj = (JSONObject) parsedArr.get(i);
                        String movieName = (String) parsedObj.get("movie");
                        String movieYear = (String) parsedObj.get("year");
                        System.out.println(movieName + "-" + movieYear);
                        op.requestOMDB(movieName, movieYear);
                        updateProgress(i, parsedArr.size() - 1);
                    }
                    op.movieInfoWriter();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return true;
            }
        };
    }

    private void outputText() {
        JSONParser parser = new JSONParser();
        textArea.setEditable(false);
        String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
        try {
            Object obj = parser.parse(new FileReader(path + File.separator + "MovieInfo.json"));
            JSONArray parsedArr = (JSONArray) obj;

            if (parsedArr.size() == 0) {
                textArea.setEditable(false);
                textArea.clear();
                textArea.appendText("No Movies found from the provided Directory !!!");
            } else {
                textArea.clear();
                // Loop JSON Array

                for (int i = 0; i < parsedArr.size(); i++) {
                    JSONObject parsedObj = (JSONObject) parsedArr.get(i);
                    textArea.setWrapText(true);
                    textArea.appendText("Title: " + (String) parsedObj.get("Title") + "\n");
                    textArea.appendText("Release Date: " + (String) parsedObj.get("Released") + "\n");
                    textArea.appendText("Genre: " + (String) parsedObj.get("Genre") + "\n");

                    textArea.appendText("IMDB Link: " + "http://www.imdb.com/title/" + (String) parsedObj.get("imdbID") + "\n");
                    textArea.appendText("IMDB Rating: " + (String) parsedObj.get("imdbRating") + "\n");
                    textArea.appendText("Actors: " + (String) parsedObj.get("Actors") + "\n");
                    textArea.appendText("Plot: " + (String) parsedObj.get("Plot") + "\n");

                    textArea.appendText("\n\n");
                }
                textArea.positionCaret(0);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void browseButtonAction() {
        DirectoryChooser dc = new DirectoryChooser();
        File selectedDC = dc.showDialog(null);
        if (selectedDC != null) {
            inputPath.setText(selectedDC.getPath());
        }
    }

    public Task createExportWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                JSONParser parser = new JSONParser();
                String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";

                try {
                    POIexcelExporter export = new POIexcelExporter();
                    Object obj = parser.parse(new FileReader(path + File.separator + "MovieInfo.json"));
                    JSONArray parsedArr = (JSONArray) obj;

                    // Loop JSON Array
                    for (int i = 0; i < parsedArr.size(); i++) {
                        JSONObject parsedObj = (JSONObject) parsedArr.get(i);
                        if (null != parsedObj.get("Title")) {
                            export.excelWriter(parsedObj, i);
                            System.out.println("Done with " + "\"" + parsedObj.get("Title") + "\"");
                        }
                        updateProgress(i, parsedArr.size() - 1);
                    }
//                    export.addImages(parsedArr);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
    }
}
