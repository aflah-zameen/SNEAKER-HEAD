package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Enums.BrandStatus;
import com.e_commerce.SNEAKERHEAD.Enums.CategoryStatus;
import com.e_commerce.SNEAKERHEAD.Mappers.ProductMapper;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import com.e_commerce.SNEAKERHEAD.Service.*;
import com.e_commerce.SNEAKERHEAD.Service.AdminManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CriteriaProductRepository criteriaProductRepository;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CouponService couponService;

    @Autowired
    CriteriaCouponRepository criteriaCouponRepository;


    @GetMapping("/product")
    public String AdminProduct(Model model)
    {
        SortProductDTO sortProductDTO = new SortProductDTO();
        Page<Product> productPage = productService.ListProduct(sortProductDTO.getPageablePage());
        List<ProductDto> productDtos = productMapper.toDTOList(productPage.getContent()).stream().peek(pd->pd.setQuantity(pd.getProductVariantDTOs().stream().mapToInt(pv -> pv.getQuantity()).sum())).peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
            pd.getProductVariantDTOs().forEach( pv -> pv.setFormattedPrice(pv.FormattedPrice()));
        }).collect(Collectors.toList());
        model.addAttribute("products",productDtos);
        model.addAttribute("totalPages",productPage.getTotalPages());
        model.addAttribute("currentPage",0);
        model.addAttribute("pageSize",10);

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
    @GetMapping("/product/editproduct/{id}")
    public String EditProduct(@PathVariable Long id)
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
        String brandName = productDto.getBrandName().trim();
        Brand brand = brandRepository.findByName(brandName).orElse(new Brand());
        Category category =categoryRepository.findByName(productDto.getCategoryName()).orElse(new Category());
//        boolean status= productRepository.existsByName(name) && brandRepository.existsByName(brand);
//        if(status)
//        {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Product details already exists.");
//        }

        try {

            HttpSession session = request.getSession();
            session.setAttribute("productName",name);
            Product product =adminManagementService.addProduct(productDto,brand,category);
            session.setAttribute("productId",product.getId());
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
        System.out.println(productId+">>>>");
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
        for(Order order : orders)
        {
            System.out.println(order);
        }
        model.addAttribute("orders",orders);
        return "admin_order";
    }
    @ResponseBody
    @PutMapping("/orders/edit")
    public ResponseEntity<?> EditOrder(@RequestBody EditOrder editOrder)
    {
        Order order = orderRepository.findById(editOrder.getOrderId()).orElseThrow(()-> new NullPointerException());
        order.setStatus(editOrder.getOrderStatus());
        order.setPaymentMethod(editOrder.getPaymentMethod());
        UserAddress address = addressRepository.findById(order.getAddress().getId()).orElse(new UserAddress());
        address.setName(editOrder.getUserName());
        addressRepository.save(address);
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

    @ResponseBody
    @PostMapping("/product/sorting")
    public ResponseEntity<List<ProductDto>> sortProduct(@RequestBody SortProductDTO sortProductDTO)
    {
            Page<Product> productPage = productService.ListProduct(sortProductDTO.getPageablePage());
            List<ProductDto> productDtos = productMapper.toDTOList(productPage.getContent());
            productDtos.stream().peek(pd -> pd.setQuantity(pd.getProductVariantDTOs().stream().mapToInt(pv -> pv.getQuantity()).sum())).collect(Collectors.toList());
            return ResponseEntity.ok(productDtos);
    }

    @ResponseBody
    @GetMapping("/product/search")
    public ResponseEntity<List<ProductDto>> saerchProductAdmin(@RequestParam(name = "keyword") String keyword) throws NullPointerException
    {
        if(keyword.isEmpty() || keyword.isBlank())
        {
            throw new NullPointerException();
        }
        List<Product> products = criteriaProductRepository.searchAllProducts(keyword);
        List<ProductDto> productDtos = productMapper.toDTOList(products).stream().peek(pd-> pd.setQuantity(pd.getProductVariantDTOs().stream().mapToInt(pv -> pv.getQuantity()).sum())).collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    //coupon
    @GetMapping("/coupons")
    public String showCoupons(Model model)
    {
        Page couponPage = couponRepository.findAll(new SortProductDTO().getPageablePage());
        model.addAttribute("coupons",couponPage.getContent());
        model.addAttribute("totalPages",couponPage.getTotalPages());
        model.addAttribute("currentPage",0);
        model.addAttribute("pageSize",10);
        return "couponAdmin";
    }


    @GetMapping("/coupon/addcoupon")
    public String showAddCoupon(Model model)
    {
        model.addAttribute("couponObject",new Coupon());
        return "add_coupon";
    }

    @PostMapping("/coupon/data")
    public String addCoupon(@Valid @ModelAttribute("couponObject") Coupon coupon,BindingResult result,Model model)
    {
        if(!(coupon.getStartDate() == null) && !(coupon.getEndDate() == null))
        {
            if (!coupon.getStartDate().isBefore(coupon.getEndDate())) {
                result.rejectValue("startDate", "error.coupon", "Start date must be before end date");
            }
         }
        if(!(coupon.getStartDate() == null) || !(coupon.getEndDate() == null))
        {
            if (coupon.getStartDate().isBefore(LocalDate.now())) {
                result.rejectValue("startDate", "error.coupon", "Please select a date from today onwards");
            }
            if (coupon.getEndDate().isBefore(LocalDate.now())) {
                result.rejectValue("endDate", "error.coupon", "Please select a date from today onwards");
            }
        }
        if(result.hasErrors()) {
            System.out.println(result.getAllErrors().toString());
            model.addAttribute("couponObject",coupon);
            return "add_coupon";
        }

        couponService.addCoupon(coupon);
        return "redirect:/admin/coupons";
    }

    @ResponseBody
    @PostMapping("/coupons/sorting")
    public ResponseEntity<List<Coupon>> sortCoupons(@RequestBody SortProductDTO sortProductDTO)
    {
        Page<Coupon> couponPage = couponRepository.findAll(sortProductDTO.getPageablePage());
        return ResponseEntity.ok(couponPage.getContent());
    }

    @ResponseBody
    @GetMapping("/coupons/search")
    public ResponseEntity<List<Coupon>> saerchCouponAdmin(@RequestParam(name = "keyword") String keyword) throws NullPointerException
    {
        if(keyword.isEmpty() || keyword.isBlank())
        {
            throw new NullPointerException();
        }
        List<Coupon> coupons = criteriaCouponRepository.searchAllCoupons(keyword);
        return ResponseEntity.ok(coupons);
    }

    //offers

    @GetMapping("/offers")



}
