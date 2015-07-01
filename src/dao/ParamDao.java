package dao;

import model.Param;

import org.springframework.stereotype.Repository;

import dao.data.Store;

@Repository("paramDao")
public class ParamDao extends Store<Param> {

}
