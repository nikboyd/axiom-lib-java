/**
 * Copyright 2013,2015 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.axiom_tools.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Converts a properly annotated model to (or from) XML or JSON.
 *
 * <h4>ModelCodec Responsibilities:</h4>
 * <ul>
 * <li>encodes a model to JSON or XML</li>
 * <li>decodes a model from JSON or XML</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>provides a model (and/or its class) to be converted</li>
 * </ul>
 * @param <ModelType> a model type
 */
@SuppressWarnings("unchecked")
public class ModelCodec<ModelType> {

    private static final Logger Log = LoggerFactory.getLogger(ModelCodec.class);
    private static final String XML_ENCODING = "UTF-8";
    private static final String Empty = "";

    private Class<ModelType> entityClass;
    private ModelType entity;

    /**
     * Returns a new ModelCodec.
     * @param <ModelType> a model type
     * @param modelClass a model class
     * @return a new ModelCodec
     */
    public static <ModelType> ModelCodec<ModelType> to(Class<ModelType> modelClass) {
        ModelCodec<ModelType> result = new ModelCodec();
        result.entityClass = modelClass;
        return result;
    }

    /**
     * Returns a new model instance.
     * @param modelXML a model in XML format
     * @return a new model, or null
     */
    public ModelType fromXML(String modelXML) {
        if (StringUtils.defaultString(modelXML).isEmpty()) {
            return null;
        }

        try {
            byte[] xmlData = modelXML.getBytes(XML_ENCODING);
            ByteArrayInputStream stream = new ByteArrayInputStream(xmlData);
            return (ModelType) buildJAXBContext().createUnmarshaller().unmarshal(stream);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Returns a new model instance.
     * @param modelJSON a model in JSON format
     * @return a new model, or null
     */
    public ModelType fromJSON(String modelJSON) {
        if (StringUtils.defaultString(modelJSON).isEmpty()) {
            return null;
        }

        try {
            return buildObjectMapper().readValue(modelJSON, this.entityClass);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Returns a new ModelCodec.
     * @param <ModelType> a model type
     * @param model a model instance to serialize
     * @return a new ModelCodec
     */
    public static <ModelType> ModelCodec<ModelType> from(ModelType model) {
        ModelCodec<ModelType> result = new ModelCodec();
        result.entity = model;
        result.entityClass = (Class<ModelType>) model.getClass();
        return result;
    }

    /**
     * Converts a model to XML.
     * @return model XML, or empty
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
            Log.error(e.getMessage(), e);
            return Empty;
        }
    }

    /**
     * Converts a model to JSON.
     * @return model JSON, or empty
     */
    public String toJSON() {
        try {
            return buildObjectMapper().writeValueAsString(this.entity);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
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
        result.enable(SerializationFeature.INDENT_OUTPUT);
        return result;
    }

} // Serialization
