package weatherproject.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import weatherproject.userservice.entity.TGUser;

@Repository
public interface UserRepository extends JpaRepository<TGUser, Long> {
}
