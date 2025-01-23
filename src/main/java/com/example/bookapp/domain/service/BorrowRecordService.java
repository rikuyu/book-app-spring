package com.example.bookapp.domain.service;

import com.example.bookapp.domain.entity.BorrowRecord;
import com.example.bookapp.domain.entity.Status;
import com.example.bookapp.infra.mapper.BookMapper;
import com.example.bookapp.infra.mapper.BorrowRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BorrowRecordService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;

    @Autowired
    public BorrowRecordService(
            BorrowRecordMapper borrowRecordMapper,
            BookMapper bookMapper
    ) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.bookMapper = bookMapper;
    }

    public List<BorrowRecord> findAllBorrowRecords() {
        return borrowRecordMapper.findAllBorrowRecords();
    }

    public List<BorrowRecord> findByUserId(int userId) {
        return borrowRecordMapper.findByUserId(userId);
    }

    public List<BorrowRecord> findByBookId(int bookId) {
        return borrowRecordMapper.findByBookId(bookId);
    }

    @Transactional
    public boolean insertBorrowRecordIfAvailable(BorrowRecord borrowRecord) {
        boolean isBookAvailable = bookMapper.findStatusById(borrowRecord.bookId) == Status.AVAIlABLE;

        if (!isBookAvailable) {
            return false;
        }

        bookMapper.borrowBook(borrowRecord.bookId);
        borrowRecordMapper.insertBorrowRecord(borrowRecord);
        return true;
    }

    @Transactional
    public void returnBook(int borrowRecordId, int bookId) {
        borrowRecordMapper.updateBorrowRecord(borrowRecordId);
        bookMapper.returnBook(bookId);
    }
}
