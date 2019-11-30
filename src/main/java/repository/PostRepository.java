package repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Deleter;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Saver;
import entity.Post;
import entity.User;
import java.util.Collection;
import static repository.RepositoryService.ofy;
import java.util.Date;

/**
 *
 * @author acoussea
 */
public class PostRepository {

    private static repository.PostRepository postRepo;

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

    public static synchronized repository.PostRepository getInstance() {
        if (postRepo == null) {
            postRepo = new repository.PostRepository();
        }
        return postRepo;
    }

    // POST
    public Post createPost(Post post){
        save().entity(post).now();
        return post;
    }

    //PUT
    public Key<Post> updatePost(Post update) {
        Long id = update.getPostId();
        if (id == null){
            return null;
        }
        Post target = this.getPostById(id);
        if (target != null) {
            target.setImageUrl(update.getImageUrl());
            target.setCaption(update.getCaption());
            return save().entity(target).now();
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

    public Collection<Post> getPostsByUser(User postedBy, int limit) {
        return query().filter("postedBy =", postedBy).limit(limit).list();
    }

    // DELETE
    public void deletePost(Post p) {
        delete().type(Post.class).id(p.getPostId()).now();
    }

    public void deleteAll() {
        delete().keys(query().keys());
    }
}
