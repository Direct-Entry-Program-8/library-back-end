package lk.ijse.dep8.library.api;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "MemberServlet", value = {"/members", "/members/"})
public class MemberServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* 1. Validate the request, if validation fails, we need to send appropriate response errors */
        /* 2. Try to register the member, according to the result, we need to send the response */
    }
}
