package pl.proinet.vsj.view.inputs;

import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.IFormInputGenerator;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;


@Component
public class FormInputInteger implements IFormInputGenerator<Integer> {

    public int priority() {
        return -100;
    }

    @Override
    public boolean matches(PropertySpector spector) {
        return spector
                .getTargetClassOptional()
                    .map( tt -> tt.isAssignableFrom(Integer.class) || tt.isAssignableFrom(int.class))
                     .orElse(false);
    }

    @Override
    public com.vaadin.flow.component.Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        IntegerField c = new IntegerField(propertySpector.getProperty());
 
        form.add(c);
        binder.bind(c, propertySpector.getProperty());

        return c;
    }
    
}
