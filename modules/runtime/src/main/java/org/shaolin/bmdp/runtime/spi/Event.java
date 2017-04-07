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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map.Entry;

public interface Event extends Serializable {

	public String getEventConsumer();
	
	public Collection<Entry<String, Serializable>> getAllAttributes();

	public Collection<String> getAttributeKeys();
	
	public Serializable getAttribute(String key);

	public void setAttribute(String key, Serializable value);

	public Object removeAttribute(String key);

	public void clear();
	
	public String getId();

	public void setId(String id);

	public Object getFlowContext();

	public void setFlowContext(Object flowContext);
	
	public String getComments();

}
