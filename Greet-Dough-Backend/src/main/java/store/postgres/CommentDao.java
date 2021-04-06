package store.postgres;

import model.Comment;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

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

    @SqlUpdate("DROP TABLE comment;")
    void resetTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS comment( " +
            "comment_id SERIAL " +  "NOT NULL, " +
            "user_id INT " +        "NOT NULL, " +
            "content TEXT " +       "NOT NULL, " +
            "PRIMARY KEY(comment_id), " +
            "CONSTRAINT fk_user " + "FOREIGN KEY(user_id) " +
                "REFERENCES users(user_id) " + "ON DELETE CASCADE, " +
            ");")
    void createTable();

}
