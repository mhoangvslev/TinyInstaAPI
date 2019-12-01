/**
 *
 * @author acoussea
 */
package entity;

import com.googlecode.objectify.annotation.*;
import java.util.Date;
import java.util.HashSet;

@Entity
public class Post {

    @Id
    private Long postId;

    private String imageUrl;
    private String caption;
    private HashSet<Long> likedBy;

    @Index
    private Date date;

    @Index
    private Long postedBy;

    public Post() {
    }

    public Post(String imageUrl, String caption, Long postedBy) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.postedBy = postedBy;
        this.date = new Date();
        this.likedBy = new HashSet<>();
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

    public String stringify() {
        String id = postId == null ? "decoy" : postId.toString();
        return "Post{" + "postId=" + id + ", imageUrl=" + imageUrl + ", caption=" + caption + ", likedBy=" + likedBy.size() + ", date=" + date + ", postedBy=" + postedBy + '}';
    }

}
