package pl.proinet.vsj.view;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;

import pl.proinet.vsj.reflection.PropertySpector;




public class FormView<T,I> extends VerticalLayout implements HasUrlParameter<I> {
    
    @Autowired CrudRepository<T,I> repository;
    @Autowired EntityManager manager;

    Class<? extends Component> parentViewClass;

    HorizontalLayout buttons = new HorizontalLayout();

    public <C extends Component> void setParentViewClass(Class<C> parentViewClass) {
        this.parentViewClass = parentViewClass;
    }


    public Class<? extends Component> getParentViewClass() {
        return parentViewClass;
    }


    //non-parametrized works, generics don't - dont change
    @SuppressWarnings("rawtypes")
    @Autowired List<IFormInputGenerator> generators;

    String[] fields;

    Class<T> cl;

    Binder<T> binder;

    I identifier = null;
    public I getIdentifier() {
        return identifier;
    }

    public void setIdentifier(I identifier) {
        this.identifier = identifier;
    }


    T value = null;

    public Binder<T> getBinder() {
        return binder;
    }

    public void setBinder(Binder<T> binder) {
        this.binder = binder;
    }

     
    Button updateButton = new Button("Save");
    Button deleteButton = new Button("Delete");
    Button newButton = new Button("New");

    public Button getUpdateButton() {
        return updateButton;
    }


    public void setUpdateButton(Button updateButton) {
        this.updateButton = updateButton;
    }


    public Button getDeleteButton() {
        return deleteButton;
    }


    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }


    public Button getNewButton() {
        return newButton;
    }


    public void setNewButton(Button newButton) {
        this.newButton = newButton;
    }


    protected FormView(Class<T> cl, String[] fields) {
        this.fields = fields;
        this.cl = cl;
        this.binder = new BeanValidationBinder<T>(cl);
        updateButton.addClickListener( event -> {
            if( binder.validate().isOk()) {
                if( value != null)
                    repository.save(value);
                if( parentViewClass !=null)
                    getUI().ifPresent( ui -> ui.navigate(parentViewClass));
            }
        });
        updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateButton.addClickShortcut(Key.ENTER);
        deleteButton.addClickListener( event -> {
            new ConfirmDialog("Are you sure", "Are you sure to delete this data?", "Confirm", confirm -> {
                repository.delete(value);
                getUI().ifPresent( ui -> ui.navigate(parentViewClass));
            }).open();
        });
        newButton.addClickListener( event -> {
            getUI().ifPresent( ui -> ui.navigate(this.getClass()));
        });
        
    }
    
    @PostConstruct
    public void init() {
        generators = generators.stream().sorted().toList();
        if( fields == null) {
            EntityType<T> cls = manager.getMetamodel().entity(cl);
            fields = cls.getAttributes().stream().map(Attribute::getName).toArray(String[]::new);
        }
        
        FormLayout form = new FormLayout();

        for( String f: fields) {
            PropertySpector spector = new PropertySpector(cl,f);
            for(IFormInputGenerator<?> g: generators) {
                if( g.matches( spector) ) {
                    g.handleAdd(form, spector, binder);
                    break;
                }
            }
           
        }
        add(form);
        //beanLoad();

        buttons.setClassName("form-buttons");
        buttons.add(updateButton,newButton,deleteButton);
        add(buttons);

        
    }

    private void beanLoad() {
        if( identifier!= null) {
            value = repository.findById(identifier).orElse(null);
            updateButton.setText("update");
            deleteButton.setEnabled(true);
        }
        else 
            try {
                value = cl.getConstructor().newInstance();
                updateButton.setText("save");
                deleteButton.setEnabled(false);

            } catch (Exception e) {
                value = null;
            }

        if( value !=null ) {
            binder.setBean(value);
        }

    }

    @Override
    public void setParameter(BeforeEvent event,@OptionalParameter I parameter) {
        setIdentifier(parameter);        
        beanLoad();
    }

    public void setBean(T bean) {
        binder.setBean(bean);
    }

}
