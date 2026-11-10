/****************************************************************/
/* Base de donnees:      H2                                     */
/* Application:          Spi4J                                  */
/* Date de creation:     04/01/2012 10:16:16                    */
/****************************************************************/

/****************************************************************/
/* Sequences                                                    */
/****************************************************************/
create sequence AW_PERSONNE_SEQ start with 1000;
create sequence AW_GRADE_SEQ start with 1000;

/****************************************************************/
/* Table: AW_PERSONNE                                           */
/****************************************************************/
create table AW_PERSONNE
(
	PERSONNE_ID NUMBER(19) not null,
	NOM VARCHAR(100) not null,
	PRENOM VARCHAR(100),
	CIVIL NUMBER(1) not null,
	DATE_NAISSANCE TIMESTAMP,
	SALAIRE NUMBER(14,2),
	VERSION TIMESTAMP not null,
	GRADE_ID NUMBER(19),
	constraint AW_PERSONNE_PK primary key (PERSONNE_ID)
);

create index AW_PERSONNE_GRADE_IDX on AW_PERSONNE (GRADE_ID);

/****************************************************************/
/* Table: AW_GRADE                                              */
/****************************************************************/
create table AW_GRADE
(
	GRADE_ID NUMBER(19) not null,
	LIBELLE VARCHAR(100) not null,
	TRIGRAMME VARCHAR(3) not null,
	XDMAJ TIMESTAMP not null,
	XTOPSUP NUMBER(1) default 0 not null,
	constraint AW_GRADE_PK primary key (GRADE_ID)
);

/****************************************************************/
/* Constraints                                                  */
/****************************************************************/
alter table AW_PERSONNE add constraint PERSONNE_GRADE_FK foreign key (GRADE_ID) references AW_GRADE (GRADE_ID);
