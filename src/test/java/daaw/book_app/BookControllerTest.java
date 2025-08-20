package daaw.book_app;

import java.util.Objects;
import daaw.book_app.persistence.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test.sql",    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String url(String p) {
        return "http://localhost:" + port + p;
    }

    @Test
    void contextLoads() {
        // If the app starts and port is injected, context is fine.
        assertThat(port).isGreaterThan(0);
    }

    @Test
    void getAll_returnsSeededBooks() {
        ResponseEntity<Book[]> resp = rest.getForEntity(url("/api/books"), Book[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody()).extracting(Book::getTitle)
                .contains("To Kill a Mockingbird", "Ulysses");
    }

    @Test
    void create_returns201_and_Location() {
        Book b = new Book();
        b.setTitle("Clean Code");
        b.setAuthor("Robert C. Martin");

        ResponseEntity<Book> resp = rest.postForEntity(url("/api/books"), b, Book.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getHeaders().getLocation()).isNotNull();

        Book saved = Objects.requireNonNull(resp.getBody(), "Body must not be null");
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Clean Code");
        assertThat(saved.getAuthor()).isEqualTo("Robert C. Martin");
    }

    @Test
    void delete_returns204() {
        ResponseEntity<Void> del = rest.exchange(
                url("/api/books/{id}"),
                HttpMethod.DELETE,
                null,
                Void.class,
                1);
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> after = rest.getForEntity(url("/api/books/1"), String.class);
        assertThat(after.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}