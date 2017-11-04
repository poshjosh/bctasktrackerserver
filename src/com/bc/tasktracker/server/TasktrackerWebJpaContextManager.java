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

import com.bc.appcore.functions.ExecuteSqlFromScriptFile;
import com.bc.appcore.jpa.JpaContextManagerImpl;
import com.bc.jpa.context.PersistenceUnitContext;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 10:27:17 AM
 */
public class TasktrackerWebJpaContextManager extends JpaContextManagerImpl {

    private static final Logger logger = Logger.getLogger(TasktrackerWebJpaContextManager.class.getName());

    private final ServletContext servletContext;

    public TasktrackerWebJpaContextManager(ServletContext servletContext) {
        this.servletContext = Objects.requireNonNull(servletContext);
    }

    @Override
    public void initDatabaseData(PersistenceUnitContext puContext) {
        
        logger.entering(this.getClass().getName(), "initDatabaseData(PersistenceUnitContext)", puContext.getName());
        
        final String [] suffixes = {"_create_tables.sql", "_insert_into_tables.sql"};
        
        for(String suffix : suffixes) {
            
            final String fname = this.servletContext.getRealPath(
                    "META-INF/sql/" + puContext.getPersistenceUnitName() + suffix);

            if(fname != null) {
                
                final List<Integer> output = new ExecuteSqlFromScriptFile<String>().apply(puContext, fname);
                
                if(output.isEmpty()) {
                    throw new RuntimeException("Failed to execute SQL script: " + fname);
                }
            }
        }
    }
}
