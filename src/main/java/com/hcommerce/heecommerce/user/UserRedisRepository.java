package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.redis.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRedisRepository extends CrudRepository<User, String> {

}
