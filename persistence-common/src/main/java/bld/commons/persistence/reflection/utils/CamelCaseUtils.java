package bld.commons.persistence.reflection.utils;

import org.apache.commons.lang.WordUtils;

import bld.commons.persistence.reflection.type.UpperLowerType;

public class CamelCaseUtils {

	private static final String P_UPPER = "(?=\\p{Upper})";

	public static String reverseCamelCase(String javaField, UpperLowerType upperLowerType) {
		String dbField = "";
		String[] splitJavaFields = javaField.split(P_UPPER);
		for (String splitJavaField : splitJavaFields) {
			dbField += splitJavaField + " ";
		}
		dbField = dbField.trim();
		dbField = dbField.replace(" ", "_");
		if (upperLowerType == null)
			dbField = Character.toUpperCase(dbField.charAt(0)) + dbField.substring(1);
		else {
			if (UpperLowerType.UPPER.equals(upperLowerType))
				dbField = dbField.toUpperCase();
			else
				dbField = dbField.toLowerCase();
		}

		return dbField;
	}
	
	
	public static String camelCase(String tableName,boolean firstCharacterLowerCase) {
		String className = tableName.replace("_", " ");
		className = (WordUtils.capitalizeFully(className)).replace(" ", "");
		if(firstCharacterLowerCase)
			className = Character.toLowerCase(className.charAt(0)) + className.substring(1);
		return className;
	}

	public static String camelCase(String tableName) {
		return camelCase(tableName, false);
	}
	

}
