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
package org.shaolin.bmdp.runtime.spi;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author wushaol
 */
public interface IRegistry {

	public String getEncoding();

	public Set<String> getConfigItemPaths();

	public Set<String> getConfigNodePaths();

	public String getValue(String path);

	public String getValue(String path, String defaultValue);
	
	public int getValue(String path, int defaultValue);
	
	public long getValue(String path, long defaultValue);
	
	public float getValue(String path, float defaultValue);
	
	public boolean getValue(String path, boolean defaultValue);
	
	public Map<String, String> getNodeItems(String path);

	public List<String> getNodeChildren(String path);

	public boolean exists(String path);

}
