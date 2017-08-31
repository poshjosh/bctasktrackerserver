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

import com.bc.appcore.User;
import java.util.Map;
import javax.security.auth.login.LoginException;
import javax.servlet.annotation.WebServlet;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 16, 2017 2:36:40 AM
 */
@WebServlet(name = "Newuser", urlPatterns = {"/newuser"})
public class Newuser extends AuthenticationServlet {

    public Newuser() {
        super("New User");
    }

    @Override
    protected boolean execute(User user, Map details) throws LoginException {
        return user.create(details);
    }
}
