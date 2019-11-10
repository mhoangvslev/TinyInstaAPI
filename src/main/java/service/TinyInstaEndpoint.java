package service;

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
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.HashSet;
import java.util.List;

@Api(
        name = "tinyinsta", 
        version = "v1", 
        namespace = @ApiNamespace(
                ownerDomain = "tinyinsta.example.com", 
                ownerName = "tinyinsta.example.com", 
                packagePath = ""))
public class TinyInstaEndpoint {

    @ApiMethod(name = "getuser", path = "getuser")
    public List<Entity> getUser(@Named("username") String username) {
        Query q = new Query("User").setFilter(new Query.FilterPredicate("username", Query.FilterOperator.EQUAL, username));
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);
        List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
        return result;
    }

    @ApiMethod(name = "getallusers", path = "getuser/all")
    public List<Entity> getAllUsers() {
        Query q = new Query("User");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);
        List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
        return result;
    }

    @ApiMethod(name = "register", path = "register")
    public void insertUser(@Named("username") String username) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity e = new Entity("User", username);
        datastore.put(e);
    }

    @ApiMethod(name = "follow")
    public void follow(@Named("followee") String folowee, @Named("follower") String follower) {
        for (Entity e : getUser(folowee)) {
            if(e != null){
                HashSet<String> followers = (HashSet) e.getProperty("followers");
                followers.add(folowee);
                e.setProperty("followers", followers);
            }
        }
    }
    
    @ApiMethod(name = "unfollow")
    public void unfollow(@Named("followee") String folowee, @Named("follower") String follower) {
        for (Entity e : getUser(folowee)) {
            if(e != null){
                HashSet<String> followers = (HashSet) e.getProperty("followers");
                followers.remove(folowee);
                e.setProperty("followers", followers);
            }
        }
    }

}
