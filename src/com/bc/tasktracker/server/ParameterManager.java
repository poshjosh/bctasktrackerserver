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

import com.bc.appcore.parameter.InvalidParameterException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.bc.tasktracker.server.WebApp;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 1:44:23 PM
 */
public interface ParameterManager {

    void addBooleanParameter(HttpServletRequest request, String name, Map<String, Object> params, boolean required) throws ParameterException;

    void addDateParameter(HttpServletRequest request, String name, Map<String, Object> params, boolean required) throws ParameterException;

    void addIntParameter(HttpServletRequest request, String name, Map<String, Object> params, boolean required) throws ParameterException;

    void addParameter(HttpServletRequest request, String name, Map<String, Object> params, boolean required) throws ParameterNotFoundException;

    <T> T parse(WebApp app, Class<T> type, String name, String value) throws InvalidParameterException;

    Date toDate(WebApp app, String name, String value) throws ParseException;

}
