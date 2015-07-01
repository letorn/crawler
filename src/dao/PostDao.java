package dao;

import model.Post;

import org.springframework.stereotype.Repository;

import dao.data.Store;

@Repository("postDao")
public class PostDao extends Store<Post> {

}
