# keycloak-spi-amqp-mfa-events

# Deployment

## Configuration

The required action module must be configured via system environment variables. All variables are strings (even boolean must be escaped as a string).

The following environment variables are defined as follows:
```sh
MB_AUTH_CONFIG_IS_SIMULATION="false"
MB_AUTH_CONFIG_AMQP_HOST="locahlost"
MB_AUTH_CONFIG_AMQP_PORT="5672"
MB_AUTH_CONFIG_AMQP_QUEUE="mbnotification"
```