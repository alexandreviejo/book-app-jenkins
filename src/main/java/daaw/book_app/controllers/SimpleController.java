package daaw.book_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import daaw.book_app.config.AppProps;
import daaw.book_app.persistence.model.Book;
import daaw.book_app.persistence.repo.BookRepository;

@Controller
public class SimpleController {

    private final AppProps appProps;
    private final BookRepository bookRepository;

    // Spring injects AppProps and BookRepository automatically (constructor injection)
    public SimpleController(BookRepository bookRepository, AppProps appProps) {
        this.bookRepository = bookRepository;
        this.appProps = appProps;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appProps.getName());
        model.addAttribute("version", appProps.getVersion());
        return "home";
    }

    @GetMapping("/listbooks")
    public String listBooks(Model model) {
        Iterable<Book> books = bookRepository.findAll();

        model.addAttribute("books", books);

        return "listbooks";
    }

}
