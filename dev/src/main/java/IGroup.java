import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IGroup extends IComponent {

    @EqualsKey
    Long getId();

    void setId(Long id);

    Long getUserId();

    void setUserId(Long userId);

    String getName();

    void setName(String name);

}
