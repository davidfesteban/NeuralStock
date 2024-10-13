package dev.misei.einfachml.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ResponseUtil {
    public static <T> ResponseEntity<T> entityResponse(Supplier<T> supplier) {
        try {
            return ResponseEntity.ok(supplier.get());
        } catch (Exception e) {
            return responseEntityFailed(e);
        }
    }

    public static <T> ResponseEntity<T> responseEntityFailed(Throwable e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Error-Message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .headers(headers)
                .build();
    }

    //public static <T> CompletableFuture<T> completableFuture(Supplier<T> supplier) {
    //    try {
    //        return CompletableFuture.completedFuture(supplier.get());
    //    } catch (Exception e) {
    //        HttpHeaders headers = new HttpHeaders();
    //        headers.add("Error-Message", e.getMessage());
//
    //        return ResponseEntity
    //                .status(HttpStatus.I_AM_A_TEAPOT)
    //                .headers(headers)
    //                .build();
    //    }
    //}
}
