package pl.proinet.vsj.view.inputs;

import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.IFormInputGenerator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;


@Component
public class FormInputDateTime implements IFormInputGenerator<LocalDateTime> {

    public int priority() {
        return -90;
    }

    @Override
    public boolean matches(PropertySpector spector) {
        return spector
                .getTargetClassOptional()
                    .map( tt -> tt.isAssignableFrom(LocalDateTime.class) )
                        .orElse(false);
    }

    @Override
    public com.vaadin.flow.component.Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        DateTimePicker c = new DateTimePicker(propertySpector.getProperty());
 
        form.add(c);
        binder.bind(c, propertySpector.getProperty());

        return c;
    }
    
}
