package lk.ijse.dep8.library.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep8.library.dto.MemberDTO;
import lk.ijse.dep8.library.exception.ValidationException;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

@WebServlet(name = "MemberServlet", value = {"/members/*"})
public class MemberServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool4library")
    private volatile DataSource pool;

    private void doSaveOrUpdate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (req.getContentType() == null ||
                !req.getContentType().toLowerCase().startsWith("application/json")) {
            res.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        String method = req.getMethod();
        String pathInfo = req.getPathInfo();

        if (method.equals("POST") && !(pathInfo == null || pathInfo.equals("/"))){
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }else if (method.equals("PUT") && !(pathInfo != null &&
                pathInfo.substring(1).matches("\\d{9}[Vv][/]?"))){
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Member does not exist");
            return;
        }

        try {
            Jsonb jsonb = JsonbBuilder.create();
            MemberDTO member = jsonb.fromJson(req.getReader(), MemberDTO.class);
            if (method.equals("POST") &&
                    member.getNic() == null || !member.getNic().matches("\\d{9}[Vv]")) {
                throw new ValidationException("Invalid NIC");
            } else if (member.getName() == null || !member.getName().matches("[A-Za-z ]+")) {
                throw new ValidationException("Invalid Name");
            } else if (member.getContact() == null || !member.getContact().matches("\\d{3}-\\d{7}")) {
                throw new ValidationException("Invalid contact number");
            }
        } catch (JsonbException | ValidationException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    (e instanceof JsonbException) ? "Invalid JSON" : e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getContentType() == null ||
                !request.getContentType().toLowerCase().startsWith("application/json")) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        try {
            Jsonb jsonb = JsonbBuilder.create();
            MemberDTO member = jsonb.fromJson(request.getReader(), MemberDTO.class);

            if (member.getNic() == null || !member.getNic().matches("\\d{9}[Vv]")) {
                throw new ValidationException("Invalid NIC");
            } else if (member.getName() == null || !member.getName().matches("[A-Za-z ]+")) {
                throw new ValidationException("Invalid Name");
            } else if (member.getContact() == null || !member.getContact().matches("\\d{3}-\\d{7}")) {
                throw new ValidationException("Invalid contact number");
            }

            try (Connection connection = pool.getConnection()) {
                PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE nic=?");
                stm.setString(1, member.getNic());
                ResultSet rst = stm.executeQuery();
                if (rst.next()) {
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Member already exists");
                    return;
                }

                stm = connection.prepareStatement("INSERT INTO member (nic, name, contact) VALUES (?,?,?)");
                stm.setString(1, member.getNic());
                stm.setString(2, member.getName());
                stm.setString(3, member.getContact());
                if (stm.executeUpdate() != 1) {
                    throw new RuntimeException("Failed to register the member");
                }
                response.setStatus(HttpServletResponse.SC_CREATED);
            }
        } catch (JsonbException | ValidationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    (e instanceof JsonbException) ? "Invalid JSON" : e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null ||
                !req.getPathInfo().substring(1).matches("\\d{9}[Vv][/]?")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Member does not exist");
            return;
        } else if (req.getContentType() == null ||
                !req.getContentType().toLowerCase(Locale.ROOT).startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

//        try{
//            Jsonb jsonb = JsonbBuilder.create();
//            MemberDTO member = jsonb.fromJson(req.getReader(), MemberDTO.class);
//        }catch (JsonbException e){
//            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
//        }

        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE nic=?");
            stm.setString(1, req.getPathInfo().substring(1));
            ResultSet rst = stm.executeQuery();

            if (!rst.next()) {
                doPost(req, resp);
            } else {

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
