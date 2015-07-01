package dao.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

public class JDBCStore {

	@Resource
	public DataSource dataSource;

	private static Connection connection;

	public Map<String, Object> selectMetaMap(String sql) {
		return executeWithResultSet(sql, new Getter<Map<String, Object>>() {
			public Map<String, Object> invoke(ResultSet resultSet) throws Exception {
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				String[] columns = new String[resultSetMetaData.getColumnCount()];
				for (int i = 0; i < columns.length; i++)
					columns[i] = resultSetMetaData.getColumnName(i + 1);
				if (resultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String column : columns)
						map.put(column, resultSet.getObject(column));
					return map;
				}
				return null;
			}
		});
	}

	public Map<String, Object> selectMetaMap(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Map<String, Object>>() {
			public Map<String, Object> invoke(ResultSet resultSet) throws Exception {
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				String[] columns = new String[resultSetMetaData.getColumnCount()];
				for (int i = 0; i < columns.length; i++)
					columns[i] = resultSetMetaData.getColumnName(i + 1);
				if (resultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String column : columns)
						map.put(column, resultSet.getObject(column));
					return map;
				}
				return null;
			}
		});
	}

	public List<Map<String, Object>> selectMetaList(String sql) {
		return executeWithResultSet(sql, new Getter<List<Map<String, Object>>>() {
			public List<Map<String, Object>> invoke(ResultSet resultSet) throws Exception {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				String[] columns = new String[resultSetMetaData.getColumnCount()];
				for (int i = 0; i < columns.length; i++)
					columns[i] = resultSetMetaData.getColumnName(i + 1);
				while (resultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String column : columns)
						map.put(column, resultSet.getObject(column));
					list.add(map);
				}
				return list;
			}
		});
	}

	public List<Map<String, Object>> selectMetaList(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<List<Map<String, Object>>>() {
			public List<Map<String, Object>> invoke(ResultSet resultSet) throws Exception {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				String[] columns = new String[resultSetMetaData.getColumnCount()];
				for (int i = 0; i < columns.length; i++)
					columns[i] = resultSetMetaData.getColumnName(i + 1);
				while (resultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String column : columns)
						map.put(column, resultSet.getObject(column));
					list.add(map);
				}
				return list;
			}
		});
	}

	public void selectResultSet(String sql, final Iterator<ResultSet> iterator) {
		executeWithResultSet(sql, new Getter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				while (resultSet.next())
					if (!iterator.next(resultSet))
						break;
				return null;
			}
		});
	}

	public void selectResultSet(String sql, final Object[] params, final Iterator<ResultSet> iterator) {
		executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				while (resultSet.next())
					if (!iterator.next(resultSet))
						break;
				return null;
			}
		});
	}

	public Boolean selectBoolean(String sql) {
		return executeWithResultSet(sql, new Getter<Boolean>() {
			public Boolean invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getBoolean(1);
				return null;
			}
		});
	}

	public Boolean selectBoolean(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Boolean>() {
			public Boolean invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getBoolean(1);
				return null;
			}
		});
	}

	public Integer selectInt(String sql) {
		return executeWithResultSet(sql, new Getter<Integer>() {
			public Integer invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getInt(1);
				return null;
			}
		});
	}

	public Integer selectInt(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Integer>() {
			public Integer invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getInt(1);
				return null;
			}
		});
	}

	public Long selectLong(String sql) {
		return executeWithResultSet(sql, new Getter<Long>() {
			public Long invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getLong(1);
				return null;
			}
		});
	}

	public Long selectLong(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Long>() {
			public Long invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getLong(1);
				return null;
			}
		});
	}

	public String selectString(String sql) {
		return executeWithResultSet(sql, new Getter<String>() {
			public String invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getString(1);
				return null;
			}
		});
	}

	public String selectString(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<String>() {
			public String invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getString(1);
				return null;
			}
		});
	}

	public Double selectDouble(String sql) {
		return executeWithResultSet(sql, new Getter<Double>() {
			public Double invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDouble(1);
				return null;
			}
		});
	}

	public Double selectDouble(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Double>() {
			public Double invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDouble(1);
				return null;
			}
		});
	}

	public Date selectDate(String sql) {
		return executeWithResultSet(sql, new Getter<Date>() {
			public Date invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDate(1);
				return null;
			}
		});
	}

	public Date selectDate(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Date>() {
			public Date invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDate(1);
				return null;
			}
		});
	}

	public Boolean update(String sql) {
		return execute(sql);
	}

	public Boolean update(String sql, Object[] params) {
		return execute(sql, params);
	}

	public Boolean update(String sql, List<Object[]> paramsList) {
		return execute(sql, paramsList);
	}

	public Boolean delete(String sql) {
		return execute(sql);
	}

	public Boolean delete(String sql, Object[] params) {
		return execute(sql, params);
	}

	public Boolean delete(String sql, List<Object[]> paramsList) {
		return execute(sql, paramsList);
	}

	public Object insert(String sql) {
		return executeWithGeneratedKey(sql, new Getter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next()) {
					return resultSet.getObject(1);
				}
				return null;
			}

		});
	}

	public Object insert(String sql, final Object[] params) {
		return executeWithGeneratedKey(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next()) {
					return resultSet.getObject(1);
				}
				return null;
			}

		});
	}

	public Object[] insert(String sql, final List<Object[]> paramsList) {
		return executeWithGeneratedKey(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (Object[] params : paramsList) {
					for (int i = 0; i < params.length; i++)
						preparedStatement.setObject(i + 1, params[i]);
					preparedStatement.addBatch();
				}
			}
		}, new Getter<Object[]>() {
			public Object[] invoke(ResultSet resultSet) throws Exception {
				Object[] objects = new Object[paramsList.size()];
				for (int i = 0; resultSet.next(); i++)
					objects[i] = resultSet.getObject(1);
				return objects;
			}

		});
	}

	public Boolean execute(String sql, final Object[] params) {
		return execute(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		});
	}

	public Boolean execute(String sql, final List<Object[]> paramsList) {
		return execute(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (Object[] params : paramsList) {
					for (int i = 0; i < params.length; i++)
						preparedStatement.setObject(i + 1, params[i]);
					preparedStatement.addBatch();
				}
			}
		});
	}

	public Boolean execute(String sql) {
		PreparedStatement preparedStatement = buildPreparedStatement(sql);
		try {
			preparedStatement.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return false;
	}

	public Boolean execute(String sql, Setter setter) {
		PreparedStatement preparedStatement = buildPreparedStatement(sql);
		try {
			setter.invoke(preparedStatement);
			preparedStatement.executeBatch();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return false;
	}

	public <T> T executeWithGeneratedKey(String sql, Getter<T> getter) {
		PreparedStatement preparedStatement = buildPreparedStatement(sql, true);
		try {
			preparedStatement.execute();
			return getter.invoke(preparedStatement.getGeneratedKeys());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return null;
	}

	public <T> T executeWithGeneratedKey(String sql, Setter setter, Getter<T> getter) {
		PreparedStatement preparedStatement = buildPreparedStatement(sql, true);
		try {
			setter.invoke(preparedStatement);
			preparedStatement.executeBatch();
			return getter.invoke(preparedStatement.getGeneratedKeys());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return null;
	}

	public <T> T executeWithResultSet(String sql, Getter<T> getter) {
		PreparedStatement preparedStatement = buildPreparedStatement(sql);
		try {
			preparedStatement.executeQuery();
			return getter.invoke(preparedStatement.getResultSet());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return null;
	}

	public <T> T executeWithResultSet(String sql, Setter setter, Getter<T> getter) {
		PreparedStatement preparedStatement = buildPreparedStatement(sql);
		try {
			setter.invoke(preparedStatement);
			preparedStatement.executeQuery();
			return getter.invoke(preparedStatement.getResultSet());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return null;
	}

	private PreparedStatement buildPreparedStatement(String sql) {
		return buildPreparedStatement(sql, false);
	}

	private PreparedStatement buildPreparedStatement(String sql, boolean autoGeneratedKeys) {
		try {
			if (connection == null || connection.isClosed()) {
				connection = dataSource.getConnection();
			}
			if (autoGeneratedKeys) {
				return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			} else {
				return connection.prepareStatement(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void closePreparedStatement(PreparedStatement preparedStatement) {
		try {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static interface Setter {
		public void invoke(PreparedStatement preparedStatement) throws Exception;
	}

	public static interface Getter<T> {
		public T invoke(ResultSet resultSet) throws Exception;
	}

	public static interface Iterator<T> {
		public Boolean next(T t);
	}

}