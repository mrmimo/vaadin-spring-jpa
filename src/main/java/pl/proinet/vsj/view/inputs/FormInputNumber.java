package pl.proinet.vsj.view.inputs;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;

import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.IFormInputGenerator;

@Component
public class FormInputNumber implements IFormInputGenerator<Double>{

    @Override
    public boolean matches(PropertySpector propertySpector) {
        return propertySpector
                .getTargetClassOptional()
                    .map( tt -> tt.isAssignableFrom(Double.class) || 
                            tt.isAssignableFrom(double.class) || 
                            tt.isAssignableFrom(Float.class)  || 
                            tt.isAssignableFrom(float.class) ).orElse(false);
    }

    @Override
    public com.vaadin.flow.component.Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        NumberField c = new NumberField(propertySpector.getProperty());
 
        form.add(c);
        binder.bind(c, propertySpector.getProperty());

        return c;
    }
    
}
