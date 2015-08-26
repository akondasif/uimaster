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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.datamodel.page.UITableColumnType;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.Chart;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;

public class HTMLChartDoughnutType extends HTMLChartSuper {
	
	private static final long serialVersionUID = -5232602952223828765L;

	public HTMLChartDoughnutType() {
	}

	public HTMLChartDoughnutType(HTMLSnapshotContext context) {
		super(context);
	}

	public HTMLChartDoughnutType(HTMLSnapshotContext context, String id) {
		super(context, id);
	}

	/**
	 * var data = [
	    {
	        value: 300,
	        color:"#F7464A",
	        highlight: "#FF5A5E",
	        label: "Red"
	    },
	    {
	        value: 50,
	        color: "#46BFBD",
	        highlight: "#5AD3D1",
	        label: "Green"
	    },
	    {
	        value: 100,
	        color: "#FDB45C",
	        highlight: "#FFC870",
	        label: "Yellow"
	    }
	]
	 */
	@Override
	public void generateData(List<UITableColumnType> columns,
			HTMLSnapshotContext context, int depth) throws Exception {
		Map<String, Integer> listData = (Map<String, Integer>)this.removeAttribute("query");
		if (listData != null && !listData.isEmpty()) {
			StringBuffer sb = new StringBuffer("data: [");
			Set<String> keys = listData.keySet();
			for (String key : keys) {
				sb.append("{").append("value: ").append(listData.get(key));
				sb.append(",").append("label: '").append(key).append("'");
				sb.append("},");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]");
			context.generateHTML(sb.toString());
		}
	}

}