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
package org.shaolin.uimaster.page.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.runtime.spi.IConstantService;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.CheckBoxGroup;
import org.shaolin.uimaster.page.ajax.ComboBox;
import org.shaolin.uimaster.page.ajax.RadioButtonGroup;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;

/**
 * The dynamic ui item is going to be generated as the described HTML widget.
 * 
 * @author wushaol
 *
 */
public class HTMLDynamicUIItem {

	/**
	 * help is not available
	 */
	protected java.lang.String uiEntityName;

	/**
	 * help is not available
	 */
	protected java.lang.String uipanel;

	/**
	 * help is not available
	 */
	protected java.lang.String filter;

	/**
	 * help is not available
	 */
	protected java.lang.String labelName;

	/**
	 * help is not available
	 */
	protected java.lang.String ceName;

	protected ExpressionType uiToData;
	
	protected ExpressionType dataToUi;
	
	
	/**
	 * help is not available
	 */
	protected int ceSelectMode = 0;

	public static final int LIST = 0;
	public static final int RADIOBUTTONGROUP = 1;
	public static final int CHECKBOXGROUP = 2;

	public java.lang.String getLabelName() {
		return labelName;
	}

	public void setLabelName(java.lang.String labelName) {
		this.labelName = labelName;
	}

	public java.lang.String getCeName() {
		return ceName;
	}

	public void setCeName(java.lang.String ceName) {
		this.ceName = ceName;
	}

	public int getCeSelectMode() {
		return ceSelectMode;
	}

	public void setCeSelectMode(int ceSelectMode) {
		this.ceSelectMode = ceSelectMode;
	}

	public java.lang.String getUiEntityName() {
		return uiEntityName;
	}

	public void setUiEntityName(java.lang.String uiEntityName) {
		this.uiEntityName = uiEntityName;
	}

	public java.lang.String getUipanel() {
		return uipanel;
	}

	public void setUipanel(java.lang.String uipanel) {
		this.uipanel = uipanel;
	}

	public java.lang.String getFilter() {
		return filter;
	}

	public void setFilter(java.lang.String filter) {
		this.filter = filter;
	}
	
	public ExpressionType getUiToData() {
		return uiToData;
	}

	public void setUiToData(ExpressionType uiToData) {
		this.uiToData = uiToData;
	}

	public ExpressionType getDataToUi() {
		return dataToUi;
	}

	public void setDataToUi(ExpressionType dataToUi) {
		this.dataToUi = dataToUi;
	}
	
	public void generate(String jsonValue, String uiid, HTMLLayoutType layout, 
			VariableEvaluator ee, HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) throws Exception {
		List<IConstantEntity> value;
		IConstantEntity constant ;
		if (this.getCeName().indexOf(",") != -1) { //name,0 support.
			constant = AppContext.get().getConstantService().getChildren(CEUtil.toCEValue(this.getCeName()));
			value = HTMLDynamicUIItem.toCElist(constant.getEntityName(), jsonValue);
			this.setCeName(constant.getEntityName());
		} else {
			constant = AppContext.get().getConstantService().getConstantEntity(this.getCeName());
			value = HTMLDynamicUIItem.toCElist(this.getCeName(), jsonValue);
		}
		Map<Integer, String> avps = constant.getAllConstants(false);
		String filterUIID = uiid.replace("-", "").replace("_", "");
		StringBuilder jssb = new StringBuilder();
//		if (context.getHTMLPrefix().length() > 0) {
//			String realid = "defaultname." + context.getHTMLPrefix().substring(0, context.getHTMLPrefix().length() - 1);
//			jssb.append("if (!").append(realid).append(") {").append(realid).append(" = new Object();} \n");
//		}
		if (this.getCeSelectMode() == HTMLDynamicUIItem.LIST) {
			HTMLComboBoxType list = new HTMLComboBoxType(context, filterUIID); 
			list.setPrefix(context.getHTMLPrefix());
			list.setHTMLLayout(layout);
			list.setFrameInfo(context.getFrameInfo());
			list.addAttribute("UIStyle", "uimaster_rightform_widget");
			
			list.setValue((value == null || value.size() == 0)? "":value.get(0).getIntValue()+"");
			list.setOptionValues(new ArrayList(avps.keySet()));
			list.setOptionDisplayValues(new ArrayList(avps.values()));
			
			Widget newWidget = list.createAjaxWidget(ee);
			if (newWidget != null) {
	        	context.addAjaxWidget(newWidget.getId(), newWidget);
	        	newWidget.removeAttribute("value");//remove the default selected.
	        }
			
			list.generateBeginHTML(context, ownerEntity, depth);
			list.generateEndHTML(context, ownerEntity, depth);
			
			String jsvar = "defaultname." + newWidget.getId();
			jssb.append(jsvar).append(" = new UIMaster.ui.combobox({ui: elementList[\"");
			jssb.append(newWidget.getId()).append("\"]});\n");
			jssb.append(jsvar).append(".init();\n");
			
		} else if (this.getCeSelectMode() == HTMLDynamicUIItem.RADIOBUTTONGROUP) {
			HTMLRadioButtonGroupType list = new HTMLRadioButtonGroupType(context, filterUIID); 
			list.setPrefix(context.getHTMLPrefix());
			list.setHTMLLayout(layout);
			list.setFrameInfo(context.getFrameInfo());
			list.addAttribute("horizontalLayout", "true");
			
			list.setValue((value == null || value.size() == 0)? "":value.get(0).getIntValue()+"");
			list.setOptionValues(new ArrayList(avps.keySet()));
			list.setOptionDisplayValues(new ArrayList(avps.values()));
			
			Widget newWidget = list.createAjaxWidget(ee);
			if (newWidget != null) {
	        	context.addAjaxWidget(newWidget.getId(), newWidget);
	        	newWidget.removeAttribute("value");//remove the default selected.
	        }
			
			list.generateBeginHTML(context, ownerEntity, depth);
			list.generateEndHTML(context, ownerEntity, depth);
			
			String jsvar = "defaultname." + newWidget.getId();
			jssb.append(jsvar).append(" = new UIMaster.ui.radiobuttongroup({ui: elementList[\"");
			jssb.append(newWidget.getId()).append("\"]});\n");
			jssb.append(jsvar).append(".init();\n");
			
		} else if (this.getCeSelectMode() == HTMLDynamicUIItem.CHECKBOXGROUP) {
			HTMLCheckBoxGroupType list = new HTMLCheckBoxGroupType(context, filterUIID); 
			list.setPrefix(context.getHTMLPrefix());
			list.setHTMLLayout(layout);
			list.setFrameInfo(context.getFrameInfo());
			list.addAttribute("horizontalLayout", "true");
			
			ArrayList<String> temp = new ArrayList<String>(value.size());
			for (IConstantEntity v : value) {
				temp.add(v.getIntValue() + "");
			}
			list.setValue(temp);
			list.setOptionValues(new ArrayList(avps.keySet()));
			list.setOptionDisplayValues(new ArrayList(avps.values()));
			
			Widget newWidget = list.createAjaxWidget(ee);
			if (newWidget != null) {
	        	context.addAjaxWidget(newWidget.getId(), newWidget);
	        }
			
			list.generateBeginHTML(context, ownerEntity, depth);
			list.generateEndHTML(context, ownerEntity, depth);
			
			String jsvar = "defaultname." + newWidget.getId();
			jssb.append(jsvar).append(" = new UIMaster.ui.checkboxgroup({ui: elementList[\"");
			jssb.append(newWidget.getId()).append("\"]});\n");
			jssb.append(jsvar).append(".init();\n");
			
		} 
		
		context.generateHTML("<script type=\"text/javascript\">\n");
		context.generateHTML("UIMaster.pageInitFunctions.push(function(){\n");
		context.generateHTML(jssb.toString());
		context.generateHTML("});\n");
		context.generateHTML("</script>");
	}
	
	public String retriveData(String uiid) {
		String jsvar = uiid;
		if (this.getCeSelectMode() == HTMLDynamicUIItem.LIST) {
			ComboBox box = AjaxActionHelper.getAjaxContext().getComboBox(jsvar);
			String v = box.getValue();
			if (v.length() == 0 || "null".equals(v)) {
				return "";
			}
			return "{\"name\":\""+this.getCeName()+"\",\"value\":\""+v+"\"}"; 
		} else if (this.getCeSelectMode() == HTMLDynamicUIItem.RADIOBUTTONGROUP) {
			RadioButtonGroup group = AjaxActionHelper.getAjaxContext().getRadioBtnGroup(jsvar);
			String v = group.getValue();
			if (v.length() == 0 || "null".equals(v)) {
				return "";
			}
			return "{\"name\":\""+this.getCeName()+"\",\"value\":\""+v+"\"}"; 
		} else if (this.getCeSelectMode() == HTMLDynamicUIItem.CHECKBOXGROUP) {
			CheckBoxGroup group = AjaxActionHelper.getAjaxContext().getCheckBoxGroup(jsvar);
			List<String> values = group.getValues();
			StringBuilder sb = new StringBuilder();
			if (values != null && values.size() > 0) {
				for (String v : values) {
					sb.append(v).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			if (sb.length() == 0) {
				return "";
			}
			return "{\"name\":\""+this.getCeName()+"\",\"value\":\""+sb.toString()+"\"}"; 
		}
		throw new IllegalStateException("Unsupported dynamic UI widget.");
	}
	
	public static String toJsonArray(List<String> items) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String i : items) {
			sb.append(i).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		return sb.toString();
	}
	
	public static List<IConstantEntity> toCElist(String ceName, String value) throws JSONException {
		if (value == null || value.trim().isEmpty()) {
			return Collections.emptyList();
		}
		
		ArrayList<IConstantEntity> ceValues = new ArrayList<IConstantEntity>();
		JSONArray array = new JSONArray(value);
		int length = array.length();
		for (int i=0; i<length; i++) {
			JSONObject item = array.getJSONObject(i);
			if (item.get("name").equals(ceName)) {
				IConstantService cs = AppContext.get().getConstantService();
				IConstantEntity constantEntity = cs.getConstantEntity(ceName);
				String values = (String)item.get("value");
				if (values.indexOf(',') != -1) {
					String[] vs= values.split(",");
					for (String v: vs) {
						ceValues.add(constantEntity.getByIntValue(Integer.valueOf(v)));
					}
				} else {
					ceValues.add(constantEntity.getByIntValue(Integer.valueOf(values)));
				}
				break;	
			}
		}
		return ceValues;
	}
}
