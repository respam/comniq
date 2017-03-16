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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by S P Mahapatra on 3/16/2017.
 */
public class MovieListParser {
    public void parseFolder(String location) {
//        System.out.println(location);
        File loc = new File(location);
        JSONArray jsonArr = new JSONArray();

        if(loc.isDirectory()) {
            String[] folders = loc.list();
            for(String folderName: folders) {
                JSONObject jsonObj = new JSONObject();
                String[] movieName = folderName.split("\\(");
                String[] movieYear = movieName[1].split("\\)");
                System.out.println(movieName[0].trim() + "  " + movieYear[0]);
                jsonObj.put("movie", movieName[0].trim());
                jsonObj.put("year", movieYear[0]);
                jsonArr.add(jsonObj);
            }

            try {
                System.out.println(System.getProperty("user.dir"));
                FileWriter localList = new FileWriter(System.getProperty("user.dir") + "/src/output/LocalList.json");
                localList.write(jsonArr.toJSONString());
                localList.flush();
                localList.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
