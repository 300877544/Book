package com.dexlock.book.services;

import com.dexlock.book.model.Book;
import com.dexlock.book.model.FileDB;
import com.dexlock.book.model.User;
import com.dexlock.book.repository.BookRepository;
import com.dexlock.book.repository.FileRepository;
import com.dexlock.book.repository.IBookService;
import com.dexlock.book.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class UserService implements IBookService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FileRepository fileRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public User storeUser(User user){

       // user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);}

    public Book storeBook(Book book){return bookRepository.save(book);}

    public Book updateBook(Book book, Long bookId) {
        Book existingBook = bookRepository.findById(bookId).orElse(null);
        existingBook.setTitle(book.getTitle());
        existingBook.setGenre(book.getGenre());
        existingBook.setSynopsis(book.getSynopsis());
        existingBook.setAuthor(book.getAuthor());
        return bookRepository.save(existingBook);

    }

    public String deleteBookId(Long id)
    {
        bookRepository.deleteById(id);
        return + id + " Book is deleted";
    }

    public FileDB storeFile(FileDB fileDB) throws IOException {
        return fileRepository.save(fileDB);

    }

    public FileDB getFile(String id) {
        return fileRepository.findById(id).get();
    }

    public Stream<FileDB> getAllFiles() {
        return fileRepository.findAll().stream();
    }

    @Override
    public List<Book> findPaginated(int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo,pageSize);
        Page<com.dexlock.book.model.Book> pagedResult = bookRepository.findAll(paging);
         return pagedResult.toList();
    }


}
