/*
 * Copyright 2019 minhhoangdang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package services;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import endpoint.TinyInstaEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author minhhoangdang
 */
public class UploadServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TinyInstaEndpoint.class.getName());

    private String createJsonResp(String content) {
        return "{\"items\": ["
                + "{\"urlImage\": \"" + content + "\"}"
                + "]}";
    }

    private static String getEndpointURL() {
        return (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
                ? "https://tinyinstagram.appspot.com/_ah/api/tinyinsta/v1/user/register/"
                : "http://localhost:8080/_ah/api/tinyinsta/v1/user/register/";
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        BlobKey avatarBlobKey = blobs.get("avatar").get(0);


        String data = req.getParameter("username")
                + "/" + req.getParameter("name")
                + "/" + avatarBlobKey.getKeyString();

        String rep = register(data);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        out.print(rep);
        out.flush();
    }

    public static String register(String data) throws IOException {
        String fullURL = getEndpointURL() + data;

        logger.log(Level.INFO, "Calling {0}", fullURL);

        URL url = new URL(fullURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining("\n"));
        }
    }

}
