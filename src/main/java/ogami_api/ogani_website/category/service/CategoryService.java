package ogami_api.ogani_website.category.service;

import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.category.model.Category;
import ogami_api.ogani_website.category.repository.CategoryRepository;
import ogami_api.ogani_website.exception.DataAlreadyExistsException;
import ogami_api.ogani_website.exception.DataNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer untuk Category.
 * Business logic untuk CRUD operations categories.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Get all categories.
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Get category by ID.
     */
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category", id));
    }

    /**
     * Create new category.
     */
    public Category createCategory(Category category) {
        // Validasi: category name tidak boleh kosong
        if (category.getCategoryName() == null || category.getCategoryName().isBlank()) {
            throw new IllegalArgumentException("Category name wajib diisi");
        }

        // Validasi: category name harus unique
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new DataAlreadyExistsException("Category dengan nama " + category.getCategoryName() + " sudah ada");
        }

        return categoryRepository.save(category);
    }

    /**
     * Update existing category.
     */
    public Category updateCategory(Integer id, Category updatedCategory) {
        Category existing = getCategoryById(id);

        // Update fields
        if (updatedCategory.getCategoryName() != null && !updatedCategory.getCategoryName().isBlank()) {
            // Check uniqueness jika nama berubah
            if (!existing.getCategoryName().equals(updatedCategory.getCategoryName()) &&
                    categoryRepository.existsByCategoryName(updatedCategory.getCategoryName())) {
                throw new DataAlreadyExistsException("Category dengan nama " + updatedCategory.getCategoryName() + " sudah ada");
            }
            existing.setCategoryName(updatedCategory.getCategoryName());
        }

        if (updatedCategory.getImage() != null) {
            existing.setImage(updatedCategory.getImage());
        }

        return categoryRepository.save(existing);
    }

    /**
     * Delete category.
     */
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new DataNotFoundException("Category", id);
        }
        categoryRepository.deleteById(id);
    }
}
