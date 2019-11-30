/**
 *
 * @author acoussea
 */

package entity;

import java.util.ArrayList;
import com.googlecode.objectify.annotation.*;
import java.util.Date;

@Entity
public class Post {

    @Id
    private Long postId;

    private String imageUrl;
    private String caption;
    private ArrayList<User> likedBy;

    @Index
    private Date date;

    @Index
    private Long postedBy;

    public Post(String imageUrl, String caption, Long postedBy) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.likedBy = new ArrayList<>();
        this.date = new Date();
        this.postedBy = postedBy;
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

    public ArrayList<User> getLikedBy() {
        return likedBy;
    }

    public void addLike(User u){
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

    public void setLikedBy(ArrayList<User> likedBy) {
        this.likedBy = likedBy;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPostedBy(Long postedBy) {
        this.postedBy = postedBy;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", caption='" + caption + '\'' +
                ", likedBy=" + likedBy +
                ", date=" + date +
                ", postedBy=" + postedBy +
                '}';
    }
}
