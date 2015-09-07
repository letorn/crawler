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
import org.springframework.stereotype.Component;

@Component
public class C3P0Store implements ApplicationContextAware {

	public static DataSource dataSource;

	public boolean execute(String sql) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeConnection(connection);
		}
	}

	public boolean execute(String sql, PreparedSetter preparedSetter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedSetter.invoke(preparedStatement);
			preparedStatement.executeBatch();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeConnection(connection);
		}
	}

	public boolean execute(String sql, final Object[] params) {
		return execute(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		});
	}

	public boolean execute(String sql, final List<Object[]> paramsList) {
		return execute(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (Object[] params : paramsList) {
					for (int i = 0; i < params.length; i++)
						preparedStatement.setObject(i + 1, params[i]);
					preparedStatement.addBatch();
				}
			}
		});
	}

	public <T> T executeGenerate(String sql, ResultGetter<T> resultGetter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.execute();
			return resultGetter.invoke(preparedStatement.getGeneratedKeys());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConnection(connection);
		}
	}

	public <T> T executeGenerate(String sql, PreparedSetter preparedSetter, ResultGetter<T> resultGetter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			preparedSetter.invoke(preparedStatement);
			preparedStatement.executeBatch();
			return resultGetter.invoke(preparedStatement.getGeneratedKeys());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConnection(connection);
		}
	}

	public Object executeGenerate(String sql) {
		return executeGenerate(sql, new ResultGetter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next()) {
					return resultSet.getObject(1);
				}
				return null;
			}

		});
	}

	public Object executeGenerate(String sql, final Object[] params) {
		return executeGenerate(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next()) {
					return resultSet.getObject(1);
				}
				return null;
			}

		});
	}

	public Object[] executeGenerate(String sql, final List<Object[]> paramsList) {
		return executeGenerate(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (Object[] params : paramsList) {
					for (int i = 0; i < params.length; i++)
						preparedStatement.setObject(i + 1, params[i]);
					preparedStatement.addBatch();
				}
			}
		}, new ResultGetter<Object[]>() {
			public Object[] invoke(ResultSet resultSet) throws Exception {
				Object[] objects = new Object[paramsList.size()];
				for (int i = 0; resultSet.next(); i++)
					objects[i] = resultSet.getObject(1);
				return objects;
			}

		});
	}

	public <T> T executeQuery(String sql, ResultGetter<T> resultGetter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.execute();
			return resultGetter.invoke(preparedStatement.getResultSet());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConnection(connection);
		}
	}

	public <T> T executeQuery(String sql, PreparedSetter preparedSetter, ResultGetter<T> resultGetter) {
		Connection connection = openConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedSetter.invoke(preparedStatement);
			preparedStatement.executeBatch();
			return resultGetter.invoke(preparedStatement.getResultSet());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConnection(connection);
		}
	}

	public Boolean selectBoolean(String sql) {
		return executeQuery(sql, new ResultGetter<Boolean>() {
			public Boolean invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getBoolean(1);
				return null;
			}
		});
	}

	public Boolean selectBoolean(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<Boolean>() {
			public Boolean invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getBoolean(1);
				return null;
			}
		});
	}

	public List<Boolean> selectBooleanList(String sql) {
		return executeQuery(sql, new ResultGetter<List<Boolean>>() {
			public List<Boolean> invoke(ResultSet resultSet) throws Exception {
				List<Boolean> list = new ArrayList<Boolean>();
				while (resultSet.next())
					list.add(resultSet.getBoolean(1));
				return list;
			}
		});
	}

	public List<Boolean> selectBooleanList(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<List<Boolean>>() {
			public List<Boolean> invoke(ResultSet resultSet) throws Exception {
				List<Boolean> list = new ArrayList<Boolean>();
				while (resultSet.next())
					list.add(resultSet.getBoolean(1));
				return list;
			}
		});
	}

	public Integer selectInt(String sql) {
		return executeQuery(sql, new ResultGetter<Integer>() {
			public Integer invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getInt(1);
				return null;
			}
		});
	}

	public Integer selectInt(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<Integer>() {
			public Integer invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getInt(1);
				return null;
			}
		});
	}

	public List<Integer> selectIntList(String sql) {
		return executeQuery(sql, new ResultGetter<List<Integer>>() {
			public List<Integer> invoke(ResultSet resultSet) throws Exception {
				List<Integer> list = new ArrayList<Integer>();
				while (resultSet.next())
					list.add(resultSet.getInt(1));
				return list;
			}
		});
	}

	public List<Integer> selectIntList(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<List<Integer>>() {
			public List<Integer> invoke(ResultSet resultSet) throws Exception {
				List<Integer> list = new ArrayList<Integer>();
				while (resultSet.next())
					list.add(resultSet.getInt(1));
				return list;
			}
		});
	}

	public Long selectLong(String sql) {
		return executeQuery(sql, new ResultGetter<Long>() {
			public Long invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getLong(1);
				return null;
			}
		});
	}

	public Long selectLong(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<Long>() {
			public Long invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getLong(1);
				return null;
			}
		});
	}

	public List<Long> selectLongList(String sql) {
		return executeQuery(sql, new ResultGetter<List<Long>>() {
			public List<Long> invoke(ResultSet resultSet) throws Exception {
				List<Long> list = new ArrayList<Long>();
				while (resultSet.next())
					list.add(resultSet.getLong(1));
				return list;
			}
		});
	}

	public List<Long> selectLongList(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<List<Long>>() {
			public List<Long> invoke(ResultSet resultSet) throws Exception {
				List<Long> list = new ArrayList<Long>();
				while (resultSet.next())
					list.add(resultSet.getLong(1));
				return list;
			}
		});
	}

	public String selectString(String sql) {
		return executeQuery(sql, new ResultGetter<String>() {
			public String invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getString(1);
				return null;
			}
		});
	}

	public String selectString(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<String>() {
			public String invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getString(1);
				return null;
			}
		});
	}

	public List<String> selectStringList(String sql) {
		return executeQuery(sql, new ResultGetter<List<String>>() {
			public List<String> invoke(ResultSet resultSet) throws Exception {
				List<String> list = new ArrayList<String>();
				while (resultSet.next())
					list.add(resultSet.getString(1));
				return list;
			}
		});
	}

	public List<String> selectStringList(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<List<String>>() {
			public List<String> invoke(ResultSet resultSet) throws Exception {
				List<String> list = new ArrayList<String>();
				while (resultSet.next())
					list.add(resultSet.getString(1));
				return list;
			}
		});
	}

	public Double selectDouble(String sql) {
		return executeQuery(sql, new ResultGetter<Double>() {
			public Double invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDouble(1);
				return null;
			}
		});
	}

	public Double selectDouble(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<Double>() {
			public Double invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDouble(1);
				return null;
			}
		});
	}

	public List<Double> selectDoubleList(String sql) {
		return executeQuery(sql, new ResultGetter<List<Double>>() {
			public List<Double> invoke(ResultSet resultSet) throws Exception {
				List<Double> list = new ArrayList<Double>();
				while (resultSet.next())
					list.add(resultSet.getDouble(1));
				return list;
			}
		});
	}

	public List<Double> selectDoubleList(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<List<Double>>() {
			public List<Double> invoke(ResultSet resultSet) throws Exception {
				List<Double> list = new ArrayList<Double>();
				while (resultSet.next())
					list.add(resultSet.getDouble(1));
				return list;
			}
		});
	}

	public Date selectDate(String sql) {
		return executeQuery(sql, new ResultGetter<Date>() {
			public Date invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDate(1);
				return null;
			}
		});
	}

	public Date selectDate(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<Date>() {
			public Date invoke(ResultSet resultSet) throws Exception {
				if (resultSet.next())
					return resultSet.getDate(1);
				return null;
			}
		});
	}

	public List<Date> selectDateList(String sql) {
		return executeQuery(sql, new ResultGetter<List<Date>>() {
			public List<Date> invoke(ResultSet resultSet) throws Exception {
				List<Date> list = new ArrayList<Date>();
				while (resultSet.next())
					list.add(resultSet.getDate(1));
				return list;
			}
		});
	}

	public List<Date> selectDateList(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<List<Date>>() {
			public List<Date> invoke(ResultSet resultSet) throws Exception {
				List<Date> list = new ArrayList<Date>();
				while (resultSet.next())
					list.add(resultSet.getDate(1));
				return list;
			}
		});
	}

	public Map<String, Object> selectMetaMap(String sql) {
		return executeQuery(sql, new ResultGetter<Map<String, Object>>() {
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

	public Map<String, Object> selectMetaMap(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<Map<String, Object>>() {
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

	public List<Map<String, Object>> selectMetaMapList(String sql) {
		return executeQuery(sql, new ResultGetter<List<Map<String, Object>>>() {
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

	public List<Map<String, Object>> selectMetaMapList(String sql, final Object[] params) {
		return executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<List<Map<String, Object>>>() {
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

	public void selectResultSet(String sql, final Iterator<ResultSet> iterator) {
		executeQuery(sql, new ResultGetter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				for (int i = 0; resultSet.next(); i++)
					if (!iterator.next(resultSet, i))
						break;
				return null;
			}
		});
	}

	public void selectResultSet(String sql, final Object[] params, final Iterator<ResultSet> iterator) {
		executeQuery(sql, new PreparedSetter() {
			public void invoke(PreparedStatement preparedStatement) throws Exception {
				for (int i = 0; i < params.length; i++)
					preparedStatement.setObject(i + 1, params[i]);
				preparedStatement.addBatch();
			}
		}, new ResultGetter<Object>() {
			public Object invoke(ResultSet resultSet) throws Exception {
				for (int i = 0; resultSet.next(); i++)
					if (!iterator.next(resultSet, i))
						break;
				return null;
			}
		});
	}

	public String[] columnNames(ResultSet resultSet) {
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

	public Connection openConnection() {
		try {
			Connection connection = dataSource.getConnection();
			// connection.prepareStatement("set names utf8mb4").executeQuery();
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void closeConnection(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		dataSource = applicationContext.getBean(DataSource.class);
	}

	public static interface PreparedSetter {
		public void invoke(PreparedStatement preparedStatement) throws Exception;
	}

	public static interface ResultGetter<T> {
		public T invoke(ResultSet resultSet) throws Exception;
	}

	public static interface Iterator<T> {
		public boolean next(T t, int index) throws Exception;
	}

}