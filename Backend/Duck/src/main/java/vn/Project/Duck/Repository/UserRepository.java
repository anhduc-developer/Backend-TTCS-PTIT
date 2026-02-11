package vn.Project.Duck.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.Project.Duck.Domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
