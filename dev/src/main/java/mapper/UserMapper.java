package mapper;

import com.synaptix.entity.IId;
import model.IUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select("SELECT * FROM t_user WHERE id = #{userId}")
    IUser findUserById(@Param("userId") IId userId);

    IUser findUserByLogin(@Param("login") String login);

}
