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
package org.shaolin.uimaster.page.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.security.UserContext;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.uimaster.page.MobilitySupport;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.exception.UIPageException;

public class UIPageObject implements java.io.Serializable {
	private static final String _CUST = "_cust";

	private static final long serialVersionUID = -3835708230178517577L;

	private static final String DEFAULT_LOCALE = "DEFAULT_LOCALE";

	private String entityName = null;

	private UIFormObject ui = null;

	private final StringBuffer pageCSS = new StringBuffer();
	
	private final StringBuffer mobPageCSS = new StringBuffer();
	
	private final StringBuffer mobAppPageCSS = new StringBuffer();
	
	private boolean hasMobilePage = false;
	
	private boolean hasCustomizedPage = false;
	
	private UIPage entity;
	
	public UIPageObject(String entityName) throws UIPageException {
		this.entityName = entityName;
		load();
	}

	private void load() throws UIPageException {
		entity = IServerServiceManager.INSTANCE.getEntityManager()
				.getEntity(entityName, UIPage.class);
		
		try {
			IServerServiceManager.INSTANCE.getEntityManager()
				.getEntity(entityName + MobilitySupport.MOB_PAGE_SUFFIX, UIPage.class);
			hasMobilePage = true;
        } catch (EntityNotFoundException e) {
        	hasMobilePage = false;
        }
		
		try {
			IServerServiceManager.INSTANCE.getEntityManager()
				.getEntity(entityName + _CUST, UIPage.class);
			hasCustomizedPage = true;
        } catch (EntityNotFoundException e) {
        	hasCustomizedPage = false;
        }
		
		ui = new UIFormObject(entityName, entity);

		Map<String, List<String>> importCSSCodeMap = new HashMap<String, List<String>>();
		Map<String, List<String>> importMobCSSCodeMap = new HashMap<String, List<String>>();
		Map<String, List<String>> importAppMobCSSCodeMap = new HashMap<String, List<String>>();
		
		addCSS(DEFAULT_LOCALE, false, importCSSCodeMap);
		addCSS(DEFAULT_LOCALE, true, importMobCSSCodeMap);
		addMobAppCSS(DEFAULT_LOCALE, importAppMobCSSCodeMap);
		
		importCSS(importCSSCodeMap);
		importMobCSS(importMobCSSCodeMap);
		importMobAppCSS(importAppMobCSSCodeMap);
	}
	
	public UIPage getUIPage() {
		return entity;
	}

	private void addCSS(String locale, boolean isMobile, Map<String, List<String>> importCSSCodeMap) {
		List<String> importCSSCode = new ArrayList<String>();
		String[] css = WebConfig.getSingleCommonCSS(entityName);
		if (css != null) {
			for (int i = 0; i < css.length; i++) {
				importCSSCode.add("<link rel=\"stylesheet\" href=\"" + css[i]
						+ "\" type=\"text/css\">\n");
			}
		}
		if (!WebConfig.skipCommonCss(entityName)) {
			String[] common = WebConfig.getCommonCss();
			if (isMobile) {
				common = WebConfig.getCommonMobCss();
			}
			for (int i = 0; common != null && i < common.length; i++) {
				importCSSCode.add("<link rel=\"stylesheet\" href=\"" + WebConfig.getResourceContextRoot() + common[i]
						+ "\" type=\"text/css\">\n");
			}
		}
		importCSSCodeMap.put(locale, importCSSCode);
	}
	
	private void addMobAppCSS(String locale, Map<String, List<String>> importAppMobCSSCodeMap) {
		List<String> importCSSCode = new ArrayList<String>();
		String[] css = WebConfig.getSingleCommonAppCSS(entityName);
		if (css != null) {
			for (int i = 0; i < css.length; i++) {
				importCSSCode.add("<link rel=\"stylesheet\" href=\"" + css[i]
						+ "\" type=\"text/css\">\n");
			}
		}
		if (!WebConfig.skipCommonCss(entityName)) {
			String[] common = WebConfig.getCommonMobAppCss();
			for (int i = 0; common != null && i < common.length; i++) {
				importCSSCode.add("<link rel=\"stylesheet\" href=\"" + common[i]
						+ "\" type=\"text/css\">\n");
			}
		}
		importAppMobCSSCodeMap.put(locale, importCSSCode);
	}

	private void importCSS(Map<String, List<String>> importCSSCodeMap) {
//		String userLocale = LocaleContext.getUserLocale();
//		if (userLocale != null && !userLocale.trim().equals(DEFAULT_LOCALE)) {
//			if (!importCSSCodeMap.containsKey(userLocale)) {
//				addCSS(userLocale, false);
//				addCSSFile(userLocale);
//			}
//		} else {
//			userLocale = DEFAULT_LOCALE;
//		}

		List<String> importCSSCode = (List<String>) importCSSCodeMap.get(DEFAULT_LOCALE);
		Iterator<String> iterator = importCSSCode.iterator();
		while (iterator.hasNext()) {
			String code = iterator.next();
			pageCSS.append(code);
		}
		String importCSS = WebConfig.getImportCSS(entityName);
		String cssCode = "<link rel=\"stylesheet\" href=\"" + importCSS
				+ "\" type=\"text/css\">\n";
		pageCSS.append(cssCode);
	}
	
	private void importMobCSS(Map<String, List<String>> importMobCSSCodeMap) {
		List<String> importCSSCode = (List<String>) importMobCSSCodeMap.get(DEFAULT_LOCALE);
		Iterator<String> iterator = importCSSCode.iterator();
		while (iterator.hasNext()) {
			String code = iterator.next();
			mobPageCSS.append(code);
		}
		String importCSS = WebConfig.getImportMobCSS(entityName);
		String cssCode = "<link rel=\"stylesheet\" href=\"" + importCSS
				+ "\" type=\"text/css\">\n";
		mobPageCSS.append(cssCode);
	}
	
	private void importMobAppCSS(Map<String, List<String>> importAppMobCSSCodeMap) {
		List<String> importCSSCode = (List<String>) importAppMobCSSCodeMap.get(DEFAULT_LOCALE);
		Iterator<String> iterator = importCSSCode.iterator();
		while (iterator.hasNext()) {
			String code = iterator.next();
			mobAppPageCSS.append(code);
		}
		String cssCode = "<link rel=\"stylesheet\" href=\"" + WebConfig.getImportAppMobCSS(entityName)
				+ "\" type=\"text/css\">\n";
		mobAppPageCSS.append(cssCode);
	}
	
	public boolean needBackButton() {
		return !WebConfig.skipBackButton(this.entityName);
	}

	public UIFormObject getUIForm() throws EntityNotFoundException, UIPageException {
		if (UserContext.isMobileRequest() && hasMobilePage()) {
			return PageCacheManager.getUIPageObject(getRuntimeEntityName()).ui;
		}
		
		return ui;
	}

	public String getRuntimeEntityName() {
		if (UserContext.isMobileRequest() && hasMobilePage()) {
			return entityName + MobilitySupport.MOB_PAGE_SUFFIX;
		}
		if (hasCustomizedPage()) {
			return entityName + _CUST;
		}
		return entityName;
	}

	public UIFormObject getUIFormObject() throws EntityNotFoundException, UIPageException {
		if (UserContext.isMobileRequest() && hasMobilePage()) {
			return PageCacheManager.getUIPageObject(getRuntimeEntityName()).ui;
		}
		
		return ui;
	}

	public String getPageHintLink() {
    	return ui.getPageHintLink();
    }
	
	public void addDynamicPageHints(String linkInfo) {
		ui.addDynamicPageHints(linkInfo);
    }
	
	public StringBuffer getPageCSS() {
		return pageCSS;
	}
	
	public StringBuffer getMobPageCSS() {
		return mobPageCSS;
	}
	
	public StringBuffer getMobAppPageCSS() {
		return mobAppPageCSS;
	}
	
	
	public boolean hasMobilePage() {
		return hasMobilePage;
	}
	
	public boolean hasCustomizedPage() {
		return hasCustomizedPage;
	}
}
