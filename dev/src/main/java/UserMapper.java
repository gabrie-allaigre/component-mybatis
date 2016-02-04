import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{userId}")
    User findUserById(@Param("userId") String userId);

    IUser findUserByLogin(@Param("login") String login);

}
