CREATE TABLE environments
(
    id           bigserial PRIMARY KEY,
    ort_version  text   NOT NULL,
    java_version text   NOT NULL,
    os           text   NOT NULL,
    processors   int    NOT NULL,
    max_memory   bigint NOT NULL
);

CREATE TABLE environment_variables
(
    id             bigserial PRIMARY KEY,
    environment_id bigint REFERENCES environments NOT NULL,
    name           text                           NOT NULL,
    value          text                           NOT NULL
);

CREATE TABLE environment_tool_versions
(
    id             bigserial PRIMARY KEY,
    environment_id bigint REFERENCES environments NOT NULL,
    name           text                           NULL,
    version        text                           NULL
);

CREATE TABLE sw360_configurations
(
    id        bigserial PRIMARY KEY,
    rest_url  text NOT NULL,
    auth_url  text NOT NULL,
    username  text NOT NULL,
    client_id text NOT NULL
);

CREATE TABLE analyzer_configurations
(
    id                     bigserial PRIMARY KEY,
    sw360_configuration_id bigint REFERENCES sw360_configurations NULL,
    allow_dynamic_versions boolean                                NOT NULL
);

CREATE TABLE enabled_package_managers
(
    id                        bigserial PRIMARY KEY,
    analyzer_configuration_id bigint REFERENCES analyzer_configurations NOT NULL,
    package_manager           text                                      NOT NULL
);

CREATE TABLE disabled_package_managers
(
    id                        bigserial PRIMARY KEY,
    analyzer_configuration_id bigint REFERENCES analyzer_configurations NOT NULL,
    package_manager           text                                      NOT NULL
);

CREATE TABLE analyzer_runs
(
    id                        bigserial PRIMARY KEY,
    analyzer_job_id           bigint REFERENCES analyzer_jobs           NOT NULL,
    environment_id            bigint REFERENCES environments            NOT NULL,
    analyzer_configuration_id bigint REFERENCES analyzer_configurations NOT NULL,
    start_time                timestamp                                 NOT NULL,
    end_time                  timestamp                                 NOT NULL
);

CREATE TABLE package_manager_configurations
(
    id                        bigserial PRIMARY KEY,
    analyzer_configuration_id bigint REFERENCES analyzer_configurations NOT NULL,
    name                      text                                      NOT NULL
);

CREATE TABLE options
(
    id    bigserial PRIMARY KEY,
    name  text NOT NULL,
    value text NOT NULL
);

CREATE TABLE package_manager_configurations_must_run_after
(
    id                               bigserial PRIMARY KEY,
    package_manager_configuration_id bigint REFERENCES package_manager_configurations NOT NULL,
    name                             text                                             NOT NULL
);

CREATE TABLE package_manager_configurations_options
(
    package_manager_configuration_id bigint REFERENCES package_manager_configurations NOT NULL,
    option_id                        bigint REFERENCES options                        NOT NULL,

    PRIMARY KEY (package_manager_configuration_id, option_id)
);
