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
package org.shaolin.uimaster.page.ajax.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This defines the behavior of a command.
 */
public interface IAjaxCommand {

	public static final String EXECUTE_SUCCESS = "1";

	public static final String EXECUTE_FAILURE = "-1";

	/**
	 * Execute this command.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Object execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	/**
	 * Whether need user session for executing this command.
	 * 
	 * @return
	 */
	public boolean needUserSession();
	
}
