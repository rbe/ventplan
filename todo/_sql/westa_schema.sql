
DROP TABLE IF EXISTS alt1;
CREATE TABLE alt1 (
  volumenstrom varchar(6) default null,
  oktavmf varchar(255) default null,
  value varchar(255) default null,
  dezibel varchar(255) default null
);

DROP TABLE IF EXISTS alt_luftmengen;
CREATE TABLE alt_luftmengen (
  artikelnummer varchar(20) not null default '',
  stufe decimal(3,0) not null default '0',
  grundcode varchar(4) default null,
  grundleistung decimal(3,0) default null,
  bedarfcode varchar(4) default null,
  bedarflesitung decimal(3,0) default null,
  partycode varchar(4) default null,
  partyleistung decimal(3,0) default null,
  primary key  (artikelnummer,stufe)
);

DROP TABLE IF EXISTS angebote;
CREATE TABLE angebote (
  id int(10) not null auto_increment,
  angebotsnummer varchar(20) default null,
  angebotsart tinyint(1) unsigned default null,
  eingang varchar(10) default null,
  ausgang varchar(10) default null,
  vertreter varchar(3) default null,
  handel varchar(50) default null,
  adresse varchar(150) default null,
  email varchar(45) default null,
  telefon varchar(30) default null,
  fax varchar(30) default null,
  handwerker varchar(50) default null,
  planer varchar(50) default null,
  bauherren varchar(50) default null,
  bauvorhaben varchar(100) default null,
  bemerkungen varchar(100) default null,
  summe varchar(13) default null,
  inhalt text,
  primary key  (id)
);
ALTER TABLE angebote ALTER COLUMN id RESTART WITH 6068;

DROP TABLE IF EXISTS ansprechpartner;
CREATE TABLE ansprechpartner (
  id tinyint(3) not null,
  name varchar(50) default null,
  telefon varchar(50) default null,
  telefax varchar(50) default null,
  email varchar(50) default null,
  primary key  (id)
);

DROP TABLE IF EXISTS artikelstamm;
CREATE TABLE artikelstamm (
  artikelnummer char(20) not null,
  artikelbezeichnung longtext,
  preis decimal(9,2) default null,
  kategorie decimal(2,0) default null,
  liefermenge decimal(5,2) default null,
  mengeneinheit char(20) default null,
  verpackungseinheit char(20) default null,
  gesperrt tinyint(1) default null,
  klasse decimal(2,0) default null,
  maxvolumenstrom decimal(3,0) default null,
  primary key  (artikelnummer),
);
CREATE INDEX as_kategorie ON artikelstamm (kategorie);

DROP TABLE IF EXISTS druckverlust;
CREATE TABLE druckverlust (
  artikelnummer varchar(20) not null default '',
  einstellung smallint(5) unsigned not null,
  luftmenge smallint(5) unsigned not null,
  luftart char(2) not null,
  ausblaswinkel decimal(3,0) not null,
  druckverlust decimal(5,2) default null,
  primary key  (artikelnummer,einstellung,luftmenge,luftart,ausblaswinkel)
);

DROP TABLE IF EXISTS einbauart;
CREATE TABLE einbauart (
  id int(1) not null default '0',
  name varchar(25) default null,
  primary key  (id)
);

DROP TABLE IF EXISTS kategorie;
CREATE TABLE kategorie (
  katid tinyint(3) not null,
  katname varchar(30) not null,
  primary key  (katid)
);

DROP TABLE IF EXISTS klassen;
CREATE TABLE klassen (
  id decimal(2,0) not null default '0',
  name varchar(30) default null,
  primary key  (id)
);

DROP TABLE IF EXISTS pakete;
CREATE TABLE pakete (
  id tinyint(3) unsigned not null auto_increment,
  kategorie decimal(2,0) unsigned not null default '0',
  name varchar(200) not null default '',
  geraet varchar(25) not null default '',
  maxvolumenstrom decimal(3,0) default null,
  bedingung varchar(25) default null,
  primary key  (id,geraet)
);
ALTER TABLE pakete ALTER COLUMN ID RESTART WITH 62;

DROP TABLE IF EXISTS rohrwerte;
CREATE TABLE rohrwerte (
  artikelnummer char(20) not null,
  klasse decimal(2,0) default null,
  flaeche decimal(5,0) default null,
  durchmesser decimal(3,0) default null,
  seitea decimal(3,0) default null,
  seiteb decimal(3,0) default null
);

-- COMMENT='Enthält die Schalldruckpegel der Hauptgeräte'
DROP TABLE IF EXISTS schalldruckpegel;
CREATE TABLE schalldruckpegel (
  artikelnummer varchar(15) not null default '',
  volumenstrom decimal(3,0) not null default '0',
  sdp50 decimal(3,1) not null default '0.0',
  sdp100 decimal(3,1) not null default '0.0',
  sdp150 decimal(3,1) not null default '0.0',
  sdp200 decimal(3,1) not null default '0.0',
  primary key  (artikelnummer,volumenstrom)
);

DROP TABLE IF EXISTS schalleistungspegel;
CREATE TABLE schalleistungspegel (
  artikelnummer varchar(15) not null default '',
  volumenstrom decimal(3,0) not null,
  slp63 decimal(3,1) default null,
  slp125 decimal(3,1) not null,
  slp250 decimal(3,1) not null,
  slp500 decimal(3,1) not null,
  slp1000 decimal(3,1) not null,
  slp2000 decimal(3,1) not null,
  slp4000 decimal(3,1) not null,
  slp8000 decimal(3,1) not null,
  dba decimal(3,1) not null,
  zuabex char(1) not null default '',
  primary key  (artikelnummer,volumenstrom,zuabex)
);

DROP TABLE IF EXISTS stueckliste;
CREATE TABLE stueckliste (
  paket tinyint(3) unsigned not null default '0',
  artikel varchar(20) not null default '',
  luftart varchar(2) not null default '',
  anzahl decimal(5,2) default null,
  reihenfolge decimal(3,0) default null,
  primary key  (paket,artikel,luftart)
);

DROP TABLE IF EXISTS system;
CREATE TABLE system (
  id decimal(1,0) not null,
  name varchar(30) not null,
  primary key  (id)
);

DROP TABLE IF EXISTS werksvertretung;
CREATE TABLE werksvertretung (
  id tinyint(3) not null,
  name1 varchar(30) default null,
  name2 varchar(30) default null,
  strasse varchar(30) default null,
  ort varchar(30) default null,
  tel varchar(30) default null,
  fax varchar(30) default null,
  email varchar(30) default null,
  primary key  (id)
);

DROP TABLE IF EXISTS widerstandsbeiwerte;
CREATE TABLE widerstandsbeiwerte (
  id int(10) not null,
  bezeichnung varchar(30) default null,
  wert decimal(3,2) default null,
  bild varchar(50) default null,
  primary key  (id)
);

CREATE VIEW v_pakete
AS
select
	p.id as id
	, k.katid as katid
	, k.katname as katname
	, p.name as name
	, p.geraet as geraet
	, p.bedingung as bedingung
from
	pakete p
	join kategorie k on p.kategorie = k.katid
;

--runscript from '/Users/rbe/project/westaflex/WestaWAC2/sql/westa_data.sql';