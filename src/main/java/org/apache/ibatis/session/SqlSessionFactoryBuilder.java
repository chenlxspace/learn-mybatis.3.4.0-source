/**
 *    Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * Builds {@link SqlSession} instances.
 *
 * @author Clinton Begin
 */
public class SqlSessionFactoryBuilder {

    /**
     * Overload build(Reader reader, String environment, Properties properties)
     * @param inputStream
     * @param properties
     * @return
     */
    public SqlSessionFactory build(Reader reader) {
        return build(reader, null, null);
    }

    /**
     * Overload build(Reader reader, String environment, Properties properties)
     * @param inputStream
     * @param properties
     * @return
     */
    public SqlSessionFactory build(Reader reader, String environment) {
        return build(reader, environment, null);
    }

    /**
     * Overload build(Reader reader, String environment, Properties properties)
     * @param inputStream
     * @param properties
     * @return
     */
    public SqlSessionFactory build(Reader reader, Properties properties) {
        return build(reader, null, properties);
    }

    /**
     * 以字符流读取mybatis-config.xml文件<br>
     * 构建Configuration实例对象<br>
     * 并获取SqlSessionFactory实例化对象<br>
     * 
     * 该方法支持方法参数传递Properties和environment Id<br>
     * 在程序中指定相关属性的值及所采用的environment<br>
     * 此方式具备最高优先级<br>
     * @param inputStream
     * @param environment
     * @param properties
     * @return
     */
    public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        try {
            XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                reader.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    /**
     * Overload build(InputStream inputStream, String environment, Properties properties)
     * @param inputStream
     * @param properties
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream) {
        return build(inputStream, null, null);
    }

    /**
     * Overload build(InputStream inputStream, String environment, Properties properties)
     * @param inputStream
     * @param properties
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream, String environment) {
        return build(inputStream, environment, null);
    }

    /**
     * Overload build(InputStream inputStream, String environment, Properties properties)
     * @param inputStream
     * @param properties
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream, Properties properties) {
        return build(inputStream, null, properties);
    }

    /**
     * 以字节流读取mybatis-config.xml文件<br>
     * 构建Configuration实例对象<br>
     * 并获取SqlSessionFactory实例化对象<br>
     * 
     * 该方法支持方法参数传递Properties和environment Id<br>
     * 在程序中指定相关属性的值及所采用的environment<br>
     * 此方式具备最高优先级<br>
     * @param inputStream
     * @param environment
     * @param properties
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                inputStream.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    /**
     * 构建SqlSessionFactory：DefaultSqlSessionFactory<br>
     * SqlSessionFactory持有Configuration对象<br>
     * Configuration对象包含全局MyBatis配置信息<br>
     * @param config Configuration
     * @return SqlSessionFactory
     */
    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }

}
