/**
 *
 * @author acoussea
 */
package entity;

import com.googlecode.objectify.annotation.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

@Entity
public class Post {

    @Id
    private Long postId;

    private String imageUrl;
    private String caption;
    
    private HashSet<Long> likedBy = new HashSet<>();

    @Index
    private Date date;

    @Index
    private Long postedBy;

    public Post() {
    }

    public Post(String imageUrl, String caption, Long postedBy, Date date) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.postedBy = postedBy;
        this.date = date;
    }

    public Long getPostId() {
        return postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public HashSet<Long> getLikedBy() {
        return likedBy;
    }

    public void addLike(Long u) {
        this.likedBy.add(u);
    }

    public void removeLike(Long u) {
        this.likedBy.remove(u);
    }

    public Date getDate() {
        return date;
    }

    public Long getPostedBy() {
        return postedBy;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setLikedBy(HashSet<Long> likedBy) {
        this.likedBy = likedBy;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPostedBy(Long postedBy) {
        this.postedBy = postedBy;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.imageUrl);
        hash = 61 * hash + Objects.hashCode(this.caption);
        hash = 61 * hash + Objects.hashCode(this.date);
        hash = 61 * hash + Objects.hashCode(this.postedBy);
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
        final Post other = (Post) obj;
        if (!Objects.equals(this.imageUrl, other.imageUrl)) {
            return false;
        }
        if (!Objects.equals(this.caption, other.caption)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.postedBy, other.postedBy)) {
            return false;
        }
        return true;
    }

}
