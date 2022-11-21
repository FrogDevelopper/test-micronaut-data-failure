package test.micronaut.data.failure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.data.r2dbc.operations.R2dbcOperations;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;
import test.micronaut.data.failure.entity.MyEntity;

@MicronautTest(startApplication = false, transactional = false)
class MyEntityRepositoryTest {
    private static final String TENANT_ID = "tenant_test";

    @Inject
    private MyEntityRepository myEntityRepository;

    @Inject
    private R2dbcOperations r2dbcOperations;

    @BeforeEach
    void beforeEach() {
        Mono.from(r2dbcOperations
                .withConnection(connection -> Mono.from(connection.createStatement("TRUNCATE TABLE my_entities;").execute()).then())).block();
    }

    @Test
    void findByEntityId_should_return_matchingCampaignByEntityId() {
        // given
        var myEntity = myEntityRepository.save(createEntity()).block();

        // when
        var myEntityFound = myEntityRepository.findByTenantIdAndEntityId(TENANT_ID, myEntity.getEntityId()).block();

        // then
        assertThat(myEntityFound)
                .usingRecursiveComparison().ignoringFields("creationDate", "updateDate")
                .isEqualTo(myEntity);
    }

    @Test
    void findByTenantIdAndEntityId_should_return_matchingCampaignByTenantIdAndEntityId() {
        // given
        var myEntity = myEntityRepository.save(createEntity()).block();

        // when
        var myEntityFound = myEntityRepository.findByTenantIdAndEntityId(TENANT_ID, myEntity.getEntityId()).block();

        // then
        assertThat(myEntityFound)
                .usingRecursiveComparison().ignoringFields("creationDate", "updateDate")
                .isEqualTo(myEntity);
    }

    @Test
    void should_findByTenantId() {
        // given
        var myEntity1 = createEntity();
        myEntity1.setEntityId("1");
        var myEntity2 = createEntity();
        myEntity2.setEntityId("2");
        var myEntity3 = createEntity();
        myEntity3.setEntityId("3");
        myEntity3.setTenantId("wrong");
        var myEntityEntity1 = myEntityRepository.save(myEntity1).block();
        var myEntityEntity2 = myEntityRepository.save(myEntity2).block();
        myEntityRepository.save(myEntity3).block();

        // when
        var myEntities = myEntityRepository.findByTenantId(TENANT_ID).collectList().block();

        // then
        assertThat(myEntities)
                .hasSize(2)
                .containsExactlyInAnyOrder(myEntityEntity1, myEntityEntity2);
    }

    @Test
    void existsByEntityId_should_return_true_when_exists() {
        // given
        var myEntity = myEntityRepository.save(createEntity()).block();

        // when
        var exists = myEntityRepository.existsByTenantIdAndEntityId(TENANT_ID, myEntity.getEntityId()).block();

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEntityId_should_return_false_when_doNotExist() {
        // given
        myEntityRepository.save(createEntity()).block();

        // when
        var exists = myEntityRepository.existsByTenantIdAndEntityId(TENANT_ID, "not_existed_id").block();

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByTenantIdAndEntityId_should_return_true_when_exists() {
        // given
        var myEntity = myEntityRepository.save(createEntity()).block();

        // when
        var exists = myEntityRepository.existsByTenantIdAndEntityId(TENANT_ID, myEntity.getEntityId()).block();

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByTenantIdAndEntityId_should_return_false_when_doNotExist() {
        // given
        myEntityRepository.save(createEntity()).block();

        // when
        var exists = myEntityRepository.existsByTenantIdAndEntityId("not_existed_id", "not_existed_id").block();

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void updateByEntityId_should_UpdateCampaign_when_InternalIdIsNull() {
        // given
        var myEntity = myEntityRepository.save(createEntity()).block();

        // when
        myEntityRepository.update(myEntity).block(); // it works
        Objects.requireNonNull(myEntity).setInternalId(null);
        // doesn't work
        myEntityRepository.updateByTenantIdAndEntityId(myEntity.getTenantId(), myEntity.getEntityId(), myEntity).block();

        // then
        var entity = myEntityRepository.findByTenantIdAndEntityId(TENANT_ID, myEntity.getEntityId()).block();
        assertThat(entity).isNotNull();
    }

    @Test
    void should_deleteCampaign_when_deletingExistentCampaign() {
        // Given
        var myEntity = myEntityRepository.save(createEntity()).block();

        // When
        Long deleted = myEntityRepository.deleteByTenantIdAndEntityId(TENANT_ID, myEntity.getEntityId()).block();

        // Then
        assertThat(deleted).isEqualTo(1L);
    }

    @Test
    void should_notDeleteCampaign_when_deletingNonExistentCampaign() {
        // Given + When
        Long deleted = myEntityRepository.deleteByTenantIdAndEntityId("not_existed_id", "not_existed_id").block();

        // Then
        assertThat(deleted).isZero();
    }

    private static MyEntity createEntity() {
        return MyEntity.builder()
                .tenantId(TENANT_ID)
                .entityId("entityId")
                .build();
    }
}

