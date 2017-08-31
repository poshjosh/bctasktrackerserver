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

package com.bc.tasktracker.server.servlets;

import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.tasktracker.server.AttributeNames;
import com.bc.tasktracker.server.ParameterManager;
import com.bc.tasktracker.server.WebApp;
import com.bc.tasktracker.jpa.entities.master.Task_;
import com.bc.tasktracker.jpa.entities.master.Taskresponse_;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 16, 2017 12:51:28 PM
 */
@WebServlet(name = "Addresponse", urlPatterns = {"/addresponse"})
public class Addresponse extends BaseServlet {

    @Override
    public void doProcessRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try{
            
            final Object result = this.execute(request);
            
            request.setAttribute(AttributeNames.USER_MESSAGE, "Response successfully added");
            
            request.getRequestDispatcher(this.getSuccessPage(request, result)).forward(request, response);
            
        }catch(ParameterException | TaskExecutionException e) {
            
            throw new ServletException(e.getUserMessage());
        }
    }

    public Object execute(HttpServletRequest request) 
        throws TaskExecutionException, ParameterException {
        
        final Map<String, Object> params = new HashMap<>();
        
        final WebApp app = this.getApp(request);

        final ParameterManager pm = app.getOrException(ParameterManager.class);

        pm.addIntParameter(request, Task_.taskid.getName(), params, true);
        pm.addParameter(request, Taskresponse_.response.getName(), params, true);
        pm.addDateParameter(request, Taskresponse_.deadline.getName(), params, false);

        final Object result = app.getAction(WebApp.ADD_TASKRESPONSE).execute(app, params);
        
        return result;
}
}
