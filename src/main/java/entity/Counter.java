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
package entity;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import java.util.HashSet;
import services.ShardedCounter;

/**
 *
 * @author minhhoangdang
 */
@Entity
@Cache
public class Counter {

    @Id
    private String name;
    private Long shardCount = ShardedCounter.INITIAL_SHARDS;
    private HashSet<CounterShard> shards = new HashSet<>();

    private Counter() {
    }

    public Counter(String counterName) {
        this.name = counterName;
    }

    public Long getShardCount() {
        return shardCount;
    }

    public void setShardCount(Long shardCount) {
        this.shardCount = shardCount;
    }

    public void addShard(CounterShard shard) {
        this.shards.add(shard);
    }

    public String getName() {
        return name;
    }

    public void setName(String counterName) {
        this.name = counterName;
    }

    public HashSet<CounterShard> getShards() {
        return shards;
    }

    public void setShards(HashSet<CounterShard> shards) {
        this.shards = shards;
    }

    @Override
    public String toString() {
        return "Counter{" + "name=" + name + ", shardCount=" + shardCount + '}';
    }
}
