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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by S P Mahapatra on 3/16/2017.
 */
public class MovieListParser {

    public void JSONWriter(JSONArray JSONarr) {
        try {
            String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
            File userOutDir = new File(path);
            if (userOutDir.exists()) {
                System.out.println(userOutDir + " already exists");
            } else if (userOutDir.mkdirs()) {
                System.out.println(userOutDir + " was created");
            } else {
                System.out.println(userOutDir + " was not created");
            }

            FileWriter localList = new FileWriter(userOutDir + File.separator + "LocalList.json");
            localList.write(JSONarr.toJSONString());
            localList.flush();
            localList.close();
            System.out.println("Local Processing Complete");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
