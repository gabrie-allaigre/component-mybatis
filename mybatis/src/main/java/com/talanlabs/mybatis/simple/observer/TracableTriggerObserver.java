package com.talanlabs.mybatis.simple.observer;

import com.talanlabs.component.IComponent;
import com.talanlabs.entity.ICancelable;
import com.talanlabs.entity.ITracable;
import com.talanlabs.mybatis.component.session.observer.AbstractTriggerObserver;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;

public class TracableTriggerObserver extends AbstractTriggerObserver {

    private final IUserByHandler userByHandler;

    public TracableTriggerObserver(IUserByHandler userByHandler) {
        super();

        this.userByHandler = userByHandler;
    }

    @Override
    public <E extends IComponent> void triggerBefore(SqlSession sqlSession, Type type, E component) {
        if (userByHandler != null) {
            if (component instanceof ITracable) {
                ITracable tracable = (ITracable) component;
                switch (type) {
                case Insert:
                    tracable.setCreatedBy(userByHandler.getUserBy());
                    tracable.setCreatedDate(new Date());
                    break;
                case Update:
                    tracable.setUpdatedBy(userByHandler.getUserBy());
                    tracable.setUpdatedDate(new Date());
                    break;
                case Delete:
                    break;
                default:
                    break;
                }
            }

            if (component instanceof ICancelable) {
                ICancelable cancelable = (ICancelable) component;
                if (cancelable.isCanceled() && cancelable.getCanceledDate() == null) {
                    switch (type) {
                    case Insert:
                        break;
                    case Update:
                        cancelable.setCanceledBy(userByHandler.getUserBy());
                        cancelable.setCanceledDate(new Date());
                        break;
                    case Delete:
                        break;
                    default:
                        break;
                    }
                }
            }
        }
    }

    public interface IUserByHandler {

        /**
         * Get user
         *
         * @return user
         */
        String getUserBy();

    }
}
