package store.postgres;

import model.Comment;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface CommentDao {

    // columns of POST and COMMENT tables
    // POST TABLE: post id, user id, content
    // COMMENT TABLE: userid, postid, commentid, content
    // SELECT userid, comment.content, nestedIDstructure FROM comment INNER JOIN post ON post.commentid = comment.commentid;

    // -- If not implemented in backend
    // @set x := 0;
    // for each commentid key in nestedIDstructure, search through the list
        // if empty, continue
        // else get the content, userid for the current commentid

    // nestedIDstructure
    // create this structure
    // can this be the commentid column, or UNNEST(nestedIDstructure) AS replies???
    // 1: [2, 3]
    // 2: []
    // 3: [4]

    // if theres no looping through hashtables then split key and values on the same commentid
    // commentid column
    // 1
    // replies column
    // [2, 3]

    @SqlUpdate("DROP TABLE IF EXISTS comments;")
    void deleteTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS comments( " +
            "comment_id SERIAL " +      "NOT NULL, " +
            "user_id INT " +            "NOT NULL, " +
            "post_id INT " +            "NOT NULL, " +
            "contents TEXT " +          "NOT NULL, " +
            "parent_id INT " +  "NULL, " +
            "PRIMARY KEY(comment_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
            "CONSTRAINT fk_post " + "FOREIGN KEY(post_id) " +
                "REFERENCES posts(post_id) " + "ON DELETE CASCADE " +
            ");")
    void createTable();

    @SqlUpdate("INSERT INTO comments ( user_id, contents, post_id, parent_id) VALUES (:user_id, :contents, :post_id, :parent_id);")
    @GetGeneratedKeys("comment_id")
    int insertComment(@Bind("user_id") int user_id,
                      @Bind("contents") String contents,
                      @Bind("post_id") int post_id,
                      @Bind("parent_id") Integer parent_id);

    /*
    @SqlUpdate("INSERT INTO postComments (comment_id, post_id) VALUES (:comment_id, :post_id);")
    void insertPostComment(@Bind("comment_id") int comment_id,
                           @Bind("post_id") int post_id);
                           */
    /*
    @SqlQuery("DELETE FROM comments WHERE comment_id = (:comment_id); DELETE FROM postComments WHERE comment_id = (:comment_id);")
    void removeComment(@Bind("comment_id") int comment_id);
    */

    @SqlQuery("SELECT * FROM comments WHERE comment_id = (:comment_id);")
    Comment getComment(@Bind("comment_id") int comment_id);

    @SqlQuery("SELECT EXISTS (SELECT * FROM comments WHERE post_id = (:post_id));")
    boolean canComment(@Bind("post_id") int post_id);

    @SqlQuery("SELECT EXISTS (SELECT * FROM comments WHERE comment_id = (:comment_id));")
    boolean canReply(@Bind("comment_id") int comment_id);

    @SqlQuery("SELECT * FROM comments WHERE parent_comment_id = (:parent_id);")
    Comment getReplies(@Bind("parent_id") int parent_id);

    @SqlQuery("SELECT * FROM comments WHERE post_id = (:post_id) AND parent_id IS null;")
    List<Comment> getParents(@Bind("post_id") int post_id);

}

