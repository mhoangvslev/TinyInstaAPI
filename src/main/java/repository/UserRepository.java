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
package repository;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import endpoint.TinyInstaEndpoint;
import entity.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static repository.RepositoryService.*;
import services.ImageServlet;

/**
 *
 * @author minhhoangdang
 */
public class UserRepository extends RepositoryService {

    private static UserRepository repo;
    private static final Logger logger = Logger.getLogger(TinyInstaEndpoint.class.getName());

    static {
        ObjectifyService.register(User.class);
    }

    private UserRepository() {
    }

    public LoadType query() {
        return query(User.class);
    }

    public static synchronized UserRepository getInstance() {
        if (repo == null) {
            repo = new UserRepository();
        }
        return repo;
    }

    // POST
    public User createUser(User u) {
        save().entity(u).now();
        return u;
    }

    // PUT
    public User updateUser(User update) {
        Long id = update.getUserId();
        if (id == null) {
            return null;
        }

        User target = this.getUserById(id);

        if (target != null) {
            target.setUsername(update.getUsername());
            target.setFollowers(update.getFollowers());
            target.setFollowing(update.getFollowing());

            if (!target.getAvatarURL().equals(update.getAvatarURL())) {
                ImageServlet.removeBlob(target.getAvatarURL());
            }

            target.setAvatarURL(update.getAvatarURL());
            save().entity(target).now();
            return target;
        }
        return null;
    }

    // GET
    public Collection<User> getAllUser(int limit) {
        return query().limit(limit).list();
    }

    public User getUserById(Long id) {
        return (User) query().id(id).now();
    }

    public Collection<User> getUsersByIds(Collection<Long> ids) {
        return query().ids(ids).values();
    }

    public Query queryFilter(Query q, String propertyName, Object propertyValue, int limit) {
        Query res = q == null ? query() : q;
        return res.filter(propertyName + " = ", propertyValue).limit(limit);
    }

    public Collection<User> getUserByUserName(String username, int limit) {
        return queryFilter(null, "username", username, limit).list();
    }

    public Collection<User> getUserByName(String name, int limit) {
        return queryFilter(null, "name", name, limit).list();
    }

    // DELETE
    public void deleteUser(Long userId) {
        transact(() -> {
            User u = (User) query().id(userId).now();
            if (u != null) {
                ImageServlet.removeBlob(u.getAvatarURL());
            }
            delete().type(User.class).id(userId).now();
            return null;
        });
    }

    public void deleteAll() {
        Collection<User> users = query().list();
        Collection<Collection<?>> batches = batch(users, 400);

        logger.log(Level.INFO, "{0} batches of {1} users", new Object[]{batches.size(), users.size()});
        batches.forEach((batch) -> {
            Collection<User> b = (Collection<User>) batch;
            logger.log(Level.INFO, "Batch with {0} users", b.size());

            HashSet<Long> ids = new HashSet<>();
            HashSet<String> blobs = new HashSet<>();

            b.forEach((user) -> {
                ids.add(user.getUserId());
                blobs.add(user.getAvatarURL());
            });

            ImageServlet.removeBlob(blobs.stream().toArray(String[]::new));
            delete().type(User.class).ids(ids).now();
        });
    }
}
