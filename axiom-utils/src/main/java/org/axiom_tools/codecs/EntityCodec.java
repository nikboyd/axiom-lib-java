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
package org.axiom_tools.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Converts a properly annotated object to (or from) XML or JSON.
 * 
 * <h4>EntityCodec Responsibilities:</h4>
 * <ul>
 * <li>encodes an entity to JSON or XML</li>
 * <li>decodes an entity from JSON or XML</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>provides an entity (and/or its class) to be converted</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class EntityCodec<EntityType> {

	private static final Log Logger = LogFactory.getLog(EntityCodec.class);
	private static final String XML_ENCODING = "UTF-8";	
	private static final String Empty = "";
	
	private Class<EntityType> entityClass;
	private EntityType entity;
	
	/**
	 * Returns a new EntityCodec.
	 * @param entityClass an entity class
	 * @return a new EntityCodec
	 */
	public static <EntityType> EntityCodec<EntityType> to(Class<EntityType> entityClass) {
		EntityCodec<EntityType> result = new EntityCodec<EntityType>();
		result.entityClass = entityClass;
		return result;
	}

	/**
	 * Returns a new entity.
	 * @param entityXML an entity in XML format
	 * @return a new entity, or null
	 */
	public EntityType fromXML(String entityXML) {
		if (StringUtils.defaultString(entityXML).isEmpty()) return null;

		try {
			byte[] xmlData = entityXML.getBytes(XML_ENCODING);
			ByteArrayInputStream stream = new ByteArrayInputStream(xmlData);
			return (EntityType) buildJAXBContext().createUnmarshaller().unmarshal(stream);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Returns a new entity.
	 * @param entityJSON an entity in JSON format
	 * @return a new entity, or null
	 */
	public EntityType fromJSON(String entityJSON) {
		if (StringUtils.defaultString(entityJSON).isEmpty()) return null;
		
		try {
			return buildObjectMapper().readValue(entityJSON, this.entityClass);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Returns a new EntityCodec.
	 * @param entity an entity to serialize
	 * @return a new EntityCodec
	 */
	public static <EntityType> EntityCodec<EntityType> from(EntityType entity) {
		EntityCodec<EntityType> result = new EntityCodec<EntityType>();
		result.entity = entity;
		result.entityClass = (Class<EntityType>) entity.getClass();
		return result;
	}
	
	/**
	 * Converts an entity to XML.
	 * @return entity XML, or empty
	 */
	public String toXML() {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Marshaller m = buildJAXBContext().createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			m.marshal(this.entity, stream);
			return stream.toString(XML_ENCODING).trim();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return Empty;
		}
	}
	
	/**
	 * Converts an entity to JSON.
	 * @return entity JSON, or empty
	 */
	public String toJSON() {
		try {
			return buildObjectMapper().writeValueAsString(this.entity);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return Empty;
		}
	}

	/**
	 * Returns a new JAXB context.
	 * @return a JAXBContext
	 * @throws JAXBException if raised during construction
	 */
	private JAXBContext buildJAXBContext() throws JAXBException {
		return JAXBContext.newInstance(this.entityClass);
	}

	/**
	 * Returns a new JSON object mapper.
	 */
	private ObjectMapper buildObjectMapper() {
		ObjectMapper result = new ObjectMapper();
		result.setAnnotationIntrospector(new JaxbAnnotationIntrospector(result.getTypeFactory()));
		return result;
	}

} // Serialization