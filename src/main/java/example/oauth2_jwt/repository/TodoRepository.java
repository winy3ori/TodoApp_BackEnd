package example.oauth2_jwt.repository;

import example.oauth2_jwt.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    List<TodoEntity> findByUserId(Long userId);
    // 추가적인 쿼리 메서드가 필요하면 여기에 작성
}