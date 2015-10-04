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

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.page.UIMatrixType;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <div class="seatlist" style="margin:0 auto;width:352px">
 * 	<dl class="clear">
 * 		<dd>
 * 			<div class="TSUfEr"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="TSUfEr"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="BVKLdL"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="BVKLdL"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="BVKLdL"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="BVKLdL"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="BVKLdL"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="BVKLdL"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="TSUfEr"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="TSUfEr"></div>
 * 		</dd>
 * 		<dd>
 * 			<div class="TSUfEr"></div>
 * 		</dd>
 * 	</dl>
 * </div>
 * 
 * @author wushaol
 *
 */
public class HTMLMatrixType extends HTMLTextWidgetType 
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLMatrixType.class);

    private String buttonType;

    public HTMLMatrixType()
    {
    }

    public HTMLMatrixType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLMatrixType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
        	UIMatrixType matrix = (UIMatrixType) this.removeAttribute("matrix");
        	String coordination = (String)this.removeAttribute("coordination");
        	String[] dimensions = coordination.split(",");
        	int x = Integer.valueOf(dimensions[0]);
        	int y = Integer.valueOf(dimensions[1]);
        	
            this.removeAttribute("init");
        	
            generateWidget(context);
            context.generateHTML("<div class=\"uimaster_matrix\" type=\"");
            context.generateHTML(coordination);
            context.generateHTML("\" id=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            context.generateHTML(">");
            
			for (int i = 0; i < x; i++) {
				context.generateHTML("<dl class=\"uimaster_matrix_row\">");
				for (int j=0; j < y; j++) {
					context.generateHTML("<dd><div class=\"uimaster_matrix_col\"></div></dd>");
				}
				context.generateHTML("</dl>");
			}
            
            context.generateHTML("</div>");
            
            generateEndWidget(context);
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
    	try {
			ExpressionType initQueryExpr = (ExpressionType)this.removeAttribute("initExpr");
			Object initResult = ee.evaluateExpression(initQueryExpr);
			ExpressionType coordinateExpr = (ExpressionType)this.removeAttribute("coordinateExpr");
			Object coorResult = ee.evaluateExpression(coordinateExpr);
			
			this.addAttribute("init", initResult);
			this.addAttribute("coordination", coorResult);
    	} catch (EvaluationException e) {
			throw new IllegalStateException(e);
		}
		return null;
    }

}
