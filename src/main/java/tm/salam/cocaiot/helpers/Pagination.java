package tm.salam.cocaiot.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pagination {

    public static <T> List<T> getPages(Collection<T> c, Integer pageSize, Integer page) {
        if (c == null)
            return Collections.emptyList();
        List<T> list = new ArrayList<T>(c);
        if (pageSize == null || pageSize <= 0 || pageSize > list.size())
            pageSize = list.size();

        return list.subList(page * pageSize, Math.min(++page*pageSize, list.size()));
    }

}
