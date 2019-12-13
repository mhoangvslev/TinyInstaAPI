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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

import repository.PostRepository;
import shardedcounter.ShardedCounter;
import repository.UserRepository;

@Api(name = "tinyinsta", version = "v1", namespace = @ApiNamespace(ownerDomain = "tinyinsta.example.com", ownerName = "tinyinsta.example.com", packagePath = "services"))
public class TinyInstaEndpoint {

    private static final Logger logger = Logger.getLogger(TinyInstaEndpoint.class.getName());
    private static final int QUERY_MAX_LIMIT = Integer.MAX_VALUE;

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
    @ApiMethod(name = "register", httpMethod = HttpMethod.POST, path = "user/register/{username}/{name}/{avatarURL}")
    public User register(
            @Named("username") String username,
            @Named("name") String name,
            @Named("avatarURL") String avatarURL
    ) {
        User target = new User(username, name, avatarURL);

        Collection<User> decoy = UserRepository.getInstance().getUserByUserName(target.getUsername(), QUERY_MAX_LIMIT);
        if (!decoy.contains(target)) {
            logger.log(Level.INFO, "[SUCCESS] Registering username {0} of name {1}", new Object[]{target.getUsername(), target.getName()});
            return UserRepository.getInstance().createUser(target);

        }

        logger.log(Level.INFO, "[FAILED] Already exists username {0} of name {1}", new Object[]{target.getUsername(), target.getName()});
        return null;
    }

    /*
    ----------------------
    -- GET
    ----------------------
     */
    /**
     *
     * @param limit query results size limit
     * @return
     */
    @ApiMethod(name = "getAllUsers", httpMethod = HttpMethod.GET, path = "user/all")
    public Collection<User> getAllUsers(@Nullable @Named("limit") @DefaultValue("50") int limit) {
        logger.log(Level.INFO, "[SUCCESS] Getting all users, return size {0}", limit);
        return UserRepository.getInstance().getAllUser(limit);
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
            @Nullable @Named("username") String username,
            @Nullable @Named("name") String name,
            @Nullable @Named("avatarURL") String avatarURL
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

            logger.log(Level.INFO, "[SUCCESS] Update user {0}", targetId);
            return UserRepository.getInstance().updateUser(target);
        }

        logger.log(Level.INFO, "[FAILED] Update user {0}", targetId);
        return null;
    }

    @ApiMethod(name = "follow", httpMethod = HttpMethod.PUT, path = "user/{userId}/follow/{targetId}")
    public User follow(@Named("userId") Long userId, @Named("targetId") Long targetId) {

        User user = UserRepository.getInstance().getUserById(userId);
        User target = UserRepository.getInstance().getUserById(targetId);

        if (user != null && target != null) {
            target.addFollower(user.getId());
            user.addFollowing(target.getId());

            UserRepository.getInstance().updateUser(target);
            UserRepository.getInstance().updateUser(user);

            logger.log(Level.INFO, "[SUCCESS] User {0} follows User {1}", new Object[]{userId, targetId});

            return user;
        }

        logger.log(Level.INFO, "[FAILED] User {0} follows User {1}", new Object[]{userId, targetId});
        return null;
    }

    @ApiMethod(name = "unfollow", httpMethod = HttpMethod.PUT, path = "user/{userId}/unfollow/{targetId}")
    public User unfollow(@Named("userId") Long userId, @Named("targetId") Long targetId) {

        User user = UserRepository.getInstance().getUserById(userId);
        User target = UserRepository.getInstance().getUserById(targetId);

        if (user != null && target != null) {
            target.removeFollower(user.getId());
            user.removeFollowing(target.getId());

            UserRepository.getInstance().updateUser(target);
            UserRepository.getInstance().updateUser(user);

            logger.log(Level.INFO, "[SUCCESS] User {0} unfollows User {1}", new Object[]{userId, targetId});

            return user;
        }

        logger.log(Level.INFO, "[FAILED] User {0} unfollows User {1}", new Object[]{userId, targetId});
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
            post.addLike(user.getId());
            PostRepository.getInstance().addToLikeCounter(post);
            PostRepository.getInstance().updatePost(post);

            logger.log(Level.INFO, "[SUCCESS] User {0} likes Post {1}", new Object[]{user.getId(), post.getPostId()});
            return post;
        }

        logger.log(Level.INFO, "[FAILED] User {0} likes Post {1}", new Object[]{userId, postId});
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
            post.removeLike(user.getId());
            PostRepository.getInstance().removeFromLikeCounter(post);
            PostRepository.getInstance().updatePost(post);

            logger.log(Level.INFO, "[SUCCESS] User {0} unlikes Post {1}", new Object[]{user.getId(), post.getPostId()});
            return post;
        }

        logger.log(Level.INFO, "[FAILED] User {0} unlikes Post {1}", new Object[]{userId, postId});
        return null;
    }

    /*
    ----------------------
    -- DELETE
    ----------------------
     */
    @ApiMethod(name = "deleteUser", httpMethod = HttpMethod.DELETE, path = "user/delete/{userId}")
    public void deleteUser(@Named("userId") Long userId) {
        logger.log(Level.INFO, "Deleting user of id {0}", userId);
        UserRepository.getInstance().deleteUser(userId);

        Collection<Post> posts = PostRepository.getInstance().getPostsByUser(userId, QUERY_MAX_LIMIT);
        if (posts != null) {
            int res = PostRepository.getInstance().deletePosts(posts);
            logger.log(Level.INFO, "[SUCCESS] Deleted {0} posts that belong to user of id {1}", new Object[]{res, userId});
        }
    }

    @ApiMethod(name = "deleteAllUsers", httpMethod = HttpMethod.DELETE, path = "user/delete/all")
    public void deleteAllUsers() {
        logger.log(Level.INFO, "Deleting all user");
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
        //logger.log(Level.INFO, "Creating post {0}", data.stringify());

        User owner = UserRepository.getInstance().getUserById(ownerId);
        if (owner != null) {
            Post post = new Post(imageURL, caption, owner.getId(), new Date());

            Collection<Post> decoy = PostRepository.getInstance().getPostsByUser(ownerId, QUERY_MAX_LIMIT);
            if (!decoy.contains(post)) {
                logger.log(Level.INFO, "[SUCCESS] Created post for user {0} at {1}", new Object[]{post.getPostedBy(), post.getDate().toString()});
                return PostRepository.getInstance().createPost(post);
            }

            logger.log(Level.INFO, "[FAILED] Post already for user {1}", new Object[]{post.getPostedBy()});
            return null;
        }

        logger.log(Level.INFO, "[FAILED] Owner {0} doesn't exist!", new Object[]{ownerId});
        return null;
    }

    /*
    ----------------------
    -- GET
    ----------------------
     */
    @ApiMethod(name = "getAllPosts", httpMethod = HttpMethod.GET, path = "post/all")
    public Collection<Post> getAllPosts(@Nullable @Named("limit") @DefaultValue("50") int limit) {
        logger.log(Level.INFO, "Getting all posts");
        return PostRepository.getInstance().getAllPost(limit);
    }

    @ApiMethod(name = "getPostsByUser", httpMethod = HttpMethod.GET, path = "post/{userId}")
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
        Collection<Post> news = new ArrayList<>();
        user.getFollowing().forEach((id) -> {
            news.addAll(PostRepository.getInstance().getPostsByUser(id, limit));
        });
        return news;
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
            @Nullable @Named("caption") String caption,
            @Nullable @Named("imageUrl") String imageUrl
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
    public Collection<Counter> getAllCounter(){
        return ShardedCounter.getAllCounter();
    }

}
