package com.synaptix.mybatis.simple.observer;

import com.synaptix.component.IComponent;
import com.synaptix.entity.ICancelable;
import com.synaptix.entity.ITracable;
import com.synaptix.mybatis.component.session.observer.AbstractTriggerObserver;

import java.util.Date;

public class TracableTriggerObserver extends AbstractTriggerObserver {

    private final IUserByHandler userByHandler;

    public TracableTriggerObserver(IUserByHandler userByHandler) {
        super();

        this.userByHandler = userByHandler;
    }

    @Override
    public <E extends IComponent> void triggerBefore(Type type, E component) {
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
