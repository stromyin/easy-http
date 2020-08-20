package demo.main;


import com.github.firelcw.EasyHttp;
import com.github.firelcw.interceptor.TimeInterceptor;
import demo.interceptor.ResultInterceptor;
import demo.model.Book;
import demo.service.BookHttpService2;

import java.util.List;

/**
 * @author liaochongwei
 * @date 2020/8/3 10:43
 */
public class BookMain2 {
    public static void main(String[] args) {
        BookMain2 main = new BookMain2();
        //main.getBookById();
        main.listBooksByAuthor1();
    }

    void getBookById() {
        BookHttpService2 bookHttpService = EasyHttp.builder()
                                                    .url("127.0.0.1:8888")
                                                    .withInterceptor(new TimeInterceptor())
                                                    .withInterceptor(new ResultInterceptor())
                                                    .build(BookHttpService2.class);
        Book book = bookHttpService.getBookById("323233");
        System.out.println(book);

    }

    void listBooksByAuthor1(){
        BookHttpService2 bookHttpService = EasyHttp.builder()
                .url("127.0.0.1:8888")
                .withInterceptor(new TimeInterceptor())
                .withInterceptor(new ResultInterceptor())
                .build(BookHttpService2.class);
        List<Book> books = bookHttpService.listBooksByAuthor("tom");
        System.out.println(books);
    }

}
