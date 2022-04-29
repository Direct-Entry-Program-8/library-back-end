package lk.ijse.dep8.library.api.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
public class CorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String origin = req.getHeader("Origin");
        if (origin != null && origin.toLowerCase().contains(getServletContext().getInitParameter("origin"))) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            if (req.getMethod().equals("OPTIONS")) {
                res.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, PUT, POST, DELETE, HEAD");
                res.setHeader("Access-Control-Allow-Headers", "Content-Type");
                res.setHeader("Access-Control-Expose-Headers", "Content-Type, X-Count");
            }
        }
        chain.doFilter(req, res);
    }
}
