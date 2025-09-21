package com.example.practicegrpc.domain.books.service;

import bookstore.BookServiceGrpc;
import bookstore.Bookstore;
import com.example.practicegrpc.domain.books.entity.Book;
import com.example.practicegrpc.global.util.TimestampConverter;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class BookGrpcService extends BookServiceGrpc.BookServiceImplBase {

    private final BookService bookService;

    @Autowired
    public BookGrpcService(BookService bookService) {this.bookService = bookService;}

    @Override
    public void addbook(Bookstore.AddBookRequest request, StreamObserver<Bookstore.Book> responseObserver) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setPublisher(request.getPublisher());
        book.setPublishedDate(TimestampConverter.fromProto(request.getPublishedDate()));

        Book savedBook = bookService.saveBook(book);
        responseObserver.onNext(Bookstore.Book.newBuilder()
                .setTitle(book.getTitle())
                .setPublishedDate(TimestampConverter.toProto(savedBook.getPublishedDate()))
                .setPublisher(savedBook.getPublisher())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getbookDetails(Bookstore.GetBookDetailRequest request, StreamObserver<Bookstore.Book> responseObserver) {
        Bookstore.Book bookDetail = bookService.findById(request.getBookId())
                .map(book -> Bookstore.Book.newBuilder()
                        .setTitle(book.getTitle())
                        .setPublishedDate(TimestampConverter.toProto(book.getPublishedDate()))
                        .setPublisher(book.getPublisher())
                        .build())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        responseObserver.onNext(bookDetail);
        responseObserver.onCompleted();
    }

    @Override
    public void listbook(Bookstore.ListBookRequest request, StreamObserver<Bookstore.Book> responseObserver) {
        List<Book> books = bookService.findAll();

        for (Book book : books) {
            responseObserver.onNext(
                    Bookstore.Book.newBuilder()
                            .setTitle(book.getTitle())
                            .setPublishedDate(TimestampConverter.toProto(book.getPublishedDate()))
                            .setPublisher(book.getPublisher())
                            .build()
            );
        }
        responseObserver.onCompleted();
    }

    @Override
    public void searchBooksByAuthor(Bookstore.SearchBooksByAuthorRequest request, StreamObserver<Bookstore.Book> responseObserver) {
        List<Book> books = bookService.findByAuthorName(request.getAuthorName());

        for (Book book : books) {
            responseObserver.onNext(
                    Bookstore.Book.newBuilder()
                            .setTitle(book.getTitle())
                            .setPublishedDate(TimestampConverter.toProto(book.getPublishedDate()))
                            .setPublisher(book.getPublisher())
                            .build()
            );
        }
        responseObserver.onCompleted();
    }
}
