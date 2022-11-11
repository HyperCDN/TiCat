#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER keycloak WITH PASSWORD 'keycloaksupersecret';
	CREATE DATABASE keycloak WITH OWNER keycloak;

	CREATE USER ticatapp WITH PASSWORD 'ticatappsupersecret';
  CREATE DATABASE ticatapp WITH OWNER ticatapp;
EOSQL