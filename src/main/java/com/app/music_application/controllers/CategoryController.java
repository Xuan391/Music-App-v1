package com.app.music_application.controllers;

import com.app.music_application.models.Category;
import com.app.music_application.models.ResponseObject;
import com.app.music_application.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/Categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/ShowAll")
    List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @GetMapping("/show/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id){
        Optional<Category> foundCategory = categoryRepository.findById(id);
        return foundCategory.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Query category successfully",foundCategory)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Cannot find category with id = "+id, "")
                );
    }
    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertCategory(@RequestParam("name") String name) {
        // check 2 product must not have the same name !!
        List<Category> foundCategory = categoryRepository.findByName(name.trim());
        if(foundCategory.size() > 0) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Category name already taken", "")
            );
        }
        Category category = new Category();
        category.setName(name);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok","Insert category successfully", categoryRepository.save(category))
        );
    }

    // update, upsert = update if found, otherwise insert
    @PutMapping("/update/{id}")
    ResponseEntity<ResponseObject> updateCategory(@RequestBody Category newCategory, @PathVariable Long id ){
        Category updateCategory =  categoryRepository.findById(id)
                .map(category -> {
                    category.setName(newCategory.getName());
                    return categoryRepository.save(category);
                }).orElse(null);
        if (updateCategory != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Update category successfully", updateCategory)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("false","cannot find category with id="+id, "")
            );
        }

    }

    //Delete a product -> DELETE method
    @DeleteMapping("/delete/{id}")
    ResponseEntity<ResponseObject> deleteCategory(@RequestParam(name = "id") Long id) {
        boolean exists = categoryRepository.existsById(id);
        if(exists){
            categoryRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "delete category successfully","")
            );
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("failed", "cannot find category to delete", "")
            );
        }
    }
}
