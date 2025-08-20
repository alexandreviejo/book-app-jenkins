package daaw.book_app.controllers;

import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;

import daaw.book_app.persistence.model.Book;
import daaw.book_app.persistence.repo.BookRepository;
import daaw.book_app.controllers.exception.BookIdMismatchException;
import daaw.book_app.controllers.exception.BookNotFoundException;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    // constructor injection
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public ResponseEntity<List<Book>> findAll() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @GetMapping("/title/{bookTitle}")
    public ResponseEntity<List<Book>> findByTitle(@PathVariable String bookTitle) {
        return ResponseEntity.ok(bookRepository.findByTitle(bookTitle));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findOne(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book, UriComponentsBuilder uriBuilder) {
        book.setId(null); // ignore client-supplied id on create
        Book saved = bookRepository.save(book);

        URI location = uriBuilder.path("/api/books/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) throw new BookNotFoundException();
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new BookIdMismatchException();
        }
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        Book saved = bookRepository.save(book);
        return ResponseEntity.ok(saved);
    }

}
