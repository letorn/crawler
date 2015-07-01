package dao.data;

import java.lang.reflect.Field;
import java.util.Map;

public class RamModel {

	private Class rlazz = Stack.class;
	private Field field;
	private Ram column;
	private Class tlazz;
	private String fieldName;
	private Field referenceField;
	private Map<Object, Object> ramMap;
	private String ramFieldName;

	public RamModel(Field field, Ram column, Class tlazz) {
		this.field = field;
		this.column = column;
		field.setAccessible(true);
		fieldName = field.getName();
		String[] strs = column.value().split(":", 2);
		if (strs.length == 2) {
			String referenceFieldName = strs[0];
			try {
				referenceField = tlazz.getDeclaredField(referenceFieldName);
				referenceField.setAccessible(true);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			String[] paths = strs[1].split("\\.", 2);
			if (paths.length == 2) {
				try {
					Field ramField = rlazz.getDeclaredField(paths[0]);
					ramField.setAccessible(true);
					ramMap = (Map<Object, Object>) ramField.get(rlazz);
					ramFieldName = paths[1];
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public String fieldName() {
		return fieldName;
	}

	public Object get(Object object) {
		try {
			return field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void set(Object object) {
		try {
			Object referenceFieldValue = referenceField.get(object);
			if (referenceFieldValue != null) {
				Object ram = ramMap.get(referenceFieldValue);
				if (ram != null) {
					Field ramField = ram.getClass().getDeclaredField(ramFieldName);
					ramField.setAccessible(true);
					field.set(object, ramField.get(ram));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
