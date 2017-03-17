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
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Controller {
    private Task parseWorker;
    private Task requestWorker;

    @FXML
    private TextArea textArea;

    @FXML
    private ProgressBar progressBar;

    String localMovies;
    @FXML private TextField inputPath;

    @FXML
    protected void handleLocalButtonAction() {
        progressBar.setProgress(0);
        parseWorker = createWorker();
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(parseWorker.progressProperty());
        new Thread(parseWorker).start();
    }

    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                localMovies = inputPath.getText();

                File loc = new File(localMovies);
                JSONArray jsonArr = new JSONArray();

                if(loc.isDirectory()) {
                    String[] folders = loc.list();
                    for(int i=0; i<folders.length; i++) {
                        JSONObject jsonObj = new JSONObject();
                        String[] movieName = folders[i].split("\\(");
                        String[] movieYear = movieName[1].split("\\)");
                        jsonObj.put("movie", movieName[0].trim());
                        jsonObj.put("year", movieYear[0]);
                        jsonArr.add(jsonObj);
                        updateProgress(i, folders.length-1);
                    }
                }
                MovieListParser mp = new MovieListParser();
                mp.JSONWriter(jsonArr);
                return true;
            }
        };
    }

    @FXML
    public void handleOMDBButtonAction() {
        progressBar.relocate(0,100);
        requestWorker = OMDBworker();
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(requestWorker.progressProperty());
        new Thread(requestWorker).start();
    }

    public Task OMDBworker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                OMDBParser op = new OMDBParser();
                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(new FileReader(System.getProperty("user.dir") + "/src/output/LocalList.json"));
                    JSONArray parsedArr = (JSONArray) obj;

                    // Loop JSON Array
                    for(int i=0; i<parsedArr.size(); i++) {
                        JSONObject parsedObj = (JSONObject) parsedArr.get(i);
                        String movieName = (String) parsedObj.get("movie");
                        String movieYear = (String) parsedObj.get("year");
                        System.out.println(movieName + "-" + movieYear);
                        op.requestOMDB(movieName, movieYear);
                        updateProgress(i, parsedArr.size()-1);
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

    @FXML
    public void handleDisplayAction() {
        outputText();
    }

    public void outputText() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(System.getProperty("user.dir") + "/src/output/MovieInfo.json"));
            JSONArray parsedArr = (JSONArray) obj;

            // Loop JSON Array
            for(int i=0; i<parsedArr.size(); i++) {
                JSONObject parsedObj = (JSONObject) parsedArr.get(i);
                textArea.setWrapText(true);
                textArea.appendText("Title: " + (String) parsedObj.get("Title") + "\n");
                textArea.appendText("Release Date: " + (String) parsedObj.get("Released") + "\n");
                textArea.appendText("Genre: " + (String) parsedObj.get("Genre") + "\n");
                textArea.appendText("IMDB Rating: " + (String) parsedObj.get("imdbRating") + "\n");
                textArea.appendText("Actors: " + (String) parsedObj.get("Actors") + "\n");
                textArea.appendText("Plot: " + (String) parsedObj.get("Plot") + "\n");

                textArea.appendText("\n\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
