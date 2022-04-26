package lk.ijse.dep8.library.api;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.InputStream;

@MultipartConfig(location = "/tmp", maxFileSize = 15 * 1024 * 1024)
@WebServlet(name = "BookServlet2", value = "/v2/books/*")
public class BookServlet2 extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doSaveOrUpdate(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doSaveOrUpdate(req,resp);
    }

    private void doSaveOrUpdate(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getContentType() == null ||
                !req.getContentType().toLowerCase().startsWith("multipart/form-data")) {
            res.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        System.out.println(req.getParameter("isbn"));
        System.out.println(req.getPart("name").getSubmittedFileName());
        System.out.println(req.getPart("author").getSubmittedFileName());
        System.out.println(req.getPart("preview").getSubmittedFileName());
    }
}
