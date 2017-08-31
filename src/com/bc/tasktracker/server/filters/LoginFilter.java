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
package com.bc.tasktracker.server.filters;

import com.bc.appcore.AppCore;
import com.bc.appcore.User;
import com.bc.tasktracker.server.AttributeNames;
import com.bc.tasktracker.server.JspPages;
import com.bc.tasktracker.server.exceptions.LoginException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * @author Josh
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = {
    '/'+JspPages.SEARCHRESULTS, 
    '/'+JspPages.ADDTASK,
    '/'+JspPages.ADDRESPONSE,
    "/search", 
    "/editsearchresults",
    "/addtask",
    "/addresponse"
})
public class LoginFilter extends BaseFilter {
    
    public LoginFilter() { }    

    @Override
    protected void doBeforeProcessing(
            ServletRequest request, ServletResponse response) 
            throws IOException, ServletException {
        final AppCore app = (AppCore)request.getServletContext().getAttribute(AttributeNames.APP);
        final User user = app.getUser();
        if(!user.isLoggedIn()) {
            final String msg = "Login required";
            throw new LoginException(msg);
        }
    }
}
