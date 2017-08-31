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

import com.bc.appcore.AppCore;
import com.bc.appcore.User;
import com.bc.tasktracker.server.AttributeNames;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Josh
 */
@WebServlet(name = "Logout", urlPatterns = {"/logout"})
public class Logout extends BaseServlet {

    private transient static final Logger logger = Logger.getLogger(Logout.class.getName());

    @Override
    public void doProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        final AppCore app = (AppCore)request.getServletContext().getAttribute(AttributeNames.APP);

        final User user = app.getUser();
        
        user.logout();
        
        request.getRequestDispatcher(this.getSuccessPage(request, user)).forward(request, response);

        request.setAttribute(AttributeNames.USER_MESSAGE, "Logout Successful");
    }
}
