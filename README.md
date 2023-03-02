## Requirements:
- JDK 17
- Tomcat 10.1.5 (already inside the project)
- Install the dependencies (already in pom.xml)
## Database:
```postgresql
CREATE DATABASE semana04;

/* if you want to use an already existing user, use this as an e.g:
   
CREATE DATABASE semana04
  WITH OWNER = caleb
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.utf8'
  LC_CTYPE = 'en_US.utf8'
  TEMPLATE = template0;

*/

CREATE TABLE clientes
(
    id        SERIAL       NOT NULL,
    nombre    VARCHAR(50)  NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    telefono  VARCHAR(15)  NOT NULL,
    email     VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE pedidos
(
    id         SERIAL         NOT NULL ,
    id_cliente INT            NOT NULL,
    fecha      DATE           NOT NULL,
    total      DECIMAL(10, 2) NOT NULL,
    estado     VARCHAR(20)    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_cliente) REFERENCES clientes (id)
);

CREATE USER caleb WITH PASSWORD 'dominic';
GRANT ALL PRIVILEGES ON DATABASE semana04 TO caleb;

CREATE USER dominic WITH PASSWORD '123456';
GRANT CONNECT ON DATABASE semana04 TO dominic;
GRANT USAGE ON SCHEMA public TO dominic;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO dominic;

CREATE USER joshua WITH PASSWORD 'joshua';
GRANT CONNECT ON DATABASE semana04 TO joshua;
GRANT USAGE ON SCHEMA public TO joshua;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO joshua;
```
