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
package org.axiom_tools.validations;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import javax.validation.*;

import org.axiom_tools.context.SpringContext;

/**
 * Validates a model using the bean validation framework.
 * 
 * <h4>ModelValidator Responsibilities:</h4>
 * <ul>
 * <li>validates an instance of an annotated model class</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>properly configure a validator</li>
 * </ul>
 */
public class ModelValidator {

	private static final String Dot = ".";

	private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();	
	private Properties messages = new Properties();
	
	/**
	 * Configures the message map from a message file.
	 * @param messageFile a message file path
	 * @throws Exception if raised within
	 */
	public void setMessages(String messageFile) throws Exception {
		InputStream fileStream = getClass().getResourceAsStream(messageFile);
		if (fileStream != null) {
			messages.load(fileStream);
			fileStream.close();
		}
	}

	/**
	 * Returns the configured validator.
	 */
	public static ModelValidator getConfiguredValidator() {
		return SpringContext.getConfigured(ModelValidator.class);
	}
	
	/**
	 * Validates an annotated entity.
	 * @param entity an annotated entity
	 * @return any constraint violation error messages discovered during entity validation
	 */
	public <EntityType> String[] validate(EntityType entity) {
		String[] empty = { };
		Set<ConstraintViolation<EntityType>> errors = factory.getValidator().validate(entity);
		if (errors.size() == 0) return empty;

		ArrayList<String> results = new ArrayList<String>();
		for (ConstraintViolation<EntityType> error : errors) {
			String messageKey = buildMessageKey(error);			
			if (messages.containsKey(messageKey)) {
				results.add(messages.getProperty(messageKey));
			}
			else {
				results.add(error.getMessage());
			}
		}

		return results.toArray(empty);
	}

	/**
	 * Builds a message key from a constraint violation.
	 * @param error a constraint violation error
	 * @return an error message key
	 */
	private <EntityType> String buildMessageKey(ConstraintViolation<EntityType> error) {
		Class<?> errorType = error.getConstraintDescriptor().getAnnotation().annotationType();
		Class<?> beanType = error.getRootBeanClass();
		StringBuilder builder = new StringBuilder();
		builder.append(errorType.getSimpleName());
		builder.append(Dot + beanType.getSimpleName());
		builder.append(Dot + error.getPropertyPath());
		return builder.toString();
	}

} // ModelValidator
