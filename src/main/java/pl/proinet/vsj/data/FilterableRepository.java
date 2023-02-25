package pl.proinet.vsj.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FilterableRepository<T,I> extends PagingAndSortingRepository<T,I> {
    
    Page<T> findFiltered(String filter, Pageable pageable);

}
