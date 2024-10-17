package zerobase.secondhand_transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.secondhand_transaction.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
