import com.google.inject.Inject;
import com.talanlabs.component.IComponent;
import com.talanlabs.entity.IId;
import com.talanlabs.mybatis.component.session.ComponentSqlSessionManager;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
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

    @Inject
    private ComponentSqlSessionManager componentSqlSessionManager;

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
        return componentSqlSessionManager.findById(componentClass, id);
    }

    @Transactional
    public <E extends IComponent> List<E> findChildrenByIdParent(Class<E> componentClass, String propertyName, IId id) {
        return sqlSessionManager.selectList(StatementNameHelper.buildFindComponentsByKey(componentClass, true, new String[] { propertyName }, null), id);
    }

    @Transactional
    public <E extends IComponent> int insert(E component) {
        return componentSqlSessionManager.insert(component);
    }

    @Transactional
    public <E extends IComponent> int update(E component) {
        return componentSqlSessionManager.update(component);
    }

    @Transactional
    public <E extends IComponent> int delete(E component) {
        return componentSqlSessionManager.delete(component);
    }

    @Transactional
    public <E extends IComponent> List<E> findRsql(Class<E> componentClass, String rsql) {
        return sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(componentClass), rsql);
    }
}
