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

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class C3P0Store implements ApplicationContextAware {

	public static DataSource dataSource;

	public static Map<String, Object> selectMetaMap(String sql) {
		return executeWithResultSet(sql, new Getter<Map<String, Object>>() {
			public Map<String, Object> invoke(ResultSet resultSet) throws Exception {
				String[] columnNames = columnNames(resultSet);
				if (resultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String columnName : columnNames)
						map.put(columnName, resultSet.getObject(columnName));
					return map;
				}
				return null;
			}
		});
	}

	public static Map<String, Object> selectMetaMap(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Map<String, Object>>() {
			public Map<String, Object> invoke(ResultSet resultSet) throws Exception {
				String[] columnNames = columnNames(resultSet);
				if (resultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String columnName : columnNames)
						map.put(columnName, resultSet.getObject(columnName));
					return map;
				}
				return null;
			}
		});
	}

	public static List<Map<String, Object>> selectMetaMapList(String sql) {
		return executeWithResultSet(sql, new Getter<List<Map<String, Object>>>() {
			public List<Map<String, Object>> invoke(ResultSet resultSet) throws Exception {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				String[] columnNames = columnNames(resultSet);
				while (resultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String columnName : columnNames)
						map.put(columnName, resultSet.getObject(columnName));
					list.add(map);
				}
				return list;
			}
		});
	}

	public static List<Map<String, Object>> selectMetaMapList(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<List<Map<String, Object>>>() {
			public List<Map<String, Object>> invoke(ResultSet resultSet) throws Exception {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				String[] columnNames = columnNames(resultSet);
				while (resultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String columnName : columnNames)
						map.put(columnName, resultSet.getObject(columnName));
					list.add(map);
				}
				return list;
			}
		});
	}

	public static void selectResultSet(String sql, final Iterator<ResultSet> iterator) {
		executeWithResultSet(sql, new Getter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				for (int i = 0; resultSet.next(); i++)
					if (!iterator.next(resultSet, i))
						break;
				return null;
			}
		});
	}

	public static void selectResultSet(String sql, final Object[] params, final Iterator<ResultSet> iterator) {
		executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				for (int i = 0; resultSet.next(); i++)
					if (!iterator.next(resultSet, i))
						break;
				return null;
			}
		});
	}

	public static Boolean selectBoolean(String sql) {
		return executeWithResultSet(sql, new Getter<Boolean>() {
			public Boolean invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getBoolean(1);
				return null;
			}
		});
	}

	public static Boolean selectBoolean(String sql, final Object[] params) {
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

	public static Integer selectInt(String sql) {
		return executeWithResultSet(sql, new Getter<Integer>() {
			public Integer invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getInt(1);
				return null;
			}
		});
	}

	public static Integer selectInt(String sql, final Object[] params) {
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

	public static Long selectLong(String sql) {
		return executeWithResultSet(sql, new Getter<Long>() {
			public Long invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getLong(1);
				return null;
			}
		});
	}

	public static Long selectLong(String sql, final Object[] params) {
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

	public static List<Long> selectLongList(String sql) {
		return executeWithResultSet(sql, new Getter<List<Long>>() {
			public List<Long> invoke(ResultSet resultSet) throws Exception {
				List<Long> list = new ArrayList<Long>();
				while (resultSet.next())
					list.add(resultSet.getLong(1));
				return list;
			}
		});
	}

	public static List<Long> selectLongList(String sql, final Object[] params) {
		return executeWithResultSet(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new Getter<List<Long>>() {
			public List<Long> invoke(ResultSet resultSet) throws Exception {
				List<Long> list = new ArrayList<Long>();
				while (resultSet.next())
					list.add(resultSet.getLong(1));
				return list;
			}
		});
	}

	public static String selectString(String sql) {
		return executeWithResultSet(sql, new Getter<String>() {
			public String invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getString(1);
				return null;
			}
		});
	}

	public static String selectString(String sql, final Object[] params) {
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

	public static Double selectDouble(String sql) {
		return executeWithResultSet(sql, new Getter<Double>() {
			public Double invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDouble(1);
				return null;
			}
		});
	}

	public static Double selectDouble(String sql, final Object[] params) {
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

	public static Date selectDate(String sql) {
		return executeWithResultSet(sql, new Getter<Date>() {
			public Date invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDate(1);
				return null;
			}
		});
	}

	public static Date selectDate(String sql, final Object[] params) {
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

	public static Boolean update(String sql) {
		return execute(sql);
	}

	public static Boolean update(String sql, Object[] params) {
		return execute(sql, params);
	}

	public static Boolean update(String sql, List<Object[]> paramsList) {
		return execute(sql, paramsList);
	}

	public static Boolean delete(String sql) {
		return execute(sql);
	}

	public static Boolean delete(String sql, Object[] params) {
		return execute(sql, params);
	}

	public static Boolean delete(String sql, List<Object[]> paramsList) {
		return execute(sql, paramsList);
	}

	public static Object insert(String sql) {
		return executeWithGeneratedKey(sql, new Getter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next()) {
					return resultSet.getObject(1);
				}
				return null;
			}

		});
	}

	public static Object insert(String sql, final Object[] params) {
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

	public static Object[] insert(String sql, final List<Object[]> paramsList) {
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

	public static Boolean execute(String sql, final Object[] params) {
		return execute(sql, new Setter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		});
	}

	public static Boolean execute(String sql, final List<Object[]> paramsList) {
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

	public static Boolean execute(String sql) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return false;
	}

	public static Boolean execute(String sql, Setter setter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			setter.invoke(preparedStatement);
			preparedStatement.executeBatch();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return false;
	}

	public static <T> T executeWithGeneratedKey(String sql, Getter<T> getter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.execute();
			return getter.invoke(preparedStatement.getGeneratedKeys());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return null;
	}

	public static <T> T executeWithGeneratedKey(String sql, Setter setter, Getter<T> getter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			setter.invoke(preparedStatement);
			preparedStatement.executeBatch();
			return getter.invoke(preparedStatement.getGeneratedKeys());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return null;
	}

	public static <T> T executeWithResultSet(String sql, Getter<T> getter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeQuery();
			return getter.invoke(preparedStatement.getResultSet());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return null;
	}

	public static <T> T executeWithResultSet(String sql, Setter setter, Getter<T> getter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			setter.invoke(preparedStatement);
			preparedStatement.executeQuery();
			return getter.invoke(preparedStatement.getResultSet());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return null;
	}

	public static String[] columnNames(ResultSet resultSet) {
		try {
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			String[] columnNames = new String[resultSetMetaData.getColumnCount()];
			for (int i = 0; i < columnNames.length; i++)
				columnNames[i] = resultSetMetaData.getColumnName(i + 1);
			return columnNames;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String[0];
	}

	public static Connection openConnection() {
		try {
			Connection connection = dataSource.getConnection();
			connection.prepareStatement("set names utf8mb4").executeQuery();
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void closeConnection(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
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
		public boolean next(T t, int i) throws Exception;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		dataSource = applicationContext.getBean(DataSource.class);
	}

}