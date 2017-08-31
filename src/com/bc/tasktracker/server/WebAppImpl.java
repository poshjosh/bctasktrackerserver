package com.bc.tasktracker.server;

import com.bc.appcore.AbstractAppCore;
import com.bc.appcore.AppContext;
import com.bc.appcore.ObjectFactory;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.parameter.ParametersBuilder;
import com.bc.tasktracker.ConfigNames;
import com.bc.tasktracker.jpa.TasktrackerSearchContext;
import com.bc.tasktracker.jpa.TasktrackerSearchContextImpl;
import com.bc.tasktracker.jpa.entities.master.Doc_;
import com.bc.tasktracker.jpa.entities.master.Task_;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.servlet.ServletContext;

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
    public List<String> getTaskColumnNames() {
        return Arrays.asList(
                    this.getConfig().getString(ConfigNames.SERIAL_COLUMNNAME), 
                    Task_.taskid.getName(), 
                    Doc_.subject.getName(), Doc_.referencenumber.getName(),
                    Doc_.datesigned.getName(), Task_.reponsibility.getName(),
                    Task_.description.getName(), Task_.timeopened.getName(),
                    Task_.timeclosed.getName(),
                    "Response 1", "Response 2", "Remarks"
            );
    }

    @Override
    protected ObjectFactory createObjectFactory() {
        return new WebAppObjectFactory(this);
    }

    @Override
    public <T> TasktrackerSearchContext<T> getSearchContext(Class<T> entityType) {
        final ResultModel resultModel = this.getResultModel(entityType, null);
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
