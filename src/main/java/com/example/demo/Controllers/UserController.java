package com.example.demo.Controllers;

import com.example.demo.Dtos.*;
import com.example.demo.Dtos.ResponseDtos.*;
import com.example.demo.Enums.*;
import com.example.demo.Models.*;
import com.example.demo.Repos.*;
import com.example.demo.Services.EmailService;
import com.example.demo.Services.JwtService;
import com.example.demo.Specifications.CarSpecs;
import com.example.demo.Specifications.OrderSpec;
import com.example.demo.Specifications.TransactionSpecs;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController
{
    private final JwtService jwtService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final CarRepo carRepo;

    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final TransactionRepo transactionRepo;

    @Autowired
    private final OrderRepo orderRepo;

    @Autowired
    private final ItemRepo itemRepo;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtBlacklistRepo blackRepo;

    @GetMapping("/signout")
    public ResponseEntity<?> signOut(HttpServletRequest req)
    {
        String jwt = jwtService.setJwt(req);

        try
        {
            JwtBlacklist blacklisted = new JwtBlacklist();
            blacklisted.setJwt(jwt);
            blackRepo.save(blacklisted);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User is successfully signed out", HttpStatus.OK);
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileDto dto, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            user = dto.updateUser(user, userRepo, passwordEncoder, authenticationManager);

            if(user == null) {
                return new ResponseEntity<>(dto.getMessage(), HttpStatus.BAD_REQUEST);
            }
            UserResponseDto resDto = new UserResponseDto(user);
            return ResponseEntity.ok(resDto);
        } catch(BadCredentialsException e) {
            return new ResponseEntity<>("Password incorrect", HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUser(@Valid @RequestParam long id, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User authUser = jwtService.getUser(jwt);
            List<Role> roles = new ArrayList<>();
            roles.add(Role.ADMIN);
            roles.add(Role.SUPER_ADMIN);

            boolean userExists = authUser.getId() != id || roles.contains(authUser.getRole()) ?
                    userRepo.existsById(id) : id == authUser.getId();
            if(userExists == false) {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }

            User user = id != authUser.getId() ? userRepo.findById(id).get() : authUser;

            UserResponseDto resDto = new UserResponseDto(user);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cart/get")
    public ResponseEntity<?> getMyCart(HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            Optional<Order> chk = orderRepo.findByUserAndStatus(user, OrderStatus.IN_CART);

            if(chk.isPresent() == false) {
                return new ResponseEntity<>("You have no item(s) in cart",
                        HttpStatus.BAD_REQUEST);
            }
            OrderResponseDto resDto = new OrderResponseDto(chk.get());
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart( @Valid @RequestBody AddToCartDto dto, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            if(dto.getQty() == 0) {
                return new ResponseEntity<>("Cant add zero items", HttpStatus.BAD_REQUEST);
            }
            Optional<Car> chk_car = carRepo.findById(dto.getCar_id());
            if(chk_car.isPresent() == false) {
                return new ResponseEntity<>("Car does not exist", HttpStatus.BAD_REQUEST);
            }
            Car car = chk_car.get();
            Order cart = null;
            Optional<Order> chk = orderRepo.findByUserAndStatus(user, OrderStatus.IN_CART);

            if(chk.isPresent() == false) {
                cart = new Order();
                cart.setUser(user);
                cart.setStatus(OrderStatus.IN_CART);
                cart = orderRepo.save(cart);
            } else {
                cart = chk.get();
            }

            Item item = new Item();
            item.setCar(car);
            item.setOrder(cart);
            item.setQuantity(dto.getQty());
            item = itemRepo.save(item);

            cart.setTotal(cart.getTotal() + (dto.getQty() * car.getPrice()));
            cart.setTotalQty(cart.getTotalQty() + dto.getQty());
            List<Item> items = cart.getItems() == null ? new ArrayList<>() : cart.getItems();
            items.add(item);
            cart.setItems(items);
            cart = orderRepo.save(cart);

            System.out.println(cart.getTotal());
            System.out.println("done");
            OrderResponseDto resDto = new OrderResponseDto(cart);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cart/remove")
    public ResponseEntity<?> removeFromCart(@Valid @RequestBody RemoveFromCartDto dto, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            if(dto.getQty() == 0) {
                return new ResponseEntity<>("Cant remove zero items", HttpStatus.BAD_REQUEST);
            }
            Optional<Order> chk_cart = orderRepo.findByUserAndStatus(user, OrderStatus.IN_CART);
            if(chk_cart.isPresent() == false) {
                return new ResponseEntity<>("You have no item in cart",
                        HttpStatus.BAD_REQUEST);
            }
            Order cart = chk_cart.get();
            Optional<Item> chk_item = itemRepo.findByIdAndOrder(dto.getItem_id(), cart);
            if(chk_item.isPresent() == false) {
                return new ResponseEntity<>("item does not exist",
                        HttpStatus.BAD_REQUEST);
            }
            Item item = chk_item.get();

            if(item.getQuantity() < dto.getQty()) {
                return new ResponseEntity<>("You dont have enough item in cart",
                        HttpStatus.BAD_REQUEST);
            }

            long remove = item.getQuantity() - dto.getQty();

            if(remove == 0) {
                List<Item> items = cart.getItems();
                items.remove(item);
                cart.setItems(items);
                cart.setTotalQty(cart.getTotalQty() - dto.getQty());
                cart.setTotal(cart.getTotal() - (item.getCar().getPrice() * dto.getQty()));
                itemRepo.delete(item);
            } else {
                item.setQuantity(remove);
                cart.setTotalQty(cart.getTotalQty() - dto.getQty());
                cart.setTotal(cart.getTotal() - (item.getCar().getPrice() * dto.getQty()));
                item = itemRepo.save(item);
            }

            cart = orderRepo.save(cart);

            OrderResponseDto resDto = new OrderResponseDto(cart);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cars/search")
    public ResponseEntity<?> searchCars(@Valid @RequestParam String search)
    {
        try {
            Specification<Car> spec = Specification.where(null);
            spec = spec.and(CarSpecs.searchCars(search));

            List<Car> cars = carRepo.findAll(spec);

            List<CarResponseDto> carDtos = new ArrayList<>();

            cars
                    .stream()
                    .map((car) -> {
                        CarResponseDto carRes = new CarResponseDto(car);
                        carDtos.add(carRes);
                        return car;
                    })
                    .collect(Collectors.toList());;

            return ResponseEntity.ok(carDtos);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/car/get")
    public ResponseEntity<?> getCar(@Valid @RequestParam long id)
    {
        try {

            boolean carExists = carRepo.existsById(id);
            if(carExists == false) {
                return new ResponseEntity<>("Car does not exist", HttpStatus.BAD_REQUEST);
            }
            Car car = carRepo.findById(id).get();

            CarResponseDto resDto = new CarResponseDto(car);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/car/cars")
    public ResponseEntity<?> getCars(@Valid @RequestBody CarsDto dto)
    {
        try {
            Specification<Car> spec = Specification.where(null);

            boolean all = true;

            if (dto.getName() != null) {
                spec = spec.and(CarSpecs.nameEquals(dto.getName()));
                all = false;
            }
            if(dto.getBrand() != null) {
                spec = spec.and(CarSpecs.brandEquals(dto.getBrand()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getType() != null) {
                spec = spec.and(CarSpecs.typeEquals(dto.getType()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getPrice() != null) {
                Map priceMap = dto.getPrice();

                if(priceMap.get("greaterOrLess").equals(GreaterOrLess.EQUAL.name())) {
                    double price = (double) priceMap.get("price");
                    spec = spec.and(CarSpecs.priceEquals(price));
                }
                if(priceMap.get("greaterOrLess").equals(GreaterOrLess.GREATER.name())) {
                    double price = (double) priceMap.get("price");
                    spec = spec.and(CarSpecs.priceGreater(price));
                }
                if(priceMap.get("greaterOrLess").equals(GreaterOrLess.LESS.name())) {
                    double price = (double) priceMap.get("price");
                    spec = spec.and(CarSpecs.priceLess(price));
                }
                if(all == true) {
                    all = false;
                }
            }

            if(dto.getCount()) {
                long count = carRepo.count(spec);
                return ResponseEntity.ok(count);
            } else {
                Pageable pageRequest = PageRequest.of(dto.getPage() - 1,
                        dto.getSize(), Sort.by("createdOn").descending());
                Page<Car> carsPage = null;
                if(all) {
                    carsPage = carRepo.findAll(pageRequest);
                } else {
                    carsPage = carRepo.findAll(spec, pageRequest);
                }
                List<Car> cars = carsPage.getContent();

                List<CarResponseDto> carDtos = new ArrayList<>();

                cars
                        .stream()
                        .map((car) -> {
                            CarResponseDto carRes = new CarResponseDto(car);
                            carDtos.add(carRes);
                            return car;
                        })
                        .collect(Collectors.toList());;

                Map<String, Object> resDto = new HashMap<>();
                resDto.put("cars", carDtos);
                resDto.put("total", carsPage.getTotalElements());
                resDto.put("totalPages", carsPage.getTotalPages());
                resDto.put("haveNextPage", carsPage.hasNext());
                resDto.put("havePrevPage", carsPage.hasPrevious());
                resDto.put("currentPage", dto.getPage());
                resDto.put("size",carsPage.getSize());

                return ResponseEntity.ok(resDto);
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/order/orders")
    public ResponseEntity<?> getOrders(@Valid @RequestBody OrdersDto dto, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);
            Specification<Order> spec = Specification.where(null);

            spec = spec.and(OrderSpec.userEquals(user));

            boolean all = true;

            if(dto.getStatus() != null) {
                spec = spec.and(OrderSpec.statusEquals(dto.getStatus()));
                if(all == true) {
                    all = false;
                }
            }

            if(dto.getLastHours() == 0) {
                LocalDateTime before = LocalDateTime.now();
                before = before.minusHours(dto.getLastHours());
                spec = spec.and(OrderSpec.createdBefore(before));
                if(all == true) {
                    all = false;
                }
            }

            if(dto.getCount()) {
                long count = orderRepo.count(spec);
                return ResponseEntity.ok(count);
            } else {
                Pageable pageRequest = PageRequest.of(dto.getPage() - 1,
                        dto.getSize(), Sort.by("createdOn").descending());
                Page<Order> ordersPage = null;
                if(all) {
                    ordersPage = orderRepo.findAll(pageRequest);
                } else {
                    ordersPage = orderRepo.findAll(spec, pageRequest);
                }
                List<Order> orders = ordersPage.getContent();

                List<OrderResponseDto> orderDtos = new ArrayList<>();

                orders
                        .stream()
                        .map((order) -> {
                            OrderResponseDto orderRes = new OrderResponseDto(order);
                            orderDtos.add(orderRes);
                            return order;
                        })
                        .collect(Collectors.toList());

                Map<String, Object> resDto = new HashMap<>();
                resDto.put("orders", orderDtos);
                resDto.put("total", ordersPage.getTotalElements());
                resDto.put("totalPages", ordersPage.getTotalPages());
                resDto.put("haveNextPage", ordersPage.hasNext());
                resDto.put("havePrevPage", ordersPage.hasPrevious());
                resDto.put("currentPage", dto.getPage());
                resDto.put("size",ordersPage.getSize());

                return ResponseEntity.ok(resDto);
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transaction/transactions")
    public ResponseEntity<?> getTransactions(@Valid @RequestBody TransactionsDto dto, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);
            Specification<Transaction> spec = Specification.where(null);
            spec = spec.and(TransactionSpecs.userEquals(user));

            boolean all = true;

            if(dto.getStatus() != null) {
                spec = spec.and(TransactionSpecs.statusEquals(dto.getStatus()));
                if(all == true) {
                    all = false;
                }
            }

            if(dto.getPaid() != null) {
                Map paidMap = dto.getPaid();
                if(paidMap.get("greaterOrLess").equals(GreaterOrLess.EQUAL.name())) {
                    double paid = (double) paidMap.get("paid");
                    spec = spec.and(TransactionSpecs.paidEquals(paid));
                }
                if(paidMap.get("greaterOrLess").equals(GreaterOrLess.GREATER.name())) {
                    double paid = (double) paidMap.get("paid");
                    spec = spec.and(TransactionSpecs.paidGreater(paid));
                }
                if(paidMap.get("greaterOrLess").equals(GreaterOrLess.LESS.name())) {
                    double paid = (double) paidMap.get("paid");
                    spec = spec.and(TransactionSpecs.paidLess(paid));
                }
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getLastHours() == 0) {
                LocalDateTime before = LocalDateTime.now();
                before = before.minusHours(dto.getLastHours());
                spec = spec.and(TransactionSpecs.createdBefore(before));
                if(all == true) {
                    all = false;
                }
            }

            if(dto.getCount()) {
                long count = transactionRepo.count(spec);
                return ResponseEntity.ok(count);
            } else {
                Pageable pageRequest = PageRequest.of(dto.getPage() - 1,
                        dto.getSize(), Sort.by("createdOn").descending());
                Page<Transaction> transactionsPage = null;
                if(all) {
                    transactionsPage = transactionRepo.findAll(pageRequest);
                } else {
                    transactionsPage = transactionRepo.findAll(spec, pageRequest);
                }
                List<Transaction> transactions = transactionsPage.getContent();

                List<TransactionResponseDto> transactionDtos = new ArrayList<>();

                transactions
                        .stream()
                        .map((transaction) -> {
                            TransactionResponseDto transactionRes = new TransactionResponseDto(transaction);
                            transactionDtos.add(transactionRes);
                            return transaction;
                        })
                        .collect(Collectors.toList());;

                Map<String, Object> resDto = new HashMap<>();
                resDto.put("transactions", transactionDtos);
                resDto.put("total", transactionsPage.getTotalElements());
                resDto.put("totalPages", transactionsPage.getTotalPages());
                resDto.put("haveNextPage", transactionsPage.hasNext());
                resDto.put("havePrevPage", transactionsPage.hasPrevious());
                resDto.put("currentPage", dto.getPage());
                resDto.put("size",transactionsPage.getSize());

                return ResponseEntity.ok(resDto);
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/order/get")
    public ResponseEntity<?> getOrder(@Valid @RequestParam long id, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            Optional<Order> chk = orderRepo.findByIdAndUser(id, user);
            if(chk.isPresent() == false) {
                return new ResponseEntity<>("Order does not exist", HttpStatus.BAD_REQUEST);
            }

            Order order = chk.get();

            OrderResponseDto resDto = new OrderResponseDto(order);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transaction/get")
    public ResponseEntity<?> getTransaction(@Valid @RequestParam long id, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            Optional<Transaction> chk = transactionRepo.findByIdAndUser(id, user);
            if(chk.isPresent() == false) {
                return new ResponseEntity<>("Transaction does not exist", HttpStatus.BAD_REQUEST);
            }

            Transaction transaction = chk.get();

            TransactionResponseDto resDto = new TransactionResponseDto(transaction);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cart/checkout")
    public ResponseEntity<?> checkout(HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            Optional<Order> chk_cart = orderRepo.findByUserAndStatus(user, OrderStatus.IN_CART);

            if(chk_cart.isPresent() == false) {
                return new ResponseEntity<>("You have no iem in cart", HttpStatus.BAD_REQUEST);
            }

            Order cart = chk_cart.get();
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setStatus(TransactionStatus.SUCCESFUL);
            transaction.setOrderr(cart);
            transaction.setPaid(cart.getTotal());
            transaction = transactionRepo.save(transaction);

            List<Item> items = cart.getItems()
                    .stream()
                    .map((item) -> {
                        item.setPaidPrice(item.getCar().getPrice());
                        item = itemRepo.save(item);
                        return item;
                    })
                    .collect(Collectors.toList());

            cart.setItems(items);
            cart.setTransaction(transaction);
            cart.setStatus(OrderStatus.CHECKED_OUT);
            cart = orderRepo.save(cart);

            OrderResponseDto resDto = new OrderResponseDto(cart);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/wants")
    public ResponseEntity<?> deleteOrDeactivateAccount(@Valid @RequestParam String want, HttpServletRequest req)
    {
        try {
            List<String> wants = new ArrayList<>();
            wants.add(Status.DELETED.name());
            wants.add(Status.INACTIVE.name());
            if(wants.contains(want) == false) {
                return new ResponseEntity<>("want can only be "+
                        Status.DELETED.name()+" or "+Status.INACTIVE.name(), HttpStatus.BAD_REQUEST);
            }
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            user.setStatus(Status.valueOf(want));
            user = userRepo.save(user);
            emailService.sendEmail(user.getEmail(),
                    "Account "+user.getStatus().name(),
                    "Your account have been succesfully "+user.getStatus().name());
            UserResponseDto resDto = new UserResponseDto(user);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
