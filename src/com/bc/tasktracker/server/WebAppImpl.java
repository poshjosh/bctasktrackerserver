package com.bc.tasktracker.server;

import com.bc.appcore.AbstractAppCore;
import com.bc.appcore.AppContext;
import com.bc.appcore.ObjectFactory;
import com.bc.appcore.parameter.ParametersBuilder;
import com.bc.tasktracker.jpa.TasktrackerSearchContext;
import com.bc.tasktracker.jpa.TasktrackerSearchContextImpl;
import java.util.Objects;
import javax.servlet.ServletContext;
import com.bc.appcore.jpa.model.EntityResultModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 12, 2017 9:49:54 PM
 */
public class WebAppImpl extends AbstractAppCore implements WebApp {

    private final ServletContext servletContext;
    
    public WebAppImpl(AppContext appContext, ServletContext sc) {
        super(appContext);
        this.servletContext = Objects.requireNonNull(sc);
    }

    @Override
    protected ObjectFactory createObjectFactory() {
        return new WebAppObjectFactory(this);
    }

    @Override
    public <T> TasktrackerSearchContext<T> getSearchContext(Class<T> entityType) {
        final EntityResultModel resultModel = this.getResultModel(entityType, null);
        return new TasktrackerSearchContextImpl<>(this, Objects.requireNonNull(resultModel));
    }
    
    @Override
    public <S> ParametersBuilder<S> getParametersBuilder(S source, String actionCommand) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
}
