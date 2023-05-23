package sg.edu.nus.iss.workshop37.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.workshop37.models.Image;

import static sg.edu.nus.iss.workshop37.repositories.DBQueries.*;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class ArticleRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public int insertArticleImage(String filename, String mediaType, byte[] image) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement statement = conn.prepareStatement(SQL_INSERT_IMAGE, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, filename);
            statement.setString(2, mediaType);
            statement.setBytes(3, image);
            return statement;
        }, keyHolder);
        BigInteger primaryKey = (BigInteger) keyHolder.getKey();

        return primaryKey.intValue();
    }

    public int insertArticleContent(int id, String title, String content) {
        Document d = new Document("article_id", id)
                .append("title", title)
                .append("content", content);
        Document insertedDoc = mongoTemplate.insert(d, "articles");
        return insertedDoc.getInteger("article_id");
    }

    public Optional<Image> getArticleImage(int imageId) {
        Optional<Image> imageOpt = jdbcTemplate.query(SQL_SELECT_IMAGE, rs -> {
            if (!rs.next()) {
                System.out.println("Image is empty");
                return Optional.empty();
            }
            Image img = new Image(imageId, rs.getString("filename"), rs.getString("media_type"),
                    rs.getBytes("content"));
            return Optional.of(img);

        }, imageId);

        return imageOpt;
    }
}
