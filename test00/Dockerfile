FROM node:12.17.0 as build-stage
WORKDIR /test00
COPY package*.json ./

RUN npm cache clean --force
RUN rm package-lock.json
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine as production-stage
RUN rm /etc/nginx/conf.d/default.conf
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf

COPY --from=build-stage ./test00/dist /usr/share/nginx/html/homepage
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

RUN apk add --no-cache tzdata
ENV TZ=Asia/Seoul
