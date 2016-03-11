import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;

import java.util.HashMap;
import java.util.Map;

public class DefaultNlsColumnHandler implements INlsColumnHandler {

    private String languageCode;

    public DefaultNlsColumnHandler() {
        super();

        this.languageCode = "fra";
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public Object getContext() {
        return languageCode;
    }

    @Override
    public Map<String, Object> getAdditionalParameter(Class<? extends IComponent> componentClass, String propertyName) {
        Map<String, Object> map = new HashMap<>();
        map.put("LANGUAGE_CODE", languageCode);
        return map;
    }

    @Override
    public String buildFindNlsColumn(Class<? extends IComponent> componentClass, String propertyName, Map<String, Object> parameterMap, Map<String, Object> additionalParameters) {
        return "select NVL((select meaning from t_nls where table_name = #{TABLE_NAME} and column_name = #{COLUMN_NAME} and language_code = #{LANGUAGE_CODE} and table_id = #{ID}), (select #{DEFAULT_VALUE} from (VALUES(0)))) from (VALUES(0))";
    }
}
