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
package org.shaolin.uimaster.page.od.mappings;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.datamodel.page.UIComponentParamType;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.UserRequestContext;
import org.shaolin.uimaster.page.exception.UIComponentNotFoundException;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.od.ODPageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIComponentParam extends DataParam {

	private static final Logger logger = LoggerFactory
			.getLogger(UIComponentParam.class);

	private final UIComponentParamType type;

	public UIComponentParam(UIComponentParamType type) {
		super(type);
		this.type = type;
	}
	
	public String getComponentPath() {
		return type.getComponentPath();
	}
	
	@Override
	public void parseDataToUI(OOEEContext ooeeContext)
			throws ParsingException {
		ExpressionType expression = type.getImplExpression();
		if (expression != null) {
			if (logger.isDebugEnabled())
				logger.debug("ui component type expression: {0}",
						expression.getExpressionString());
			expression.parse(ooeeContext);
		}
	}

	@Override
	public void parseUIToData(OOEEContext ooeeContext, ComponentMappingType mapping)
			throws ParsingException {
	}
	
	/**
	 * 
	 * Evaluate ui component attributes.
	 * Situation examples:
	 * <br>1.od base rule (bmiasia.ebos.odmapper.baserule.).
	 * <mappingRule entityName="org.shaolin.uimaster.page.od.rules.UIChoiceOptionValueAndCE"/>
     * <UIComponent paramName="UIChoice" componentPath="UISingleChoice"/>
	 * <br>2.od entity mapping root node (1 level).
	 * <mappingRule entityName="bmiasia.app.common.od.InputMenu"/>
     * <UIComponent paramName="UIEntity" componentPath="UIEntity"/>
     * 
	 * @param param
	 * @param odContext
	 * 
	 * @return
	 * @throws EvaluationException
	 */
	@Override
	public Object executeDataToUI(ODContext odContext) throws EvaluationException {
		String uiid = type.getComponentPath();
		if (odContext.getUIParamName().equals(ODPageContext.UIPageEntity_MARK)) {
			// remove page prefix.
			uiid = uiid.substring(uiid.indexOf('.') + 1);
		} else {
			// handle ui form.
			if (uiid.indexOf(odContext.getUIParamName()) != -1) {
				uiid = uiid.replace(odContext.getUIParamName(), odContext.getUiEntity().getName());
			} 
//			else {
//				uiid = odContext.getUiEntity().getName() + '.' + uiid;
//			}
		}
		String htmlPrefix = odContext.getHtmlContext().getHTMLPrefix();
		if (htmlPrefix != null && htmlPrefix.length() > 0) {
			uiid = htmlPrefix + uiid;
		}
		if (!odContext.isDataToUI()) {
			//optimize the performance.
			return uiid;
		}
		UserRequestContext htmlContext = odContext.getHtmlContext();
		try {
			return htmlContext.getHtmlWidget(uiid);
		} catch (UIComponentNotFoundException e) {
			throw new EvaluationException(e.getMessage(), e);
		}
	}

	@Override
	public void executeUIToData(ODContext odContext, OOEEContext ooeeContext,
			ComponentMappingType mapping) throws EvaluationException {
		// doesn't support!
	}

}