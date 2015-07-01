package dao.data;

public class TableModel {

	private String className;
	private String tableName;

	public TableModel(Class clazz, Table table) {
		className = clazz.getSimpleName();
		tableName = table.value();
		if ("".equals(tableName))
			tableName = className;
	}

	public String className() {
		return className;
	}

	public String tableName() {
		return tableName;
	}

}
