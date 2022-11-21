CREATE TABLE IF NOT EXISTS my_entities
(
    internal_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    tenant_id   VARCHAR(20)        NOT NULL,
    entity_id   VARCHAR(100)       NOT NULL,
    UNIQUE (tenant_id, entity_id)
);
