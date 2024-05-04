package tm.salam.cocaiot.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tm.salam.cocaiot.daoes.CategoryRepository;
import tm.salam.cocaiot.dtoes.CategoryDTO;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.models.Category;

import java.util.LinkedList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ResponseTransfer getAllCategoryDTOS(){

        final ResponseTransfer responseTransfer;
        List<Category> categories=categoryRepository.getAllCategories();
        List<CategoryDTO>categoryDTOS=new LinkedList<>();

        if(categories==null){
            categories=new LinkedList<>();
        }
        for(Category category:categories){
            categoryDTOS.add(toCategoryDTO(category));
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept all category successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(categoryDTOS)
                .build();

        return responseTransfer;
    }

    private CategoryDTO toCategoryDTO(final Category category){

        if(category==null){

            return null;
        }
        CategoryDTO categoryDTO= CategoryDTO.builder()
                .uuid(category.getUuid())
                .name(category.getName())
                .build();

        return categoryDTO;
    }

}
