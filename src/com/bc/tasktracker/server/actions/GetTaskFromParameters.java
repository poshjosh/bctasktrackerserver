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

import com.bc.appcore.AppCore;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.jpa.entities.master.Task_;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 29, 2017 11:38:09 PM
 */
public class GetTaskFromParameters implements Function<Map<String, Object>, Task>{

    private final AppCore app;

    public GetTaskFromParameters(AppCore app) {
        this.app = Objects.requireNonNull(app);
    }
    
    @Override
    public Task apply(Map<String, Object> params) {
        try{
            return this.execute(params);
        }catch(ParameterNotFoundException e) {
            throw new NullPointerException("Task / taskid == NULL");
        }
    }
    
    public Task execute(Map<String, Object> params) throws ParameterNotFoundException {
        final Object idOval = params.get(Task_.taskid.getName());
        final Integer taskid = idOval == null ? null : 
                idOval instanceof Integer ? (Integer)idOval : Integer.parseInt(idOval.toString());
        final Task task = taskid == null ? (Task)params.get(Task.class.getName()) :
                app.getActivePersistenceUnitContext().getDao().findAndClose(Task.class, taskid);
        if(task == null) {
            throw new ParameterNotFoundException(Task.class.getName());
        }
        return task;
    }
}
