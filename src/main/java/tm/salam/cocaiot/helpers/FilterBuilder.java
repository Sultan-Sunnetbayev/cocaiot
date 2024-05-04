package tm.salam.cocaiot.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component
public class FilterBuilder {

    @Value("${default.sort.column}")
    private String defaultSortColumn;
    @Value("${default.sort.type}")
    private String defaultSortType;

    public Pageable buildFilter(final int page, final int size, List<String>sortBy, List<SortType> sortTypes) {

        if(sortBy==null || sortBy.isEmpty()){
            sortBy=new LinkedList<>();
            sortBy.add(defaultSortColumn);
        }
        if(sortTypes==null || sortTypes.isEmpty()){
            sortTypes=new LinkedList<>();
            sortTypes.add(SortType.valueOf(defaultSortType));
        }
        Sort sort = null;
        Iterator sortByIterator = sortBy.iterator();
        Iterator sortTypeIterator = sortTypes.iterator();

        if(sortByIterator.hasNext() && sortTypeIterator.hasNext()) {
            switch ((SortType) sortTypeIterator.next()) {
                case ASCENDING:
                    sort = Sort.by(Sort.Direction.ASC, sortByIterator.next().toString());
                    break;
                case DESCENDING:
                    sort = Sort.by(Sort.Direction.DESC, sortByIterator.next().toString());
                    break;
            }
        } else{
            return null;
        }
        while (sortByIterator.hasNext() && sortTypeIterator.hasNext()) {
            switch ((SortType) sortTypeIterator.next()) {
                case ASCENDING:
                    sort = sort.and(Sort.by(Sort.Direction.ASC, sortByIterator.next().toString()));
                    break;
                case DESCENDING:
                    sort = sort.and(Sort.by(Sort.Direction.DESC, sortByIterator.next().toString()));
                    break;
            }
        }

        return sort == null ? null : PageRequest.of(page, size, sort);
    }

}
