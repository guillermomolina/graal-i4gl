IMAGE=guillermomolina/i4gl
VERSION=20.3.0

build:
	docker build -t ${IMAGE}:latest -t ${IMAGE}:${VERSION} -f Dockerfile .

run:
	docker run --rm -it --name i4gl ${IMAGE} bash