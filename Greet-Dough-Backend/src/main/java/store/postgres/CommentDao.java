package store.postgres;

import model.Comment;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface CommentDao {

    @SqlUpdate( "DROP TABLE IF EXISTS comments;")
    void deleteTable();

    @SqlUpdate( "CREATE TABLE IF NOT EXISTS comments( " +
                    "comment_id SERIAL " +      "NOT NULL, " +
                    "user_id INT " +            "NOT NULL, " +
                    "post_id INT " +            "NOT NULL, " +
                    "contents TEXT " +          "NOT NULL, " +
                    "parent_id INT " +          "NULL, " +
                    "PRIMARY KEY(comment_id), " +
                    "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                        "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
                    "CONSTRAINT fk_post " + "FOREIGN KEY(post_id) " +
                        "REFERENCES posts(post_id) " + "ON DELETE CASCADE " +
                ");")
    void createTable();

    @SqlUpdate( "INSERT INTO comments ( user_id, contents, post_id, parent_id) " +
                    "VALUES (:user_id, :contents, :post_id, :parent_id);")
    @GetGeneratedKeys("comment_id")
    int addComment(@Bind("contents") String contents,
                   @Bind("user_id") int user_id,
                   @Bind("post_id") int post_id,
                   @Bind("parent_id") Integer parent_id);

    @SqlQuery(  "SELECT * FROM comments " +
                    "WHERE comment_id = (:comment_id);")
    Comment getComment(@Bind("comment_id") int comment_id);

    @SqlQuery(  "SELECT EXISTS( " +
                    "SELECT * FROM comments " +
                    "WHERE comment_id = (:comment_id));")
    boolean hasComment(@Bind("comment_id") int comment_id);

    @SqlQuery(  "SELECT * FROM comments " +
                    "WHERE parent_id = (:parent_id);")
    List<Comment> getReplies(@Bind("parent_id") int parent_id);

    @SqlQuery(  "SELECT * FROM comments " +
                    "WHERE post_id = (:post_id) AND " +
                            "parent_id IS null;")
    List<Comment> getParents(@Bind("post_id") int post_id);

    @SqlQuery(  "SELECT EXISTS( " +
                    "SELECT * FROM comments " +
                    "WHERE comment_id = (:comment_id) AND " +
                            "parent_id IS null);")
    boolean isParent(@Bind("comment_id") int comment_id);

    @SqlUpdate( "DELETE FROM comments " +
                    "WHERE comment_id = (:comment_id);")
    void deleteComment(@Bind("comment_id") int comment_id);

}

