package com.church.bulletin.repository;

import com.church.bulletin.entity.BulletinPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulletinPageRepository extends JpaRepository<BulletinPage, Long> {
    List<BulletinPage> findAllByOrderByPageNumberAsc();
}
