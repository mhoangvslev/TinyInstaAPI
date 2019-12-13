package repository;

import shardedcounter.ShardedCounter;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;
import endpoint.TinyInstaEndpoint;
import entity.Post;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;
import static repository.RepositoryService.*;

/**
 *
 * @author acoussea
 */
public class PostRepository extends RepositoryService {

    private static PostRepository postRepo;
    private static final String LIKE_COUNTER_PREFIX = "like_counter#";

    private static final Logger LOGGER = Logger.getLogger(TinyInstaEndpoint.class.getName());

    static {
        ObjectifyService.register(Post.class);
    }

    private PostRepository() {
    }

    private LoadType query() {
        return query(Post.class);
    }

    public static synchronized PostRepository getInstance() {
        if (postRepo == null) {
            postRepo = new PostRepository();
        }
        return postRepo;
    }

    // POST
    public Post createPost(Post post) {
        save().entity(post).now();

        String name = PostRepository.LIKE_COUNTER_PREFIX + post.getPostId();
        ShardedCounter.createShardedCounter(name);
        return post;
    }

    //PUT
    public Post updatePost(Post update) {
        Long id = update.getPostId();
        if (id == null) {
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

    public void addToLikeCounter(Post p) {
        String name = PostRepository.LIKE_COUNTER_PREFIX + p.getPostId();
        ShardedCounter counter = ShardedCounter.getShardedCounter(name);

        if (p.getLikes() + 1 <= p.getLikedBy().size()) {
            counter.increment();
            p.setLikes(counter.getCount());
        }

    }

    public void removeFromLikeCounter(Post p) {
        String name = PostRepository.LIKE_COUNTER_PREFIX + p.getPostId();
        ShardedCounter counter = ShardedCounter.getShardedCounter(name);

        if (p.getLikes() > 1) {
            counter.decrement();
            p.setLikes(counter.getCount());
        }
    }

    // GET
    public Collection<Post> getAllPost(int limit) {
        return query().limit(limit).list();
    }

    public Post getPostById(Long id) {
        return (Post) query().id(id).now();
    }

    public Collection<Post> getPostsByDate(Date date, int limit) {
        return query().filter("date =", date).limit(limit).list();
    }

    public Collection<Post> getPostsByUser(Long postedBy, int limit) {
        return query().filter("postedBy =", postedBy).limit(limit).list();
    }

    // DELETE
    public void deletePost(Long id) {
        ShardedCounter.getShardedCounter(LIKE_COUNTER_PREFIX + id).deleteCounter();
        delete().type(Post.class).id(id).now();
    }

    public int deletePosts(Collection<Post> posts) {

        HashSet<Long> ids = new HashSet<>();
        posts.forEach((p) -> {
            ids.add(p.getPostId());
        });

        delete().type(Post.class).ids(ids).now();
        return ids.size();
    }

    public void deleteAll() {
        delete().keys(query().keys());
    }
}
