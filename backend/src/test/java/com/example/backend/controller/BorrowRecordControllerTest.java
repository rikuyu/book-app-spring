package com.example.backend.controller;

import com.example.backend.domain.entity.BorrowRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BorrowRecordController.class)
class BorrowRecordControllerTest extends ControllerTestBase {

    BorrowRecord mockBorrowRecord1 = new BorrowRecord(1, 1, 1, null, null);
    BorrowRecord mockBorrowRecord2 = new BorrowRecord(2, 2, 2, null, null);

    @Test
    void getBorrowRecord_success() throws Exception {
        when(borrowRecordService.findAllBorrowRecords()).thenReturn(Arrays.asList(mockBorrowRecord1, mockBorrowRecord2));
        mockMvc.perform(get("/borrow_records"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                [
                                    {
                                        "id": 1,
                                        "user_id": 1,
                                        "book_id": 1,
                                        "borrowed_date": null,
                                        "returned_date": null
                                    },
                                    {
                                        "id": 2,
                                        "user_id": 2,
                                        "book_id": 2,
                                        "borrowed_date": null,
                                        "returned_date": null
                                    }
                                ]
                        """));

        verify(borrowRecordService, times(1)).findAllBorrowRecords();
    }

    @Test
    void getBookRecordsByUserId_success() throws Exception {
        when(borrowRecordService.findByUserId(1)).thenReturn(Collections.singletonList(mockBorrowRecord1));
        mockMvc.perform(get("/borrow_records/users").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                [
                                    {
                                        "id": 1,
                                        "user_id": 1,
                                        "book_id": 1,
                                        "borrowed_date": null,
                                        "returned_date": null
                                    }
                                ]
                        """));
        verify(borrowRecordService, times(1)).findByUserId(1);
    }

    @Test
    void getBookRecordsByUserId_fail() throws Exception {
        when(borrowRecordService.findByUserId(1)).thenReturn(Collections.singletonList(mockBorrowRecord1));
        mockMvc.perform(get("/borrow_records/users").param("id", "0"))
                .andExpect(status().isBadRequest());
        verify(borrowRecordService, times(0)).findByUserId(1);
    }

    @Test
    void getBookRecordsByBookId_success() throws Exception {
        when(borrowRecordService.findByBookId(1)).thenReturn(Collections.singletonList(mockBorrowRecord1));
        mockMvc.perform(get("/borrow_records/books").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                [
                                    {
                                        "id": 1,
                                        "user_id": 1,
                                        "book_id": 1,
                                        "borrowed_date": null,
                                        "returned_date": null
                                    }
                                ]
                        """));
        verify(borrowRecordService, times(1)).findByBookId(1);
    }

    @Test
    void borrowBook_success() throws Exception {
        when(borrowRecordService.insertBorrowRecordIfAvailable(any())).thenReturn(true);

        var requestBody = """
                {
                    "user_id": 1,
                    "book_id": 1
                }
                """;

        mockMvc.perform(
                post("/borrow_records")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        ).andExpect(status().isNoContent());

        verify(borrowRecordService, times(1)).insertBorrowRecordIfAvailable(any());
    }

    @Test
    void borrowBook_fail() throws Exception {
        when(borrowRecordService.insertBorrowRecordIfAvailable(any())).thenReturn(true);
        mockMvc.perform(
                post("/borrow_records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        ).andExpect(status().isBadRequest());
        verify(borrowRecordService, times(0)).insertBorrowRecordIfAvailable(any());
    }

    @Test
    void returnBook_success() throws Exception {
        when(borrowRecordService.returnBook(1, 1)).thenReturn(true);
        mockMvc.perform(
                put("/borrow_records/{borrow_record}/books/{book_id}", 1, 1).with(csrf())
        ).andExpect(status().isNoContent());

        verify(borrowRecordService, times(1)).returnBook(1, 1);
    }

    @Test
    void returnBook_fail_bookId() throws Exception {
        when(borrowRecordService.returnBook(1, 1)).thenReturn(true);
        mockMvc.perform(
                put("/borrow_records/{borrow_record}/books/{book_id}", 0, 1).with(csrf())
        ).andExpect(status().isBadRequest());

        verify(borrowRecordService, times(0)).returnBook(0, 1);
    }

    @Test
    void returnBook_fail_borrowRecordId() throws Exception {
        when(borrowRecordService.returnBook(1, 1)).thenReturn(true);
        mockMvc.perform(
                put("/borrow_records/{borrow_record}/books/{book_id}", 1, 0).with(csrf())
        ).andExpect(status().isBadRequest());

        verify(borrowRecordService, times(0)).returnBook(1, 0);
    }
}