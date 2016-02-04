import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.guice.transactional.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

public class FooService {

    @Inject
    private UserMapper userMapper;

    @Inject
    private SqlSessionFactory sqlSessionFactory;

    public void init() {
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ScriptRunner scriptRunner = new ScriptRunner(sqlSession.getConnection());
        try {
            scriptRunner.runScript(Resources.asCharSource(Resources.getResource("init-script.sql"), Charsets.UTF_8).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
