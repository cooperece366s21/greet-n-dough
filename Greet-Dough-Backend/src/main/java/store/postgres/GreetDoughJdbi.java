package store.postgres;

import model.*;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GreetDoughJdbi {

    public static Jdbi create( String url ) {

        Jdbi jdbi = Jdbi.create( url, BaseDao.name, BaseDao.password )
                .installPlugin( new PostgresPlugin() )
                .installPlugin( new SqlObjectPlugin() );
//        jdbi.registerRowMapper(
//
//            // Same as
//            //      new RowMapper<User>()
//            (RowMapper<User>) (rs, ctx) -> {
//                int id = rs.getInt("id");
//                String name = rs.getString("name");
//                return new User( name, id );
//            }
//
//        );

        // you can register row mappers here or you can use @RegisterRowMapper annotation on each Dao
        // method
        jdbi.registerRowMapper( new UserRowMapper() );
        jdbi.registerRowMapper( new PostRowMapper() );
        jdbi.registerRowMapper( new ImageRowMapper() );
        jdbi.registerRowMapper( new LikeRowMapper() );
        jdbi.registerRowMapper( new CommentRowMapper() );

        return jdbi;

    }

    public static class UserRowMapper implements RowMapper<User> {

        @Override
        public User map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int ID = rs.getInt("user_id");
            String name = rs.getString("user_name");

            return new User( name, ID );

        }

    }

    public static class PostRowMapper implements RowMapper<Post> {

        @Override
        public Post map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int ID = rs.getInt("post_id");
            int userID = rs.getInt("user_id");
            Integer imageID = rs.getObject("image_id", Integer.class);
            String contents = rs.getString("contents");

            return new Post( contents, ID, userID, imageID );

        }

    }

    public static class ImageRowMapper implements RowMapper<Image> {

        @Override
        public Image map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int ID = rs.getInt("image_id");
            int userID = rs.getInt("user_id");
            String path = rs.getString("path");

            return new Image( path, ID, userID );

        }

    }

    public static class LikeRowMapper implements RowMapper<Likes> {

        @Override
        public Likes map(final ResultSet rs, final StatementContext ctx) throws SQLException{

            int post_id = rs.getInt("post_id");
            int userID = rs.getInt("user_id");

            return new Likes(post_id, userID);

        }
    }

    public static class CommentRowMapper implements RowMapper<Comment> {

        @Override
        public Comment map(final ResultSet rs, final StatementContext ctx) throws SQLException{

            String content = rs.getString("content");
            int comment_id = rs.getInt("comment_id");
            int userID = rs.getInt("user_id");

            return new Comment(content, comment_id, userID);

        }
    }

}
