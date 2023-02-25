package pl.proinet.vsj.view.inputs;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;

import pl.proinet.vsj.data.FilterableRepository;
import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.IFormInputGenerator;


public class RelationCombo<T,I> implements IFormInputGenerator<T> {

    Class<T> cl;
    String[] fields;
    @Autowired PagingAndSortingRepository<T,I> repository;

    protected RelationCombo(Class<T> cl, String... fields) {
        this.cl = cl;
        this.fields = fields;
    }

    @Override
    public boolean matches(PropertySpector propertySpector) {
        return propertySpector
                .getTargetClassOptional()
                    .map( tt -> tt.isAssignableFrom(cl) )
                        .orElse(false);
    }

    @Override
    public Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        ComboBox<T> box = new ComboBox<T>(propertySpector.getProperty());
        box.setAllowCustomValue(false);
        if( repository instanceof FilterableRepository<T,I>) {
            FilterableRepository<T,I> repository = (FilterableRepository<T,I>) this.repository;
            box.setItems( query -> repository.findFiltered(query.getFilter().orElse(""), PageRequest.of(query.getPage(), query.getPageSize())).stream());
        } else {
            box.setItems( query -> repository.findAll( PageRequest.of(query.getPage(), query.getPageSize())).stream());
        }



        box.setItemLabelGenerator(item -> 
                Stream.of(fields)
                    .map( f -> {
                        try {
                            return String.valueOf(BeanUtils.getPropertyDescriptor(cl, f).getReadMethod().invoke(item));
                        } catch (BeansException | IllegalAccessException | IllegalArgumentException | InvocationTargetException x) {
                            return "ERROR";
                        }
                    }).collect(Collectors.joining(",")));


        binder.bind(box,propertySpector.getProperty());
        form.add(box);
        return box;
    }
    
}
