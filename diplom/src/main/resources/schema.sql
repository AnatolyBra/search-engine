DROP TABLE IF EXISTS _index;
DROP TABLE IF EXISTS _page;
CREATE TABLE _page(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    path VARCHAR(255) DEFAULT NULL,
    code INT NOT NULL,
    content mediumtext);
CREATE INDEX path_index ON _page (path);

DROP TABLE IF EXISTS _lemma;
CREATE TABLE _lemma(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    lemma varchar(255) DEFAULT NULL,
    frequency int DEFAULT NULL,
    CONSTRAINT  lemma_unique UNIQUE (lemma));

DROP TABLE IF EXISTS _field;
CREATE TABLE _field(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) DEFAULT NULL,
    selector VARCHAR(255) DEFAULT NULL,
    weight double DEFAULT NULL);

CREATE TABLE _index(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    page_id INT NOT NULL,
    lemma_id INT NOT NULL,
    `rank` FLOAT NOT NULL);
    ALTER TABLE _index ADD FOREIGN KEY (page_id) REFERENCES _page(id);
    ALTER TABLE _index ADD FOREIGN KEY (lemma_id) REFERENCES _lemma(id);