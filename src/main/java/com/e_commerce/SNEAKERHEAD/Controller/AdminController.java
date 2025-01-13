package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Mappers.*;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import com.e_commerce.SNEAKERHEAD.Service.*;
import com.e_commerce.SNEAKERHEAD.Service.AdminManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.aspectj.apache.bcel.classfile.LocalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
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

    @Autowired
    OffersRepository offersRepository;

    @Autowired
    OfferMapper offerMapper;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    SalesOrderMapper salesOrderMapper;

    @Autowired
    SaleReportService saleReportService;

    @Autowired
    RefferalCodeRepository refferalCodeRepository;

    @Autowired
    SaleRepository saleRepository;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    AllOrdersDTO allOrdersDTO;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    UserMapper userMapper;

    @GetMapping("/dashboard")
    public String showDashboard()
    {
        return "admin_dashboard";
    }

    @ResponseBody
    @GetMapping("/dashboard/sales")
    public List<SalesOverviewDTO> salesOverview(@RequestParam(value = "startDate",required = false) LocalDate startDate,
                                                @RequestParam(value = "endDate",required = false) LocalDate endDate,
                                                @RequestParam(value = "year",required = false) Integer year,
                                                @RequestParam(value = "month",required = false) Integer month)
    {
        List<SalesOverviewDTO> salesOverviewDTOS = new ArrayList<>();
        if(year != null)
        {
            startDate = LocalDate.of(year,1,1);
            endDate = LocalDate.of(year,12,31);
        }
        else if(month != null)
        {
            int currentYear = LocalDate.now().getYear();
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
            System.out.println(startDate+">>"+endDate);
        }
        List<OrderEntity> orderEntities = orderRepository.findByOrderDateBetweenAndStatus(startDate,endDate,"DELIVERED");
        orderEntities.forEach(or->
                salesOverviewDTOS.add(new SalesOverviewDTO(or.getOrderDate(),or.getOrderTotal())));
        return salesOverviewDTOS;
    }

    @ResponseBody
    @GetMapping("/dashboard/top")
    public List<TopSellingDTO> fetchTop(@RequestParam(name = "type") String type)
    {
        return dashboardService.getTopSelling(type);
    }


    @GetMapping("/product")
    public String AdminProduct(Model model)
    {
        SortProductDTO sortProductDTO = new SortProductDTO();
        Page<Product> productPage = productService.ListProduct(sortProductDTO.getPageablePage());
        List<ProductDto> productDtos = productMapper.toDTOList(productPage.getContent()).stream().peek(pd->pd.setQuantity(pd.getProductVariantDTOs().stream().mapToInt(pv -> pv.getQuantity()).sum())).peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
            pd.getProductVariantDTOs().forEach( pv -> pv.setFormattedPrice(pv.FormattedPrice(pv.getPrice())));
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
    public String EditProduct(@PathVariable Long id,Model model)
    {
        Product product = productRepository.findById(id).orElse(new Product());
        ProductDto productDto=productMapper.toDTO(product);
        AddProduct data = productService.addProduct();
        model.addAttribute("data",data);
        model.addAttribute("product",productDto);
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

    @ResponseBody
    @PutMapping("/product/edit/data")
    public ResponseEntity<?> editProduct(@Valid @RequestBody ProductDto productDto,BindingResult result)
    {
        if (result.hasErrors()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result.getAllErrors());
        }
        else if(productDto.getId()==null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("product not found");
        }
        Product product = productRepository.findById(productDto.getId()).orElse(new Product());
        product.setName(productDto.getName());
        product.setCategory(categoryRepository.findByName(productDto.getCategoryName()).orElse(new Category()));
        product.setBrand(brandRepository.findByName(productDto.getBrandName()).orElse(new Brand()));
        product.setWeight(productDto.getWeight());
        product.setManufacturer(product.getManufacturer());
        product.setGenericName(productDto.getGenericName());
        product.setDescription(productDto.getDescription());
        product.setImportedBy(productDto.getImportedBy());
        product.setCountryOfOrigin(product.getCountryOfOrigin());
        product.setMarketedBy(productDto.getMarketedBy());
        productRepository.save(product);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/product/variant")
    public String getAddProduct(@RequestParam(name = "productId", required = false) Long productId,HttpServletRequest request,Model model)
    {
        if(productId != null)
        {
            Product product = productRepository.findById(productId).orElse(new Product());
            ProductDto productDto = productService.productToProductDto(product);
            model.addAttribute("product",productDto);
            return "addProductVariant";
        }
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
    public ResponseEntity<?> addProduct(@RequestParam("articleCode") String articleCode,
                                        @RequestParam("colorCode") String colorCode,
                                        @RequestParam("color") String color,
                                        @RequestParam("price") Double price,
                                        @RequestParam("quantity") Integer quantity,
                                        @RequestParam("maxQuantity") Integer maxQuantity,
                                        @RequestParam("size") String[] size,
                                        @RequestParam("images") List<MultipartFile> images,
                                        @RequestParam("productId") Long productId)
    {
        if(productVariantRepository.existsByArticleCode(articleCode))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This variant already exist");
        }
        try {

            adminManagementService.addProductVariant(articleCode,colorCode,color,price,quantity,maxQuantity,size,images, productId);
            return ResponseEntity.ok("Product successfully added");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseBody
    @PutMapping("/product/variant/edit")
    public ResponseEntity<?> editVariant(@RequestParam("articleCode") String articleCode,
                                         @RequestParam("colorCode") String colorCode,
                                         @RequestParam("color") String color,
                                         @RequestParam("price") Double price,
                                         @RequestParam("quantity") Integer quantity,
                                         @RequestParam("maxQuantity") Integer maxQuantity,
                                         @RequestParam(value = "size" , required = false) String[] size,
                                         @RequestParam(value = "images" , required = false) List<MultipartFile> images,
                                         @RequestParam("productId") Long productId,
                                         @RequestParam("id") Long id) throws IOException
    {
        ProductVariant productVariant = productVariantRepository.findById(id).orElse(new ProductVariant());
        productVariant.setColorCode(colorCode);
       if(images != null && !images.isEmpty())
       {
           List<String> paths = adminManagementService.saveImagesToDirectory(images,articleCode,productVariant.getProduct().getName());
           if(productVariant.getImages() != null)
            paths.addAll(productVariant.getImages());
           productVariant.setImages(paths);
       }
        productVariant.setArticleCode(articleCode);
        if(size.length>0 )
            productVariant.setSize(size);
        productVariant.setPrice(price);
        productVariant.setQuantity(quantity);
        productVariant.setColor(color);
        productVariant.setMaxQuantity(maxQuantity);
        productVariantRepository.save(productVariant);
        return ResponseEntity.ok("success");
    }

    @ResponseBody
    @PutMapping("/variant/image/remove")
    public ResponseEntity<?> removeImage(@RequestBody Map<String ,Object> formDataImage)
    {
        ProductVariant productVariant = productVariantRepository.findById(Long.parseLong((String)formDataImage.get("variantId"))).orElse(new ProductVariant());
        List<String>finalImages = productVariant.getImages();
        finalImages.removeAll((List<String>)formDataImage.get("images"));
        productVariant.setImages(finalImages);
        productVariantRepository.save(productVariant);
        return ResponseEntity.ok("success");
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
        List<OrderEntity> orders = orderRepository.findAll();
        model.addAttribute("orders",orders);
        return "admin_order";
    }
    @ResponseBody
    @PutMapping("/orders/edit")
    public ResponseEntity<?> EditOrderEntity(@RequestBody EditOrder editOrder)
    {
        OrderEntity order = orderRepository.findById(editOrder.getOrderId()).orElseThrow(()-> new NullPointerException());
        order.setStatus(editOrder.getOrderStatus());
        order.setPaymentMethod(editOrder.getPaymentMethod());
        UserAddress address = addressRepository.findById(order.getAddress().getId()).orElse(new UserAddress());
        address.setName(editOrder.getUserName());
        addressRepository.save(address);
        orderRepository.save(order);
        if(order.getStatus().equalsIgnoreCase("DELIVERED"))
        {
            if(order.getReferralEntity() != null)
            {
                ReferralEntity referralEntity = refferalCodeRepository.findById(order.getReferralEntity().getId()).orElse(new ReferralEntity());
                Long userId = referralEntity.getUser().getId();
                Wallet wallet = walletRepository.findByUser_id(userId).orElse(new Wallet());
                wallet.setBalance(wallet.getBalance()+500);
                walletRepository.save(wallet);
                Transaction transaction = new Transaction();
                transaction.setTransactionDate(LocalDate.now());
                transaction.setAmount(500D);
                transaction.setWallet(wallet);
                transaction.setStatus("REFER REWARD");
                transactionRepository.save(transaction);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Confirmed order");
    }
    @ResponseBody
    @PostMapping("/orders/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id)
    {
        OrderEntity order = orderRepository.findById(id).orElseThrow(()-> new NullPointerException());
        order.setCancellation(true);
        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.OK).body("Confirmed cancellation");
    }

    @ResponseBody
    @PostMapping("/order/return")
    public ResponseEntity<?> returnProduct(@RequestParam("orderId") Long id)
    {
        OrderEntity order = orderRepository.findById(id).orElse(new OrderEntity());
        order.setStatus("RETURNED");
        orderRepository.save(order);

        WebUser user = order.getUser();

        Wallet wallet = walletRepository.findByUser_id(user.getId()).orElse(new Wallet());
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(order.getOrderTotal());
        transaction.setStatus("REFUNDED");
        transaction.setTransactionDate(LocalDate.now());
        transactionRepository.save(transaction);


        wallet.setBalance(wallet.getBalance()+order.getOrderTotal());
        wallet.setLastUpdate(LocalDate.now());
        walletRepository.save(wallet);

        return ResponseEntity.ok("success");

    }

    @ResponseBody
    @GetMapping("/orders/sorting")
    public ResponseEntity<AllOrdersDTO> sortOrders(@RequestParam(name = "sortBy") String sortBy,
                                        @RequestParam(name="orderType") String orderType,
                                        @RequestParam(name="pageNumber") Integer pageNumber,
                                        @RequestParam(name="pageSize") Integer pageSize)
    {

            Pageable pageable = PageRequest.of(pageNumber,pageSize,Sort.Direction.fromString(orderType),sortBy);
            Page<OrderEntity> orderEntities = orderRepository.findAll(pageable);
            List<OrderEntityDTO> orderEntityDTOS = new ArrayList<>();
            for(OrderEntity oe : orderEntities.getContent())
            {
                orderEntityDTOS.add(new OrderEntityDTO(oe.getId(),oe.getUser().getFullName(),addressMapper.toDTO(oe.getAddress()),oe.getDeductedAmount(),oe.getOrderTotal(),oe.getOrderDate(),oe.getPaymentMethod(),oe.getStatus(),oe.isCancellation()));
            }
            allOrdersDTO.setOrders(orderEntityDTOS);
            allOrdersDTO.setTotalPages(orderEntities.getTotalPages());
            return ResponseEntity.ok(allOrdersDTO);
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
    public String showAllOffers(Model model)
    {
        SortProductDTO sortProductDTO = new SortProductDTO();
        Page offerPage = offersRepository.findAll(sortProductDTO.getPageablePage());
        model.addAttribute("offers",offerPage.getContent());
        model.addAttribute("totalPages",offerPage.getTotalPages());
        model.addAttribute("currentPage",0);
        model.addAttribute("pageSize",10);
        return "offerAdmin";
    }

    @GetMapping("/offer/addoffer")
    public String showAddOffers(Model model)
    {
        model.addAttribute("offerObject",new OfferDTO());
        model.addAttribute("categories",categoryRepository.findAllByStatus(true));
        model.addAttribute("products",productRepository.findAllByStatus(true));
        return "add_offer";
    }

    @PostMapping("/offer/data")
    public String addOffer(@Valid @ModelAttribute("offerObject") OfferDTO offerDTO,BindingResult result,Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("offerObject",offerDTO);
            model.addAttribute("categories",categoryRepository.findAllByStatus(true));
            model.addAttribute("products",productRepository.findAllByStatus(true));
            return "add_offer";
        }

        Offer offer=offerMapper.toEntity(offerDTO);
        offer.setType(offerDTO.getType());
        offer.setTypeId(offerDTO.getTypeId());
        offer.setIsActive(true);
        offer=offersRepository.save(offer);
        if(offerDTO.getType().equalsIgnoreCase("product"))
        {
            Product product = productRepository.findById(offerDTO.getTypeId()).orElse(new Product());
//            List<Offer> productOffers = offersRepository.findByTypeAndTypeIdAndEndDateGreaterThanEqual("product",product.getId(),LocalDate.now());
//            List<Offer> categoryOffers = offersRepository.findByTypeAndTypeIdAndEndDateGreaterThanEqual("category",product.getId(),LocalDate.now());
//
//            List<Offer> allOffers = new ArrayList<>();
//            allOffers.addAll(productOffers);
//            allOffers.addAll(categoryOffers);
//            double discountValue = offerDTO.getDiscountValue();
            if(product.getAppliedOffer()!=null)
            {
                    if(product.getAppliedOffer().getDiscountValue()<offerDTO.getDiscountValue())
                    {
                        product.setAppliedOffer(offer);
                        for(ProductVariant pv : product.getProductVariants())
                        {
                            pv.setOfferPrice(pv.getPrice()-(pv.getPrice()*((double)offerDTO.getDiscountValue())/100));
                            productVariantRepository.save(pv);
                        }
                        productRepository.save(product);
                    }

            }
            else
            {
                product.setAppliedOffer(offer);
                productRepository.save(product);
                for(ProductVariant pv : product.getProductVariants())
                {
                    pv.setOfferPrice(pv.getPrice()-(pv.getPrice()*((double)offerDTO.getDiscountValue())/100));
                    productVariantRepository.save(pv);
                }
            }


        }
        else
        {
            Category category = categoryRepository.findById(offerDTO.getTypeId()).orElse(new Category());
            List<Product> products = category.getProducts();
            for(Product p : products)
            {
                System.out.println(p.getId());
            }
            for(Product product : products)
            {
                if(product.getAppliedOffer()!=null)
                {
                    if(product.getAppliedOffer().getDiscountValue()<offerDTO.getDiscountValue())
                    {

                        product.setAppliedOffer(offer);
                        for(ProductVariant pv : product.getProductVariants())
                        {
                            pv.setOfferPrice(pv.getPrice()-(pv.getPrice()*((double)offerDTO.getDiscountValue())/100));
                            productVariantRepository.save(pv);
                        }
                        productRepository.save(product);
                    }
                }
                else
                {
                    product.setAppliedOffer(offer);
                    productRepository.save(product);
                    for(ProductVariant pv : product.getProductVariants())
                    {
                        pv.setOfferPrice(pv.getPrice()-(pv.getPrice()*((double)offerDTO.getDiscountValue())/100));
                        productVariantRepository.save(pv);
                    }
                }
            }
        }
        return "redirect:/admin/offers";
    }

    @ResponseBody
    @PostMapping("/offers/sorting")
    public ResponseEntity<List<OfferDTO>> sortOffers(@RequestBody SortProductDTO sortProductDTO)
    {
        Page<Offer> offerPage = offersRepository.findAll(sortProductDTO.getPageablePage());
        List<Offer> offers = offerPage.getContent();
        List<OfferDTO> offerDTOS = offerMapper.toDTOList(offers);
        return ResponseEntity.ok(offerDTOS);
    }


    @GetMapping("/sales")
    public String showSalesReport(Model model)
    {
        Pageable pageable = PageRequest.of(0,5, Sort.Direction.ASC,"id");
        Page<OrderEntity> orderPage = orderRepository.findAllByStatus("DELIVERED",pageable);
        List<OrderEntity> orderEntities = orderRepository.findAllByStatus("DELIVERED");
        model.addAttribute("orders",orderPage.getContent());
        model.addAttribute("totalPages",orderPage.getTotalPages());

        Double overallSales = orderEntities.stream().mapToDouble(OrderEntity::getOrderTotal).sum();
        Long overallOrders = orderEntities.stream().count();
        Double overallDiscounts = orderEntities.stream().mapToDouble(OrderEntity::getDeductedAmount).sum();
        model.addAttribute("overallSales",overallSales);
        model.addAttribute("overallOrders",overallOrders);
        model.addAttribute("overallDiscounts",overallDiscounts);
        return "sales_report";
    }

    @ResponseBody
    @PostMapping("/sales/sorting")
    public ResponseEntity<SalesPaginationDTO> paginationSales(@RequestBody SalesPaginationDTO salesPaginationDTO) {
        // Determine start and end dates based on report type
        Page<OrderEntity> orderEntityPage;
        List<OrderEntity> orderEntities;
       if(salesPaginationDTO.getReportType().equalsIgnoreCase("ALL"))
       {
           orderEntityPage = orderRepository.findAllByStatus("DELIVERED",salesPaginationDTO.getPageablePage());
           orderEntities = orderRepository.findAllByStatus("DELIVERED");
       }
       else
       {
           LocalDate startDate = salesPaginationDTO.getStartDate();
           LocalDate endDate = salesPaginationDTO.getEndDate();
           System.out.println(startDate);
           if (startDate == null) {
               startDate = saleReportService.getStartOfReport(salesPaginationDTO.getReportType());
               endDate = saleReportService.getEndOfReport(salesPaginationDTO.getReportType());
           }

           // Fetch paginated and filtered data from the repository
          orderEntityPage = orderRepository.findByOrderDateBetweenAndStatus(startDate,endDate,salesPaginationDTO.getPageablePage(),"DELIVERED");
           orderEntities = orderRepository.findByOrderDateBetweenAndStatus(startDate,endDate,"DELIVERED");
       }// Map to DTO
        salesPaginationDTO.setOrdersList(salesOrderMapper.toDTOList(orderEntityPage.getContent()));
        salesPaginationDTO.setTotalPages(orderEntityPage.getTotalPages());
        salesPaginationDTO.setOverallOrders(orderEntities.stream().count());
        salesPaginationDTO.setOverallSales(orderEntities.stream().mapToDouble(OrderEntity::getOrderTotal).sum());
        salesPaginationDTO.setDiscountValue(orderEntities.stream().mapToDouble(OrderEntity::getDeductedAmount).sum());
        // Return the response
        return ResponseEntity.ok(salesPaginationDTO);
    }

    @GetMapping("/sales/download-pdf")
    public ResponseEntity<InputStreamResource> downloadPdf(@RequestParam("reportType") String reportType,
                                                           @RequestParam("startDate") String startDateString,
                                                           @RequestParam("endDate") String endDateString,
                                                           HttpServletResponse response) {
        try {
            LocalDate startDate;
            LocalDate endDate;
            if(!startDateString.equalsIgnoreCase("null"))
            {
                startDate = LocalDate.parse(startDateString);
                endDate = LocalDate.parse(endDateString);
            }
            else
            {
                startDate =null;
                endDate = null;
            }
            List<OrderEntity> orderEntities;
            if(reportType.equalsIgnoreCase("ALL"))
            {
                orderEntities = orderRepository.findAllByStatus("DELIVERED");
            }
            else
            {
                if (startDate == null) {
                    startDate = saleReportService.getStartOfReport(reportType);
                    endDate = saleReportService.getEndOfReport(reportType);
                }
                orderEntities = orderRepository.findByOrderDateBetweenAndStatus(startDate,endDate,"DELIVERED");
            }

            String filePath = "sales_report.pdf";

            // Generate the PDF
            ByteArrayOutputStream pdfContent = saleReportService.generateSalesReportPdf(filePath, orderEntities);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfContent.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sales/download-excel")
    public ResponseEntity<InputStreamResource> downloadExcel(@RequestParam("reportType") String reportType,
                                                             @RequestParam("startDate") String startDateString,
                                                             @RequestParam("endDate") String endDateString, HttpServletResponse response) {
        try {
            LocalDate startDate;
            LocalDate endDate;
            if(!startDateString.equalsIgnoreCase("null"))
            {
                startDate = LocalDate.parse(startDateString);
                endDate = LocalDate.parse(endDateString);
            }
            else
            {
                startDate =null;
                endDate = null;
            }
            List<OrderEntity> orderEntities;
            if(reportType.equalsIgnoreCase("ALL"))
            {
                orderEntities = orderRepository.findAllByStatus("DELIVERED");
            }
            else
            {
                if (startDate == null) {
                    startDate = saleReportService.getStartOfReport(reportType);
                    endDate = saleReportService.getEndOfReport(reportType);
                }
                orderEntities = orderRepository.findByOrderDateBetweenAndStatus(startDate,endDate,"DELIVERED");
            }

            String filePath = "sales_report.xlsx";

            // Generate the PDF
            ByteArrayOutputStream byteArrayOutputStream=saleReportService.generateSalesReportExcel(filePath, orderEntities);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();

        }
    }



}
