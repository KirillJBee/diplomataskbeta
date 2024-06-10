# syntax=docker/dockerfile:1.4

FROM nginx:latest
COPY ./data /usr/share/nginx/html


