#!/bin/bash
set -e
readonly CONFIG_FILE=".env"
readonly SPRING_CONF_FILE="backend/src/main/resources/application.properties.2"

configure_properties() {
	local db_user
	local db_password
	local db_host
	local backend_port
	local auth_source
    local jwt_secret
    local jwt_expiration

	while IFS="=" read -r key value; do
      case "$key" in
      "MONGO_USERNAME") db_user="$value" ;;
      "MONGO_PASSWORD") db_password="$value" ;;
      "BACKEND_PORT") backend_port="$value" ;;
      "MONGO_INITDB_DATABASE") auth_source="$value" ;;
      "MONGO_HOST") db_host="$value" ;;
      "JWT_SECRET") jwt_secret="$value" ;;
      "JWT_EXPIRATION") jwt_expiration="$value" ;;
      esac
	done < "$CONFIG_FILE"
	uri="mongodb://${db_user}:${db_password}@${db_host}/${auth_source}?authSource=${auth_source}&gssapiServiceName=mongodb"
	echo "spring.data.mongodb.uri=${uri}" > "$SPRING_CONF_FILE"
	echo "spring.data.mongodb.database=${auth_source}" >> "$SPRING_CONF_FILE"
	echo "server.port=${backend_port}" >> "$SPRING_CONF_FILE"
    echo "spring.data.mongodb.database=${auth_source}" >> "$SPRING_CONF_FILE"
    echo "spring.data.mongodb.authentication-database=${auth_source}" >> "$SPRING_CONF_FILE"
    echo "server.port=${backend_port}" >> "$SPRING_CONF_FILE"
	cat <<EOF > init-mongo.js
db.createUser({
	user: "$db_user",
	pwd: "$db_password",
	roles: [
	  {
	    role: "readWrite",
	    db: "$auth_source"
	  },
	  {
	    role: "readWrite",
	    db: "test"
	  },
	  {
	    role: "dbAdmin",
	    db: "$auth_source"
	  }
	]
})
EOF
}

configure_properties

docker-compose up --build