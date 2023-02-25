package pl.proinet.vsj.view;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;

public abstract class AbstractTabView<T,I> extends VerticalLayout {
        
    @Autowired EntityManager manager;

    @Autowired PagingAndSortingRepository<T,I> repository;

    @Autowired EntityManagerFactory factory;

    Class<T> cl;

    HorizontalLayout buttons = new HorizontalLayout();
    
    String[] fields = null;

    protected Grid<T> grid;

    protected AbstractTabView(Class<T> cl, String[] fields) {
       this.cl = cl;
       this.fields = fields;
       grid = new Grid<T>(cl);
            
       configureButtons();
    }

    abstract void configureButtons();

    

    @PostConstruct
    public void init() {
        EntityType<T> cls = manager.getMetamodel().entity(cl);

        if( fields != null) {
            //has been passed in constructor, grid don't contain any nested properties, add them
            Stream.of(fields).filter(s -> s.contains(".")).forEach(s -> {
                grid.addColumn(s);
            });
        }
        if( fields == null) {
            fields = cls.getAttributes().stream().map(Attribute::getName).toArray(String[]::new);
        }

        if( fields != null) {     
            hideAndSortGrid(grid,fields);
        }

        grid.setItems( query -> 
            query.getSortOrders().isEmpty() 
                ?
                repository.findAll(
                    PageRequest.of(
                        query.getPage(),
                        query.getPageSize()
                        )
                    ).stream()
                :
                repository.findAll(
                    PageRequest.of(
                        query.getPage(),
                        query.getPageSize(),
                        query.getSortOrders().get(0).getDirection() == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC,
                        query.getSortOrders().stream().map( so -> so.getSorted()).toArray(String[]::new)
                        )).stream()
        );
 
        add(grid);
        add(buttons);
    }

    public static <T> void hideAndSortGrid(Grid<T> grid, String... fields) {
        Set<String> fset = Set.of(fields);

        grid.getColumns().stream().filter( column -> !fset.contains(column.getKey())).forEach( column -> grid.removeColumn(column));
                
        List<Grid.Column<T>> columns = Stream.of(fields).map( f -> grid.getColumnByKey(f)).collect(Collectors.toList());
        grid.setColumnOrder(columns);
    }

}
