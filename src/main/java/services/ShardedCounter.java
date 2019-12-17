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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;
import com.googlecode.objectify.TxnType;
import endpoint.TinyInstaEndpoint;
import endpoints.repackaged.com.google.common.base.Preconditions;
import entity.Counter;
import entity.CounterShard;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import repository.RepositoryService;
import static repository.RepositoryService.*;

/**
 *
 * @author minhhoangdang
 */
public class ShardedCounter extends RepositoryService {

    public static final long INITIAL_SHARDS = 5;
    private static final long MAX_SHARDS = 15;

    public static final String SHARD_PREFIX = "counterShard-";

    private final Random generator = new Random();

    private static final Logger logger = Logger.getLogger(TinyInstaEndpoint.class.getName());

    private String name;
    private String shardName;

    static {
        ObjectifyService.register(Counter.class);
        ObjectifyService.register(CounterShard.class);
    }

    private ShardedCounter() {
    }

    private ShardedCounter(final String name) {
        this.name = name;
        this.shardName = SHARD_PREFIX + name + "#";
        //logger.log(Level.INFO, "Created a ShardedCounter object of name {0} with shardName = {1}", new Object[]{this.name, this.shardName});
    }

    public void addShards(final long count) {
        Counter cnt = new Counter(this.name);
        Key counterKey = save().entity(cnt).now();

        //logger.log(Level.INFO, "[DEBUG] Added {0} shards for Key({1}) {2}", new Object[]{count, counterKey.getKind(), counterKey.getName()});
        incrementPropTx(counterKey, count, ShardedCounter.INITIAL_SHARDS + count);
    }

    public String getShardName() {
        return shardName;
    }

    private int getShardCount(Counter c) {
        Long result;
        if (c != null) {
            result = c.getShardCount();

        } else {
            result = ShardedCounter.INITIAL_SHARDS;
        }
        return result.intValue();
    }

    private Key<CounterShard> shardKey(final String name) {
        Optional<Key<CounterShard>> q = ofy().load().type(CounterShard.class).keys().list().stream()
                .filter((key) -> (key.getName().equals(name)))
                .findFirst();

        if (q.isPresent()) {
            return q.get();
        } else {
            CounterShard shard = new CounterShard(name);
            return save().entity(shard).now();
        }
    }

    public final void increment() {
        Counter c = getCounter(name);

        Preconditions.checkNotNull(c, shardName, 0, 0);

        if (c != null) {
            int numShards = getShardCount(c);
            long shardNum = generator.nextInt(numShards);

            Key shardKey = shardKey(shardName + shardNum);
            CounterShard shard = (CounterShard) incrementPropTx(shardKey, 1L, 1L);
            c.addShard(shard);
            //logger.log(Level.INFO, "[DEBUG] Shard {0} increments 1 count", shard.getName());
        }
    }

    public final void decrement() {
        Counter c = getCounter(name);

        if (c != null) {
            int numShards = getShardCount(c);
            long shardNum = generator.nextInt(numShards);

            Key shardKey = shardKey(shardName + shardNum);
            CounterShard shard = (CounterShard) incrementPropTx(shardKey, -1L, -1L);
            c.addShard(shard);
            //logger.log(Level.INFO, "[DEBUG] Shard {0} decrements 1 count", shard.getName());
        }
    }

    /*
    ===================
    === TRANSACTION ===
    ===================
     */
    /**
     * Modify Datastore property value inside a transaction
     * https://github.com/objectify/objectify/wiki/Transactions#transactions-and-caching
     *
     * @param key
     * @param amount
     * @param init
     * @return
     */
    private Object incrementPropTx(Key key, final long amount, final long init) {
        switch (key.getKind()) {
            case "Counter":
            default:
                return execute(TxnType.REQUIRES_NEW, () -> {
                    Counter cnt = (Counter) query(key).now();

                    if (cnt != null) {
                        Long value = cnt.getShardCount() + amount > ShardedCounter.MAX_SHARDS ? ShardedCounter.MAX_SHARDS : cnt.getShardCount() + amount;
                        cnt.setShardCount(value);
                    } else {
                        cnt = new Counter(key.getName());
                        cnt.setShardCount(init);
                    }
                    save().entity(cnt);
                    //logger.log(Level.INFO, "Counter {0} now has {1} shards", new Object[]{cnt.getName(), cnt.getShardCount()});
                    return cnt;
                });

            case "CounterShard":
                return execute(TxnType.REQUIRES_NEW, () -> {
                    CounterShard shard = (CounterShard) query(key).now();

                    if (shard != null) {
                        //logger.log(Level.INFO, "[DEBUG] Shard {0} ({1}) of kind {2} has been retrived", new Object[]{key.getName(), shard.getCount(), key.getKind()});
                        shard.setCount(shard.getCount() + amount);
                    } else {
                        shard = new CounterShard(key.getName());
                        shard.setCount(init);
                    }
                    save().entity(shard);
                    //logger.log(Level.INFO, "Shard {0} now has {1} counts", new Object[]{shard.getName(), shard.getCount().intValue()});
                    return shard;
                });
        }
    }

    /*
    ===================================
    == GET / DELETE
    ===================================
     */
    public final long getCount() {
        Long sum = 0L;
        Counter c = getCounter(name);
        if (c != null) {
            sum = c.getShards().stream()
                    .map((shard) -> shard.getCount())
                    .reduce(sum, (accumulator, _item) -> accumulator + _item);
            //logger.log(Level.INFO, "[DEBUG] Counter {0} returns {1} counts", new Object[]{c.getName(), sum.intValue()});
        }

        return sum;
    }

    public String getName() {
        return name;
    }

    public static Collection<Counter> getAllCounter() {
        return query(Counter.class).list();
    }

    private static Counter getCounter(String name) {
        return (Counter) query(Counter.class).id(name).now();
    }

    public static ShardedCounter getShardedCounter(String name) {
        return new ShardedCounter(name);
    }

    public static ShardedCounter createShardedCounter(String name) {
        ShardedCounter sc = new ShardedCounter(name);
        sc.addShards(0L);
        return sc;
    }

    public void deleteCounter() {
        Counter c = (Counter) query(Counter.class).id(name).now();

        if (c != null) {
            HashSet<String> ids = new HashSet<>();
            c.getShards().forEach((shard) -> {
                ids.add(shard.getName());
            });

            delete().type(CounterShard.class).ids(ids);
            delete().type(Counter.class).id(name).now();
        }
    }

    public static void deleteAllShards() {
        Collection<CounterShard> shards = query(CounterShard.class).list();
        Collection<Collection<?>> batches = batch(shards, 400);

        //logger.log(Level.INFO, "{0} batches of {1} shards", new Object[]{batches.size(), shards.size()});
        batches.forEach((batch) -> {
            Collection<CounterShard> b = (Collection<CounterShard>) batch;
            //logger.log(Level.INFO, "Batch with {0} shards", b.size());
            delete().entities(b).now();
        });
    }

    public static void deleteAllCounter() {
        Collection<Counter> counters = query(Counter.class).list();
        Collection<Collection<?>> batches = batch(counters, 400);

        //logger.log(Level.INFO, "{0} batches of {1} counters", new Object[]{batches.size(), counters.size()});
        batches.forEach((batch) -> {
            Collection<Counter> b = (Collection<Counter>) batch;
            //logger.log(Level.INFO, "Batch with {0} counters", b.size());
            delete().entities(b).now();
        });
    }

    public static void deleteAll() {
        deleteAllShards();
        deleteAllCounter();
    }
}
