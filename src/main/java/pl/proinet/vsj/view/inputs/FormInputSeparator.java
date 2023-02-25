package pl.proinet.vsj.view.inputs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.data.binder.Binder;

import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.IFormInputGenerator;

@org.springframework.stereotype.Component
public class FormInputSeparator implements IFormInputGenerator<Object> {

    @Override
    public boolean matches(PropertySpector propertySpector) {
        return "-".equals(propertySpector.getProperty());
    }

    @Override
    public Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        Component c = new Hr();
        form.add(c);
        form.setColspan(c, 2);
        return c;
    }
    
}
