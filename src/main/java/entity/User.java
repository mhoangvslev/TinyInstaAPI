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

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import java.util.HashSet;
import javax.jdo.annotations.Persistent;

/**
 *
 * @author minhhoangdang
 */
@Entity
public class User {

    @Id
    private Long id;

    @Index
    private String username;

    private String avatarURL;

    @Persistent
    private HashSet<Long> followers;
    private HashSet<Long> following;

    public User() {

    }

    public User(String username) {
        this.username = username;
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashSet<Long> getFollowers() {
        return followers;
    }

    public void setFollowers(HashSet<Long> followers) {
        this.followers = followers;
    }

    public HashSet<Long> getFollowing() {
        return following;
    }

    public void setFollowing(HashSet<Long> following) {
        this.following = following;
    }

    public void addFollower(Long id) {
        this.followers.add(id);
    }

    public void removeFollower(Long id) {
        this.followers.remove(id);
    }

    public void addFollowing(Long id) {
        this.following.add(id);
    }

    public void removeFollowing(Long id) {
        this.following.remove(id);
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", avatarURL=" + avatarURL + '}';
    }    

}
