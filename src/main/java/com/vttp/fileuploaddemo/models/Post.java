package com.vttp.fileuploaddemo.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Post {
    private int postId;
    private String title;
    private byte[] content;
    private String mediatype;

    public String getMediatype() {
        return this.mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public int getPostId() {
        return this.postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public static Post createFromResultSet(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getInt("post_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getBytes("pic"));
        post.setMediatype(rs.getString("media_type"));
        return post;
    }
}
