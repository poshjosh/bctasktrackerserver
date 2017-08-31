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

import com.bc.appcore.jpa.JpaContextManagerImpl;
import com.bc.appcore.sql.script.SqlScriptImporter;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaContextImpl;
import com.bc.tasktracker.jpa.entities.master.Task;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;
import javax.servlet.ServletContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 10:27:17 AM
 */
public class JpaContextManagerWeb extends JpaContextManagerImpl {

    private final ServletContext servletContext;

    public JpaContextManagerWeb(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public JpaContextManagerWeb(ServletContext servletContext, Predicate<String> persistenceUnitTest) {
        super(persistenceUnitTest);
        this.servletContext = servletContext;
    }
    
    @Override
    public void importInitialData(JpaContext jpaContext) {

        final String appId = (String)servletContext.getAttribute(AttributeNames.APP_ID);
        Objects.requireNonNull(appId);
        
        final String resource = "META-INF/"+appId+"/sql/insert.sql";

        final String realPath = servletContext.getRealPath(resource);

        final List<Integer> output = new SqlScriptImporter("utf-8", Level.INFO)
                .executeSqlScript(jpaContext.getEntityManager(Task.class), realPath);

        if(output.isEmpty()) {
            throw new RuntimeException("Failed to execute SQL script: " + realPath);
        }
    }
            
    @Override
    public JpaContext createJpaContext(URI uri) throws IOException {
//                final Function<String, File> getFileForPu = 
//                        new GetPropertiesFileForPersistenceUnit(servletContext.getRealPath(parentDirResource));
        return new JpaContextImpl(uri, null);
    }
}
