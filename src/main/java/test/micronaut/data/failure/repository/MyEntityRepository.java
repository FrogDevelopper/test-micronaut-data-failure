package test.micronaut.data.failure.repository;

import static javax.transaction.Transactional.TxType.REQUIRED;

import javax.transaction.Transactional;
import javax.validation.Valid;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import test.micronaut.data.failure.entity.MyEntity;

@Transactional(REQUIRED)
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface MyEntityRepository extends ReactorCrudRepository<MyEntity, Long> {
    Mono<MyEntity> findByTenantIdAndEntityId(@NonNull String tenantId, @NonNull String entityId);

    Flux<MyEntity> findByTenantId(@NonNull String tenantId);

    Mono<Boolean> existsByTenantIdAndEntityId(@NonNull String tenantId, @NonNull String entityId);

    Mono<Long> updateByTenantIdAndEntityId(@NonNull String tenantId, @NonNull String entityId, @NonNull @Valid MyEntity myEntity);

    Mono<Long> deleteByTenantIdAndEntityId(@NonNull String tenantId, @NonNull String entityId);
}
