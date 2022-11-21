package test.micronaut.data.failure.entity;

import static io.micronaut.data.annotation.GeneratedValue.Type.IDENTITY;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@Data
@SuperBuilder(toBuilder = true)
@Jacksonized
@NoArgsConstructor
@MappedEntity("my_entities")
public class MyEntity {

    @Id
    @GeneratedValue(IDENTITY)
    private Long internalId;
    private String tenantId;
    private String entityId;

}
