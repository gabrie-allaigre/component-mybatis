import com.google.inject.Inject;
import com.synaptix.component.IComponent;
import com.synaptix.entity.IId;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import mapper.UserMapper;
import model.IUser;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSessionManager;
import org.mybatis.guice.transactional.Transactional;

import java.io.IOException;
import java.util.List;

public class FooService {

    @Inject
    private UserMapper userMapper;

    @Inject
    private SqlSessionManager sqlSessionManager;

    @Transactional
    public void init() {
        ScriptRunner scriptRunner = new ScriptRunner(sqlSessionManager.getConnection());
        try {
            scriptRunner.runScript(Resources.getResourceAsReader("init-script.sql"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public IUser findUserById(IId userId) {
        return this.userMapper.findUserById(userId);
    }

    @Transactional
    public IUser findUserByLogin(String login) {
        return this.userMapper.findUserByLogin(login);
    }

    @Transactional
    public <E extends IComponent> E findById(Class<E> componentClass, IId id) {
        return sqlSessionManager.<E>selectOne(StatementNameHelper.buildFindEntityByIdKey(componentClass), id);
    }

    @Transactional
    public <E extends IComponent> List<E> findChildrenByIdParent(Class<E> componentClass, String propertyName, IId id) {
        return sqlSessionManager.<E>selectList(StatementNameHelper.buildFindComponentsByKey(componentClass,true, propertyName), id);
    }
}
