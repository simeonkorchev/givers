#!/bin/bash
set -e

readonly CONFIG_FILE=".env"
readonly SPRING_CONF_FILE="backend/src/main/resources/application.properties"

configure_properties() {
	local db_user
	local db_password
	local db_host
	local db_port
	local backend_port
	local auth_source
    local jwt_secret
    local jwt_expiration
    local images_mount
    local user_interactions_count

	while IFS="=" read -r key value; do
      case "$key" in
        "MONGO_USERNAME") db_user="$value" ;;
        "MONGO_PASSWORD") db_password="$value" ;;
        "BACKEND_PORT") backend_port="$value" ;;
        "MONGO_INITDB_DATABASE") auth_source="$value" ;;
        "MONGO_HOST") db_host="$value" ;;
        "JWT_SECRET") jwt_secret="$value" ;;
        "JWT_EXPIRATION") jwt_expiration="$value" ;;
        "MONGO_PORT") db_port="$value" ;;
        "RECOMMENDER_PORT") recommender_port="$value" ;;
        "RECOMMENDER_PATH") recommender_path="$value" ;;
        "RECOMMENDER_HOST") recommender_host="$value" ;;
        "INIT_DATA") init_data="$value" ;;
        "LOGS_COUNT") logs_count="$value" ;;
        "CAUSES_COUNT") causes_count="$value" ;;
        "IMAGES_MOUNT") images_mount="$value" ;;
        "IMAGES_PATH") images_path="$value" ;;
        "USER_INTERACTIONS_COUNT") user_interactions_count="$value" ;;
      esac
	done < "$CONFIG_FILE"

	uri="mongodb://${db_user}:${db_password}@${db_host}:${db_port}/${auth_source}?authSource=${auth_source}&gssapiServiceName=mongodb"
	echo "spring.data.mongodb.uri=${uri}" > "$SPRING_CONF_FILE"
	echo "server.port=${backend_port}" >> "$SPRING_CONF_FILE"
    echo "spring.data.mongodb.database=${auth_source}" >> "$SPRING_CONF_FILE"
    echo "spring.data.mongodb.authentication-database=${auth_source}" >> "$SPRING_CONF_FILE"
    echo "recommender.url=http://${recommender_host}:${recommender_port}" >> "$SPRING_CONF_FILE"
    echo "recommender.path=${recommender_path}" >> "$SPRING_CONF_FILE"
    echo "init.data=${init_data}" >> "$SPRING_CONF_FILE"
    echo "causes.count=${causes_count}" >> "$SPRING_CONF_FILE"
    echo "logs.count=${logs_count}" >> "$SPRING_CONF_FILE"
    echo "jwt.secret=${jwt_secret}" >> "$SPRING_CONF_FILE"
    echo "jwt.expiration=${jwt_expiration}" >> "$SPRING_CONF_FILE"
    echo "images.mount=${images_mount}" >> "$SPRING_CONF_FILE"
    echo "user.interactions.count=${user_interactions_count}" >> "$SPRING_CONF_FILE"
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
    cat <<EOF > frontend/src/environments/environment.ts
export const environment = {
  production: false,
  backendUrl: "http://localhost:${backend_port}",
  imagesMount: "${images_path}"
};
EOF
}

echo "Preparing configurations..."

configure_properties

echo "Preparation of the configurations done..."

echo "Running docker-compose up..."

docker-compose up --build
