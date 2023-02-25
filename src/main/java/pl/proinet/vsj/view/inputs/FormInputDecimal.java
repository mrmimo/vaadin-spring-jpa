package pl.proinet.vsj.view.inputs;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;

import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.IFormInputGenerator;

@Component
public class FormInputDecimal implements IFormInputGenerator<BigDecimal>{

    @Override
    public boolean matches(PropertySpector propertySpector) {
        return propertySpector
            .getTargetClassOptional()
                .map( tt -> tt.isAssignableFrom(BigDecimal.class) )
                    .orElse(false);
    }

    @Override
    public com.vaadin.flow.component.Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        BigDecimalField c = new BigDecimalField(propertySpector.getProperty());
        //c.setPlaceholder("placehd");
        binder.bind(c, propertySpector.getProperty());
        form.add(c);
        

        return c;
    }
    
}
