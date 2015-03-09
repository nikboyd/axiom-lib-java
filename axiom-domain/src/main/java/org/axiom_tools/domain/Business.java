/**
 * Copyright 2013,2015 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axiom_tools.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Identifies and describes a legal business entity.
 * 
 * <h4>Business Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 */
public class Business extends Party {

	private static final long serialVersionUID = 1001001L;
	private static final Log Logger = LogFactory.getLog(PhoneNumber.class);

	@Override
	protected Log getLogger() {
		return Logger;
	}

} // Business