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
    public void init(String script) {
        ScriptRunner scriptRunner = new ScriptRunner(sqlSessionManager.getConnection());
        scriptRunner.setLogWriter(null);
        try {
            scriptRunner.runScript(Resources.getResourceAsReader(script));
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
        return sqlSessionManager.<E>selectList(StatementNameHelper.buildFindComponentsByKey(componentClass, true, propertyName), id);
    }

    @Transactional
    public <E extends IComponent> int insert(Class<E> componentClass, E component) {
        return sqlSessionManager.insert(StatementNameHelper.buildInsertKey(componentClass), component);
    }

    @Transactional
    public <E extends IComponent> int update(Class<E> componentClass, E component) {
        return sqlSessionManager.update(StatementNameHelper.buildUpdateKey(componentClass), component);
    }

    @Transactional
    public <E extends IComponent> int delete(Class<E> componentClass, E component) {
        return sqlSessionManager.update(StatementNameHelper.buildDeleteKey(componentClass), component);
    }
}
