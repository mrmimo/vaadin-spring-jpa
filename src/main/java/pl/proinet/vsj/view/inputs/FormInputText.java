package pl.proinet.vsj.view.inputs;

import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.IFormInputGenerator;

import javax.persistence.Column;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;


@Component
public class FormInputText implements IFormInputGenerator<String> {

    public int priority() {
        return -100;
    }

    @Override
    public boolean matches(PropertySpector spector) {
        return spector
                .getTargetClassOptional()
                    .map( tt -> tt.isAssignableFrom(String.class))
                        .orElse(false);
    }

    @Override
    public com.vaadin.flow.component.Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        final TextField c = new TextField(propertySpector.getProperty());
        propertySpector.targetAnnotation(Column.class).ifPresent(
            column -> {
                if( column.length() < 255) c.setMaxLength(column.length());
            }
        );

        form.add(c);
        binder.bind(c, propertySpector.getProperty());
        return c;

    }
    
}



