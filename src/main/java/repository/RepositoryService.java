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
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.TxnType;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Deleter;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Saver;
import entity.Counter;
import entity.CounterShard;
import entity.Post;
import entity.User;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author minhhoangdang
 */
public abstract class RepositoryService {

    static {
        // Register all classes
        ObjectifyService.register(User.class);
        ObjectifyService.register(Post.class);
        ObjectifyService.register(Counter.class);
        ObjectifyService.register(CounterShard.class);
    }

    private static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static LoadType query(Class<?> aClass) {
        return ofy().load().type(aClass);
    }

    public static Saver save() {
        return ofy().save();
    }

    public LoadResult query(Key<?> k) {
        return ofy().load().key(k);
    }

    public static Deleter delete() {
        return ofy().delete();
    }

    public static Object transact(Work<?> work) {
        return ofy().transact(work);
    }

    public static Object execute(TxnType transactionType, Work<?> work) {
        return ofy().execute(transactionType, work);
    }

    public static Collection<Collection<?>> batch(Collection<?> collection, int chunkSize) {
        if (chunkSize <= 0) {
            return null;  // just in case :)
        }

        ArrayList<?> target = new ArrayList<>(collection);

        int rest = target.size() % chunkSize;
        int chunks = target.size() / chunkSize + (rest > 0 ? 1 : 0);

        Collection<Collection<?>> arrays = new ArrayList<>();
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            arrays.add(target.subList(i * chunkSize, i * chunkSize + chunkSize));
        }
        if (rest > 0) { // only when we have a rest
            arrays.add(target.subList((chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest));
        }
        return arrays;
    }
}
