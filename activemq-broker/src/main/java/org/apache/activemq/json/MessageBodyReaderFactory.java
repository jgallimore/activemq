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
package org.apache.activemq.json;

import javax.ws.rs.ext.MessageBodyReader;
import java.lang.reflect.InvocationTargetException;

/**
 * This class provides the entry point for the abstraction between JSON
 * libraries. We default to Jackson, but if that is not on the classpath, and a JSONB
 * implementation is, we'll use that instead.
 *
 * The use of MessageBodyReaders to convert a TextMessages to a
 * Java object is one of the proposals for a future version of JMS:
 *
 * https://github.com/jakartaee/messaging-proposals/tree/master/jsonb-messages/proposal1
 * https://github.com/jakartaee/messaging-proposals/tree/master/jaxb-messages/proposal1
 *
 * This abstraction could potentially be extended out to support this functionality.
 */
public class MessageBodyReaderFactory {

    private static Class<?> implementationClass = setupImplementation();

    public static <T> MessageBodyReader<T> get(Class<T> cls) {
        if (implementationClass == null) {
            // locate the implementation
            setupImplementation();
        }

        try {
            return (MessageBodyReader<T>) implementationClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Uh oh");
        }
    }

    private static Class<?> setupImplementation() {
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            return Class.forName("org.apache.activemq.json.JacksonMessageBodyReader");
        } catch (Exception e) {
        }

        try {
            Class.forName("javax.json.bind.Jsonb");
            return Class.forName("org.apache.activemq.json.JsonBMessageBodyReader");
        } catch (Exception e) {
        }



        return null;
    }


}
