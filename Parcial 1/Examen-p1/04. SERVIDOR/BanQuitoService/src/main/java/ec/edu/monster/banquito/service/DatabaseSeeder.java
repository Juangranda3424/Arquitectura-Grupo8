package ec.edu.monster.banquito.service;

import ec.edu.monster.banquito.entity.*;
import ec.edu.monster.banquito.repository.*;
import ec.edu.monster.phoneshop.entity.Product;
import ec.edu.monster.phoneshop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DatabaseSeeder {
    private final Logger logger = Logger.getLogger(DatabaseSeeder.class.getName());
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();
    private final MovementRepository movementRepository;

    private final ProductRepository productRepository;

    @Transactional
    public void seed() {
        createDefaultRoles();
        //createDefaultPermissions();
        createUsers();
        createProducts();
    }

    private void createDefaultRoles() {
        Role roleUser = roleRepository.findFirstByNameIgnoreCase("USER")
                .orElse(Role.builder().name("USER").description("Rol de usuario regular").build());
        Role roleAdmin = roleRepository.findFirstByNameIgnoreCase("ADMIN")
                .orElse(Role.builder().name("ADMIN").description("Rol de administrador").build());

        List<Role> roles = List.of(roleUser, roleAdmin);

        roles.forEach(role -> {
            boolean exists = role.getId() != null;

            if (!exists) {
                roleRepository.save(role);
                logger.info("Rol '" + role.getName() + "' creado exitosamente.");
            } else {
                logger.info("El rol '" + role.getName() + "' ya existe.");
            }
        });
    }

    private void createDefaultPermissions() {
        Map<String, List<String>> permissions = new HashMap<>();
        permissions.put("USER", List.of(
                // user permissions here
        ));
        permissions.put("ADMIN", List.of(
                // admin permissions here
        ));

        permissions.forEach((roleName, permissionList) -> {
            Optional<Role> role = roleRepository.findFirstByNameIgnoreCase(roleName);

            if (role.isPresent()) {
                Set<Permission> existingPermissions = role.get().getPermissions();
                Set<Permission> newPermissions = new HashSet<>();

                for (String permission : permissionList) {
                    boolean exists = existingPermissions.stream().anyMatch(p -> p.getName().equals(permission));

                    if (!exists) {
                        Permission newPermission = Permission.builder().name(permission).build();
                        newPermissions.add(newPermission);
                    }
                }

                List<Permission> addedPermissions = permissionRepository.saveAll(newPermissions);
                role.get().getPermissions().addAll(addedPermissions);
                roleRepository.save(role.get());

                logger.info(String.format("%d permisos añadidos al rol '%s'.", addedPermissions.size(), roleName));
            }
        });
    }

    private void createUsers() {
        createUser("monster", "monster9", "ADMIN", "1726744291");
        createUser("demo", "demo", "USER", "1726744292");
        createUser("demo2", "demo2", "USER", "1726744293");
        createUser("demo3", "demo3", "USER", "1726744294");
        createUser("demo4", "demo4", "USER", "1726744295");


        fillAccount("monster");
        fillAccount("demo");
        fillAccount("demo2");
        fillAccount("demo3");
        fillAccount("demo4");
    }

    private void fillAccount(String username) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElse(null);
        List<Movement> movements = movementRepository.findAllBySender(user);

        if (!movements.isEmpty()) {
            return;
        }

        BankAccount savingsBankAccount = bankAccountRepository.findAllByUserId(user.getId())
                .stream()
                .filter(account -> account.getType() == BankAccountType.SAVINGS)
                .findFirst()
                .orElse(null);

        if (savingsBankAccount == null) {
            logger.info(String.format("La cuenta de ahorros del usuario '%s' no existe.", username));
            return;
        }

        int totalDeposits = faker.number().numberBetween(15, 25);
        int totalWithdrawals = faker.number().numberBetween(5, 10);

        for (int i = 0; i < totalDeposits; i++) {
            BigDecimal amount = BigDecimal.valueOf(faker.number().randomDouble(2, 5000, 10000));
            Movement movement = Movement.builder()
                    .amount(amount)
                    .type(MovementType.DEPOSIT)
                    .targetAccount(savingsBankAccount)
                    .sender(user)
                    .build();

            savingsBankAccount.setBalance(savingsBankAccount.getBalance().add(amount));
            movementRepository.save(movement);
            bankAccountRepository.save(savingsBankAccount);
        }

        for (int i = 0; i < totalWithdrawals; i++) {
            BigDecimal amount = BigDecimal.valueOf(faker.number().randomDouble(2, 100, 500));
            BankAccount sourceAccount = bankAccountRepository.findById(savingsBankAccount.getId()).orElse(null);

            if (sourceAccount == null) {
                continue;
            }

            if (sourceAccount.getBalance().compareTo(amount) < 0) {
                continue;
            }

            Movement movement = Movement.builder()
                    .amount(amount)
                    .type(MovementType.WITHDRAWAL)
                    .targetAccount(savingsBankAccount)
                    .sender(user)
                    .build();

            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));

            movementRepository.save(movement);
            bankAccountRepository.save(sourceAccount);
        }

        List<BankAccount> checkingBankAccounts = bankAccountRepository.findAll()
                .stream()
                .filter(account -> account.getType() == BankAccountType.SAVINGS && !account.getUser().getId().equals(user.getId()))
                .toList();

        int totalTransfers = faker.number().numberBetween(1, 5);

        for (int i = 0; i < totalTransfers; i++) {
            BankAccount targetAccount = checkingBankAccounts.get(faker.number().numberBetween(0, checkingBankAccounts.size() - 1));
            BankAccount sourceAccount = bankAccountRepository.findById(savingsBankAccount.getId()).orElse(null);
            BigDecimal amount = BigDecimal.valueOf(faker.number().randomDouble(2, 10, 100));

            if (sourceAccount == null) {
                continue;
            }

            Movement movement = Movement.builder()
                    .amount(amount)
                    .type(MovementType.TRANSFER)
                    .sourceAccount(sourceAccount)
                    .targetAccount(targetAccount)
                    .sender(user)
                    .build();

            targetAccount.setBalance(targetAccount.getBalance().add(amount));
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));

            movementRepository.save(movement);
            bankAccountRepository.save(targetAccount);
            bankAccountRepository.save(sourceAccount);
        }
    }

    private void createUser(String username, String password, String role, String identificationNumber) {
        if (userRepository.findFirstByUsernameIgnoreCase(username).isPresent()) {
            logger.info(String.format("El usuario '%s' ya existe.", username));
            return;
        }

        User user = User.builder()
                .identificationNumber(identificationNumber)
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(String.format("%s@example.com", username))
                .role(roleRepository.findFirstByNameIgnoreCase(role).orElse(null))
                .build();
        User savedUser = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .firstName(StringUtils.capitalize(username))
                .lastName("")
                .birthDate(faker.date().birthdayLocalDate(25, 45))
                .user(savedUser)
                .build();

        userProfileRepository.save(profile);

        BankAccount savingsBankAccount = BankAccount.builder()
                .type(BankAccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .user(savedUser)
                .build();
        bankAccountRepository.save(savingsBankAccount);

        BankAccount checkingBankAccount = BankAccount.builder()
                .type(BankAccountType.CHECKING)
                .balance(BigDecimal.ZERO)
                .user(savedUser)
                .build();

        bankAccountRepository.save(checkingBankAccount);

        logger.info(String.format("Usuario '%s' con rol '%s' creado exitosamente.", username, role));
    }

    private void createProducts() {
        List<Product> products = List.of(
                Product.builder().name("Samsung Galaxy S21").price(BigDecimal.valueOf(700)).build(),
                Product.builder().name("iPhone 12 Pro").price(BigDecimal.valueOf(1099.99)).build(),
                Product.builder().name("Google Pixel 5").price(BigDecimal.valueOf(699.99)).build(),
                Product.builder().name("OnePlus 9 Pro").price(BigDecimal.valueOf(899.99)).build(),
                Product.builder().name("Xiaomi Mi 11").price(BigDecimal.valueOf(799.99)).build(),

                Product.builder().name("Samsung Galaxy A12").price(BigDecimal.valueOf(179.99)).build(),
                Product.builder().name("Motorola Moto G Power").price(BigDecimal.valueOf(199.99)).build(),
                Product.builder().name("Nokia 5.4").price(BigDecimal.valueOf(249.99)).build(),
                Product.builder().name("LG K92 5G").price(BigDecimal.valueOf(359.99)).build()
        );

        products.forEach(product -> {
            boolean exists = productRepository.findFirstByNameIgnoreCase(product.getName()).isPresent();

            if (!exists) {
                productRepository.save(product);
                logger.info("Producto '" + product.getName() + "' creado exitosamente.");
            } else {
                logger.info("El producto '" + product.getName() + "' ya existe.");
            }
        });
    }
}
