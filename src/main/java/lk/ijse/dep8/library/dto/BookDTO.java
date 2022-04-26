package lk.ijse.dep8.library.dto;

import java.io.Serializable;
import java.util.Arrays;

public class BookDTO implements Serializable {
    private String isbn;
    private String name;
    private String author;
    private byte[] preview;

    public BookDTO() {
    }

    public BookDTO(String isbn, String name, String author) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
    }

    public BookDTO(String isbn, String name, String author, byte[] preview) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.preview = preview;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public byte[] getPreview() {
        return preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "isbn='" + isbn + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", preview=" + Arrays.toString(preview) +
                '}';
    }
}
