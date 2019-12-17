package endpoint;

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
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.DefaultValue;
import com.google.api.server.spi.config.Named;
import entity.Counter;
import entity.Post;
import entity.User;
import java.util.Arrays;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

import repository.PostRepository;
import services.ShardedCounter;
import repository.UserRepository;

class TestResult {

    private final String testName;
    private final String description;
    private final Long record;

    public TestResult(String testName, String description, Long record) {
        this.testName = testName;
        this.description = description;
        this.record = record;
    }

    public Map<String, Object> getResults() {
        Map<String, Object> result = new HashMap<>();
        result.put("testName", this.testName);
        result.put("description", description);
        result.put("record", this.record);
        return result;
    }

}

@Api(name = "tinyinsta", version = "v1", namespace = @ApiNamespace(ownerDomain = "tinyinsta.example.com", ownerName = "tinyinsta.example.com", packagePath = "services"))
public class TinyInstaEndpoint {

    private static TinyInstaEndpoint instance;
    private static final Logger logger = Logger.getLogger(TinyInstaEndpoint.class.getName());
    private static final int QUERY_MAX_LIMIT = Integer.MAX_VALUE;

    public static TinyInstaEndpoint getInstance() {
        if (instance == null) {
            return new TinyInstaEndpoint();
        }
        return instance;
    }

    /*
    ===================================================================================
    == USER
    ===================================================================================
     */
    //
    /*
    ----------------------
    -- POST
    ----------------------
     */
    /**
     * Enregistrer un nouveau utilisateur
     *
     * @param username
     * @param name
     * @param avatarURL
     * @return le nouvelle
     */
    @ApiMethod(name = "register", httpMethod = HttpMethod.POST, path = "user/register")
    public User register(
            @Named("username") String username,
            @Named("name") String name,
            @Named("avatarURL") String avatarURL
    ) {
        User target = new User(username, name, avatarURL);

        Collection<User> decoy = UserRepository.getInstance().getUserByUserName(target.getUsername(), QUERY_MAX_LIMIT);
        if (!decoy.contains(target)) {
            //logger.log(Level.INFO, "[SUCCESS] Registering username {0} of name {1}", new Object[]{target.getUsername(), target.getName()});
            return UserRepository.getInstance().createUser(target);

        }

        //logger.log(Level.INFO, "[FAILED] Already exists username {0} of name {1}", new Object[]{target.getUsername(), target.getName()});
        return null;
    }

    /*
    ----------------------
    -- GET
    ----------------------
     */
    @ApiMethod(name = "findUser", httpMethod = HttpMethod.GET, path = "user/find")
    public Collection<User> findUser(
            @Nullable @Named("userId") Long userId,
            @Nullable @Named("username") String username,
            @Nullable @Named("name") String name,
            @Nullable @Named("limit") @DefaultValue("50") int limit) {

        // When userId is filled, no need to do the rest
        if (userId != null) {
            User res = UserRepository.getInstance().getUserById(userId);
            return res != null ? Arrays.asList(new User[]{res}) : null;
        }

        // When neither field are filled
        if (username == null && name == null) {
            return UserRepository.getInstance().getAllUser(limit);
        } // When only name is filled
        else if (username == null && name != null) {
            return UserRepository.getInstance().getUserByName(name, limit);
        } // When only username is filled
        else if (name == null && username != null) {
            return UserRepository.getInstance().getUserByUserName(username, limit);
        } // When both username and name are filled
        else {
            return UserRepository.getInstance()
                    .query().filter("username", username)
                    .filter("name", name)
                    .list();
        }
    }

    @ApiMethod(name = "getFollowers", httpMethod = HttpMethod.GET, path = "user/{userId}/followers")
    public Collection<User> getFollowers(@Named("userId") Long userId) {
        User user = UserRepository.getInstance().getUserById(userId);
        if (user != null) {
            Collection<Long> followersIds = user.getFollowers();
            return UserRepository.getInstance().getUsersByIds(followersIds);
        }
        return null;
    }

    @ApiMethod(name = "getFollowers", httpMethod = HttpMethod.GET, path = "user/{userId}/following")
    public Collection<User> getFollowing(@Named("userId") Long userId) {
        User user = UserRepository.getInstance().getUserById(userId);
        if (user != null) {
            Collection<Long> followingIds = user.getFollowing();
            return UserRepository.getInstance().getUsersByIds(followingIds);
        }
        return null;
    }

    /*
    ----------------------
    -- PUT
    ----------------------
     */
    @ApiMethod(name = "updateUser", httpMethod = HttpMethod.PUT, path = "user/{userId}/update")
    public User updateUser(
            @Named("userId") Long targetId,
            @Nullable
            @Named("username") String username,
            @Nullable
            @Named("name") String name,
            @Nullable
            @Named("avatarURL") String avatarURL
    ) {
        User target = UserRepository.getInstance().getUserById(targetId);

        if (target != null) {
            if (name != null) {
                target.setName(name);
            }

            if (username != null) {
                target.setUsername(username);
            }

            if (avatarURL != null) {
                target.setAvatarURL(avatarURL);
            }

            //logger.log(Level.INFO, "[SUCCESS] Update user {0}", targetId);
            return UserRepository.getInstance().updateUser(target);
        }

        //logger.log(Level.INFO, "[FAILED] Update user {0}", targetId);
        return null;
    }

    @ApiMethod(name = "follow", httpMethod = HttpMethod.PUT, path = "user/{userId}/follow/{targetId}")
    public User follow(@Named("userId") Long userId, @Named("targetId") Long targetId) {

        User user = UserRepository.getInstance().getUserById(userId);
        User target = UserRepository.getInstance().getUserById(targetId);

        if (user != null && target != null) {
            target.addFollower(user.getUserId());
            user.addFollowing(target.getUserId());

            UserRepository.getInstance().updateUser(target);
            UserRepository.getInstance().updateUser(user);

            //logger.log(Level.INFO, "[SUCCESS] User {0} follows User {1}", new Object[]{userId, targetId});
            return user;
        }

        //logger.log(Level.INFO, "[FAILED] User {0} follows User {1}", new Object[]{userId, targetId});
        return null;
    }

    @ApiMethod(name = "unfollow", httpMethod = HttpMethod.PUT, path = "user/{userId}/unfollow/{targetId}")
    public User unfollow(@Named("userId") Long userId, @Named("targetId") Long targetId) {

        User user = UserRepository.getInstance().getUserById(userId);
        User target = UserRepository.getInstance().getUserById(targetId);

        if (user != null && target != null) {
            target.removeFollower(user.getUserId());
            user.removeFollowing(target.getUserId());

            UserRepository.getInstance().updateUser(target);
            UserRepository.getInstance().updateUser(user);

            //logger.log(Level.INFO, "[SUCCESS] User {0} unfollows User {1}", new Object[]{userId, targetId});
            return user;
        }

        //logger.log(Level.INFO, "[FAILED] User {0} unfollows User {1}", new Object[]{userId, targetId});
        return null;
    }

    @ApiMethod(name = "like", httpMethod = HttpMethod.PUT, path = "user/{userId}/like/{postId}")
    public Post like(
            @Named("userId") Long userId,
            @Named("postId") Long postId
    ) {
        User user = UserRepository.getInstance().getUserById(userId);
        Post post = PostRepository.getInstance().getPostById(postId);

        if (user != null && post != null) {
            post.addLike(user.getUserId());
            PostRepository.getInstance().addToLikeCounter(post);
            PostRepository.getInstance().updatePost(post);

            //logger.log(Level.INFO, "[SUCCESS] User {0} likes Post {1}", new Object[]{user.getUserId(), post.getPostId()});
            return post;
        }

        //logger.log(Level.INFO, "[FAILED] User {0} likes Post {1}", new Object[]{userId, postId});
        return null;
    }

    @ApiMethod(name = "like", httpMethod = HttpMethod.PUT, path = "user/{userId}/unlike/{postId}")
    public Post unlike(
            @Named("userId") Long userId,
            @Named("postId") Long postId
    ) {
        User user = UserRepository.getInstance().getUserById(userId);
        Post post = PostRepository.getInstance().getPostById(postId);

        if (user != null && post != null) {
            post.removeLike(user.getUserId());
            PostRepository.getInstance().removeFromLikeCounter(post);
            PostRepository.getInstance().updatePost(post);

            //logger.log(Level.INFO, "[SUCCESS] User {0} unlikes Post {1}", new Object[]{user.getUserId(), post.getPostId()});
            return post;
        }

        //logger.log(Level.INFO, "[FAILED] User {0} unlikes Post {1}", new Object[]{userId, postId});
        return null;
    }

    /*
    ----------------------
    -- DELETE
    ----------------------
     */
    @ApiMethod(name = "deleteUser", httpMethod = HttpMethod.DELETE, path = "user/delete/{userId}")
    public void deleteUser(@Named("userId") Long userId) {
        //logger.log(Level.INFO, "Deleting user of id {0}", userId);
        UserRepository.getInstance().deleteUser(userId);

        Collection<Post> posts = PostRepository.getInstance().getPostsByUser(userId, QUERY_MAX_LIMIT);
        if (posts != null) {
            PostRepository.getInstance().deletePosts(posts);
            //logger.log(Level.INFO, "[SUCCESS] Deleted posts that belong to user of id {0}", userId);
        }
    }

    @ApiMethod(name = "deleteAllUsers", httpMethod = HttpMethod.DELETE, path = "user/delete/all")
    public void deleteAllUsers() {
        //logger.log(Level.INFO, "Deleting all user");
        UserRepository.getInstance().deleteAll();
        PostRepository.getInstance().deleteAll();
    }

    /*
    ===================================================================================
    == POSTS
    ===================================================================================
     */
    //
    /*
    ----------------------
    -- POST
    ----------------------
     */
    @ApiMethod(name = "createPost", httpMethod = HttpMethod.POST, path = "post/create")
    public Post createPost(
            @Named("ownerId") Long ownerId,
            @Named("imageURL") String imageURL,
            @Named("caption") String caption
    ) {

        User owner = UserRepository.getInstance().getUserById(ownerId);
        if (owner != null) {
            Post post = new Post(imageURL, caption, owner.getUserId(), new Date());

            Collection<Post> decoy = PostRepository.getInstance().getPostsByUser(ownerId, QUERY_MAX_LIMIT);
            if (!decoy.contains(post)) {
                //logger.log(Level.INFO, "[SUCCESS] Created post for user {0} at {1}", new Object[]{post.getPostedBy(), post.getDate().toString()});
                return PostRepository.getInstance().createPost(post);
            }

            //logger.log(Level.INFO, "[FAILED] Post already for user {1}", new Object[]{post.getPostedBy()});
            return null;
        }

        //logger.log(Level.INFO, "[FAILED] Owner {0} doesn't exist!", new Object[]{ownerId});
        return null;
    }

    /*
    ----------------------
    -- GET
    ----------------------
     */
    @ApiMethod(name = "getAllPosts", httpMethod = HttpMethod.GET, path = "post/all")
    public Collection<Post> getAllPosts(@Nullable
            @Named("limit")
            @DefaultValue("50") int limit) {
        //logger.log(Level.INFO, "Getting all posts");
        return PostRepository.getInstance().getAllPost(limit);
    }

    @ApiMethod(name = "getPostById", httpMethod = HttpMethod.GET, path = "post/{postId}")
    public Post getPostById(@Named("postId") Long postId) {
        return PostRepository.getInstance().getPostById(postId);
    }

    @ApiMethod(name = "getPostsByUser", httpMethod = HttpMethod.GET, path = "user/{userId}/posts")
    public Collection<Post> getPostsByUser(
            @Named("userId") Long userId,
            @Nullable @Named("limit") @DefaultValue("50") int limit
    ) {
        return PostRepository.getInstance().getPostsByUser(userId, limit);
    }

    @ApiMethod(name = "getPostsByFollow", httpMethod = HttpMethod.GET, path = "post/followed/{userId}")
    public Collection<Post> getPostsByFollow(
            @Named("userId") Long userId,
            @Nullable @Named("limit") @DefaultValue("50") int limit
    ) {
        User user = UserRepository.getInstance().getUserById(userId);
        if (user != null) {
            HashSet<Post> news = new HashSet<>();
            user.getFollowing().forEach((id) -> {
                news.addAll(PostRepository.getInstance().getPostsByUser(id, limit));
            });
            return news;

        }
        return null;
    }

    /*
    ----------------------
    -- UPDATE
    ----------------------
     */
    //
    @ApiMethod(name = "updatePost", httpMethod = HttpMethod.PUT, path = "post/{postId}/update")
    public Post updatePost(
            @Named("postId") Long postId,
            @Nullable
            @Named("caption") String caption,
            @Nullable
            @Named("imageUrl") String imageUrl
    ) {
        Post target = PostRepository.getInstance().getPostById(postId);

        if (target != null) {
            if (caption != null) {
                target.setCaption(caption);
            }
            if (imageUrl != null) {
                target.setImageUrl(imageUrl);
            }
            return PostRepository.getInstance().updatePost(target);
        }
        return null;
    }

    /*
    ----------------------
    -- DELETE
    ----------------------
     */
    @ApiMethod(name = "deletePost", httpMethod = HttpMethod.DELETE, path = "post/delete/{postId}")
    public void deletePost(@Named("postId") Long postId) {
        PostRepository.getInstance().deletePost(postId);
    }

    @ApiMethod(name = "deleteAllPosts", httpMethod = HttpMethod.DELETE, path = "post/delete/all")
    public void deleteAllPosts() {
        PostRepository.getInstance().deleteAll();
    }

    @ApiMethod(name = "getAllCounter", httpMethod = HttpMethod.GET, path = "counter/all")
    public Collection<Counter> getAllCounter() {
        return ShardedCounter.getAllCounter();
    }

    /*
    ------------------------
    --- TESTS 
    ------------------------
     */
    /**
     * Test 1: How much time it takes to post of message if followed by 10, 100
     * and 500 followers? (average on 30 measures)
     *
     * @param nbFollowers
     * @return time score in ms
     */
    @ApiMethod(name = "testPostMessageAndNotify", httpMethod = HttpMethod.GET, path = "benchmark/test1/{nbFollowers}")
    public TestResult testPostMessageAndNotify(@Named("nbFollowers") int nbFollowers) {

        // Target makes a post
        long totalTime = 0L;

        for (int i = 0; i < 30; i++) {

            // createUser
            User target = register("username0", "name0", "avatar0");

            // Create followers
            HashSet<Long> followerIds = new HashSet<>();
            for (int j = 1; j <= nbFollowers; j++) {
                User follower = register("username" + j, "name" + j, "avatar" + j);
                follow(follower.getUserId(), target.getUserId());
                followerIds.add(follower.getUserId());
            }

            long startTime = new Date().getTime();
            createPost(target.getUserId(), "image0", "caption0");

            // Notify all follower
            followerIds.forEach((followerId) -> {
                getPostsByFollow(followerId, 50);

            });

            long endTime = new Date().getTime();
            deleteAllUsers();
            totalTime += endTime - startTime;
        }

        logger.log(Level.INFO, "[TEST] Test 1 results is {0}", totalTime / 30);
        return new TestResult(
                "Test 1",
                "How much time it takes to post of message if followed by 10, 100 and 500 followers? (average on 30 measures)",
                totalTime / 30
        );
    }

    /**
     * Test 2: How much time it takes to retrieve the last 10, 100 and 500 last
     * messages ? (average of 30 measures)
     *
     * @param nbPost
     * @return
     */
    @ApiMethod(name = "testRecupPost", httpMethod = HttpMethod.GET, path = "benchmark/test2/{nbPost}")
    public TestResult testRecupPost(@Named("nbPost") int nbPost) {

        long totalTime = 0L;

        for (int i = 0; i < 30; i++) {

            // createUser
            User owner = register("username0", "name0", "avatar0");
            User follower = register("username1", "name1", "avatar1");
            
            follow(follower.getUserId(), owner.getUserId());

            // Create posts
            for (int j = 1; j <= nbPost; j++) {
                createPost(owner.getUserId(), "img" + j, "caption" + j);
            }

            long startTime = new Date().getTime();
            getPostsByFollow(follower.getUserId(), nbPost);
            long endTime = new Date().getTime();

            totalTime += endTime - startTime;
            deleteAllUsers();
        }

        logger.log(Level.INFO, "[TEST] Test 2 results is {0}", totalTime / 30);

        return new TestResult(
                "Test 2",
                "How much time it takes to retrieve the last 10, 100 and 500 last messages ? (average of 30 measures)",
                totalTime / 30
        );
    }

    /**
     * Test 3: How much “likes” can you do per second ?? (average on 30
     * measures)
     *
     * @param nbUsers
     * @return
     */
    @ApiMethod(name = "testLikes", httpMethod = HttpMethod.GET, path = "benchmark/test3/{nbUsers}")
    public TestResult testLikes(@Named("nbUsers") int nbUsers) {
        long totalTime = 0L;

        for (int i = 0; i < 30; i++) {

            // createUser
            User owner = register("username0", "name0", "avatar0");
            Post p = createPost(owner.getUserId(), "img0", "caption0");

            // Create posts
            HashSet<User> followers = new HashSet<>();
            for (int j = 1; j <= nbUsers; j++) {
                followers.add(register("username" + j, "name" + j, "avatar0" + j));
            }

            long startTime = new Date().getTime();
            followers.forEach((f) -> {
                like(f.getUserId(), p.getPostId());
            });

            long endTime = new Date().getTime();

            deleteAllUsers();
            totalTime += endTime - startTime;
        }

        logger.log(Level.INFO, "[TEST] Test 3 results is {0}", totalTime / 30);

        return new TestResult(
                "Test 3",
                "How much “likes” can you do per second ?? (average on 30",
                totalTime / 30
        );
    }

}
