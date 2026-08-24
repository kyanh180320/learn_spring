package repository;

import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Các hàm CRUD cơ bản (save, findById, findAll, deleteById...)
    // đã có sẵn từ JpaRepository, KHÔNG CẦN viết lại.

    // 2. Query Methods (Spring tự động sinh ra SQL dựa theo tên hàm)
    boolean existsByEmail(String email);


    Optional<User> findByUsername(String username);

    // 3. Custom Query bằng JPQL hoặc Native SQL (Nếu hàm phức tạp)
    // @Query("SELECT u FROM User u WHERE u.email = :email")
    // User findUserCustom(@Param("email") String email);
}