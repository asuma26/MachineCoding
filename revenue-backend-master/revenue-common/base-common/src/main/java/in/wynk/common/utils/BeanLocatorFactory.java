package in.wynk.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class BeanLocatorFactory implements ApplicationListener<ContextRefreshedEvent> {
    private static ApplicationContext applicationContext;

    public static <T> T getBean(final String name, final Class<T> clazz) {
        try {
            return applicationContext.getBean(name, clazz);
        } catch (BeansException e) {
            throw new IllegalArgumentException("No bean exists by name - " + name + " of type - " + clazz.getCanonicalName());
        }
    }

    public static <T> T getBean(final Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            throw new IllegalArgumentException("No bean exists by " + clazz.getCanonicalName());
        }
    }

    public static <T> T getBeanOrDefault(final String name, final Class<T> clazz, T defaultBean) {
        try {
            return applicationContext.getBean(name, clazz);
        } catch (BeansException e) {
            return defaultBean;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        BeanLocatorFactory.applicationContext = event.getApplicationContext();
    }
}
