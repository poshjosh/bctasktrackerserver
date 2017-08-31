package com.bc.tasktracker.server.filters;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Josh
 */
@WebFilter(filterName  = "CacheControlFilter", 
           urlPatterns = {"*.css", "*.CSS", "*.js", "*.JS", "*.jpeg", "*.JPEG", "*.jpg", "*.JPG", "*.png", "*.PNG", "*.gif", "*.GIF", "*.ico", "*.htc", "*.HTC"},
           initParams  = { 
               @WebInitParam(name = "Cache-Control", value = "max-age=31536000", description = "no-cache, OR 3600 = 24 hrs    108000 = 7 days    31536000 = 1 year") 
           }
)
public class CacheControlFilter extends BaseFilter {
    
    private transient static final Logger logger = Logger.getLogger(CacheControlFilter.class.getName());

    @Override
    protected void doAfterProcessing(
            ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        
//XLogger.getInstance().entering(this.getClass(), "doAfterProcessing", "");
//long mb4 = Runtime.getRuntime().freeMemory();
//long tb4 = System.currentTimeMillis();
        
        // Set the provided HTTP response parameters
        // In this case our init Parameter is 'cache-control'
        //
        final FilterConfig filterConfig = this.getFilterConfig();
        
        if(filterConfig == null) {
            return;
        }
        
        final String cache_control = filterConfig.getInitParameter("Cache-Control");
        
        logger.log(Level.FINER, "Cache-control: {0}", cache_control);            

        if(cache_control != null) {
            
            ((HttpServletResponse)response).addHeader("Cache-Control", cache_control);
        }
        
//XLogger.getInstance().log(Level.INFO, "Expended. time: {0} millis, memory: {1} bytes", 
//this.getClass(), System.currentTimeMillis()-tb4, Runtime.getRuntime().freeMemory()-mb4);        
    }
}
