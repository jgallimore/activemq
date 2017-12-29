/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.filter;

import org.apache.activemq.command.Message;
import org.apache.activemq.util.ByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JAXPXPathEvaluator implements XPathExpression.XPathEvaluator {

    public static final String DOCUMENT_BUILDER_FACTORY_FEATURE = "org.apache.activemq.documentBuilderFactory.feature";

    private static final XPathFactory FACTORY = XPathFactory.newInstance();
    private static final Logger LOG = LoggerFactory.getLogger(XalanXPathEvaluator.class);

    private static final DocumentBuilder builder;
    private final String xpathExpression;
    private final XPath xpath = FACTORY.newXPath();

    static {
        final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setIgnoringComments(true);
        try {
            // set some reasonable defaults
            builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            // setup the feature from the system property
            setupFeatures(builderFactory);
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.warn("Error setting document builder factory feature", e);
            throw new RuntimeException("No document builder available");
        }
    }

    public JAXPXPathEvaluator(String xpathExpression) throws Exception {
        this.xpathExpression = xpathExpression;

    }

    public boolean evaluate(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            String text = ((TextMessage)message).getText();
            return evaluate(text);
        } else if (message instanceof BytesMessage) {
            BytesMessage bm = (BytesMessage)message;
            byte data[] = new byte[(int)bm.getBodyLength()];
            bm.readBytes(data);
            return evaluate(data);
        }
        return false;
    }

    private boolean evaluate(byte[] data) {
        try {
            InputSource inputSource = new InputSource(new ByteArrayInputStream(data));
            Document inputDocument = builder.parse(inputSource);
            return ((Boolean)xpath.evaluate(xpathExpression, inputDocument, XPathConstants.BOOLEAN)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluate(String text) {
        try {
            InputSource inputSource = new InputSource(new StringReader(text));
            Document inputDocument = builder.parse(inputSource);
            return ((Boolean)xpath.evaluate(xpathExpression, inputDocument, XPathConstants.BOOLEAN)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    protected static void setupFeatures(DocumentBuilderFactory factory) {
        Properties properties = System.getProperties();
        List<String> features = new ArrayList<String>();
        for (Map.Entry<Object, Object> prop : properties.entrySet()) {
            String key = (String) prop.getKey();
            if (key.startsWith(DOCUMENT_BUILDER_FACTORY_FEATURE)) {
                String uri = key.split(DOCUMENT_BUILDER_FACTORY_FEATURE + ":")[1];
                Boolean value = Boolean.valueOf((String)prop.getValue());
                try {
                    factory.setFeature(uri, value);
                    features.add("feature " + uri + " value " + value);
                } catch (ParserConfigurationException e) {
                    LOG.warn("DocumentBuilderFactory doesn't support the feature {} with value {}, due to {}.", new Object[]{uri, value, e});
                }
            }
        }
        if (features.size() > 0) {
            StringBuffer featureString = new StringBuffer();
            // just log the configured feature
            for (String feature : features) {
                if (featureString.length() != 0) {
                    featureString.append(", ");
                }
                featureString.append(feature);
            }
        }

    }


    @Override
    public String toString() {
        return xpathExpression;
    }
}
