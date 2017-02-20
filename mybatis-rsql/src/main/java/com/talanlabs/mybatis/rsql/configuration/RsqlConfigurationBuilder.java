package com.talanlabs.mybatis.rsql.configuration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.ILikePolicy;
import com.talanlabs.mybatis.rsql.engine.INlsColumnRsqlHandler;
import com.talanlabs.mybatis.rsql.engine.IStringPolicy;
import com.talanlabs.mybatis.rsql.engine.orderby.ComponentSortVisitor;
import com.talanlabs.mybatis.rsql.engine.orderby.registry.DefaultSortDirectionManagerRegistry;
import com.talanlabs.mybatis.rsql.engine.orderby.registry.ISortDirectionManagerRegistry;
import com.talanlabs.mybatis.rsql.engine.policy.DefaultLikePolicy;
import com.talanlabs.mybatis.rsql.engine.policy.NothingStringPolicy;
import com.talanlabs.mybatis.rsql.engine.where.ComponentRsqlVisitor;
import com.talanlabs.mybatis.rsql.engine.where.registry.DefaultComparisonOperatorManagerRegistry;
import com.talanlabs.mybatis.rsql.engine.where.registry.IComparisonOperatorManagerRegistry;
import com.talanlabs.mybatis.rsql.sort.SortParser;
import com.talanlabs.mybatis.rsql.statement.IPageStatementFactory;
import com.talanlabs.rtext.Rtext;
import com.talanlabs.rtext.configuration.RtextConfigurationBuilder;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;

public class RsqlConfigurationBuilder {

    private RsqlConfigurationImpl rsqlConfiguration;

    private RsqlConfigurationBuilder() {
        super();

        this.rsqlConfiguration = new RsqlConfigurationImpl();
    }

    public static RsqlConfigurationBuilder newBuilder() {
        return new RsqlConfigurationBuilder();
    }

    /**
     * Rtext
     *
     * @param rtext Rtext
     */
    public RsqlConfigurationBuilder rtext(Rtext rtext) {
        this.rsqlConfiguration.rtext = rtext;
        return this;
    }

    public RsqlConfigurationBuilder nlsColumnRsqlHandler(INlsColumnRsqlHandler nlsColumnRsqlHandler) {
        this.rsqlConfiguration.nlsColumnRsqlHandler = nlsColumnRsqlHandler;
        return this;
    }

    /**
     * Rsql parser
     *
     * @param rsqlParser a parser
     */
    public RsqlConfigurationBuilder rsqlParser(RSQLParser rsqlParser) {
        this.rsqlConfiguration.rsqlParser = rsqlParser;
        return this;
    }

    /**
     * Sort parser
     *
     * @param sortParser a parser
     */
    public RsqlConfigurationBuilder sortParser(SortParser sortParser) {
        this.rsqlConfiguration.sortParser = sortParser;
        return this;
    }

    /**
     * A comparison operator manager registry
     *
     * @param comparisonOperatorManagerRegistry a comparison operator registry
     */
    public RsqlConfigurationBuilder comparisonOperatorManagerRegistry(IComparisonOperatorManagerRegistry comparisonOperatorManagerRegistry) {
        this.rsqlConfiguration.comparisonOperatorManagerRegistry = comparisonOperatorManagerRegistry;
        return this;
    }

    /**
     * A sort direction manager registry
     *
     * @param sortDirectionManagerRegistry a sort direction registry
     */
    public RsqlConfigurationBuilder sortDirectionManagerRegistry(ISortDirectionManagerRegistry sortDirectionManagerRegistry) {
        this.rsqlConfiguration.sortDirectionManagerRegistry = sortDirectionManagerRegistry;
        return this;
    }

    /**
     * @param defaultTableName a table name
     */
    public RsqlConfigurationBuilder defaultTableName(String defaultTableName) {
        this.rsqlConfiguration.defaultTableName = defaultTableName;
        return this;
    }

    /**
     * @param defaultJoinPrefix a join prefix name
     */
    public RsqlConfigurationBuilder defaultJoinPrefix(String defaultJoinPrefix) {
        this.rsqlConfiguration.defaultJoinPrefix = defaultJoinPrefix;
        return this;
    }

    /**
     * @param defaultParamPrefix a param name prefix
     */
    public RsqlConfigurationBuilder defaultParamPrefix(String defaultParamPrefix) {
        this.rsqlConfiguration.defaultParamPrefix = defaultParamPrefix;
        return this;
    }

    /**
     * @param stringPolicy a string policy
     */
    public RsqlConfigurationBuilder stringPolicy(IStringPolicy stringPolicy) {
        this.rsqlConfiguration.stringPolicy = stringPolicy;
        return this;
    }

    /**
     * @param likePolicy a like policy (default %)
     */
    public RsqlConfigurationBuilder likePolicy(ILikePolicy likePolicy) {
        this.rsqlConfiguration.likePolicy = likePolicy;
        return this;
    }

    public RsqlConfigurationBuilder pageStatementFactory(IPageStatementFactory pageStatementFactory) {
        this.rsqlConfiguration.pageStatementFactory = pageStatementFactory;
        return this;
    }

    public IRsqlConfiguration build() {
        if (rsqlConfiguration.comparisonOperatorManagerRegistry == null) {
            rsqlConfiguration.comparisonOperatorManagerRegistry = new DefaultComparisonOperatorManagerRegistry(rsqlConfiguration);
        }
        if (rsqlConfiguration.sortDirectionManagerRegistry == null) {
            rsqlConfiguration.sortDirectionManagerRegistry = new DefaultSortDirectionManagerRegistry(rsqlConfiguration);
        }
        return rsqlConfiguration;
    }

    private static class RsqlConfigurationImpl implements IRsqlConfiguration {

        private static final Logger LOG = LogManager.getLogger(RsqlConfigurationImpl.class);

        private Rtext rtext;
        private INlsColumnRsqlHandler nlsColumnRsqlHandler;
        private RSQLParser rsqlParser;
        private SortParser sortParser;
        private IComparisonOperatorManagerRegistry comparisonOperatorManagerRegistry;
        private ISortDirectionManagerRegistry sortDirectionManagerRegistry;
        private String defaultTableName = "t";
        private String defaultJoinPrefix = "j";
        private String defaultParamPrefix = "";
        private IStringPolicy stringPolicy;
        private ILikePolicy likePolicy;
        private IPageStatementFactory pageStatementFactory;

        private Cache<Class<? extends IComponent>, ComponentRsqlVisitor<? extends IComponent>> rsqlCache = CacheBuilder.newBuilder().build();
        private Cache<Class<? extends IComponent>, ComponentSortVisitor<? extends IComponent>> sortCache = CacheBuilder.newBuilder().build();

        private RsqlConfigurationImpl() {
            super();

            this.rtext = new Rtext(RtextConfigurationBuilder.newBuilder().build());
            this.rsqlParser = new RSQLParser();
            this.sortParser = new SortParser();
            this.stringPolicy = new NothingStringPolicy();
            this.likePolicy = new DefaultLikePolicy();
        }

        @Override
        public Rtext getRtext() {
            return rtext;
        }

        @Override
        public INlsColumnRsqlHandler getNlsColumnRsqlHandler() {
            return this.nlsColumnRsqlHandler;
        }

        @Override
        public <E extends IComponent> ComponentRsqlVisitor<E> getComponentRsqlVisitor(Class<E> componentClass) {
            try {
                //noinspection unchecked
                return (ComponentRsqlVisitor<E>) rsqlCache.get(componentClass, () -> new ComponentRsqlVisitor<>(componentClass, comparisonOperatorManagerRegistry));
            } catch (ExecutionException e) {
                LOG.error("Failed to get visitor", e);
            }
            return null;
        }

        @Override
        public <E extends IComponent> ComponentSortVisitor<E> getComponentSortVisitor(Class<E> componentClass) {
            try {
                //noinspection unchecked
                return (ComponentSortVisitor<E>) sortCache.get(componentClass, () -> new ComponentSortVisitor<>(componentClass, sortDirectionManagerRegistry));
            } catch (ExecutionException e) {
                LOG.error("Failed to get visitor", e);
            }
            return null;
        }

        @Override
        public RSQLParser getRsqlParser() {
            return rsqlParser;
        }

        @Override
        public SortParser getSortParser() {
            return sortParser;
        }

        @Override
        public EngineContext newEngineContext() {
            return EngineContext.newBulder().defaultTablePrefix(defaultTableName).defaultJoinPrefix(defaultJoinPrefix).defaultParamPrefix(defaultParamPrefix).build();
        }

        @Override
        public IStringPolicy getStringPolicy() {
            return stringPolicy;
        }

        @Override
        public ILikePolicy getLikePolicy() {
            return likePolicy;
        }

        @Override
        public IPageStatementFactory getPageStatementFactory() {
            return pageStatementFactory;
        }
    }
}
