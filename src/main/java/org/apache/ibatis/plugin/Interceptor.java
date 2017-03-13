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
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * @author Clinton Begin
 */
public interface Interceptor {

    /**
     * 拦截方法, 内部必须调用invocation.proceed() <br>
     * 显式地推进责任链前进, 即下一个拦截器拦截目标方法 <br>
     * @param invocation
     * @return
     * @throws Throwable
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 用当前拦截器生成目标的代理 <br>
     * Plugin.wrap(target,this) 来完成的, 把目标target和拦截器this传给了包装函数<br>
     * @param target
     * @return
     */
    Object plugin(Object target);

    /**
     * 用于设置额外的参数, 参数配置在拦截器的Properties节点里
     * @param properties
     */
    void setProperties(Properties properties);

}
