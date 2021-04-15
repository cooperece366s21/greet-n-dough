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

        // Register RowMappers
        jdbi.registerRowMapper( new UserRowMapper() );
        jdbi.registerRowMapper( new PostRowMapper() );
        jdbi.registerRowMapper( new ImageRowMapper() );
//        jdbi.registerRowMapper( new LikeRowMapper() );
        jdbi.registerRowMapper( new CommentRowMapper() );

        return jdbi;

    }

    public static class UserRowMapper implements RowMapper<User> {

        @Override
        public User map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int uid = rs.getInt("user_id");
            String name = rs.getString("user_name");

            return new User( name, uid );

        }

    }

    public static class PostRowMapper implements RowMapper<Post> {

        @Override
        public Post map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int pid = rs.getInt("post_id");
            int uid = rs.getInt("user_id");
            Integer iid = rs.getObject("image_id", Integer.class);
            String contents = rs.getString("contents");

            return new Post( contents, pid, uid, iid );

        }

    }

    public static class ImageRowMapper implements RowMapper<Image> {

        @Override
        public Image map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int iid = rs.getInt("image_id");
            int uid = rs.getInt("user_id");
            String path = rs.getString("path");

            return new Image( path, iid, uid );

        }

    }

//    public static class LikeRowMapper implements RowMapper<Likes> {
//
//        @Override
//        public Likes map( final ResultSet rs, final StatementContext ctx ) throws SQLException {
//
//            int pid = rs.getInt("post_id");
//            int uid = rs.getInt("user_id");
//
//            return new Likes( pid, uid );
//
//        }
//
//    }

    public static class CommentRowMapper implements RowMapper<Comment> {

        @Override
        public Comment map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            String content = rs.getString("content");
            int cid = rs.getInt("comment_id");
            int uid = rs.getInt("user_id");
            int pid = rs.getInt("post_id");
            Integer parentID = rs.getObject("parent_id", Integer.class);

            return new Comment( content, cid, uid, pid, parentID );

        }

    }

}
