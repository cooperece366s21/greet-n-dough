package utility;

import model.*;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GreetDoughJdbi {

    public static Jdbi create( String url ) {

        Jdbi jdbi = Jdbi.create( url, BaseDao.name, BaseDao.password )
                .installPlugin( new PostgresPlugin() )
                .installPlugin( new SqlObjectPlugin() );

        // Register RowMappers
        jdbi.registerRowMapper( new UserRowMapper() );
        jdbi.registerRowMapper( new PostRowMapper() );
        jdbi.registerRowMapper( new ImageRowMapper() );
        jdbi.registerRowMapper( new LikeRowMapper() );
        jdbi.registerRowMapper( new CommentRowMapper() );
        jdbi.registerRowMapper( new ProfileRowMapper() );
        jdbi.registerRowMapper( new UserTierRowMapper() );

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
            String title = rs.getString("post_title");
            String contents = rs.getString("post_contents");
            LocalDateTime timeCreated = rs.getObject("time_created", LocalDateTime.class);

            // Convert the aggregated image_id's into a List
            List<Integer> iidList = Optional.ofNullable( rs.getArray("image_id_agg") )
                    .map(a -> {
                        try {
                            return Arrays.stream((Integer[]) a.getArray()).collect(Collectors.toCollection(LinkedList::new));
                        } catch ( SQLException throwables ) {
                            throwables.printStackTrace();
                            return new LinkedList<Integer>();
                        }
                    }).orElseGet(LinkedList::new);


            return new Post( title, contents, pid, uid, iidList, timeCreated );

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

    public static class LikeRowMapper implements RowMapper<Likes> {

        @Override
        public Likes map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int pid = rs.getInt("post_id");

            // Convert the aggregated user_id's into a HashSet
            HashSet<Integer> userLikes =
                    Arrays.stream( (Integer[]) rs.getArray("user_id_agg").getArray() )
                            .collect(Collectors.toCollection(HashSet::new));

            return new Likes( pid, userLikes );

        }

    }

    public static class CommentRowMapper implements RowMapper<Comment> {

        @Override
        public Comment map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            String content = rs.getString("contents");
            int cid = rs.getInt("comment_id");
            int uid = rs.getInt("user_id");
            int pid = rs.getInt("post_id");
            Integer parentID = rs.getObject("parent_id", Integer.class);

            return new Comment( content, cid, uid, pid, parentID );

        }

    }

    public static class ProfileRowMapper implements RowMapper<Profile> {

        @Override
        public Profile map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int uid = rs.getInt("user_id");
            String bio = rs.getString("user_bio");
            Integer iid = rs.getObject("image_id", Integer.class);

            return new Profile( uid, bio, iid );

        }

    }

    public static class UserTierRowMapper implements RowMapper<UserTier> {

        @Override
        public UserTier map( final ResultSet rs, final StatementContext ctx ) throws SQLException {

            int uid = rs.getInt("user_id");
            int tier = rs.getInt("tier");

            return new UserTier( uid, tier );

        }

    }

}
