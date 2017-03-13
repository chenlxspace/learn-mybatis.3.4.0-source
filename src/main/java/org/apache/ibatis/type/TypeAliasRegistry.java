/**
 *    Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.Resources;

/**
 * @author Clinton Begin
 */
public class TypeAliasRegistry {

    /**
     * 缓存Map, 缓存别名与Class的映射关系
     */
    private final Map<String, Class<?>> TYPE_ALIASES = new HashMap<String, Class<?>>();

    /**
     * 构造器, 内置 Java 常用类的别名
     */
    public TypeAliasRegistry() {
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);

        registerAlias("byte[]", Byte[].class);
        registerAlias("long[]", Long[].class);
        registerAlias("short[]", Short[].class);
        registerAlias("int[]", Integer[].class);
        registerAlias("integer[]", Integer[].class);
        registerAlias("double[]", Double[].class);
        registerAlias("float[]", Float[].class);
        registerAlias("boolean[]", Boolean[].class);

        registerAlias("_byte", byte.class);
        registerAlias("_long", long.class);
        registerAlias("_short", short.class);
        registerAlias("_int", int.class);
        registerAlias("_integer", int.class);
        registerAlias("_double", double.class);
        registerAlias("_float", float.class);
        registerAlias("_boolean", boolean.class);

        registerAlias("_byte[]", byte[].class);
        registerAlias("_long[]", long[].class);
        registerAlias("_short[]", short[].class);
        registerAlias("_int[]", int[].class);
        registerAlias("_integer[]", int[].class);
        registerAlias("_double[]", double[].class);
        registerAlias("_float[]", float[].class);
        registerAlias("_boolean[]", boolean[].class);

        registerAlias("date", Date.class);
        registerAlias("decimal", BigDecimal.class);
        registerAlias("bigdecimal", BigDecimal.class);
        registerAlias("biginteger", BigInteger.class);
        registerAlias("object", Object.class);

        registerAlias("date[]", Date[].class);
        registerAlias("decimal[]", BigDecimal[].class);
        registerAlias("bigdecimal[]", BigDecimal[].class);
        registerAlias("biginteger[]", BigInteger[].class);
        registerAlias("object[]", Object[].class);

        registerAlias("map", Map.class);
        registerAlias("hashmap", HashMap.class);
        registerAlias("list", List.class);
        registerAlias("arraylist", ArrayList.class);
        registerAlias("collection", Collection.class);
        registerAlias("iterator", Iterator.class);

        registerAlias("ResultSet", ResultSet.class);
    }

    /**
     * 解析别名获取类Class
     * @param string
     * @return
     */
    @SuppressWarnings("unchecked")
    // throws class cast exception as well if types cannot be assigned
    public <T> Class<T> resolveAlias(String string) {
        try {
            if (string == null) {
                return null;
            }
            // issue #748
            String key = string.toLowerCase(Locale.ENGLISH);
            Class<T> value;
            if (TYPE_ALIASES.containsKey(key)) {
                value = (Class<T>) TYPE_ALIASES.get(key);
            } else {
                value = (Class<T>) Resources.classForName(string);
            }
            return value;
        } catch (ClassNotFoundException e) {
            throw new TypeException("Could not resolve type alias '" + string + "'.  Cause: " + e, e);
        }
    }

    /**
     * 根据包命名自动扫描注册包内及子包内所有类
     * 
     * @param packageName
     */
    public void registerAliases(String packageName) {
        registerAliases(packageName, Object.class);
    }

    /**
     * 包名及父类类型
     * 
     * @param packageName
     * @param superType
     */
    public void registerAliases(String packageName, Class<?> superType) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
        //查找该类的所有子类
        resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
        //获取该类的所有子类
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        
        for (Class<?> type : typeSet) {
            // Ignore inner classes and interfaces (including package-info.java)
            // Skip also inner classes. See issue #6
            //不支持接口, 内部类
            if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
                //默认按类名第一个字母小写设置别名
                registerAlias(type);
            }
        }
    }

    /**
     * 注册类别名
     * @param type
     */
    public void registerAlias(Class<?> type) {
        String alias = type.getSimpleName();
        //如果有别名注解, 以注解中的别名为准
        Alias aliasAnnotation = type.getAnnotation(Alias.class);
        if (aliasAnnotation != null) {
            alias = aliasAnnotation.value();
        }
        //注册类别名
        registerAlias(alias, type);
    }

    /**
     * 缓存类别名和类全路径映射关系
     * 
     * @param alias
     * @param value
     */
    public void registerAlias(String alias, Class<?> value) {
        if (alias == null) {
            throw new TypeException("The parameter alias cannot be null");
        }
        // issue #748
        //别名全英文小写
        String key = alias.toLowerCase(Locale.ENGLISH);
        
        //已存在则抛异常, 即别名不能重复
        if (TYPE_ALIASES.containsKey(key) && TYPE_ALIASES.get(key) != null && !TYPE_ALIASES.get(key).equals(value)) {
            throw new TypeException("The alias '" + alias + "' is already mapped to the value '"
                    + TYPE_ALIASES.get(key).getName() + "'.");
        }
        
        //缓存别名和类的映射关系
        TYPE_ALIASES.put(key, value);
    }

    /**
     * 缓存类别名和类名称映射关系
     * 通过类加载器的classForName获取类的全路径
     * @param alias
     * @param value
     */
    public void registerAlias(String alias, String value) {
        try {
            registerAlias(alias, Resources.classForName(value));
        } catch (ClassNotFoundException e) {
            throw new TypeException("Error registering type alias " + alias + " for " + value + ". Cause: " + e, e);
        }
    }

    /**
     * 获取所有别名
     * 
     * @since 3.2.2
     */
    public Map<String, Class<?>> getTypeAliases() {
        return Collections.unmodifiableMap(TYPE_ALIASES);
    }

}
