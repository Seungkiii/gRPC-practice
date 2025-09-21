package com.example.practicegrpc.domain.reviews.service;

import bookstore.Bookstore;
import bookstore.ReviewServciceGrpc;
import com.example.practicegrpc.domain.reviews.entity.Review;
import com.example.practicegrpc.global.util.TimestampConverter;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class ReviewGrpcService extends ReviewServciceGrpc.ReviewServciceImplBase {
    private final ReviewService reviewService;

    @Autowired
    public ReviewGrpcService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void getReviews(Bookstore.GetReviewsRequest request, StreamObserver<Bookstore.Review> responseObserver) {
        List<Review> reviews = reviewService.findByBookId(request.getBookId());

        for (Review review : reviews) {
            responseObserver.onNext(
                    Bookstore.Review.newBuilder()
                            .setId(review.getId())
                            .setBookId(review.getBook().getId())
                            .setRating(review.getRating())
                            .setCreatedDate(TimestampConverter.toProto(review.getCreatedDate()))
                            .setContent(review.getContent())
                            .build()
            );
        }

        responseObserver.onCompleted();
    }
}
