package pl.proinet.vsj.view;

import java.util.Set;
import javax.annotation.PostConstruct;

import org.springframework.data.domain.ExampleMatcher;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid.SelectionMode;


public class TabView<T,I> extends AbstractTabView<T,I> {


    Class<? extends FormView<T,I>> editViewClass;


    Set<T> selectedItems;
    T selectedItem;

    T filter;
    ExampleMatcher matcher = null;

    protected TabView(Class<T> cl, String... strings) {
        super(cl, strings);
    }


    public Class<? extends FormView<T, I>> getEditViewClass() {
        return editViewClass;
    }


    public void setEditViewClass(Class<? extends FormView<T, I>> editViewClass) {
        this.editViewClass = editViewClass;
    }


    protected void configureButtons() {
        Button deleteButton = new Button("Delete");
        Button newButton = new Button("New");        
        deleteButton.addClickListener( event -> {
                new ConfirmDialog("Are you sure", "Are you sure to delete this data?", "Confirm", confirm -> {
                    repository.deleteAll(selectedItems);
                    grid.getDataProvider().refreshAll();
                }).open();
            });
        deleteButton.setEnabled(false);
        newButton.addClickListener( event -> {
            getUI().ifPresent( ui -> ui.navigate(editViewClass));
        });
        grid.addSelectionListener( event -> {
            deleteButton.setEnabled( ! event.getAllSelectedItems().isEmpty());
        });
        buttons.add(deleteButton,newButton);
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addSelectionListener( event -> {
            selectedItems = event.getAllSelectedItems();
            selectedItem  = event.getFirstSelectedItem().orElse(null);
        });
        if( editViewClass != null)
            grid.addItemClickListener(
                event -> getUI().ifPresent(
                    ui -> ui.navigate(editViewClass, (I) factory.getPersistenceUnitUtil().getIdentifier(event.getItem()))
                )
            );
        if( matcher != null)
            configureFiltering();
    }

    protected configureFiltering() {
        filter = cl.getConstructor(null).newInstance(); //let's construct filtering instance
    }


}