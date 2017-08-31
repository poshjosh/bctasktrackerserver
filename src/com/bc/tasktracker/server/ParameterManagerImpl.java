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
import com.bc.appcore.util.TextHandler;
import com.bc.tasktracker.server.AttributeNames;
import com.bc.tasktracker.server.WebApp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 1:41:25 PM
 */
public class ParameterManagerImpl implements ParameterManager {

    @Override
    public void addParameter(HttpServletRequest request, 
            String name, Map<String, Object> params, boolean required) 
            throws ParameterNotFoundException {
        final String value = request.getParameter(name);
        if(required && value == null) {
            throw new ParameterNotFoundException(name);
        }
        if(value != null) {
            params.put(name, value);
        }
    }
    
    @Override
    public void addBooleanParameter(HttpServletRequest request, 
            String name, Map<String, Object> params, boolean required) 
            throws ParameterException {
        final String value = request.getParameter(name);
        if(required && value == null) {
            throw new ParameterNotFoundException(name);
        }
        if(value != null) {
            final Boolean bool = this.parse(this.getApp(request), Boolean.class, name, value);
            params.put(name, bool);
        }
    }
    
    @Override
    public void addIntParameter(HttpServletRequest request, 
            String name, Map<String, Object> params, boolean required) 
            throws ParameterException {
        final String value = request.getParameter(name);
        if(required && value == null) {
            throw new ParameterNotFoundException(name);
        }
        if(value != null) {
            final Integer integer = this.parse(this.getApp(request), Integer.class, name, value);
            params.put(name, integer);
        }
    }
    
    @Override
    public void addDateParameter(HttpServletRequest request, 
            String name, Map<String, Object> params, boolean required) 
            throws ParameterException {
        final String value = request.getParameter(name);
        if(required && value == null) {
            throw new ParameterNotFoundException(name);
        }
        if(value != null) {
            final WebApp app = this.getApp(request);
            final Date date = this.parse(app, Date.class, name, value);
            params.put(name, date);
        }
    }
    
    @Override
    public <T> T parse(WebApp app, Class<T> type, String name, String value) throws InvalidParameterException {
        final Object output;
        try{
            if(type.equals(String.class)) {
                output = value;
            }else if(type.equals(Boolean.class)) {
                output = Boolean.parseBoolean(value);
            }else if(type.equals(Integer.class)) {
                output = Integer.parseInt(value);
            }else if(Date.class.isAssignableFrom(type)) {
                output = this.toDate(app, name, value);
            }else{
                output = value;
            }
            return (T)output;
        }catch(ParseException | RuntimeException e) {
            throw new InvalidParameterException(name + '=' + value, e);
        }
    }
    
    @Override
    public Date toDate(WebApp app, String name, String value) throws ParseException {
        Date date;
        final DateFormat dateFormat = app.getDateFormat();
        try{
            date = dateFormat.parse(value);
        }catch(ParseException e) {
            final TextHandler textHandler = app.getOrException(TextHandler.class).of(dateFormat);
            date = textHandler.getDate(value);
            if(date == null) {
                throw e;
            }
        }
        return date;
    }
    
    public WebApp getApp(HttpServletRequest request) {
        final WebApp app = (WebApp)request.getServletContext().getAttribute(AttributeNames.APP);
        return app;
    }
}
