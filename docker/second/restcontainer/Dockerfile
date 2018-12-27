FROM openjdk:8-jdk-slim
COPY ./EPSG-DataSource.properties /
RUN apt -y update
RUN apt -y install gdal-bin python-gdal
EXPOSE 8080