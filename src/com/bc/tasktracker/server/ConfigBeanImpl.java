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

import com.bc.config.Config;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2017 10:24:42 PM
 */
public class ConfigBeanImpl implements ConfigBean, Serializable {
    
    private String name;
    
    private Config config;

    public ConfigBeanImpl() { }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public String getString() {
        return config.getString(name);
    }
    
    @Override
    public Boolean getBoolean() {
        return config.getBoolean(name);
    }
    
    @Override
    public Double getDouble() {
        return config.getDouble(name);
    }
    
    @Override
    public Long getLong() {
        return config.getLong(name);
    }
    
    @Override
    public Collection getCollection() {
        return config.getCollection(name, Collections.EMPTY_SET);
    }
    
    @Override
    public Calendar getCalendar() {
        try{
            return config.getTime(name);
        }catch(ParseException e) {
            return null;
        }
    }
}
