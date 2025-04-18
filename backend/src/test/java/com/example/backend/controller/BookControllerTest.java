package com.example.backend.controller;

import com.example.backend.domain.entity.Book;
import com.example.backend.domain.entity.Status;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest extends ControllerTestBase {

    Book mockBook1 = new Book(1, "book1", Status.AVAILABLE);
    Book mockBook2 = new Book(2, "book2", Status.AVAILABLE);

    @Test
    void getBook_success() throws Exception {
        when(bookService.findAllBooks()).thenReturn(Arrays.asList(mockBook1, mockBook2));
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "id": 1,
                                "title": "book1",
                                "status": "AVAILABLE"
                            },
                            {
                                "id": 2,
                                "title": "book2",
                                "status": "AVAILABLE"
                            }
                        ]
                        """
                ));
        verify(bookService, times(1)).findAllBooks();
    }

    @Test
    void getBookById_success() throws Exception {
        when(bookService.findById(1)).thenReturn(mockBook1);
        mockMvc.perform(get("/books/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "title": "book1",
                            "status": "AVAILABLE"
                        }
                        """
                ));
        verify(bookService, times(1)).findById(1);
    }

    @Test
    void getBookById_fail() throws Exception {
        when(bookService.findById(1)).thenReturn(mockBook1);
        mockMvc.perform(get("/books/{id}", 0))
                .andExpect(status().isBadRequest());
        verify(bookService, times(0)).findById(1);
    }

    @Test
    void addBook_success() throws Exception {
        doNothing().when(bookService).insertBook(any());
        var requestBody = """
                {
                    "title": "test"
                }
                """;

        mockMvc.perform(
                post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())
        ).andExpect(status().isNoContent());
        verify(bookService, times(1)).insertBook(any());
    }

    @Test
    void addBook_fail() throws Exception {
        doNothing().when(bookService).insertBook(any());
        mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(status().isBadRequest());
        verify(bookService, times(0)).insertBook(any());
    }

    @Test
    void deleteBookById_success() throws Exception {
        when(bookService.deleteById(1)).thenReturn(1);
        mockMvc.perform(delete("/books/{id}", 1).with(csrf()))
                .andExpect(status().isNoContent());
        verify(bookService, times(1)).deleteById(1);
    }

    @Test
    void deleteBookById_fail() throws Exception {
        mockMvc.perform(delete("/books/{id}", 0).with(csrf()))
                .andExpect(status().isBadRequest());
        verify(bookService, times(0)).deleteById(1);
    }

    @Test
    void deleteBookById_notFound() throws Exception {
        when(bookService.deleteById(100)).thenReturn(0);
        mockMvc.perform(delete("/books/{id}", 100).with(csrf()))
                .andExpect(status().isInternalServerError());
        verify(bookService, times(1)).deleteById(100);
    }

    @Test
    void searchBooks_success() throws Exception {
        var keyword = "ok1";
        when(bookService.search(keyword)).thenReturn(Arrays.asList(mockBook1));
        mockMvc.perform(get("/books/search").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                [
                                    {
                                        "id": 1,
                                        "title": "book1",
                                        "status": "AVAILABLE"
                                    }
                                ]
                                """
                ));
        verify(bookService, times(1)).search(keyword);
    }

    @Test
    void searchBooks_successNoResult() throws Exception {
        var keyword = "nonexistent";
        when(bookService.search(keyword)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/books/search").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookService, times(1)).search(keyword);
    }

    @Test
    void searchBooks_fail() throws Exception {
        var keyword = "";
        when(bookService.search(keyword)).thenReturn(Arrays.asList(mockBook1));
        mockMvc.perform(get("/books/search").param("keyword", keyword))
                .andExpect(status().isBadRequest());
        verify(bookService, times(0)).search(keyword);
    }

    @Test
    void getPopularBooks_success() throws Exception {
        when(bookService.getPopularBooks()).thenReturn(Arrays.asList(mockBook1, mockBook2));
        mockMvc.perform(get("/books/popular"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "id": 1,
                                "title": "book1",
                                "status": "AVAILABLE"
                            },
                            {
                                "id": 2,
                                "title": "book2",
                                "status": "AVAILABLE"
                            }
                        ]
                        """
                ));
        verify(bookService, times(1)).getPopularBooks();
    }

    @Test
    void getPopularBooks_successEmpty() throws Exception {
        when(bookService.getPopularBooks()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/books/popular"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookService, times(1)).getPopularBooks();
    }
}