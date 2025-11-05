package com.letocart.java_apirest_2026.service;

import com.letocart.java_apirest_2026.model.Orders;
import com.letocart.java_apirest_2026.model.OrdersDetails;
import com.letocart.java_apirest_2026.model.Account;
import com.letocart.java_apirest_2026.model.Product;
import com.letocart.java_apirest_2026.repository.OrdersRepository;
import com.letocart.java_apirest_2026.repository.AccountRepository;
import com.letocart.java_apirest_2026.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrdersService(OrdersRepository ordersRepository,
                         AccountRepository accountRepository,
                         ProductRepository productRepository) {
        this.ordersRepository = ordersRepository;
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
    }

    public Orders createOrder(Long accountId, List<OrdersDetails> orderDetailsList) throws Exception {
        // Récupérer le compte
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Compte non trouvé avec l'ID: " + accountId));

        // Créer la commande
        Orders order = new Orders();
        order.setAccount(account);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Traiter chaque ligne de détail
        for (OrdersDetails detail : orderDetailsList) {
            // Vérifier que le produit existe
            Product product = productRepository.findById(detail.getProduct().getProductId())
                    .orElseThrow(() -> new Exception("Produit non trouvé"));

            // Vérifier le stock
            if (product.getStockQuantity() < detail.getQuantity()) {
                throw new Exception("Stock insuffisant pour le produit: " + product.getName());
            }

            // Calculer le sous-total
            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(detail.getQuantity()));
            detail.setUnitPrice(product.getPrice());
            detail.setSubtotal(subtotal);
            detail.setOrders(order);
            detail.setProduct(product);

            totalAmount = totalAmount.add(subtotal);

            // Déduire du stock
            product.setStockQuantity(product.getStockQuantity() - detail.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        order.setOrdersDetails(orderDetailsList);

        return ordersRepository.save(order);
    }

    public List<Orders> getAllOrders() {
        return (List<Orders>) ordersRepository.findAll();
    }

    public Optional<Orders> getOrderById(Long id) {
        return ordersRepository.findById(id);
    }

    public List<Orders> getOrdersByAccount(Long accountId) {
        return ordersRepository.findByAccountAccountId(accountId);
    }

    public Orders updateOrderStatus(Long orderId, String status) throws Exception {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Commande non trouvée avec l'ID: " + orderId));

        order.setStatus(status);
        return ordersRepository.save(order);
    }

    public void deleteOrder(Long id) throws Exception {
        if (!ordersRepository.existsById(id)) {
            throw new Exception("Commande non trouvée avec l'ID: " + id);
        }
        ordersRepository.deleteById(id);
    }
}
