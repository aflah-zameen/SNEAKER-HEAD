package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Enums.BrandStatus;
import com.e_commerce.SNEAKERHEAD.Enums.CategoryStatus;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import com.e_commerce.SNEAKERHEAD.Service.*;
import com.e_commerce.SNEAKERHEAD.Service.AdminManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

//    @Autowired
//    UserManagementService userManagementService;
    @Autowired
AdminManagementService adminManagementService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductVariantRepository productVariantRepository;

    @Autowired
    OrderRepository orderRepository;


    @GetMapping("/product")
    public String AdminProduct(Model model)
    {
        List<ProductDto> products = productService.ListProduct();
        model.addAttribute("products",products);
        return "product";
    }
    @GetMapping("/product/addproduct")
    public String AddProduct(Model model)
    {
        AddProduct data = productService.addProduct();
        model.addAttribute("data",data);
        model.addAttribute("product", new ProductRequest());
        return "addproduct";
    }
    @GetMapping("/product/editproduct")
    public String EditProduct()
    {
        return "editproduct";
    }

    @ResponseBody
    @PostMapping("/product/add")
    public ResponseEntity<?> validateProduct(@Valid @RequestBody  ProductDto productDto, BindingResult result, HttpServletRequest request)
    {
        // Validate incoming JSON
        if (result.hasErrors()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result.getAllErrors());
        }

        String name = productDto.getName().trim();
        String brand = productDto.getBrandName().trim();
        boolean status= productRepository.existsByName(name) && brandRepository.existsByName(brand);
        if(status)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Product details already exists.");
        }

        try {

            HttpSession session = request.getSession();
            session.setAttribute("productName",name);
            adminManagementService.addProduct(productDto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Product details successfully added, now add your product variant.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Product details are not validated" + ex.getMessage());
        }


    }

    @GetMapping("/product/variant")
    public String getAddProduct(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        String productName = (String)session.getAttribute("productName");
        if(productRepository.existsByName(productName))
        {
          Product product=productRepository.findByName(productName).orElseThrow(() ->new NullPointerException());
          ProductDto productDto = productService.productToProductDto(product);
          session.setAttribute("productId",product.getId());
          model.addAttribute("product",productDto);
          return "addProductVariant";
        }
        else
        {
            return "redirect:/admin/product/addproduct";
        }
    }

    @ResponseBody
    @PostMapping("/product/variant")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductVariantDTO productVariantDto, HttpServletRequest request )
    {
        if(productVariantRepository.existsByArticleCode(productVariantDto.getArticleCode()))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This variant already exist");
        }
        HttpSession session = request.getSession();
        Long productId =(Long) session.getAttribute("productId");

        adminManagementService.addProductVariant(productVariantDto,productId);
        return ResponseEntity.ok("Product successfully added");
    }



    @GetMapping("/category")
    public String showCategory(Model model)
    {
        List<Category> categoryList = categoryRepository.findAll();
        Map<String,String> pageContent = new HashMap<>();
        pageContent.put("header","Categories");
        pageContent.put("addButton","+ Add Category");
        pageContent.put("formHeader","Category");
        model.addAttribute("attributes", categoryList);
        model.addAttribute("pageContent",pageContent);
        return "category-brand.html";
    }
    @GetMapping("/brands")
    public String showBrand(Model model)
    {
        List<Brand> brandList = brandRepository.findAll();
        Map<String,String> pageContent = new HashMap<>();
        pageContent.put("header","Brands");
        pageContent.put("addButton","+ Add Brand");
        pageContent.put("formHeader","Brand");
        model.addAttribute("attributes", brandList);
        model.addAttribute("pageContent",pageContent);
        return "category-brand.html";
    }

    @GetMapping("/attribute/edit")
    public String EditCategory(Model model,@RequestParam Long id,@RequestParam String type)
    {
        Map<String,String> pageContent = new HashMap<>();
        if(type.equals("Category"))
        {
            Category category = categoryRepository.findById(id).orElseThrow(()->new NullPointerException());
            model.addAttribute("attribute",category);
            pageContent.put("header","Edit Category");
            pageContent.put("addButton","Save Category");
            pageContent.put("formHeader","Category");
        }
        else
        {
            Brand brand = brandRepository.findById(id).orElseThrow(()->new NullPointerException());
            model.addAttribute("attribute",brand);
            pageContent.put("header","Edit Brands");
            pageContent.put("addButton","Save Brand");
            pageContent.put("formHeader","Brands");
        }
        model.addAttribute("pageContent",pageContent);

        return "edit-category-brand.html";
    }

    @GetMapping("/attribute/add")
    public String AddCategory(@RequestParam String type,Model model)
    {
        Map<String,String> pageContent = new HashMap<>();
        if(type.equals("Category"))
        {
            pageContent.put("header","Add Category");
            pageContent.put("addButton","+ Add Category");
            pageContent.put("formHeader","Category");
        }
        else
        {
            pageContent.put("header","Add Brand");
            pageContent.put("addButton","+ Add Brand");
            pageContent.put("formHeader","Brands");
        }
        model.addAttribute("pageContent",pageContent);
        model.addAttribute("attribute",new Attribute());
        return "add-category-brand";
    }

    @PostMapping(value = "/attribute/add/data")
    public ResponseEntity<?> SaveCategory(@Valid @RequestBody Attribute attribute,@RequestParam String type)
    {
        System.out.println(type.equals("Category"));
        if(type.equals("Category"))
        {
            if (categoryRepository.existsByName(attribute.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Category already exists."));
            }
            Category category = new Category();
            category.setName(attribute.getName());
            category.setDescription(attribute.getDescription());
            categoryRepository.save(category);
        }
        else{
            if (brandRepository.existsByName(attribute.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Brand already exists."));
            }
            Brand brand = new Brand();
            brand.setName(attribute.getName());
            brand.setDescription(attribute.getDescription());
            brandRepository.save(brand);
        }

        return ResponseEntity.ok(Map.of("name", attribute.getName(), "message", "Added successfully."));
    }

    @PostMapping(value = "/attribute/edit/data")
    public ResponseEntity<?> UpdateCategory(@RequestBody Attribute attribute)
    {
        if(attribute.getType().equals("Category"))
        {
            Category temp = categoryRepository.findById(attribute.getId()).orElseThrow(()-> new NullPointerException());

            if ((temp.getName().equals(attribute.getName()) && temp.getDescription().equals(attribute.getDescription())) || (!temp.getName().equals(attribute.getName()) && categoryRepository.existsByName(attribute.getName())) ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Category already exists."));
            }
            temp.setName(attribute.getName());
            temp.setDescription(attribute.getDescription());
            temp.setId(attribute.getId());
            categoryRepository.save(temp);
        }
        else
        {
            Brand temp = brandRepository.findById(attribute.getId()).orElseThrow(()-> new NullPointerException());

            if ((temp.getName().equals(attribute.getName()) && temp.getDescription().equals(attribute.getDescription())) || (!temp.getName().equals(attribute.getName()) && brandRepository.existsByName(attribute.getName())) ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Brand already exists."));
            }
            temp.setName(attribute.getName());
            temp.setDescription(attribute.getDescription());
            temp.setId(attribute.getId());
            brandRepository.save(temp);
        }

        return ResponseEntity.ok(Map.of("name", attribute.getName(), "message", "Updated successfully."));
    }
    @ResponseBody
    @PutMapping("/attribute/status")
    public ResponseEntity<?> updateStatusAttribute(@RequestParam Boolean status ,@RequestParam Long attributeId,@RequestParam String type)
    {
        if(type.equals("Category"))
        {
            Category category = categoryRepository.findById(attributeId).orElse(new Category());
            if (category == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
            }
            category.setStatus(status ? false : true);
            categoryRepository.save(category);
        }
        else
        {
            Brand brand = brandRepository.findById(attributeId).orElse(new Brand());
            if (brand == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
            }
            brand.setStatus(status ? false : true);
            brandRepository.save(brand);
        }
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @Transactional
    @ResponseBody
    @GetMapping("attribute/remove")
    public ResponseEntity<?> removeAttribute(@RequestParam String type,@RequestParam Long attributeId)
    {
        if(type.equals("Category"))
        {
            categoryRepository.deleteById(attributeId);

        }
        else
        {
            brandRepository.deleteById(attributeId);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Removed");
    }

    @GetMapping("/users")
    public String ListUsers(Model model)
    {
        List<UsersList> ListUser =  userService.ListUser();
        model.addAttribute("users",ListUser);
        return "userpage";
    }

    @ResponseBody
    @PutMapping("/users/block")
    public ResponseEntity<?> blockUser(@RequestParam Long userId,@RequestParam Boolean status)
    {
        WebUser user = userRepository.findById(userId).orElse(new WebUser());
        if(user == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
        user.setStatus(status ? false : true);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("User is blocked");
    }


    @GetMapping("/orders")
    public String ListOrders(Model model)
    {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders",orders);
        return "admin_order";
    }
    @ResponseBody
    @PostMapping("/orders/confirm/{id}")
    public ResponseEntity<?> confirmOrder(@PathVariable Long id)
    {
        Order order = orderRepository.findById(id).orElseThrow(()-> new NullPointerException());
        order.setStatus("CONFIRMED");
        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.OK).body("Confirmed order");
    }
    @ResponseBody
    @PostMapping("/orders/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id)
    {
        Order order = orderRepository.findById(id).orElseThrow(()-> new NullPointerException());
        order.setCancellation(true);
        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.OK).body("Confirmed cancellation");
    }

    @ResponseBody
    @PutMapping("/product/status")
    public ResponseEntity<?> updateStatus(@RequestParam Boolean status ,@RequestParam Long productId )
    {
        Product product = productRepository.findById(productId).orElse(new Product());
        if (product == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
        product.setStatus(status ? false : true);
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

}
