init:
	./initializer.sh
clean:
	docker system prune -f
	docker rmi givers_frontend
	docker rmi givers_backend
	docker rmi givers_recommender
stop:
	docker-compose down -v