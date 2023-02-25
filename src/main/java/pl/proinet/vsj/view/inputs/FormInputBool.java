package pl.proinet.vsj.view.inputs;

import javax.persistence.Column;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.binder.Binder;

import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.IFormInputGenerator;

@Component
public class FormInputBool implements IFormInputGenerator<Boolean>{
    
    public int priority() {
        return -100;
    }

    @Override
    public boolean matches(PropertySpector propertySpector) {
        return propertySpector
                .getTargetClassOptional()
                    .map( tt -> (tt.isAssignableFrom(Boolean.class) || tt.isAssignableFrom(boolean.class)))
                    .orElse(false);
    }

    @Override
    public com.vaadin.flow.component.Component  handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        return propertySpector.getTargetClassOptional().map( tt -> {
            String name = propertySpector.getProperty();
            if( !  (tt.isAssignableFrom(boolean.class)) && 
                    (propertySpector.targetAnnotation(Column.class)
                        .map( column -> column.nullable())
                         .orElse(true) )
            ) {
                RadioButtonGroup<Boolean> group = new RadioButtonGroup<>();
                group.setLabel(name);
                group.setItems(true, false, null);
                form.add(group);
                binder.bind(group, name);
                return group;

            } else {
                Checkbox c = new Checkbox(name);
                form.add(c);
                binder.bind(c, name);
                return c;    
            }
        }).orElse(null);
        
    }

}
