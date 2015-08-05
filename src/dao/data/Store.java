package dao.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dao.data.C3P0Store.Iterator;
import dao.data.C3P0Store.PreparedSetter;
import dao.data.C3P0Store.ResultGetter;

public class Store<T> {

	private C3P0Store store = new C3P0Store();
	private StoreModel<T> storeModel = new StoreModel<T>(Store.class, this.getClass());

	public boolean add(final T t) {
		if (storeModel.tableModel() != null) {
			store.executeGenerate(storeModel.insertSQL(), new PreparedSetter() {
				public void invoke(PreparedStatement preparedStatement) throws Exception {
					Object[] params = storeModel.catchInsertValues(t);
					for (int i = 0; i < params.length; i++)
						preparedStatement.setObject(i + 1, params[i]);
					preparedStatement.addBatch();
				}
			}, new ResultGetter<T>() {
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

	public boolean add(final List<T> list) {
		if (storeModel.tableModel() != null) {
			store.executeGenerate(storeModel.insertSQL(), new PreparedSetter() {
				public void invoke(PreparedStatement preparedStatement) throws Exception {
					for (T t : list) {
						Object[] params = storeModel.catchInsertValues(t);
						for (int i = 0; i < params.length; i++)
							preparedStatement.setObject(i + 1, params[i]);
						preparedStatement.addBatch();
					}
				}
			}, new ResultGetter<List<T>>() {
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

	public boolean update(final T t) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			return store.execute(storeModel.updateSQL(), new PreparedSetter() {
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

	public boolean update(final List<T> list) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			return store.execute(storeModel.updateSQL(), new PreparedSetter() {
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

	public boolean save(T t) {
		if (storeModel.idModel().get(t) == null)
			return add(t);
		else
			return update(t);
	}

	public boolean save(List<T> list) {
		boolean b = true;
		for (T t : list)
			if (storeModel.idModel().get(t) == null)
				b &= add(t);
			else
				b &= update(t);
		return b;
	}

	public boolean delete(final Object t) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			return store.execute(storeModel.deleteByIdSQL(), new PreparedSetter() {
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

	public boolean delete(Object[] ids) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			return store.execute(storeModel.deleteByIdsSQL(ids));
		}
		return false;
	}

	public boolean delete(List<T> list) {
		if (storeModel.tableModel() != null && storeModel.idModel() != null) {
			Object[] ids = new Object[list.size()];
			for (int i = 0; i < ids.length; i++)
				ids[i] = storeModel.idModel().get(list.get(i));
			return store.execute(storeModel.deleteByIdsSQL(ids));
		}
		return false;
	}

	public boolean deleteAll() {
		if (storeModel.tableModel() != null) {
			return store.execute(storeModel.deleteAllSQL());
		}
		return false;
	}

	public T get(Object id) {
		return select(storeModel.selectByIdSQL(), new Object[] { id });
	}

	public List<T> get(Object[] ids) {
		return selectList(storeModel.selectByIdsSQL(ids));
	}

	public List<T> getAll() {
		return selectList(storeModel.selectAllSQL());
	}

	public T select(String sql) {
		return store.executeQuery(sql, new ResultGetter<T>() {
			public T invoke(ResultSet resultSet) throws Exception {
				String[] columnNames = store.columnNames(resultSet);
				if (resultSet.next())
					return storeModel.newTlazz(resultSet, columnNames);
				return null;
			}
		});
	}

	public T select(String sql, final Object[] params) {
		return store.executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<T>() {
			public T invoke(ResultSet resultSet) throws Exception {
				String[] columnNames = store.columnNames(resultSet);
				if (resultSet.next())
					return storeModel.newTlazz(resultSet, columnNames);
				return null;
			}
		});
	}

	public List<T> selectList(String sql) {
		return store.executeQuery(sql, new ResultGetter<List<T>>() {
			public List<T> invoke(ResultSet resultSet) throws Exception {
				List<T> list = new ArrayList<T>();
				String[] columnNames = store.columnNames(resultSet);
				while (resultSet.next())
					list.add(storeModel.newTlazz(resultSet, columnNames));
				return list;
			}
		});
	}

	public List<T> selectList(String sql, final Object[] params) {
		return store.executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<List<T>>() {
			public List<T> invoke(ResultSet resultSet) throws Exception {
				List<T> list = new ArrayList<T>();
				String[] columnNames = store.columnNames(resultSet);
				while (resultSet.next())
					list.add(storeModel.newTlazz(resultSet, columnNames));
				return list;
			}
		});
	}

	public void selectList(String sql, final Iterator<T> iterator) {
		store.selectResultSet(sql, new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				String[] columnNames = store.columnNames(resultSet);
				return iterator.next(storeModel.newTlazz(resultSet, columnNames), i);
			}
		});
	}

	public void selectList(String sql, Object[] params, final Iterator<T> iterator) {
		store.selectResultSet(sql, params, new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				String[] columnNames = store.columnNames(resultSet);
				return iterator.next(storeModel.newTlazz(resultSet, columnNames), i);
			}
		});
	}

	public void selectAll(final Iterator<T> iterator) {
		selectList(storeModel.selectAllSQL(), iterator);
	}

}