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

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by S P Mahapatra on 3/16/2017.
 */
public class OMDBParser {
    private JSONArray movieInfo = new JSONArray();

    public void requestOMDB(String movie, String year) {
        String imageURL;

        // Location for downloading and storing thumbnails
        String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator +
                "output" + File.separator + "thumbnails";
        File userOutDir = new File(path);

        // Create Thumbnails directory if not present
        if (userOutDir.mkdirs()) {
            System.out.println(userOutDir + " was created");
        }

        // Connect to OMDB API and fetch details
        try {
            // Proxy details
//            HttpHost proxy = new HttpHost("web-proxy.in.hpecorp.net", 8080, "http");

            HttpClient client = HttpClientBuilder.create().build();

            // Start of proxy client
//            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//            CloseableHttpClient client = HttpClients.custom()
//                    .setRoutePlanner(routePlanner)
//                    .build();
            // End of proxy client

            String uri = "http://www.omdbapi.com/?t=" + movie + "&y=" + year;
            uri = uri.replace(" ","%20");
            HttpGet request = new HttpGet(uri);
            HttpResponse response = client.execute(request);
            String json = EntityUtils.toString(response.getEntity());
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(json);

            // Excluding Non Movies
            if(null != jsonObject.get("Title")) {
                movieInfo.add(jsonObject);

                // Download the thumbnails if present
                if(null != jsonObject.get("Poster")) {
                    imageURL = ((String) jsonObject.get("Poster"));
                    System.out.println(imageURL);
                    URL thumbnail = new URL(imageURL);
                    ReadableByteChannel rbc = Channels.newChannel(thumbnail.openStream());
                    FileOutputStream fos = new FileOutputStream(path + File.separator + jsonObject.get("Title") + ".jpg");
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }

            }

        } catch (ParseException | IOException | UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    public void movieInfoWriter() {
        String path = System.getProperty("user.home") + File.separator + "comniq" + File.separator + "output";
        File userOutDir = new File(path);
        if (userOutDir.exists()) {
            System.out.println(userOutDir + " already exists");
        } else if (userOutDir.mkdirs()) {
            System.out.println(userOutDir + " was created");
        } else {
            System.out.println(userOutDir + " was not created");
        }
        try {
            FileWriter omdbList = new FileWriter(userOutDir + File.separator + "MovieInfo.json");
            omdbList.write(movieInfo.toJSONString());
            omdbList.flush();
            omdbList.close();
            System.out.println("Movie Info Written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
