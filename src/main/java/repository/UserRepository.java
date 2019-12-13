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
import entity.User;
import java.util.Collection;
import static repository.RepositoryService.*;

/**
 *
 * @author minhhoangdang
 */
public class UserRepository extends RepositoryService {

    private static UserRepository repo;

    static {
        ObjectifyService.register(User.class);
    }

    private UserRepository() {
    }

    private LoadType query() {
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

    private Query queryFilter(Query q, String propertyName, Object propertyValue, int limit) {
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
        delete().type(User.class).id(userId).now();
    }

    public void deleteAll() {
        delete().keys(query().keys());
    }
}
