package repository;

import services.ShardedCounter;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;
import endpoint.TinyInstaEndpoint;
import entity.Post;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static repository.RepositoryService.*;
import services.ImageServlet;

/**
 *
 * @author acoussea
 */
public class PostRepository extends RepositoryService {

    private static PostRepository postRepo;
    private static final String LIKE_COUNTER_PREFIX = "like_counter#";

    private static final Logger logger = Logger.getLogger(TinyInstaEndpoint.class.getName());

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

            if (!update.getImageUrl().equals(target.getImageUrl())) {
                ImageServlet.removeBlob(target.getImageUrl());
            }

            target.setImageUrl(update.getImageUrl());
            target.setCaption(update.getCaption());
            save().entity(target).now();
            return target;
        }
        return null;
    }

    public void addToLikeCounter(Post p) {
        transact(() -> {
            String name = PostRepository.LIKE_COUNTER_PREFIX + p.getPostId();
            ShardedCounter counter = ShardedCounter.getShardedCounter(name);

            if (p.getLikes() + 1 <= p.getLikedBy().size()) {
                counter.increment();
                p.setLikes(counter.getCount());
            }
            return null;
        });
    }

    public void removeFromLikeCounter(Post p) {
        transact(() -> {
            String name = PostRepository.LIKE_COUNTER_PREFIX + p.getPostId();
            ShardedCounter counter = ShardedCounter.getShardedCounter(name);

            if (p.getLikes() >= 1) {
                counter.decrement();
                p.setLikes(counter.getCount());
            }
            return null;
        });
    }

    // GET
    public Collection<Post> getAllPost(int limit) {
        return query().order("-date").limit(limit).list();
    }

    public Post getPostById(Long id) {
        return (Post) query().id(id).now();
    }

    public Collection<Post> getPostsByUser(Long postedBy, int limit) {
        return query().filter("postedBy", postedBy.toString()).order("-date").limit(limit).list();
    }

    // DELETE
    public void deletePost(Long id) {
        ShardedCounter.getShardedCounter(LIKE_COUNTER_PREFIX + id).deleteCounter();
        Post p = (Post) query().id(id).now();
        ImageServlet.removeBlob(p.getImageUrl());
        delete().type(Post.class).id(id).now();
    }

    public void deletePosts(Collection<Post> posts) {
        HashSet<Long> ids = new HashSet<>();
        posts.forEach((p) -> {
            Long postId = p.getPostId();
            ImageServlet.removeBlob(p.getImageUrl());
            ShardedCounter.getShardedCounter(LIKE_COUNTER_PREFIX + postId).deleteCounter();
            ids.add(p.getPostId());
        });

        delete().type(Post.class).ids(ids).now();
    }

    public void deleteAll() {
        Collection<Post> posts = query().list();
        Collection<Collection<?>> batches = batch(posts, 400);

        logger.log(Level.INFO, "{0} batches of {1} users", new Object[]{batches.size(), posts.size()});
        batches.forEach((batch) -> {
            Collection<Post> b = (Collection<Post>) batch;
            logger.log(Level.INFO, "Batch with {0} users", b.size());

            HashSet<Long> ids = new HashSet<>();
            HashSet<String> blobs = new HashSet<>();

            b.forEach((post) -> {
                ids.add(post.getPostId());
                blobs.add(post.getImageUrl());
            });

            ImageServlet.removeBlob(blobs.stream().toArray(String[]::new));
            delete().type(Post.class).ids(ids).now();
        });

        ShardedCounter.deleteAllCounter();
    }
}
