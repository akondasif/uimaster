/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.bmdp.runtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.json.JSONArray;
import org.shaolin.bmdp.json.JSONException;
import org.shaolin.bmdp.json.JSONObject;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.runtime.be.IBusinessEntity;
import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.utils.StringUtil;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.util.ExpressionUtil;

public final class VariableUtil {
	
	public static Object createVariableObject(VariableType variable)
			throws EntityNotFoundException {
		String entityName = variable.getType().getEntityName();
		return createObject(entityName, variable, variable.getCategory());
	}

	private static Object createObject(String entityName, VariableType variable, VariableCategoryType categoryType)
			throws EntityNotFoundException {
		if (VariableCategoryType.BUSINESS_ENTITY == categoryType) {
			if (variable.getDefault() != null) {
				return getVariableDefaultValue(variable);
			}
			return BEUtil.createBEObject(entityName);
		} else if (VariableCategoryType.CONSTANT_ENTITY == categoryType) {
			if (variable.getDefault() != null) {
				return getVariableDefaultValue(variable);
			}
			return CEUtil.getConstantEntity(entityName);
		} else if (VariableCategoryType.JAVA_CLASS == categoryType
				|| VariableCategoryType.JAVA_PRIMITIVE == categoryType) {
			try {
				if (variable.getDefault() != null) {
					return getVariableDefaultValue(variable);
				}
				Class<?> objectClass = Class.forName(entityName);
				if (objectClass.isInterface()) {
					return null;
				}
				if (objectClass == boolean.class || objectClass == Boolean.class) {
					return Boolean.FALSE;
				} else if (objectClass == byte.class || objectClass == Byte.class) {
					return new Byte((byte) 0);
				} else if (objectClass == short.class || objectClass == Short.class) {
					return new Short((short) 0);
				} else if (objectClass == int.class || objectClass == Integer.class) {
					return new Integer(0);
				} else if (objectClass == long.class || objectClass == Long.class) {
					return new Long(0);
				} else if (objectClass == float.class || objectClass == Float.class) {
					return new Float(0);
				} else if (objectClass == double.class || objectClass == Double.class) {
					return new Double(0);
				} else if (objectClass == char.class || objectClass == Character.class) {
					return new Character((char) 0);
				} else {
					try {
						return objectClass.newInstance();
					} catch (InstantiationException e) {
						// no default constructor defined.
						return null;
					}
				}
			} catch (Exception ex) {
				throw new EntityNotFoundException(
						ExceptionConstants.UIMASTER_COMMON_003, ex,
						new Object[] { entityName });
			}
		} else if (VariableCategoryType.JOIN_TABLE == categoryType) {
			return null;
		} else {
			throw new EntityNotFoundException(
					ExceptionConstants.UIMASTER_COMMON_008, new Object[] { categoryType.value(),
							entityName });
		}
	}

	public static String getVariableBeanName(String varName) {
		return StringUtil.getBeanName(varName);
	}

	public static String getVariableClassName(Class clazz) {

		StringBuffer classNameBuffer = new StringBuffer();

		while (clazz.isArray()) {
			classNameBuffer.append("[]");
			clazz = clazz.getComponentType();
		}

		return clazz.getName() + classNameBuffer.toString();
	}

	public static String getVariableClassName(VariableType variable)
			throws EntityNotFoundException {
		VariableCategoryType categoryType = variable.getCategory();
		String variableType = variable.getType().getEntityName();

		String variableClassName = null;

		if (categoryType == null) {
			categoryType = VariableCategoryType.JAVA_CLASS;
		}
		if (categoryType == VariableCategoryType.BUSINESS_ENTITY) {
//			String packageName = variableType.substring(0, variableType.length() - variableType.lastIndexOf('.') - 1);
//			String name = variableType.substring(variableType.length() - variableType.lastIndexOf('.'));
			variableClassName = variableType + "Impl";
		} else if (categoryType == VariableCategoryType.CONSTANT_ENTITY) {
			variableClassName = variableType;
		} else if (categoryType == VariableCategoryType.JAVA_CLASS
				|| VariableCategoryType.JAVA_PRIMITIVE == categoryType) {
			variableClassName = variableType;
		} 

		return variableClassName;
	}

	public static Class<?> getVariableClass(VariableType variable)
			throws EntityNotFoundException {
		String varClassName = getVariableClassName(variable);
		try {
			return ExpressionUtil.findClass(varClassName);
		} catch (ParsingException ex) {
			throw new EntityNotFoundException(
					ExceptionConstants.UIMASTER_COMMON_007, ex,
					new Object[] { varClassName });
		}
	}

	public static String getVariableDeclareString(VariableType variableData)
			throws EntityNotFoundException {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getVariableClassName(variableData));
		buffer.append(" ");
		buffer.append(variableData.getName());
		if (variableData.getDefault() != null) {
			buffer.append(" = ");
			buffer.append(variableData.getDefault().getExpressionString());
		}

		return buffer.toString();
	}

	// Variable <--> Value
	private static Object getVariableInitialValue(VariableType variable) {
		return getVariableInitialValue(getVariableClass(variable));
	}

	private static Object getVariableInitialValue(Class objectClass) {
		if (objectClass == boolean.class) {
			return Boolean.FALSE;
		} else if (objectClass == byte.class) {
			return new Byte((byte) 0);
		} else if (objectClass == short.class) {
			return new Short((short) 0);
		} else if (objectClass == int.class) {
			return new Integer(0);
		} else if (objectClass == long.class) {
			return new Long(0);
		} else if (objectClass == float.class) {
			return new Float(0);
		} else if (objectClass == double.class) {
			return new Double(0);
		} else if (objectClass == char.class) {
			return new Character((char) 0);
		} else {
			return null;
		}
	}

	public static Map createVariableClassMap(List<VariableType> vars) {
		Map varClass = new HashMap();
		for (VariableType var: vars) {
			varClass.put(var.getName(), getVariableClass(var));
		}
		return varClass;
	}

	public static Map createVariableInitialValueMap(List<VariableType> vars) {
		Map varValue = new HashMap();
		for (VariableType var: vars) {
			varValue.put(var.getName(), getVariableInitialValue(var));
		}
		return varValue;
	}

	public static Map createVariableNullValueMap(List<VariableType> vars) {
		Map varValue = new HashMap();
		for (VariableType var: vars) {
			varValue.put(var.getName(), null);
		}
		return varValue;
	}

	// Variable <--> ParsingContext && Variable <--> EvaluationContext
	public static ParsingContext createParsingContext(List<VariableType> vars) {
		return new DefaultParsingContext(createVariableClassMap(vars));
	}

	public static EvaluationContext createEvaluationContext(List<VariableType> vars) {
		return createEvaluationContext(vars, true);
	}

	public static EvaluationContext createEvaluationContext(
			List<VariableType> vars, boolean needDefaultValue) {
		Map varValue;
		if (needDefaultValue) {
			varValue = createVariableDefaultValueMap(vars);
		} else {
			varValue = createVariableNullValueMap(vars);
		}

		return new DefaultEvaluationContext(varValue);
	}

	public static Map createVariableDefaultValueMap(List<VariableType> vars) {
		Map varValue = new HashMap();
		for (VariableType var: vars) {
			Object defaultValue = createVariableObject(var);
			varValue.put(var.getName(), defaultValue);
		}
		return varValue;
	}

	private static Object getVariableDefaultValue(VariableType variable) {
		Class c = getVariableClass(variable);
		Object value;

		if (variable.getDefault() != null) {
			String expression = variable.getDefault().getExpressionString();
			CompilationUnit unit = null;
			try {
				unit = StatementParser.parse(expression);
				value = StatementEvaluator.evaluate(unit);
			} catch (Exception ex) {
				value = null;
			}
		} else {
			value = getVariableInitialValue(c);
		}

		return value;
	}
	
	public static HashMap<String, Object> convertJsonToVar(JSONObject json) throws JSONException {
    	HashMap<String, Object> inputParams = new HashMap<String, Object>(json.length());
		Iterator<String> keys = json.keys();
		while (keys.hasNext()) {
			String k = keys.next();
			if (json.has(k+"_t")) {
				String type = json.getString(k+"_t");
				if ("be".equals(type)) {
					inputParams.put(k, json.getBEntity(k));
				} else if ("ce".equals(type)) {
					inputParams.put(k, json.getCEntity(k));
				} else if ("map".equals(type)) {
					inputParams.put(k, json.getJSONObject(k));
				} else if ("list".equals(type)) {
					inputParams.put(k, json.getJSONArray(k));
				} else {
					inputParams.put(k, json.get(k));
				}
			} else {
				inputParams.put(k, json.get(k));
			}
		}
		return inputParams;
    }
    
    public static JSONObject convertVarToJson(HashMap<String, Object> inputParams) throws JSONException {
    	JSONObject json = new JSONObject();
		for (Entry<String, Object> var : inputParams.entrySet()) {
			if (var.getValue() instanceof IBusinessEntity) {
				json.put(var.getKey(), (IBusinessEntity)var.getValue());
				json.put(var.getKey()+"_t", "be");
			} else if (var.getValue() instanceof IConstantEntity) {
				json.put(var.getKey(), (IConstantEntity)var.getValue());
				json.put(var.getKey()+"_t", "ce");
			} else if (var.getValue() instanceof Map) {
				json.put(var.getKey(), (Map)var.getValue());
				json.put(var.getKey()+"_t", "map");
			} else if (var.getValue() instanceof List) {
				json.put(var.getKey(), new JSONArray((List)var.getValue()));
				json.put(var.getKey()+"_t", "list");
			} else {
				json.put(var.getKey(), var.getValue());
			}
		}
		json.remove("UIEntity");
		return json;
    }
}
