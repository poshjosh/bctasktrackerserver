/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.tasktracker.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import javax.servlet.ServletContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 15, 2017 10:42:05 AM
 */
public class ServletContextClassLoaderDecorator extends ClassLoader {

    private final ServletContext servletContext;
    
    private final ClassLoader delegate;

    public ServletContextClassLoaderDecorator(ServletContext servletContext) {
        this.servletContext = Objects.requireNonNull(servletContext);
        this.delegate = servletContext.getClassLoader();
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return delegate.loadClass(name);
    }

    @Override
    public URL getResource(String name) {
        try{
            URL url = servletContext.getResource(name);
            if(url == null) {
                url = delegate.getResource(name);
                if(url == null) {
                    url = Thread.currentThread().getContextClassLoader().getResource(name);
                }
            }
            
            return url;
        }catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> en = delegate.getResources(name);
        if(en == null) {
            en = Thread.currentThread().getContextClassLoader().getResources(name);
        }
        return en;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
System.out.println("Name: "+name);        
        name = name.replace('\\', '/');
System.out.println(" URI: "+name);                
        InputStream in = servletContext.getResourceAsStream(name);
System.out.println("ServletContext: ");        
System.out.println("A=================================" + in);                        
        if(in == null) {
            in = delegate.getResourceAsStream(name);
System.out.println("Delegate: "+delegate);            
System.out.println("B=================================" + in);                                    
            if(in == null) {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                in = classLoader.getResourceAsStream(name);
System.out.println("ContextClassLoader: "+classLoader);                
System.out.println("C=================================" + in);                                        
            }
        }    
        return in;
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        delegate.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        delegate.setPackageAssertionStatus(packageName, enabled);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        delegate.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void clearAssertionStatus() {
        delegate.clearAssertionStatus();
    }
}
