package dao;

import model.Technology;

import org.springframework.stereotype.Repository;

import dao.data.Store;

@Repository("technologyDao")
public class TechnologyDao extends Store<Technology> {

}
