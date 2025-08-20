package daaw.book_app.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
    }

    @ExceptionHandler(BookIdMismatchException.class)
    public ResponseEntity<String> handleIdMismatch(BookIdMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Book ID mismatch");
    }
}