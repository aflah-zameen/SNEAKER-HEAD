package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Mappers.AddressMapper;
import com.e_commerce.SNEAKERHEAD.Mappers.CartMapper;
import com.e_commerce.SNEAKERHEAD.Mappers.ProductMapper;
import com.e_commerce.SNEAKERHEAD.Mappers.UserMapper;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import com.e_commerce.SNEAKERHEAD.Service.*;
import com.razorpay.RazorpayClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IModel;

import java.beans.Transient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {



    @Autowired
    ProductService productService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Autowired
    AdminManagementService adminManagementService;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ProductVariantRepository productVariantRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemsRepository orderItemsRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CartMapper cartMapper;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    WishlistRepository wishlistRepository;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CouponService couponService;

    @Autowired
    UserCouponUsageRepository userCouponUsageRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    RefferalCodeRepository refferalCodeRepository;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    UserMapper userMapper;

    @GetMapping("/home")
    public String AdminProduct()
    {
        return "index";
    }
//    @GetMapping("/women")
//    public String WomenProduct(Model model,HttpServletRequest request)
//    {
//        HttpSession session = request.getSession();
//        String email =(String)session.getAttribute("userEmail");
//        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
//        Category category = categoryRepository.findByName("Women").orElseThrow(()->new NullPointerException());
//        List<ProductDto> products = productService.categoryProduct(category,user.getId());
//        List<Brand> brands = brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
//        model.addAttribute("products",products);
//        model.addAttribute("url","women");
//        model.addAttribute("category","Women");
//        model.addAttribute("brands",brands);
//        model.addAttribute("breadcrumb","Women");
//        return "productList";
//    }

//    @GetMapping("/product/filter")
//    public ResponseEntity<List<ProductDto>> filterWomenProducts(@RequestParam String filterType,@RequestParam String filterValue,@RequestParam String category,Model model)
//    {
////        if(category.equals("Available") || category.equals("Search"))
////        {
////
////            return ResponseEntity.ok(productDtos);
////        }
////        else {
////            Category categoryObject = categoryRepository.findByName(category).orElseThrow(() -> new NullPointerException());
////            List<ProductDto> productDTOs = productService.filterProduct(categoryObject, filterValue);
////            return ResponseEntity.ok(productDTOs);
////        }
//        List<ProductDto> productDtos=productService.filterProduct(filterType,filterValue);
//        if(productDtos.isEmpty())
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(productDtos);
//        else
//        return ResponseEntity.ok(productDtos);
//    }


//    @GetMapping("/men")
//    public String MenProduct(Model model,HttpServletRequest request)
//    {
//        HttpSession session = request.getSession();
//        String email =(String)session.getAttribute("userEmail");
//        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
//        List<Brand> brands = brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
//        List<Category> categories = categoryRepository.findAll().stream().filter(Category::getStatus).toList();
//        Map <String,String> filter = new HashMap<>();
//        filter.put("category","men");
//        Page<ShopProductDTO> productDtos = productService.getProducts(0,6,"id","ASC",null, filter,user.getId());
//        model.addAttribute("products",productDtos);
//        model.addAttribute("url","men");
//        model.addAttribute("category","Men");
//        model.addAttribute("breadcrumb","Men");
//        model.addAttribute("brands",brands);
//        model.addAttribute("categories",categories);
//
//        return "productList";
//    }

    @GetMapping("/product/detail/{id}")
    public String EditProduct(@PathVariable("id") Long id, Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        ProductDto details = productMapper.toDTO(productRepository.findById(id).orElse(new Product()));
        if(details.getAppliedOffer()!=null && details.getAppliedOffer().getEndDate().isBefore(LocalDate.now()))
        {
            for(ProductVariantDTO variant : details.getProductVariantDTOs())
                variant.setOfferPrice(null);
            details.setAppliedOffer(null);
            details.getDefaultVariantDTO().setOfferPrice(null);
        }

        System.out.println(details);
        model.addAttribute("userId",user.getId());
        model.addAttribute("details",details);
        model.addAttribute("categoryUrl",details.getCategoryName().toLowerCase());
        return "productdetails";
    }

    @GetMapping("product/buyNow")
    public String buyProduct(@RequestParam int selectedQuantity,
                             @RequestParam double price,
                             @RequestParam Long variantId,
                             @RequestParam String selectedSize, HttpServletRequest request,Model model)
    {

        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String) session.getAttribute("userEmail")).orElse(new WebUser());
        UserAddress address = addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE").stream().filter(a->a.getDefaultAddressStatus()).findFirst().orElse(new UserAddress());
        Double totalPrice = price*selectedQuantity;
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String FormattedSubTotal = formatter.format(totalPrice);
        model.addAttribute("addressObject",new AddressDTO());
        model.addAttribute("defaultAddress",address);
        model.addAttribute("subtotalFormatted",FormattedSubTotal);
        model.addAttribute("subtotal",totalPrice);
        model.addAttribute("variantId",variantId);
        model.addAttribute("quantity",selectedQuantity);
        model.addAttribute("selectedSize",selectedSize);
        return "direct-checkout";
    }

    @PostMapping("/product/buy")
    public ResponseEntity<?>  buyProduct( @RequestBody OrderDto orderDto,HttpServletRequest request)
    {

        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String) session.getAttribute("userEmail")).orElse(new WebUser());
        OrderEntity order = new OrderEntity();
        UserAddress address = addressRepository.findById(orderDto.getAddressId()).orElse(new UserAddress());
        ProductVariant productVariant = productVariantRepository.findById(orderDto.getVariantId()).orElse(new ProductVariant());
        order.setUser(user);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setPaymentMethod(orderDto.getPaymentMethod());
        order.setStatus("PENDING");
        order.setOrderTotal(productVariant.getPrice()*orderDto.getQuantity());
        OrderItems orderItems = new OrderItems();
        orderItems.setPrice(productVariant.getPrice());
        orderItems.setProductVariant(productVariant);
        orderItems.setQuantity(orderDto.getQuantity());
        orderItems.setOrder(orderRepository.save(order));
        orderItemsRepository.save(orderItems);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/overview")
    public String showOverview(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElse(new WebUser());
        model.addAttribute("user",user);
        return "overview";
    }

    @GetMapping("/orders")
    public String showOrders(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        List<ProductVariant> productVariants = new ArrayList<>();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        List<OrderEntity> orders = orderRepository.findAllByUser_id(user.getId());
        orders=orders.stream().sorted(Comparator.comparingLong(OrderEntity::getId).reversed()).toList();
//        Collections.reverse(orders);
//        orders = orders.stream()
//                .sorted(Comparator.comparing(Order::isCancellation)) // Sort false first, true later
//                .collect(Collectors.toList());
        model.addAttribute("orders",orders);
        model.addAttribute("user",user);
        return "orders";
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
    public ResponseEntity<?> returnOrder(@RequestParam("orderId") Long orderId,@RequestParam("returnReason") String reason)
    {
        OrderEntity order = orderRepository.findById(orderId).orElse(new OrderEntity());
        order.setReturnReason(reason);
        order.setStatus("RETURN");
        orderRepository.save(order);
        return ResponseEntity.ok("Success");
    }

    @ResponseBody
    @GetMapping("/orders/invoice")
    public ResponseEntity<InputStreamResource> downloadInvoice(@RequestParam("orderId") Long orderId){
       try{
           String filePath = "invoice_"+orderId+".pdf";
           ByteArrayOutputStream pdfContent = invoiceService.generateInvoicePdf(orderId);
           ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfContent.toByteArray());

           HttpHeaders headers = new HttpHeaders();
           headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+filePath);

           return ResponseEntity.ok()
                   .headers(headers)
                   .contentType(MediaType.APPLICATION_PDF)
                   .body(new InputStreamResource(inputStream));

       }
       catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity.internalServerError().build();
       }

    }


    @GetMapping("/editprofile")
    public String editProfile(Model  model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        UserDTO userDto = new UserDTO();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setPhone(user.getPhone());
        userDto.setGender(user.getGender());
        userDto.setEmail(user.getEmail());
        userDto.setJoinDate(user.getJoinDate());
        model.addAttribute("userObject",userDto);
        return "editprofile";
    }
    @PostMapping("/editprofile")
    public String dataProfile(@Valid @ModelAttribute("userObject") UserDTO userDto, BindingResult result, HttpServletRequest request)
    {
        if(result.hasErrors())
        {
            return "editprofile";
        }
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        user.setPhone(userDto.getPhone());
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        userRepository.save(user);
        session.setAttribute("userEmail",userDto.getEmail());
        session.setAttribute("userName",userDto.getFullName());
        return "redirect:/user/overview";
    }

    @GetMapping("/addresses")
    public String showaddresses(Model model, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        List<UserAddress> userAddresses = addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE" );
        AddressDTO defaultAddressDTO = new AddressDTO();
        for(UserAddress y : userAddresses)
        {
            if(y.getDefaultAddressStatus()==true)
            {
                defaultAddressDTO.setId(y.getId());
                defaultAddressDTO.setUser(userMapper.toDTO(user));
                defaultAddressDTO.setName(y.getName());
                defaultAddressDTO.setPhone(y.getPhone());
                defaultAddressDTO.setBuilding(y.getBuilding());
                defaultAddressDTO.setCity(y.getCity());
                defaultAddressDTO.setCountry(y.getCountry());
                defaultAddressDTO.setState(y.getState());
                defaultAddressDTO.setLandmark(y.getLandmark());
                defaultAddressDTO.setInstruction(y.getInstruction());
                defaultAddressDTO.setZipCode(y.getZipCode());
                defaultAddressDTO.setType(y.getType());
                defaultAddressDTO.setStreet(y.getStreet());
            }
        }
        if(userAddresses.isEmpty())
        {
            userAddresses=null;
        }
        model.addAttribute("defaultAddress", defaultAddressDTO);
        model.addAttribute("allAddress",userAddresses);
        model.addAttribute("user",user);
        return "addresses";
    }
    @GetMapping("/addresses/add")
    public String showAddAddresses(Model model)
    {
        model.addAttribute("addressObject",new AddressDTO());
        return "editaddresses";
    }
    @GetMapping("/addresses/edit/{id}")
    public String editAddress(@PathVariable Long id,Model model)
    {
        UserAddress address = addressRepository.findById(id).orElse(new UserAddress());
        AddressDTO addressDTO = addressMapper.toDTO(address);
        model.addAttribute("addressObject",addressDTO);
        return "editaddresses";
    }

    @PostMapping("/address/data")
    public String addAddress(@Valid @ModelAttribute("addressObject") AddressDTO addressDto, BindingResult result, HttpServletRequest request, Model model)
    {
        System.out.println(addressDto.getId()+">>>>>>");
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        if(result.hasErrors())
        {
            return "editaddresses";
        }
        if(addressDto.getId()==null)
        {
            System.out.println("heyyy from addAddress");
            userService.addAddress(addressDto,user);
        }
        else
        {
            UserAddress address = addressMapper.toEntity(addressDto);
            List<UserAddress> addresses =addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE").stream().filter(a -> a.getDefaultAddressStatus()).collect(Collectors.toList());
            if(addresses.isEmpty())
            {
                address.setDefaultAddressStatus(true);
            }
            for(UserAddress add : addresses)
            {
                if(add.getId() == address.getId())
                {
                    if(add.getDefaultAddressStatus())
                    {
                        address.setDefaultAddressStatus(true);
                    }
                }
            }
            address.setStatus("AVAILABLE");
            address.setUser(user);
            addressRepository.save(address);
        }
        return "redirect:/user/addresses";
    }

    @GetMapping("/addresses/default/{id}")
    public String setDefault(@PathVariable Long id, HttpServletRequest request)
    {
        UserAddress address = addressRepository.findById(id).orElseThrow(()-> new NullPointerException());
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        List<UserAddress> userAddresses = addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE" );
        for(UserAddress y : userAddresses)
        {
            if(y.getDefaultAddressStatus()==true)
            {
               y.setDefaultAddressStatus(false);
            }
        }
        address.setDefaultAddressStatus(true);
        addressRepository.save(address);
        return "redirect:/user/addresses";
    }
    @GetMapping("/addresses/remove/{id}")
    public String removeAddress(@PathVariable Long id,HttpServletRequest request)
    {
        UserAddress address = addressRepository.findById(id).orElseThrow(()->new NullPointerException());
        address.setStatus("UNAVAILABLE");
        addressRepository.save(address);
        return "redirect:/user/addresses";
    }

    @GetMapping("/wishlist")
    public String showWhishlist(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        List<Wishlist> wishlists = wishlistRepository.findAllByUser_id(user.getId());
        List<Product> products = productRepository.findAllByStatus(true).stream().filter(pd -> wishlists.stream().anyMatch(wi-> wi.getProduct().getId().equals(pd.getId()))).collect(Collectors.toList());
        List<ProductDto> productDtos = productMapper.toDTOList(products).stream().peek(pd -> { pd.getProductVariantDTOs().stream().peek(pv -> pv.setFormattedPrice(pv.FormattedPrice(pv.getPrice())));
                                                                                                        pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(new ProductVariantDTO()));
                                                                                                        }).collect(Collectors.toList());
        for(ProductDto p : productDtos)
        {
            if(p.getAppliedOffer()!=null && p.getAppliedOffer().getEndDate().isBefore(LocalDate.now()))
            {
                p.setAppliedOffer(null);
                p.getDefaultVariantDTO().setOfferPrice(null);
            }
        }
        model.addAttribute("products",productDtos);
        return "wishlist";
    }

    @ResponseBody
    @GetMapping("/wishlist/data")
    public List<ProductVariantDTO>  fetchModal(@RequestParam("productId") Long productId)
    {
        Product product = productRepository.findById(productId).orElse(new Product());
        Map<String,String[]> colorSizes = new HashMap<>();
        return productMapper.toDTO(product).getProductVariantDTOs();
    }

    @ResponseBody
    @PostMapping("/wishlist/addCart")
    public ResponseEntity<?> addToCart(@RequestParam("variantId") Long variantId,
                                       @RequestParam("quantity") Integer quantity,
                                       @RequestParam("size") String size,
                                       HttpServletRequest request
                                       )
    {
        HttpSession session = request.getSession();
        String email = (String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        ProductVariant productVariant = productVariantRepository.findById(variantId).orElseThrow(() ->new NullPointerException());
                Cart cart = new Cart();
                cart.setUser(user);
                cart.setSize(Double.parseDouble(size));
                cart.setQuantity(quantity);
                cart.setProductVariant(productVariant);
                cart.setTotalAmount(((double)quantity)*(productVariant.getOfferPrice() != null ? productVariant.getOfferPrice() : productVariant.getPrice()));
                cartRepository.save(cart);
                return ResponseEntity.ok("Added successfully");
    }


    @GetMapping("/cart")
    public String showCart(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        String email = (String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        List<Cart> carts = cartRepository.findAllByUser_id(user.getId()).stream().filter(ct ->
                ct.getProductVariant().getProduct().getStatus()).toList();
        if(carts.isEmpty())
            carts=null;
        else {
            Double subTotal = 0D;
            for (Cart cart : carts) {
                if(cart.getProductVariant().getProduct().getAppliedOffer() != null &&cart.getProductVariant().getProduct().getAppliedOffer().getEndDate().isBefore(LocalDate.now()))
                {
                    cart.getProductVariant().setOfferPrice(null);
                    cart.getProductVariant().getProduct().setAppliedOffer(null);
                }
                subTotal = subTotal + cart.getTotalAmount();
            }

            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            String FormattedSubTotal = formatter.format(subTotal);
            session.setAttribute("subtotal", subTotal);
            session.setAttribute("subtotalFormatted", FormattedSubTotal);
            model.addAttribute("subtotalFormatted", FormattedSubTotal);
            model.addAttribute("subtotal", subTotal);
        }
        model.addAttribute("carts", carts);
        return "cart";
    }

    @ResponseBody
    @GetMapping("/cart/data/")
    public ResponseEntity<?> submitCheckout(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        Long userId = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException()).getId();
        List<Cart> carts = cartRepository.findAllByUser_id(userId);
        if(carts.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products in the cart");
        }
        for(Cart cart : carts)
        {
            if(cart.getQuantity() > cart.getProductVariant().getQuantity())
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Remove the out of stock product");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Product ready for the checkout");
    }

    @ResponseBody
    @PostMapping("/addcart")
    public ResponseEntity<?> addCartPost(@RequestBody CartDTO cartDto, BindingResult result, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email = (String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        ProductVariant productVariant = productVariantRepository.findById(cartDto.getProductVariantId()).orElse(new ProductVariant());
        if(result.hasErrors())
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product cannot be added");
        }
        else if(cartDto.getQuantity() > productVariant.getQuantity() )
        {
            Map<String, Object> response = new HashMap<>();
            response.put("quantity", productVariant.getQuantity());
            response.put("message","Lack of products");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        cartDto.setTotalAmount(productVariant.getOfferPrice() != null && productVariant.getProduct().getAppliedOffer().getEndDate().isAfter(LocalDate.now()) ? productVariant.getOfferPrice()*cartDto.getQuantity()
                : productVariant.getPrice()*cartDto.getQuantity());
        Cart newCart = cartMapper.toEntity(cartDto);
        newCart.setUser(user);
        cartRepository.save(newCart);
        return ResponseEntity.status(HttpStatus.OK).body("Product added to cart");
    }

    @Transactional
    @PostMapping("/cart/remove/{id}")
    public ResponseEntity<?> deleteCartProduct(@PathVariable Long id,HttpServletRequest request)
    {
        cartRepository.deleteById(id);
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        Long userId = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException()).getId();
        List<Cart> carts = cartRepository.findAllByUser_id(userId);
        Double subtotal= 0D;
        for(Cart cart : carts)
        {
            subtotal += cart.getTotalAmount();
        }
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String FormattedSubTotal =  formatter.format(subtotal);
        Map<String, Object> response = new HashMap<>();
        response.put("subtotal", FormattedSubTotal);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        String email = (String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        UserAddress address = addressRepository.findByUser_idAndDefaultAddressStatusAndStatus(user.getId(), true,"AVAILABLE").orElse(new UserAddress());
        Wallet wallet = walletRepository.findByUser_id(user.getId()).orElse(new Wallet());
        if(wallet.getId() == null)
        {
            wallet.setUser(user);
            wallet.setBalance(0D);
            wallet.setLastUpdate(LocalDate.now());
            walletRepository.save(wallet);
        }
        String FormattedSubTotal = (String) session.getAttribute("subtotalFormatted");
        OrderDto orderDto = new OrderDto();
        orderDto.setVariantId((Long)session.getAttribute("productVariantId"));
        orderDto.setQuantity((Integer)session.getAttribute("productQuantity"));
        List<Coupon> coupons = couponService.ListCoupons(user);
        model.addAttribute("subtotal",   session.getAttribute("subtotal"));
        model.addAttribute("addressObject",new AddressDTO());
        model.addAttribute("defaultAddress",address);
        model.addAttribute("subtotalFormatted",FormattedSubTotal);
        model.addAttribute("coupons",coupons);
        model.addAttribute("wallet",wallet);
        return "checkout";
    }

    @PostMapping("/checkout/address/data")
    public ResponseEntity<?> addCheckoutAddress(@RequestBody @Valid AddressDTO addressDto, BindingResult result, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        if(result.hasErrors())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some thing gone wrong");
        }
        userService.addAddress(addressDto,user);

        return ResponseEntity.status(HttpStatus.OK).body("Created");
    }

    @ResponseBody
    @PostMapping("/checkout")
    public ResponseEntity<?> postCheckout(@RequestBody OrderDto orderDto,BindingResult result,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email=(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        List<Cart> carts = cartRepository.findAllByUser_id(user.getId());
        if(carts.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cart is empty");
        }
        if(result.hasErrors())
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Order is not accepted");
        }
        OrderEntity order = new OrderEntity();
        UserAddress userAddress=addressRepository.findById(orderDto.getAddressId()).orElseThrow(()->new NullPointerException());
        Double orderTotal =(Double)session.getAttribute("subtotal");
        if(orderDto.getCouponId()!=0)
        {
            Coupon coupon = couponRepository.findById(orderDto.getCouponId()).orElse(new Coupon());
            UserCouponUsage userCouponUsage = userCouponUsageRepository.findByUser_idAndCoupon_id(user.getId(),coupon.getId()).orElse(new UserCouponUsage());
            if(coupon.getDiscountValue()!=null)
            {
                if(coupon.getDiscountType().equalsIgnoreCase("percentage"))
                {
                    orderTotal = orderTotal*(coupon.getDiscountValue()/100);
                }
                else
                {
                    orderTotal = orderTotal-coupon.getDiscountValue();
                }
                if(userCouponUsage.getId() != null)
                {
                    userCouponUsage.setUsageCount(userCouponUsage.getUsageCount()+1);
                    coupon.setUsedCount(coupon.getUsedCount()+1);
                }
                else
                {
                    userCouponUsage.setCoupon(coupon);
                    userCouponUsage.setUser(user);
                    userCouponUsage.setUsageCount(1);
                    coupon.setUsedCount(coupon.getUsedCount()+1);
                }
            }
            couponRepository.save(coupon);
            userCouponUsageRepository.save(userCouponUsage);
            if(coupon.getId() != null)
            {
                order.setCoupon(coupon);
            }
            else
            {
                order.setCoupon(null);
            }

        }
        if(orderDto.getReferralId() !=0)
        {
            ReferralEntity referralEntity = refferalCodeRepository.findById(orderDto.getReferralId()).orElse(new ReferralEntity());
            order.setReferralEntity(referralEntity);

        }
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setOrderTotal(orderDto.getOrderTotal());
        order.setAddress(userAddress);
        order.setDeductedAmount(orderTotal-orderDto.getOrderTotal());
        order.setPaymentMethod(orderDto.getPaymentMethod());
        if(orderDto.getPaymentMethod().equalsIgnoreCase("cod"))
        {
            order.setStatus("PENDING");

        }
        else
        {
            order.setStatus("PAYMENTED");
        }
        OrderEntity newOrder = orderRepository.save(order);
        for(Cart cart : carts)
        {
            OrderItems orderItems = new OrderItems();
            orderItems.setOrder(newOrder);
            orderItems.setPrice(cart.getProductVariant().getPrice());
            orderItems.setQuantity(cart.getQuantity());
            orderItems.setProductVariant(cart.getProductVariant());
            orderItemsRepository.save(orderItems);
        }
        orderService.updateCartAndStock(carts,user.getId());
        return ResponseEntity.status(HttpStatus.OK).body("order placed");

    }

    @ResponseBody
    @PostMapping("/checkout/applyCoupon")
    public ResponseEntity<?> addCoupon(@RequestParam(name = "couponId", defaultValue = "0") Long id,@RequestParam("subtotal") Double subtotal)
    {
        Map<String,String> response = new HashMap<>();
        Coupon coupon = couponRepository.findById(id).orElse(new Coupon());
        if(coupon!=null)
        {
            String discountType = coupon.getDiscountType();
            Double price = coupon.getDiscountValue();
            if(discountType.equalsIgnoreCase("percentage"))
            {
                price = subtotal * (price/100);
                subtotal = subtotal - price;
            }
            else {
                subtotal = subtotal - price;
            }

            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            String FormattedSubTotal = formatter.format(subtotal);
            String FormattedDiscountValue = formatter.format(price);

            response.put("couponCode",coupon.getCouponCode().toUpperCase());
            response.put("FormattedSubtotal",FormattedSubTotal);
            response.put("FormattedDiscountValue",FormattedDiscountValue);
            response.put("couponId",String.valueOf(coupon.getId()));

            return ResponseEntity.ok(response);
        }
        else {
            response.put("errorMessage","Promo code("+coupon.getCouponCode()+") is not valid!");
            return  ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model)
    {
        List <ProductDto> productDtos=productService.searchProducts(keyword);
        List<Brand> brands = brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
        model.addAttribute("products",productDtos);
        model.addAttribute("url","search");
        model.addAttribute("category","Search");
        model.addAttribute("breadcrumb","Search");
        model.addAttribute("keyword",keyword);
        model.addAttribute("brands",brands);
        model.addAttribute("breadcrumb","Men");

        return "productList";
    }

    @GetMapping("/shop")
    public String showShop(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElse(new WebUser());
        List<Brand> brands = brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
        List<Category> categories = categoryRepository.findAll().stream().filter(Category::getStatus).toList();
        Page<ShopProductDTO> productDtos = productService.getProducts(0,6,"id","ASC",null,null,user != null ? user.getId():null);
        model.addAttribute("products",productDtos);
        model.addAttribute("url","shop");
        model.addAttribute("category","Available");
        model.addAttribute("breadcrumb","Shop");
        model.addAttribute("brands",brands);
        model.addAttribute("categories",categories);

        return "productList";
    }

    //wishlist
    @PostMapping("/wishlist/add")
    public ResponseEntity<?> addToWishList(@RequestParam(name = "productId") Long id,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        Product product = productRepository.findById(id).orElse(new Product());
        Wishlist wishlist  = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlist.setAddedDate(LocalDate.now());
        wishlistRepository.save(wishlist);
        session.setAttribute("wishlistCount",((Integer) session.getAttribute("wishlistCount"))+1);
        return ResponseEntity.ok("completed");
    }

    @Transactional
    @DeleteMapping("/wishlist/remove")
    public ResponseEntity<?> deleteFromWishlist(@RequestParam(name="productId") Long id,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        Long wishlistId = wishlistRepository.findByUser_idAndProduct_id(user.getId(),id).orElse(new Wishlist()).getId();
        wishlistRepository.deleteById(wishlistId);
        session.setAttribute("wishlistCount",((Integer) session.getAttribute("wishlistCount"))-1);
        return ResponseEntity.ok("completed");
    }

    @Value("${RAZORPAY_KEY_ID}")
    private String razorpayKeyId;

    @Value("${RAZORPAY_KEY_SECRET}")
    private String razorpayKeySecret;

    @GetMapping("/razorpay-key")
    public ResponseEntity<Map<String, String>> getRazorpayKey() {
        return ResponseEntity.ok(Collections.singletonMap("key", razorpayKeyId));
    }


    @ResponseBody
    @GetMapping("/razorpay")
    public ResponseEntity<?> createOrder(@RequestParam double amount) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            // Create the order request
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100)); // Convert to paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            // Call Razorpay API to create order
            com.razorpay.Order order = razorpayClient.orders.create(orderRequest);

            // Return order details to frontend
            return ResponseEntity.ok(order.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @ResponseBody
    @PostMapping("/razorpay")
    public ResponseEntity<?> addDetails(@RequestParam("couponId") Long couponId ,
                                        @RequestParam("addressId") Long addressId,
                                        @RequestParam("selectedMethod") String selectedMethod,
                                        @RequestParam("amount") Double amount,HttpServletRequest request,
                                        @RequestParam(name = "status", required = false,defaultValue = "PAID") String status,
                                        @RequestParam(name = "referralId") Long referralId)
    {
        System.out.println("razorpay = "+selectedMethod);
        HttpSession session = request.getSession();
        String email=(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        Double orderTotal =(Double)session.getAttribute("subtotal");
        List<Cart> carts = cartRepository.findAllByUser_id(user.getId());

        //-update-transaction
        Transaction transaction = new Transaction();
        transaction.setStatus("DEBITED");
        transaction.setAmount(amount/100);
        transaction.setTransactionDate(LocalDate.now());
        transactionRepository.save(transaction);

        //update-payment-transaction-tables

        Payment payment = new Payment();
        payment.setPaymentDate(LocalDate.now());
        payment.setTransaction(transaction);
        payment.setPaymentMethod(selectedMethod);
        if(status.equalsIgnoreCase("unpaid"))
            payment.setStatus("UNPAID");
        else
            payment.setStatus("SUCCESS");
        payment.setUser(user);
        paymentRepository.save(payment);

        OrderEntity order = new OrderEntity();
        UserAddress userAddress=addressRepository.findById(addressId).orElseThrow(()->new NullPointerException());

        if(couponId!=0)
        {
            Coupon coupon = couponRepository.findById(couponId).orElse(new Coupon());
            order.setUser(user);
            if(coupon.getId() != null)
            {
                order.setCoupon(coupon);
            }
            else
            {
                order.setCoupon(null);
            }

            UserCouponUsage userCouponUsage = userCouponUsageRepository.findByUser_idAndCoupon_id(user.getId(),coupon.getId()).orElse(new UserCouponUsage());
            if(userCouponUsage.getId() != null)
            {
                userCouponUsage.setUsageCount(userCouponUsage.getUsageCount()+1);
                coupon.setUsedCount(coupon.getUsedCount()+1);
                couponRepository.save(coupon);
                userCouponUsageRepository.save(userCouponUsage);
            }
            else
            {
                userCouponUsage.setCoupon(coupon);
                userCouponUsage.setUser(user);
                userCouponUsage.setUsageCount(1);
                coupon.setUsedCount(coupon.getUsedCount()+1);
                couponRepository.save(coupon);
                userCouponUsageRepository.save(userCouponUsage);
            }
        }
        System.out.println(referralId);
        if(referralId != 0)
        {
            System.out.println(referralId);
            ReferralEntity referralEntity = refferalCodeRepository.findById(referralId).orElse(new ReferralEntity());
            order.setReferralEntity(referralEntity);
        }
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setPayment(payment);
        order.setOrderTotal(amount/100);
        order.setAddress(userAddress);
        order.setPaymentMethod(selectedMethod);
        order.setDeductedAmount(orderTotal-(amount/100));
        if(status.equalsIgnoreCase("unpaid"))
            order.setStatus("UNPAID");
        else
            order.setStatus("PAYMENTED");
        OrderEntity newOrder = orderRepository.save(order);
        for(Cart cart : carts)
        {
            OrderItems orderItems = new OrderItems();
            orderItems.setOrder(newOrder);
            orderItems.setPrice(cart.getProductVariant().getPrice());
            orderItems.setQuantity(cart.getQuantity());
            orderItems.setProductVariant(cart.getProductVariant());
            orderItemsRepository.save(orderItems);
        }
        orderService.updateCartAndStock(carts,user.getId());

        return ResponseEntity.ok("Success");


    }

    @ResponseBody
    @PutMapping("/razorpay")
    public ResponseEntity<?> repaymentStatus(@RequestParam("orderId") Long orderId)
    {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(new OrderEntity());
        orderEntity.setStatus("PAYMENTED");
        Payment payment = orderEntity.getPayment();
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);
        orderRepository.save(orderEntity);
        return ResponseEntity.ok("updated successfully");
    }


    //wallet-controller
    @GetMapping("/wallet")
    public String viewWallet(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());

        Wallet wallet = walletRepository.findByUser_id(user.getId()).orElse(new Wallet());

        if(wallet.getBalance() == null)
        {
            wallet.setBalance(0D);
            wallet.setUser(user);
            walletRepository.save(wallet);
        }
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String balance = formatter.format(wallet.getBalance());
        model.addAttribute("wallet", wallet);
        model.addAttribute("balance", balance);
        return "wallet_user";
    }

    @GetMapping("/wallet/history")
    public String viewHistory(Model model,HttpServletRequest request)
    {

        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());

        Wallet wallet = walletRepository.findByUser_id(user.getId()).orElse(new Wallet());
        List<Transaction> transactionList;
        if(wallet.getId() != null)
        {
            transactionList = transactionRepository.findAllByWallet_id(wallet.getId()).stream().sorted(Comparator.comparingLong(Transaction::getId)).collect(Collectors.toList());
            Collections.reverse(transactionList);
        }
        else
        {
          transactionList = null;
        }
        model.addAttribute("transactions",transactionList);

        return "transaction_user";
    }

    @ResponseBody
    @PostMapping("/wallet/addMoney")
    public ResponseEntity<String> addMoney(@RequestParam(name = "amount") Double amount,HttpServletRequest request)
    {

        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());


        Wallet wallet = walletRepository.findByUser_id(user.getId()).orElse(new Wallet());
        if(wallet.getBalance() != null)
        {
            wallet.setBalance(wallet.getBalance()+amount);
        }
        else
        {
            wallet.setBalance(amount);
        }
        wallet.setLastUpdate(LocalDate.now());
        wallet.setUser(user);
        wallet = walletRepository.save(wallet);
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDate.now());
        transaction.setStatus("CREDITED");
        transaction.setAmount(amount);
        transaction.setWallet(wallet);
        transactionRepository.save(transaction);
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String formattedPrice = formatter.format(wallet.getBalance());
        return ResponseEntity.ok(formattedPrice);
    }

    @ResponseBody
    @PostMapping("/wallet/purchase")
    public ResponseEntity<?> walletPurchase(@RequestParam(name = "couponId",defaultValue = "0") Long couponId ,
                                            @RequestParam("addressId") Long addressId,
                                            @RequestParam("selectedMethod") String selectedMethod,
                                            @RequestParam("amount") Double amount,
                                            @RequestParam(name = "referralId") Long referralId,
                                            HttpServletRequest request)
    {
        System.out.println("wallet = "+selectedMethod);
        HttpSession session = request.getSession();
        String email=(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        Wallet wallet = walletRepository.findByUser_id(user.getId()).orElse(new Wallet());
        List<Cart> carts = cartRepository.findAllByUser_id(user.getId());
        Double orderTotal =(Double)session.getAttribute("subtotal");

        //-update-transaction
        wallet.setBalance(wallet.getBalance()-amount);
        walletRepository.save(wallet);

        //-update-transaction
        Transaction transaction = new Transaction();
        transaction.setStatus("DEBITED");
        transaction.setAmount(amount);
        transaction.setWallet(wallet);
        transactionRepository.save(transaction);

        //update-payment-transaction-tables

        Payment payment = new Payment();
        payment.setPaymentDate(LocalDate.now());
        payment.setTransaction(transaction);
        payment.setPaymentMethod(selectedMethod);
        payment.setStatus("SUCCESS");
        payment.setUser(user);
        paymentRepository.save(payment);

        OrderEntity order = new OrderEntity();
        UserAddress userAddress=addressRepository.findById(addressId).orElseThrow(()->new NullPointerException());

        if(couponId!=0)
        {
            Coupon coupon = couponRepository.findById(couponId).orElse(new Coupon());
            order.setUser(user);
            if(coupon != null)
            {
                order.setCoupon(coupon);
            }
            else
            {
                order.setCoupon(null);
            }

            UserCouponUsage userCouponUsage = userCouponUsageRepository.findByUser_idAndCoupon_id(user.getId(),coupon.getId()).orElse(new UserCouponUsage());
            if(userCouponUsage.getId() != null)
            {
                userCouponUsage.setUsageCount(userCouponUsage.getUsageCount()+1);
                coupon.setUsedCount(coupon.getUsedCount()+1);
                couponRepository.save(coupon);
                userCouponUsageRepository.save(userCouponUsage);
            }
            else
            {
                userCouponUsage.setCoupon(coupon);
                userCouponUsage.setUser(user);
                userCouponUsage.setUsageCount(1);
                coupon.setUsedCount(coupon.getUsedCount()+1);
                couponRepository.save(coupon);
                userCouponUsageRepository.save(userCouponUsage);
            }
        }
        if(referralId !=0)
        {
            System.out.println(referralId);
            ReferralEntity referralEntity = refferalCodeRepository.findById(referralId).orElse(new ReferralEntity());
            order.setReferralEntity(referralEntity);

        }
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setPayment(payment);
        order.setOrderTotal(amount);
        System.out.println(orderTotal+">>>"+amount);
        order.setDeductedAmount(orderTotal-amount);
        order.setAddress(userAddress);
        order.setPaymentMethod(selectedMethod);
        order.setStatus("PAYMENTED");
        OrderEntity newOrder = orderRepository.save(order);
        for(Cart cart : carts)
        {
            OrderItems orderItems = new OrderItems();
            orderItems.setOrder(newOrder);
            orderItems.setPrice(cart.getProductVariant().getPrice());
            orderItems.setQuantity(cart.getQuantity());
            orderItems.setProductVariant(cart.getProductVariant());
            orderItemsRepository.save(orderItems);
        }
        orderService.updateCartAndStock(carts,user.getId());

        return ResponseEntity.ok("Success");


    }

    @GetMapping("/referral")
    public ResponseEntity<?> sendReferral(@RequestParam("email") String email,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String userEmail=(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(userEmail).orElseThrow(()->new NullPointerException());
        String referralCode;
        if(refferalCodeRepository.existsByUser_id(user.getId()))
        {
            ReferralEntity referralEntity = refferalCodeRepository.findByUser_id(user.getId()).orElse(new ReferralEntity());
            referralCode = referralEntity.getReferralCode();
        }
        else
        {
            referralCode = userService.generateReferralCode(user.getId());
            ReferralEntity referralEntity = new ReferralEntity();
            referralEntity.setUser(user);
            referralEntity.setReferralCode(referralCode);
            referralEntity.setUsageCount(0);
            refferalCodeRepository.save(referralEntity);
        }
        userService.sendReferralCodeEmail(email,referralCode);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/referral/check")
    public ResponseEntity<?> applyRefferal(@RequestParam("referralCode") String referralCode,@RequestParam("subtotal") Double subtotal)
    {
        Map<String,String> response = new HashMap<>();
        ReferralEntity referralEntity = refferalCodeRepository.findByReferralCode(referralCode).orElse(new ReferralEntity());
        if(referralEntity.getId() == null)
        {
            return  ResponseEntity.badRequest().body("bad request");
        }
        referralEntity.setUsageCount(referralEntity.getUsageCount()+1);
        subtotal=subtotal-1000;
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String FormattedSubTotal = formatter.format(subtotal);

        response.put("couponCode",referralCode);
        response.put("FormattedSubtotal",FormattedSubTotal);
        response.put("FormattedDiscountValue",formatter.format(1000));
        response.put("id",String.valueOf(referralEntity.getId()));
        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @PostMapping("/shop")
    public ResponseEntity<Page<ShopProductDTO>> getProducts(@RequestBody ShopRequestDTO shopRequestDTO,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String userEmail=(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(userEmail).orElseThrow(NullPointerException::new);

       Page<ShopProductDTO> products =  productService.getProducts(shopRequestDTO.getPage(),shopRequestDTO.getSize(),shopRequestDTO.getSortBy(),shopRequestDTO.getSortDirection(),shopRequestDTO.getSearchQuery(),shopRequestDTO.getFilters(),user.getId());
       return ResponseEntity.ok(products);
    }

    @ResponseBody
    @PutMapping("/profile/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassword changePassword,BindingResult result,HttpServletRequest request)
    {
        System.out.println(changePassword);
        if(result.hasErrors())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        HttpSession session = request.getSession();
        String userEmail=(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(userEmail).orElseThrow(NullPointerException::new);

        return adminManagementService.changePassword(changePassword,user);
    }


}
