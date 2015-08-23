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
package org.shaolin.bmdp.workflow.spi;

public interface WorkflowSession extends java.io.Serializable {
	
	public static final int COMMIT = 1;
	public static final int ROLLBACK = 0;
	public static final int NOTHING = 2;

	/**
	 * Get the session id.
	 * 
	 * @return The session id.
	 */
	public String getID();

	/**
	 * @see COMMIT
	 * @see ROLLBACK
	 * @see NOTHING
	 * 
	 * @return Get the transaction status flag.
	 */
	public int getTXFlag();

}
