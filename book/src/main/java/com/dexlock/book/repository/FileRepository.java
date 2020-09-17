package com.dexlock.book.repository;

import com.dexlock.book.model.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileDB,String> {
}
