package com.synaptix.mybatis.component.factory;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentBeanMethod;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.executor.loader.WriteReplaceInterface;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ComponentProxyFactory implements org.apache.ibatis.executor.loader.ProxyFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentProxyFactory.class);

    private JavassistProxyFactory javassistProxyFactory = new JavassistProxyFactory();

    public ComponentProxyFactory() {
        super();
    }

    @Override
    public Object createProxy(Object target, ResultLoaderMap lazyLoader, Configuration configuration, ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        if (target != null && ComponentFactory.getInstance().isComponentType(target.getClass()) && ComponentFactory.getInstance().getComponentType((IComponent) target) != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Create Proxy for component=" + ComponentFactory.getInstance().getComponentClass((IComponent) target));
            }
            return _createProxy((IComponent) target, lazyLoader, configuration);
        }
        return javassistProxyFactory.createProxy(target, lazyLoader, configuration, objectFactory, constructorArgTypes, constructorArgs);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    @SuppressWarnings("unchecked")
    private <E extends IComponent> Object _createProxy(E component, ResultLoaderMap lazyLoader, Configuration configuration) {
        Class<E> componentClass = ComponentFactory.getInstance().getComponentClass(component);
        ClassLoader classLoader = componentClass.getClassLoader();
        Class<?>[] interfaces = new Class[] { componentClass, WriteReplaceInterface.class, com.synaptix.component.factory.Proxy.class };
        InvocationHandler proxy = new ComponentInvocationHandler<E>(componentClass, component, lazyLoader, configuration);
        return (E) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

    private class ComponentInvocationHandler<E extends IComponent> implements InvocationHandler {

        private static final String FINALIZE_METHOD = "finalize";

        private static final String WRITE_REPLACE_METHOD = "writeReplace";

        private final ComponentDescriptor componentDescriptor;

        private final E component;

        private final ResultLoaderMap lazyLoader;

        private final boolean aggressive;

        public ComponentInvocationHandler(Class<E> componentClass, E component, ResultLoaderMap lazyLoader, Configuration configuration) {
            super();
            this.componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
            this.component = component;
            this.lazyLoader = lazyLoader;
            this.aggressive = configuration.isAggressiveLazyLoading();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                Object res;
                synchronized (lazyLoader) {
                    if (isWriteReplaceMethod(method)) {
                        lazyLoader.loadAll();
                        res = component;
                    } else {
                        if (lazyLoader.size() > 0 && !isFinalizeMethod(method)) {
                            if (aggressive) {
                                lazyLoader.loadAll();
                            } else {
                                ComponentBeanMethod cbm = componentDescriptor.getComponentBeanMethod(method.toGenericString());
                                if (cbm != null) {
                                    switch (cbm) {
                                    case GET:
                                    case SET: {
                                        String propertyName = cbm.inferName(method);
                                        if (lazyLoader.hasLoader(propertyName)) {
                                            lazyLoader.load(propertyName);
                                        }
                                    }
                                    break;
                                    case STRAIGHT_GET_PROPERTY:
                                    case STRAIGHT_SET_PROPERTY: {
                                        String propertyName = (String) args[0];
                                        if (lazyLoader.hasLoader(propertyName)) {
                                            lazyLoader.load(propertyName);
                                        }
                                    }
                                    break;
                                    case EQUALS:
                                    case HASHCODE:
                                        Set<String> propertyNames = componentDescriptor.getEqualsKeyPropertyNames();
                                        if (propertyNames != null && !propertyNames.isEmpty()) {
                                            for (String propertyName : propertyNames) {
                                                if (lazyLoader.hasLoader(propertyName)) {
                                                    lazyLoader.load(propertyName);
                                                }
                                            }
                                        }
                                        break;
                                    case TO_STRING:
                                    case STRAIGHT_GET_PROPERTIES:
                                    case STRAIGHT_SET_PROPERTIES:
                                        lazyLoader.loadAll();
                                        break;
                                    default:
                                        break;
                                    }
                                }
                            }
                        }

                        res = method.invoke(component, args);
                    }
                }
                return res;
            } catch (Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        }

        private boolean isWriteReplaceMethod(Method method) {
            if (!Object.class.equals(method.getReturnType())) {
                return false;
            }
            if (method.getParameterTypes().length != 0) {
                return false;
            }
            String name = method.getName();
            if (name.equals(WRITE_REPLACE_METHOD)) {
                return true;
            }
            return false;
        }

        private boolean isFinalizeMethod(Method method) {
            if (!void.class.equals(method.getReturnType())) {
                return false;
            }
            if (method.getParameterTypes().length != 0) {
                return false;
            }
            String name = method.getName();
            if (name.equals(FINALIZE_METHOD)) {
                return true;
            }
            return false;
        }
    }
}
