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

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * This is a simple implementation of MessageBodyReader that will use JSONB to write to JSON.
 * @param <T>
 */
public class JsonBMessageBodyWriter<T> implements MessageBodyWriter<T> {

    private static final Jsonb JSONB = getJsonB();

    private static Jsonb getJsonB() {
        final ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        final ClassLoader cl = JsonBMessageBodyWriter.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);

        try {
            return JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        } catch (Exception e) {
            return null;
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }


    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
    }

    @Override
    public void writeTo(final T t, Class<?> cls, final Type type, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> multivaluedMap, final OutputStream outputStream) throws IOException, WebApplicationException {
        JSONB.toJson(t, outputStream);
    }
}
