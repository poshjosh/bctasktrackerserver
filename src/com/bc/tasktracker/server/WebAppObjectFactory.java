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

import com.bc.tasktracker.TasktrackerCoreObjectFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 1:53:52 PM
 */
public class WebAppObjectFactory extends TasktrackerCoreObjectFactory {

    public WebAppObjectFactory(WebApp app) {
        super(app);
    }

    @Override
    public <T> T doGetOrException(Class<T> type) throws Exception {
        Object output;
        if(type.equals(ParameterManager.class)){
            output = new ParameterManagerImpl();
        }else{
            output = super.doGetOrException(type);
        }  
        return (T)output;
    }

    @Override
    public WebApp getApp() {
        return (WebApp)super.getApp();
    }
}
