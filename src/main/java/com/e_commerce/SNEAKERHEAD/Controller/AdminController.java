package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.Category;
import com.e_commerce.SNEAKERHEAD.Entity.Order;
import com.e_commerce.SNEAKERHEAD.Entity.Product;
import com.e_commerce.SNEAKERHEAD.Enums.CategoryStatus;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import com.e_commerce.SNEAKERHEAD.Service.*;
import com.e_commerce.SNEAKERHEAD.Service.AdminManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public String AdminCategory(Model model)
    {
        List<Category> categoryList = categoryRepository.findAll();
        Set<Category> finallist= new HashSet<>();
        for(Category x : categoryList )
        {
            finallist.add(x);
        }

        model.addAttribute("categories", finallist);
        return "category";
    }

    @GetMapping("/category/edit")
    public String EditCategory(Model model,@RequestParam Long id)
    {
        Category category = categoryRepository.findById(id).orElseThrow(()->new NullPointerException());
        model.addAttribute("category",category);
        return "editcategory";
    }

    @GetMapping("/category/add")
    public String AddCategory(Model model)
    {
        model.addAttribute("category",new Category());
        return "addcategory";
    }

    @PostMapping(value = "/category/add/data")
    public ResponseEntity<?> SaveCategory(@Valid @RequestBody CategoryDto categoryDto)
    {
        System.out.println(categoryDto);
        if (categoryRepository.existsByName(categoryDto.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Category already exists."));
        }
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        categoryRepository.save(category);
        return ResponseEntity.ok(Map.of("name", category.getName(), "message", "Category added successfully."));
    }

    @PostMapping(value = "/category/edit/data")
    public ResponseEntity<?> UpdateCategory(@Valid @RequestBody Category category)
    {
        Category temp = categoryRepository.findById(category.getId()).orElseThrow(()-> new NullPointerException());

        if ((temp.getName().equals(category.getName()) && temp.getDescription().equals(category.getDescription())) || (!temp.getName().equals(category.getName()) && categoryRepository.existsByName(category.getName())) ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Category already exists."));
        }
        temp.setName(category.getName());
        temp.setDescription(category.getDescription());
        temp.setId(category.getId());
        temp.setCategoryStatus(CategoryStatus.ACTIVE);
        categoryRepository.save(temp);
        return ResponseEntity.ok(Map.of("name", category.getName(), "message", "Category updated successfully."));
    }

    @GetMapping("/users")
    public String ListUsers(Model model)
    {
        List<UsersList> ListUser =  userService.ListUser();
        model.addAttribute("users",ListUser);
        return "userpage";
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


}
