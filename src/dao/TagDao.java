package dao;

import model.Tag;

import org.springframework.stereotype.Repository;

import dao.data.Store;

@Repository("tagDao")
public class TagDao extends Store<Tag> {

}
