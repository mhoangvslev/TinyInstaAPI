
import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import entity.Post;
import entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
/**
 *
 * @author minhhoangdang
 */
public class Benchmark {

    private static User userFromJSON(String json) {
        return new Gson().fromJson(json, User.class);
    }

    private static Post postFromJson(String json){
        return new Gson().fromJson(json,Post.class);
    }

    private static String getEndpoint() {
        return (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
                ? "https://tinyinstagram.appspot.com/"
                : "http://localhost:8080/";
    }

    /**
     * Test 1: How much time it takes to post of message if followed by 10, 100
     * and 500 followers? (average on 30 measures)
     *
     * @param nbFollowers
     * @return time score in ms
     * @throws java.io.IOException
     */
    public static long testPostMessageAndNotify(int nbFollowers) throws IOException {

        // createUser
        User target = userFromJSON(Benchmark.createUser("username0", "name0", "avatar0"));

        // Create followers
        HashSet<Long> followerIds = new HashSet<>();
        for (int i = 1; i <= nbFollowers; i++) {
            User follower = userFromJSON(Benchmark.createUser("username" + i, "name" + i, "avatar" + i));
            Benchmark.follow(follower.getUserId(), target.getUserId());
            followerIds.add(follower.getUserId());
        }

        // Target makes a post
        long startTime = new Date().getTime();
        Benchmark.createPost(target.getUserId(), "image0", "caption0");

        // Notify all follower
        followerIds.forEach((followerId) -> {
            try {
                Benchmark.getNewsFeed(followerId, 50);
            } catch (IOException ex) {
                Logger.getLogger(Benchmark.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        long endTime = new Date().getTime();
        Benchmark.deleteAll();
        return endTime - startTime;
    }

    /**
     * Test 2: How much time it takes to retrieve the last 10, 100 and 500 last
     * messages ? (average of 30 measures)
     * @param nbPost
     * @return 
     * @throws java.io.IOException
     */
    public static long testRecupPost(int nbPost) throws IOException {

        // createUser
        User owner = userFromJSON(Benchmark.createUser("username0", "name0", "avatar0"));

        // Create posts
        for (int i = 1; i <= nbPost; i++) {
            Post p = postFromJson(Benchmark.createPost(owner.getUserId(),"test","test"));
        }

        long startTime = new Date().getTime();
        Benchmark.getPosts(nbPost);

        long endTime = new Date().getTime();
        Benchmark.deleteAll();
        return endTime - startTime;
    }

    /**
     * Test 3: How much “likes” can you do per second ?? (average on 30
     * measures)
     */

    /*
     * =============================== 
     * - API Methods
     * ===============================
     */
    /**
     *
     * @param userName
     * @param name
     * @param avatarURL
     * @return
     * @throws IOException
     */
    public static String createUser(String userName, String name, String avatarURL) throws IOException {
        String params = "?";
        params += "username=" + URLEncoder.encode(userName, "UTF-8");
        params += "&name=" + URLEncoder.encode(name, "UTF-8");
        params += "&avatarURL=" + URLEncoder.encode(avatarURL, "UTF-8");

        URL url = new URL(getEndpoint() + "_ah/api/tinyinsta/v1/user/register" + params);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static String follow(Long userId, Long targetId) throws IOException {
        URL url = new URL(
                MessageFormat.format(getEndpoint() + "_ah/api/tinyinsta/v1/user/{0}/follow/{1}", userId, targetId)
        );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static String unfollow(Long userId, Long targetId) throws IOException {
        URL url = new URL(
                MessageFormat.format(getEndpoint() + "_ah/api/tinyinsta/v1/user/{0}/unfollow/{1}", userId, targetId)
        );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static String like(Long userId, Long postId) throws IOException {
        URL url = new URL(
                MessageFormat.format(getEndpoint() + "_ah/api/tinyinsta/v1/user/{0}/like/{1}", userId, postId)
        );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static String unlike(Long userId, Long postId) throws IOException {
        URL url = new URL(
                MessageFormat.format(getEndpoint() + "_ah/api/tinyinsta/v1/user/{0}/like/{1}", userId, postId)
        );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static String deleteAll() throws IOException {
        URL url = new URL(getEndpoint() + "_ah/api/tinyinsta/v1/user/delete/all");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static String createPost(Long ownerId, String imageURL, String caption) throws IOException {

        String params = "?";
        params += "ownerId=" + URLEncoder.encode(ownerId.toString(), "UTF-8");
        params += "&imageURL=" + URLEncoder.encode(imageURL, "UTF-8");
        params += "&caption=" + URLEncoder.encode(caption, "UTF-8");

        URL url = new URL(getEndpoint() + "_ah/api/tinyinsta/v1/post/create" + params);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static String getNewsFeed(Long userId, int limit) throws IOException {
        URL url = new URL(
                MessageFormat.format(getEndpoint() + "_ah/api/tinyinsta/v1/user/{0}/posts?limit={1}", userId, limit)
        );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static String getPosts(int limit) throws IOException{
        URL url = new URL(
                MessageFormat.format(getEndpoint() + "_ah/api/tinyinsta/v1/post/all/?limit={0}", limit)
        );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return r.lines().collect(Collectors.joining(" "));
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Test 1 (10) score: " + Benchmark.testPostMessageAndNotify(100) + " ms");
        //System.out.println("Test 1 (100) score: " + Benchmark.testPostMessageAndNotify(100) + " ms");
        //System.out.println("Test 1 (500) score: " + Benchmark.testPostMessageAndNotify(500) + " ms");

        System.out.println("Test 2 (10) : " + Benchmark.testRecupPost(10) + "ms");
        System.out.println("Test 2 (100) : " + Benchmark.testRecupPost(100) + "ms");
        System.out.println("Test 2 (500) : " + Benchmark.testRecupPost(500) + "ms");
    }
}
