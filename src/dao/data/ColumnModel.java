package dao.data;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public class ColumnModel {

	private Field field;
	private Column column;
	private String fieldName;
	private String columnName;

	public ColumnModel() {
	}

	public ColumnModel(Field field, Column column) {
		this.field = field;
		this.column = column;
		field.setAccessible(true);
		fieldName = field.getName();
		columnName = column.value();
		if ("".equals(columnName))
			columnName = fieldName;
	}

	public String fieldName() {
		return fieldName;
	}

	public String columnName() {
		return columnName;
	}

	public Object get(Object object) {
		try {
			return field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void set(Object object, ResultSet resultSet) {
		try {
			set(object, resultSet.getObject(columnName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void set(Object object, Object value) {
		try {
			if (value != null && value instanceof Long && field.getType() == Integer.class)
				value = ((Long) value).intValue();
			field.set(object, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
