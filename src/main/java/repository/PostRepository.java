package repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Deleter;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Saver;
import entity.Post;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import static repository.RepositoryService.ofy;

/**
 *
 * @author acoussea
 */
public class PostRepository {

    private static PostRepository postRepo;

    static {
        ObjectifyService.register(Post.class);
    }

    private PostRepository() {
    }

    public LoadType query() {
        return ofy().load().type(Post.class);
    }

    public Saver save() {
        return ofy().save();
    }

    public Deleter delete() {
        return ofy().delete();
    }

    public static synchronized PostRepository getInstance() {
        if (postRepo == null) {
            postRepo = new PostRepository();
        }
        return postRepo;
    }

    // POST
    public Post createPost(Post post){
        save().entity(post).now();
        return post;
    }

    //PUT
    public Post updatePost(Post update) {
        Long id = update.getPostId();
        if (id == null){
            return null;
        }
        Post target = this.getPostById(id);
        if (target != null) {
            target.setImageUrl(update.getImageUrl());
            target.setCaption(update.getCaption());
            save().entity(target).now();
            return target;
        }
        return null;
    }


    // GET
    public Collection<Post> getAllPost(int limit) {
        return query().limit(limit).list();
    }

    public Post getPostById(Long id) {
        return (Post) query().id(id).now();
    }

    public Collection<Post> getPostsByDate(Date date, int limit){
        return query().filter("date =", date).limit(limit).list();
    }

    public Collection<Post> getPostsByUser(Long postedBy, int limit) {
        return query().filter("postedBy =", postedBy).limit(limit).list();
    }

    // DELETE
    public void deletePost(Long id) {
        delete().type(Post.class).id(id).now();
    }
    
    public int deletePosts(Collection<Post> posts){
        Long[] arr = (Long[]) posts.stream()
                .filter((p) -> (p != null && p.getPostId() != null))
                .map((p) -> (p.getPostId())).toArray();
        
        Collection<Long> ids = Arrays.asList(arr);
        delete().type(Post.class).ids(ids).now();
        return ids.size();
    }

    public void deleteAll() {
        delete().keys(query().keys());
    }
}
