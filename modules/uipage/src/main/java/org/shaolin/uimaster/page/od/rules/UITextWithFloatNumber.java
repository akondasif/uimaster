package org.shaolin.uimaster.page.od.rules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.TextWidget;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.od.formats.FormatUtil;
import org.shaolin.uimaster.page.widgets.HTMLLabelType;
import org.shaolin.uimaster.page.widgets.HTMLTextWidgetType;

public class UITextWithFloatNumber implements IODMappingConverter {
	private HTMLTextWidgetType uiText;
	private String uiid;
	private double floatNumber;
	private String displayStringData;
	private boolean displayZero;
	private String stringData;
	private String localeConfig;
	private Map<String, Object> propValues;

	public static UITextWithFloatNumber createRule() {
		return new UITextWithFloatNumber();
	}
	
	public UITextWithFloatNumber() {
		this.displayZero = true;
	}

	public String getRuleName() {
		return this.getClass().getName();
	}

	public HTMLTextWidgetType getUIText() {
		return this.uiText;
	}

	public void setUIText(HTMLTextWidgetType UIText) {
		this.uiText = UIText;
	}

	private HTMLTextWidgetType getUIHTML() {
		return this.uiText;
	}

	public double getFloatNumber() {
		return this.floatNumber;
	}

	public void setFloatNumber(double FloatNumber) {
		this.floatNumber = FloatNumber;
	}

	public String getDisplayStringData() {
		return this.displayStringData;
	}

	public void setDisplayStringData(String DisplayStringData) {
		this.displayStringData = DisplayStringData;
	}

	public boolean getDisplayZero() {
		return this.displayZero;
	}

	public void setDisplayZero(boolean displayZero) {
		this.displayZero = displayZero;
	}

	public String getStringData() {
		return this.stringData;
	}

	public void setStringData(String StringData) {
		this.stringData = StringData;
	}

	public String getLocaleConfig() {
		return this.localeConfig;
	}

	public void setLocaleConfig(String LocaleConfig) {
		this.localeConfig = LocaleConfig;
	}

	public Map<String, Object> getPropValues() {
		return this.propValues;
	}

	public void setPropValues(Map<String, Object> PropValues) {
		this.propValues = PropValues;
	}

	public Map<String, Class<?>> getDataEntityClassInfo() {
		HashMap<String, Class<?>> dataClassInfo = new LinkedHashMap<String, Class<?>>();
		dataClassInfo.put("FloatNumber", Double.TYPE);
		dataClassInfo.put("DisplayStringData", String.class);
		dataClassInfo.put("displayZero", Boolean.TYPE);
		dataClassInfo.put("StringData", String.class);
		dataClassInfo.put("FloatNumberFormat", String.class);
		dataClassInfo.put("LocaleConfig", String.class);
		dataClassInfo.put("PropValues", Map.class);
		dataClassInfo.put("NumberObject", Object.class);

		return dataClassInfo;
	}

	public Map<String, Class<?>> getUIEntityClassInfo() {
		HashMap<String, Class<?>> uiClassInfo = new HashMap<String, Class<?>>();

		uiClassInfo.put(UI_WIDGET_TYPE, HTMLTextWidgetType.class);

		return uiClassInfo;
	}

	public static Map<String, String> getRequiredUIParameter(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put(UI_WIDGET_TYPE, param);

		return dataClassInfo;
	}
	
	public static Map<String, String> getRequiredDataParameters(String value) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put("FloatNumber", value);
		
		return dataClassInfo;
	}
	
	public void setInputData(Map<String, Object> paramValue)
			throws UIConvertException {
		try {
			if (paramValue.containsKey(UI_WIDGET_TYPE)) {
				this.uiText = ((HTMLTextWidgetType) paramValue.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey(UI_WIDGET_ID)) {
				this.uiid = (String) paramValue.get(UI_WIDGET_ID);
			}
			if (paramValue.containsKey("FloatNumber")) {
				try {
					this.floatNumber = ((Number) paramValue.get("FloatNumber"))
						.doubleValue();
				} catch (NullPointerException e) {
					this.floatNumber = 0.0f;
				}
			}
			if (paramValue.containsKey("PropValues")) {
				this.propValues = ((Map) paramValue.get("PropValues"));
			}
			if (paramValue.containsKey("StringData")) {
				this.stringData = ((String) paramValue.get("StringData"));
			}
			if (paramValue.containsKey("DisplayStringData")) {
				this.displayStringData = ((String) paramValue
						.get("DisplayStringData"));
			}
			if (paramValue.containsKey("LocaleConfig")) {
				this.localeConfig = ((String) paramValue
						.get("LocaleConfig"));
			}
			if (paramValue.containsKey("displayZero")) {
				this.displayZero = ((Boolean) paramValue.get("displayZero"))
						.booleanValue();
			}
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_070", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public Map<String, Object> getOutputData() throws UIConvertException {
		Map<String, Object> paramValue = new HashMap<String, Object>();
		try {
			paramValue.put(UI_WIDGET_TYPE, this.uiText);
			paramValue.put("FloatNumber", Double.valueOf(this.floatNumber));
			paramValue.put("PropValues", this.propValues);
			paramValue.put("StringData", this.stringData);
			paramValue.put("DisplayStringData", this.displayStringData);
			paramValue.put("LocaleConfig", this.localeConfig);
			paramValue.put("displayZero", Boolean.valueOf(this.displayZero));
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}
			throw new UIConvertException("EBOS_ODMAPPER_071", t,
					new Object[] { getUIHTML().getUIID() });
		}
		return paramValue;
	}

	public String[] getImplementInterfaceName() {
		return new String[0];
	}

	public void pushDataToWidget(HTMLSnapshotContext htmlContext) throws UIConvertException {
		try {
			if ((this.displayZero) || (this.floatNumber != 0.0f)) {
				this.stringData = FormatUtil.convertDataToUI(
						FormatUtil.FLOAT_NUMBER, new Double(this.floatNumber),
						this.localeConfig, this.propValues);
				this.uiText.setValue(this.stringData);
				if (this.uiText instanceof HTMLLabelType) {
					if (this.displayStringData != null) {
						((HTMLLabelType) this.uiText)
								.setDisplayValue(this.displayStringData);
					} else {
						((HTMLLabelType) this.uiText)
								.setDisplayValue(this.stringData);
					}
				}
			}
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_072", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public void pullDataFromWidget(HTMLSnapshotContext htmlContext) throws UIConvertException {
		try {
			TextWidget textComp = (TextWidget) AjaxActionHelper
					.getCachedAjaxWidget(this.uiid, htmlContext);
			String value = textComp.getValue();
			value = value != null ? value.trim() : "";
			if ("".equals(value)) {
				this.floatNumber = 0.0f;
			} else {
				Object numberObject = FormatUtil.convertUIToData(
						FormatUtil.FLOAT_NUMBER, value,
						this.localeConfig, this.propValues);

				this.floatNumber = ((Number)numberObject).doubleValue();
			}
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_073", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public void callAllMappings(boolean isDataToUI) throws UIConvertException {
	}
}