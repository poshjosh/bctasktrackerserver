package com.bc.tasktracker.server.servlets;

import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.tasktracker.server.AttributeNames;
import com.bc.tasktracker.server.ParameterManager;
import com.bc.tasktracker.server.WebApp;
import com.bc.tasktracker.jpa.entities.master.Doc_;
import com.bc.tasktracker.jpa.entities.master.Task_;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 16, 2017 8:52:56 AM
 */
@WebServlet(name = "Addtask", urlPatterns = {"/addtask"})
public class Addtask extends BaseServlet {

    @Override
    public void doProcessRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        final Logger logger = Logger.getLogger(this.getClass().getName());
        
        logger.entering(this.getClass().getName(), "doProcessRequest(HttpServletRequest, HttpServletResponse)");
        
        final Map<String, Object> params = new HashMap<>();
        
        try{
            
            final WebApp app = this.getApp(request);
            
            final ParameterManager pm = app.getOrException(ParameterManager.class);
            
            pm.addIntParameter(request, Doc_.docid.getName(), params, false);
            pm.addParameter(request, Doc_.referencenumber.getName(), params, false);
            pm.addParameter(request, Doc_.subject.getName(), params, true);
            pm.addParameter(request, Task_.description.getName(), params, true);
            pm.addParameter(request, Task_.reponsibility.getName(), params, true);
            pm.addDateParameter(request, Doc_.datesigned.getName(), params, false);
            pm.addDateParameter(request, Task_.timeopened.getName(), params, false);
            
            final Object result = app.getAction(WebApp.ADD_TASK).execute(app, params);
            
            request.setAttribute(AttributeNames.USER_MESSAGE, "Task successfully added");
            
            request.getRequestDispatcher(this.getSuccessPage(request, result)).forward(request, response);
            
        }catch(ParameterException | TaskExecutionException e) {
            
            throw new ServletException(e.getUserMessage(), e);
        }
    }
}
