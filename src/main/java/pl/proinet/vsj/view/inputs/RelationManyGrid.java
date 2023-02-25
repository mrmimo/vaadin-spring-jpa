package pl.proinet.vsj.view.inputs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import pl.proinet.vsj.reflection.PropertySpector;
import pl.proinet.vsj.view.AbstractTabView;
import pl.proinet.vsj.view.FormView;
import pl.proinet.vsj.view.IFormInputGenerator;


public class RelationManyGrid<T,I> implements IFormInputGenerator<T> {

    Class<T> cl;
    String[] fields;
    Class<? extends FormView<T,I>> editViewClass;
    
    @Autowired
    ApplicationContext applicationContext;

  

    public interface EditItemDelegate<T> {
        boolean showEditor(T item);
    }

    private class RelationManyGridComponent extends VerticalLayout {
        T selectedItem;
        Set<T> selectedItems;
        Grid<T> grid;
        HasValueGrid gridValue;
        Button newButton = new Button("New");
        Button deleteButton = new Button("Delete");
        HorizontalLayout buttons = new HorizontalLayout(newButton, deleteButton);
        PropertySpector spector;

        @SuppressWarnings("unchecked")
        public RelationManyGridComponent(HasValueGrid gridValue, 
                                        PropertySpector spector, 
                                        Binder binder,
                                        java.util.function.Function<T,Boolean> editorDelegate) {
            this.gridValue = gridValue;
            this.grid = gridValue.getGrid();
            this.grid.setSelectionMode(SelectionMode.MULTI);
            this.spector = spector;
            add(grid,buttons);
            grid.addSelectionListener( event -> {
                selectedItems = event.getAllSelectedItems();
                selectedItem  = event.getFirstSelectedItem().orElse(null);               
            });
            grid.addItemClickListener(click -> {
                editorDelegate.apply(click.getItem());
            });
            newButton.addClickListener( event -> {
                if( editorDelegate != null)
                spector.getTargetTypeArguments().ifPresent( type -> {
                        try {
                            T newItem = (T) Class.forName(type[0].getTypeName()).getConstructor().newInstance();
                            spector.targetAnnotation(OneToMany.class).ifPresent( onetomany -> {
                                if( onetomany.mappedBy() != null )
                                    try {
                                        Class.forName(type[0].getTypeName())
                                            .getMethod( PropertySpector.toSetter(onetomany.mappedBy()), spector.getBeanClass() )
                                                .invoke(newItem, binder.getBean());
                                    } catch (IllegalAccessException | IllegalArgumentException
                                            | InvocationTargetException | NoSuchMethodException | SecurityException
                                            | ClassNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }                                
                            });
                            if( editorDelegate.apply(newItem) ) {
                                gridValue.addItem(newItem);
                            }
                        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                                | InvocationTargetException | NoSuchMethodException | SecurityException
                                | ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                });
            });
            deleteButton.addClickListener( event -> {
                if( selectedItems != null && selectedItems.size() > 0) {
                    new ConfirmDialog("Are you sure", "Are you sure to delete this data?", "Confirm", confirm -> {
                        gridValue.removeItems(selectedItems);
                        binder.refreshFields();
                    }).open();
                }
            });
        }
        



    }


    protected RelationManyGrid(Class<T> cl, String... fields) {
        this.cl = cl;
        this.fields = fields;
    }

    public void setEditViewClass(Class<? extends FormView<T,I>> editViewClass) {
        this.editViewClass = editViewClass;
    }

    @Override
    public boolean matches(PropertySpector propertySpector) {
        return propertySpector
                .getTargetClassOptional()
                    .map( tt -> Collection.class.isAssignableFrom(tt) && 
                                propertySpector.getTargetTypeArguments().map( ctt -> cl.getTypeName().equals(ctt[0].getTypeName()))
                                .orElse(false))
                        .orElse(false);
    }

    @Override
    public Component handleAdd(FormLayout form, PropertySpector propertySpector, Binder<?> binder) {
        Grid<T> grid = new Grid<T>(cl);
        HasValueGrid valueGrid = new HasValueGrid(grid);
        binder.bind(valueGrid,propertySpector.getProperty());
        RelationManyGridComponent gridComponent = 
        new RelationManyGridComponent(valueGrid, propertySpector, binder,
            item -> { 
                try {
                    //FormView<T,I> editView = editViewClass.getConstructor().newInstance();
                    FormView<T,I> editView = applicationContext.getBean(editViewClass);
                    final Dialog d = new Dialog();
                    d.setHeaderTitle("Edit " + cl.getSimpleName());
                    //editView.init();
                    d.add(editView);
                    editView.getNewButton().setVisible(false);
                    editView.getUpdateButton().addClickListener(click -> d.close());
                    editView.getUpdateButton().setText("Update!");
                    editView.getDeleteButton().setVisible(false);
                    editView.setBean(item);
                    d.addOpenedChangeListener(action -> {
                        if (! action.isOpened()) try { binder.refreshFields();} catch (Exception e){}
                    });
                    d.open();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        
        );

        if( fields != null) 
            AbstractTabView.hideAndSortGrid(grid, fields);
        form.add(gridComponent);
        form.setColspan(gridComponent, 2);
        
       
        return gridComponent;
    }
    
    protected class RelationChangeEvent implements ValueChangeEvent<Collection<T>> {

        HasValue<?,Collection<T>> source;
        boolean fromClient;
        Collection<T> oldValue;
        Collection<T> newValue;

        public RelationChangeEvent(HasValue<?,Collection<T>> source, boolean fromClient, Collection<T> oldValue, Collection<T> newValue) {
            this.source = source;
            this.fromClient = fromClient;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public HasValue<?, Collection<T>> getHasValue() {
            return source;
        }

        @Override
        public boolean isFromClient() {
            return fromClient;
        }

        @Override
        public Collection<T> getOldValue() {
            return oldValue;
        }

        @Override
        public Collection<T> getValue() {
            return newValue;
        }

    }

    protected class HasValueGrid implements HasValue<RelationChangeEvent,Collection<T>> {
        Grid<T> grid;
        Collection<T> items;
        List<ValueChangeListener<? super RelationManyGrid<T, I>.RelationChangeEvent>> listeners = new ArrayList<>();
        protected HasValueGrid(Grid<T> grid) {
            this.grid = grid;
        }

        private void notifyListeners(Collection<T> oldValue) {
            listeners.forEach( listener -> listener.valueChanged( new RelationChangeEvent(this,false,oldValue,items)));
        }

        public void addItem(T item) {
            Collection<T> oldValue = new ArrayList<>(items);
            items.add(item);
            notifyListeners(oldValue);
        }

        public void removeItems(Collection<T> itemsToRemove) {
            Collection<T> oldValue = new ArrayList<>(items);
            items.removeAll(itemsToRemove);
            notifyListeners(oldValue);
        }

        @Override
        public void setValue(Collection<T> value) {
            Collection<T> oldValue = items;
            items = value;
            grid.setItems(value);            
            notifyListeners(oldValue);
        }
        @Override
        public Collection<T> getValue() {
            return items;
        }
        @Override
        public Registration addValueChangeListener(
                ValueChangeListener<? super RelationManyGrid<T, I>.RelationChangeEvent> listener) {
            return Registration.addAndRemove(listeners, listener);
        }
        @Override
        public void setReadOnly(boolean readOnly) {
        }
        @Override
        public boolean isReadOnly() {
            return true;
        }
        @Override
        public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        }
        @Override
        public boolean isRequiredIndicatorVisible() {
            return false;
        }
        public Grid<T> getGrid() {
            return grid;
        }
    }

}
