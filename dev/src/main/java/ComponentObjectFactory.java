import com.synaptix.component.factory.ComponentFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

public class ComponentObjectFactory extends DefaultObjectFactory {

    @Override
    public <T> T create(Class<T> type) {
        if (ComponentFactory.getInstance().isComponentType(type)) {
            return ComponentFactory.getInstance().createInstance(type);
        }
        return super.create(type);
    }
}
