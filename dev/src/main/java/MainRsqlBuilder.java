import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;

public class MainRsqlBuilder {

    public static void main(String[] args) {
        System.out.println(RsqlBuilder.newBuilder().bool("name").isTrue().query());
    }
}
