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
import entity.Message;
import entity.Post;
import entity.User;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

import repository.PostRepository;
import repository.UserRepository;

@Api(name = "tinyinsta", version = "v1", namespace = @ApiNamespace(ownerDomain = "tinyinsta.example.com", ownerName = "tinyinsta.example.com", packagePath = "services"))
public class TinyInstaEndpoint {

    private static final Logger logger = Logger.getLogger(TinyInstaEndpoint.class.getName());

    // Test
    @ApiMethod(name = "hello1", httpMethod = HttpMethod.GET, path = "hello")
    public Message hello1() {
        return new Message("Hello world!");
    }

    @ApiMethod(name = "hello2", httpMethod = HttpMethod.GET, path = "hello2")
    public Message hello2(@Nullable @Named("name") @DefaultValue("world") String name) {
        return new Message("Hello " + name + "!");
    }

    @ApiMethod(name = "hello3", httpMethod = HttpMethod.GET, path = "hello/hello3/{name}")
    public Message hello3(@Named("name") String name) {
        return new Message("Hello " + name + "!");
    }

    // USER
    @ApiMethod(name = "getUser", httpMethod = HttpMethod.GET, path = "user")
    public Collection<User> getUser(
            User searchData,
            @Nullable @Named("limit") @DefaultValue("50") int limit) {
        if (searchData != null) {
            logger.log(Level.INFO, "Getting user {0}", searchData.stringify());
            return UserRepository.getInstance().getUser(searchData, limit);
        }
        return null;
    }

    @ApiMethod(name = "getAllUsers", httpMethod = HttpMethod.GET, path = "user/all")
    public Collection<User> getAllUsers(@Nullable @Named("limit") @DefaultValue("50") int limit) {
        logger.log(Level.INFO, "Getting all users, return size {0}", limit);
        return UserRepository.getInstance().getAllUser(limit);
    }

    @ApiMethod(name = "register", httpMethod = HttpMethod.POST, path = "user/register")
    public User register(User newUser) {
        //logger.log(Level.INFO, "Registering username {0} of name {1}", new Object[]{newUser.getUsername(), newUser.getName()});
        return UserRepository.getInstance().createUser(newUser);
    }

    @ApiMethod(name = "deleteUser", httpMethod = HttpMethod.DELETE, path = "user/delete/{userId}")
    public void deleteUser(@Named("userId") Long userId) {
        logger.log(Level.INFO, "Deleting user of id {0}", userId);
        UserRepository.getInstance().deleteUser(userId);
    }

    @ApiMethod(name = "deleteAllUsers", httpMethod = HttpMethod.DELETE, path = "user/delete/all")
    public void deleteAllUsers() {
        logger.log(Level.INFO, "Deleting all user");
        UserRepository.getInstance().deleteAll();
    }

    @ApiMethod(name = "follow", httpMethod = HttpMethod.PUT, path = "user/{userId}/follow/{targetId}")
    public User follow(@Named("userId") Long userId, @Named("targetId") Long targetId) {
        logger.log(Level.INFO, "User {0} follows User {1}", new Object[]{userId, targetId});

        User user = UserRepository.getInstance().getUserById(userId);
        User target = UserRepository.getInstance().getUserById(targetId);

        target.addFollower(user.getId());
        user.addFollowing(target.getId());

        UserRepository.getInstance().updateUser(target);
        UserRepository.getInstance().updateUser(user);

        return user;

    }

    @ApiMethod(name = "unfollow", httpMethod = HttpMethod.PUT, path = "user/{userId}/unfollow/{targetId}")
    public User unfollow(@Named("userId") Long userId, @Named("targetId") Long targetId) {

        logger.log(Level.INFO, "User {0} unfollows User {1}", new Object[]{userId, targetId});

        User user = UserRepository.getInstance().getUserById(userId);
        User target = UserRepository.getInstance().getUserById(targetId);

        target.removeFollower(user.getId());
        user.removeFollowing(target.getId());

        UserRepository.getInstance().updateUser(target);
        UserRepository.getInstance().updateUser(user);

        return user;
    }

    @ApiMethod(name = "createPost", httpMethod = HttpMethod.POST, path = "post/create")
    public Post createPost(Post data) {
        //logger.log(Level.INFO, "Creating post {0}", data.stringify());
        return PostRepository.getInstance().createPost(data);
    }

    // POSTS
    @ApiMethod(name = "getAllPosts", httpMethod = HttpMethod.GET, path = "post/all")
    public Collection<Post> getAllPosts(@Nullable @Named("limit") @DefaultValue("50") int limit) {
        logger.log(Level.INFO, "Getting all posts");
        return PostRepository.getInstance().getAllPost(limit);
    }

}
