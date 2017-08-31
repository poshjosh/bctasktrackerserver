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
package com.bc.tasktracker.server.listeners;

import com.bc.appcore.AppContext;
import com.bc.appcore.AppCore;
import com.bc.tasktracker.server.AppLauncherWeb;
import com.bc.tasktracker.server.AttributeNames;
import com.bc.tasktracker.server.ServletContextClassLoaderDecorator;
import com.bc.tasktracker.server.WebApp;
import com.bc.tasktracker.server.WebAppImpl;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.bc.appcore.properties.PropertiesContext;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Web application lifecycle listener.
 *
 * @author Josh
 */
public class ServletContextListenerImpl implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ServletContextListenerImpl.class.getName());
    
    private final String appId;

    public ServletContextListenerImpl(String appId) {
        this.appId = Objects.requireNonNull(appId);
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        final ServletContext servletContext = sce.getServletContext();
        
        this.setAttributesFromInitParameters(servletContext);
        
        final Boolean PRODUCTION_MODE = (Boolean)servletContext.getAttribute(AttributeNames.PRODUCTION_MODE);

        servletContext.setAttribute(AttributeNames.APP_ID, appId);

        logger.info(() -> "App ID: " + appId + ", Production mode: " + PRODUCTION_MODE);
        
        final String externalResource = "META-INF/bctasktracker"; 
        final String internalResource = "META-INF/"+appId;
        
        final ClassLoader classLoader = new ServletContextClassLoaderDecorator(servletContext);
        
        final PropertiesContext parentPropsCtxExternal = PropertiesContext.builder()
                .classLoader(Thread.currentThread().getContextClassLoader())
                .workingDirPath(externalResource)
                .build();
        
        final PropertiesContext parentPropsCtxInternal = PropertiesContext.builder()
                .classLoader(classLoader)
                .workingDirPath(servletContext.getRealPath(internalResource))
                .typeSuffix(PropertiesContext.TypeName.LOGGING, PRODUCTION_MODE ? null : "devmode")
                .build();
                
        final List<PropertiesContext> parentPropertiesPaths = Arrays.asList(
                parentPropsCtxExternal, parentPropsCtxInternal
        );
        
        final AppLauncherWeb launcher = new AppLauncherWeb();
        
        final Function<AppContext, WebApp> createAppFromContext =
                (appContext) -> new WebAppImpl(appContext, servletContext);
        
        launcher.servletContext(servletContext)
                .enableSync(false)
                .productionMode(PRODUCTION_MODE)
                .parentPropertiesPaths(parentPropertiesPaths)
                .classLoader(new ServletContextClassLoaderDecorator(servletContext))
                .createAppFromContext(createAppFromContext)
                .appId(appId);
        
        final WebApp app = launcher.launch(new String[0]);
        
        servletContext.setAttribute(AttributeNames.APP, app);
    }
    
    private void setAttributesFromInitParameters(ServletContext servletContext) {
        final Enumeration<String> paramNames = servletContext.getInitParameterNames();
        if(paramNames == null) {
            return;
        }
        while(paramNames.hasMoreElements()) {
            final String paramName = paramNames.nextElement();
            final String sval = servletContext.getInitParameter(paramName);
            final Object paramValue = AttributeNames.PRODUCTION_MODE.equals(paramName) ?
                    Boolean.valueOf(sval) : sval; 
            servletContext.setAttribute(paramName, paramValue);
        }
    }
        
    @Override
    public void contextDestroyed(ServletContextEvent sce) { 
        
        final AppCore app = (AppCore)sce.getServletContext().getAttribute(AttributeNames.APP);
        
        if(app != null) {
            app.shutdown();
        }
    }
}
