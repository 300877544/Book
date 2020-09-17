package com.dexlock.book.controller;

import com.dexlock.book.config.JwtTokenUtil;
import com.dexlock.book.config.JwtUserDetailsService;
import com.dexlock.book.message.JwtRequest;
import com.dexlock.book.message.JwtResponse;
import com.dexlock.book.message.ResponseFile;
import com.dexlock.book.message.ResponseMessage;
import com.dexlock.book.model.Book;
import com.dexlock.book.model.FileDB;
import com.dexlock.book.model.User;
import com.dexlock.book.model.UserDTO;
import com.dexlock.book.repository.BookRepository;
import com.dexlock.book.repository.IBookService;
import com.dexlock.book.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserService service;

    @Autowired
    private IBookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    String jwtToken;
    String username;

    @GetMapping("/hello")
    public String firstPage() {
        return "Hello World";
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
        return ResponseEntity.ok(userDetailsService.save(user));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        System.out.println(userDetails.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
        //author password: authorpassword, admin: apassword, reader:rpassword
        //bookdatabase
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        }
        catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }


        @PostMapping("/user")
    public ResponseEntity<ResponseMessage> addUser(@RequestBody User user, HttpServletRequest request) {
        String message4 = "";
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            jwtToken = requestTokenHeader.substring(7);
            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            if (username.equals("Admin")) {
                user.setPassword(bcryptEncoder.encode(user.getPassword()));
                service.storeUser(user);
                message4 = "User added successfully";
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message4));
            }
            else{
                message4 = "Only Admin can add user";
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage(message4));
            }

        } catch (Exception e) {
            message4 = " Attributes not matching the constraint ";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message4));
        }
    }


    @PostMapping("/book")
    public ResponseEntity<ResponseMessage> addBook(@RequestBody Book book, HttpServletRequest request) {
        String message5 = "";
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            jwtToken = requestTokenHeader.substring(7);
            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            if (username.equals("Admin") || (username.equals("Author"))) {
                service.storeBook(book);
            }
            message5 = "Book added successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message5));
        } catch (Exception e) {
            message5 = " Attributes not matching the constraint ";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message5));
        }
    }

    @PutMapping("/book/{bookId}")
    public ResponseEntity<ResponseMessage> updateBook(@RequestBody Book book, @PathVariable Long bookId, HttpServletRequest request){
        String message7="";
        try{
            final String requestTokenHeader = request.getHeader("Authorization");
            jwtToken = requestTokenHeader.substring(7);
            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            if (username.equals("Admin") || (username.equals("Author"))) {
                service.updateBook(book, bookId);

            }

            message7 = "Book updated successfully";
            return  ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message7));
        }
        catch (Exception e)
        {
            message7= "Bad Request";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message7));
        }
    }

    @PostMapping("{bookId}/addImage")
    public ResponseEntity<ResponseMessage> addImageFile( @RequestParam("file") MultipartFile file, @PathVariable Long bookId, HttpServletRequest request)
    {
        String message;
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            jwtToken = requestTokenHeader.substring(7);
            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            if (username.equals("Admin") || (username.equals("Author"))) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                FileDB fileDB = new FileDB(fileName, file.getContentType(), file.getBytes());
                fileDB.setBookId(bookId);
                service.storeFile(fileDB);
            }
            message = "Image added successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        } catch (IOException e) {
            e.printStackTrace();
            message = "Image not added";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<ResponseFile>> getListFiles(){
        List<ResponseFile> files =  service.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files")
                    .path(dbFile.getId())
                    .toUriString();
            return  new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getData().length);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(files);

    }

    @DeleteMapping("book/{bookId}")
    public ResponseEntity<ResponseMessage> deleteBook(@PathVariable Long bookId, HttpServletRequest request){
        String message1 = "";
        try{
            final String requestTokenHeader = request.getHeader("Authorization");
            jwtToken = requestTokenHeader.substring(7);
            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            if (username.equals("Admin") || (username.equals("Author"))) {
                service.deleteBookId(bookId);
            }
            message1 = "Book deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message1));
        }
        catch (Exception e)
        {
            message1 = " Book not deleted";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message1));
        }
    }

    @GetMapping("/book/{pageNo}/{pageSize}")
    public List<Book> getPaginatedBook(@PathVariable int pageNo, @PathVariable int pageSize){
        return bookService.findPaginated(pageNo, pageSize);
    }
@GetMapping("/bookFindTitle")
    public List<Book> findBookTitles(@RequestParam Optional<String> title){
        return bookRepository.findByTitle(title.orElse("_"));
    }

    @GetMapping("/bookFindGenreOrAuthor")
    public List<Book> findBookGenreOrAuthor(@RequestParam Optional<String> genre, @RequestParam Optional<String> author){
        Optional<List<Book>> optionalBookList =  bookRepository.getTheBooksWithGenreAndAuthor(genre.orElse("_"), author.orElse("_"));
        return optionalBookList.get();
    }

//    Optional<List<Book>> optionalBookList = myBookRepo.getTheBooksWithMultCopies();
//   if (optionalBookList.isPresent()){
//        List<Book> bookList = optionalBookList.get();
//    }
}
