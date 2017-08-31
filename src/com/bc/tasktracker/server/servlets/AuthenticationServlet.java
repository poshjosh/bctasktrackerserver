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
import com.bc.tasktracker.server.exceptions.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 16, 2017 2:39:22 AM
 */
public abstract class AuthenticationServlet extends BaseServlet {

    private transient static final Logger logger = Logger.getLogger(AuthenticationServlet.class.getName());
    
    private final String id;
    
    public AuthenticationServlet(String id) {
        this.id = Objects.requireNonNull(id);
    }
    
    protected abstract boolean execute(User user, Map params) 
            throws javax.security.auth.login.LoginException;

    @Override
    public void doProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        response.setContentType("text/html;charset=UTF-8");
        
        final AppCore app = (AppCore)request.getServletContext().getAttribute(AttributeNames.APP);

        final User user = app.getUser();
        
        if(user.isLoggedIn()) {
            
            final String msg = "You are already logged in as: " + user.getName();

            throw new LoginException(msg);
            
        }else{
            
            String username = request.getParameter("username");
            if(username == null || username.isEmpty()) {
                username = request.getParameter("username_option");
                if(username == null || username.isEmpty()) {
                    throw new LoginException("Please enter a username");
                }
            }

            final String password = request.getParameter("password");
            if(password == null || password.isEmpty()) {
                throw new LoginException("Please enter a password");
            }

            try{

                final Map details = new HashMap(4, 0.75f);
                details.put("username", username);
                details.put("password", password.toCharArray());

                if(this.execute(user, details)) {

                    request.setAttribute(AttributeNames.USER_MESSAGE, this.toTitleCase(id) + " Successful");

                    request.getRequestDispatcher(this.getSuccessPage(request, user)).forward(request, response);

                }else{

                    final String msg = this.toTitleCase(id) + " Error.";

                    throw new LoginException(msg);
                }

            }catch(javax.security.auth.login.LoginException e) {

                final String msg = this.toTitleCase(id) + " Error.";

                logger.log(Level.WARNING, msg, e);

                throw new LoginException(msg + ' ' + e.getLocalizedMessage());
            }
        }
    }
    
    private String toTitleCase(String s) {
        return Character.toTitleCase(s.charAt(0)) + s.substring(1);
    }
}
