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

import com.bc.appcore.AppLauncherCore;
import com.bc.appcore.jpa.JpaContextManager;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 21, 2017 10:02:04 PM
 */
public class AppLauncherWeb extends AppLauncherCore<WebApp> {

    private static final Logger logger = Logger.getLogger(AppLauncherWeb.class.getName());

    private ServletContext servletContext;
    
    public AppLauncherWeb() {
        this.enableSync(false);
        this.maxTrials(1);
    }

    @Override
    public String getUserHomeWorkingDirPath(String fname) {
        final String path = Paths.get(System.getProperty("user.home"), fname).toString();
        logger.fine(() -> "User.home working dir: " + path);
        return path;
    }
    
    @Override
    public String getAppMetaInfWorkingDirPath(String fname) {
        final String path = servletContext.getRealPath(Paths.get("META-INF", fname).toString());
        logger.fine(() -> "App META-INF working dir: " + path);
        return path;
    }

    public AppLauncherWeb servletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        return this;
    }
    
    @Override
    public void onStartupException(Throwable t, String description, int exitCode) {
        
        logger.log(Level.SEVERE, description, t);
        
        throw new RuntimeException(description, t);
    }

    @Override
    public JpaContextManager getJpaContextManager() {
        Objects.requireNonNull(servletContext);
        return new TasktrackerWebJpaContextManager(servletContext);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
