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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.datamodel.page.UITableColumnType;
import org.shaolin.uimaster.page.DisposableBfString;
import org.shaolin.uimaster.page.UserRequestContext;
import org.shaolin.uimaster.page.javacc.UIVariableUtil;

public class HTMLChartPolarPieType extends HTMLChartSuper {
	private static final long serialVersionUID = -5232602952223828765L;

	public HTMLChartPolarPieType(String id) {
		super(id);
	}

	@Override
	public void generateData(List<UITableColumnType> columns,
			UserRequestContext context, int depth) throws Exception {
		Map<String, String> cssStyles = new HashMap<String, String>();
		for (UITableColumnType col: columns) {
			cssStyles.put(UIVariableUtil.getI18NProperty(col.getTitle()), col.getCssStype());
		}
		Map<String, Integer> listData = (Map<String, Integer>)this.removeAttribute("query");
		if (listData != null && !listData.isEmpty()) {
			StringBuilder sb = DisposableBfString.getBuffer();
			try {
				sb.append("datasets: [{ data: [");
				Set<String> keys = listData.keySet();
				for (String key : keys) {
					sb.append(listData.get(key)).append(",");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append("], backgroundColor: [");
				for (String key : keys) {
					String css = cssStyles.get(key);
					if (css != null) {
						sb.append(css).append(",");
					} else {
						//get default
						sb.append(cssStyles.get(0)).append(",");
					}
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append("]}]");
				context.generateHTML(sb.toString());
			} finally {
				DisposableBfString.release(sb);
			}
		}
	}

}
