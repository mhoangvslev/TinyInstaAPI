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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Deleter;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Saver;
import entity.User;
import java.util.Collection;
import static repository.RepositoryService.ofy;

/**
 *
 * @author minhhoangdang
 */
public class UserRepository {

    private static UserRepository repo;

    static {
        ObjectifyService.register(User.class);
    }

    private UserRepository() {
    }

    public LoadType query() {
        return ofy().load().type(User.class);
    }

    public Saver save() {
        return ofy().save();
    }

    public Deleter delete() {
        return ofy().delete();
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
    public Key<User> updateUser(User update) {
        Long id = update.getId();
        if (id == null) {
            return null;
        }

        User target = this.getUserById(id);

        if (target != null) {
            target.setUsername(update.getUsername());
            target.setFollowers(update.getFollowers());
            target.setFollowing(update.getFollowing());
            target.setAvatarURL(update.getAvatarURL());

            return save().entity(target).now();
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

    public Collection<User> getUserByName(String username, int limit) {
        return query().filter("username =", username).limit(limit).list();
    }

    // DELETE
    public void deleteUser(User u) {
        delete().type(User.class).id(u.getId()).now();
    }

    public void deleteAll() {
        delete().keys(query().keys());
    }
}
