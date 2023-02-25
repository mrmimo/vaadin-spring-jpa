package pl.proinet.vsj.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;

import pl.proinet.vsj.reflection.PropertySpector;

public interface IFormInputGenerator<T> extends Comparable<IFormInputGenerator<T>>{
    
    default int priority() {
        return 0;
    }

    default int compareTo(IFormInputGenerator<T> g) {
        return Integer.compare(g.priority(),priority());
    }

    boolean matches(PropertySpector propertySpector);

    
    Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder);

}
