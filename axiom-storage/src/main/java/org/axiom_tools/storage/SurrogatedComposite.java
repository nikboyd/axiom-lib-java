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
package org.axiom_tools.storage;

/**
 * A composition of surrogated items, some of whose components are
 * held in maps or sets.
 */
public interface SurrogatedComposite extends SurrogatedItem {
	
	/**
	 * Returns any maps that contain surrogated components. Due to the limitations of
	 * generic maps, their actual type signatures are erased. The framework used to 
	 * save the components contained in the maps will determine their actual types.
	 */
	public Object[] componentMaps();
	
	/**
	 * Returns any sets that contain surrogated components. Due to the limitations of
	 * generic sets, their actual type signatures are erased. The framework used to 
	 * save the components contained in the maps will determine their actual types.
	 */
	public Object[] componentSets();

	/**
	 * Returns any directly related surrogated components.
	 */
	public SurrogatedItem[] components();

	/**
	 * Sets component references after they are saved.
	 * @param components saved components
	 */
	public void components(SurrogatedItem[] components);

} // SurrogatedComposite
