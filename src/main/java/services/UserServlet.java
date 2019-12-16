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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import endpoint.TinyInstaEndpoint;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author minhhoangdang
 */
public class UserServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TinyInstaEndpoint.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String response = null;

        if (req.getParameter("action") != null) {
            switch (req.getParameter("action")) {
                case "get-upload-url":
                    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                    response = blobstoreService.createUploadUrl("/_servlet/user-util",
                            UploadOptions.Builder.withGoogleStorageBucketName("tinyinsta-image-service"));
                    break;

                default:
                    break;
            }
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, response);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        BlobKey avatarBlobKey = blobs.get("avatar").get(0);

        User u = null;
        if (req.getParameter("actionType").equals("create")) {
            u = TinyInstaEndpoint.getInstance().register(
                    req.getParameter("username"),
                    req.getParameter("name"),
                    avatarBlobKey.getKeyString());
        } else if (req.getParameter("actionType").equals("update")) {
            u = TinyInstaEndpoint.getInstance().updateUser(
                    Long.parseLong(req.getParameter("userId")),
                    req.getParameter("username"),
                    req.getParameter("name"),
                    avatarBlobKey.getKeyString());
        }

        if (u == null) {
            ImageServlet.removeBlob(avatarBlobKey.getKeyString());
            resp.sendRedirect(req.getHeader("referer"));
        } else {
            //logger.log(Level.INFO, "Redirect to {0}", req.getHeader("referer"));
            resp.sendRedirect(req.getHeader("referer") + "#!/user/" + u.getUserId());
        }

        /*resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, u);
        out.flush();*/
    }
}
