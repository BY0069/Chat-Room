package Entity;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Mapper类，用于用户信息的持久化存储
 */
public interface UserDAO {

    @Select("select * from user;")
    List<User> getAllUsers();

    @Select("select * from user where username=#{username}")
    User getUserByName(String username);

    @Insert("insert into user(id, username, nickname, password, status) " +
            "values (#{id}, #{username}, #{nickname}, #{password}, #{status})")
    void saveUser(User user);

    @Select("select status from user where username=#{username}")
    String getStatusByUsername(String username);

    @Update("update user set status=#{status} where username=#{username}")
    void setStatus(@Param("username") String username, @Param("status") Status status);

    @Select("select  * from user where status='online'")
    List<User> getAllOnlineUsers();
}
