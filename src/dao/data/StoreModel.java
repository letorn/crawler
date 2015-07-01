package dao.data;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.ResolvableType;

public class StoreModel<T> {

	private Class tlazz;

	private TableModel tableModel;
	private IdModel idModel;
	private List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
	private List<MappingModel> mappingModels = new ArrayList<MappingModel>();
	private List<RamModel> ramModels = new ArrayList<RamModel>();
	private Map<String, ColumnModel> mappings = new HashMap<String, ColumnModel>();

	private String selectAllSQL;
	private String selectByIdSQL;
	private String selectByIdsSQL;
	private String insertSQL;
	private String updateSQL;
	private String deleteSQL;

	public StoreModel(Class clazz, Class child) {
		tlazz = ResolvableType.forClass(child).as(clazz).resolveGeneric(0);

		if (tlazz.isAnnotationPresent(Table.class))
			tableModel = new TableModel(tlazz, (Table) tlazz.getAnnotation(Table.class));

		for (Field field : tlazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Id.class))
				idModel = new IdModel(field, field.getAnnotation(Id.class));
			else if (field.isAnnotationPresent(Column.class))
				columnModels.add(new ColumnModel(field, field.getAnnotation(Column.class)));
			else if (field.isAnnotationPresent(Mapping.class))
				mappingModels.add(new MappingModel(field, field.getAnnotation(Mapping.class)));
			else if (field.isAnnotationPresent(Ram.class))
				ramModels.add(new RamModel(field, field.getAnnotation(Ram.class), tlazz));
		}

		if (idModel != null)
			mappings.put(idModel.columnName(), idModel);
		for (ColumnModel columnModel : columnModels)
			mappings.put(columnModel.columnName(), columnModel);
		for (MappingModel mappingModel : mappingModels)
			mappings.put(mappingModel.columnName(), mappingModel);

		selectAllSQL = buildSelectAllSQL();
		selectByIdSQL = buildSelectByIdSQL();
		// selectByIdsSQL = buildSelectByIdsSQL();
		insertSQL = buildInsertSQL();
		updateSQL = buildUpdateSQL();
		deleteSQL = buildDeleteSQL();
	}

	public Object[] catchInsertValues(T t) {
		List<Object> values = new ArrayList<Object>();
		if (idModel != null)
			values.add(idModel.get(t));
		for (ColumnModel columnModel : columnModels)
			values.add(columnModel.get(t));
		return values.toArray();
	}

	public Object[] catchUpdateValues(T t) {
		List<Object> values = new ArrayList<Object>();
		for (ColumnModel columnModel : columnModels)
			values.add(columnModel.get(t));
		if (idModel != null)
			values.add(idModel.get(t));
		return values.toArray();
	}

	public T newTlazz() {
		try {
			return (T) tlazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public T newTlazz(ResultSet resultSet) {
		T t = newTlazz();
		if (idModel != null)
			idModel.set(t, resultSet);
		for (ColumnModel columnModel : columnModels)
			columnModel.set(t, resultSet);
		for (MappingModel mappingModel : mappingModels)
			mappingModel.set(t, resultSet);
		for (RamModel ramModel : ramModels)
			ramModel.set(t);
		return t;
	}

	public T newTlazz(ResultSet resultSet, String[] columnNames) {
		T t = newTlazz();
		try {
			for (String columnName : columnNames)
				mappings.get(columnName).set(t, resultSet.getObject(columnName));
			for (RamModel ramModel : ramModels)
				ramModel.set(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public Class tlazz() {
		return tlazz;
	}

	public TableModel tableModel() {
		return tableModel;
	}

	public IdModel idModel() {
		return idModel;
	}

	public List<ColumnModel> columnModels() {
		return columnModels;
	}

	public List<MappingModel> mappingModels() {
		return mappingModels;
	}

	public List<RamModel> ramModels() {
		return ramModels;
	}

	public String selectAllSQL() {
		return selectAllSQL;
	}

	public String selectByIdSQL() {
		return selectByIdSQL;
	}

	public String selectByIdsSQL(Object[] ids) {
		return buildSelectByIdsSQL(ids);
	}

	public String insertSQL() {
		return insertSQL;
	}

	public String updateSQL() {
		return updateSQL;
	}

	public String deleteSQL() {
		return deleteSQL;
	}

	private String buildSelectAllSQL() {
		if (tableModel != null) {
			return String.format("select * from %s", tableModel.tableName());
		}
		return null;
	}

	private String buildSelectByIdSQL() {
		if (tableModel != null && idModel != null) {
			return String.format("select * from %s where %s=%s", tableModel.tableName(), idModel.columnName(), "?");
		}
		return null;
	}

	private String buildSelectByIdsSQL(Object[] ids) {
		if (tableModel != null && idModel != null) {
			return String.format("select * from %s where %s in(%s)", tableModel.tableName(), idModel.columnName(), StringUtils.join(ids, ","));
		}
		return null;
	}

	private String buildInsertSQL() {
		if (tableModel != null) {
			List<String> columns = new ArrayList<String>();
			List<String> values = new ArrayList<String>();
			if (idModel != null) {
				columns.add(idModel.columnName());
				values.add("?");
			}
			for (ColumnModel columnModel : columnModels) {
				columns.add(columnModel.columnName());
				values.add("?");
			}
			return String.format("insert into %s(%s) values(%s)", tableModel.tableName(), StringUtils.join(columns, ","), StringUtils.join(values, ","));
		}
		return null;
	}

	private String buildUpdateSQL() {
		if (tableModel != null && idModel != null) {
			List<String> sets = new ArrayList<String>();
			for (ColumnModel columnModel : columnModels) {
				sets.add(String.format("%s=%s", columnModel.columnName(), "?"));
			}
			return String.format("update %s set %s where %s=%s", tableModel.tableName(), StringUtils.join(sets, ","), idModel.columnName(), "?");
		}
		return null;
	}

	private String buildDeleteSQL() {
		if (tableModel != null && idModel != null) {
			return String.format("delete from %s where %s=%s", tableModel.tableName(), idModel.columnName(), "?");
		}
		return null;
	}

}
