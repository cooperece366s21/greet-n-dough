package store.postgres;

import model.User;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GreetDoughJdbi {

    public static Jdbi create( String url ) {

        Jdbi jdbi = Jdbi.create( url, BaseDAO.name, BaseDAO.password )
                .installPlugin( new PostgresPlugin() );
        jdbi.installPlugin( new SqlObjectPlugin() );
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

        return jdbi;

    }

    public static class UserRowMapper implements RowMapper<User> {

        @Override
        public User map(final ResultSet rs, final StatementContext ctx) throws SQLException {

            int id = rs.getInt("id");
            String name = rs.getString("name");

            return new User( name, id );

        }

    }

}
