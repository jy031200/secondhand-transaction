package zerobase.secondhand_transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.secondhand_transaction.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}