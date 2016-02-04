import com.google.inject.Inject;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.guice.transactional.Transactional;

import java.io.StringReader;

public class FooService {

    @Inject
    private UserMapper userMapper;

    @Inject
    private SqlSessionFactory sqlSessionFactory;

    public void init() {
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ScriptRunner scriptRunner = new ScriptRunner(sqlSession.getConnection());
        scriptRunner.runScript(new StringReader("DROP TABLE user IF EXISTS;\nCREATE TABLE user (id INT,login VARCHAR(256));\nINSERT INTO USER (id,login) VALUES (1,'gabriel');\n"
                + "INSERT INTO USER (id,login) VALUES (2,'sandra');"));

        sqlSession.commit();
        sqlSession.close();
    }

    @Transactional
    public User findUserById(String userId) {
        return this.userMapper.findUserById(userId);
    }

    @Transactional
    public IUser findUserByLogin(String login) {
        return this.userMapper.findUserByLogin(login);
    }

}
