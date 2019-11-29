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
import com.googlecode.objectify.Key;
import entity.User;
import java.util.Collection;
import javax.annotation.Nullable;

import repository.UserRepository;

@Api(name = "tinyinsta", version = "v1", namespace = @ApiNamespace(ownerDomain = "tinyinsta.example.com", ownerName = "tinyinsta.example.com", packagePath = "services"))
public class TinyInstaEndpoint {

    // Test
    /*@ApiMethod(name = "hello", httpMethod = HttpMethod.GET, path = "hello")
    public String hello() {
        return "Hello world\n";
    }*/

    // USER
    @ApiMethod(name = "getUser", httpMethod = HttpMethod.GET, path = "user/{username}")
    public Collection<User> getUser(@Named("username") String username, @Nullable @Named("limit") @DefaultValue("50") int limit) {
        return UserRepository.getInstance().getUserByName(username, limit);
    }

    @ApiMethod(name = "getAllUsers", httpMethod = HttpMethod.GET, path = "user/all")
    public Collection<User> getAllUsers(@Nullable @Named("limit") @DefaultValue("50") int limit) {
        return UserRepository.getInstance().getAllUser(limit);
    }

    @ApiMethod(name = "register", httpMethod = HttpMethod.POST, path = "user/register/{username}")
    public Key<User> register(@Named("username") String username) {
        return UserRepository.getInstance().createUser(new User(username));
    }

    @ApiMethod(name = "follow", httpMethod = HttpMethod.PUT, path = "user/{user}/follow/{target}")
    public User follow(@Named("user") Long userId, @Named("target") Long targetId) {

        User user = UserRepository.getInstance().getUserById(userId);
        User target = UserRepository.getInstance().getUserById(targetId);

        target.addFollower(user.getId());
        user.addFollowing(target.getId());

        UserRepository.getInstance().updateUser(target);
        UserRepository.getInstance().updateUser(user);

        return user;

    }

    @ApiMethod(name = "unfollow", httpMethod = HttpMethod.PUT, path = "user/{user}/unfollow/{target}")
    public User unfollow(@Named("user") Long userId, @Named("target") Long targetId) {
        User user = UserRepository.getInstance().getUserById(userId);
        User target = UserRepository.getInstance().getUserById(targetId);

        target.removeFollower(user.getId());
        user.removeFollowing(target.getId());

        UserRepository.getInstance().updateUser(target);
        UserRepository.getInstance().updateUser(user);

        return user;
    }

    // POSTS
}
