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

package com.bc.tasktracker.server.actions;

import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.server.WebApp;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 29, 2017 11:42:26 PM
 */
public class CloseTask implements Action<WebApp, Date> {

    private static final Logger logger = Logger.getLogger(CloseTask.class.getName());

    @Override
    public Date execute(WebApp app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final Task task = new GetTaskFromParameters(app).apply(params);
        
        final Date timeclosed = task.getTimeclosed();
        
        final Date output;
        
        if(timeclosed == null) {
            
            output = new Date();
            
            task.setTimeclosed(output);
            
            app.getActivePersistenceUnitContext().getDao().begin().mergeAndClose(task);
            
            logger.fine(() -> "Set timeclosed to: " + output + ", task: " + task);
            
        }else{
            
            output = timeclosed;
            
            logger.fine(() -> "Task already closed, task " + task);
        }
        
        return output;
    }
}
