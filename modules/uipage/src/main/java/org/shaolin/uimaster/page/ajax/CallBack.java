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
package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

/**
 * This interface is purposed for all the ajax call back actions.
 * Refer to RefForm.openInWindows and closeIfinWindows as an example.
 * 
 * @author wushaol
 *
 */
public interface CallBack extends Serializable {

	void execute(Object... objects);
}
