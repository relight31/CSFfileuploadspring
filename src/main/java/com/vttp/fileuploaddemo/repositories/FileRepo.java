package com.vttp.fileuploaddemo.repositories;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vttp.fileuploaddemo.models.Post;

@Repository
public class FileRepo {
    Logger logger = Logger.getLogger(FileRepo.class.getName());
    @Autowired
    private JdbcTemplate template;

    private static final String SQL_INSERT_BLOB = "insert into post(title, media_type, pic) values (?,?,?)";
    private static final String SQL_GET_BLOB = "select * from post where post_id = ?";

    public boolean upload(String title, String mediatype, InputStream file) {
        return template.update(SQL_INSERT_BLOB, title, mediatype, file) == 1;
    }

    public Optional<Post> getPost(int id) {
        Optional<Post> opt = template.query(SQL_GET_BLOB,
                (ResultSet rs) -> {
                    if (!rs.next()) {
                        logger.info("No results returned");
                        return Optional.empty();
                    }
                    logger.info("result returned");
                    return Optional.of(Post.createFromResultSet(rs));
                },
                id);
        return opt;
    }
}
