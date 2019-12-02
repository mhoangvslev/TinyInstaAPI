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
import java.util.Objects;

/**
 *
 * @author minhhoangdang
 */
@Entity
public class User {

    @Id
    private Long userId;

    @Index
    private String username;

    @Index
    private String name;

    private String avatarURL;

    private HashSet<Long> followers = new HashSet<>();
    private HashSet<Long> following = new HashSet<>();

    public User() {

    }

    public User(String username, String name, String avatarURL) {
        this.username = username;
        this.name = name;
        this.avatarURL = avatarURL;
    }

    public Long getId() {
        return userId;
    }

    public void setId(Long id) {
        this.userId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (this.followers == null) {
            this.followers = new HashSet();
        }
        this.followers.add(id);
    }

    public void removeFollower(Long id) {
        this.followers.remove(id);
    }

    public void addFollowing(Long id) {
        if (this.following == null) {
            this.following = new HashSet();
        }
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
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

}
