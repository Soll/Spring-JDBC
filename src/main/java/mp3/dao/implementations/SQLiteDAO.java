package mp3.dao.implementations;

import mp3.dao.interfaces.MP3Dao;
import mp3.dao.objects.Author;
import mp3.dao.objects.MP3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component("sqliteDAO")
public class SQLiteDAO implements MP3Dao {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private String mp3View = "mp3_view";
    private String mp3Table = "mp3";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Transactional
    public int insert(MP3 mp3) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlAuthor = "insert into author (name) values (:name)";
        Author author = mp3.getAuthor();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", author.getName());
        jdbcTemplate.update(sqlAuthor, params, keyHolder);

        int author_id = keyHolder.getKey().intValue();

        String sqlMP3 = "insert into mp3 (name, author_id) values (:name, :author_id)";
        params = new MapSqlParameterSource();
        params.addValue("name", mp3.getName());
        params.addValue("author_id", author_id);

        return jdbcTemplate.update(sqlMP3, params);

    }

    public int insertList(List<MP3> mp3List) {

        int i = 0;

        for (MP3 mp3 : mp3List) {
            insert(mp3);
            i++;
        }

        return i;
    }

    public void delete(int id) {

        String sql = "delete from " + mp3Table + " where id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        jdbcTemplate.update(sql, params);
    }

    public void delete(MP3 mp3) {
        delete(mp3.getId());
    }

    public MP3 getMP3ById(int id) {

        String sql = "select * from " + mp3View + " where mp3_id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return jdbcTemplate.queryForObject(sql, params, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByName(String name) {
        String sql = "select * from " + mp3View + " where upper (mp3_name) like :name";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", "%" + name.toUpperCase() + "%");

        return jdbcTemplate.query(sql, params, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByAuthor(String author) {
        String sql = "select * from " + mp3View + " where upper (author_name) like :author";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("author", "%" + author.toUpperCase() + "%");

        return jdbcTemplate.query(sql, params, new MP3RowMapper());
    }

    public int getMP3Count() {
        String sql = "select count(*) from " + mp3View;

        return jdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class);
    }

    public Map<String, Integer> getStat() {

        String sql = "select author_name, count(*) as count from " + mp3View + " group by author_name";

        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> extractData(ResultSet resultSet) throws SQLException, DataAccessException {

                Map<String, Integer> map = new TreeMap<String, Integer>();
                while (resultSet.next()) {
                    String author = resultSet.getString("author_name");
                    int count = resultSet.getInt("count");
                    map.put(author, count);
                }
                return map;
            }
        });
    }

    private static final class MP3RowMapper implements RowMapper<MP3> {

        @Override
        public MP3 mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            Author author = new Author();
            author.setId(resultSet.getInt("author_id"));
            author.setName(resultSet.getString("author_name"));

            MP3 mp3 = new MP3();
            mp3.setId(resultSet.getInt("mp3_id"));
            mp3.setName(resultSet.getString("mp3_name"));
            mp3.setAuthor(author);

            return mp3;
        }
    }

}
