import model.*;
import utility.*;
import store.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public class Handler {

    private final UserStore userStore;
    private final PostStore postStore;
    private final ImageStore imageStore;
    private final LikeStore likeStore;
    private final SubStore subStore;
    private final FollowStore followStore;
    private final PostCommentStore postCommentStore;
    private final CommentStore commentStore;
    private final Gson gson = new Gson();

    public Handler( UserStore userStore,
                    PostStore postStore,
                    ImageStore imageStore,
                    LikeStore likeStore,
                    CommentStore commentStore,
                    SubStore subStore,
                    FollowStore followStore,
                    PostCommentStore postCommentStore
    ) {
                       
        this.userStore = userStore;
        this.postStore = postStore;
        this.imageStore = imageStore;
        this.likeStore = likeStore;
        this.commentStore = commentStore;
        this.subStore = subStore;
        this.followStore = followStore;
        this.postCommentStore = postCommentStore;

    }

    // PRIVATE HELPER FUNCTIONS
    private Pair grabUserPair( Request req ) {

        int uid = Integer.parseInt( req.queryParams("uid") );
        int targetUser = Integer.parseInt( req.params(":id") );

        if ( userStore.getUser(uid) == null ) {

            System.out.println("Current user "+uid+" does not exist");
            return null;

        }
        if ( userStore.getUser(targetUser) == null ) {

            System.out.println("Target user "+targetUser+" does not exist");
            return null;

        }
        return new Pair(uid, targetUser);

    }

    private int checkUserPostPerms( int uid, int pid ) {

        if ( !postStore.hasPost(pid) ) {

            System.out.println("Post does not exist");
            return 404;

        }

        if ( !userStore.hasUser(uid) ) {

            System.out.println("User does not exist");
            return 404;

        }


        ArrayList<Integer> subs = subStore.getSubscriptions(uid);
        int tuid = postStore.getPost(pid).getUserID();

        return ( (subs != null) && (subs.contains(tuid)) ? 200 : 403 );

    }

    // USER ACTIONS
    public User getUser( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":id") );

        if ( userStore.hasUser(uid) ) {

            res.status(200);
            return userStore.getUser(uid);

        } else {

            res.status(404);
            return null;

        }

    }

    public Integer createUser( Request req, Response res ) {

        String name = req.queryParams("name");
        User tempUser = userStore.addUser(name);

        IOservice.saveObject(userStore, "data/users.txt");
        System.out.println( "User Created: " + tempUser.getName() + ", " + tempUser.getID() );

        res.status(200);
        return 0;

    }

    public int deleteUser( Request req, Response res ) {

        int uid = Integer.parseInt( req.params(":id") );
        User tempUser = userStore.getUser(uid);

        if ( userStore.deleteUser(uid) ) {

            System.out.println( gson.toJson(tempUser) );
            IOservice.saveObject(userStore, "data/users.txt");
            res.status(200);
            return 200;

        } else {

            res.status(404);
            return 404;

        }

    }

    // USER RELATION ACTIONS

    public int subscribe( Request req, Response res ) {

        Pair userPair = this.grabUserPair(req);

        if ( userPair == null ) {

            res.status(404);
            return 404;

        }

        List<Integer> curSubs = subStore.getSubscriptions(userPair.getLeft());

        if ( curSubs != null && curSubs.contains( userPair.getRight() ) ) {

            System.out.println("Current User already is subscribed");
            res.status(404);
            return 404;

        }

        subStore.addSubscription( userPair.getLeft(), userPair.getRight() );
        System.out.println( "current subs: " + subStore.getSubscriptions(userPair.getLeft()) );
        IOservice.saveObject(subStore, "data/subs.txt");

        return 0;

    }

    public int unsubscribe( Request req, Response res ) {

        Pair userPair = this.grabUserPair(req);
        if ( userPair == null ) {

            res.status(404);
            return 404;

        }

        List<Integer> curSubs = subStore.getSubscriptions( userPair.getLeft() );

        if ( curSubs == null ) {

            System.out.println("User not subscribed to anyone");
            res.status(404);
            return 404;

        }

        if ( !curSubs.contains(userPair.getRight()) ) {
            System.out.println("Current user not subscribed to target user");
        }

        subStore.removeSubscription( userPair.getLeft(), userPair.getRight() );
        IOservice.saveObject(subStore, "data/subs.txt");
        System.out.println( subStore.getSubscriptions(userPair.getRight()) );
        res.status(200);
        return 0;

    }

/*
    public Integer follow( Request req ) {

        Pair userPair = this.grabUserPair(req);
        if ( userPair == null ) {
            return null;
        }
        List<Integer> curFollows = followStore.getFollowers( userPair.getLeft() );

        if ( curFollows!=null && curFollows.contains( userPair.getRight() ) ) {
            System.out.println("Current user is already following the target user");
            return null;
        }

        followStore.addFollower( userPair.getLeft(), userPair.getRight() );
        IOservice.saveObject(followStore, "data/follows.txt");
        System.out.println("Currently following: " + followStore.getFollowers( userPair.getLeft() ) );

        return 0;

    }

    public Integer unfollow( Request req ) {

        Pair userPair = this.grabUserPair(req);
        if ( userPair == null ) {
            return null;
        }
        List<Integer> curFollows = followStore.getFollowers( userPair.getLeft() );

        if ( curFollows == null ) {
            System.out.println("Current user not following anyone");
            return null;
        }

        if ( !curFollows.contains( userPair.getRight()) ) {
            System.out.println("Current user not following target user");
            return null;
        }

        followStore.removeFollower( userPair.getLeft(), userPair.getRight() );
        IOservice.saveObject(followStore, "data/follows.txt");
        System.out.println("Currently following: " + followStore.getFollowers( userPair.getLeft() ) );

        return 0;

    }

 */

    // POST ACTIONS
    public Post getPost( Request req, Response res ) {

        int pid = Integer.parseInt( req.params(":id") );

        if ( postStore.hasPost(pid) ) {

            res.status(200);
            System.out.println("Post does not exist");
            return postStore.getPost(pid);

        } else {

            res.status(404);
            return null;

        }

    }

    public int createPost( Request req, Response res ) {

        int uid = Integer.parseInt( req.queryParams("uid") );
        String imageQuery = req.queryParams("imageID");
        String contentQuery = req.queryParams("contents");
        int imageID = (imageQuery != null) ? Integer.parseInt(imageQuery) : -1;

        if ( !userStore.hasUser(uid) ) {

            res.status(404);
            System.out.println("User does not exist");
            return 404;

        }

        Post tempPost = postStore.addPost( contentQuery, uid, imageID );
        IOservice.saveObject( postStore, "data/posts.txt" );

//        Image imagePath = new Image(postID, userID);
//        ImageStore.moveImage(imagePath);

        Likes tempLike = new Likes( tempPost.getID(), uid );
        this.likeStore.addLikes( tempLike );

        System.out.println( gson.toJson(tempPost) );

//        IOservice.saveObject( this.imageStore, "data/posts.txt" );
//        System.out.println( gson.toJson(imagePath) );
        res.status(200);
        return 200;

    }

    public Integer deletePost( Request req, Response res ) {

        int postID = Integer.parseInt( req.params(":id") );
        Post tempPost = postStore.getPost( postID );

        if ( postStore.deletePost( postID ) ) {

            postCommentStore.deletePost( postID );
            likeStore.deleteLikes( postID );

            IOservice.saveObject (postStore, "data/posts.txt" );
            IOservice.saveObject (postCommentStore, "data/postsComments.txt");
            IOservice.saveObject (likeStore, "data/likes.txt");
            System.out.println( gson.toJson(tempPost) );

        } else {

            res.status(404);
            return 404;

        }

        res.status(200);
        return 200;

    }

    public List<Post> getFeed( Request req, Response res ) {

        int uid = Integer.parseInt( req.queryParams("uid") );
        int tuid = Integer.parseInt( req.params(":id") );
        List<Integer> curSubs = subStore.getSubscriptions(uid);

        if ( !userStore.hasUser(uid) || !userStore.hasUser(tuid) ) {

            res.status(404);
            return null;

        }

        if ( (curSubs == null) || (!curSubs.contains(tuid)) ) {

            System.out.println("Current user does not have permission to this feed");
            res.status(403);
            return null;

        }

        return postStore.makeFeed(tuid);

    }

    public Likes getLikes( Request req, Response res ) {

        int uid = Integer.parseInt( req.queryParams("uid") );
        int pid = Integer.parseInt( req.params(":postID") );
        int status = checkUserPostPerms(uid, pid);
        res.status(status);

        if ( res.status() == 200 ) {
            return likeStore.getID(pid);
        }

        System.out.println("Error code: " + res.status() );
        return null;

    }

    public Integer likePost( Request req, Response res ) {

        int pid = Integer.parseInt( req.params(":postID") );
        int uid = Integer.parseInt( req.queryParams("uid") );
        int status = checkUserPostPerms(uid, pid);
        Likes postLikes = likeStore.getID(pid);
        res.status(status);

        if ( res.status()==200 ) {

            HashSet<Integer> userLikes = postLikes.getUserLikes();

            if ( !userLikes.contains(uid) ) {

                postLikes.incrementLike(uid);
                IOservice.saveObject( likeStore, "data/likes.txt" );
                System.out.println( gson.toJson(postLikes) );

            }

        } else {
            System.out.println("Error code: " + res.status());
        }

        return res.status();

    }

    public Integer createComment( Request req, Response res ) {

        int pid = Integer.parseInt( req.params(":postID") );
        int uid = Integer.parseInt( req.queryParams("uid") );
        String contentQuery = req.queryParams("contents");
        int status = checkUserPostPerms(uid, pid);
        res.status(status);

        if ( res.status() == 200 ) {

            Comment newComment = commentStore.addComment( contentQuery, uid );
            postCommentStore.addComment( pid, newComment.getID() );

            IOservice.saveObject( commentStore, "data/comments.txt" );
            IOservice.saveObject( postCommentStore, "data/postsComments.txt" );
            System.out.println( gson.toJson(newComment) );

        } else {
            System.out.println("Error code: " + res.status());
        }

        return res.status();
    }

    // havent checked permissions for this yet
    public ArrayList<Comment> getComments( Request req, Response res ) {

        int pid = Integer.parseInt( req.params(":postID") );
        int uid = Integer.parseInt( req.queryParams("uid") );
        int status = checkUserPostPerms(uid, pid);
        ArrayList<Comment> comments = new ArrayList<>();
        res.status(status);

        if ( res.status() == 200 ) {

            ArrayList<Integer> commentIDs = postCommentStore.getComments(pid);
            for ( Integer ID : commentIDs ) {
                comments.add( commentStore.getComment(ID) );
            }

        } else {
            System.out.println("Error code: " + res.status());
        }

        return comments;
    }

}



