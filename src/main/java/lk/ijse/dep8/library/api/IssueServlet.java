package lk.ijse.dep8.library.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep8.library.dto.IssueDTO;
import lk.ijse.dep8.library.exception.ValidationException;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

@WebServlet(name = "IssueServlet", value = {"/issues", "/issues/"})
public class IssueServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool4library")
    private volatile DataSource pool;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /* Let's validate the request first */

        /* Validating the content type */
        if (req.getContentType() == null || !req.getContentType().startsWith("application/json")){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        try{
            /* Let's try to convert the JSON into Java Object (IssueDTO) */
            Jsonb jsonb = JsonbBuilder.create();
            IssueDTO issue = jsonb.fromJson(req.getReader(), IssueDTO.class);

            /* Let's validate the nic and isbn */
            if (issue.getNic() == null || !issue.getNic().matches("\\d{9}[Vv]")){
                throw new ValidationException("Invalid NIC");
            }else if (issue.getIsbn() == null || !issue.getIsbn().matches("\\d+")){
                throw new ValidationException("Invalid ISBN");
            }

            try (Connection connection = pool.getConnection()) {

                /* Let's check whether the nic and isbn are within the DB */
                PreparedStatement stm = connection.prepareStatement("SELECT * FROM book INNER JOIN member WHERE nic=? AND isbn=?");
                stm.setString(1, issue.getNic());
                stm.setString(1, issue.getIsbn());
                if (!stm.executeQuery().next()){
                    throw new ValidationException("Invalid NIC or Invalid ISBN");
                }

                /* Let's check the book availability */
                stm = connection.prepareStatement("SELECT * FROM issue WHERE isbn = ?");
                stm.setString(1, issue.getIsbn());
                if (stm.executeQuery().next()){
                    resp.sendError(HttpServletResponse.SC_GONE, "Book is not available");
                }

                /* Let's place the issue */
                issue.setDate(Date.valueOf(LocalDate.now()));
                stm = connection.prepareStatement("INSERT INTO issue (nic, isbn, date) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                stm.setString(1, issue.getNic());
                stm.setString(2, issue.getIsbn());
                stm.setDate(3, issue.getDate());
                if (stm.executeUpdate() != 1){
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to place the issue");
                }
                ResultSet generatedKeys = stm.getGeneratedKeys();
                generatedKeys.next();
                issue.setId(generatedKeys.getInt(1));

                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_CREATED);
                jsonb.toJson(issue, resp.getWriter());
            }
        }catch(JsonbException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
        }catch (ValidationException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }catch (Throwable t){
            t.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
