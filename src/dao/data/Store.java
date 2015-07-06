package dao.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Store<T> extends C3P0Store {

	private StoreModel<T> storeModel;

	public Store() {
		storeModel = new StoreModel<T>(Store.class, this.getClass());
	}

	public Boolean add(final T t) {
		if (storeModel.tableModel() != null) {
			executeWithGeneratedKey(storeModel.insertSQL(), new Setter() {
				public void invoke(PreparedStatement preparedStatement) throws Exception {
					Object[] params = storeModel.catchInsertValues(t);
					for (int i = 0; i < params.length; i++)
						preparedStatement.setObject(i + 1, params[i]);
					preparedStatement.addBatch();
				}
			}, new Getter<T>() {
				public T invoke(ResultSet resultSet) throws Exception {
					if (resultSet.next())
						storeModel.idModel().set(t, resultSet.getObject(1));
					return t;
				}
			});
			return true;
		}
		return false;
	}

	public Boolean add(final List<T> list) {
		if (storeModel.tableModel() != null) {
			executeWithGeneratedKey(storeModel.insertSQL(), new Setter() {
				public void invoke(PreparedStatement preparedStatement) throws Exception {
					for (T t : list) {
						Object[] params = storeModel.catchInsertValues(t);
						for (int i = 0; i < params.length; i++)
							preparedStatement.setObject(i + 1, params[i]);
						preparedStatement.addBatch();
					}
				}
			}, new Getter<List<T>>() {
				public List<T> invoke(ResultSet resultSet) throws Exception {
					for (int i = 0; resultSet.next(); i++)
						storeModel.idModel().set(list.get(i), resultSet.getObject(1));
					return list;
				}
			});
			return true;
		}
		return false;
	}

	public Boolean update(final T t) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			return execute(storeModel.updateSQL(), new Setter() {
				public void invoke(PreparedStatement preparedStatement) throws Exception {
					Object[] params = storeModel.catchUpdateValues(t);
					for (int i = 0; i < params.length; i++)
						preparedStatement.setObject(i + 1, params[i]);
					preparedStatement.addBatch();
				}
			});
		}
		return false;
	}

	public Boolean update(final List<T> list) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			return execute(storeModel.updateSQL(), new Setter() {
				public void invoke(PreparedStatement preparedStatement) throws Exception {
					for (T t : list) {
						Object[] params = storeModel.catchUpdateValues(t);
						for (int i = 0; i < params.length; i++)
							preparedStatement.setObject(i + 1, params[i]);
						preparedStatement.addBatch();
					}
				}
			});
		}
		return false;
	}

	public Boolean save(T t) {
		if (storeModel.idModel().get(t) == null)
			return add(t);
		else
			return update(t);
	}

	public Boolean save(List<T> list) {
		Boolean b = true;
		for (T t : list)
			if (storeModel.idModel().get(t) == null)
				b &= add(t);
			else
				b &= update(t);
		return b;
	}

	public Boolean deleteAll() {
		if (storeModel.tableModel() != null) {
			return execute(storeModel.deleteAllSQL());
		}
		return false;
	}

	public Boolean delete(final Object t) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			return execute(storeModel.deleteByIdSQL(), new Setter() {
				public void invoke(PreparedStatement preparedStatement) throws Exception {
					if (t.getClass() == storeModel.tlazz())
						preparedStatement.setObject(1, storeModel.idModel().get(t));
					else
						preparedStatement.setObject(1, t);
					preparedStatement.addBatch();
				}
			});
		}
		return false;
	}

	public Boolean delete(Object[] ids) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			return execute(storeModel.deleteByIdsSQL(ids));
		}
		return false;
	}

	public Boolean delete(List<T> list) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			Object[] ids = new Object[list.size()];
			for (int i = 0; i < ids.length; i++)
				ids[i] = storeModel.idModel().get(list.get(i));
			return execute(storeModel.deleteByIdsSQL(ids));
		}
		return false;
	}

	public T get(Object id) {
		return select(storeModel.selectByIdSQL(), new Object[] { id });
	}

	public List<T> get(Object[] ids) {
		return selectList(storeModel.selectByIdsSQL(ids));
	}

	public T select(Object id) {
		return select(storeModel.selectByIdSQL(), new Object[] { id });
	}

	public T select(String sql) {
		return executeWithResultSet(sql, new Getter<T>() {
			public T invoke(ResultSet resultSet) throws Exception {
				String[] columnNames = columnNames(resultSet);
				if (resultSet.next())
					return storeModel.newTlazz(resultSet, columnNames);
				return null;
			}
		});
	}

	public T select(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<T>() {
			public T invoke(ResultSet resultSet) throws Exception {
				String[] columnNames = columnNames(resultSet);
				if (resultSet.next())
					return storeModel.newTlazz(resultSet, columnNames);
				return null;
			}
		});
	}

	public List<T> selectList(String sql) {
		return executeWithResultSet(sql, new Getter<List<T>>() {
			public List<T> invoke(ResultSet resultSet) throws Exception {
				List<T> list = new ArrayList<T>();
				String[] columnNames = columnNames(resultSet);
				while (resultSet.next())
					list.add(storeModel.newTlazz(resultSet, columnNames));
				return list;
			}
		});
	}

	public List<T> selectList(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<List<T>>() {
			public List<T> invoke(ResultSet resultSet) throws Exception {
				List<T> list = new ArrayList<T>();
				String[] columnNames = columnNames(resultSet);
				while (resultSet.next())
					list.add(storeModel.newTlazz(resultSet, columnNames));
				return list;
			}
		});
	}

	public void selectList(String sql, final Iterator<T> iterator) {
		selectResultSet(sql, new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				String[] columnNames = columnNames(resultSet);
				return iterator.next(storeModel.newTlazz(resultSet, columnNames), i);
			}
		});
	}

	public void selectList(String sql, Object[] params, final Iterator<T> iterator) {
		selectResultSet(sql, params, new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				String[] columnNames = columnNames(resultSet);
				return iterator.next(storeModel.newTlazz(resultSet, columnNames), i);
			}
		});
	}

	public List<T> selectAll() {
		return selectList(storeModel.selectAllSQL());
	}

	public void selectAll(final Iterator<T> iterator) {
		selectList(storeModel.selectAllSQL(), iterator);
	}

}