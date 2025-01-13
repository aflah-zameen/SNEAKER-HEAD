package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Mappers.OfferMapper;
import com.e_commerce.SNEAKERHEAD.Mappers.ProductMapper;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductVariantRepository productVariantRepository;

    @Autowired
    ProductDto productDto;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    WishlistRepository wishlistRepository;

    @Autowired
    OffersRepository offersRepository;

    @Autowired
    OfferMapper offerMapperr;

    @Autowired
    ProductSpecificationRepository productSpecificationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public static String formatPrice(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("##,##,##0.00"); // Indian-style formatting
        return "₹" + decimalFormat.format(price);
    }


//    public List<ProductDto> categoryProduct(Category category,Long id)
//    {
//        List<Product> products = productRepository.findByCategory(category);
//        List<Wishlist> wishlists = wishlistRepository.findAllByUser_id(id);
//        List<Offer> offers = offersRepository.findAllByIsActive(true);
//        List<ProductDto> productDtos=  productMapper.toDTOList(products).stream().filter(ProductDto::getStatus).peek(pd -> {
//            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(null));
//            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice(pv.getPrice())));
//            pd.setWishlisted(wishlists.stream().anyMatch(wi-> wi.getProduct().getId().equals(pd.getId())));
//        } ).collect(Collectors.toList());
//        productDtos
//                .forEach(pd -> {
//                    offers
//                            .forEach(of -> {
//                                if(of.getCategory()!=null)
//                                {
//                                    if(pd.getCategoryName().equalsIgnoreCase(of.getCategory().getName()))
//                                    {
//                                        offers.forEach(of2 -> {
//                                            if(of2.getProduct() != null)
//                                            {
//                                                if(Objects.equals(of2.getProduct().getId(),pd.getId()))
//                                                {
//                                                    pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getDiscountValue()>of2.getDiscountValue() ? of.getId() : of2.getId()).orElse(new Offer())));
//                                                        pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                                                        pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));});
//
//                                                }
//                                            }
//                                            else
//                                            {
//                                                pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getId()).orElse(new Offer())));
//                                                    pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                                                    pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));
//                                                });
//                                            }
//                                        });
//                                    }
//                                }
//                                else if(Objects.equals(of.getProduct(),productRepository.findById(pd.getId()).orElse(new Product())))
//                                {
//                                    pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getId()).orElse(new Offer())));
//                                        pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                                        pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));
//                                    });
//                                }
//                            });
//
//                    pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(null));
//                });
//        return productDtos;
//    }
//    public ProductDto FindProduct(Long id)
//    {
//        Product product = productRepository.findById(id).orElseThrow(()-> new NullPointerException());
//        ProductDto pd = productMapper.toDTO(product);
//        List <Offer> offers = offersRepository.findAllByIsActive(true);
//        for(ProductVariantDTO p : pd.getProductVariantDTOs())
//        {
//            p.setFormattedPrice(p.FormattedPrice(p.getPrice()));
//        }
//        offers
//                .forEach(of -> {
//                    if(of.getCategory()!=null)
//                    {
//                        if(pd.getCategoryName().equalsIgnoreCase(of.getCategory().getName()))
//                        {
//                            offers.forEach(of2 -> {
//                                if(of2.getProduct() != null)
//                                {
//                                    if(Objects.equals(of2.getProduct().getId(),pd.getId()))
//                                    {
//                                        pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getDiscountValue()>of2.getDiscountValue() ? of.getId() : of2.getId()).orElse(new Offer())));
//                                            pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                                            pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));});
//
//                                    }
//                                }
//                                else
//                                {
//                                    pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getId()).orElse(new Offer())));
//                                        pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                                        pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));
//                                    });
//                                }
//                            });
//                        }
//                    }
//                    else if(Objects.equals(of.getProduct(),productRepository.findById(pd.getId()).orElse(new Product())))
//                    {
//                        pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getId()).orElse(new Offer())));
//                            pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                            pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));
//                            System.out.println(pv.getOfferPriceDouble());
//                        });
//                    }
//                });
//
//        pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
//        return pd;
//    }


    public Page<Product> ListProduct(Pageable pageable) {
        Page<Product>productPage = productRepository.findAll(pageable);
        return productPage;

//        return productMapper.toDTOList(products).stream().peek(pd -> {pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
//            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice()));
//        pd.setQuantity(pd.getProductVariantDTOs().stream().mapToInt(ProductVariantDTO::getQuantity).sum());}).collect(Collectors.toList());
    }

    public AddProduct addProduct()
    {
        List<Category> ListCategories = categoryRepository.findAll();
        List<Brand> ListBrands =brandRepository.findAll();
        AddProduct addProduct = new AddProduct();
        List<String> categories = new ArrayList<>() ;
        List<String> brands = new ArrayList<>();
        for(Category x : ListCategories)
        {
            categories.add(x.getName());

        }
        for(Brand y : ListBrands)
        {
            brands.add(y.getName());
        }
        addProduct.setBrands(brands);
        addProduct.setCategories(categories);
        return addProduct;
    }
    public ProductDto productToProductDto(Product product)
    {
        ProductDto productDto = new ProductDto();
        List<ProductVariantDTO> productVariantDTOS = new ArrayList<>();
        List<ProductVariant> productVariants = productVariantRepository.findByProduct_id(product.getId()).orElseThrow(()->new NullPointerException());
        for(ProductVariant x : productVariants)
        {
            ProductVariantDTO productVariantDto = new ProductVariantDTO();
            productVariantDto.setId(x.getId());
            productVariantDto.setImages(x.getImages());
            productVariantDto.setColorCode(x.getColor());
            productVariantDto.setArticleCode(x.getArticleCode());
            productVariantDto.setSize(x.getSize());
            productVariantDto.setQuantity(x.getQuantity());
            productVariantDto.setPrice(x.getPrice());
//            productVariantDto.setFormatedPrice(formatPrice(x.getPrice()));
            productVariantDTOS.add(productVariantDto);
        }

        productDto.setName(product.getName());
//        productDto.setBrand(product.getBrand());
//        productDto.setProductVariantDTOS(productVariantDTOS);
//        productDto.setCategory(product.getCategory());
        productDto.setImportedBy(product.getImportedBy());
        productDto.setGenericName(product.getGenericName());
        productDto.setManufacturer(product.getManufacturer());
        productDto.setWeight(product.getWeight());
        productDto.setCountryOfOrigin(product.getCountryOfOrigin());
        productDto.setId(product.getId());
        if(!productVariantDTOS.isEmpty())
        {
//            productDto.setDefaultVariant(productVariantDTOS.get(0));
        }
        productDto.setDescription(product.getDescription());
        productDto.setStatus(product.getStatus());
        productDto.setMarketedBy(product.getMarketedBy());
        productDto.setQuantity(productVariantRepository.getTotalQuantityByProductId(product.getId()));

        return productDto;
    }

//    public List<ProductDto> filterProduct(Category category, String filterValue) {
//        List<Product> products;
//        switch (filterValue)
//        {
//            case "price-high-low": products = productRepository.findByCategoryAndSortByPriceDesc(category);
//                                    break;
//            case "price-low-high": products = productRepository.findByCategoryAndSortByPriceAsc(category);
//                                    break;
//            case "aA-zZ": products = productRepository.findByCategoryAndSortByNameAsc(category);
//                break;
//            case "zZ-aA": products = productRepository.findByCategoryAndSortByNameDesc(category);
//                break;
//            default: products= new ArrayList<>();
//        }
//        List<ProductDto> productDtos= productMapper.toDTOList(products).stream().filter(ProductDto::getStatus).collect(Collectors.toList());
//        for(ProductDto p : productDtos)
//        {
//            p.setDefaultVariantDTO(p.getProductVariantDTOs().getFirst());
//            for(ProductVariantDTO pv : p.getProductVariantDTOs())
//            {
//                pv.setFormattedPrice(pv.FormattedPrice());
//            }
//        }
//        return productDtos;
//    }

//    public List<ProductDto> filterProduct(String filterType, String filterValue)
//    {
//        List<Product> product = productRepository.findAll();
//        List<ProductDto> productDtos = productMapper.toDTOList(product).stream().filter(ProductDto::getStatus).peek(pd -> {
//            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
//            pd.getProductVariantDTOs().forEach( pv -> pv.setFormattedPrice(pv.FormattedPrice(pv.getPrice())));
//        }).collect(Collectors.toList());
//        List<Brand> brands=brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
//        List<Category> categories=categoryRepository.findAll().stream().filter(Category::getStatus).collect(Collectors.toList());
//        System.out.println(filterType.equals("Shoe Size (UK)")+">>>>>>");
//        if(filterType.equals("Shoe Size (UK)"))
//        {
//            System.out.println(filterValue);
//            switch (filterValue)
//            {
//                case "6" : productDtos = productDtos.stream()
//                        .filter(pd -> pd.getProductVariantDTOs().stream()
//                                .anyMatch(variant -> variant.getSize().stream().anyMatch(size -> size.equals("6")))) // Filter ProductDtos
//                        .collect(Collectors.toList());
//                    System.out.println("hiii");
//                    break;
//                case "7" :productDtos = productDtos.stream()
//                        .filter(pd -> pd.getProductVariantDTOs().stream()
//                                .anyMatch(variant -> variant.getSize().stream().anyMatch(size -> size.equals("7")))) // Filter ProductDtos
//                        .collect(Collectors.toList());
//                    break;
//                default: productDtos = new ArrayList<>();
//            }
//        }
//        else if(filterType.equals("Price"))
//        {
//            switch (filterValue)
//            {
//                case "<5000" : productDtos = productDtos.stream()
//                        .filter(pd -> pd.getProductVariantDTOs().stream()
//                                .anyMatch(variant -> variant.getPrice() < 5000)) // Filter ProductDtos
//                        .collect(Collectors.toList());
//                    break;
//                case "5000-10000" :productDtos = productDtos.stream()
//                        .filter(pd -> pd.getProductVariantDTOs().stream()
//                                .anyMatch(variant -> variant.getPrice() >=5000 && variant.getPrice() <=10000)) // Filter ProductDtos
//                        .collect(Collectors.toList());
//                    break;
//
//                case "10000-15000" :productDtos = productDtos.stream()
//                        .filter(pd -> pd.getProductVariantDTOs().stream()
//                                .anyMatch(variant -> variant.getPrice() >=10000 && variant.getPrice() <=15000)) // Filter ProductDtos
//                        .collect(Collectors.toList());
//                    break;
//
//                case ">15000" :productDtos = productDtos.stream()
//                        .filter(pd -> pd.getProductVariantDTOs().stream()
//                                .anyMatch(variant -> variant.getPrice() >15000 )) // Filter ProductDtos
//                        .collect(Collectors.toList());
//                    break;
//
//                default: productDtos = new ArrayList<>();
//            }
//        }
//        else if(filterType.equals("Brand"))
//        {
//           for(Brand brand : brands)
//           {
//               if(brand.getName().contains(filterValue))
//               {
//                   productDtos = productDtos.stream()
//                           .filter(pd -> pd.getBrandName().equals(filterValue)) // Filter ProductDtos
//                           .collect(Collectors.toList());
//               }
//           }
//        }
//        else if(filterType.equals("Gender"))
//        {
//            for(Category category : categories)
//            {
//                if(category.getName().equals(filterValue))
//                {
//                    productDtos = productDtos.stream()
//                            .filter(pd -> pd.getCategoryName().equals(filterValue)) // Filter ProductDtos
//                            .collect(Collectors.toList());
//                }
//            }
//        }
//        else if(filterType.equals("Sort by"))
//        {
//            switch (filterValue)
//            {
//                case "price-high-low": productDtos = productDtos.stream().sorted(Comparator.comparingDouble(pd -> pd.getDefaultVariantDTO().getPrice())).collect(Collectors.toList());
//                    Collections.reverse(productDtos);
//                    break;
//                case "price-low-high": productDtos = productDtos.stream().sorted(Comparator.comparingDouble(pd -> pd.getDefaultVariantDTO().getPrice())).collect(Collectors.toList());
//                    break;
//                case "aA-zZ": productDtos = productDtos.stream().sorted(Comparator.comparing(ProductDto::getName)).collect(Collectors.toList());
//                    break;
//                case "zZ-aA": productDtos = productDtos.stream().sorted(Comparator.comparing(ProductDto::getName).reversed()).collect(Collectors.toList());
//                    break;
//                default: productDtos= new ArrayList<>();
//            }
//        }
//        List<Wishlist> wishlists = wishlistRepository.findAll();
//        return productDtos.stream().peek(pd -> pd.setWishlisted(wishlists.stream().anyMatch(wi -> wi.getProduct().getId().equals(pd.getId())))).collect(Collectors.toList());
//
//    }

    public List<ProductDto> searchProducts(String keyword)
    {
        List<Product> products = productRepository.searchProducts(keyword);
        List<ProductDto> productDtos = productMapper.toDTOList(products).stream().filter(ProductDto::getStatus).peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
            pd.getProductVariantDTOs().forEach( pv -> pv.setFormattedPrice(pv.FormattedPrice(pv.getPrice())));
        }).collect(Collectors.toList());

        return productDtos;
    }

//    public List<ProductDto> ListAllProducts(Long id) {
//        List<Product> products = productRepository.findAll();
//        products = products.stream().filter(Product::getStatus).collect(Collectors.toList());
//        List<Wishlist> wishlists = wishlistRepository.findAllByUser_id(id);
//        List<Offer> offers = offersRepository.findAllByIsActive(true);
//        List<ProductDto> productDtos= productMapper.toDTOList(products).stream().peek(pd -> {
//            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice(pv.getPrice())));
//            pd.setWishlisted(wishlists.stream().anyMatch(wi -> wi.getProduct().getId().equals(pd.getId())));
//        }).collect(Collectors.toList());
//        productDtos
//                .forEach(pd -> {
//                    offers
//                            .forEach(of -> {
//                                if(of.getCategory()!=null)
//                                {
//                                    if(pd.getCategoryName().equalsIgnoreCase(of.getCategory().getName()))
//                                    {
//                                        offers.forEach(of2 -> {
//                                            if(of2.getProduct() != null)
//                                            {
//                                                if(Objects.equals(of2.getProduct().getId(),pd.getId()))
//                                                {
//                                                    pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getDiscountValue()>of2.getDiscountValue() ? of.getId() : of2.getId()).orElse(new Offer())));
//                                                        pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                                                        pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));});
//
//                                                }
//                                            }
//                                            else
//                                            {
//                                                pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getId()).orElse(new Offer())));
//                                                    pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                                                    pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));
//                                                });
//                                            }
//                                        });
//                                    }
//                                }
//                                else if(Objects.equals(of.getProduct(),productRepository.findById(pd.getId()).orElse(new Product())))
//                                {
//                                    pd.getProductVariantDTOs().forEach(pv -> {pv.setOffer(offerMapperr.toDTO(offersRepository.findById(of.getId()).orElse(new Offer())));
//                                        pv.setOfferPrice(pv.FormattedPrice(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100))));
//                                        pv.setOfferPriceDouble(pv.getPrice()-(pv.getPrice()*(pv.getOffer().getDiscountValue()/100)));
//                                    });
//                                }
//                            });
//
//                    pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(null));
//                });
//
//        return productDtos;
//
//    }

    public Page<ShopProductDTO> getProducts(Integer page, Integer size, String sortBy, String sortDirection, String searchQuery, Map<String, String> filters,Long userId) {
        Specification<Product> spec = Specification.where(null);
        Sort sort = Sort.unsorted();
        if ("price".equals(sortBy)) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                // Join productVariants
                Join<Product, ProductVariant> variantsJoin = root.join("productVariants", JoinType.LEFT);

                // Sort directly by price without considering offerPrice
                Expression<Double> priceExpression = variantsJoin.get("price");

                // Apply sorting (ASC or DESC) based on price
                if ("asc".equalsIgnoreCase(sortDirection)) {
                    query.orderBy(criteriaBuilder.asc(priceExpression)); // Ascending order
                } else {
                    query.orderBy(criteriaBuilder.desc(priceExpression)); // Descending order
                }

                return criteriaBuilder.conjunction(); // Return conjunction (no additional filters here)
            });
        }

        else
        {
            sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        }

        Pageable pageable = PageRequest.of(page,size,sort);
        List<Wishlist> wishlists;
        if(userId != null)
            wishlists = wishlistRepository.findAllByUser_id(userId);
        else {
            wishlists = null;
        }
        spec=spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"),true));
        if(searchQuery != null && !searchQuery.isEmpty())
        {
            spec = spec.and(searchQuery(searchQuery));
        }
        if(filters != null)
        {
            for(Map.Entry<String,String> entry : filters.entrySet())
            {
                    spec = spec.and(fieldFilter(entry.getKey(),entry.getValue()));

            }
        }
        if ("price".equals(sortBy)) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                // Join productVariants
                Join<Product, ProductVariant> variantsJoin = root.join("productVariants", JoinType.LEFT);

                Expression<Double> minPrice = criteriaBuilder.min(variantsJoin.get("price"));

                // Add minPrice to SELECT clause
                query.multiselect(
                        root.get("id"), // Product ID
                        root.get("name"), // Product name or other necessary product fields
                        minPrice // Aggregated minimum price
                );

                // Group by product ID and other selected fields
                query.groupBy(root.get("id"),variantsJoin.get("price"));

                // Apply sorting using aggregated minPrice
                query.orderBy(sortDirection.equalsIgnoreCase("asc")
                        ? criteriaBuilder.asc(minPrice)
                        : criteriaBuilder.desc(minPrice));

                // Return a conjunction (no additional filter criteria here)
                return criteriaBuilder.conjunction();
            });
        }


        Page<Product> productPage = productSpecificationRepository.findAll(spec,pageable);
        return productPage.map(product ->{
                    List<VariantCard> variantCards=new ArrayList<>();
                    product.getProductVariants().forEach(pv->
                        variantCards.add(new VariantCard(pv.getColorCode(),pv.getColor(),pv.getQuantity(),pv.getImages().getFirst(),pv.getPrice(),(pv.getProduct().getAppliedOffer()!=null && pv.getProduct().getAppliedOffer().getEndDate().isAfter(LocalDate.now())) ? pv.getOfferPrice() : null,pv.getStockStatus())));
                    return new ShopProductDTO(product.getId(),product.getName(),product.getBrand().getName(),product.getStatus(),wishlists != null ? wishlists.stream().anyMatch(wi->wi.getProduct().equals(product)) : null,(product.getAppliedOffer()!=null && product.getAppliedOffer().getEndDate().isAfter(LocalDate.now())) ? product.getAppliedOffer().getOfferName():null,(product.getAppliedOffer()!=null && product.getAppliedOffer().getEndDate().isAfter(LocalDate.now())) ? product.getAppliedOffer().getDiscountValue() : null,variantCards,variantCards.getFirst());

                }
                        );

    }

    private Specification<Product> searchQuery(String searchQuery){
        return ((root, query, criteriaBuilder) -> {
            String likeQuery = "%"+searchQuery.toLowerCase()+"%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),likeQuery)
//                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),likeQuery)
            );
        } );
    }

    private Specification<Product> fieldFilter(String field,String value)
    {
        return (root, query, cb) -> {
            if (field.equalsIgnoreCase("brand") || field.equalsIgnoreCase("category")) {
                // Filter by brand or category name
                return cb.equal(cb.lower(root.get(field).get("name")), value.toLowerCase());
            } else if (field.equalsIgnoreCase("price")) {
                // Parse the value into a range or single value
                String[] priceRange = value.split("-");
                if (priceRange.length == 2) {
                    double minPrice = Double.parseDouble(priceRange[0]);
                    double maxPrice = Double.parseDouble(priceRange[1]);

                    // Join with productVariants and apply price condition
                    Join<Product, ProductVariant> variants = root.join("productVariants");
                    return cb.between(variants.get("price"), minPrice, maxPrice);
                } else {
                    throw new IllegalArgumentException("Invalid price range format. Use 'min-max', e.g., '100-500'.");
                }
            }
            else if (field.equalsIgnoreCase("size")) {
                List<ProductVariant> matchingVariants = findProductVariantsBySize(value);
                // You could return a Predicate to check if the Product has any matching variants
                if (matchingVariants.isEmpty()) {
                    return cb.conjunction(); // No matching products, return an always-true predicate
                } else {
                    // Add filtering logic based on matching variants
                    // For example, you could return a condition based on the presence of a variant
                    Join<Product, ProductVariant> variants = root.join("productVariants");
                    CriteriaBuilder.In<ProductVariant> inClause = cb.in(variants);
                    for (ProductVariant variant : matchingVariants) {
                        inClause.value(variant);
                    }

                    // Return the condition that filters products based on the size condition
                    return inClause;
                }
            } else if (field.equalsIgnoreCase("stockStatus")) {
                Join<Product, ProductVariant> variants = root.join("productVariants");
                return cb.equal(variants.get("stockStatus"), value);

            } else
                {
                    Join<Product, ProductVariant> variants = root.join("productVariants");
                    return cb.equal(variants.get(field),value);
                }
        };
    }

    private List<ProductVariant> findProductVariantsBySize(String size) {
        String sql = "SELECT * FROM product_variant pv WHERE ? = ANY(pv.size)";
        Query query = entityManager.createNativeQuery(sql, ProductVariant.class);
        query.setParameter(1, size);  // Bind the size parameter to the query
        return query.getResultList();  // Return list of product variants matching the size
    }


//    public List<ProductDto> adminProductSorting(SortProductDTO sortProductDTO)
//    {
//        productRepository.
//    }

}
