package bookshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Date;
import java.util.List;

 @RestController
 public class BookController {
        @Autowired
        BookRepository bookRepository;

@RequestMapping(value = "/chkAndModifyStock",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8")

public boolean modifyStock(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
                boolean status = false;
                Long bookId = Long.valueOf(request.getParameter("bookId"));
                int qty = Integer.parseInt(request.getParameter("qty"));

                Book book = bookRepository.findByBookId(bookId);

                if(book != null){
                        if (book.getStock() >= qty) {
                                book.setStock(book.getStock() - qty);
                                bookRepository.save(book);
                                status = true;
                        }
                }

                return status;
        }

 }
