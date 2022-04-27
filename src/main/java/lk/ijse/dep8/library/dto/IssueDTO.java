package lk.ijse.dep8.library.dto;

import jakarta.json.bind.annotation.JsonbDateFormat;

import java.io.Serializable;
import java.sql.Date;

public class IssueDTO implements Serializable {
    private int id;
    private String nic;
    private String isbn;
    @JsonbDateFormat("yyyy-MM-dd")
    private Date date;

    public IssueDTO() {
    }

    public IssueDTO(String nic, String isbn) {
        this.nic = nic;
        this.isbn = isbn;
    }

    public IssueDTO(int id, String nic, String isbn, Date date) {
        this.id = id;
        this.nic = nic;
        this.isbn = isbn;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "IssueDTO{" +
                "id=" + id +
                ", nic='" + nic + '\'' +
                ", isbn='" + isbn + '\'' +
                ", date=" + date +
                '}';
    }
}
