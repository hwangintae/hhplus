package org.hhplus.ecommerce.orders.infra.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderItemMongoRepository extends MongoRepository<OrderItemMongo, String> {
}
